package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class Card_1261_039_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(
                new HashMap<>(Map.of("obi", "1_21", "luke", "1_19", "resilience", "11_29",
                        "lLow1", "1_21", "lLow2", "1_21", "lHigh1", "11_29", "lHigh2", "11_29")),
                new HashMap<>(Map.of("epic", "1261_39", "vader", "1_168", "circle", "1_271",
                        "obsession", "101_6", "saber", "1_324", "dLow1", "1_168", "dLow2", "1_168")),
                25, 25, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void finalPrintingUsesTheVirtualIdentityAndEpicEventMetadata() {
        var scn = scenario();
        var bp = scn.GetDSCard("epic").getBlueprint();
        assertEquals("Epic Duel", bp.getTitle());
        assertEquals(Side.DARK, bp.getSide());
        assertEquals(0, bp.getDestiny(), scn.epsilon);
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, bp.getExpansionSet());
        assertEquals(Uniqueness.UNIQUE, bp.getUniqueness());
        assertEquals(Rarity.V, bp.getRarity());
        assertTrue(bp.hasVirtualSuffix());
        assertEquals(PlayCardZoneOption.YOUR_SIDE_OF_TABLE, bp.getSinglePlayCardZoneOption());
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>(List.of(CardType.EPIC_EVENT)));
        scn.BlueprintIconCheck(bp, new ArrayList<>(List.of(Icon.EPIC_EVENT, Icon.CLOUD_CITY, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(bp, new ArrayList<>());
    }

    @Test(timeout = 10000)
    public void deploysToTableAndProhibitsResilienceDuringBothPlayersMovePhasesOnly() {
        var scn = scenario();
        var epic = scn.GetDSCard("epic");
        var resilience = scn.GetLSCard("resilience");
        scn.StartGame();
        scn.MoveCardsToDSHand(epic);
        scn.MoveCardsToLSHand(resilience);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(5);
        scn.DSDeployCard(epic);
        scn.PassAllResponses();
        assertEquals(Zone.SIDE_OF_TABLE, epic.getZone());
        var query = scn.game().getModifiersQuerying();
        assertFalse(query.isPlayingCardProhibited(scn.gameState(), resilience, false));
        scn.SkipToPhase(Phase.MOVE);
        assertTrue(query.isPlayingCardProhibited(scn.gameState(), resilience, false));
        scn.SkipToLSTurn(Phase.MOVE);
        assertTrue(query.isPlayingCardProhibited(scn.gameState(), resilience, false));
        scn.SkipToPhase(Phase.DRAW);
        assertFalse(query.isPlayingCardProhibited(scn.gameState(), resilience, false));
    }

    @Test(timeout = 10000)
    public void actualCircleUsesTwoDrawsAndAbilityThenDarkWinsByBothSaberBonuses() {
        var scn = openDuel("circle", "obi", false, true, false);
        assertDuelTotals(scn, 10, 8);
        settle(scn, true);
        assertTrue(scn.GetLSLostPile().contains(scn.GetLSCard("obi")));
        assertEquals(3, scn.GetLSLostPileCount());
        assertEquals(2, scn.GetDSLostPileCount());
        assertTrue(scn.GetDSLostPile().contains(scn.GetDSCard("circle")));
        assertTrue(scn.GetDSCard("vader").getZone().isInPlay());
    }

    @Test(timeout = 10000)
    public void actualObsessionOnLightsTurnLosesVaderAndWinnerMayDeclineRetrieval() {
        var scn = openDuel("obsession", "luke", true, false, true);
        assertDuelTotals(scn, 8, 16);
        settle(scn, false);
        assertTrue(scn.GetDSLostPile().contains(scn.GetDSCard("vader")));
        assertEquals(4, scn.GetDSLostPileCount());
        assertEquals(3, scn.GetLSLostPileCount());
        assertTrue(scn.GetLSCard("luke").getZone().isInPlay());
    }

    @Test(timeout = 10000)
    public void equalTotalsTieWithoutLossOrRetrieval() {
        var scn = openDuel("circle", "obi", false, false, false);
        assertNull(scn.gameState().getDuelState());
        assertTrue(scn.GetLSCard("obi").getZone().isInPlay());
        assertTrue(scn.GetDSCard("vader").getZone().isInPlay());
        assertEquals(0, scn.GetLSLostPileCount());
        assertEquals(4, scn.GetDSLostPileCount());
    }

    private VirtualTableScenario openDuel(String interrupt, String target, boolean lightTurn,
            boolean saber, boolean highLightDraws) {
        var scn = scenario();
        var play = scn.GetDSCard(interrupt);
        var vader = scn.GetDSCard("vader");
        var light = scn.GetLSCard(target);
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(scn.GetDSCard("epic"));
        scn.MoveCardsToDSHand(play);
        scn.MoveCardsToLSHand(scn.GetLSCard("resilience"));
        scn.MoveCardsToLocation(site, vader);
        if (lightTurn)
            scn.SkipToLSTurn(Phase.CONTROL);
        else
            scn.SkipToDSTurn(Phase.CONTROL);
        var query = scn.game().getModifiersQuerying();
        assertTrue(query.getInitiateEpicDuelActions(scn.gameState(), play).isEmpty());
        scn.MoveCardsToLocation(site, light);
        if (saber)
            scn.AttachCardsTo(vader, scn.GetDSCard("saber"));
        scn.EnsureDSForcePile(6);
        scn.EnsureLSForcePile(6);
        scn.SkipToPhase(Phase.MOVE);
        assertFalse("Native matching should find the real duelists",
                query.getInitiateEpicDuelActions(scn.gameState(), play).isEmpty());
        var stash = target.equals("obi") ? scn.GetDSReserveDeck() : scn.GetLSReserveDeck();
        var stashCards = stash.stream().filter(c -> !List.of(scn.GetDSCard("dLow1"), scn.GetDSCard("dLow2"),
                scn.GetLSCard("lHigh1"), scn.GetLSCard("lHigh2")).contains(c)).limit(3)
                .map(PhysicalCardImpl.class::cast).toArray(PhysicalCardImpl[]::new);
        if (target.equals("obi"))
            scn.MoveCardsToTopOfDSLostPile(stashCards);
        else
            scn.MoveCardsToTopOfLSLostPile(stashCards);
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSCard("dLow1"), scn.GetDSCard("dLow2"));
        scn.MoveCardsToTopOfLSReserveDeck(scn.GetLSCard(highLightDraws ? "lHigh1" : "lLow1"),
                scn.GetLSCard(highLightDraws ? "lHigh2" : "lLow2"));
        if (lightTurn && scn.LSGetDecision() != null)
            scn.LSPass();
        assertTrue("Epic mode is available", scn.DSCardActionAvailable(play, "Initiate epic duel"));
        scn.DSUseCardAction(play, "Initiate epic duel");
        if (scn.DSHasCardChoiceAvailable(vader))
            scn.DSChooseCard(vader);
        if (scn.DSHasCardChoiceAvailable(light))
            scn.DSChooseCard(light);
        for (int i = 0; i < 70 && scn.GetCurrentDecision().getText().toLowerCase().contains("optional response"); i++) {
            if (scn.DSGetDecision() != null)
                scn.DSPass();
            else
                scn.LSPass();
        }
        return scn;
    }

    private void assertDuelTotals(VirtualTableScenario scn, int dark, int light) {
        var duel = scn.gameState().getDuelState();
        assertNotNull("The duel must remain live for its loss decisions", duel);
        assertTrue("Before totals: " + scn.GetCurrentDecision().getText(), duel.isReachedResults());
        assertEquals(dark, duel.getFinalDuelTotal(scn.DS), scn.epsilon);
        assertEquals(light, duel.getFinalDuelTotal(scn.LS), scn.epsilon);
    }

    private void settle(VirtualTableScenario scn, boolean retrieve) {
        for (int i = 0; i < 12 && scn.gameState().getDuelState() != null; i++) {
            if (scn.DSDecisionAvailable("Choose Force to lose"))
                scn.DSChooseCard(scn.GetTopOfDSForcePile());
            else if (scn.LSDecisionAvailable("Choose Force to lose"))
                scn.LSChooseCard(scn.GetTopOfLSForcePile());
            else if (scn.DSDecisionAvailable("Retrieve 2 Force?")) {
                if (retrieve)
                    scn.DSChooseYes();
                else
                    scn.DSChooseNo();
            } else if (scn.LSDecisionAvailable("Retrieve 2 Force?")) {
                if (retrieve)
                    scn.LSChooseYes();
                else
                    scn.LSChooseNo();
            } else {
                assertTrue(scn.GetCurrentDecision().getText().toLowerCase().contains("optional response"));
                scn.PassAllResponses();
            }
        }
        assertNull(scn.gameState().getDuelState());
    }

    @org.junit.Test
    public void CurrentTextIsTheLatestVersion() {
        // PC card list (PC-E1) is the latest text ("lowest total"; same play).
        var scn = new com.gempukku.swccgo.framework.VirtualTableScenario(new java.util.HashMap<>(), new java.util.HashMap<>(java.util.Map.of("c", "1261_39")), 10, 10,
                com.gempukku.swccgo.framework.StartingSetup.DefaultLSGroundLocation, com.gempukku.swccgo.framework.StartingSetup.DefaultDSGroundLocation,
                com.gempukku.swccgo.framework.StartingSetup.NoLSStartingInterrupts, com.gempukku.swccgo.framework.StartingSetup.NoDSStartingInterrupts,
                com.gempukku.swccgo.framework.StartingSetup.NoLSShields, com.gempukku.swccgo.framework.StartingSetup.NoDSShields,
                com.gempukku.swccgo.framework.VirtualTableScenario.Open);
        var text = scn.GetDSCard("c").getBlueprint().getGameText();
        org.junit.Assert.assertEquals("Deploy on table. A Jedi's Resilience may not be played during move phases. The Circle Is Now Complete or Vader's Obsession may be played during any move phase to initiate an epic duel as follows: If Vader present with specified target, each player draws two destiny (if armed with a lightsaber, each destiny is +1); add ability. Loser (lowest total) is lost, losing player loses 2 Force, and winning player may retrieve 2 Force.", text);
        org.junit.Assert.assertFalse(text.contains("(lower total)"));
        org.junit.Assert.assertNull(scn.GetDSCard("c").getBlueprint().getLegacyVersion());
    }
}
