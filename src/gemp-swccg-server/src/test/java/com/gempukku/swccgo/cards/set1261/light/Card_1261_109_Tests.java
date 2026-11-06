package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.BooleanSupplier;

import static org.junit.Assert.*;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;

public class Card_1261_109_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("dash", "1261_109"); put("speeder", "3_69"); put("luke", "1_19");
            put("hoth", "3_62"); put("otherSite", "3_56"); put("pilot", "1_27");
        }}, new HashMap<>() {{ put("vader", "1_168"); }}, 30, 30,
                StartingSetup.LSStartingLocation("1_136"), StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void DashInRogue10StatsMatchFinalMaster() {
        var scn = scenario(); var b = scn.GetLSCard("dash").getBlueprint();
        assertEquals("Dash In Rogue 10", b.getTitle());
        assertFalse(b.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness());
        assertEquals(Side.LIGHT, b.getSide());
        assertEquals(2, b.getDestiny(), scn.epsilon);
        assertEquals(3, b.getDeployCost(), scn.epsilon);
        assertEquals(5, b.getPower(), scn.epsilon);
        assertEquals(5, b.getManeuver(), scn.epsilon);
        assertEquals(4, b.getLandspeed(), scn.epsilon);
        assertEquals(5, b.getForfeit(), scn.epsilon);
        assertEquals(1, b.getPilotCapacity());
        assertEquals(0, b.getPassengerCapacity());
        assertEquals("Enclosed.", b.getLore());
        assertEquals(CardSubtype.COMBAT, b.getCardSubtype());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, b.getExpansionSet());
        assertEquals(Rarity.V, b.getRarity());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(Arrays.asList(CardType.VEHICLE)));
        scn.BlueprintIconCheck(b, new ArrayList<>(Arrays.asList(Icon.VEHICLE, Icon.PILOT, Icon.HOTH, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>(Arrays.asList(Keyword.ENCLOSED, Keyword.SNOWSPEEDER, Keyword.ROGUE_SQUADRON)));
    }

    private void startAtSite(VirtualTableScenario scn, boolean anotherSpeeder) {
        scn.StartGame();
        var site = scn.GetLSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetLSCard("dash"), scn.GetLSCard("luke"), scn.GetDSCard("vader"));
        if (anotherSpeeder) scn.MoveCardsToLocation(site, scn.GetLSCard("speeder"));
    }

    @Test
    public void DashInRogue10PermanentPilotProvidesAbilityThree() {
        var scn = scenario(); startAtSite(scn, false);
        assertEquals(3, scn.GetBattleDestinyAbility(scn.GetLSCard("dash")));
        assertTrue(com.gempukku.swccgo.filters.Filters.hasPermanentAboard(com.gempukku.swccgo.filters.Filters.Dash)
                .accepts(scn.game(), scn.GetLSCard("dash")));
    }

    @Test
    public void DashInRogue10MayDeployAnAdditionalPilot() {
        var scn = scenario(); startAtSite(scn, false);
        var pilot = scn.GetLSCard("pilot"); var dash = scn.GetLSCard("dash");
        scn.MoveCardsToLSHand(pilot); scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSDeployCard(pilot); scn.LSChooseCard(dash); scn.PassAllResponses();
        assertEquals(dash, pilot.getAttachedTo());
        assertEquals(2, scn.GetBattleDestinyAbility(pilot));
        assertTrue(com.gempukku.swccgo.filters.Filters.piloting(dash).accepts(scn.game(), pilot));
        assertEquals(7, scn.GetPower(dash));
    }

    @Test
    public void DashInRogue10HasPrintedImmunityThresholdFour() {
        var scn = scenario(); startAtSite(scn, false);
        var dash = scn.GetLSCard("dash");
        assertEquals(4, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), dash), scn.epsilon);
    }

    private void attrition(int destiny, boolean immune) {
        var scn = scenario(); startAtSite(scn, false);
        scn.MoveCardsToLSHand(scn.GetLSCard("luke"));
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.PrepareDSDestiny(destiny - 1);
        scn.LSInitiateBattle(scn.GetLSStartingLocation());
        scn.PassAllResponses(); scn.SkipToDamageSegment(true); scn.PassDamageSegmentActions();
        assertEquals(destiny, scn.GetUnpaidLSAttrition());
        if (scn.AwaitingLSBattleDamagePayment()) scn.LSPayRemainingBattleDamageFromReserveDeck();
        assertEquals(immune, scn.LSDecisionAvailable("(if desired)"));
        if (immune) scn.LSChooseCards();
        else scn.LSPayAttritionFromCardInPlay(scn.GetLSCard("dash"));
        assertInZone(immune ? Zone.AT_LOCATION : Zone.LOST_PILE, scn.GetLSCard("dash"));
    }

    @Test public void DashInRogue10DoesNotPayAttritionThree() { attrition(3, true); }
    @Test public void DashInRogue10MayBeForfeitedAtAttritionFour() { attrition(4, false); }

    private void battle(VirtualTableScenario scn) {
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.PrepareLSDestiny(2); scn.PrepareLSDestiny(6);
        scn.LSInitiateBattle(scn.GetLSStartingLocation());
        scn.PassAllResponses(); scn.SkipToPowerSegment();
    }

    private boolean reach(VirtualTableScenario scn, BooleanSupplier goal) {
        for (int i = 0; i < 60; i++) {
            if (goal.getAsBoolean()) return true;
            var d = scn.GetCurrentDecision(); if (d == null) return false;
            String player = scn.LSGetDecision() != null ? scn.LS : scn.DS;
            if (d.getText().toLowerCase().contains("battle destiny?") && scn.ChoiceAvailable(player, "Yes"))
                scn.PlayerChooseYes(player);
            else if (d.getText().toLowerCase().contains("response") || d.getText().toLowerCase().contains("or pass"))
                scn.PlayerPass(player);
            else return false;
        }
        return goal.getAsBoolean();
    }

    private boolean drawAction(VirtualTableScenario scn) {
        return scn.LSGetADParam("actionText") != null && scn.LSCardActionAvailable(scn.GetLSCard("dash"), "Draw two battle destiny");
    }

    @Test
    public void DashInRogue10DrawsTwoAndActuallyUsesChosenDestiny() {
        var scn = scenario(); startAtSite(scn, true);
        battle(scn); assertTrue(reach(scn, () -> drawAction(scn)));
        scn.LSUseCardAction(scn.GetLSCard("dash"), "Draw two battle destiny");
        assertTrue(reach(scn, () -> scn.LSDecisionAvailable("Choose") && scn.LSGetCardChoiceCount() == 2));
        scn.LSChooseCard(scn.GetLSDestiny(2));
        scn.PassAllResponses();
        assertEquals(2, scn.GetLSTotalDestiny());
    }

    @Test
    public void DashInRogue10MayDeclineAndDrawOnlyOne() {
        var scn = scenario(); startAtSite(scn, true);
        battle(scn); assertTrue(reach(scn, () -> drawAction(scn)));
        scn.LSPass(); scn.PassAllResponses();
        assertEquals(6, scn.GetLSTotalDestiny());
        assertTrue(scn.GetLSReserveDeck().contains(scn.GetLSDestiny(2)));
    }

    @Test
    public void DashInRogue10DoesNotDrawTwoWithoutAnotherT47() {
        var scn = scenario(); startAtSite(scn, false); battle(scn);
        scn.LSChooseYes();
        assertFalse(drawAction(scn));
    }

    @Test
    public void DashInRogue10DoesNotCountT47AtAnotherSite() {
        var scn = scenario(); startAtSite(scn, false);
        scn.MoveLocationToTable(scn.GetLSCard("hoth"));
        scn.MoveCardsToLocation(scn.GetLSCard("hoth"), scn.GetLSCard("speeder"));
        battle(scn); scn.LSChooseYes(); assertFalse(drawAction(scn));
    }

    @Test
    public void DashInRogue10DoesNotAffectBattleAtAnotherSite() {
        var scn = scenario(); startAtSite(scn, true);
        var hoth = scn.GetLSCard("hoth"); scn.MoveLocationToTable(hoth);
        scn.MoveCardsToLocation(hoth, scn.GetLSCard("luke"), scn.GetDSCard("vader"));
        scn.SkipToLSTurn(Phase.BATTLE); scn.LSInitiateBattle(hoth); scn.PassAllResponses(); scn.SkipToPowerSegment();
        scn.LSChooseYes(); assertFalse(drawAction(scn));
    }
}
