package com.gempukku.swccgo.hall;

import com.gempukku.swccgo.ai.models.BeginnerAi;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.state.GameEvent;
import com.gempukku.swccgo.game.GameRecorder;
import com.gempukku.swccgo.game.ParticipantCommunicationVisitor;
import com.gempukku.swccgo.game.Player;
import com.gempukku.swccgo.game.SwccgGameMediator;
import com.gempukku.swccgo.game.save.GameSave;
import com.gempukku.swccgo.game.save.GameSaveCodec;
import com.gempukku.swccgo.game.save.GameSaveHeader;
import com.gempukku.swccgo.game.save.GameStateHash;
import com.gempukku.swccgo.game.save.InMemoryGameSaveDAO;
import com.gempukku.swccgo.game.save.SaveBuild;
import com.gempukku.swccgo.game.save.SavedGameManager;
import com.gempukku.swccgo.guest.PlayNowBot;
import com.gempukku.swccgo.guest.PlayNowGames;
import com.gempukku.swccgo.guest.PlayNowSide;
import com.gempukku.swccgo.logic.decisions.AwaitingDecision;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Play now games are saved, unloaded when idle, resumed, and moved to a new account on signup. */
public class PlayNowSaveTests {
    private static final String GUEST = "guest-abc";
    private static final String IP = "10.0.0.1";
    private static final GameSaveCodec CODEC = new GameSaveCodec(VirtualTableScenario._cardLibrary, VirtualTableScenario._formatLibrary, SaveBuild.current());

    /** A clock the test moves by hand. */
    static final class TestClock extends Clock {
        long now = 1_000_000_000_000L;

        void advance(Duration d) {
            now += d.toMillis();
        }

        @Override
        public long millis() {
            return now;
        }

        @Override
        public Instant instant() {
            return Instant.ofEpochMilli(now);
        }

