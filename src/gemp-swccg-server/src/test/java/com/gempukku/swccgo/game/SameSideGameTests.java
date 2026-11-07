package com.gempukku.swccgo.game;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.decisions.AwaitingDecision;
import com.gempukku.swccgo.logic.timing.DefaultSwccgGame;
import com.gempukku.swccgo.logic.timing.DefaultUserFeedback;
import com.gempukku.swccgo.logic.vo.SwccgDeck;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * swccg-guild/work#176, same-side games (Bill 2026-11-05): a coin toss decides who takes the Light Side seat; the "Light
 * Side goes first" cards read seat-neutrally (Death Star: your opponent goes first; Massassi Throne Room: you go first);
 * claims in opposite directions tie and a coin toss decides who goes first (Bill 2026-11-07); each player uses their seat's Force icons; uniqueness is per player.
 */
public class SameSideGameTests {
    static final String A = "alice";
    static final String B = "bob";

    static SwccgDeck deck(String name, String startingLocation, String... extra) {
        SwccgDeck deck = new SwccgDeck(name);
        deck.addCard(startingLocation);
        for (String card : extra)
            deck.addCard(card);
        boolean light = VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint(startingLocation).getSide() == com.gempukku.swccgo.common.Side.LIGHT;
        for (int i = 0; i < 40; i++)
            deck.addCard(light ? "1_28" : "1_194");
        return deck;
    }

    static class Game {
        DefaultSwccgGame game;
        DefaultUserFeedback feedback;
        GameState state() { return game.getGameState(); }
    }

    static Game start(SwccgDeck a, SwccgDeck b, long seed) {
        Game g = new Game();
        g.feedback = new DefaultUserFeedback();
        Map<String, SwccgDeck> decks = new HashMap<>();
        decks.put(A, a);
        decks.put(B, b);
        g.game = new DefaultSwccgGame(VirtualTableScenario._formatLibrary.getFormat("anything_goes"), decks, g.feedback,
                VirtualTableScenario._cardLibrary, new HashMap<>(Map.of(A, 0, B, 0)), false);
        g.feedback.setGame(g.game);
        g.game.getGameState().setRandomSeed(seed);
        g.game.startGame();
        return g;
    }

    static String answer(AwaitingDecision d) {
        var p = d.getDecisionParameters();
        if (p.containsKey("cardId") && p.get("cardId").length > 0) return p.get("cardId")[0];
        if (p.containsKey("results")) return "0";
        if (p.containsKey("actionId")) return "";
        if (p.containsKey("min")) return p.get("min")[0];
        return "0";
    }

    /** Answers every decision with its first choice (passing optional actions) until the first turn starts. */
    static void toFirstTurn(Game g) {
        for (int i = 0; i < 200; i++) {
            if (g.state().getCurrentPhase() != null && g.state().getCurrentPhase() != Phase.PLAY_STARTING_CARDS
                    && g.state().getPlayersLatestTurnNumber(g.state().getCurrentPlayerId()) >= 1)
                return;
            boolean any = false;
            for (String p : List.of(A, B)) {
                AwaitingDecision d = g.feedback.getAwaitingDecision(p);
                if (d == null) continue;
                any = true;
                g.feedback.participantDecided(p);
                try {
                    d.decisionMade(answer(d));
                } catch (Exception e) {
                    throw new RuntimeException(d.getText(), e);
                }
                g.game.carryOutPendingActionsUntilDecisionNeeded();
                break;
            }
            if (!any) break;
        }
    }

    static String log(Game g) {
        return String.join("\n", g.state().getLastMessages());
    }

    // seeds whose coin toss seats alice on the Dark Side seat (7) and on the Light Side seat (1000)
    static final long ALICE_DARK = 7;
    static final long ALICE_LIGHT = 1000;

    static boolean logHas(Game g, String text) {
        return g.state().getLastMessages().stream().anyMatch(m -> m.contains(text));
    }

