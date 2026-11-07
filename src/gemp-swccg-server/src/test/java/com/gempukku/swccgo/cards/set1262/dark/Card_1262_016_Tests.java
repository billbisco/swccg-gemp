package com.gempukku.swccgo.cards.set1262.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.*;

public class Card_1262_016_Tests {
    private VirtualTableScenario scenario() {
        var scn = new VirtualTableScenario(new HashMap<>(Map.of("luke", "1_19")),
                new HashMap<>(Map.ofEntries(Map.entry("mount", "1262_16"), Map.entry("mount2", "1262_16"),
                        Map.entry("witch", "1262_2"), Map.entry("otherwitch", "1262_2"),
                        Map.entry("trooper", "1_194"), Map.entry("wampa", "3_93"), Map.entry("cave", "3_150"),
                        Map.entry("town", "1_295"))),
                20, 20, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        scn.StartGame();
        return scn;
    }

    @Test public void RancorMountStatsIconsAndKeywordsMatchPrintedCard() {
        var scn = scenario();
        var b = scn.GetDSCard("mount").getBlueprint();
        assertEquals("Rancor Mount", b.getTitle());
        assertEquals("Indigenous to Dathomir, but found on several dozen worlds throughout the galaxy. Vicious predator. Sometimes kept as pets by eccentrics and crime lords.", b.getLore());
        assertEquals(Side.DARK, b.getSide());
        assertEquals(Uniqueness.UNRESTRICTED, b.getUniqueness());
        assertEquals(CardCategory.VEHICLE, b.getCardCategory());
        assertEquals(CardSubtype.CREATURE, b.getCardSubtype());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_2, b.getExpansionSet());
        assertEquals(Rarity.V, b.getRarity());
        assertTrue(b.isLegacy());
        assertFalse(b.hasVirtualSuffix());
        assertEquals(2, b.getDestiny(), scn.epsilon);
        assertEquals(3, b.getDeployCost(), scn.epsilon);
        assertEquals(3, b.getPower(), scn.epsilon);
        assertEquals(5, b.getArmor(), scn.epsilon);
        assertEquals(2, b.getLandspeed(), scn.epsilon);
        assertEquals(5, b.getForfeit(), scn.epsilon);
        assertEquals(1, b.getPassengerCapacity());
        assertEquals(0, b.getPilotCapacity());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(List.of(CardType.VEHICLE)));
        scn.BlueprintIconCheck(b, new ArrayList<>(List.of(Icon.VEHICLE, Icon.JABBAS_PALACE, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>());
    }

    @Test public void RancorMountActuallyBoardsOneWitchButNotTrooperAndOnlyRidingRaisesPower() {
        var scn = scenario();
        var site = scn.GetDSStartingLocation();
        var mount = scn.GetDSCard("mount");
        var witch = scn.GetDSCard("witch");
        var other = scn.GetDSCard("otherwitch");
        var trooper = scn.GetDSCard("trooper");
        scn.MoveCardsToLocation(site, mount, witch, other, trooper);
        scn.SkipToDSTurn(Phase.MOVE);
        assertEquals(3, scn.GetPower(mount));
        assertFalse(scn.DSCardActionAvailable(trooper, "Embark"));
        assertTrue(scn.DSCardActionAvailable(witch, "Embark"));
        scn.DSUseCardAction(witch, "Embark");
        if (scn.DSHasCardChoiceAvailable(mount)) scn.DSChooseCard(mount);
        scn.PassAllResponses();
        assertEquals(List.of(witch), scn.gameState().getPassengerCardsAboard(mount));
        assertEquals(5, scn.GetPower(mount));
        if (scn.AwaitingLSMovePhaseActions()) scn.LSPass();
        assertTrue(scn.AwaitingDSMovePhaseActions());
        assertFalse(scn.DSCardActionAvailable(other, "Embark"));
        assertTrue(scn.DSCardActionAvailable(witch, "Disembark"));
        scn.DSUseCardAction(witch, "Disembark");
        if (scn.DSHasCardChoiceAvailable(site)) scn.DSChooseCard(site);
        scn.PassAllResponses();
        assertEquals(site, witch.getAtLocation());
        assertNull(witch.getAttachedTo());
        assertEquals(3, scn.GetPower(mount));
    }

    @Test public void RancorMountHalfAbilityNeedsTwoMountsToControlAnEmptySite() {
        var scn = scenario();
        var site = scn.GetDSStartingLocation();
        var mount = scn.GetDSCard("mount");
        scn.MoveCardsToLocation(site, mount);
        var querying = scn.game().getModifiersQuerying();
        assertEquals(0.5f, querying.getAbility(scn.gameState(), mount), scn.epsilon);
        assertEquals(0.5f, querying.getTotalAbilityPresentAtLocation(scn.gameState(), scn.DS, site), scn.epsilon);
        scn.MoveCardsToLocation(site, scn.GetDSCard("mount2"));
        assertEquals(1f, querying.getTotalAbilityPresentAtLocation(scn.gameState(), scn.DS, site), scn.epsilon);
        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.DSForceDrainAvailable(site));
        scn.DSForceDrainAt(site);
        scn.PassForceDrainStartResponses();
        assertEquals(1, scn.GetForceDrainTotal());
    }

