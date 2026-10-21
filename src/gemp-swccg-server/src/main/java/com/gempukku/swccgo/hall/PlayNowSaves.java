package com.gempukku.swccgo.hall;

import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.ai.SwccgAiController;
import com.gempukku.swccgo.bot.BotStatsGameResultListener;
import com.gempukku.swccgo.chat.ChatRoomMediator;
import com.gempukku.swccgo.db.BotStatsDAO;
import com.gempukku.swccgo.db.PlayerDAO;
import com.gempukku.swccgo.game.SwccgFormat;
import com.gempukku.swccgo.game.SwccgGameMediator;
import com.gempukku.swccgo.game.SwccgoServer;
import com.gempukku.swccgo.game.formats.SwccgoFormatLibrary;
import com.gempukku.swccgo.game.save.GameResumer;
import com.gempukku.swccgo.game.save.GameSaveCodec;
import com.gempukku.swccgo.game.save.GameSaveDAO;
import com.gempukku.swccgo.game.save.GameSaveHeader;
import com.gempukku.swccgo.game.save.SaveBuild;
import com.gempukku.swccgo.game.save.SaveRoot;
import com.gempukku.swccgo.game.save.SavedGameManager;
import com.gempukku.swccgo.guest.GuestDecks;
import com.gempukku.swccgo.guest.GuestIdentity;
import com.gempukku.swccgo.guest.PlayNowBot;
import com.gempukku.swccgo.guest.PlayNowGames;
import com.gempukku.swccgo.guest.PlayNowSide;
import com.gempukku.swccgo.logic.timing.DefaultSwccgGame;
import com.gempukku.swccgo.logic.timing.DefaultUserFeedback;
import com.gempukku.swccgo.logic.timing.GameResultListener;
import com.gempukku.swccgo.logic.vo.SwccgDeck;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.SecureRandom;
import java.time.Clock;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Saving and resuming Play now games, for {@link HallServer}.
 * <ul>
 * <li>A new game gets a random seed and a header (decks, seats, bot), and is handed to the
 * {@link SavedGameManager} right after the engine sets it up, before the bot's first answer.</li>
 * <li>Every answer goes to the manager, which saves at the first quiet point of each turn.</li>
 * <li>When the manager unloads an idle game, the game is taken off the server; the hall's cleanup then drops its
 * table.</li>
 * <li>A finished or cancelled game's save is deleted.</li>
 * </ul>
 * Locks: a game's own lock is always taken before the manager's. The manager only ever <i>tries</i> a game's lock.
 */
final class PlayNowSaves implements SavedGameManager.LiveGameHooks, GameResumer.GameFactory {
    private static final Logger LOG = LogManager.getLogger(PlayNowSaves.class);
    static final String STARTED_AS_GUEST = "Started as guest";

    private final SavedGameManager _manager;
    private final SwccgoServer _swccgoServer;
    private final PlayNowGames _games;
    private final SwccgoFormatLibrary _formats;
    private final SwccgCardBlueprintLibrary _library;
    private final boolean _bonusAbilities;
    private final BotStatsDAO _botStatsDAO;
    private final PlayerDAO _playerDAO;
    private final SecureRandom _random = new SecureRandom();
    private final Map<String, SwccgGameMediator> _locked = new ConcurrentHashMap<>();

    PlayNowSaves(GameSaveDAO dao, GameSaveCodec codec, Clock clock, SavedGameManager.Settings settings,
                 SwccgoServer swccgoServer, PlayNowGames games, SwccgoFormatLibrary formats, SwccgCardBlueprintLibrary library,
                 boolean bonusAbilities, BotStatsDAO botStatsDAO, PlayerDAO playerDAO) {
        _swccgoServer = swccgoServer;
        _games = games;
        _formats = formats;
        _library = library;
        _bonusAbilities = bonusAbilities;
        _botStatsDAO = botStatsDAO;
        _playerDAO = playerDAO;
        _manager = new SavedGameManager(dao, codec, new GameResumer(codec, this), clock, settings);
        _manager.setHooks(this);
    }

    SavedGameManager getManager() {
        return _manager;
    }

    long newSeed() {
        return _random.nextLong();
    }

    /** The header of a new Play now game. */
    static GameSaveHeader header(String gameId, String seatId, PlayNowSide side, PlayNowBot bot, long seed) {
        GameSaveHeader header = new GameSaveHeader();
        header.gameId = gameId;
        header.formatCode = HallServer.PLAY_NOW_FORMAT;
        header.seed = seed;
        header.buildCommit = SaveBuild.current().getBuildCommit();
        header.seats.add(GameSaveHeader.seat(seatId, seatId, null, GuestDecks.deck(side)));
        header.seats.add(GameSaveHeader.seat(bot.getPlayerId(), bot.getPlayerId(), bot.getCode(), GuestDecks.deck(side.opposite())));
        return header;
    }

    /** The player's seat in a saved Play now game. */
    static GameSaveHeader.Seat humanSeat(GameSaveHeader header) {
        for (GameSaveHeader.Seat seat : header.seats) {
            if (seat.bot == null) {
                return seat;
            }
        }
        return null;
    }