        @Override
        public ZoneOffset getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return this;
        }
    }

    /** Stores saves without their snapshot, as if they came from another build: resume must replay the move log. */
    static final class MoveLogOnlyDAO extends InMemoryGameSaveDAO {
        @Override
        public void save(String gameId, String owner, GameSave save) {
            super.save(gameId, owner, new GameSave(save.getHeader(), save.getLog(), null, 0, null));
        }
    }

    private PlayNowTestServer _server;
    private InMemoryGameSaveDAO _dao;
    private TestClock _clock;

    @BeforeEach
    public void setUp() {
        start(new InMemoryGameSaveDAO(), 20);
    }

    private void start(InMemoryGameSaveDAO dao, int maxGames) {
        _server = new PlayNowTestServer();
        _dao = dao;
        _clock = new TestClock();
        _server.hallServer.setMaxPlayNowGames(maxGames);
        _server.hallServer.enablePlayNowSaves(_dao, CODEC, _clock,
                new SavedGameManager.Settings(maxGames, Duration.ofMinutes(15), Duration.ofMinutes(30), Duration.ofDays(15)));
        GameRecorder.GameRecordingInProgress recording = mock(GameRecorder.GameRecordingInProgress.class);
        when(_server.gameRecorder.recordGame(any(), anyString(), anyString(), anyMap())).thenReturn(recording);
        when(_server.gameRecorder.recordGame(any(), anyString(), anyString(), anyMap(), anyMap())).thenReturn(recording);
    }

    private SwccgGameMediator game(String gameId) {
        return _server.swccgoServer.getGameById(gameId);
    }

    private static int turns(SwccgGameMediator m) {
        return m.getGameState().getPlayersLatestTurnNumber(m.getGameState().getDarkPlayer())
                + m.getGameState().getPlayersLatestTurnNumber(m.getGameState().getLightPlayer());
    }

    /** The human seat answers like a beginner bot until the game reaches the given number of turns. */
    private static void playUntilTurn(SwccgGameMediator m, String seat, int turn) throws Exception {
        Player player = PlayNowTestServer.seat(seat);
        int[] channel = new int[1];
        m.signupUserForGame(player, new ParticipantCommunicationVisitor() {
            @Override
            public void visitChannelNumber(int channelNumber) {
                channel[0] = channelNumber;
            }

            @Override
            public void visitClock(Map<String, Integer> secondsLeft) {
            }

            @Override
            public void visitGameEvent(GameEvent gameEvent) {
            }
        });
        BeginnerAi ai = new BeginnerAi();
        for (int i = 0; i < 3000 && turns(m) < turn && !m.isFinished(); i++) {
            AwaitingDecision decision = m.getSaveRoot().feedback().getAwaitingDecision(seat);
            assertNotNull(decision, "the bot should have played on to the player's next decision");
            ai.setGame(m.getSaveRoot().game());
            String answer = ai.decide(seat, decision, m.getGameState());
            m.playerAnswered(player, channel[0], decision.getAwaitingDecisionId(), answer);
        }
        assertTrue(turns(m) >= turn, "game reached turn " + turn);
    }

    private static String hash(SwccgGameMediator m) {
        return GameStateHash.of(m.getSaveRoot().game());
    }

    @Test
    public void ANewGameIsSavedBeforeTheBotsFirstAnswer() throws Exception {
        //test1: the save exists as soon as the game starts, under the owner
        String gameId = _server.hallServer.startPlayNowGame(GUEST, null, PlayNowSide.DARK, PlayNowBot.OZZEL, IP);
        assertEquals(GUEST, _dao.getOwner(gameId));
        //test2: the header names the format, both decks and the bot, and a seed
        GameSaveHeader header = _dao.loadHeader(gameId);
        assertEquals("premiere_anh", header.formatCode);
        assertEquals(2, header.seats.size());
        assertEquals("ozzel", header.seat(Player.AI_BEGINNER_ID).bot);
        assertNull(header.seat("Dark Side").bot);
        //test3: a game in memory is not listed as a saved game
        assertTrue(_server.hallServer.getSavedPlayNowGames(GUEST).isEmpty());
    }

    @Test
    public void EachTurnIsSaved() throws Exception {
        //test1: the header's turn count follows the game, so the Resume list can show it
        String gameId = _server.hallServer.startPlayNowGame(GUEST, null, PlayNowSide.LIGHT, PlayNowBot.OZZEL, IP);
        playUntilTurn(game(gameId), "Light Side", 3);
        assertTrue(_dao.loadHeader(gameId).turn >= 2, "turn " + _dao.loadHeader(gameId).turn);
        //test2: every answer, the player's and the bot's, is in the move log
        assertTrue(_dao.load(gameId).getLog().size() > 10);
    }

    @Test
    public void AnIdleGameIsPutAwayAndResumesWhereItWas() throws Exception {
        String gameId = _server.hallServer.startPlayNowGame(GUEST, null, PlayNowSide.LIGHT, PlayNowBot.YODA, IP);
        playUntilTurn(game(gameId), "Light Side", 2);
        String before = hash(game(gameId));

        //test1: after 15 idle minutes the tick saves the game and takes it off the server
        _clock.advance(Duration.ofMinutes(16));
        _server.hallServer.tickPlayNowSaves();
        assertNull(game(gameId));
        assertNull(_server.hallServer.getPlayNowGames().get(gameId));

        //test2: it is listed for its owner only
        List<GameSaveHeader> saved = _server.hallServer.getSavedPlayNowGames(GUEST);
        assertEquals(1, saved.size());
        assertEquals(gameId, saved.get(0).gameId);
        assertTrue(_server.hallServer.getSavedPlayNowGames("guest-other").isEmpty());

        //test3: someone else can't resume it
        assertNull(_server.hallServer.resumePlayNowGame("guest-other", gameId, IP));
        assertNull(game(gameId));

        //test4: the owner resumes it under the same id, in the same position, with their seat and the bot
        assertEquals(gameId, _server.hallServer.resumePlayNowGame(GUEST, gameId, IP));
        SwccgGameMediator resumed = game(gameId);
        assertNotNull(resumed);
        assertEquals(before, hash(resumed));
        PlayNowGames.Entry entry = _server.hallServer.getPlayNowGames().get(gameId);
        assertEquals("Light Side", entry.getSeatId());
        assertEquals(PlayNowBot.YODA, entry.getBot());
        assertTrue(resumed.isBotGame());

        //test5: play goes on, and is saved again
        int logBefore = _dao.load(gameId).getLog().size();
        playUntilTurn(resumed, "Light Side", turns(resumed) + 2);
        assertTrue(_dao.load(gameId).getLog().size() > logBefore);
    }

    @Test
    public void AGameFromAnotherBuildIsReplayedFromItsMoveLog() throws Exception {
        //test1: without a usable snapshot the game is rebuilt from the header and every logged answer, and lands
        //in exactly the same position (same seed, decks and bot as the live game)
        start(new MoveLogOnlyDAO(), 20);
        String gameId = _server.hallServer.startPlayNowGame(GUEST, null, PlayNowSide.DARK, PlayNowBot.OZZEL, IP);
        playUntilTurn(game(gameId), "Dark Side", 3);
        String before = hash(game(gameId));
        _clock.advance(Duration.ofMinutes(16));
        _server.hallServer.tickPlayNowSaves();
        assertNull(game(gameId));

        assertEquals(gameId, _server.hallServer.resumePlayNowGame(GUEST, gameId, IP));
        assertEquals(before, hash(game(gameId)));
    }

    @Test
    public void AGameThatCantBeReplayedIsReportedAndDropped() throws Exception {
        //test1: a save whose moves no longer replay (here: a different seed) can't be continued; the save is deleted
        start(new MoveLogOnlyDAO(), 20);
        String gameId = _server.hallServer.startPlayNowGame(GUEST, null, PlayNowSide.LIGHT, PlayNowBot.OZZEL, IP);
        playUntilTurn(game(gameId), "Light Side", 2);
        _clock.advance(Duration.ofMinutes(16));
        _server.hallServer.tickPlayNowSaves();
        GameSave save = _dao.load(gameId);
        save.getHeader().seed += 1;
        _dao.save(gameId, GUEST, save);

        assertThrows(HallServer.PlayNowIncompatibleException.class, () -> _server.hallServer.resumePlayNowGame(GUEST, gameId, IP));
        assertNull(_dao.load(gameId));
        assertNull(game(gameId));
    }

    @Test
    public void AGameInUseIsNotPutAway() throws Exception {
        //test1: while an answer is being applied (the game's lock is held) the tick leaves the game alone
        String gameId = _server.hallServer.startPlayNowGame(GUEST, null, PlayNowSide.LIGHT, PlayNowBot.OZZEL, IP);
        SwccgGameMediator m = game(gameId);
        CountDownLatch locked = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        Thread busy = new Thread(() -> {
            m.getWriteLock().lock();
            try {
                locked.countDown();
                release.await(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                m.getWriteLock().unlock();
            }
        });
        busy.start();
        assertTrue(locked.await(10, TimeUnit.SECONDS));
        _clock.advance(Duration.ofMinutes(16));
        _server.hallServer.tickPlayNowSaves();
        assertNotNull(game(gameId));
        release.countDown();
        busy.join();

        //test2: the next tick puts it away
        _server.hallServer.tickPlayNowSaves();
        assertNull(game(gameId));
    }

    @Test
    public void APlayerLookingAtTheGameKeepsItInMemory() throws Exception {
        //test1: the player's polling counts as activity, so a game being thought about is not put away
        String gameId = _server.hallServer.startPlayNowGame(GUEST, null, PlayNowSide.LIGHT, PlayNowBot.OZZEL, IP);
        _clock.advance(Duration.ofMinutes(10));
        playUntilTurn(game(gameId), "Light Side", 0); // signs up the player's channel: a look at the game
        _clock.advance(Duration.ofMinutes(10));
        _server.hallServer.tickPlayNowSaves();
        assertNotNull(game(gameId));
    }

    @Test
    public void AFinishedGamesSaveIsDeleted() throws Exception {
        //test1
        String gameId = _server.hallServer.startPlayNowGame(GUEST, null, PlayNowSide.LIGHT, PlayNowBot.OZZEL, IP);
        game(gameId).concede(PlayNowTestServer.seat("Light Side"));
        assertTrue(game(gameId).isFinished());
        assertNull(_dao.load(gameId));
    }

    @Test
    public void WhenEveryTableIsInUseTheAnswerIsBusy() throws Exception {
        start(new InMemoryGameSaveDAO(), 1);
        String first = _server.hallServer.startPlayNowGame(GUEST, null, PlayNowSide.LIGHT, PlayNowBot.OZZEL, IP);

        //test1: a game played in the last 30 minutes is never evicted to make room
        assertThrows(HallServer.PlayNowBusyException.class,
                () -> _server.hallServer.startPlayNowGame("guest-two", null, PlayNowSide.DARK, PlayNowBot.OZZEL, "10.0.0.2"));

        //test2: one idle for 30 minutes (and saved) is put away to make room
        _clock.advance(Duration.ofMinutes(31));
        String second = _server.hallServer.startPlayNowGame("guest-two", null, PlayNowSide.DARK, PlayNowBot.OZZEL, "10.0.0.2");
        assertNull(game(first));
        assertNotNull(_dao.load(first));

        //test3: resuming the first now is busy, and the save is kept
        assertThrows(HallServer.PlayNowBusyException.class, () -> _server.hallServer.resumePlayNowGame(GUEST, first, IP));
        assertNotNull(_dao.load(first));
        assertNotNull(game(second));
    }

    @Test
    public void AGameWhoseSaveFailsIsNeverEvicted() throws Exception {
        //test1: at the cap, a game that can't be saved stays in memory and the newcomer is told it's busy
        start(new InMemoryGameSaveDAO(), 1);
        String first = _server.hallServer.startPlayNowGame(GUEST, null, PlayNowSide.LIGHT, PlayNowBot.OZZEL, IP);
        _dao.failSavesFor.add(first);
        _clock.advance(Duration.ofMinutes(31));
        assertThrows(HallServer.PlayNowBusyException.class,
                () -> _server.hallServer.startPlayNowGame("guest-two", null, PlayNowSide.DARK, PlayNowBot.OZZEL, "10.0.0.2"));
        assertNotNull(game(first));
    }

    @Test
    public void ASignedUpGuestsGameCountsOnTheirNewAccount() throws Exception {
        Player luke = _server.member(7, "luke");
        String gameId = _server.hallServer.startPlayNowGame(GUEST, null, PlayNowSide.LIGHT, PlayNowBot.OZZEL, IP);
        verify(_server.botStatsDAO, never()).createPlayerStats(anyInt());

        _server.hallServer.adoptGuestGames(GUEST, "luke");

        //test1: the game and its save now belong to the account; the seat stays "Light Side"
        PlayNowGames.Entry entry = _server.hallServer.getPlayNowGames().get(gameId);
        assertEquals("luke", entry.getOwner());
        assertEquals("Light Side", entry.getSeatId());
        assertTrue(entry.isStartedAsGuest());
        assertEquals("luke", _dao.getOwner(gameId));

        //test2: from now on it is recorded for luke, in the history as "Started as guest"
        verify(_server.botStatsDAO).createPlayerStats(luke.getId());
        verify(_server.gameRecorder).recordGame(eq(game(gameId)), anyString(), eq(PlayNowSaves.STARTED_AS_GUEST), anyMap(),
                eq(Map.of("Light Side", "luke")));

        //test3: the result goes on luke's bot record
        game(gameId).concede(PlayNowTestServer.seat("Light Side"));
        verify(_server.botStatsDAO).recordGameResult(eq(luke.getId()), eq(false), anyInt(), anyInt(), anyInt(), anyInt());
    }

    @Test
    public void AMembersResumedGameStillCountsOnTheirRecord() throws Exception {
        //test1: a member's game, put away and resumed, is recorded under their name again (not as a guest game)
        Player luke = _server.member(7, "luke");
        String gameId = _server.hallServer.startPlayNowGame("luke", luke, PlayNowSide.LIGHT, PlayNowBot.OZZEL, IP);
        _clock.advance(Duration.ofMinutes(16));
        _server.hallServer.tickPlayNowSaves();
        assertNull(game(gameId));
        assertEquals(1, _server.hallServer.getSavedPlayNowGames("luke").size());

        assertEquals(gameId, _server.hallServer.resumePlayNowGame("luke", gameId, IP));
        verify(_server.gameRecorder).recordGame(eq(game(gameId)), anyString(), eq("Casual"), anyMap(), eq(Map.of()));
        verify(_server.botStatsDAO, atLeastOnce()).createPlayerStats(luke.getId());
        assertFalse(_server.hallServer.getPlayNowGames().get(gameId).isStartedAsGuest());
    }

    @Test
    public void AGuestGameStaysTheGuestsWhenMovingItToTheAccountFails() throws Exception {
        _server.member(7, "luke");
        String gameId = _server.hallServer.startPlayNowGame(GUEST, null, PlayNowSide.LIGHT, PlayNowBot.OZZEL, IP);
        _dao.failChangeOwner = true;

        //test1: the failure reaches the caller (signup turns it into a notice)
        assertThrows(RuntimeException.class, () -> _server.hallServer.adoptGuestGames(GUEST, "luke"));

        //test2: nothing moved: the live game and its save are still the guest's, and nothing is recorded for luke
        PlayNowGames.Entry entry = _server.hallServer.getPlayNowGames().get(gameId);
        assertEquals(GUEST, entry.getOwner());
        assertFalse(entry.isStartedAsGuest());
        assertEquals(GUEST, _dao.getOwner(gameId));
        verify(_server.botStatsDAO, never()).createPlayerStats(anyInt());

        //test3: it is still the guest's to put away and resume; luke has nothing
        _dao.failChangeOwner = false;
        _clock.advance(Duration.ofMinutes(16));
        _server.hallServer.tickPlayNowSaves();
        assertNull(game(gameId));
        assertEquals(1, _server.hallServer.getSavedPlayNowGames(GUEST).size());
        assertEquals(0, _server.hallServer.getSavedPlayNowGames("luke").size());
        assertEquals(gameId, _server.hallServer.resumePlayNowGame(GUEST, gameId, IP));
    }

    @Test
    public void AGuestGameStillMovesWhenItsRecordingCantStart() throws Exception {
        _server.member(7, "luke");
        String gameId = _server.hallServer.startPlayNowGame(GUEST, null, PlayNowSide.LIGHT, PlayNowBot.OZZEL, IP);
        doThrow(new RuntimeException("bot stats unavailable")).when(_server.botStatsDAO).createPlayerStats(anyInt());

        //test1: once the saves have moved, a failure setting up the recording doesn't undo or fail the move
        _server.hallServer.adoptGuestGames(GUEST, "luke");
        assertEquals("luke", _server.hallServer.getPlayNowGames().get(gameId).getOwner());
        assertEquals("luke", _dao.getOwner(gameId));

        //test2: the game plays on for luke
        assertNotNull(game(gameId));
        assertFalse(game(gameId).isFinished());
    }
}
