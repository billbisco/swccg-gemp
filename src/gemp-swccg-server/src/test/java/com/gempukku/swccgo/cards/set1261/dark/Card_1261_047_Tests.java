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

public class Card_1261_047_Tests {
    private static final String DEPLOY = "Deploy Imperial aboard your starship";

    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(
                new HashMap<>(Map.of("luke", "1_19", "han", "1_11", "obi", "1_21", "enemyShip", "1_146")),
                new HashMap<>(Map.ofEntries(
                        Map.entry("combo", "1261_47"), Map.entry("trooper", "1_194"),
                        Map.entry("crew", "1_166"), Map.entry("admiral", "1220_1"),
                        Map.entry("abilityThree", "1_178"), Map.entry("alien", "4_100"),
                        Map.entry("ship", "1_302"), Map.entry("system", "1_282"),
                        Map.entry("vader", "1_168"), Map.entry("blaster", "1_317"),
                        Map.entry("strike", "5_135"), Map.entry("bounty", "5_113"),
                        Map.entry("existingBounty", "5_113"))),
                24, 24, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void identityPreservesBothUniqueTitleComponentsAndHostIcon() {
        var scn = scenario();
        var card = scn.GetDSCard("combo").getBlueprint();
        assertEquals("We Have A Prisoner & I Can't Shake Him!", card.getTitle());
        assertEquals(List.of(new TitleComponent("We Have A Prisoner", Uniqueness.UNIQUE),
                new TitleComponent("I Can't Shake Him!", Uniqueness.UNIQUE)), card.getTitleComponents());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(CardSubtype.USED_OR_LOST, card.getCardSubtype());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        assertEquals(Side.DARK, card.getSide());
        assertEquals(Rarity.V, card.getRarity());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, card.getExpansionSet());
        assertFalse(card.hasVirtualSuffix());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>(List.of(CardType.INTERRUPT)));
        scn.BlueprintIconCheck(card, new ArrayList<>(List.of(Icon.INTERRUPT, Icon.A_NEW_HOPE, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(card, new ArrayList<>());
    }

    @Test
    public void usedDuringOpponentDeployPaysNormalCostAndReshuffles() {
        var scn = scenario();
        var combo = scn.GetDSCard("combo");
        var ship = scn.GetDSCard("ship");
        var crew = scn.GetDSCard("crew");
        var system = scn.GetDSCard("system");
        scn.StartGame();
        scn.MoveLocationToTable(system);
        scn.MoveCardsToLocation(system, ship);
        scn.MoveCardsToDSHand(combo);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(6);
        for (String key : List.of("crew", "abilityThree", "admiral", "alien"))
            scn.MoveCardsToTopOfOwnReserveDeck(scn.GetDSCard(key));
        if (scn.AwaitingLSDeployPhaseActions())
            scn.LSPass();
        assertTrue(scn.DSCardPlayAvailable(combo, DEPLOY));
        var revealed = scn.GetDSCard("abilityThree");
        assertTrue(scn.GetDSReserveDeck().contains(revealed));
        scn.gameState().markCardsRevealedFromPile(List.of(revealed));
        int before = scn.GetDSForcePileCount();
        scn.DSPlayCard(combo, DEPLOY);
        scn.PassAllResponses();
        assertTrue(scn.DSDecisionAvailable("Choose card to deploy"));
        assertTrue(selectable(scn, crew));
        assertFalse(selectable(scn, scn.GetDSCard("abilityThree")));
        assertFalse(selectable(scn, scn.GetDSCard("admiral")));
        assertFalse(selectable(scn, scn.GetDSCard("alien")));
        scn.DSChooseCard(crew);
        if (scn.DSHasCardChoiceAvailable(ship))
            scn.DSChooseCard(ship);
        scn.PassAllResponses();
        assertEquals("Aboard deployment must resolve; decision: " + scn.GetCurrentDecision(), ship, crew.getAttachedTo());
        assertEquals(before - 2, scn.GetDSForcePileCount());
        assertEquals(Zone.TOP_OF_USED_PILE, combo.getZone());
        assertTrue(scn.GetDSReserveDeck().contains(revealed));
        assertFalse(scn.gameState().isCardRevealedFromPile(revealed));
    }

    @Test
    public void ownDeployAndOpponentDeployWithoutOwnShipCannotUseDownload() {
        var scn = scenario();
        var combo = scn.GetDSCard("combo");
        var system = scn.GetDSCard("system");
        var ship = scn.GetDSCard("ship");
        scn.StartGame();
        scn.MoveCardsToDSHand(combo);
        scn.MoveLocationToTable(system);
        scn.MoveCardsToLocation(system, ship);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertFalse(scn.DSCardPlayAvailable(combo, DEPLOY));

        var other = scenario();
        var otherSystem = other.GetDSCard("system");
        other.StartGame();
        other.MoveCardsToDSHand(other.GetDSCard("combo"));
        other.MoveLocationToTable(otherSystem);
        other.MoveCardsToLocation(otherSystem, other.GetLSCard("enemyShip"));
        other.SkipToLSTurn(Phase.DEPLOY);
        if (other.AwaitingLSDeployPhaseActions())
            other.LSPass();
        assertTrue(other.AwaitingDSDeployPhaseActions());
        assertFalse(other.DSCardPlayAvailable(other.GetDSCard("combo"), DEPLOY));
    }

    @Test
    public void weaponLossIsReplacedByCaptureAndOnlyNewBountyOnThatCharacterIsBarred() {
        var scn = scenario();
        var site = scn.GetDSStartingLocation();
        var trooper = scn.GetDSCard("trooper");
        var luke = scn.GetLSCard("luke");
        var han = scn.GetLSCard("han");
        var combo = scn.GetDSCard("combo");
        var strike = scn.GetDSCard("strike");
        var blaster = scn.GetDSCard("blaster");
        var oldBounty = scn.GetDSCard("existingBounty");
        scn.StartGame();
        scn.MoveCardsToLocation(site, luke, han, scn.GetLSCard("obi"), scn.GetDSCard("vader"), trooper);
        scn.AttachCardsTo(trooper, blaster);
        scn.AttachCardsTo(luke, oldBounty);
        scn.MoveCardsToDSHand(combo, strike, scn.GetDSCard("bounty"));
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.EnsureLSForcePile(5);
        scn.EnsureDSForcePile(10);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        if (scn.AwaitingLSWeaponsSegmentActions())
            scn.LSPass();
        scn.PrepareDSDestiny(6);
        scn.DSUseCardAction(blaster, "Fire");
        scn.DSChooseCard(luke);
        scn.PassResponses("Fire ");
        scn.PassDestinyDrawResponses();
        scn.PassResponses("ABOUT_TO_BE_HIT");
        reachInterrupt(scn, strike, "Make");
        scn.DSPlayCard(strike, "Make");
        scn.DSChooseCard(luke);
        reachInterrupt(scn, combo, "Capture");
        int force = scn.GetDSForcePileCount();
        scn.DSPlayCard(combo, "Capture");
        finishCapture(scn, luke, trooper);
        scn.PassAllResponses();
        assertEquals(force - 1, scn.GetDSForcePileCount());
        assertTrue(scn.GetDSLostPile().contains(combo));
        assertSame(trooper, luke.getAttachedTo());
        assertSame(luke, oldBounty.getAttachedTo());
        assertEquals(7, scn.GetForfeit(luke));
        assertTrue(bountyProhibited(scn, luke));
        assertFalse(bountyProhibited(scn, han));
    }

    @Test
    public void forfeitStillPaysBattleAttritionBeforeCaptureAndRestrictionExpiresNextTurn() {
        var scn = scenario();
        var site = scn.GetDSStartingLocation();
        var luke = scn.GetLSCard("luke");
        var vader = scn.GetDSCard("vader");
        var combo = scn.GetDSCard("combo");
        scn.StartGame();
        scn.MoveCardsToLocation(site, luke, vader);
        scn.MoveCardsToDSHand(combo, scn.GetDSCard("bounty"));
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.EnsureDSForcePile(10);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        scn.PrepareDSDestiny(4);
        scn.PrepareLSDestiny(0);
        scn.SkipToDamageSegment(true);
        scn.PassResponses("Just drew");
        scn.PassDestinyDrawResponses();
        scn.PassAllResponses();
        scn.PassPowerSegmentActions();
        scn.PassAllResponses();
        scn.PassDamageSegmentActions();
        assertTrue("Expected the observed LS payment decision: " + scn.GetCurrentDecision(),
                scn.AwaitingLSAttritionPayment() || scn.AwaitingLSBattleDamagePayment());
        scn.LSChooseCard(luke);
        reachInterrupt(scn, combo, "Capture");
        scn.DSPlayCard(combo, "Capture");
        finishCapture(scn, luke, vader);
        scn.PassAllResponses();
        assertEquals(0, scn.GetUnpaidLSAttrition());
        assertTrue(bountyProhibited(scn, luke));
        for (int i = 0; i < 20 && scn.gameState().isDuringBattle(); i++) {
            scn.PassAllResponses();
            if (scn.AwaitingLSBattleDamagePayment())
                scn.LSPayBattleDamageFromReserveDeck();
            else if (scn.AwaitingDSBattleDamagePayment())
                scn.DSPayBattleDamageFromReserveDeck();
            else if (scn.AwaitingLSForceLossPayment())
                scn.LSPayRemainingForceLossFromReserveDeck();
            else if (scn.AwaitingDSForceLossPayment())
                scn.DSPayRemainingForceLossFromReserveDeck();
            else if (scn.gameState().isDuringBattle())
                passOptional(scn);
        }
        assertFalse("Finish the battle before changing turns", scn.gameState().isDuringBattle());
        String battleTurn = scn.GetCurrentPlayer();
        scn.SkipToPhase(Phase.DRAW);
        for (int i = 0; i < 12 && battleTurn.equals(scn.GetCurrentPlayer()); i++) {
            var decision = scn.GetCurrentDecision();
            assertNotNull("Turn ended without reaching the next player", decision);
            String prompt = decision.getText();
            if (prompt.contains("Do you want to Pass?"))
                scn.ChooseOption(scn.GetDecidingPlayer(), "Yes");
            else {
                assertTrue("Unexpected turn-end decision: " + prompt,
                        prompt.toLowerCase().contains("optional") || prompt.contains("action")
                                || prompt.toLowerCase().contains("draw"));
                scn.PlayerPass(scn.GetDecidingPlayer());
            }
        }
        assertNotEquals("The actual turn must end before checking expiry", battleTurn, scn.GetCurrentPlayer());
        assertFalse(bountyProhibited(scn, luke));
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

    private boolean bountyProhibited(VirtualTableScenario scn, PhysicalCardImpl target) {
        return scn.game().getModifiersQuerying().isProhibitedFromDeployingTo(scn.gameState(),
                scn.GetDSCard("bounty"), target, null);
    }

    private void reachInterrupt(VirtualTableScenario scn, PhysicalCardImpl card, String text) {
        for (int i = 0; i < 30; i++) {
            if (scn.DSDecisionAvailable("Optional response") && scn.DSCardPlayAvailable(card, text))
                return;
            if (scn.AwaitingDSForceLossPayment())
                scn.DSPayRemainingForceLossFromReserveDeck();
            else
                passOptional(scn);
        }
        fail("No live interrupt response: " + text);
    }

    private void finishCapture(VirtualTableScenario scn, PhysicalCardImpl captive, PhysicalCardImpl escort) {
        for (int i = 0; i < 30 && !captive.isCaptive(); i++) {
            if (scn.DSCaptureDecisionAvailable())
                scn.DSChooseSeizeCaptive();
            else if (scn.DSHasCardChoiceAvailable(escort))
                scn.DSChooseCard(escort);
            else if (scn.DSHasCardChoiceAvailable(captive))
                scn.DSChooseCard(captive);
            else
                passOptional(scn);
        }
        assertTrue(captive.isCaptive());
    }

    private void passOptional(VirtualTableScenario scn) {
        assertTrue("Expected an optional response: " + scn.GetCurrentDecision(),
                scn.DSDecisionAvailable("Optional response") || scn.LSDecisionAvailable("Optional response"));
        scn.PlayerPass(scn.GetDecidingPlayer());
    }
}
