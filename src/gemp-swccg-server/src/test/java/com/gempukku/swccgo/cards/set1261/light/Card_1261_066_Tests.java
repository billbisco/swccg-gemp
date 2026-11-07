package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import static com.gempukku.swccgo.framework.Assertions.*;
import static org.junit.Assert.*;

public class Card_1261_066_Tests {
    private static final String POWER = "Add one destiny to total power";
    private static final String ATTRITION = "Add one destiny to attrition";
    private static final String ABILITY = "Use Han's ability for weapon destiny instead";
    private static final String DOWNLOAD = "Deploy non-Maintenance Chewie";

    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("han", "1261_66"); put("falcon", "1_143"); put("red", "1_144"); put("lando", "5_5"); put("wing", "1_146");
            put("leia", "1_17"); put("pistol", "1_154"); put("chewie", "109_1"); put("maintenance", "13_9");
            put("site", "1_129"); put("remote", "1_136"); put("jedi", "9_24"); put("saber", "9_90"); put("traffic", "1_66");
        }}, new HashMap<>() {{ put("tie", "1_304"); put("trooper", "1_194"); put("vader", "1_168"); }},
                60, 60, StartingSetup.LSStartingLocation("2_65"), StartingSetup.DefaultDSSpaceSystem,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }
    private VirtualTableScenario started() {
        var scn = scenario(); scn.StartGame(); scn.MoveLocationToTable(scn.GetLSCard("site")); scn.MoveLocationToTable(scn.GetLSCard("remote"));
        scn.MoveCardsToLSHand(scn.GetLSCard("han")); return scn;
    }
    @Test public void CourageousHanUsesPrintedArmorFiveAndNewTitle() {
        var scn = scenario(); var b = scn.GetLSCard("han").getBlueprint();
        assertEquals("Han Solo, Courageous Smuggler", b.getTitle()); assertEquals(Side.LIGHT, b.getSide());
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness()); assertFalse(b.hasVirtualSuffix());
        assertEquals(1, b.getDestiny(), scn.epsilon); assertEquals(3, b.getDeployCost(), scn.epsilon); assertEquals(4, b.getPower(), scn.epsilon);
        assertEquals(3, b.getAbility(), scn.epsilon); assertEquals(5, b.getArmor(), scn.epsilon); assertEquals(6, b.getForfeit(), scn.epsilon);
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, b.getExpansionSet()); assertEquals(Rarity.V, b.getRarity());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(Arrays.asList(CardType.REBEL)));
        scn.BlueprintPersonaCheck(b, new ArrayList<>(Arrays.asList(Persona.HAN)));
        scn.BlueprintIconCheck(b, new ArrayList<>(Arrays.asList(Icon.REBEL, Icon.PILOT, Icon.WARRIOR, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>(Arrays.asList(Keyword.SMUGGLER)));
    }
    @Test public void CourageousHanActuallyDeploysForThreeAndUsesArmorDefense() {
        var scn = started(); scn.SkipToLSTurn(Phase.CONTROL); scn.LSActivateForceCheat(5); scn.SkipToPhase(Phase.DEPLOY);
        int force = scn.GetLSForcePileCount(); scn.LSDeployCard(scn.GetLSCard("han")); scn.LSChooseCard(scn.GetLSCard("site")); scn.PassAllResponses();
        assertAtLocation(scn.GetLSCard("site"), scn.GetLSCard("han")); assertEquals(force-3, scn.GetLSForcePileCount()); assertEquals(5, scn.GetDefense(scn.GetLSCard("han")));
    }
    private VirtualTableScenario spaceBattle(String ship, boolean passenger) {
        return spaceBattle(ship, passenger, false);
    }
    private VirtualTableScenario spaceBattle(String ship, boolean passenger, boolean extraAbility) {
        var scn = started(); var craft = scn.GetLSCard(ship); var system = scn.GetLSStartingLocation();
        scn.MoveCardsToLocation(system, craft, scn.GetDSCard("tie"));
        if (passenger) {
            scn.MoveCardsToLSHand(scn.GetLSCard("lando")); scn.BoardAsPilot(craft, scn.GetLSCard("lando")); scn.BoardAsPassenger(craft, scn.GetLSCard("han"));
        } else scn.BoardAsPilot(craft, scn.GetLSCard("han"));
        if (extraAbility) scn.MoveCardsToLocation(system, scn.GetLSCard("wing"));
        scn.SkipToLSTurn(Phase.BATTLE); scn.LSInitiateBattle(system); scn.PassAllResponses(); return scn;
    }
    private void finishDestinies(VirtualTableScenario scn, boolean drawBattle) {
        for (int i = 0; i < 4; i++) { scn.SkipToEndOfPowerSegment(drawBattle); scn.PassDestinyDrawResponses(); }
    }
    @Test public void CourageousHanAddsThreeEvenWhenPilotingOtherStarship() {
        var scn = spaceBattle("red", false); assertEquals(6, scn.GetPower(scn.GetLSCard("red")));
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("han"), POWER)); assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("han"), ATTRITION));
    }
    @Test public void CourageousHanPassengerDoesNotAddPowerOrGrantFalconDestiny() {
        var scn = spaceBattle("falcon", true); assertEquals(6, scn.GetPower(scn.GetLSCard("falcon")));
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("han"), POWER)); assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("han"), ATTRITION));
    }
    @Test public void CourageousHanActuallyDrawsPowerOnlyAndCannotAlsoChooseAttrition() {
        var scn = spaceBattle("falcon", false); var han = scn.GetLSCard("han"); assertEquals(6, scn.GetPower(scn.GetLSCard("falcon")));
        scn.LSUseCardAction(han, POWER); scn.PassAllResponses(); if (scn.AwaitingDSWeaponsSegmentActions()) scn.DSPass();
        assertFalse(scn.GetLSAvailableActions().stream().anyMatch(x -> x.contains(ATTRITION) || x.contains(POWER)));
        scn.PrepareLSDestiny(3); finishDestinies(scn, false);
        assertEquals(3, scn.gameState().getBattleState().getTotalDestinyToPowerOnly(scn.LS), scn.epsilon);
        assertEquals(0, scn.gameState().getBattleState().getNumDestinyToAttritionDrawn(scn.LS)); assertEquals(0, scn.GetLSTotalDestiny());
    }
    @Test public void CourageousHanActuallyDrawsAttritionOnlyAndCannotAlsoChoosePower() {
        var scn = spaceBattle("falcon", false, true); scn.LSUseCardAction(scn.GetLSCard("han"), ATTRITION); scn.PassAllResponses();
        if (scn.AwaitingDSWeaponsSegmentActions()) scn.DSPass();
        assertFalse(scn.GetLSAvailableActions().stream().anyMatch(x -> x.contains(ATTRITION) || x.contains(POWER)));
        scn.MoveCardsToTopOfLSReserveDeck(scn.GetLSDestiny(3), scn.GetLSDestiny(0));
        finishDestinies(scn, true); scn.PassResponses("INITIAL_ATTRITION_CALCULATED");
        assertEquals(1, scn.gameState().getBattleState().getNumDestinyToAttritionDrawn(scn.LS));
        assertEquals(0, scn.gameState().getBattleState().getTotalDestinyToPowerOnly(scn.LS), scn.epsilon);
        assertEquals(3, scn.GetUnpaidDSAttrition());
    }
    @Test public void CourageousHanDoesNotDrawAttritionDestinyWithoutABattleDestiny() {
        var scn = spaceBattle("falcon", false); scn.LSUseCardAction(scn.GetLSCard("han"), ATTRITION); scn.PassAllResponses();
        scn.PrepareLSDestiny(3); finishDestinies(scn, false);
        assertEquals(0, scn.gameState().getBattleState().getNumDestinyToAttritionDrawn(scn.LS)); assertInZone(Zone.RESERVE_DECK, scn.GetLSDestiny(3));
    }
    private VirtualTableScenario fire(boolean hanFires) {
        var scn = started(); var site = scn.GetLSCard("site"); var shooter = scn.GetLSCard(hanFires ? "han" : "leia");
        scn.MoveCardsToLocation(site, scn.GetLSCard("han"), scn.GetDSCard("trooper"));
        if (!hanFires) scn.MoveCardsToLocation(site, shooter);
        scn.AttachCardsTo(shooter, scn.GetLSCard("pistol")); scn.SkipToLSTurn(Phase.CONTROL); scn.LSActivateForceCheat(5); scn.SkipToPhase(Phase.BATTLE);
        scn.LSInitiateBattle(site); scn.PassAllResponses(); scn.PrepareLSDestiny(0); scn.LSUseCardAction(scn.GetLSCard("pistol"), "Fire");
        scn.LSChooseCard(scn.GetDSCard("trooper"));
        // Stop on the real substitution window; otherwise advance through the weapon's nested response windows.
        for (int i = 0; i < 16; i++) {
            if (scn.GetLSAvailableActions().stream().anyMatch(x -> x.contains(ABILITY))) break;
            var decision = scn.GetCurrentDecision();
            if (decision == null || !decision.getText().toLowerCase().contains("response")) break;
            scn.PlayerPass(scn.GetAwaitingDecision(scn.LS) != null ? scn.LS : scn.DS);
        }
        return scn;
    }
    @Test public void CourageousHanSubstitutesAbilityBeforeDrawingAndHitsTrooper() {
        var scn = fire(true); assertTrue(scn.GetCurrentDecision().getText(), scn.GetLSAvailableActions().stream().anyMatch(x -> x.contains(ABILITY)));
        scn.LSUseCardAction(scn.GetLSCard("han"), ABILITY);
        scn.PassWeaponFireWithDestinyDraw(); assertTrue(scn.GetDSCard("trooper").isHit()); assertInZone(Zone.RESERVE_DECK, scn.GetLSDestiny(0));
        assertEquals(0, scn.GetForfeit(scn.GetDSCard("trooper")));
    }
    @Test public void CourageousHanMayDeclineSubstitutionAndActuallyDrawZero() {
        var scn = fire(true); assertTrue(scn.GetCurrentDecision().getText(), scn.GetLSAvailableActions().stream().anyMatch(x -> x.contains(ABILITY)));
        scn.LSPass();
        scn.PassAllResponses(); scn.PassWeaponFireWithDestinyDraw();
        assertFalse(scn.GetDSCard("trooper").isHit()); assertInZone(Zone.USED_PILE, scn.GetLSDestiny(0));
    }
    @Test public void CourageousHanDoesNotSubstituteForOtherCharactersWeapon() {
        var scn = fire(false); assertFalse(scn.GetLSAvailableActions().stream().anyMatch(x -> x.contains(ABILITY)));
        scn.PassWeaponFireWithDestinyDraw(); assertFalse(scn.GetDSCard("trooper").isHit()); assertInZone(Zone.USED_PILE, scn.GetLSDestiny(0));
    }
    @Test public void CourageousHanActuallyDownloadsNonMaintenanceChewieToSameSiteAndReshuffles() {
        var scn = started(); var site = scn.GetLSCard("site"); scn.MoveCardsToLocation(site, scn.GetLSCard("han"));
        scn.SkipToLSTurn(Phase.CONTROL); scn.LSActivateForceCheat(10);
        scn.MoveCardsToTopOfLSReserveDeck(scn.GetLSCard("chewie"), scn.GetLSCard("maintenance"), scn.GetLSCard("traffic"));
        scn.SkipToPhase(Phase.DEPLOY); int force = scn.GetLSForcePileCount();
        scn.gameState().markCardsRevealedFromPile(Collections.singletonList(scn.GetLSCard("traffic")));
        scn.LSUseCardAction(scn.GetLSCard("han"), DOWNLOAD); scn.PassAllResponses();
        int illegal = scn.LSGetBPChoices().indexOf(scn.GetLSCard("maintenance").getBlueprintId(true));
        assertTrue(illegal < 0 || !Boolean.parseBoolean(scn.LSGetADParamAsList("selectable").get(illegal)));
        scn.LSChooseCard(scn.GetLSCard("chewie"));
        assertFalse(scn.LSHasCardChoiceAvailable(scn.GetLSCard("remote")));
        if (scn.LSHasCardChoiceAvailable(site)) scn.LSChooseCard(site);
        scn.PassAllResponses(); assertAtLocation(site, scn.GetLSCard("chewie")); assertEquals(force-5, scn.GetLSForcePileCount());
        assertFalse(scn.gameState().isCardRevealedFromPile(scn.GetLSCard("traffic"))); assertInZone(Zone.RESERVE_DECK, scn.GetLSCard("maintenance"));
        scn.MoveCardsToTopOfLSReserveDeck(scn.GetLSCard("chewie")); scn.SkipToLSTurn(Phase.DEPLOY);
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("han"), DOWNLOAD));
    }
    @Test public void CourageousHanCannotDownloadChewieAtSystemOrOutsideDeployPhase() {
        var scn = started(); scn.MoveCardsToLocation(scn.GetLSCard("site"), scn.GetLSCard("han")); scn.SkipToLSTurn(Phase.CONTROL);
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("han"), DOWNLOAD));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("falcon")); scn.MoveCardsToLSHand(scn.GetLSCard("han"));
        scn.BoardAsPilot(scn.GetLSCard("falcon"), scn.GetLSCard("han")); scn.SkipToPhase(Phase.DEPLOY);
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("han"), DOWNLOAD));
    }
    private void attrition(int amount, boolean immune) {
        var scn = started(); var han = scn.GetLSCard("han"); var site = scn.GetLSCard("site");
        scn.MoveCardsToLocation(site, han, scn.GetLSCard("jedi"), scn.GetDSCard("vader")); scn.AttachCardsTo(scn.GetLSCard("jedi"), scn.GetLSCard("saber"));
        scn.SkipToLSTurn(Phase.BATTLE); scn.LSInitiateBattle(site); scn.PassAllResponses(); scn.PrepareLSDestiny(0); scn.PrepareDSDestiny(amount-1);
        finishDestinies(scn, true); scn.PassResponses("INITIAL_ATTRITION_CALCULATED"); scn.PassDamageSegmentActions();
        assertEquals(amount, scn.GetUnpaidLSAttrition()); assertEquals(0, scn.GetUnpaidLSBattleDamage()); assertEquals(immune, scn.LSDecisionAvailable("(if desired)"));
        if (immune) { scn.LSChooseCards(); assertAtLocation(site, han); }
        else { scn.LSPayAttritionFromCardInPlay(han); scn.PassAllResponses(); assertInZone(Zone.LOST_PILE, han); }
    }
    @Test public void CourageousHanSurvivesActualAttritionTwo() { attrition(2, true); }
    @Test public void CourageousHanMustForfeitAtActualAttritionThree() { attrition(3, false); }
}
