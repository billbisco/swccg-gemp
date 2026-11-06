package com.gempukku.swccgo.cards.set1262.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import static com.gempukku.swccgo.framework.Assertions.*;
import static org.junit.Assert.*;

public class Card_1262_024_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("security", "1262_24"); put("trooper", "1_28"); put("luke", "1_19"); put("obi", "1_21");
            put("spy", "1_20"); put("naboo", "13_31"); put("core", "13_32"); put("beggar", "1_44");
        }}, new HashMap<>() {{
            put("vader", "1_168"); put("trooper1", "1_194"); put("trooper2", "1_194"); put("trooper3", "1_194");
            put("derdram", "4_98"); put("ephant", "6_101"); put("alter", "1_234"); put("hypo", "2_112");
        }}, 50, 50, StartingSetup.LSStartingLocation("1_136"), StartingSetup.DSStartingLocation("12_176"),
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test public void SecurityBreachStatsMatchFinalMaster() {
        var scn = scenario(); var b = scn.GetLSCard("security").getBlueprint();
        assertEquals("Security Breach", b.getTitle()); assertEquals(Side.LIGHT, b.getSide());
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness()); assertFalse(b.hasVirtualSuffix());
        assertEquals(3, b.getDestiny(), scn.epsilon); assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_2, b.getExpansionSet());
        assertEquals(Rarity.V, b.getRarity());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(Arrays.asList(CardType.EFFECT)));
        scn.BlueprintIconCheck(b, new ArrayList<>(Arrays.asList(Icon.EFFECT, Icon.A_NEW_HOPE, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>());
    }

    private VirtualTableScenario deployed() {
        var scn = scenario(); scn.StartGame(); scn.MoveCardsToLSHand(scn.GetLSCard("security"));
        scn.SkipToLSTurn(Phase.DEPLOY); int force = scn.GetLSForcePileCount();
        scn.LSDeployCard(scn.GetLSCard("security")); scn.PassAllResponses();
        assertInZone(Zone.SIDE_OF_TABLE, scn.GetLSCard("security"));
        assertEquals(force, scn.GetLSForcePileCount()); return scn;
    }
    @Test public void SecurityBreachDeploysOnTableWithoutForceCost() { deployed(); }

    @Test public void SecurityBreachCancelsBothSidesOfReflectionsThreeNabooSiteAndRestoresOnRemoval() {
        var scn = scenario(); scn.StartGame(); var site = scn.GetLSCard("naboo"); scn.MoveLocationToTable(site);
        scn.MoveCardsToLocation(site, scn.GetLSCard("obi"), scn.GetDSCard("vader"));
        scn.MoveCardsToLSHand(scn.GetLSCard("security")); scn.SkipToLSTurn(Phase.DEPLOY);
        assertEquals(7, scn.GetPower(scn.GetLSCard("obi"))); assertEquals(7, scn.GetPower(scn.GetDSCard("vader")));
        scn.LSDeployCard(scn.GetLSCard("security")); scn.PassAllResponses();
        assertEquals(5, scn.GetPower(scn.GetLSCard("obi"))); assertEquals(6, scn.GetPower(scn.GetDSCard("vader")));
        scn.MoveCardsToLSHand(scn.GetLSCard("security"));
        assertEquals(7, scn.GetPower(scn.GetLSCard("obi"))); assertEquals(7, scn.GetPower(scn.GetDSCard("vader")));
    }

    @Test public void SecurityBreachCancelsReflectionsThreeNabooDrainPenaltyButNotOtherSiteText() {
        var scn = deployed(); var site = scn.GetLSCard("naboo"); scn.MoveLocationToTable(site);
        scn.MoveCardsToLocation(site, scn.GetDSCard("vader")); scn.SkipToDSTurn(Phase.CONTROL);
        int lost = scn.GetLSLostPileCount(); scn.DSForceDrainAt(site); scn.PassForceDrainStartResponses();
        scn.LSPayRemainingForceLossFromReserveDeck(); scn.PassAllResponses(); assertEquals(lost + 2, scn.GetLSLostPileCount());
        scn.MoveCardsToLSHand(scn.GetLSCard("security")); scn.SkipToLSTurn(Phase.DRAW); scn.SkipToDSTurn(Phase.CONTROL);
        lost = scn.GetLSLostPileCount(); scn.DSForceDrainAt(site); scn.PassForceDrainStartResponses();
        scn.LSPayRemainingForceLossFromReserveDeck(); scn.PassAllResponses(); assertEquals(lost + 1, scn.GetLSLostPileCount());
    }

    private void derdram(int others, boolean cancelled) {
        var scn = deployed(); var site = scn.GetDSStartingLocation(); scn.MoveCardsToLocation(site, scn.GetDSCard("derdram"));
        for (int i = 1; i <= others; i++) scn.MoveCardsToLocation(site, scn.GetDSCard("trooper" + i));
        scn.SkipToDSTurn(Phase.CONTROL); scn.DSActivateForceCheat(4);
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSCard("hypo"));
        scn.SkipToPhase(Phase.DEPLOY);
        assertEquals(cancelled, scn.game().getModifiersQuerying().isGameTextCanceled(scn.gameState(), scn.GetDSCard("derdram")));
        assertEquals(!cancelled, scn.DSCardActionAvailable(scn.GetDSCard("derdram"), "Take card into hand"));
        if (!cancelled) {
            int force = scn.GetDSForcePileCount(); scn.DSUseCardAction(scn.GetDSCard("derdram"), "Take card into hand");
            scn.PassAllResponses(); scn.DSChooseCard(scn.GetDSCard("hypo")); scn.PassAllResponses();
            assertInHand(scn.GetDSCard("hypo")); assertEquals(force - 1, scn.GetDSForcePileCount());
        }
    }
    @Test public void SecurityBreachCancelsDerdramAlone() { derdram(0, true); }
    @Test public void SecurityBreachCancelsDerdramWithOnlyTwoOtherCharacters() { derdram(2, true); }
    @Test public void SecurityBreachPreservesDerdramWithExactlyThreeOtherCharacters() { derdram(3, false); }

    private void ephant(int others, boolean blocksSpy) {
        var scn = deployed(); var site = scn.GetDSStartingLocation(); scn.MoveCardsToLocation(site, scn.GetDSCard("ephant"));
        for (int i = 1; i <= others; i++) scn.MoveCardsToLocation(site, scn.GetDSCard("trooper" + i));
        scn.MoveLocationToTable(scn.GetLSCard("naboo"));
        scn.MoveCardsToLSHand(scn.GetLSCard("spy")); scn.SkipToDSTurn(Phase.DRAW); scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSActivateForceCheat(5); scn.LSDeployCard(scn.GetLSCard("spy"));
        assertEquals(!blocksSpy, scn.LSHasCardChoiceAvailable(site));
        assertTrue(scn.LSHasCardChoiceAvailable(scn.GetLSStartingLocation()));
        if (!blocksSpy) { scn.LSChooseCard(site); scn.PassAllResponses(); assertAtLocation(site, scn.GetLSCard("spy")); }
    }
    @Test public void SecurityBreachCancelsEphantWithTwoOthersAndAllowsActualSpyDeployment() { ephant(2, false); }
    @Test public void SecurityBreachPreservesEphantWithExactlyThreeOthersAndBlocksSpy() { ephant(3, true); }

    private VirtualTableScenario battle(boolean luke, boolean extra, boolean source) {
        var scn = deployed(); var site = scn.GetLSStartingLocation();
        if (!source) scn.MoveCardsToLSHand(scn.GetLSCard("security"));
        scn.MoveCardsToLocation(site, scn.GetLSCard(luke ? "luke" : "trooper"), scn.GetDSCard("vader"));
        if (extra) scn.MoveCardsToLocation(site, scn.GetDSCard("trooper1"), scn.GetDSCard("trooper2"));
        scn.SkipToPhase(Phase.BATTLE); scn.LSInitiateBattle(site); scn.PassAllResponses();
        scn.SkipToDamageSegment(false); scn.PassDamageSegmentActions(); return scn;
    }

    private void reduce(boolean luke, boolean extra, int original, int remaining) {
        var scn = battle(luke, extra, true); var effect = scn.GetLSCard("security");
        assertEquals(original, scn.GetUnpaidLSBattleDamage()); assertTrue(scn.LSCardActionAvailable(effect, "Place out of play"));
        int lost = scn.GetLSLostPileCount(); scn.LSUseCardAction(effect, "Place out of play"); scn.PassAllResponses();
        assertInZone(Zone.OUT_OF_PLAY, effect); assertEquals(lost, scn.GetLSLostPileCount());
        if (remaining > 0) {
            assertTrue(scn.AwaitingLSBattleDamagePayment()); assertEquals(remaining, scn.GetUnpaidLSBattleDamage());
            scn.LSPayRemainingBattleDamageFromReserveDeck(); scn.PassAllResponses();
            assertEquals(lost + remaining, scn.GetLSLostPileCount());
        } else assertFalse(scn.AwaitingLSBattleDamagePayment());
    }
    @Test public void SecurityBreachReducesFiveActualDamageToOne() { reduce(false, false, 5, 1); }
    @Test public void SecurityBreachReductionFloorsThreeDamageAtZero() { reduce(true, false, 3, 0); }
    @Test public void SecurityBreachReducesSevenActualDamageToThree() { reduce(false, true, 7, 3); }

    @Test public void SecurityBreachCannotUseReductionFromHand() {
        var scn = battle(false, false, false); assertTrue(scn.AwaitingLSBattleDamagePayment());
        assertEquals(5, scn.GetUnpaidLSBattleDamage()); assertInHand(scn.GetLSCard("security"));
    }
    @Test public void SecurityBreachReductionIsOptional() {
        var scn = battle(false, false, true); assertTrue(scn.LSCardActionAvailable(scn.GetLSCard("security"), "Place out of play"));
        scn.LSPass(); scn.PassAllResponses(); assertTrue(scn.AwaitingLSBattleDamagePayment());
        assertEquals(5, scn.GetUnpaidLSBattleDamage());
        for (int i = 0; i < 5; i++) {
            scn.LSPayBattleDamageFromReserveDeck(); scn.PassAllResponses();
        }
        assertInZone(Zone.SIDE_OF_TABLE, scn.GetLSCard("security"));
    }
    @Test public void SecurityBreachOncePerGameSurvivesLeavingPlayAndANewTurn() {
        var scn = battle(false, false, true); var effect = scn.GetLSCard("security");
        scn.LSUseCardAction(effect, "Place out of play"); scn.PassAllResponses();
        scn.LSPayRemainingBattleDamageFromReserveDeck(); scn.PassAllResponses();
        scn.MoveCardsToLSSideOfTable(effect); scn.SkipToDSTurn(Phase.ACTIVATE); scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(scn.GetLSStartingLocation()); scn.PassAllResponses(); scn.SkipToDamageSegment(false); scn.PassDamageSegmentActions();
        assertTrue(scn.AwaitingLSBattleDamagePayment()); assertEquals(5, scn.GetUnpaidLSBattleDamage());
        assertInZone(Zone.SIDE_OF_TABLE, effect);
    }
}
