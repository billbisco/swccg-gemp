package com.gempukku.swccgo.cards.set1261.dark;

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

public class Card_1261_049_Tests {
    private static final String USED = "Protect defending Imperial";
    private static final String DEPLOY = "Deploy Eriadu";
    private static final String RETRIEVE = "Retrieve Eriadu";

    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(
                new HashMap<>(Map.of("sense", "1_109", "luke", "1_19", "xwing", "1_146")),
                new HashMap<>(Map.of(
                        "orders", "1261_49", "tagge", "1_178", "veers", "3_87", "eriadu", "102_7",
                        "pilot", "1_173", "vader", "1_168", "alien", "1_171", "ship", "1_302")),
                24, 24, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void printedIdentity() {
        var scn = scenario();
        var card = scn.GetDSCard("orders").getBlueprint();
        assertEquals("Tarkin's Orders", card.getTitle());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(CardSubtype.USED_OR_LOST, card.getCardSubtype());
        assertEquals(Rarity.V, card.getRarity());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertTrue(card.hasVirtualSuffix());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, card.getExpansionSet());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>(List.of(CardType.INTERRUPT)));
        scn.BlueprintIconCheck(card, new ArrayList<>(List.of(Icon.INTERRUPT, Icon.PREMIUM, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(card, new ArrayList<>());
    }

    @Test
    public void usedProtectsOnlyTargetAndGrantsOneDestinyToAnOtherwiseIneligibleDefense() {
        var scn = scenario();
        var orders = scn.GetDSCard("orders");
        var tagge = scn.GetDSCard("tagge");
        var pilot = scn.GetDSCard("pilot");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, tagge, scn.GetLSCard("luke"));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), pilot);
        scn.MoveCardsToDSHand(orders);
        scn.MoveCardsToLSHand(scn.GetLSCard("sense"));
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.EnsureLSForcePile(5);
        scn.LSInitiateBattle(site);
        reachUsedResponse(scn, orders);
        assertEquals(0, scn.GetDSBattleDestinyCount());
        scn.DSPlayCard(orders, USED);
        if (scn.DSHasCardChoiceAvailable(tagge))
            scn.DSChooseCard(tagge);
        assertTrue("Used remains vulnerable to Sense", scn.LSCardPlayAvailable(scn.GetLSCard("sense"), "cancel"));
        scn.PassAllResponses();
        assertTrue(scn.game().getModifiersQuerying().isProhibitedFromHavingForfeitReduced(scn.gameState(), tagge));
        assertFalse(scn.game().getModifiersQuerying().isProhibitedFromHavingForfeitReduced(scn.gameState(), pilot));
        assertEquals(1, scn.GetDSBattleDestinyCount());
        assertEquals(Zone.TOP_OF_USED_PILE, orders.getZone());
    }

    @Test
    public void usedDoesNotAddAnotherDestinyWhenDefenseAlreadyQualifies() {
        var scn = scenario();
        var orders = scn.GetDSCard("orders");
        var tagge = scn.GetDSCard("tagge");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, tagge, scn.GetDSCard("pilot"), scn.GetLSCard("luke"));
        scn.MoveCardsToDSHand(orders);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.EnsureLSForcePile(5);
        scn.LSInitiateBattle(site);
        reachUsedResponse(scn, orders);
        int before = scn.GetDSBattleDestinyCount();
        assertTrue(before >= 1);
        scn.DSPlayCard(orders, USED);
        if (scn.DSHasCardChoiceAvailable(tagge))
            scn.DSChooseCard(tagge);
        scn.PassAllResponses();
        assertEquals(before, scn.GetDSBattleDestinyCount());
    }

    @Test
    public void ownAttackOrWrongAbilityCannotOfferUsedMode() {
        for (boolean ownAttack : List.of(false, true)) {
            var scn = scenario();
            var orders = scn.GetDSCard("orders");
            var site = scn.GetDSStartingLocation();
            scn.StartGame();
            scn.MoveCardsToLocation(site, scn.GetDSCard(ownAttack ? "tagge" : "pilot"), scn.GetLSCard("luke"));
            scn.MoveCardsToDSHand(orders);
            if (ownAttack) {
                scn.SkipToDSTurn(Phase.BATTLE);
                scn.EnsureDSForcePile(5);
                scn.DSInitiateBattle(site);
            } else {
                scn.SkipToLSTurn(Phase.BATTLE);
                scn.EnsureLSForcePile(5);
                scn.LSInitiateBattle(site);
            }
            for (int i = 0; i < 12 && (scn.DSDecisionAvailable("Optional response") || scn.LSDecisionAvailable("Optional response")); i++) {
                assertFalse(scn.DSCardPlayAvailable(orders, USED));
                scn.PlayerPass(scn.GetDecidingPlayer());
            }
            assertTrue(scn.gameState().isDuringBattle());
        }
    }

    @Test
    public void lostDeployUsesOnePlusNormalDeploymentAndCannotBeSensed() {
        var scn = scenario();
        var orders = scn.GetDSCard("orders");
        var tagge = scn.GetDSCard("tagge");
        var eriadu = scn.GetDSCard("eriadu");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToDSHand(orders);
        scn.MoveCardsToLSHand(scn.GetLSCard("sense"));
        scn.MoveCardsToLocation(site, scn.GetLSCard("luke"));
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.EnsureDSForcePile(8);
        assertFalse(scn.DSCardPlayAvailable(orders, DEPLOY));
        scn.SkipToPhase(Phase.DEPLOY);
        for (String key : List.of("tagge", "eriadu", "veers", "pilot", "vader", "alien"))
            scn.MoveCardsToTopOfOwnReserveDeck(scn.GetDSCard(key));
        scn.gameState().markCardsRevealedFromPile(List.of(eriadu));
        int before = scn.GetDSForcePileCount();
        scn.DSPlayCard(orders, DEPLOY);
        assertFalse(scn.LSCardPlayAvailable(scn.GetLSCard("sense"), "cancel"));
        scn.PassAllResponses();
        assertTrue(selectable(scn, tagge));
        assertTrue(selectable(scn, eriadu));
        for (String key : List.of("veers", "pilot", "vader", "alien"))
            assertFalse("Not a legal choice: " + key, selectable(scn, scn.GetDSCard(key)));
        scn.DSChooseCard(tagge);
        if (scn.DSHasCardChoiceAvailable(site))
            scn.DSChooseCard(site);
        scn.PassAllResponses();
        assertTrue(tagge.getZone().isInPlay());
        assertEquals(before - 4, scn.GetDSForcePileCount());
        assertTrue(scn.GetDSLostPile().contains(orders));
        assertTrue(scn.GetDSReserveDeck().contains(eriadu));
        assertFalse(scn.gameState().isCardRevealedFromPile(eriadu));
    }

    @Test
    public void lostRetrievalTakesEriaduFromLostPileOutsideDeployment() {
        var scn = scenario();
        var orders = scn.GetDSCard("orders");
        var eriadu = scn.GetDSCard("eriadu");
        var veers = scn.GetDSCard("veers");
        scn.StartGame();
        scn.MoveCardsToDSHand(orders);
        scn.MoveCardToZone(scn.DS, veers, Zone.LOST_PILE);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.EnsureDSForcePile(6);
        assertFalse(scn.DSCardPlayAvailable(orders, RETRIEVE));
        scn.MoveCardToZone(scn.DS, eriadu, Zone.LOST_PILE);
        scn.PassResponses("Control action");
        if (!scn.DSCardPlayAvailable(orders, RETRIEVE))
            scn.SkipToDSTurn(Phase.CONTROL);
        scn.EnsureDSForcePile(6);
        int before = scn.GetDSForcePileCount();
        assertTrue(scn.DSCardPlayAvailable(orders, RETRIEVE));
        scn.DSPlayCard(orders, RETRIEVE);
        scn.PassAllResponses();
        if (scn.DSHasCardChoiceAvailable(eriadu))
            scn.DSChooseCard(eriadu);
        scn.PassAllResponses();
        assertTrue(eriadu.getZone() == Zone.USED_PILE || eriadu.getZone() == Zone.TOP_OF_USED_PILE);
        assertTrue(scn.GetDSLostPile().contains(veers));
        assertEquals(before - 1, scn.GetDSForcePileCount());
        assertTrue(scn.GetDSLostPile().contains(orders));
    }

    private void reachUsedResponse(VirtualTableScenario scn, PhysicalCardImpl orders) {
        for (int i = 0; i < 10; i++) {
            if (scn.DSDecisionAvailable("Optional response") && scn.DSCardPlayAvailable(orders, USED))
                return;
            assertTrue("Expected a battle initiation response: " + scn.GetCurrentDecision(),
                    scn.LSDecisionAvailable("Optional response") || scn.DSDecisionAvailable("Optional response"));
            scn.PlayerPass(scn.GetDecidingPlayer());
        }
        fail("The Used action was absent from the live battle initiation");
    }

    private boolean selectable(VirtualTableScenario scn, PhysicalCardImpl card) {
        var ids = scn.DSGetADParamAsList("blueprintId");
        int index = ids != null ? ids.indexOf(card.getBlueprintId(true))
                : scn.DSGetADParamAsList("cardId").indexOf(Integer.toString(card.getCardId()));
        if (index < 0)
            return false;
        var flags = scn.DSGetADParamAsList("selectable");
        return flags == null || flags.isEmpty() || Boolean.parseBoolean(flags.get(index));
    }
}
