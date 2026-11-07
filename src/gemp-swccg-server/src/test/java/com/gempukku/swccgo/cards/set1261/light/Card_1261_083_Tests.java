package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import static com.gempukku.swccgo.framework.Assertions.*;
import static org.junit.Assert.*;

public class Card_1261_083_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("dockyards", "1261_83"); put("cruiser", "9_79"); put("home", "9_74"); put("profundity", "207_18");
            put("corvette", "1_140"); put("luke", "1_19"); put("rebel", "1_28"); put("support", "9_1");
            put("ralltiir", "2_65"); put("moncal", "9_59"); put("yavin", "1_135"); put("warRoom", "9_58");
            put("ebo", "3_34"); put("generators", "3_61"); put("beggar", "1_44");
        }}, new HashMap<>() {{ put("devastator", "1_301"); put("vader", "1_168"); put("alter", "1_234"); }},
                60, 60, StartingSetup.LSStartingLocation("1_136"), StartingSetup.DSStartingLocation("12_176"),
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test public void MonCalamariDockyardsStatsMatchFinalLegacySource() {
        var scn = scenario(); var b = scn.GetLSCard("dockyards").getBlueprint();
        assertEquals("Mon Calamari Dockyards", b.getTitle()); assertFalse(b.hasVirtualSuffix());
        assertEquals(Side.LIGHT, b.getSide()); assertEquals(Uniqueness.UNIQUE, b.getUniqueness());
        assertEquals(3, b.getDestiny(), scn.epsilon); assertEquals(CardSubtype.NORMAL, b.getCardSubtype());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, b.getExpansionSet()); assertEquals(Rarity.V, b.getRarity());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(Arrays.asList(CardType.EFFECT)));
        scn.BlueprintIconCheck(b, new ArrayList<>(Arrays.asList(Icon.EFFECT, Icon.DEATH_STAR_II, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>());
    }

    private VirtualTableScenario started(boolean source) {
        var scn = scenario(); scn.StartGame();
        for (String key : new String[]{"ralltiir", "moncal", "yavin", "generators"}) scn.MoveLocationToTable(scn.GetLSCard(key));
        if (source) scn.MoveCardsToLSSideOfTable(scn.GetLSCard("dockyards"));
        else scn.MoveCardsToLSHand(scn.GetLSCard("dockyards"));
        return scn;
    }
    private void force(VirtualTableScenario scn, int amount) {
        while (scn.GetLSForcePileCount() > 0) scn.MoveCardsToTopOfLSReserveDeck(scn.GetTopOfLSForcePile());
        scn.LSActivateForceCheat(amount); assertEquals(amount, scn.GetLSForcePileCount());
    }

    @Test public void MonCalamariDockyardsActuallyDeploysOnTableForFree() {
        var scn = started(false); scn.SkipToLSTurn(Phase.CONTROL); force(scn, 0); scn.SkipToPhase(Phase.DEPLOY);
        scn.LSDeployCard(scn.GetLSCard("dockyards")); scn.PassAllResponses();
        assertInZone(Zone.SIDE_OF_TABLE, scn.GetLSCard("dockyards")); assertEquals(0, scn.GetLSForcePileCount());
    }
    @Test public void MonCalamariDockyardsDeploysRestrictedCruiserAtOtherSystemForExactlySix() {
        var scn = started(true); var ship = scn.GetLSCard("cruiser"); scn.MoveCardsToLSHand(ship);
        scn.SkipToLSTurn(Phase.CONTROL); force(scn, 6); scn.SkipToPhase(Phase.DEPLOY);
        scn.LSDeployCard(ship); assertTrue(scn.LSHasCardChoiceAvailable(scn.GetLSCard("ralltiir")));
        scn.LSChooseCard(scn.GetLSCard("ralltiir")); scn.PassAllResponses();
        assertAtLocation(scn.GetLSCard("ralltiir"), ship); assertEquals(0, scn.GetLSForcePileCount());
    }
    @Test public void MonCalamariDockyardsFiveForceCannotPaySixAtOtherSystem() {
        var scn = started(true); var ship = scn.GetLSCard("cruiser"); scn.MoveCardsToLSHand(ship);
        scn.SkipToLSTurn(Phase.CONTROL); force(scn, 5); scn.SkipToPhase(Phase.DEPLOY);
        // Mon Calamari itself discounts one more, so the ship has a legal deploy action with five Force.
        scn.LSDeployCard(ship); assertFalse(scn.LSHasCardChoiceAvailable(scn.GetLSCard("ralltiir")));
        assertTrue(scn.LSHasCardChoiceAvailable(scn.GetLSCard("moncal"))); scn.LSChooseCard(scn.GetLSCard("moncal"));
        scn.PassAllResponses(); assertAtLocation(scn.GetLSCard("moncal"), ship); assertEquals(0, scn.GetLSForcePileCount());
    }
    @Test public void MonCalamariDockyardsRemovalRestoresPrintedLocationRestrictions() {
        var scn = started(true); var ship = scn.GetLSCard("cruiser"); scn.MoveCardsToLSHand(ship, scn.GetLSCard("dockyards"));
        scn.SkipToLSTurn(Phase.CONTROL); force(scn, 8); scn.SkipToPhase(Phase.DEPLOY);
        scn.LSDeployCard(ship); assertFalse(scn.LSHasCardChoiceAvailable(scn.GetLSCard("ralltiir")));
        assertTrue(scn.LSHasCardChoiceAvailable(scn.GetLSCard("yavin"))); scn.LSChooseCard(scn.GetLSCard("yavin"));
        scn.PassAllResponses(); assertEquals(0, scn.GetLSForcePileCount());
    }
    @Test public void MonCalamariDockyardsCapsCombinedActualDeploymentReductionAtThree() {
        var scn = started(true); var ship = scn.GetLSCard("cruiser"); scn.MoveCardsToLSHand(ship);
        scn.AttachCardsTo(scn.GetLSCard("generators"), scn.GetLSCard("ebo"));
        scn.SkipToLSTurn(Phase.CONTROL); force(scn, 5); scn.SkipToPhase(Phase.DEPLOY);
        scn.LSDeployCard(ship); scn.LSChooseCard(scn.GetLSCard("moncal")); scn.PassAllResponses();
        // Printed8: Dockyards-2, MonCal-1, EchoBaseOperations-1; the printed -3 ceiling makes cost5.
        assertAtLocation(scn.GetLSCard("moncal"), ship); assertEquals(0, scn.GetLSForcePileCount());
    }
    @Test public void MonCalamariDockyardsDoesNotDiscountHomeOne() {
        var scn = started(true);
        assertEquals(12, scn.game().getModifiersQuerying().getDeployCost(scn.gameState(), scn.GetLSCard("home")), scn.epsilon);
    }
    @Test public void MonCalamariDockyardsDoesNotCapExcludedHomeOnesOtherDiscounts() {
        var scn = started(true); scn.MoveLocationToTable(scn.GetLSCard("warRoom"));
        scn.MoveCardsToLocation(scn.GetLSCard("warRoom"), scn.GetLSCard("rebel"));
        assertEquals(7, scn.game().getModifiersQuerying().getDeployCost(scn.gameState(), scn.GetLSCard("home")), scn.epsilon);
    }
    @Test public void MonCalamariDockyardsLegacyDoesNotUseModernProfundityException() {
        var scn = started(true);
        assertEquals(4, scn.game().getModifiersQuerying().getDeployCost(scn.gameState(), scn.GetLSCard("profundity")), scn.epsilon);
    }
    @Test public void MonCalamariDockyardsDoesNotDiscountOtherCapitalStarships() {
        var scn = started(true);
        assertEquals(3, scn.game().getModifiersQuerying().getDeployCost(scn.gameState(), scn.GetLSCard("corvette")), scn.epsilon);
    }

    private VirtualTableScenario battle(boolean source, boolean pilot, String shipName, int destiny) {
        var scn = started(source); var system = scn.GetLSCard("ralltiir"); var ship = scn.GetLSCard(shipName);
        scn.MoveCardsToLocation(system, ship, scn.GetDSCard("devastator"));
        scn.BoardAsPilot(scn.GetDSCard("devastator"), scn.GetDSCard("vader"));
        if (pilot) scn.BoardAsPilot(ship, scn.GetLSCard("luke"));
        scn.SkipToLSTurn(Phase.BATTLE); scn.PrepareLSDestiny(6); scn.PrepareLSDestiny(2); scn.PrepareDSDestiny(destiny - 1);
        scn.LSInitiateBattle(system); scn.PassAllResponses(); return scn;
    }
    @Test public void MonCalamariDockyardsLowAbilityCruiserDrawsActualBattleDestiny() {
        var scn = battle(true, false, "cruiser", 1); scn.SkipToEndOfPowerSegment(true);
        assertEquals(2, scn.GetLSTotalDestiny()); assertInZone(Zone.RESERVE_DECK, scn.GetLSDestiny(6));
    }
    @Test public void MonCalamariDockyardsDoesNotAddSecondDestinyWhenAlreadyAble() {
        var scn = battle(true, true, "cruiser", 1); scn.SkipToEndOfPowerSegment(true);
        assertEquals(2, scn.GetLSTotalDestiny()); assertInZone(Zone.RESERVE_DECK, scn.GetLSDestiny(6));
    }
    @Test public void MonCalamariDockyardsCruiserWithoutSourceCannotDrawLowAbilityDestiny() {
        var scn = battle(false, false, "cruiser", 1); scn.SkipToPowerSegment();
        assertFalse(scn.LSDecisionAvailable("battle destiny?")); assertInZone(Zone.RESERVE_DECK, scn.GetLSDestiny(2));
    }
    @Test public void MonCalamariDockyardsDoesNotGiveOtherCapitalShipsBattleDestiny() {
        var scn = battle(true, false, "corvette", 1); scn.SkipToPowerSegment();
        assertFalse(scn.LSDecisionAvailable("battle destiny?")); assertInZone(Zone.RESERVE_DECK, scn.GetLSDestiny(2));
    }
    private void attrition(boolean source, int value, boolean immune) {
        var scn = battle(source, false, "cruiser", value); var ship = scn.GetLSCard("cruiser");
        scn.SkipToDamageSegment(true); scn.PassDamageSegmentActions(); assertEquals(value, scn.GetUnpaidLSAttrition());
        if (scn.AwaitingLSBattleDamagePayment()) scn.LSPayRemainingBattleDamageFromReserveDeck();
        assertEquals(immune, scn.LSDecisionAvailable("(if desired)"));
        if (immune) scn.LSChooseCards(); else scn.LSPayAttritionFromCardInPlay(ship);
        assertInZone(immune ? Zone.AT_LOCATION : Zone.LOST_PILE, ship);
    }
    @Test public void MonCalamariDockyardsCruiserSurvivesActualAttritionThree() { attrition(true, 3, true); }
    @Test public void MonCalamariDockyardsCruiserMustForfeitAtActualAttritionFour() { attrition(true, 4, false); }
    @Test public void MonCalamariDockyardsAbsentCruiserMustForfeitAtAttritionThree() { attrition(false, 3, false); }

    @Test public void MonCalamariDockyardsCancelsExistingCapitalSupport() {
        var scn = started(false); scn.MoveCardsToLSSideOfTable(scn.GetLSCard("support"));
        scn.SkipToLSTurn(Phase.DEPLOY); scn.LSDeployCard(scn.GetLSCard("dockyards")); scn.PassAllResponses();
        assertInZone(Zone.LOST_PILE, scn.GetLSCard("support")); assertInZone(Zone.SIDE_OF_TABLE, scn.GetLSCard("dockyards"));
    }
    @Test public void MonCalamariDockyardsCancelsCapitalSupportActuallyBeingPlayed() {
        var scn = started(true); scn.MoveCardsToLSHand(scn.GetLSCard("support")); scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSPlayCard(scn.GetLSCard("support")); scn.PassAllResponses(); assertInZone(Zone.LOST_PILE, scn.GetLSCard("support"));
    }
    @Test public void MonCalamariDockyardsAbsentCapitalSupportStaysOnTable() {
        var scn = started(false); scn.MoveCardsToLSHand(scn.GetLSCard("support")); scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSPlayCard(scn.GetLSCard("support")); scn.PassAllResponses(); assertInZone(Zone.SIDE_OF_TABLE, scn.GetLSCard("support"));
    }
    @Test public void MonCalamariDockyardsDoesNotCancelOtherEffects() {
        var scn = started(true); var ebo = scn.GetLSCard("ebo"); scn.AttachCardsTo(scn.GetLSCard("generators"), ebo);
        scn.SkipToLSTurn(Phase.CONTROL); assertEquals(scn.GetLSCard("generators"), ebo.getAttachedTo());
    }
    @Test public void MonCalamariDockyardsIsNotAnAlterTargetWhileOrdinaryEffectIsCanceled() {
        var scn = started(true); var source = scn.GetLSCard("dockyards"); var beggar = scn.GetLSCard("beggar");
        scn.AttachCardsTo(scn.GetDSStartingLocation(), beggar); scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("vader"));
        scn.MoveCardsToDSHand(scn.GetDSCard("alter")); scn.SkipToDSTurn(Phase.CONTROL); scn.PrepareDSDestiny(1);
        scn.DSPlayCard(scn.GetDSCard("alter"), "Draw destiny to cancel Effect");
        assertFalse(scn.DSHasCardChoiceAvailable(source)); assertTrue(scn.DSHasCardChoiceAvailable(beggar));
        scn.DSChooseCard(beggar); scn.DSChooseCard(scn.GetDSCard("vader"));
        scn.PassAllResponses(); scn.PassDestinyDrawResponses(); scn.PassAllResponses();
        assertInZone(Zone.LOST_PILE, beggar); assertInZone(Zone.SIDE_OF_TABLE, source);
    }
}
