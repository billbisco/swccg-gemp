package com.gempukku.swccgo.game.save;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Clock;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Keeps saved games in step with live ones and decides what stays in memory.
 * <ul>
 * <li>Every answer goes into the game's move log. At the first quiet point of each turn the game is snapshotted
 * and written to storage.</li>
 * <li>{@link #tick()} (run about once a minute) unloads games idle for {@code unloadIdle} (15 min): it saves them and
 * drops them from memory. It also deletes saves not touched for {@code retention} (15 days), guests and signed-up
 * players alike.</li>
 * <li>When {@code maxLiveGames} are in memory, a start or resume first evicts the longest-idle game idle for at least
 * {@code capEvictIdle} (30 min). That only finds games the 15-minute unload couldn't save. Otherwise the request is
 * refused with {@link BusyException}. (The 30-minute backstop is fine for now; revisit later.)</li>
 * <li>{@link #shutdown()} saves every live game.</li>
 * </ul>
 * Thread-safe; every method locks the manager.
 */
public class SavedGameManager {
    private static final Logger LOG = LogManager.getLogger(SavedGameManager.class);

    /**
     * Limits and timings (all config).
     * @param maxLiveGames games kept in memory at once
     * @param unloadIdle idle time after which a game is saved and dropped from memory
     * @param capEvictIdle at the cap, the minimum idle time of a game that may be evicted
     * @param retention a save not touched for this long is deleted
     */
    public record Settings(int maxLiveGames, Duration unloadIdle, Duration capEvictIdle, Duration retention) {
        public static Settings defaults() {
            return new Settings(20, Duration.ofMinutes(15), Duration.ofMinutes(30), Duration.ofDays(15));
        }
    }

    /** No room for another game in memory. */
    public static class BusyException extends Exception {
        public BusyException() {
            super("Too many games in progress, try again later");
        }
    }

    /** A game in memory. */
    public static final class LiveGame {
        private final String _gameId;
        private final String _owner;
        private final SaveRoot _root;
        private final SaveRecorder _recorder;
        private long _lastActivity;
        private int _snapshotTurn = -1;

        private LiveGame(String gameId, String owner, SaveRoot root, SaveRecorder recorder, long now) {
            _gameId = gameId;
            _owner = owner;
            _root = root;
            _recorder = recorder;
            _lastActivity = now;
        }

        public String getGameId() {
            return _gameId;
        }

        public String getOwner() {
            return _owner;
        }

        public SaveRoot getRoot() {
            return _root;
        }

        public long getLastActivity() {
            return _lastActivity;
        }
    }

    private final GameSaveDAO _dao;
    private final GameResumer _resumer;
    private final GameSaveCodec _codec;
    private final Clock _clock;
    private final Settings _settings;
    private final Map<String, LiveGame> _live = new LinkedHashMap<>();

    public SavedGameManager(GameSaveDAO dao, GameSaveCodec codec, GameResumer resumer, Clock clock, Settings settings) {
        _dao = dao;
        _codec = codec;
        _resumer = resumer;
        _clock = clock;
        _settings = settings;
    }

    /**
     * Registers a new, started game and writes its first save.
     * @throws BusyException if no game can be evicted to make room
     */
    public synchronized LiveGame start(String gameId, String owner, GameSaveHeader header, SaveRoot root) throws BusyException {
        makeRoom();
        long now = _clock.millis();
        header.createdAt = now;
        LiveGame game = new LiveGame(gameId, owner, root, new SaveRecorder(header, _codec), now);
        _live.put(gameId, game);
        snapshotAtTurnStart(game);
        persist(game);
        return game;
    }

    /**
     * Records an answer that was just applied to a live game (see {@link DecisionApplier}).
     */
    public synchronized void answered(String gameId, String player, String answer) {
        LiveGame game = _live.get(gameId);
        if (game == null) {
            throw new IllegalStateException("Game " + gameId + " is not in memory");
        }
        game._recorder.answered(game._root, player, answer);
        game._lastActivity = _clock.millis();
        if (snapshotAtTurnStart(game)) {
            persist(game);
        }
    }

    /**
     * Gets a game in memory, loading it from storage if it was unloaded.
     * @return the game, or null if there is no such game
     * @throws BusyException if it must be loaded and no game can be evicted to make room
     * @throws GameSaveIncompatibleException if the saved game no longer replays on this build
     */
    public synchronized LiveGame resume(String gameId) throws BusyException, GameSaveIncompatibleException {
        LiveGame game = _live.get(gameId);
        if (game != null) {
            game._lastActivity = _clock.millis();
            return game;
        }
        GameSave save = _dao.load(gameId);
        if (save == null) {
            return null;
        }
        makeRoom();
        GameResumer.Resumed resumed = _resumer.resume(save);
        game = new LiveGame(gameId, _dao.getOwner(gameId), resumed.root(), new SaveRecorder(save.getHeader(), _codec, save.getLog()), _clock.millis());
        if (resumed.method() == GameResumer.Method.MOVE_LOG) {
            // the old snapshot belongs to another build; take one for this build
            game._recorder.snapshotIfQuiet(game._root);
            persist(game);
        }
        _live.put(gameId, game);
        return game;
    }

    /** A game ended: drop it from memory and delete its save. */
    public synchronized void finished(String gameId) {
        _live.remove(gameId);
        _dao.delete(gameId);
    }

    /** A guest signed up: their saved and live games move to the account. */
    public synchronized void changeOwner(String fromOwner, String toOwner) {
        _dao.changeOwner(fromOwner, toOwner);
        for (Map.Entry<String, LiveGame> e : _live.entrySet()) {
            LiveGame g = e.getValue();
            if (g._owner.equals(fromOwner)) {
                LiveGame moved = new LiveGame(g._gameId, toOwner, g._root, g._recorder, g._lastActivity);
                moved._snapshotTurn = g._snapshotTurn;
                e.setValue(moved);
            }
        }
    }

    /** Unloads idle games and deletes old saves. Run about once a minute. */
    public synchronized void tick() {
        long now = _clock.millis();
        for (LiveGame game : new ArrayList<>(_live.values())) {
            if (now - game._lastActivity >= _settings.unloadIdle().toMillis()) {
                unload(game);
            }
        }
        int deleted = _dao.deleteLastPlayedAtOrBefore(now - _settings.retention().toMillis());
        if (deleted > 0) {
            LOG.info("Deleted " + deleted + " saved games not played for " + _settings.retention().toDays() + " days");
        }
    }

    /** Saves every live game (server shutdown). */
    public synchronized void shutdown() {
        for (LiveGame game : new ArrayList<>(_live.values())) {
            unload(game);
        }
    }

    public synchronized Collection<LiveGame> getLiveGames() {
        return Collections.unmodifiableList(new ArrayList<>(_live.values()));
    }

    public synchronized boolean isLive(String gameId) {
        return _live.containsKey(gameId);
    }

    /**
     * At the cap, evicts the longest-idle game idle at least capEvictIdle, but only once it is saved. A game whose
     * save fails is never dropped; the next qualifying game is tried, and if none can be saved the caller is busy.
     * Revisit later.
     */
    private void makeRoom() throws BusyException {
        if (_live.size() < _settings.maxLiveGames()) {
            return;
        }
        long now = _clock.millis();
        List<LiveGame> byIdle = new ArrayList<>(_live.values());
        byIdle.sort((a, b) -> Long.compare(a._lastActivity, b._lastActivity));
        for (LiveGame game : byIdle) {
            if (now - game._lastActivity < _settings.capEvictIdle().toMillis()) {
                break;
            }
            if (unload(game)) {
                return;
            }
        }
        throw new BusyException();
    }

    /** Saves a game and drops it from memory. If the save fails the game stays in memory and is retried next tick. */
    private boolean unload(LiveGame game) {
        game._recorder.snapshotIfQuiet(game._root);
        if (!persist(game)) {
            return false;
        }
        _live.remove(game._gameId);
        return true;
    }

    /** Snapshots the game at the first quiet point of each turn. */
    private boolean snapshotAtTurnStart(LiveGame game) {
        int turn = game._root.game().getGameState().getPlayersLatestTurnNumber(game._root.game().getDarkPlayer())
                + game._root.game().getGameState().getPlayersLatestTurnNumber(game._root.game().getLightPlayer());
        if (turn == game._snapshotTurn) {
            return false;
        }
        if (game._recorder.snapshotIfQuiet(game._root)) {
            game._snapshotTurn = turn;
            return true;
        }
        return false;
    }

    private boolean persist(LiveGame game) {
        try {
            _dao.save(game._gameId, game._owner, game._recorder.toSave(game._root, game._lastActivity));
            return true;
        } catch (RuntimeException e) {
            LOG.error("Unable to save game " + game._gameId, e);
            return false;
        }
    }
}