    @Test
    public void CoinTossSeatsTheSameSidePlayers() {
        Game g = start(deck("a", "2_143"), deck("b", "12_176"), ALICE_DARK);
        assertEquals(A, g.state().getDarkPlayer());
        assertEquals(B, g.state().getLightPlayer());
        assertTrue(g.state().isSameSideGame());
        assertTrue(log(g), logHas(g, "coin toss: bob takes the Light Side seat"));
        Game h = start(deck("a", "2_143"), deck("b", "12_176"), ALICE_LIGHT);
        assertEquals(A, h.state().getLightPlayer());
    }

    @Test
    public void DeathStarStarterLetsTheOpponentGoFirstFromEitherSeat() {
        for (long seed : new long[]{ALICE_DARK, ALICE_LIGHT}) {
            Game g = start(deck("a", "2_143"), deck("b", "12_176"), seed);
            toFirstTurn(g);
            assertEquals("seed " + seed + "\n" + log(g), B, g.state().getCurrentPlayerId());
            assertTrue(logHas(g, "bob goes first"));
        }
    }

    @Test
    public void MassassiThroneRoomStarterGoesFirstFromEitherSeat() {
        for (long seed : new long[]{ALICE_DARK, ALICE_LIGHT}) {
            Game g = start(deck("a", "1_138"), deck("b", "5_79"), seed);
            toFirstTurn(g);
            assertEquals("seed " + seed + "\n" + log(g), A, g.state().getCurrentPlayerId());
        }
    }

    @Test
    public void WithoutSuchCardsTheDarkSideSeatGoesFirst() {
        Game g = start(deck("a", "12_176"), deck("b", "4_154"), ALICE_LIGHT);
        toFirstTurn(g);
        assertEquals(log(g), g.state().getDarkPlayer(), g.state().getCurrentPlayerId());
    }

    @Test
    public void EachPlayerUsesTheForceIconsOnTheirSeatsSide() {
        // Death Star: 3 Dark Side icons, none for the Light Side; Marketplace: 1 and 1
        for (long seed : new long[]{ALICE_DARK, ALICE_LIGHT}) {
            Game g = start(deck("a", "2_143"), deck("b", "12_176"), seed);
            toFirstTurn(g);
            var q = g.game.getModifiersQuerying();
            float dark = q.getTotalForceGeneration(g.state(), g.state().getDarkPlayer());
            float light = q.getTotalForceGeneration(g.state(), g.state().getLightPlayer());
            assertEquals("seed " + seed, 3f, dark - light, 0.001f);
        }
    }

    @Test
    public void UniquenessIsPerPlayer() {
        // Jawa Pack (a unique Effect) on alice's side does not block bob's copy
        Game g = start(deck("a", "2_143", "1_219"), deck("b", "12_176", "1_219"), ALICE_DARK);
        toFirstTurn(g);
        PhysicalCard alices = null, bobs = null;
        for (PhysicalCard c : g.state().getAllPermanentCards())
            if (c.getBlueprintId(true).equals("1_219")) {
                if (c.getOwner().equals(A)) alices = c; else bobs = c;
            }
        assertNotNull(alices);
        assertNotNull(bobs);
        g.state().removeCardsFromZone(List.of(alices));
        g.state().addCardToZone(alices, com.gempukku.swccgo.common.Zone.SIDE_OF_TABLE, A);
        assertTrue("alice's copy is on the table", alices.getZone().isInPlay());
        assertFalse("bob may still deploy his own copy",
                g.game.getModifiersQuerying().isUniquenessOnTableLimitReached(g.state(), bobs));
    }
    /**
     * Both players' claims tie (Bill 2026-11-07): a coin toss with the game's seeded random decides who goes first. Over
     * a range of seeds the toss must go both ways, to both seats and to both players, and the same seed must give the
     * same result.
     */
    static void assertTieDecidedByCoinToss(String startA, String startB) {
        java.util.Set<String> winners = new java.util.HashSet<>();
        java.util.Set<Boolean> darkSeatWon = new java.util.HashSet<>();
        for (long seed = 1; seed <= 12; seed++) {
            Game g = start(deck("a", startA), deck("b", startB), seed);
            toFirstTurn(g);
            String winner = g.state().getFirstTurnCoinTossWinner();
            assertNotNull("seed " + seed + "\n" + log(g), winner);
            assertEquals("seed " + seed + "\n" + log(g), winner, g.state().getCurrentPlayerId());
            assertTrue(log(g), logHas(g, "the claims tie; a coin toss decides who goes first: " + winner + " wins the toss"));
            assertTrue(log(g), logHas(g, winner + " goes first"));
            assertFalse(log(g), logHas(g, "they cancel"));
            // the seeded outcome is reproducible
            Game again = start(deck("a", startA), deck("b", startB), seed);
            toFirstTurn(again);
            assertEquals("seed " + seed, winner, again.state().getFirstTurnCoinTossWinner());
            assertEquals("seed " + seed, winner, again.state().getCurrentPlayerId());
            winners.add(winner);
            darkSeatWon.add(winner.equals(g.state().getDarkPlayer()));
        }
        assertEquals("both players win the toss for some seed", 2, winners.size());
        assertEquals("both seats win the toss for some seed", 2, darkSeatWon.size());
    }

