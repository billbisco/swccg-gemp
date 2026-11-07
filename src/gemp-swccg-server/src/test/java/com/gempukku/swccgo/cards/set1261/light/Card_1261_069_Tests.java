package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.*;

public class Card_1261_069_Tests {
    private static final String COMPANION = "Move one Rebel with Leia";

    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.ofEntries(
                Map.entry("leia", "1261_69"), Map.entry("sporting", "1_156"),
                Map.entry("jungle", "1_137"), Map.entry("rebel", "1_28"),
                Map.entry("otherRebel", "2_18"), Map.entry("droid", "2_14"),
                Map.entry("otherLeia", "1_17"), Map.entry("jedi", "9_24"),
                Map.entry("saber", "9_90"), Map.entry("falcon", "1_143"),
                Map.entry("red", "1_144"), Map.entry("tibrin", "6_87"))),
                new HashMap<>(Map.of("vader", "1_168", "trooper", "1_194")),
                40, 40, StartingSetup.LSStartingLocation("1_136"), StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, "legacy");
    }

    @Test public void optimisticLeiaUsesTheExactLegacyPrinting() {
        var scn = scenario(); var b = scn.GetLSCard("leia").getBlueprint();
        assertEquals("Leia, Optimistic Leader", b.getTitle());
        assertEquals(1, b.getDestiny(), scn.epsilon);
        assertEquals(3, b.getPower(), scn.epsilon); assertEquals(3, b.getAbility(), scn.epsilon);
        assertEquals(3, b.getDeployCost(), scn.epsilon); assertEquals(7, b.getForfeit(), scn.epsilon);
        assertEquals(Side.LIGHT, b.getSide()); assertEquals(Rarity.V, b.getRarity());
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness()); assertFalse(b.hasVirtualSuffix());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, b.getExpansionSet());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(List.of(CardType.REBEL)));
        scn.BlueprintPersonaCheck(b, new ArrayList<>(List.of(Persona.LEIA)));
        scn.BlueprintIconCheck(b, new ArrayList<>(List.of(Icon.REBEL, Icon.PILOT, Icon.WARRIOR, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>(List.of(Keyword.SPY, Keyword.FEMALE)));
    }

    @Test public void leiaAddsOnePowerOnlyWhilePilotingAnyShip() {
        var scn = scenario(); scn.StartGame(); var space = scn.GetLSCard("tibrin");
        var leia = scn.GetLSCard("leia"); var falcon = scn.GetLSCard("falcon"); var red = scn.GetLSCard("red");
        scn.MoveLocationToTable(space); scn.MoveCardsToLocation(space, falcon, red);
        int before = scn.GetPower(falcon);
        scn.MoveCardsToLSHand(leia); scn.BoardAsPassenger(falcon, leia);
        assertEquals(before, scn.GetPower(falcon));
        scn.MoveCardsToLSHand(leia); scn.BoardAsPilot(red, leia);
        assertEquals(4, scn.GetPower(red));
    }

    private void darkDeploy(String cardName, boolean leiaAtTarget, int expected) {
        var scn = scenario(); scn.StartGame();
        var target = scn.GetLSStartingLocation(); var remote = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(leiaAtTarget ? target : remote, scn.GetLSCard("leia"));
        var card = scn.GetDSCard(cardName);
        scn.MoveCardsToDSHand(card);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSActivateForceCheat(12);
        int before = scn.GetDSForcePileCount();
        scn.DSDeployCardAndPassResponses(card, target);
        assertAtLocation(target, card);
        assertEquals(expected, before - scn.GetDSForcePileCount());
    }

    @Test public void darkJediPayOneExtraHereButNotElsewhereAndOtherCharactersAreUnchanged() {
        darkDeploy("vader", true, 7);
        darkDeploy("vader", false, 6);
        darkDeploy("trooper", true, 1);
    }

    private boolean fireSportingBlaster(boolean optimistic) {
        var scn = scenario(); scn.StartGame(); var site = scn.GetLSStartingLocation();
        var leia = scn.GetLSCard(optimistic ? "leia" : "otherLeia");
        var blaster = scn.GetLSCard("sporting"); var trooper = scn.GetDSCard("trooper");
        scn.MoveCardsToLocation(site, leia, trooper);
        assertEquals(3, scn.GetPower(leia));
        scn.AttachCardsTo(leia, blaster);
        assertEquals(optimistic ? 4 : 3, scn.GetPower(leia));
        scn.SkipToLSTurn(Phase.BATTLE); scn.LSInitiateBattle(site); scn.PassAllResponses();
        scn.PrepareLSDestiny(2); scn.LSUseCardAction(blaster, "Fire");
        if (scn.LSHasCardChoiceAvailable(trooper)) scn.LSChooseCard(trooper);
        scn.PassAllResponses(); scn.PassWeaponFireWithDestinyDraw();
        return trooper.isHit();
    }

    @Test public void actualSportingBlasterHitsOnlyWithOptimisticLeiasDestinyBonus() {
        assertTrue(fireSportingBlaster(true));
        assertFalse(fireSportingBlaster(false));
    }

    private VirtualTableScenario react() {
        var scn = scenario(); scn.StartGame();
        var origin = scn.GetLSCard("jungle"); var target = scn.GetLSStartingLocation();
        scn.MoveLocationToTable(origin);
        scn.MoveCardsToLocation(origin, scn.GetLSCard("leia"), scn.GetLSCard("rebel"),
                scn.GetLSCard("otherRebel"), scn.GetLSCard("droid"));
        scn.MoveCardsToLocation(target, scn.GetLSCard("jedi"), scn.GetDSCard("vader"));
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.LSActivateForceCheat(5);
        scn.DSInitiateBattle(target);
        assertTrue(scn.GetLSAvailableActions().toString(), scn.LSCardActionAvailable(scn.GetLSCard("leia"), "Move as a 'react"));
        scn.LSUseCardAction(scn.GetLSCard("leia"), "Move as a 'react");
        if (scn.LSHasCardChoiceAvailable(target)) scn.LSChooseCard(target);
        for (int i = 0; i < 18 && scn.GetLSAvailableActions().stream().noneMatch(x -> x.contains(COMPANION)); i++) {
            var d = scn.GetCurrentDecision();
            if (d == null || !d.getText().toLowerCase().contains("response")) break;
            scn.PlayerPass(scn.LSGetDecision() != null ? scn.LS : scn.DS);
        }
        assertTrue(scn.GetLSAvailableActions().toString(), scn.LSCardActionAvailable(scn.GetLSCard("leia"), COMPANION));
        return scn;
    }

    @Test public void actualLandspeedReactCanBringExactlyOnePresentRebel() {
        var scn = react(); var target = scn.GetLSStartingLocation(); var origin = scn.GetLSCard("jungle");
        var rebel = scn.GetLSCard("rebel"); var other = scn.GetLSCard("otherRebel"); var droid = scn.GetLSCard("droid");
        scn.LSUseCardAction(scn.GetLSCard("leia"), COMPANION);
        assertTrue(scn.LSHasCardChoicesAvailable(rebel, other));
        assertFalse(scn.LSHasCardChoiceAvailable(droid));
        scn.LSChooseCard(rebel); scn.PassAllResponses();
        assertAtLocation(target, scn.GetLSCard("leia"), rebel);
        assertAtLocation(origin, other, droid);
    }

    @Test public void actualLandspeedReactMayDeclineTheCompanion() {
        var scn = react();
        scn.LSPass(); scn.PassAllResponses();
        assertAtLocation(scn.GetLSStartingLocation(), scn.GetLSCard("leia"));
        assertAtLocation(scn.GetLSCard("jungle"), scn.GetLSCard("rebel"), scn.GetLSCard("otherRebel"));
    }

    private void attrition(int amount, boolean immune) {
        var scn = scenario(); scn.StartGame(); var leia = scn.GetLSCard("leia"); var site = scn.GetLSStartingLocation();
        scn.MoveCardsToLocation(site, leia, scn.GetLSCard("jedi"), scn.GetDSCard("vader"));
        scn.AttachCardsTo(scn.GetLSCard("jedi"), scn.GetLSCard("saber"));
        scn.SkipToLSTurn(Phase.BATTLE); scn.LSInitiateBattle(site); scn.PassAllResponses();
        scn.PrepareLSDestiny(0); scn.PrepareDSDestiny(amount - 1);
        for (int i = 0; i < 4; i++) { scn.SkipToEndOfPowerSegment(true); scn.PassDestinyDrawResponses(); }
        scn.PassResponses("INITIAL_ATTRITION_CALCULATED"); scn.PassDamageSegmentActions();
        assertEquals(amount, scn.GetUnpaidLSAttrition()); assertEquals(0, scn.GetUnpaidLSBattleDamage());
        assertEquals(immune, scn.LSDecisionAvailable("(if desired)"));
        if (immune) { scn.LSChooseCards(); assertAtLocation(site, leia); }
        else { scn.LSPayAttritionFromCardInPlay(leia); scn.PassAllResponses(); assertInZone(Zone.LOST_PILE, leia); }
    }

    @Test public void leiaSurvivesActualAttritionTwo() { attrition(2, true); }
    @Test public void leiaMustForfeitAtActualAttritionThree() { attrition(3, false); }
}