    static GameSaveHeader.Seat botSeat(GameSaveHeader header) {
        for (GameSaveHeader.Seat seat : header.seats) {
            if (seat.bot != null) {
                return seat;
            }
        }
        return null;
    }

    /** Hooks that keep a game's save up to date. */
    SwccgGameMediator.SaveHooks hooksFor(final SwccgGameMediator mediator, final String owner, final GameSaveHeader newGameHeader) {
        final String gameId = mediator.getGameId();
        return new SwccgGameMediator.SaveHooks() {
            @Override
            public void started() {
                try {
                    _manager.start(gameId, owner, newGameHeader, mediator.getSaveRoot());
                } catch (SavedGameManager.BusyException e) {
                    // The hall made room first, so this only happens if the limits were changed; play on unsaved.
                    LOG.warn("Play now game " + gameId + " is not being saved: no room");
                }
            }

            @Override
            public void answered(String playerId, String answer) {
                // Only the manager's unload removes a live game, and it needs this game's lock, which we hold.
                if (_manager.isLive(gameId)) {
                    _manager.answered(gameId, playerId, answer);
                }
            }

            @Override
            public void touched() {
                _manager.touch(gameId);
            }
        };
    }

    /** Deletes the save once the game is over. */
    GameResultListener deleteSaveWhenOver(final String gameId) {
        return new GameResultListener() {
            @Override
            public void gameFinished(String winnerPlayerId, String winReason, Map<String, String> loserPlayerIdsWithReasons,
                                     String winnerSide, String loserSide) {
                _manager.finished(gameId);
            }

            @Override
            public void gameCancelled() {
                _manager.finished(gameId);
            }
        };
    }

    /**
     * From now on, the game counts on the owner's record: bot stats, game history and replay. A guest who signed
     * up still sits as "Light Side"/"Dark Side"; their result goes on the account and the history entry is marked
     * "Started as guest".
     */
    void recordFor(SwccgGameMediator mediator, PlayNowGames.Entry game) {
        recordWithoutSaves(_swccgoServer, _botStatsDAO, _playerDAO, mediator, game);
    }

    /** {@link #recordFor}, when saving is off. */
    static void recordWithoutSaves(SwccgoServer swccgoServer, BotStatsDAO botStatsDAO, PlayerDAO playerDAO,
                                   SwccgGameMediator mediator, PlayNowGames.Entry game) {
        String account = game.getOwner();
        if (GuestIdentity.isGuestId(account)) {
            return;
        }
        boolean startedAsGuest = !game.getSeatId().equals(account);
        ChatRoomMediator chatRoom = swccgoServer.getGameChatRoom(mediator.getGameId());
        if (botStatsDAO != null) {
            BotStatsGameResultListener stats = new BotStatsGameResultListener(botStatsDAO, playerDAO, mediator,
                    game.getBot().getPlayerId(), startedAsGuest ? account : null);
            if (chatRoom != null) {
                stats.setChatRoom(chatRoom);
            }
            mediator.addGameResultListener(stats);
            if (stats.isInitialized()) {
                mediator.addGameStateListener(stats.getGameStateListener().getPlayerId(), stats.getGameStateListener());
            }
        }
        swccgoServer.recordGame(mediator, startedAsGuest ? STARTED_AS_GUEST : "Casual",
                startedAsGuest ? Collections.singletonMap(game.getSeatId(), account) : Collections.<String, String>emptyMap());
    }

    // GameResumer.GameFactory: rebuild a game from its header, when the snapshot can't be used (another build).
    @Override
    public SaveRoot newGame(GameSaveHeader header) {
        SwccgFormat format = _formats.getFormat(header.formatCode);
        Map<String, SwccgDeck> decks = new HashMap<>();
        Map<String, Integer> clocks = new HashMap<>();
        Map<String, SwccgAiController> bots = new HashMap<>();
        for (GameSaveHeader.Seat seat : header.seats) {
            decks.put(seat.playerId, seat.toDeck());
            clocks.put(seat.playerId, 0);
            if (seat.bot != null) {
                bots.put(seat.playerId, PlayNowBot.fromCode(seat.bot).newController());
            }
        }
        DefaultUserFeedback feedback = new DefaultUserFeedback();
        DefaultSwccgGame game = new DefaultSwccgGame(format, decks, feedback, _library, clocks, _bonusAbilities);
        feedback.setGame(game);
        game.getGameState().setRandomSeed(header.seed);
        game.startGame();
        return new SaveRoot(game, feedback, bots);
    }

    // SavedGameManager.LiveGameHooks
    @Override
    public boolean tryLock(String gameId) {
        SwccgGameMediator mediator = _swccgoServer.getGameById(gameId);
        if (mediator == null) {
            return true;
        }
        if (!mediator.getWriteLock().tryLock()) {
            return false;
        }
        _locked.put(gameId, mediator);
        return true;
    }

    @Override
    public void unlock(String gameId) {
        SwccgGameMediator mediator = _locked.remove(gameId);
        if (mediator != null) {
            mediator.getWriteLock().unlock();
        }
    }

    @Override
    public void unloaded(String gameId) {
        _swccgoServer.removeGame(gameId);
        _games.remove(gameId);
        LOG.info("Play now game " + gameId + " saved and unloaded");
    }
}
