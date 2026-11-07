package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import static org.junit.Assert.*;

public class Card_1261_005_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{ put("obiwan", "1_21"); put("han", "1_11"); }},
                new HashMap<>() {{ put("boba", "1261_5"); put("blaster", "5_179"); put("sword", "1_324");
                    put("vader", "1_168"); put("scout", "3_156"); put("ship", "1_306");
                    put("hoth", "3_143"); put("bay", "1_285"); put("otherBay", "1_291"); }}, 30, 30,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test public void RelentlessBobaPrintedMetadataMatchesSource() {
        var scn = scenario(); var b = scn.GetDSCard("boba").getBlueprint();
        assertEquals("Boba Fett, Relentless Bounty Hunter", b.getTitle()); assertFalse(b.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness()); assertEquals(Side.DARK, b.getSide());
        assertEquals(1, b.getDestiny(), scn.epsilon); assertEquals(4, b.getDeployCost(), scn.epsilon);
        assertEquals(4, b.getPower(), scn.epsilon); assertEquals(3, b.getAbility(), scn.epsilon);
        assertEquals(5, b.getArmor(), scn.epsilon); assertEquals(6, b.getForfeit(), scn.epsilon);
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, b.getExpansionSet()); assertEquals(Rarity.V, b.getRarity());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>() {{ add(CardType.ALIEN); }});
        scn.BlueprintKeywordCheck(b, new ArrayList<>() {{ add(Keyword.BOUNTY_HUNTER); }});
        scn.BlueprintIconCheck(b, new ArrayList<>() {{ add(Icon.ALIEN); add(Icon.PILOT); add(Icon.WARRIOR); add(Icon.JABBAS_PALACE); add(Icon.LEGACY_BLOCK_6); }});
        scn.BlueprintPersonaCheck(b, new ArrayList<>() {{ add(Persona.BOBA_FETT); }});
    }

    @Test public void RelentlessBobaAddsExactlyThreeToPilotedVehicleAndShipNotAsPassenger() {
        var scn = scenario(); scn.StartGame(); var boba = scn.GetDSCard("boba"); var scout = scn.GetDSCard("scout");
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scout); scn.BoardAsPilot(scout, boba);
        assertEquals(6, scn.GetPower(scout)); scn.MoveCardsToDSHand(boba); scn.BoardAsPassenger(scout, boba);
        assertEquals(3, scn.GetPower(scout)); scn.MoveCardsToDSHand(boba);
        scn.MoveLocationToTable(scn.GetDSCard("hoth")); scn.MoveCardsToLocation(scn.GetDSCard("hoth"), scn.GetDSCard("ship"));
        scn.BoardAsPilot(scn.GetDSCard("ship"), boba); assertEquals(5, scn.GetPower(scn.GetDSCard("ship")));
    }

    @Test public void RelentlessBobaHasPrintedFlyingLandspeedThree() {
        var scn = scenario(); scn.StartGame(); scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("boba"));
        assertEquals(3, scn.GetLandspeed(scn.GetDSCard("boba")));
    }

    private VirtualTableScenario battle(String weapon, boolean naturalAbility) {
        var scn = scenario(); scn.StartGame(); var site = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetDSCard("boba"), scn.GetLSCard("obiwan"));
        if (weapon != null) scn.AttachCardsTo(scn.GetDSCard("boba"), scn.GetDSCard(weapon));
        if (naturalAbility) scn.MoveCardsToLocation(site, scn.GetDSCard("vader"));
        scn.DSStartBattleAndSkipToWeaponsSegment(site); return scn;
    }

    @Test public void RelentlessBobaArmedBlasterOffersEitherDestinyButNotExtraBattleDestiny() {
        var scn = battle("blaster", false); var boba = scn.GetDSCard("boba");
        assertTrue(scn.DSCardActionAvailable(boba, "Add one destiny to total power"));
        assertTrue(scn.DSCardActionAvailable(boba, "Add one destiny to attrition"));
        assertEquals(0, scn.GetDSBattleDestinyCount());
    }

    @Test public void RelentlessBobaUnarmedOrNonBlasterCannotAddEitherDestiny() {
        for (String weapon : new String[] {null, "sword"}) {
            var scn = battle(weapon, false); var boba = scn.GetDSCard("boba");
            assertFalse(scn.DSCardActionAvailable(boba, "Add one destiny to total power"));
            assertFalse(scn.DSCardActionAvailable(boba, "Add one destiny to attrition"));
        }
    }

    @Test public void RelentlessBobaPowerChoiceConsumesBothAlternativesAndActuallyAddsOneDestiny() {
        var scn = battle("blaster", false); var boba = scn.GetDSCard("boba");
        scn.PrepareDSDestiny(5); int reserve = scn.GetDSReserveDeckCount();
        scn.DSUseCardAction(boba, "Add one destiny to total power"); scn.PassAllResponses();
        if (scn.AwaitingLSWeaponsSegmentActions()) scn.LSPass();
        assertFalse(scn.DSCardActionAvailable(boba, "Add one destiny to total power"));
        assertFalse(scn.DSCardActionAvailable(boba, "Add one destiny to attrition"));
        scn.PassWeaponsSegmentActions(); scn.PassDestinyDrawResponses(); scn.SkipBattleDestinyDraws(false);
        assertEquals(1, scn.gameState().getBattleState().getNumDestinyToTotalPowerDrawn(scn.DS));
        assertEquals(9, scn.GetDSTotalPower()); assertEquals(reserve - 1, scn.GetDSReserveDeckCount());
    }

    @Test public void RelentlessBobaAttritionChoiceActuallyDrawsOneAndDoesNotAlsoAddPower() {
        var scn = battle("blaster", true); var boba = scn.GetDSCard("boba");
        scn.PrepareDSDestiny(5); scn.PrepareDSDestiny(1);
        scn.DSUseCardAction(boba, "Add one destiny to attrition"); scn.PassAllResponses();
        if (scn.AwaitingLSWeaponsSegmentActions()) scn.LSPass();
        assertFalse(scn.DSCardActionAvailable(boba, "Add one destiny to total power"));
        assertFalse(scn.DSCardActionAvailable(boba, "Add one destiny to attrition"));
        scn.PassWeaponsSegmentActions(); scn.SkipBattleDestinyDraws(true); scn.PassDestinyDrawResponses();
        assertEquals(1, scn.gameState().getBattleState().getNumDestinyToAttritionDrawn(scn.DS));
        assertEquals(0, scn.gameState().getBattleState().getNumDestinyToTotalPowerDrawn(scn.DS));
        assertEquals(6, scn.gameState().getBattleState().getBaseAttrition(scn.LS), scn.epsilon);
    }

    private VirtualTableScenario escort(boolean captive, int force, Phase phase) {
        var scn = scenario(); scn.StartGame(); var site = scn.GetDSStartingLocation();
        scn.MoveLocationToTable(scn.GetDSCard("bay")); scn.MoveLocationToTable(scn.GetDSCard("otherBay"));
        scn.MoveCardsToLocation(site, scn.GetDSCard("boba"), scn.GetLSCard("han"));
        if (captive) scn.CaptureCardWith(scn.GetDSCard("boba"), scn.GetLSCard("han"));
        scn.SkipToDSTurn(phase); scn.EnsureDSForcePile(force); scn.DrainDSForcePileTo(force); return scn;
    }

    @Test public void RelentlessBobaPaysTwoToRelocateAcrossSystemsWithCaptiveAndCanRepeat() {
        var scn = escort(true, 4, Phase.MOVE); var boba = scn.GetDSCard("boba"); var han = scn.GetLSCard("han");
        assertEquals(Zone.CAPTIVE, han.getZone()); assertSame(boba, han.getAttachedTo());
        scn.DSUseCardAction(boba, "Relocate with captive to docking bay");
        if (scn.DSHasCardChoiceAvailable(scn.GetDSCard("bay"))) scn.DSChooseCard(scn.GetDSCard("bay")); scn.PassAllResponses();
        assertEquals(scn.GetDSCard("bay"), boba.getAtLocation()); assertEquals(2, scn.GetDSForcePileCount());
        assertSame(boba, han.getAttachedTo()); assertEquals(Zone.CAPTIVE, han.getZone());
        if (scn.LSDecisionAvailable("move phase")) scn.LSPass();
        assertTrue(scn.DSCardActionAvailable(boba, "Relocate with captive to docking bay"));
        scn.DSUseCardAction(boba, "Relocate with captive to docking bay");
        if (scn.DSHasCardChoiceAvailable(scn.GetDSCard("otherBay"))) scn.DSChooseCard(scn.GetDSCard("otherBay")); scn.PassAllResponses();
        assertEquals(scn.GetDSCard("otherBay"), boba.getAtLocation()); assertEquals(0, scn.GetDSForcePileCount());
        assertSame(boba, han.getAttachedTo());
    }

    @Test public void RelentlessBobaCannotRelocateWithoutCaptive() {
        var scn = escort(false, 4, Phase.MOVE);
        assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("boba"), "Relocate with captive to docking bay"));
    }

    @Test public void RelentlessBobaCannotRelocateWithoutTwoForceOrOutsideOwnMovePhase() {
        var scn = escort(true, 1, Phase.MOVE);
        assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("boba"), "Relocate with captive to docking bay"));
        scn = escort(true, 4, Phase.DEPLOY);
        assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("boba"), "Relocate with captive to docking bay"));
    }

    private void attrition(int destiny, boolean immune) {
        var scn = battle(null, false); var boba = scn.GetDSCard("boba");
        scn.PrepareLSDestiny(destiny); scn.SkipToDamageSegment(true); scn.PassDamageSegmentActions();
        scn.DSPayRemainingBattleDamageFromReserveDeck(); scn.PassAllResponses();
        assertEquals(immune ? "0" : "1", scn.GetADParam(scn.DS, "min")[0]);
        if (immune) { scn.DSPass(); scn.PassAllResponses(); assertEquals(Zone.AT_LOCATION, boba.getZone()); }
        else { scn.DSPayAttritionFromCardInPlay(boba); scn.PassAllResponses();
            com.gempukku.swccgo.framework.Assertions.assertInZone(Zone.LOST_PILE, boba); }
    }

    @Test public void RelentlessBobaSurvivesTwoAttritionButMustSatisfyThree() {
        attrition(2, true); attrition(3, false);
    }
}
