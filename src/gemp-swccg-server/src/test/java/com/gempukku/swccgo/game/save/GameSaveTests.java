package com.gempukku.swccgo.game.save;

import com.esotericsoftware.kryo.KryoException;
import com.gempukku.swccgo.ai.SwccgAiController;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.state.actions.DefaultActionsEnvironment;
import org.junit.jupiter.api.Test;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameSaveTests {
    private static final int CONTINUE_STEPS = 300;
    private static final GameSaveCodec CODEC = new GameSaveCodec(VirtualTableScenario._cardLibrary, VirtualTableScenario._formatLibrary, SaveBuild.current());

    /** A bot game plus the recorder a server would keep for it. */
    private static final class Recorded {
        final BotGame game;
        final Map<String, SwccgAiController> bots;
        final SaveRecorder recorder;

        Recorded(long seed, String lsBot, String dsBot) {
            game = new BotGame(seed);
            bots = BotGame.bots(lsBot, dsBot);
            GameSaveHeader header = new GameSaveHeader();
            header.gameId = "test-" + seed;
            header.formatCode = "premiere_anh";
            header.seed = seed;
            header.seats.add(GameSaveHeader.seat(BotGame.LS, "Light Side", lsBot, BotGame.lsDeck()));
            header.seats.add(GameSaveHeader.seat(BotGame.DS, "Dark Side", dsBot, BotGame.dsDeck()));
            header.buildCommit = SaveBuild.current().getBuildCommit();
            recorder = new SaveRecorder(header, CODEC);
        }

        SaveRoot root() {
            return new SaveRoot(game.game, game.feedback, bots);
        }

        BotGame.Step step() {
            BotGame.Step s = game.botStep(bots);
            recorder.answered(root(), s.player(), s.answer());
            return s;
        }

        void playUntil(Predicate<BotGame> stop) {
            while (game.canContinue() && !stop.test(game)) {
                step();
                assertTrue(game.turns() < 40);
            }
            assertTrue(game.canContinue());
        }
    }

    /** Builds a fresh game from a header, as the server would after a deploy. */
    private static final GameResumer.GameFactory FACTORY = header -> {
        BotGame g = new BotGame(header.seed, header.seat(BotGame.LS).toDeck(), header.seat(BotGame.DS).toDeck());
        return new SaveRoot(g.game, g.feedback, BotGame.bots(header.seat(BotGame.LS).bot, header.seat(BotGame.DS).bot));
    };

    private static List<BotGame.Step> continueWithBots(BotGame g, Map<String, SwccgAiController> bots, int steps) {
        List<BotGame.Step> out = new ArrayList<>();
        for (int i = 0; i < steps && g.canContinue(); i++) {
            out.add(g.botStep(bots));
        }
        return out;
    }

    private static boolean atTurnStart(BotGame g) {
        return g.turns() >= 5 && g.game.getGameState().getCurrentPhase() == Phase.ACTIVATE && QuietPoint.isQuiet(g.game, g.feedback);
    }

    private static boolean midBattle(BotGame g) {
        return g.game.getGameState().getCurrentPhase() == Phase.BATTLE && g.game.getGameState().getBattleState() != null
                && !((DefaultActionsEnvironment) g.game.getActionsEnvironment()).isActionStackEmpty();
    }

    @Test
    public void GameSaveAtTurnStartContinuesIdentically() {
        //test1: a game saved at the start of a turn and loaded continues exactly like the original, bots included
        //test2: the loaded game is a separate copy
        Recorded r = new Recorded(42, "ozzel", "yoda");
        r.playUntil(GameSaveTests::atTurnStart);
        SaveRoot loaded = CODEC.read(CODEC.write(r.root()));

        List<BotGame.Step> original = continueWithBots(r.game, r.bots, CONTINUE_STEPS);
        List<BotGame.Step> fromSave = continueWithBots(BotGame.of(loaded), loaded.bots(), CONTINUE_STEPS);

        assertEquals(CONTINUE_STEPS, original.size());
        assertEquals(original, fromSave); //test1
        assertNotSame(r.game.game, loaded.game()); //test2
    }

    @Test
    public void GameSaveMidBattleContinuesIdentically() {
        //test1: a game saved mid-battle, with actions on the stack, continues exactly like the original
        Recorded r = new Recorded(7, "yoda", "ozzel");
        r.playUntil(GameSaveTests::midBattle);
        SaveRoot loaded = CODEC.read(CODEC.write(r.root()));

        List<BotGame.Step> original = continueWithBots(r.game, r.bots, CONTINUE_STEPS);
        List<BotGame.Step> fromSave = continueWithBots(BotGame.of(loaded), loaded.bots(), CONTINUE_STEPS);

        assertEquals(original, fromSave); //test1
    }

    @Test
    public void GameSaveLeavesOutRevertHistoryAndListeners() {
        //test1: the live game has revert history; the loaded game has none
        //test2: listeners are not saved; the loaded game has empty listener sets
        //test3: the snapshot stays small without revert history
        Recorded r = new Recorded(42, "ozzel", "ozzel");
        r.playUntil(GameSaveTests::atTurnStart);
        r.game.game.addGameStateListener(BotGame.LS, new com.gempukku.swccgo.game.state.GameCommunicationChannel(BotGame.LS, 1));
        assertFalse(r.game.game.getAllGameStateListeners().isEmpty());
        byte[] snapshot = CODEC.write(r.root());
        SaveRoot loaded = CODEC.read(snapshot);

        assertFalse(r.game.game.getSnapshots().isEmpty()); //test1
        assertTrue(loaded.game().getSnapshots().isEmpty()); //test1
        assertTrue(loaded.game().getAllGameStateListeners().isEmpty()); //test2
        assertTrue(snapshot.length < 150_000); //test3
    }

    @Test
    public void GameSaveRefusesClassesNotOnTheAllowlist() {
        //test1: a class that isn't on the build-time list can't be saved
        //test2: neither can an unlisted JDK class
        KryoException e = assertThrows(KryoException.class, () -> CODEC.encode(new Object[]{new NotOnTheList()}));
        assertTrue(e.getMessage().contains("Class is not registered")); //test1
        assertThrows(KryoException.class, () -> CODEC.encode(new Object[]{new ConcurrentLinkedQueue<String>()})); //test2
    }

    private static final class NotOnTheList {
    }

    @Test
    public void GameSaveResumesFromSnapshotAndReplaysLaterAnswers() throws Exception {
        //test1: on the same build the snapshot is used
        //test2: answers after the snapshot are replayed, so the loaded game is where the original is
        //test3: and it continues identically
        Recorded r = new Recorded(1001, "yoda", "yoda");
        r.playUntil(GameSaveTests::atTurnStart);
        assertTrue(r.recorder.snapshotIfQuiet(r.root()));
        for (int i = 0; i < 40; i++) {
            r.step();
        }
        GameSave save = r.recorder.toSave(r.root(), 0);

        GameResumer.Resumed resumed = new GameResumer(CODEC, FACTORY).resume(save);

        assertEquals(GameResumer.Method.SNAPSHOT, resumed.method()); //test1
        assertEquals(GameStateHash.of(r.game.game), GameStateHash.of(resumed.root().game())); //test2
        assertEquals(continueWithBots(r.game, r.bots, CONTINUE_STEPS), continueWithBots(BotGame.of(resumed.root()), resumed.root().bots(), CONTINUE_STEPS)); //test3
    }

    @Test
    public void GameSaveFallsBackToMoveLogWhenBuildDiffers() throws Exception {
        //test1: a snapshot from another build is not read; the game is rebuilt from the header and the move log
        //test2: the rebuilt game is where the original is
        //test3: and it continues identically (the bots are fresh, and these bots keep no memory that changes their answers here)
        Recorded r = new Recorded(42, "ozzel", "ozzel");
        r.playUntil(GameSaveTests::atTurnStart);
        assertTrue(r.recorder.snapshotIfQuiet(r.root()));
        GameSave current = r.recorder.toSave(r.root(), 0);
        GameSave fromOtherBuild = new GameSave(current.getHeader(), current.getLog(), current.getSnapshot(), current.getSnapshotLogSize(), "another-build");

        GameResumer.Resumed resumed = new GameResumer(CODEC, FACTORY).resume(fromOtherBuild);

        assertEquals(GameResumer.Method.MOVE_LOG, resumed.method()); //test1
        assertEquals(GameStateHash.of(r.game.game), GameStateHash.of(resumed.root().game())); //test2
        assertEquals(continueWithBots(r.game, r.bots, CONTINUE_STEPS), continueWithBots(BotGame.of(resumed.root()), resumed.root().bots(), CONTINUE_STEPS)); //test3
    }

    @Test
    public void GameSaveFallsBackToMoveLogWhenSnapshotIsUnreadable() throws Exception {
        //test1: a damaged snapshot falls back to the move log instead of failing the game
        Recorded r = new Recorded(7, "ozzel", "yoda");
        r.playUntil(GameSaveTests::atTurnStart);
        assertTrue(r.recorder.snapshotIfQuiet(r.root()));
        GameSave current = r.recorder.toSave(r.root(), 0);
        byte[] damaged = current.getSnapshot().clone();
        for (int i = damaged.length / 3; i < damaged.length / 3 + 64; i++) {
            damaged[i] ^= 0x5A;
        }
        GameSave save = new GameSave(current.getHeader(), current.getLog(), damaged, current.getSnapshotLogSize(), current.getSnapshotFingerprint());

        GameResumer.Resumed resumed = new GameResumer(CODEC, FACTORY).resume(save);

        assertEquals(GameResumer.Method.MOVE_LOG, resumed.method()); //test1
        assertEquals(GameStateHash.of(r.game.game), GameStateHash.of(resumed.root().game())); //test1
    }

    @Test
    public void GameSaveMoveLogReplayStopsWhereTheGameGoesADifferentWay() {
        //test1: when a replay reaches a different position than the one logged, the game is refused at that answer
        //test2: a header whose seed doesn't match the log (stand-in for a card that changed behavior) is refused too
        Recorded r = new Recorded(42, "ozzel", "ozzel");
        r.playUntil(GameSaveTests::atTurnStart);
        GameSave save = r.recorder.toSave(r.root(), 0);
        DecisionLog tampered = new DecisionLog();
        List<DecisionLog.Entry> entries = save.getLog().getEntries();
        int bad = -1;
        for (int i = 0; i < entries.size(); i++) {
            DecisionLog.Entry e = entries.get(i);
            if (bad < 0 && e.hash() != null && i > 50) {
                bad = i;
            }
            tampered.add(e.player(), e.answer(), i == bad ? "0000000000000000" : e.hash());
        }
        GameSave badLog = new GameSave(save.getHeader(), tampered, null, 0, null);

        GameSaveIncompatibleException e1 = assertThrows(GameSaveIncompatibleException.class, () -> new GameResumer(CODEC, FACTORY).resume(badLog));
        assertEquals(bad, e1.getStep()); //test1

        save.getHeader().seed = 43;
        assertThrows(GameSaveIncompatibleException.class, () -> new GameResumer(CODEC, FACTORY).resume(new GameSave(save.getHeader(), save.getLog(), null, 0, null))); //test2
    }

    @Test
    public void GameSaveRebuildsHashedCollectionsSoLookupsStillWork() throws Exception {
        //test1: every key of every hash map and set in the loaded game is found again (card hashes are set before cards go into sets)
        Recorded r = new Recorded(7, "yoda", "ozzel");
        r.playUntil(GameSaveTests::midBattle);
        SaveRoot loaded = CODEC.read(CODEC.write(r.root()));

        int checked = 0;
        for (Object o : reachable(loaded.game())) {
            if (o instanceof HashMap<?, ?> m) {
                for (Object k : m.keySet()) {
                    assertTrue(m.containsKey(k)); //test1
                    checked++;
                }
            } else if (o instanceof HashSet<?> set) {
                for (Object e : set) {
                    assertTrue(set.contains(e)); //test1
                    checked++;
                }
            }
        }
        assertTrue(checked > 1000);
    }

    @Test
    public void GameSaveKeepsHashSetIterationOrder() {
        //test1: a card set that grew and then shrank iterates in the same order after a load (a hash set never shrinks its table)
        BotGame g = new BotGame(3);
        HashSet<PhysicalCard> cards = new HashSet<>(g.game.getGameState().getAllPermanentCards());
        Set<Integer> keep = Set.of(5, 20, 33, 50, 70, 90);
        cards.removeIf(c -> !keep.contains(c.getPermanentCardId()));
        assertEquals(6, cards.size());
        @SuppressWarnings("unchecked")
        HashSet<PhysicalCard> back = (HashSet<PhysicalCard>) ((Object[]) CODEC.decode(CODEC.encode(new Object[]{cards})))[0];

        assertEquals(ids(cards), ids(back)); //test1
    }

    private static List<Integer> ids(Iterable<PhysicalCard> cards) {
        List<Integer> ids = new ArrayList<>();
        for (PhysicalCard c : cards) {
            ids.add(c.getPermanentCardId());
        }
        return ids;
    }

    /** Every object reachable from root through fields, arrays and collections (shared card library left out). */
    private static Set<Object> reachable(Object root) throws IllegalAccessException {
        Set<Object> seen = Collections.newSetFromMap(new IdentityHashMap<>());
        ArrayDeque<Object> queue = new ArrayDeque<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            Object o = queue.poll();
            if (o == null || !seen.add(o) || o instanceof Class || o instanceof SwccgCardBlueprintLibrary || o instanceof SwccgCardBlueprint) {
                continue;
            }
            Class<?> c = o.getClass();
            if (c.isArray()) {
                if (!c.getComponentType().isPrimitive()) {
                    for (Object e : (Object[]) o) {
                        if (e != null) {
                            queue.add(e);
                        }
                    }
                }
                continue;
            }
            if (c.getName().startsWith("java.lang.")) {
                continue;
            }
            for (Class<?> t = c; t != null; t = t.getSuperclass()) {
                for (Field f : t.getDeclaredFields()) {
                    if (!Modifier.isStatic(f.getModifiers()) && !f.getType().isPrimitive()) {
                        f.setAccessible(true);
                        Object v = f.get(o);
                        if (v != null) {
                            queue.add(v);
                        }
                    }
                }
            }
        }
        return seen;
    }

    @Test
    public void DecisionLogKeepsAnswersExactly() {
        //test1: answers with tabs, newlines, backslashes and nulls survive encoding
        //test2: a hash is kept every HASH_EVERY answers and on the last one after markLast
        DecisionLog log = new DecisionLog();
        for (int i = 0; i < 25; i++) {
            log.add(i % 2 == 0 ? BotGame.LS : BotGame.DS, i == 3 ? "a\tb\nc\\d" : i == 4 ? null : Integer.toString(i), "h" + i);
        }
        log.markLast("last");
        DecisionLog back = DecisionLog.fromBytes(log.toBytes());

        assertEquals(log.getEntries(), back.getEntries()); //test1
        assertEquals("a\tb\nc\\d", back.getEntries().get(3).answer()); //test1
        assertNull(back.getEntries().get(4).answer()); //test1
        assertEquals("h9", back.getEntries().get(9).hash()); //test2
        assertNull(back.getEntries().get(10).hash()); //test2
        assertEquals("last", back.getEntries().get(24).hash()); //test2
    }

    @Test
    public void GameSaveHeaderChecksDeckHashes() {
        //test1: a header round-trips through JSON
        //test2: a deck edited after the game started is refused
        Recorded r = new Recorded(5, "ozzel", "yoda");
        GameSaveHeader back = GameSaveHeader.fromJson(r.recorder.toSave(r.root(), 123).getHeader().toJson());
        assertEquals(5, back.seed); //test1
        assertEquals("yoda", back.seat(BotGame.DS).bot); //test1
        assertEquals(BotGame.lsDeck().getCards(), back.seat(BotGame.LS).cards); //test1
        assertEquals(123, back.updatedAt); //test1

        String edited = r.recorder.toSave(r.root(), 0).getHeader().toJson().replaceFirst("\"1_3\"", "\"1_4\"");
        assertThrows(IllegalArgumentException.class, () -> GameSaveHeader.fromJson(edited)); //test2
    }

    @Test
    public void KryoClassListIsBuiltFromTheGameClasses() {
        //test1: the build-time list is sorted and holds the engine, card and bot classes
        //test2: the build fingerprint is set
        List<String> names = SaveBuild.current().getClassNames();
        List<String> sorted = new ArrayList<>(names);
        sorted.sort(null);
        assertEquals(sorted, names); //test1
        assertTrue(names.contains("com.gempukku.swccgo.game.PhysicalCardImpl")); //test1
        assertTrue(names.contains("com.gempukku.swccgo.cards.set1.light.Card1_003")); //test1
        assertTrue(names.contains("com.gempukku.swccgo.ai.models.AdvancedAi")); //test1
        assertNotNull(SaveBuild.current().getClassFingerprint()); //test2
        assertEquals(32, SaveBuild.current().getClassFingerprint().length()); //test2
    }
}
