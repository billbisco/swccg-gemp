package com.gempukku.swccgo.ai;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.TestBase;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.game.state.GameCommunicationChannel;
import com.gempukku.swccgo.logic.modifiers.TotalForceGenerationModifier;
import com.gempukku.swccgo.logic.timing.DefaultSwccgGame;
import com.gempukku.swccgo.logic.timing.GameSnapshot;
import com.gempukku.swccgo.logic.timing.TurnProcedure;
import org.junit.Assume;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * swccg-guild/work#136: what a big board costs. Very large decks matter to the engine through the cards that end up
 * in play, so this builds boards of N characters (half per side, a quarter of each side at the opponent's site so a
 * battle is possible), attaches the server's client channels, and times the operations #slicers named: activating 18
 * Force, moving through the phases, starting a battle, a revert snapshot and the status-bar stats.
 * <pre>mvn -o test -pl gemp-swccg-server -Dtest=BoardScalingBenchmark -Dgemp.bench=true [-Dgemp.bench.board=50,100,200,400,800]</pre>
 */
public class BoardScalingBenchmark {
    private static final String DS = TestBase.DS;
    private static final String LS = TestBase.LS;
    private static final String STORMTROOPER = "1_194";
    private static final String REBEL_TROOPER = "1_28";

    private static double ms(long nanos) {
        return nanos / 1e6;
    }

    private static Object field(Object target, Class<?> owner, String name) throws ReflectiveOperationException {
        java.lang.reflect.Field f = owner.getDeclaredField(name);
        f.setAccessible(true);
        return f.get(target);
    }

    static String measure(int board) throws Exception {
        int perSide = board / 2;
        HashMap<String, String> ls = new HashMap<>();
        HashMap<String, String> ds = new HashMap<>();
        for (int i = 0; i < perSide; i++) {
            ls.put("rt-" + i, REBEL_TROOPER);
            ds.put("st-" + i, STORMTROOPER);
        }
        VirtualTableScenario scn = new VirtualTableScenario(ls, ds, 80, 80,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields,
                VirtualTableScenario.Open);
        scn.ApplyAdHocModifier(new TotalForceGenerationModifier(null, 60, DS));
        scn.StartGame();
        PhysicalCardImpl dsSite = scn.GetDSStartingLocation();
        PhysicalCardImpl lsSite = scn.GetLSStartingLocation();
        List<PhysicalCardImpl> dsHome = new ArrayList<>(), dsAway = new ArrayList<>();
        List<PhysicalCardImpl> lsHome = new ArrayList<>(), lsAway = new ArrayList<>();
        for (int i = 0; i < perSide; i++) {
            (i % 4 == 0 ? dsAway : dsHome).add(scn.GetDSCard("st-" + i));
            (i % 4 == 0 ? lsAway : lsHome).add(scn.GetLSCard("rt-" + i));
        }
        scn.MoveCardsToLocation(dsSite, dsHome.toArray(new PhysicalCardImpl[0]));
        scn.MoveCardsToLocation(lsSite, dsAway.toArray(new PhysicalCardImpl[0]));
        scn.MoveCardsToLocation(lsSite, lsHome.toArray(new PhysicalCardImpl[0]));
        scn.MoveCardsToLocation(lsSite, lsAway.toArray(new PhysicalCardImpl[0]));

        DefaultSwccgGame game = scn.game();
        List<GameCommunicationChannel> channels = new ArrayList<>();
        int n = 1;
        for (String player : Arrays.asList(DS, LS)) {
            GameCommunicationChannel channel = new GameCommunicationChannel(player, n++);
            game.addGameStateListener(player, channel);
            channels.add(channel);
        }
        channels.forEach(GameCommunicationChannel::consumeGameEvents);
        int inPlay = game.getGameState().getCardsInPlay().size();

        // stats (the status bar), as the engine recomputes them once per decision
        TurnProcedure turnProcedure = (TurnProcedure) field(game, DefaultSwccgGame.class, "_turnProcedure");
        long t = System.nanoTime();
        game.getGameStats().updateGameStats(game);
        long stats = System.nanoTime() - t;

        // a revert snapshot of this board
        t = System.nanoTime();
        GameSnapshot.createGameSnapshot(-1, "bench", game.getGameState(),
                (com.gempukku.swccgo.logic.modifiers.querying.ModifiersLogic) field(game, DefaultSwccgGame.class, "_modifiersLogic"),
                game.getActionsEnvironment(), turnProcedure);
        long snapshot = System.nanoTime() - t;

        // activate 18 Force (two answers)
        scn.DSChooseAction("Activate Force");
        t = System.nanoTime();
        scn.DSDecided(18);
        if (scn.LSDecisionAvailable("Choose amount of Force to allow opponent to activate"))
            scn.LSDecided(18);
        long activate = System.nanoTime() - t;

        // pass through to the battle phase, then start a battle at the contested site (-1 when the scenario couldn't)
        long toBattle = -1_000_000L, battle = -1_000_000L;
        try {
            t = System.nanoTime();
            for (int guard = 0; guard < 200 && game.getGameState().getCurrentPhase() != Phase.BATTLE; guard++) {
                boolean acted = false;
                for (String p : Arrays.asList(DS, LS)) {
                    var d = scn.userFeedback().getAwaitingDecision(p);
                    if (d != null) {
                        scn.PlayerDecided(p, "");
                        acted = true;
                        break;
                    }
                }
                if (!acted)
                    throw new IllegalStateException("no decision to pass");
            }
            toBattle = System.nanoTime() - t;
            if (scn.DSCanInitiateBattle(lsSite)) {
                Sampler sampler = Boolean.getBoolean("gemp.bench.sample") ? new Sampler(Thread.currentThread()) : null;
                t = System.nanoTime();
                scn.DSInitiateBattle(lsSite);
                battle = System.nanoTime() - t;
                if (sampler != null)
                    sampler.report(board);
            }
        } catch (RuntimeException | AssertionError exp) {
            StringBuilder pending = new StringBuilder();
            for (String p : Arrays.asList(DS, LS)) {
                var d = scn.userFeedback().getAwaitingDecision(p);
                if (d != null)
                    pending.append(p).append(": ").append(d.getText()).append("; ");
            }
            System.out.println("[board136] " + board + " phase/battle step skipped: " + exp.getMessage() + " phase="
                    + game.getGameState().getCurrentPhase() + " pending=" + pending);
        }
        long events = 0;
        for (GameCommunicationChannel channel : channels)
            events += channel.consumeGameEvents().size();

        return String.format(Locale.ROOT, "%d,%d,%.2f,%.2f,%.1f,%.1f,%.1f,%d",
                board, inPlay, ms(stats), ms(snapshot), ms(activate), ms(toBattle), ms(battle), events);
    }

