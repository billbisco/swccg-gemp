package com.gempukku.swccgo.cards.set1263.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.decisions.CardActionSelectionDecision;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.*;

public class Card_1263_013_Tests {
    private static final String BOOST = "Increase Force drains at related battlegrounds this turn";
    private static final String CANCEL = "Cancel Force drain at a related site";

    private VirtualTableScenario scenario() {
        var scn = new VirtualTableScenario(
                new HashMap<>(Map.of("ridge", "3_62", "trench", "3_63", "rebel", "1_28")),
                new HashMap<>(Map.ofEntries(Map.entry("garrison", "1263_13"),
                        Map.entry("walker1", "3_154"), Map.entry("walker2", "3_155"),
                        Map.entry("walker3", "3_157"), Map.entry("pilot1", "3_87"),
                        Map.entry("pilot3", "3_83"), Map.entry("perimeter", "3_144"),
                        Map.entry("nonBattleground", "3_148"), Map.entry("hoth", "3_143"),
                        Map.entry("wastes", "1_293"), Map.entry("jawa", "1_292"),
                        Map.entry("mos", "1_295"), Map.entry("trooper", "1_194"))),
                26, 26, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        scn.StartGame();
        scn.MoveLocationToTable(scn.GetDSCard("hoth"));
        scn.MoveLocationToTable(scn.GetLSCard("trench"));
        scn.MoveLocationToTable(scn.GetDSCard("perimeter"));
        scn.MoveLocationToTable(scn.GetLSCard("ridge"));
        for (String key : List.of("nonBattleground", "wastes", "jawa", "mos")) {
            scn.MoveLocationToTable(scn.GetDSCard(key));
        }
        scn.MoveCardsToDSHand(scn.GetDSCard("garrison"));
        return scn;
    }

    @Test public void WalkerGarrisonPrintedMetadataMatches() {
        var scn = scenario();
        var b = scn.GetDSCard("garrison").getBlueprint();
        assertEquals("Walker Garrison", b.getTitle());
        assertEquals(Side.DARK, b.getSide());
        assertEquals(Uniqueness.UNRESTRICTED, b.getUniqueness());
        assertEquals(4, b.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.LOST, b.getCardSubtype());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_3, b.getExpansionSet());
        assertEquals(Rarity.V, b.getRarity());
        assertTrue(b.hasVirtualSuffix());
        assertTrue(b.isLegacy());
        assertEquals("When efficiently deployed, a squadron of AT-ATs can quickly take control of a wide area, making it easy for Imperial forces to dominate a planet.", b.getLore());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(List.of(CardType.INTERRUPT)));
        scn.BlueprintIconCheck(b, new ArrayList<>(List.of(Icon.INTERRUPT, Icon.PREMIUM, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>());
    }

    private void walkers(VirtualTableScenario scn, PhysicalCardImpl first, PhysicalCardImpl second,
                         PhysicalCardImpl third, boolean pilotThird) {
        var firstWalker = scn.GetDSCard("walker1");
        var thirdWalker = scn.GetDSCard("walker3");
        scn.MoveCardsToLocation(first, firstWalker);
        scn.BoardAsPilot(firstWalker, scn.GetDSCard("pilot1"));
        scn.MoveCardsToLocation(second, scn.GetDSCard("walker2")); // permanent pilot
        scn.MoveCardsToLocation(third, thirdWalker);
        if (pilotThird) scn.BoardAsPilot(thirdWalker, scn.GetDSCard("pilot3"));
    }

    @Test public void WalkerGarrisonIncreasesAnActualHothDrainByTwoAndExpiresNextTurn() {
        var scn = scenario();
        var ridge = scn.GetLSCard("ridge");
        var hoth = scn.GetDSCard("hoth");
        var notBattleground = scn.GetDSCard("nonBattleground");
        var elsewhere = scn.GetDSStartingLocation();
        walkers(scn, scn.GetLSCard("trench"), scn.GetDSCard("perimeter"), ridge, true);
        scn.MoveCardsToLocation(elsewhere, scn.GetDSCard("trooper"));
        scn.SkipToPhase(Phase.CONTROL);
        float baseHoth = drainAmount(scn, hoth, scn.DS);
        float baseUnrelated = drainAmount(scn, elsewhere, scn.DS);
        float baseNonBattleground = drainAmount(scn, notBattleground, scn.DS);
        float baseOpponent = drainAmount(scn, ridge, scn.LS);
        playBoost(scn);
        assertEquals(baseHoth + 2, drainAmount(scn, hoth, scn.DS), scn.epsilon);
        assertEquals(baseUnrelated, drainAmount(scn, elsewhere, scn.DS), scn.epsilon);
        assertEquals(baseNonBattleground, drainAmount(scn, notBattleground, scn.DS), scn.epsilon);
        assertEquals(baseOpponent, drainAmount(scn, ridge, scn.LS), scn.epsilon);
        assertEquals(3, resolveDrain(scn, ridge, true, false));
        scn.SkipToLSTurn(Phase.CONTROL);
        assertEquals(baseHoth, drainAmount(scn, hoth, scn.DS), scn.epsilon);
    }

    @Test public void WalkerGarrisonIncreasesActualNonHothDrainByOneAtRelatedSiteWithoutWalker() {
        var scn = scenario();
        var mos = scn.GetDSCard("mos");
        walkers(scn, scn.GetDSStartingLocation(), scn.GetDSCard("wastes"), scn.GetDSCard("jawa"), true);
        scn.MoveCardsToLocation(mos, scn.GetDSCard("trooper"));
        scn.SkipToPhase(Phase.CONTROL);
        playBoost(scn);
        assertEquals(2, resolveDrain(scn, mos, true, false));
    }

    @Test public void WalkerGarrisonNeedsThreeDifferentRelatedBattlegroundsWithActiveWalkers() {
        for (int reason = 0; reason < 4; reason++) {
            var scn = scenario();
            var ridge = scn.GetLSCard("ridge");
            var third = switch (reason) {
                case 0 -> ridge; // unpiloted
                case 1 -> scn.GetDSCard("perimeter"); // two AT-ATs at the same site
                case 2 -> scn.GetDSCard("wastes"); // unrelated
                default -> scn.GetDSCard("nonBattleground");
            };
            walkers(scn, scn.GetLSCard("trench"), scn.GetDSCard("perimeter"), third, reason != 0);
            scn.SkipToPhase(Phase.CONTROL);
            assertFalse(scn.DSCardPlayAvailable(scn.GetDSCard("garrison"), BOOST));
        }
    }

    @Test public void WalkerGarrisonCancelsActualDrainAtRelatedSiteWithOnlyOneATAT() {
        var scn = scenario();
        scn.MoveCardsToLocation(scn.GetDSCard("wastes"), scn.GetDSCard("walker2"));
        var drainSite = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(drainSite, scn.GetLSCard("rebel"));
        scn.SkipToLSTurn(Phase.CONTROL);
        assertEquals(0, resolveDrain(scn, drainSite, false, true));
        assertInZone(Zone.LOST_PILE, scn.GetDSCard("garrison"));
    }

    @Test public void WalkerGarrisonCannotCancelUnrelatedDrainOrWithOnlyUnpilotedATAT() {
        for (boolean unpiloted : List.of(false, true)) {
            var scn = scenario();
            var walker = scn.GetDSCard(unpiloted ? "walker1" : "walker2");
            var walkerSite = unpiloted ? scn.GetDSCard("wastes") : scn.GetLSCard("ridge");
            scn.MoveCardsToLocation(walkerSite, walker);
            var drainSite = scn.GetDSStartingLocation();
            scn.MoveCardsToLocation(drainSite, scn.GetLSCard("rebel"));
            scn.SkipToLSTurn(Phase.CONTROL);
            assertEquals(1, resolveDrain(scn, drainSite, false, false));
            assertInZone(Zone.HAND, scn.GetDSCard("garrison"));
        }
    }

    private void playBoost(VirtualTableScenario scn) {
        var garrison = scn.GetDSCard("garrison");
        assertTrue(scn.DSCardPlayAvailable(garrison, BOOST));
        scn.DSPlayCard(garrison, BOOST);
        scn.PassAllResponses();
        if (scn.LSGetDecision() instanceof CardActionSelectionDecision) scn.LSPass();
        assertInZone(Zone.LOST_PILE, garrison);
    }

    private int resolveDrain(VirtualTableScenario scn, PhysicalCardImpl site, boolean darkDrain, boolean cancel) {
        int before = darkDrain ? scn.GetLSLostPileCount() : scn.GetDSLostPileCount();
        if (darkDrain) scn.DSForceDrainAt(site); else scn.LSForceDrainAt(site);
        boolean canceled = false;
        for (int i = 0; i < 45; i++) {
            if (darkDrain ? scn.AwaitingDSControlPhaseActions() : scn.AwaitingLSControlPhaseActions()) break;
            if (scn.AwaitingLSForceLossPayment()) scn.LSPayRemainingForceLossFromReserveDeck();
            else if (scn.AwaitingDSForceLossPayment()) scn.DSPayRemainingForceLossFromReserveDeck();
            else if (scn.DSGetDecision() instanceof CardActionSelectionDecision
                    && scn.DSCardPlayAvailable(scn.GetDSCard("garrison"), CANCEL)) {
                assertTrue("Cancel action should be absent when its conditions fail", cancel);
                scn.DSPlayCard(scn.GetDSCard("garrison"), CANCEL);
                canceled = true;
            } else if (scn.LSGetDecision() instanceof CardActionSelectionDecision) scn.LSPass();
            else if (scn.DSGetDecision() instanceof CardActionSelectionDecision) scn.DSPass();
            else fail("Unexpected drain decision: " + scn.GetCurrentDecision().getText());
        }
        assertTrue(darkDrain ? scn.AwaitingDSControlPhaseActions() : scn.AwaitingLSControlPhaseActions());
        assertEquals(cancel, canceled);
        return (darkDrain ? scn.GetLSLostPileCount() : scn.GetDSLostPileCount()) - before - (canceled ? 1 : 0);
    }

    private float drainAmount(VirtualTableScenario scn, PhysicalCardImpl site, String player) {
        return scn.game().getModifiersQuerying().getForceDrainAmount(scn.gameState(), site, player);
    }
}