    @Test
    public void BothStartingAtDeathStarTieSoACoinTossDecides() {
        // the second Death Star converts the first (PC rules); each claimed the first turn for the other player
        assertTieDecidedByCoinToss("2_143", "2_143");
    }

    @Test
    public void BothStartingAtMassassiThroneRoomTieSoACoinTossDecides() {
        assertTieDecidedByCoinToss("1_138", "1_138");
    }

    @Test
    public void SingleClaimsMakeNoCoinToss() {
        for (long seed : new long[]{ALICE_DARK, ALICE_LIGHT}) {
            Game ds = start(deck("a", "2_143"), deck("b", "12_176"), seed);
            toFirstTurn(ds);
            assertEquals(B, ds.state().getCurrentPlayerId());
            assertNull(ds.state().getFirstTurnCoinTossWinner());
            assertFalse(log(ds), logHas(ds, "coin toss decides who goes first"));
            Game mtr = start(deck("a", "1_138"), deck("b", "5_79"), seed);
            toFirstTurn(mtr);
            assertEquals(A, mtr.state().getCurrentPlayerId());
            assertNull(mtr.state().getFirstTurnCoinTossWinner());
            assertFalse(log(mtr), logHas(mtr, "coin toss decides who goes first"));
        }
    }

    @Test
    public void NormalGamesAreUnchanged() {
        // Light vs Dark: no seating toss, Dark Side goes first unless Death Star / Massassi Throne Room says Light
        for (long seed : new long[]{ALICE_DARK, ALICE_LIGHT}) {
            Game plain = start(deck("a", "4_154"), deck("b", "5_79"), seed);
            assertFalse(plain.state().isSameSideGame());
            assertEquals(A, plain.state().getDarkPlayer());
            toFirstTurn(plain);
            assertEquals(log(plain), A, plain.state().getCurrentPlayerId());
            assertTrue(logHas(plain, "Dark Side goes first"));
            assertNull(plain.state().getFirstTurnCoinTossWinner());

            Game ds = start(deck("a", "2_143"), deck("b", "5_79"), seed);
            toFirstTurn(ds);
            assertEquals(log(ds), B, ds.state().getCurrentPlayerId());
            assertTrue(logHas(ds, "Light Side goes first"));
            assertNull(ds.state().getFirstTurnCoinTossWinner());

            Game both = start(deck("a", "2_143"), deck("b", "1_138"), seed);
            toFirstTurn(both);
            assertEquals(log(both), B, both.state().getCurrentPlayerId());
            assertNull(both.state().getFirstTurnCoinTossWinner());
            assertFalse(logHas(both, "coin toss decides who goes first"));
        }
    }
}