    private void forceDrain(boolean riderHere, boolean mountHere, int expected) {
        var scn = scenario();
        var site = scn.GetDSStartingLocation();
        var mount = scn.GetDSCard("mount");
        var witch = scn.GetDSCard("witch");
        var distant = scn.GetDSCard("town");
        scn.MoveLocationToTable(distant);
        scn.MoveCardsToLocation(mountHere ? site : distant, mount);
        if (riderHere) {
            scn.MoveCardsToDSHand(witch);
            scn.BoardAsPassenger(mount, witch);
        } else {
            scn.MoveCardsToLocation(site, witch);
        }
        if (!mountHere) scn.MoveCardsToLocation(site, scn.GetDSCard("trooper"));
        scn.SkipToDSTurn(Phase.CONTROL);
        int before = scn.GetLSLifeForceRemaining();
        scn.DSForceDrainAt(site);
        scn.PassForceDrainStartResponses();
        assertEquals(expected, scn.GetForceDrainTotal());
        scn.LSPayRemainingForceLossFromReserveDeck();
        scn.PassAllResponses();
        assertEquals(before - expected, scn.GetLSLifeForceRemaining());
    }

    @Test public void RancorMountRiddenRaisesActualOwnDrainHereByOne() { forceDrain(true, true, 2); }
    @Test public void RancorMountUnmountedWithWitchAtSiteDoesNotRaiseDrain() { forceDrain(false, true, 1); }
    @Test public void RancorMountRiddenAtAnotherSiteDoesNotRaiseDrainHere() { forceDrain(true, false, 1); }

    @Test public void RancorMountRiderDoesNotBoostOpponentsDrainHere() {
        var scn = scenario();
        var site = scn.GetDSStartingLocation();
        var mount = scn.GetDSCard("mount");
        mount.setOwner(scn.LS);
        scn.MoveCardsToLocation(site, mount, scn.GetDSCard("trooper"));
        var witch = scn.GetDSCard("witch");
        witch.setOwner(scn.LS);
        scn.MoveCardsToLSHand(witch);
        scn.BoardAsPassenger(mount, witch);
        assertEquals(5, scn.GetPower(mount));
        assertEquals(1f, scn.game().getModifiersQuerying().getForceDrainAmount(scn.gameState(), site, scn.DS), scn.epsilon);
        assertEquals(2f, scn.game().getModifiersQuerying().getForceDrainAmount(scn.gameState(), site, scn.LS), scn.epsilon);
    }

    private VirtualTableScenario attack(boolean rider) {
        var scn = scenario();
        var site = scn.GetDSCard("cave");
        scn.MoveLocationToTable(site);
        var mount = scn.GetDSCard("mount");
        scn.MoveCardsToLocation(site, mount, scn.GetDSCard("wampa"));
        if (rider) {
            var witch = scn.GetDSCard("witch");
            scn.MoveCardsToDSHand(witch);
            scn.BoardAsPassenger(mount, witch);
        }
        scn.SkipToPhase(Phase.BATTLE);
        assertTrue(scn.AwaitingDSBattlePhaseActions());
        return scn;
    }

