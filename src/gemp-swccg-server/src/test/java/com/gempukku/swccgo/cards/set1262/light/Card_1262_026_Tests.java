package com.gempukku.swccgo.cards.set1262.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import static com.gempukku.swccgo.framework.Assertions.*;
import static org.junit.Assert.*;

public class Card_1262_026_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("kaadu", "1262_26"); put("rider", "1262_20"); put("gungan", "12_7"); put("trooper", "1_28");
            put("jungle", "1_137"); put("swamp", "4_87");
        }}, new HashMap<>() {{ put("vader", "1_168"); put("snake", "4_109"); }}, 50, 50,
                StartingSetup.LSStartingLocation("1_136"), StartingSetup.DSStartingLocation("12_176"),
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test public void KaaduStatsMatchFinalLegacyInsteadOfOriginalKaadu() {
        var scn = scenario(); var b = scn.GetLSCard("kaadu").getBlueprint();
        assertEquals("Kaadu", b.getTitle()); assertTrue(b.hasVirtualSuffix()); assertEquals(Side.LIGHT, b.getSide());
        assertEquals(Uniqueness.UNRESTRICTED, b.getUniqueness()); assertEquals(CardSubtype.CREATURE, b.getCardSubtype());
        assertEquals(4, b.getDestiny(), scn.epsilon); assertEquals(1, b.getDeployCost(), scn.epsilon);
        assertEquals(1, b.getPower(), scn.epsilon); assertEquals(2, b.getManeuver(), scn.epsilon);
        assertEquals(4, b.getLandspeed(), scn.epsilon); assertEquals(2, b.getForfeit(), scn.epsilon);
        assertEquals(0, b.getPilotCapacity()); assertEquals(1, b.getPassengerCapacity());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_2, b.getExpansionSet()); assertEquals(Rarity.V, b.getRarity());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(Arrays.asList(CardType.VEHICLE)));
        scn.BlueprintIconCheck(b, new ArrayList<>(Arrays.asList(Icon.VEHICLE, Icon.THEED_PALACE, Icon.EPISODE_I, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>(Arrays.asList(Keyword.KAADU)));
        scn.BlueprintModelTypeCheck(b, new ArrayList<>());
    }

    private VirtualTableScenario start(String passenger) {
        var scn = scenario(); scn.StartGame(); scn.MoveLocationToTable(scn.GetLSCard("jungle"));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("kaadu"));
        if (passenger != null) scn.BoardAsPassenger(scn.GetLSCard("kaadu"), scn.GetLSCard(passenger));
        return scn;
    }

    @Test public void KaaduHasHalfAbilityEvenWithoutRider() {
        var scn = start(null);
        assertEquals(0.5, scn.game().getModifiersQuerying().getAbility(scn.gameState(), scn.GetLSCard("kaadu")), scn.epsilon);
    }

    @Test public void KaaduMayDeployExactlyOneActualRiderAsPassenger() {
        var scn = start(null); var rider = scn.GetLSCard("rider"); var kaadu = scn.GetLSCard("kaadu");
        scn.MoveCardsToLSHand(rider); scn.SkipToLSTurn(Phase.DEPLOY); scn.LSActivateForceCheat(5);
        int force = scn.GetLSForcePileCount(); scn.LSDeployCard(rider); scn.LSChooseCard(kaadu); scn.PassAllResponses();
        assertEquals(kaadu, rider.getAttachedTo()); assertTrue(Filters.aboardAsPassenger(kaadu).accepts(scn.game(), rider));
        assertEquals(force - 3, scn.GetLSForcePileCount());
        scn.MoveCardsToLSHand(scn.GetLSCard("trooper")); scn.DSPass(); scn.LSDeployCard(scn.GetLSCard("trooper"));
        assertFalse(scn.LSHasCardChoiceAvailable(kaadu));
    }

    @Test public void KaaduRiderBoostsOnlyItsMountAndBothLoseImmunityWhenRiderLeaves() {
        var scn = start("rider"); var kaadu = scn.GetLSCard("kaadu"); var rider = scn.GetLSCard("rider");
        assertEquals(3, scn.GetPower(kaadu)); assertEquals(4, scn.GetManeuver(kaadu)); assertEquals(2, scn.GetPower(rider));
        assertEquals(4, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), kaadu), scn.epsilon);
        assertEquals(4, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), rider), scn.epsilon);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), rider);
        assertEquals(1, scn.GetPower(kaadu)); assertEquals(2, scn.GetManeuver(kaadu));
        assertEquals(0, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), kaadu), scn.epsilon);
        assertEquals(0, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), rider), scn.epsilon);
    }

    @Test public void KaaduGenericGunganRiderDoesNotGiveLegacyBeastRiderBonuses() {
        var scn = start("gungan"); var kaadu = scn.GetLSCard("kaadu");
        assertEquals(1, scn.GetPower(kaadu)); assertEquals(2, scn.GetManeuver(kaadu));
        assertEquals(0, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), kaadu), scn.epsilon);
    }

    private void drain(boolean ridden, int expected) {
        var scn = start(ridden ? "rider" : null);
        if (!ridden) scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("rider"));
        scn.SkipToLSTurn(Phase.CONTROL); int lost = scn.GetDSLostPileCount();
        scn.LSForceDrainAt(scn.GetLSStartingLocation()); scn.PassForceDrainStartResponses();
        scn.DSPayRemainingForceLossFromReserveDeck(); scn.PassAllResponses(); assertEquals(lost + expected, scn.GetDSLostPileCount());
    }
    @Test public void KaaduBeastRiderAddsOneToActualDrainWithoutSwampRestriction() { drain(true, 2); }
    @Test public void KaaduBeastRiderOnFootAtSameSiteDoesNotAddDrain() { drain(false, 1); }

    @Test public void KaaduMovementStillCostsForceInsteadOfOriginalFreeMove() {
        var scn = start("rider"); scn.SkipToLSTurn(Phase.MOVE); scn.LSActivateForceCheat(3);
        int force = scn.GetLSForcePileCount(); scn.LSMoveCard(scn.GetLSCard("kaadu"), scn.GetLSCard("jungle")); scn.PassAllResponses();
        assertAtLocation(scn.GetLSCard("jungle"), scn.GetLSCard("kaadu")); assertEquals(force - 1, scn.GetLSForcePileCount());
    }

    @Test public void KaaduDoesNotInheritOriginalGunganReact() {
        var scn = start("gungan"); var jungle = scn.GetLSCard("jungle");
        scn.MoveCardsToLocation(jungle, scn.GetLSCard("trooper"), scn.GetDSCard("vader"));
        scn.SkipToDSTurn(Phase.BATTLE); scn.LSActivateForceCheat(5); scn.DSInitiateBattle(jungle);
        assertNull(scn.LSGetDecision()); assertNotNull(scn.DSGetDecision());
        assertAtLocation(scn.GetLSStartingLocation(), scn.GetLSCard("kaadu"));
    }

    private VirtualTableScenario attack(boolean ridden) {
        var scn = scenario(); scn.StartGame(); var swamp = scn.GetLSCard("swamp"); scn.MoveLocationToTable(swamp);
        scn.MoveCardsToLocation(swamp, scn.GetLSCard("kaadu"));
        if (ridden) scn.BoardAsPassenger(scn.GetLSCard("kaadu"), scn.GetLSCard("rider"));
        else scn.MoveCardsToLocation(swamp, scn.GetLSCard("rider"));
        scn.SkipToDSTurn(Phase.BATTLE); scn.MoveCardsToLocation(swamp, scn.GetDSCard("snake"));
        return scn;
    }
    @Test public void KaaduBeastRiderPreventsActualCreatureAttackAction() {
        var scn = attack(true); assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("snake"), "Initiate attack"));
        assertAtLocation(scn.GetLSCard("swamp"), scn.GetLSCard("kaadu"));
    }
    @Test public void KaaduWithoutMountedBeastRiderCanActuallyBeEaten() {
        var scn = attack(false); scn.PrepareDSDestiny(4); scn.DSUseCardAction(scn.GetDSCard("snake"), "Initiate attack");
        scn.PassAllResponses(); scn.PassWeaponsSegmentActions(); scn.PassAllResponses(); scn.PassDestinyDrawResponses(); scn.PassAllResponses();
        assertInZone(Zone.LOST_PILE, scn.GetLSCard("kaadu")); assertAtLocation(scn.GetLSCard("swamp"), scn.GetLSCard("rider"));
    }

    private void attrition(int destiny, boolean immune, boolean forfeitRider) {
        var scn = start("rider"); var site = scn.GetLSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetDSCard("vader")); scn.SkipToLSTurn(Phase.BATTLE);
        scn.PrepareLSDestiny(0); scn.PrepareDSDestiny(destiny - 1); scn.LSInitiateBattle(site);
        scn.PassAllResponses(); scn.SkipToDamageSegment(true); scn.PassDamageSegmentActions();
        assertEquals(destiny, scn.GetUnpaidLSAttrition());
        if (scn.AwaitingLSBattleDamagePayment()) scn.LSPayRemainingBattleDamageFromReserveDeck();
        assertEquals(immune, scn.LSDecisionAvailable("(if desired)"));
        if (immune) {
            scn.LSChooseCards(); assertAtLocation(site, scn.GetLSCard("kaadu"));
            assertEquals(scn.GetLSCard("kaadu"), scn.GetLSCard("rider").getAttachedTo());
        } else {
            var target = scn.GetLSCard(forfeitRider ? "rider" : "kaadu"); scn.LSPayAttritionFromCardInPlay(target);
            assertInZone(Zone.LOST_PILE, target);
        }
    }
    @Test public void KaaduAndMountedRiderBothSurviveActualAttritionThree() { attrition(3, true, false); }
    @Test public void KaaduMayBeForfeitedAtActualAttritionFour() { attrition(4, false, false); }
    @Test public void KaaduMountedRiderMayBeForfeitedAtActualAttritionFour() { attrition(4, false, true); }
}
