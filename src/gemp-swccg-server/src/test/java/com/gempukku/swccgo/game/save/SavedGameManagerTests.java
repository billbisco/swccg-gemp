package com.gempukku.swccgo.game.save;

import com.gempukku.swccgo.ai.SwccgAiController;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SavedGameManagerTests {
    private static final GameSaveCodec CODEC = new GameSaveCodec(VirtualTableScenario._cardLibrary, VirtualTableScenario._formatLibrary, SaveBuild.current());
    /** Rebuilds a game from its header: bots only for bot seats, as the server will. */
    private static final GameResumer.GameFactory FACTORY = header -> {
        BotGame g = new BotGame(header.seed, header.seat(BotGame.LS).toDeck(), header.seat(BotGame.DS).toDeck());
        Map<String, SwccgAiController> bots = new java.util.HashMap<>();
        for (GameSaveHeader.Seat seat : header.seats) {
            if (seat.bot != null) {
                bots.put(seat.playerId, BotGame.bot(seat.bot));
            }
        }
        return new SaveRoot(g.game, g.feedback, bots);
    };

    /** A clock the test moves by hand. */
    private static final class TestClock extends Clock {
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
        public Clock withZone(java.time.ZoneId zone) {
            return this;
        }
    }

    private final TestClock _clock = new TestClock();
    private final InMemoryGameSaveDAO _dao = new InMemoryGameSaveDAO();

    private SavedGameManager manager(int maxLiveGames) {
        SavedGameManager.Settings d = SavedGameManager.Settings.defaults();
        return new SavedGameManager(_dao, CODEC, new GameResumer(CODEC, FACTORY), _clock, new SavedGameManager.Settings(maxLiveGames, d.unloadIdle(), d.capEvictIdle(), d.retention()));
    }

    private static GameSaveHeader header(String gameId, long seed) {
        GameSaveHeader h = new GameSaveHeader();
        h.gameId = gameId;
        h.formatCode = "premiere_anh";
        h.seed = seed;
        h.seats.add(GameSaveHeader.seat(BotGame.LS, "Light Side", null, BotGame.lsDeck()));
        h.seats.add(GameSaveHeader.seat(BotGame.DS, "Dark Side", "ozzel", BotGame.dsDeck()));
        h.buildCommit = SaveBuild.current().getBuildCommit();
        return h;
    }

    /** Starts a game; the guest's seat is played by a bot here so the test can drive it. */
    private SavedGameManager.LiveGame start(SavedGameManager m, String gameId, String owner, long seed) throws Exception {
        BotGame g = new BotGame(seed);
        return m.start(gameId, owner, header(gameId, seed), new SaveRoot(g.game, g.feedback, BotGame.bots("ozzel", "ozzel")));
    }

    /** Plays answers through the manager, as the server will. */
    private static List<BotGame.Step> play(SavedGameManager m, SavedGameManager.LiveGame live, Map<String, SwccgAiController> bots, int steps) {
        BotGame g = BotGame.of(live.getRoot());
        List<BotGame.Step> out = new ArrayList<>();
        for (int i = 0; i < steps && g.canContinue(); i++) {
            BotGame.Step s = g.botStep(bots);
            m.answered(live.getGameId(), s.player(), s.answer());
            out.add(s);
        }
        return out;
    }

    @Test
    public void SavedGameUnloadsAfter15MinutesIdleAndResumesWhereItWas() throws Exception {
        //test1: idle 14 minutes, the game stays in memory
        //test2: idle 15 minutes, it is saved and dropped from memory
        //test3: resuming loads it from the snapshot at the same position
        //test4: and it plays on exactly like a copy that was never unloaded
        SavedGameManager m = manager(20);
        SavedGameManager.LiveGame live = start(m, "g1", "guest-a", 42);
        Map<String, SwccgAiController> bots = BotGame.bots("ozzel", "ozzel");
        play(m, live, bots, 400);
        String hash = GameStateHash.of(live.getRoot().game());
        SaveRoot neverUnloaded = CODEC.read(CODEC.write(new SaveRoot(live.getRoot().game(), live.getRoot().feedback(), bots)));

        _clock.advance(Duration.ofMinutes(14));
        m.tick();
        assertTrue(m.isLive("g1")); //test1

        _clock.advance(Duration.ofMinutes(1));
        m.tick();
        assertFalse(m.isLive("g1")); //test2
        assertTrue(_dao.has("g1")); //test2

        SavedGameManager.LiveGame resumed = m.resume("g1");
        assertEquals(hash, GameStateHash.of(resumed.getRoot().game())); //test3
        assertEquals("guest-a", resumed.getOwner()); //test3
        List<BotGame.Step> after = new ArrayList<>();
        BotGame rg = BotGame.of(resumed.getRoot());
        Map<String, SwccgAiController> resumedBots = resumed.getRoot().bots();
        for (int i = 0; i < 200 && rg.canContinue(); i++) {
            after.add(rg.botStep(resumedBots));
        }
        BotGame ref = BotGame.of(neverUnloaded);
        List<BotGame.Step> expected = new ArrayList<>();
        for (int i = 0; i < 200 && ref.canContinue(); i++) {
            expected.add(ref.botStep(neverUnloaded.bots()));
        }
        assertEquals(expected, after); //test4
    }

    @Test
    public void SavedGameSnapshotsAtTheStartOfEachTurn() throws Exception {
        //test1: after a few turns the stored save has a snapshot partway through the log
        //test2: a save is written once per turn, not on every answer
        SavedGameManager m = manager(20);
        SavedGameManager.LiveGame live = start(m, "g1", "guest-a", 7);
        List<BotGame.Step> steps = play(m, live, BotGame.bots("ozzel", "ozzel"), 600);

        assertTrue(_dao.snapshotLogSize("g1") > 100); //test1
        assertTrue(_dao.saves > 2); //test2
        assertTrue(_dao.saves < steps.size() / 10); //test2
    }

    @Test
    public void SavedGameIsSavedOnShutdown() throws Exception {
        //test1: shutdown saves every game in memory, and they resume at the same position
        SavedGameManager m = manager(20);
        SavedGameManager.LiveGame a = start(m, "a", "guest-a", 1);
        SavedGameManager.LiveGame b = start(m, "b", "guest-b", 2);
        play(m, a, BotGame.bots("ozzel", "ozzel"), 150);
        play(m, b, BotGame.bots("ozzel", "ozzel"), 90);
        String hashA = GameStateHash.of(a.getRoot().game());
        String hashB = GameStateHash.of(b.getRoot().game());

        m.shutdown();
        SavedGameManager restarted = manager(20);

        assertEquals(hashA, GameStateHash.of(restarted.resume("a").getRoot().game())); //test1
        assertEquals(hashB, GameStateHash.of(restarted.resume("b").getRoot().game())); //test1
    }

    @Test
    public void SavedGameAtTheCapEvictsOnlyGamesIdleAtLeast30Minutes() throws Exception {
        //test1: at the cap with every game idle under 30 minutes, a new game is refused
        //test2: once a game is idle 30 minutes, the longest-idle one is evicted (and saved) to make room
        //test3: the evicted game can still be resumed
        SavedGameManager m = manager(2);
        _dao.failSaves = true; // the 15-minute unload can't save, so both games stay in memory
        start(m, "a", "guest-a", 1);
        _clock.advance(Duration.ofMinutes(5));
        start(m, "b", "guest-b", 2);
        _clock.advance(Duration.ofMinutes(24));
        m.tick();
        assertTrue(m.isLive("a"));
        assertTrue(m.isLive("b"));
        assertThrows(SavedGameManager.BusyException.class, () -> start(m, "c", "guest-c", 3)); //test1

        _clock.advance(Duration.ofMinutes(1));
        _dao.failSaves = false;
        start(m, "c", "guest-c", 3);
        assertFalse(m.isLive("a")); //test2
        assertTrue(m.isLive("b")); //test2
        assertTrue(_dao.has("a")); //test2

        _clock.advance(Duration.ofMinutes(30));
        assertNotNull(m.resume("a")); //test3
    }

    @Test
    public void SavedGameAtTheCapNeverDropsAGameThatCouldNotBeSaved() throws Exception {
        //test1: if the longest-idle game can't be saved, it stays live and the next idle game is evicted instead
        //test2: if no idle game can be saved, the new game is refused and nothing is dropped
        SavedGameManager m = manager(2);
        _dao.failSaves = true;
        start(m, "a", "guest-a", 1);
        _clock.advance(Duration.ofMinutes(1));
        start(m, "b", "guest-b", 2);
        _clock.advance(Duration.ofMinutes(40));
        m.tick();
        _dao.failSaves = false;
        _dao.failSavesFor.add("a");
        start(m, "c", "guest-c", 3);
        assertTrue(m.isLive("a")); //test1
        assertFalse(m.isLive("b")); //test1
        assertTrue(_dao.has("b")); //test1

        _clock.advance(Duration.ofMinutes(40));
        _dao.failSavesFor.add("c");
        assertThrows(SavedGameManager.BusyException.class, () -> start(m, "d", "guest-d", 4)); //test2
        assertTrue(m.isLive("a")); //test2
        assertTrue(m.isLive("c")); //test2
        assertFalse(_dao.has("a")); //test2
    }

    @Test
    public void SavedGameIsDeletedAfter15DaysWithoutPlay() throws Exception {
        //test1: a save is kept for 15 days after the last answer
        //test2: and deleted at 15 days, for guests and signed-up players alike
        SavedGameManager m = manager(20);
        SavedGameManager.LiveGame guest = start(m, "guest-game", "guest-a", 1);
        SavedGameManager.LiveGame member = start(m, "member-game", "Bill", 2);
        play(m, guest, BotGame.bots("ozzel", "ozzel"), 30);
        play(m, member, BotGame.bots("ozzel", "ozzel"), 30);
        _clock.advance(Duration.ofMinutes(15));
        m.tick();

        _clock.advance(Duration.ofDays(15).minusMinutes(16));
        m.tick();
        assertTrue(_dao.has("guest-game")); //test1
        assertTrue(_dao.has("member-game")); //test1

        _clock.advance(Duration.ofMinutes(1));
        m.tick();
        assertFalse(_dao.has("guest-game")); //test2
        assertFalse(_dao.has("member-game")); //test2
        assertNull(m.resume("guest-game")); //test2
    }

    @Test
    public void SavedGameFinishedGameIsDeleted() throws Exception {
        //test1: a finished game leaves memory and storage
        SavedGameManager m = manager(20);
        start(m, "g1", "guest-a", 1);
        m.finished("g1");
        assertFalse(m.isLive("g1")); //test1
        assertFalse(_dao.has("g1")); //test1
    }

    @Test
    public void SavedGameMovesToTheAccountOnSignup() throws Exception {
        //test1: a guest's saved and live games belong to their new account
        SavedGameManager m = manager(20);
        start(m, "saved", "guest-a", 1);
        _clock.advance(Duration.ofMinutes(15));
        m.tick();
        start(m, "live", "guest-a", 2);

        m.changeOwner("guest-a", "NewPlayer");

        assertEquals(List.of("saved"), _dao.getGameIds("NewPlayer").stream().filter("saved"::equals).toList()); //test1
        assertEquals("NewPlayer", m.resume("live").getOwner()); //test1
        assertEquals("NewPlayer", m.resume("saved").getOwner()); //test1
    }

    @Test
    public void SavedGameFromAnotherBuildResumesFromTheMoveLogAndIsResnapshotted() throws Exception {
        //test1: a save whose snapshot came from another build resumes from the move log at the same position
        //test2: it gets a snapshot for this build at its next turn start
        SavedGameManager m = manager(20);
        SavedGameManager.LiveGame live = start(m, "g1", "guest-a", 5);
        play(m, live, BotGame.bots("ozzel", "ozzel"), 300);
        String hash = GameStateHash.of(live.getRoot().game());
        _clock.advance(Duration.ofMinutes(15));
        m.tick();
        GameSave stored = _dao.load("g1");
        _dao.save("g1", "guest-a", new GameSave(stored.getHeader(), stored.getLog(), stored.getSnapshot(), stored.getSnapshotLogSize(), "older-build"));

        SavedGameManager restarted = manager(20);
        SavedGameManager.LiveGame resumed = restarted.resume("g1");
        assertEquals(hash, GameStateHash.of(resumed.getRoot().game())); //test1

        play(restarted, resumed, BotGame.bots("ozzel", "ozzel"), 200);
        assertEquals(SaveBuild.current().getClassFingerprint(), _dao.load("g1").getSnapshotFingerprint()); //test2
    }
}
