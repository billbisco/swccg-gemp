package com.gempukku.swccgo.game.save;

import com.esotericsoftware.kryo.KryoException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Keeps one game's save up to date: logs every answer and takes a snapshot when asked at a quiet point.
 * Not thread-safe; call it from the thread that runs the game.
 */
public final class SaveRecorder {
    private static final Logger LOG = LogManager.getLogger(SaveRecorder.class);

    private final GameSaveHeader _header;
    private final GameSaveCodec _codec;
    private final DecisionLog _log;
    private byte[] _snapshot;
    private int _snapshotLogSize;
    private String _snapshotFingerprint;

    public SaveRecorder(GameSaveHeader header, GameSaveCodec codec) {
        this(header, codec, new DecisionLog());
    }

    /** Continues recording a resumed game. */
    public SaveRecorder(GameSaveHeader header, GameSaveCodec codec, DecisionLog log) {
        _header = header;
        _codec = codec;
        _log = log;
    }

    /** Records an answer that was just applied (see {@link DecisionApplier}). */
    public void answered(SaveRoot root, String player, String answer) {
        _log.add(player, answer, GameStateHash.of(root.game()));
    }

    /**
     * Snapshots the game if it is at a quiet point.
     * @return true if a snapshot was taken
     */
    public boolean snapshotIfQuiet(SaveRoot root) {
        if (!QuietPoint.isQuiet(root.game(), root.feedback())) {
            return false;
        }
        try {
            _snapshot = _codec.write(root);
            _snapshotLogSize = _log.size();
            _snapshotFingerprint = _codec.getBuild().getClassFingerprint();
            return true;
        } catch (KryoException e) {
            // The move log alone can still rebuild the game.
            LOG.warn("Snapshot of game " + _header.gameId + " failed; keeping the previous snapshot", e);
            return false;
        }
    }

    /** The save as of now. */
    public GameSave toSave(SaveRoot root, long now) {
        _log.markLast(GameStateHash.of(root.game()));
        _header.updatedAt = now;
        _header.turn = root.game().getGameState().getPlayersLatestTurnNumber(root.game().getDarkPlayer())
                + root.game().getGameState().getPlayersLatestTurnNumber(root.game().getLightPlayer());
        return new GameSave(_header, _log, _snapshot, _snapshotLogSize, _snapshotFingerprint);
    }

    public DecisionLog getLog() {
        return _log;
    }
}
