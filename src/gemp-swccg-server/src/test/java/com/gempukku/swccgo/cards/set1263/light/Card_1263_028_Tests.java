package com.gempukku.swccgo.cards.set1263.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.*;

public class Card_1263_028_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("firefight", "1263_28"); put("trooper", "1_28"); put("trooper2", "1_28");
            put("farTrooper", "1_28"); put("luke", "1_19"); put("bravo", "14_58");
            put("red5", "2_71"); put("speeder", "3_69"); put("coruscantSite", "12_74");
        }}, new HashMap<>() {{
            put("vader", "1_168"); put("stormtrooper", "1_194"); put("tie", "1_304");
            put("coruscant", "12_165");
        }}, 40, 40, StartingSetup.LSStartingLocation("1_136"), StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void FirefightStatsMatchPrintedSource() {
        var scn = scenario(); var b = scn.GetLSCard("firefight").getBlueprint();
        assertEquals("Firefight", b.getTitle()); assertTrue(b.hasVirtualSuffix());
        assertEquals(Side.LIGHT, b.getSide()); assertEquals(Uniqueness.UNRESTRICTED, b.getUniqueness());
        assertEquals(5, b.getDestiny(), scn.epsilon); assertEquals(CardSubtype.USED, b.getCardSubtype());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_3, b.getExpansionSet()); assertEquals(Rarity.V, b.getRarity());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(Arrays.asList(CardType.INTERRUPT)));
        scn.BlueprintIconCheck(b, new ArrayList<>(Arrays.asList(Icon.INTERRUPT, Icon.ENDOR, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>());
    }

    private VirtualTableScenario ground(boolean coruscant, boolean troopers) {
        var scn = scenario(); scn.StartGame();
        var site = coruscant ? scn.GetLSCard("coruscantSite") : scn.GetLSStartingLocation();
        if (coruscant) scn.MoveLocationToTable(site);
        scn.MoveCardsToLocation(site, scn.GetLSCard("luke"), scn.GetDSCard("vader"));
        if (troopers) scn.MoveCardsToLocation(site, scn.GetLSCard("trooper"), scn.GetLSCard("trooper2"));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetLSCard("farTrooper"));
        scn.MoveCardsToLSHand(scn.GetLSCard("firefight"));
        scn.SkipToLSTurn(Phase.BATTLE); scn.LSInitiateBattle(site);
        return scn;
    }

    private void play(VirtualTableScenario scn) {
        assertTrue(scn.LSCardPlayAvailable(scn.GetLSCard("firefight")));
        scn.LSPlayCard(scn.GetLSCard("firefight")); scn.PassAllResponses();
        assertInZone(Zone.USED_PILE, scn.GetLSCard("firefight"));
    }

    @Test
    public void FirefightBoostsEachOwnTrooperPresentButNotOtherCharactersOrSites() {
        var scn = ground(false, true); play(scn);
        assertEquals(2, scn.GetPower(scn.GetLSCard("trooper")));
        assertEquals(2, scn.GetPower(scn.GetLSCard("trooper2")));
        assertEquals(3, scn.GetPower(scn.GetLSCard("luke")));
        assertEquals(6, scn.GetPower(scn.GetDSCard("vader")));
        assertEquals(1, scn.GetPower(scn.GetLSCard("farTrooper")));
        assertTrue(scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), scn.GetLSCard("trooper")) > 100);
        assertEquals(0, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), scn.GetLSCard("farTrooper")), scn.epsilon);
    }

    @Test
    public void FirefightDoesNotPlayAtCoruscantSite() {
        var scn = ground(true, true);
        assertFalse(scn.LSCardPlayAvailable(scn.GetLSCard("firefight")));
    }

    @Test
    public void FirefightDoesNotPlayWithoutEligibleParticipant() {
        var scn = ground(false, false);
        assertFalse(scn.LSCardPlayAvailable(scn.GetLSCard("firefight")));
    }

    @Test
    public void FirefightOnlyPlaysWhenBattleJustInitiated() {
        var scn = ground(false, true); scn.PassAllResponses();
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        assertFalse(scn.LSCardPlayAvailable(scn.GetLSCard("firefight")));
    }

    @Test
    public void FirefightDoesNotGrantBonusToLaterArrival() {
        var scn = ground(false, true); play(scn);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("farTrooper"));
        assertEquals(1, scn.GetPower(scn.GetLSCard("farTrooper")));
        assertEquals(0, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), scn.GetLSCard("farTrooper")), scn.epsilon);
    }

    @Test
    public void FirefightDoesNotAffectTrooperInsideEnclosedVehicle() {
        var scn = scenario(); scn.StartGame(); var site = scn.GetLSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetLSCard("luke"), scn.GetLSCard("trooper"),
                scn.GetLSCard("speeder"), scn.GetDSCard("vader"));
        scn.BoardAsPassenger(scn.GetLSCard("speeder"), scn.GetLSCard("farTrooper"));
        scn.MoveCardsToLSHand(scn.GetLSCard("firefight"));
        scn.SkipToLSTurn(Phase.BATTLE); scn.LSInitiateBattle(site); play(scn);
        assertEquals(2, scn.GetPower(scn.GetLSCard("trooper")));
        assertEquals(1, scn.GetPower(scn.GetLSCard("farTrooper")));
        assertEquals(0, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), scn.GetLSCard("farTrooper")), scn.epsilon);
    }

    @Test
    public void FirefightProtectedTroopersDoNotPayBattleAttrition() {
        var scn = ground(false, true); play(scn);
        scn.MoveCardsToLSHand(scn.GetLSCard("luke"));
        scn.PrepareDSDestiny(1);
        scn.SkipToDamageSegment(true); scn.PassDamageSegmentActions();
        assertTrue(scn.AwaitingLSAttritionPayment());
        assertEquals(2, scn.GetUnpaidLSAttrition());
        if (scn.AwaitingLSBattleDamagePayment()) scn.LSPayRemainingBattleDamageFromReserveDeck();
        assertTrue(scn.LSDecisionAvailable("(if desired)"));
        scn.LSChooseCards();
        scn.PassAllResponses();
        assertInZone(Zone.AT_LOCATION, scn.GetLSCard("trooper"));
        assertInZone(Zone.AT_LOCATION, scn.GetLSCard("trooper2"));
    }

    @Test
    public void FirefightLastsBeyondBattleAndExpiresAtEndOfTurn() {
        var scn = ground(false, true); play(scn);
        scn.SkipToDamageSegment(false); scn.PassDamageSegmentActions();
        scn.DSPayRemainingBattleDamageFromReserveDeck(); scn.PassAllResponses();
        assertEquals(2, scn.GetPower(scn.GetLSCard("trooper")));
        scn.SkipToDSTurn(Phase.ACTIVATE);
        assertEquals(1, scn.GetPower(scn.GetLSCard("trooper")));
        assertEquals(0, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), scn.GetLSCard("trooper")), scn.epsilon);
    }

    @Test
    public void FirefightAffectsRepublicStarfighterAtCoruscantSystem() {
        var scn = scenario(); scn.StartGame(); var system = scn.GetDSCard("coruscant");
        scn.MoveLocationToTable(system);
        scn.MoveCardsToLocation(system, scn.GetLSCard("bravo"), scn.GetLSCard("red5"), scn.GetDSCard("tie"));
        scn.BoardAsPilot(scn.GetLSCard("red5"), scn.GetLSCard("luke"));
        scn.MoveCardsToLSHand(scn.GetLSCard("firefight"));
        scn.SkipToLSTurn(Phase.BATTLE); scn.LSInitiateBattle(system); play(scn);
        assertEquals(3, scn.GetPower(scn.GetLSCard("bravo")));
        assertEquals(6, scn.GetPower(scn.GetLSCard("red5")));
        assertTrue(scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), scn.GetLSCard("bravo")) > 100);
        assertEquals(4, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), scn.GetLSCard("red5")), scn.epsilon);
    }
}
