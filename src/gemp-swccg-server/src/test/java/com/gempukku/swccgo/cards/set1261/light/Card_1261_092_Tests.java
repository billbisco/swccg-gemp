package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class Card_1261_092_Tests {
    private static final String DEPLOY = "Deploy a Rebel from Reserve Deck aboard your starship";
    private static final String RETRIEVE = "Draw destiny to retrieve Rebels";

    private VirtualTableScenario setup() {
        var scn = new VirtualTableScenario(new HashMap<>(Map.ofEntries(
                Map.entry("combo", "1261_92"), Map.entry("other", "1210_160"),
                Map.entry("trooper", "2_18"), Map.entry("red", "1_144"),
                Map.entry("home", "9_74"), Map.entry("admiral", "203_9"),
                Map.entry("leia", "1_17"), Map.entry("nonrebel", "1_66"),
                Map.entry("oldUsed", "1_146"), Map.entry("site", "1_136"))),
                new HashMap<>(Map.of("damage", "1_222", "sense", "1_267",
                        "vader", "1_168", "tie", "1_304", "system", "1_282")),
                45, 45, StartingSetup.DefaultLSSpaceSystem, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        scn.StartGame();
        return scn;
    }

    private boolean available(VirtualTableScenario scn, String action) {
        return scn.GetLSAvailableActions().stream().anyMatch(x -> x.contains(action));
    }

    private void passResponsesUntil(VirtualTableScenario scn, java.util.function.BooleanSupplier ready) {
        for (int i = 0; i < 18 && !ready.getAsBoolean(); i++) {
            var decision = scn.GetCurrentDecision();
            if (decision == null || !decision.getText().toLowerCase().contains("response")) return;
            scn.PlayerPass(scn.LSGetDecision() != null ? scn.LS : scn.DS);
        }
    }

    private boolean selectable(VirtualTableScenario scn, PhysicalCardImpl card) {
        if (scn.LSGetDecision() == null) return false;
        var blueprints = scn.LSGetADParam("blueprintId");
        var enabled = scn.LSGetADParam("selectable");
        if (blueprints != null && enabled != null) {
            for (int i = 0; i < Math.min(blueprints.length, enabled.length); i++) {
                if (card.getBlueprintId(true).equals(blueprints[i]) && Boolean.parseBoolean(enabled[i])) return true;
            }
            return false;
        }
        return scn.LSHasCardChoiceAvailable(card);
    }

    @Test public void finalComboHasBothUniqueTitlesAndNoOriginalVirtualSuffix() {
        var scn = setup(); var b = scn.GetLSCard("combo").getBlueprint();
        assertEquals("Antilles Maneuver & Rebel Reinforcements", b.getTitle());
        assertEquals(5, b.getDestiny(), scn.epsilon);
        assertEquals(Side.LIGHT, b.getSide()); assertEquals(Rarity.V, b.getRarity());
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness());
        assertEquals(CardSubtype.USED_OR_LOST, b.getCardSubtype());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, b.getExpansionSet());
        assertFalse(b.hasVirtualSuffix());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(List.of(CardType.INTERRUPT)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>());
        scn.BlueprintIconCheck(b, new ArrayList<>(List.of(Icon.INTERRUPT, Icon.SPECIAL_EDITION, Icon.LEGACY_BLOCK_6)));
    }

    @Test public void deployModeCostsForceAndExcludesAbilityThreeAndAbilityTwoAdmiral() {
        var scn = setup();
        var home = scn.GetLSCard("home"); var combo = scn.GetLSCard("combo");
        var trooper = scn.GetLSCard("trooper"); var leia = scn.GetLSCard("leia");
        var admiral = scn.GetLSCard("admiral");
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), home, scn.GetDSCard("tie"));
        scn.MoveCardsToLSHand(combo);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertFalse(available(scn, DEPLOY));
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.LSActivateForceCheat(5);
        scn.MoveCardsToTopOfLSReserveDeck(trooper, leia, admiral);
        var marker = scn.GetLSCard("nonrebel");
        scn.MoveCardsToTopOfLSReserveDeck(marker);
        scn.gameState().markCardsRevealedFromPile(List.of(marker));
        if (scn.DSGetDecision() != null) scn.DSPass();
        assertTrue(available(scn, DEPLOY));
        int before = scn.GetLSForcePileCount();
        scn.LSPlayCard(combo, DEPLOY);
        scn.PassAllResponses();
        assertTrue(selectable(scn, trooper));
        assertFalse(selectable(scn, leia));
        assertFalse(selectable(scn, admiral));
        scn.LSChooseCard(trooper);
        if (scn.LSHasCardChoiceAvailable(home)) scn.LSChooseCard(home);
        if (scn.LSGetMultipleChoices() != null && scn.LSGetMultipleChoices().contains("Passenger")) scn.LSChoose("Passenger");
        scn.PassForceUseResponses();
        scn.PassAllResponses();
        assertEquals("Pending after deploy: " + scn.GetCurrentDecision(), home, trooper.getAttachedTo());
        assertEquals(before - 2, scn.GetLSForcePileCount());
        assertFalse(scn.gameState().isCardRevealedFromPile(marker));
        assertEquals(Zone.RESERVE_DECK, admiral.getZone());
        assertEquals(Zone.RESERVE_DECK, leia.getZone());
    }

    private VirtualTableScenario lateralDamage(boolean holdCombo) {
        var scn = setup(); var ship = scn.GetLSCard("home"); var dsSystem = scn.GetDSCard("system");
        scn.MoveLocationToTable(dsSystem);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), ship);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("vader"));
        scn.MoveCardsToDSHand(scn.GetDSCard("damage"), scn.GetDSCard("sense"));
        if (holdCombo) scn.MoveCardsToLSHand(scn.GetLSCard("combo"));
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSActivateForceCheat(8);
        scn.DSPlayCard(scn.GetDSCard("damage"));
        if (scn.DSHasCardChoiceAvailable(dsSystem)) scn.DSChooseCard(dsSystem);
        if (scn.DSHasCardChoiceAvailable(ship)) scn.DSChooseCard(ship);
        return scn;
    }

    @Test public void usedCancelStopsActualLateralDamageWhileItIsBeingPlayed() {
        var scn = lateralDamage(true);
        passResponsesUntil(scn, () -> available(scn, "Cancel Lateral Damage"));
        assertTrue(available(scn, "Cancel Lateral Damage"));
        scn.LSPlayCard(scn.GetLSCard("combo"), "Cancel Lateral Damage");
        assertFalse(scn.GetDSAvailableActions().stream().anyMatch(x -> x.contains("Draw destiny to cancel")));
        scn.PassAllResponses();
        assertFalse(scn.GetDSCard("damage").getZone().isInPlay());
        assertTrue(scn.GetPower(scn.GetLSCard("home")) > 0);
    }

    @Test public void usedCancelAlsoRemovesResolvedLateralDamageAndRestoresStats() {
        var scn = lateralDamage(false); var home = scn.GetLSCard("home");
        scn.PassAllResponses();
        assertEquals(0, scn.GetPower(home)); assertEquals(0, scn.GetForfeit(home));
        scn.MoveCardsToLSHand(scn.GetLSCard("combo"));
        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.GetLSAvailableActions().toString(), available(scn, "Cancel Lateral Damage"));
        scn.LSPlayCard(scn.GetLSCard("combo"), "Cancel Lateral Damage");
        if (scn.LSHasCardChoiceAvailable(scn.GetDSCard("damage"))) scn.LSChooseCard(scn.GetDSCard("damage"));
        scn.PassAllResponses();
        assertFalse(scn.GetDSCard("damage").getZone().isInPlay());
        assertTrue(scn.GetPower(home) > 0); assertTrue(scn.GetForfeit(home) > 0);
    }

    private VirtualTableScenario retrieval(boolean tied) {
        var scn = setup();
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("vader"));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetDSCard("tie"));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("home"));
        if (tied) scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetLSCard("leia"));
        scn.MoveCardsToLSHand(scn.GetLSCard("combo"), scn.GetLSCard("other"));
        scn.MoveCardsToTopOfLSLostPile(scn.GetLSCard("nonrebel"), scn.GetLSCard("trooper"), scn.GetLSCard("red"));
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfLSUsedPile(scn.GetLSCard("oldUsed"));
        return scn;
    }

    @Test public void permanentPilotCountsButEqualAbilityCardCountsDoNotQualify() {
        assertFalse(available(retrieval(true), RETRIEVE));
        assertTrue(available(retrieval(false), RETRIEVE));
    }

    private void resolveRetrieval(VirtualTableScenario scn, int amount, boolean takeOne) {
        var combo = scn.GetLSCard("combo"); var trooper = scn.GetLSCard("trooper");
        var red = scn.GetLSCard("red"); var other = scn.GetLSCard("nonrebel");
        scn.PrepareLSDestiny(2);
        scn.LSPlayCard(combo, RETRIEVE);
        scn.PassAllResponses();
        scn.PassDestinyDrawResponses();
        boolean handChoice = false;
        var trace = new ArrayList<String>();
        for (int i = 0; i < 30; i++) {
            var d = scn.GetCurrentDecision();
            trace.add(d == null ? "none" : d.getText());
            if (scn.LSDecisionAvailable("Choose a retrieved")) {
                assertTrue(selectable(scn, trooper));
                assertEquals(amount == 2, selectable(scn, red));
                assertFalse(selectable(scn, scn.GetLSCard("oldUsed")));
                if (takeOne) scn.LSChooseCard(trooper); else scn.LSDecided("");
                handChoice = true;
                continue;
            }
            if (scn.LSDecisionAvailable("Choose value for X")) {
                scn.LSDecided(amount); continue;
            }
            if (scn.LSDecisionAvailable("Viewing") || scn.LSDecisionAvailable("prior to choosing")) {
                scn.LSDecided(""); continue;
            }
            if (selectable(scn, trooper)) {
                assertFalse(selectable(scn, other));
                scn.LSChooseCard(trooper); continue;
            }
            if (selectable(scn, red)) {
                assertFalse(selectable(scn, other));
                scn.LSChooseCard(red); continue;
            }
            if (combo.getZone() == Zone.LOST_PILE || combo.getZone() == Zone.TOP_OF_LOST_PILE) break;
            if (d == null) break;
            String prompt = d.getText().toLowerCase();
            if (prompt.contains("optional") || prompt.contains("or pass") || prompt.contains("response")) {
                scn.PlayerPass(scn.LSGetDecision() != null ? scn.LS : scn.DS);
                continue;
            }
            break;
        }
        assertTrue("The actual retrieval must offer the optional hand selection: " + trace, handChoice);
        if (takeOne) assertEquals(Zone.HAND, trooper.getZone());
        else assertTrue(scn.GetLSUsedPile().contains(trooper));
        if (amount == 2) assertTrue(scn.GetLSUsedPile().contains(red));
        else assertTrue(scn.GetLSLostPile().contains(red));
        assertTrue(scn.GetLSLostPile().contains(other));
        assertTrue(scn.GetLSLostPile().contains(combo));
        assertFalse(available(scn, RETRIEVE));
    }

    @Test public void realSenseCanRespondToTheLostModeButNotTheCancellationMode() {
        var vulnerable = retrieval(false);
        vulnerable.MoveCardsToDSHand(vulnerable.GetDSCard("sense"));
        vulnerable.LSPlayCard(vulnerable.GetLSCard("combo"), RETRIEVE);
        passResponsesUntil(vulnerable, () -> vulnerable.GetDSAvailableActions().stream().anyMatch(x -> x.contains("Draw destiny to cancel")));
        assertTrue(vulnerable.GetDSAvailableActions().toString(), vulnerable.DSCardPlayAvailable(vulnerable.GetDSCard("sense")));
        vulnerable.PrepareDSDestiny(1);
        vulnerable.DSPlayCard(vulnerable.GetDSCard("sense"), "Draw destiny to cancel");
        if (vulnerable.DSHasCardChoiceAvailable(vulnerable.GetDSCard("vader"))) vulnerable.DSChooseCard(vulnerable.GetDSCard("vader"));
        vulnerable.PassAllResponses(); vulnerable.PassDestinyDrawResponses(); vulnerable.PassAllResponses();
        assertTrue(vulnerable.GetLSLostPile().contains(vulnerable.GetLSCard("red")));
        assertTrue(vulnerable.GetLSLostPile().contains(vulnerable.GetLSCard("trooper")));
    }

    @Test public void zeroDestinyDoesNotRetrieveAndStillConsumesTheGameUse() {
        var scn = retrieval(false);
        scn.PrepareLSDestiny(0);
        scn.LSPlayCard(scn.GetLSCard("combo"), RETRIEVE);
        scn.PassAllResponses(); scn.PassDestinyDrawResponses(); scn.PassAllResponses();
        assertTrue(scn.GetLSLostPile().contains(scn.GetLSCard("red")));
        assertTrue(scn.GetLSLostPile().contains(scn.GetLSCard("trooper")));
        assertTrue(scn.GetLSLostPile().contains(scn.GetLSCard("combo")));
        assertFalse(available(scn, RETRIEVE));
    }


    @Test public void retrieveBothRealCardKindsAndTakeOnlyOneIntoHand() {
        resolveRetrieval(retrieval(false), 2, true);
    }

    @Test public void destinyTwoAllowsRetrievingOnlyOneAndDecliningTheHandChoice() {
        resolveRetrieval(retrieval(false), 1, false);
    }
}
