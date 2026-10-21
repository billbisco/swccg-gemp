package com.gempukku.swccgo.game.save;

import com.gempukku.swccgo.ai.SwccgAiController;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Snapshots read by a Kryo instance that didn't write them: a fresh codec, one that has already used other classes
 * in another order, and a second JVM, as after a server restart.
 */
public class GameSaveFreshKryoTests {

    private static GameSaveCodec newCodec() {
        return new GameSaveCodec(VirtualTableScenario._cardLibrary, VirtualTableScenario._formatLibrary, SaveBuild.current());
    }

    private static BotGame playToTurnStart(long seed, Map<String, SwccgAiController> bots, int minTurns) {
        BotGame g = new BotGame(seed);
        while (g.canContinue() && !(g.turns() >= minTurns && g.game.getGameState().getCurrentPhase() == Phase.ACTIVATE
                && QuietPoint.isQuiet(g.game, g.feedback))) {
            g.botStep(bots);
            assertTrue(g.turns() < 40);
        }
        return g;
    }

    private static GameSave recordedSave(long seed) {
        GameSaveCodec codec = newCodec();
        BotGame g = new BotGame(seed);
        Map<String, SwccgAiController> bots = BotGame.bots("ozzel", "yoda");
        GameSaveHeader header = new GameSaveHeader();
        header.gameId = "fresh-" + seed;
        header.formatCode = "premiere_anh";
        header.seed = seed;
        header.seats.add(GameSaveHeader.seat(BotGame.LS, "Light Side", "ozzel", BotGame.lsDeck()));
        header.seats.add(GameSaveHeader.seat(BotGame.DS, "Dark Side", "yoda", BotGame.dsDeck()));
        header.buildCommit = SaveBuild.current().getBuildCommit();
        SaveRecorder recorder = new SaveRecorder(header, codec);
        while (g.canContinue() && !(g.turns() >= 5 && g.game.getGameState().getCurrentPhase() == Phase.ACTIVATE
                && QuietPoint.isQuiet(g.game, g.feedback))) {
            BotGame.Step s = g.botStep(bots);
            recorder.answered(new SaveRoot(g.game, g.feedback, bots), s.player(), s.answer());
        }
        assertTrue(recorder.snapshotIfQuiet(new SaveRoot(g.game, g.feedback, bots)));
        return recorder.toSave(new SaveRoot(g.game, g.feedback, bots), 0);
    }

    private static final GameResumer.GameFactory FACTORY = header -> {
        BotGame g = new BotGame(header.seed);
        return new SaveRoot(g.game, g.feedback, BotGame.bots(header.seats.get(0).bot, header.seats.get(1).bot));
    };

    @Test
    public void ASnapshotLoadsInACodecThatDidNotWriteIt() {
        BotGame original = playToTurnStart(42, BotGame.bots("ozzel", "yoda"), 5);
        byte[] snapshot = newCodec().write(new SaveRoot(original.game, original.feedback, BotGame.bots("ozzel", "yoda")));
        String expected = GameStateHash.of(original.game);

        //test1: a brand-new codec, that has registered nothing yet, reads it
        assertEquals(expected, GameStateHash.of(newCodec().read(snapshot).game()));

        //test2: so does one that has already written a different game, registering classes in another order
        GameSaveCodec used = newCodec();
        BotGame other = playToTurnStart(7, BotGame.bots("yoda", "yoda"), 3);
        used.write(new SaveRoot(other.game, other.feedback, BotGame.bots("yoda", "yoda")));
        assertEquals(expected, GameStateHash.of(used.read(snapshot).game()));
    }

    @Test
    public void ASnapshotLoadsInAnotherJvm() throws Exception {
        //test1: written here, read by a separate JVM that starts with nothing registered (a server restart)
        BotGame original = playToTurnStart(42, BotGame.bots("ozzel", "yoda"), 5);
        byte[] snapshot = newCodec().write(new SaveRoot(original.game, original.feedback, BotGame.bots("ozzel", "yoda")));
        Path file = Files.createTempFile("gemp-snapshot", ".bin");
        try {
            Files.write(file, snapshot);
            List<String> cmd = new ArrayList<>();
            cmd.add(System.getProperty("java.home") + File.separator + "bin" + File.separator + "java");
            for (String arg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
                if (arg.startsWith("--add-opens") || arg.startsWith("-Dgemp."))
                    cmd.add(arg);
            }
            cmd.add("-cp");
            cmd.add(System.getProperty("java.class.path"));
            cmd.add(ReadSnapshotInNewJvm.class.getName());
            cmd.add(file.toString());
            Process p = new ProcessBuilder(cmd).redirectErrorStream(true).start();
            String out = new String(p.getInputStream().readAllBytes());
            assertTrue(p.waitFor(2, TimeUnit.MINUTES));
            assertEquals(0, p.exitValue(), out);
            assertTrue(out.contains("hash=" + GameStateHash.of(original.game)), out);
        } finally {
            Files.deleteIfExists(file);
        }
    }

    @Test
    public void ASaveWrittenWithAnotherClassListReplaysTheMoveLog() throws Exception {
        GameSave save = recordedSave(1001);

        //test1: the header records the class list the snapshot was written with
        assertEquals(SaveBuild.current().getClassListFingerprint(), save.getHeader().kryoClassList);
        assertEquals(GameResumer.Method.SNAPSHOT, new GameResumer(newCodec(), FACTORY).resume(save).method());

        //test2: with another list (or none recorded, as in older saves) the snapshot isn't read; the log is replayed
        String hash = GameStateHash.of(new GameResumer(newCodec(), FACTORY).resume(save).root().game());
        save.getHeader().kryoClassList = "another-list";
        GameResumer.Resumed other = new GameResumer(newCodec(), FACTORY).resume(save);
        assertEquals(GameResumer.Method.MOVE_LOG, other.method());
        assertEquals(hash, GameStateHash.of(other.root().game()));
        save.getHeader().kryoClassList = null;
        assertEquals(GameResumer.Method.MOVE_LOG, new GameResumer(newCodec(), FACTORY).resume(save).method());
    }

    @Test
    public void TheClassListFingerprintFollowsTheIdOrder() {
        //test1: the same names in the same order always give the same fingerprint
        List<String> names = List.of("[Ljava.lang.Object;", "java.util.ArrayList", "java.util.HashMap");
        assertEquals(SaveBuild.fingerprintOf(names), SaveBuild.fingerprintOf(new ArrayList<>(names)));
        //test2: swapping two names (which swaps their ids) changes it
        assertNotEquals(SaveBuild.fingerprintOf(names),
                SaveBuild.fingerprintOf(List.of("java.util.ArrayList", "[Ljava.lang.Object;", "java.util.HashMap")));
    }
}