    /** A plain stack sampler (every 5 ms) for one thread: self time and inclusive time per method. */
    static final class Sampler {
        private final java.util.Map<String, Integer> self = new java.util.HashMap<>();
        private final java.util.Map<String, Integer> inclusive = new java.util.HashMap<>();
        private final java.util.Map<String, Integer> chains = new java.util.HashMap<>();
        private int samples;
        private volatile boolean running = true;
        private final Thread worker;

        Sampler(Thread target) {
            worker = new Thread(() -> {
                while (running) {
                    StackTraceElement[] st = target.getStackTrace();
                    if (st.length > 0) {
                        samples++;
                        self.merge(st[0].getClassName() + "." + st[0].getMethodName(), 1, Integer::sum);
                        java.util.Set<String> seen = new java.util.HashSet<>();
                        StringBuilder chain = new StringBuilder();
                        int depth = 0;
                        for (StackTraceElement e : st) {
                            String m = e.getClassName().replace("com.gempukku.swccgo.", "") + "." + e.getMethodName();
                            if (seen.add(m))
                                inclusive.merge(m, 1, Integer::sum);
                            if (depth < 14 && e.getClassName().startsWith("com.gempukku")) {
                                chain.append(depth == 0 ? "" : " < ").append(m.substring(m.lastIndexOf('.', m.lastIndexOf('.') - 1) + 1)).append(":").append(e.getLineNumber());
                                depth++;
                            }
                        }
                        chains.merge(chain.toString(), 1, Integer::sum);
                    }
                    try { Thread.sleep(5); } catch (InterruptedException ignored) { return; }
                }
            });
            worker.setDaemon(true);
            worker.start();
        }

        void report(int board) throws InterruptedException {
            running = false;
            worker.join();
            StringBuilder sb = new StringBuilder("== sample board=" + board + " samples=" + samples + "\n-- self\n");
            top(self, 15, sb);
            sb.append("-- inclusive\n");
            top(inclusive, Integer.getInteger("gemp.bench.top", 45), sb);
            sb.append("-- chains\n");
            top(chains, 12, sb);
            System.out.println(sb);
        }

        private void top(java.util.Map<String, Integer> m, int n, StringBuilder sb) {
            m.entrySet().stream().sorted((a, b) -> b.getValue() - a.getValue()).limit(n)
                    .forEach(e -> sb.append(String.format(Locale.ROOT, "%5.1f%% %s%n", 100.0 * e.getValue() / Math.max(1, samples), e.getKey())));
        }
    }

    @Test
    public void BoardScalingBenchmarkRun() throws Exception {
        Assume.assumeTrue("set -Dgemp.bench=true to run", Boolean.getBoolean("gemp.bench"));
        int[] boards = Arrays.stream(System.getProperty("gemp.bench.board", "50,100,200,400,800").split(","))
                .mapToInt(s -> Integer.parseInt(s.trim())).toArray();
        Path out = Paths.get(System.getProperty("gemp.bench.out", "target/large-deck-bench"));
        Files.createDirectories(out);
        Path csv = out.resolve("board-scaling.csv");
        StringBuilder sb = new StringBuilder("characters,cards_in_play,stats_ms,snapshot_ms,activate18_ms,to_battle_ms,start_battle_ms,client_events\n");
        measure(20); // warm-up
        for (int board : boards) {
            String line = measure(board);
            System.out.println("[board136] " + line);
            sb.append(line).append('\n');
        }
        Files.write(csv, sb.toString().getBytes(StandardCharsets.UTF_8));
    }
}
