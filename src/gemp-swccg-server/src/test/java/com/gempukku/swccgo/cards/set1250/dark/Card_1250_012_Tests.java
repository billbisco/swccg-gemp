package com.gempukku.swccgo.cards.set1250.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import static org.junit.Assert.*;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;

public class Card_1250_012_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{ }},
                new HashMap<>() {{ put("watto", "1250_12"); put("junkyard", "12_178"); put("trooper", "1_194"); }},
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }
    @Test public void WattoStatsIconsAndKeywordsMatchPrintedImage() {
        var scn = scenario(); var bp = scn.GetDSCard("watto").getBlueprint();
        assertEquals("Watto", bp.getTitle()); assertTrue(bp.hasVirtualSuffix());
        assertEquals(Side.DARK, bp.getSide()); assertEquals(Uniqueness.UNIQUE, bp.getUniqueness());
        assertEquals(3, bp.getDestiny(), scn.epsilon); assertEquals(2, bp.getDeployCost(), scn.epsilon);
        assertEquals(3, bp.getPower(), scn.epsilon); assertEquals(2, bp.getAbility(), scn.epsilon); assertEquals(4, bp.getForfeit(), scn.epsilon);
        assertEquals(Species.TOYDARIAN, bp.getSpecies()); assertTrue(bp.hasPersona(Persona.WATTO));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, bp.getExpansionSet()); assertEquals(Rarity.V, bp.getRarity());
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>() {{ add(CardType.ALIEN); }});
        scn.BlueprintIconCheck(bp, new ArrayList<>() {{ add(Icon.ALIEN); add(Icon.EPISODE_I); add(Icon.TATOOINE); add(Icon.LEGACY_BLOCK_5); }});
        scn.BlueprintKeywordCheck(bp, new ArrayList<>() {{ add(Keyword.GAMBLER); }});
    }
    private VirtualTableScenario setup(boolean junkyard) {
        var scn = scenario(); scn.StartGame(); scn.MoveLocationToTable(scn.GetDSCard("junkyard"));
        scn.MoveCardsToLocation(junkyard ? scn.GetDSCard("junkyard") : scn.GetDSStartingLocation(), scn.GetDSCard("watto"));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("trooper"));
        scn.SkipToDSTurn(Phase.CONTROL); return scn;
    }
    private void force(VirtualTableScenario scn, int... destinies) {
        scn.MoveCardsToTopOfDSReserveDeck(scn.gameState().getForcePile(scn.DS).toArray(PhysicalCardImpl[]::new));
        for (int destiny : destinies) scn.MoveCardsToTopOfDSForcePile(scn.GetDSDestiny(destiny));
    }
    private void reveal(VirtualTableScenario scn) {
        assertTrue(scn.DSCardActionAvailable(scn.GetDSCard("watto"), "Reveal random Force Pile card"));
        scn.DSUseCardAction(scn.GetDSCard("watto"), "Reveal random Force Pile card"); scn.PassAllResponses();
    }
    private void returnToDarkActions(VirtualTableScenario scn) {
        if (scn.AnyDecisionsAvailable(scn.LS)) {
            assertTrue(scn.LSDecisionAvailable("action"));
            scn.LSPass();
        }
        assertTrue(scn.AwaitingDSControlPhaseActions());
    }
    @Test public void WattoOddRevealIncreasesActualDrainHereAndUsesRevealedCard() {
        var scn = setup(true); var yard = scn.GetDSCard("junkyard"); force(scn, 3);
        int hand = scn.GetDSHandCount(); int life = scn.GetLSLifeForceRemaining(); reveal(scn);
        assertInZone(Zone.USED_PILE, scn.GetDSDestiny(3)); assertEquals(hand, scn.GetDSHandCount());
        returnToDarkActions(scn); scn.DSForceDrainAt(yard); scn.PassForceDrainStartResponses(); assertEquals(2, scn.GetForceDrainTotal());
        scn.LSPayRemainingForceLossFromReserveDeck(); scn.PassAllResponses(); assertEquals(life - 2, scn.GetLSLifeForceRemaining());
    }
    @Test public void WattoOddRevealDoesNotIncreaseDrainAtOtherSite() {
        var scn = setup(true); force(scn, 1); var site = scn.GetDSStartingLocation();
        int base = (int) scn.game().getModifiersQuerying().getForceDrainAmount(scn.gameState(), site, scn.DS);
        reveal(scn); returnToDarkActions(scn); scn.DSForceDrainAt(site); scn.PassForceDrainStartResponses(); assertEquals(base, scn.GetForceDrainTotal());
    }
    @Test public void WattoOddBonusExpiresAtEndOfTurn() {
        var scn = setup(true); force(scn, 1); reveal(scn); scn.SkipToLSTurn(Phase.CONTROL); scn.SkipToDSTurn(Phase.CONTROL);
        scn.DSForceDrainAt(scn.GetDSCard("junkyard")); scn.PassForceDrainStartResponses(); assertEquals(1, scn.GetForceDrainTotal());
    }
    private void even(int destiny) {
        var scn = setup(true); force(scn, destiny); scn.PrepareDSDestiny(5); var top = scn.GetTopOfDSReserveDeck();
        int hand = scn.GetDSHandCount(); reveal(scn);
        assertEquals(hand + 1, scn.GetDSHandCount()); assertInZone(Zone.HAND, top); assertInZone(Zone.USED_PILE, scn.GetDSDestiny(destiny));
        returnToDarkActions(scn); scn.DSForceDrainAt(scn.GetDSCard("junkyard")); scn.PassForceDrainStartResponses(); assertEquals(1, scn.GetForceDrainTotal());
    }
    @Test public void WattoEvenRevealDrawsTopReserveCardAndUsesRevealedCard() { even(2); }
    @Test public void WattoZeroDestinyTakesEvenBranch() { even(0); }
    @Test public void WattoMayRevealOnlyOnceInSameTurn() {
        var scn = setup(true); force(scn, 3); reveal(scn); force(scn, 1);
        scn.SkipToPhase(Phase.DEPLOY); assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("watto"), "Reveal random Force Pile card"));
    }
    @Test public void WattoMayRevealAgainDuringOpponentsTurn() {
        var scn = setup(true); force(scn, 3); reveal(scn); scn.SkipToLSTurn(Phase.ACTIVATE); force(scn, 2);
        scn.LSPass();
        if (scn.LSDecisionAvailable("You have not activated Force")) scn.LSChooseYes();
        reveal(scn); assertInZone(Zone.USED_PILE, scn.GetDSDestiny(2));
    }
    @Test public void WattoCannotRevealAtAnotherSite() {
        var scn = setup(false); assertTrue(scn.AwaitingDSControlPhaseActions());
        assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("watto"), "Reveal random Force Pile card"));
    }
    @Test public void WattoCannotRevealFromEmptyForcePile() {
        var scn = setup(true); force(scn); scn.SkipToPhase(Phase.DEPLOY); assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("watto"), "Reveal random Force Pile card"));
    }
    @Test public void WattoRandomRevealCanSelectEitherCardInsteadOfAlwaysTakingTop() {
        var selected = new HashSet<Integer>();
        for (long seed : new long[]{0, 4096, 8192, 12288}) {
            var scn = setup(true); force(scn, 1, 2); scn.gameState().getRandom().setSeed(seed); reveal(scn);
            for (int destiny : new int[]{1, 2})
                if (scn.GetDSDestiny(destiny).getZone() == Zone.TOP_OF_USED_PILE || scn.GetDSDestiny(destiny).getZone() == Zone.USED_PILE) selected.add(destiny);
            assertEquals(1, scn.GetDSForcePileCount());
        }
        assertEquals(new HashSet<Integer>() {{ add(1); add(2); }}, selected);
    }
}