    @Test public void RancorMountCannotBeAttackedWhileRiddenByDathomirWitch() {
        var scn = attack(true);
        assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("wampa"), "Initiate attack"));
    }

    @Test public void RancorMountCanBeTargetedByActualCreatureAttackWhileUnmounted() {
        var scn = attack(false);
        var wampa = scn.GetDSCard("wampa");
        var mount = scn.GetDSCard("mount");
        assertTrue(scn.DSCardActionAvailable(wampa, "Initiate attack"));
        scn.DSUseCardAction(wampa, "Initiate attack");
        scn.PassResponses("ATTACK_TARGET_SELECTED");
        scn.PassResponses("ATTACK_INITIATED");
        assertTrue(scn.gameState().isParticipatingInAttack(mount));
    }

    private VirtualTableScenario attrition(int destiny, boolean ridden) {
        var scn = scenario();
        var site = scn.GetDSStartingLocation();
        var mount = scn.GetDSCard("mount");
        var witch = scn.GetDSCard("witch");
        scn.MoveCardsToLocation(site, mount, scn.GetLSCard("luke"));
        if (ridden) {
            scn.MoveCardsToDSHand(witch);
            scn.BoardAsPassenger(mount, witch);
        } else {
            scn.MoveCardsToLocation(site, witch);
        }
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.PrepareDSDestiny(7);
        scn.PrepareLSDestiny(destiny);
        scn.DSInitiateBattle(site);
        scn.SkipToDamageSegment(true);
        scn.PassDamageSegmentActions();
        assertEquals(0, scn.GetUnpaidDSBattleDamage());
        assertEquals(destiny, scn.GetUnpaidDSAttrition());
        return scn;
    }

    @Test public void RancorMountAndRiderBothSurviveActualAttritionThree() {
        var scn = attrition(3, true);
        assertTrue(scn.DSDecisionAvailable("Choose a card from battle to forfeit (if desired)"));
        scn.DSPass();
        assertInZone(Zone.AT_LOCATION, scn.GetDSCard("mount"));
        assertEquals(scn.GetDSCard("mount"), scn.GetDSCard("witch").getAttachedTo());
    }

    @Test public void RancorMountAndRiderCanEachSatisfyAttritionExactlyFour() {
        for (String card : List.of("mount", "witch")) {
            var scn = attrition(4, true);
            assertFalse(scn.DSDecisionAvailable("Choose a card from battle to forfeit (if desired)"));
            var forfeited = scn.GetDSCard(card);
            assertTrue(scn.DSHasCardChoiceAvailable(forfeited));
            scn.DSChooseCard(forfeited);
            scn.PassAllResponses();
            if (scn.DSDecisionAvailable("Choose card to put on Lost Pile")) {
                assertTrue(scn.DSHasCardChoiceAvailable(forfeited));
                scn.DSChooseCard(forfeited);
                scn.PassAllResponses();
            }
            assertInZone(Zone.LOST_PILE, forfeited);
        }
    }

    @Test public void RancorMountAndRiderHaveNoImmunityAboveFour() {
        var scn = attrition(5, true);
        assertFalse(scn.DSDecisionAvailable("Choose a card from battle to forfeit (if desired)"));
        assertTrue(scn.DSHasCardChoiceAvailable(scn.GetDSCard("mount")));
        assertTrue(scn.DSHasCardChoiceAvailable(scn.GetDSCard("witch")));
    }

    @Test public void RancorMountAndWitchAtSameSiteWithoutRiderHaveNoAttritionImmunity() {
        var scn = attrition(3, false);
        assertFalse(scn.DSDecisionAvailable("Choose a card from battle to forfeit (if desired)"));
        assertTrue(scn.DSHasCardChoiceAvailable(scn.GetDSCard("mount")));
        assertTrue(scn.DSHasCardChoiceAvailable(scn.GetDSCard("witch")));
    }
}
