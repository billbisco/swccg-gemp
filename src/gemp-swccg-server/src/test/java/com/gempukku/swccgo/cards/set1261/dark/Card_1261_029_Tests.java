package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.actions.SystemQueueAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class Card_1261_029_Tests {
    private static final String SEARCH = "Take location Effect into hand";

    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(
                new HashMap<>(Map.of("goo", "7_62", "luke", "1_19", "xwing", "1_146", "ywing", "1_147")),
                new HashMap<>(Map.ofEntries(
                        Map.entry("combo", "1261_29"), Map.entry("crossfire", "1230_26"),
                        Map.entry("regularCrossfire", "8_121"), Map.entry("locationEffect", "1_227"),
                        Map.entry("siteEffect", "1_213"), Map.entry("expand", "1_215"),
                        Map.entry("ordinaryEffect", "1_225"), Map.entry("device", "1_202"),
                        Map.entry("tagge", "1_178"), Map.entry("vader", "1_168"),
                        Map.entry("maintenance", "13_59"), Map.entry("permanentWeapon", "108_5"),
                        Map.entry("trooper", "1_194"), Map.entry("devastator", "1_301"),
                        Map.entry("destroyer", "1_302"), Map.entry("independent", "4_168"),
                        Map.entry("system", "1_282"))),
                32, 32, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void printedIdentityAndBothImmunities() {
        var scn = scenario();
        var combo = scn.GetDSCard("combo");
        var bp = combo.getBlueprint();
        assertEquals("Breached Defenses & Molator", bp.getTitle());
        assertEquals(List.of(new TitleComponent("Breached Defenses", Uniqueness.UNIQUE),
                new TitleComponent("Molator", Uniqueness.UNIQUE)), bp.getTitleComponents());
        assertEquals(1, bp.getDestiny(), scn.epsilon);
        assertEquals(Uniqueness.UNIQUE, bp.getUniqueness());
        assertEquals(CardSubtype.NORMAL, bp.getCardSubtype());
        assertEquals(Side.DARK, bp.getSide());
        assertEquals(Rarity.V, bp.getRarity());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, bp.getExpansionSet());
        assertFalse(bp.hasVirtualSuffix());
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>(List.of(CardType.EFFECT)));
        scn.BlueprintIconCheck(bp, new ArrayList<>(List.of(Icon.EFFECT, Icon.HOTH, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(bp, new ArrayList<>());
        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(combo);
        assertTrue(scn.game().getModifiersQuerying().mayNotBeCanceled(scn.gameState(), combo));
        assertTrue(scn.game().getModifiersQuerying().isImmuneToCardTitle(scn.gameState(), combo, Title.Alter));
    }

    @Test
    public void searchPaysThreeAndReshufflesOnlyOnceWithRealLocationChoices() {
        var scn = scenario();
        var combo = scn.GetDSCard("combo");
        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(combo, scn.GetDSCard("regularCrossfire"));
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.EnsureDSForcePile(8);
        for (String key : List.of("locationEffect", "siteEffect", "expand", "ordinaryEffect", "device"))
            scn.MoveCardsToTopOfOwnReserveDeck(scn.GetDSCard(key));
        var locationEffect = scn.GetDSCard("locationEffect");
        var siteEffect = scn.GetDSCard("siteEffect");
        scn.gameState().markCardsRevealedFromPile(List.of(locationEffect));
        int force = scn.GetDSForcePileCount();
        assertTrue(scn.DSCardActionAvailable(combo, SEARCH));
        scn.DSUseCardAction(combo, SEARCH);
        scn.PassAllResponses();
        assertTrue(scn.DSHasCardChoiceAvailable(locationEffect));
        assertTrue(scn.DSHasCardChoiceAvailable(siteEffect));
        assertFalse(scn.DSHasCardChoiceAvailable(scn.GetDSCard("expand")));
        assertFalse(scn.DSHasCardChoiceAvailable(scn.GetDSCard("ordinaryEffect")));
        assertFalse(scn.DSHasCardChoiceAvailable(scn.GetDSCard("device")));
        scn.DSChooseCard(siteEffect);
        scn.PassAllResponses();
        assertEquals(Zone.HAND, siteEffect.getZone());
        assertEquals(force - 3, scn.GetDSForcePileCount());
        assertTrue(scn.GetDSReserveDeck().contains(locationEffect));
        assertFalse(scn.gameState().isCardRevealedFromPile(locationEffect));
        assertFalse(scn.DSCardActionAvailable(combo, SEARCH));
        assertTrue(GameTextActionId.LEGACY__BREACHED_DEFENSES_MOLATOR__TAKE_EFFECT.isSearchCardPile());
        assertTrue(GameTextActionId.LEGACY__BREACHED_DEFENSES_MOLATOR__TAKE_EFFECT.isPerGame());
    }

    @Test
    public void virtualCrossfireOnTableOrOutOfPlayBlocksButLostDoesNot() {
        for (Zone zone : List.of(Zone.SIDE_OF_TABLE, Zone.OUT_OF_PLAY, Zone.LOST_PILE)) {
            var scn = scenario();
            var combo = scn.GetDSCard("combo");
            var crossfire = scn.GetDSCard("crossfire");
            scn.StartGame();
            scn.MoveCardsToDSSideOfTable(combo);
            scn.MoveCardToZone(scn.DS, crossfire, zone);
            scn.SkipToDSTurn(Phase.CONTROL);
            scn.EnsureDSForcePile(8);
            assertEquals("Crossfire zone " + zone, zone == Zone.LOST_PILE,
                    scn.DSCardActionAvailable(combo, SEARCH));
        }
    }

    @Test
    public void immunityCancelsActualGooNeeTaySurchargeOnlyForEligibleCharacters() {
        var scn = scenario();
        var combo = scn.GetDSCard("combo");
        var tagge = scn.GetDSCard("tagge");
        var vader = scn.GetDSCard("vader");
        var maintenance = scn.GetDSCard("maintenance");
        var permanent = scn.GetDSCard("permanentWeapon");
        scn.StartGame();
        scn.MoveCardsToDSHand(tagge, vader, maintenance, permanent);
        scn.MoveCardsToLSSideOfTable(scn.GetLSCard("goo"));
        assertEquals(5, scn.GetDeployCost(tagge));
        scn.MoveCardsToDSSideOfTable(combo);
        assertEquals(3, scn.GetDeployCost(tagge));
        assertEquals(8, scn.GetDeployCost(vader));
        assertEquals(7, scn.GetDeployCost(maintenance));
        assertEquals(7, scn.GetDeployCost(permanent));
    }

    @Test
    public void characterLossBeforeDamageIsDeferredWithZeroForfeitAndSurvivesSourceLeaving() {
        var scn = scenario();
        var combo = scn.GetDSCard("combo");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(combo);
        scn.MoveCardsToLocation(site, trooper, scn.GetDSCard("vader"), scn.GetLSCard("luke"));
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.EnsureDSForcePile(6);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        loseAtCurrentBattleWindow(scn, trooper);
        assertFalse(scn.GetDSLostPile().contains(trooper));
        assertEquals(0, scn.game().getModifiersQuerying().getForfeitWhenForfeiting(scn.gameState(), trooper), scn.epsilon);
        scn.MoveCardToZone(scn.DS, combo, Zone.LOST_PILE);
        finishBattle(scn);
        assertTrue(scn.GetDSLostPile().contains(trooper));
    }

    @Test
    public void defersYourNonIndependentShipButNotIndependentOrOpposingShips() {
        var scn = scenario();
        var system = scn.GetDSCard("system");
        var own = scn.GetDSCard("devastator");
        var independent = scn.GetDSCard("independent");
        var enemy = scn.GetLSCard("xwing");
        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(scn.GetDSCard("combo"));
        scn.MoveLocationToTable(system);
        scn.MoveCardsToLocation(system, own, independent, scn.GetDSCard("destroyer"), enemy, scn.GetLSCard("ywing"));
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.EnsureDSForcePile(6);
        scn.DSInitiateBattle(system);
        scn.PassAllResponses();
        loseAtCurrentBattleWindow(scn, own);
        assertFalse(scn.GetDSLostPile().contains(own));
        assertEquals(0, scn.game().getModifiersQuerying().getForfeitWhenForfeiting(scn.gameState(), own), scn.epsilon);
        loseAtCurrentBattleWindow(scn, independent);
        assertTrue(scn.GetDSLostPile().contains(independent));
        loseAtCurrentBattleWindow(scn, enemy);
        assertTrue(scn.GetLSLostPile().contains(enemy));
        finishBattle(scn);
        assertTrue(scn.GetDSLostPile().contains(own));
    }

    private void loseAtCurrentBattleWindow(VirtualTableScenario scn, PhysicalCardImpl card) {
        assertTrue("Loss must be injected before the damage segment", scn.gameState().isDuringBattle()
                && !scn.gameState().getBattleState().isReachedDamageSegment());
        var action = new SystemQueueAction();
        action.setPerformingPlayer(scn.GetCurrentPlayer());
        action.appendEffect(new LoseCardFromTableEffect(action, card, true));
        scn.game().getActionsEnvironment().addActionToStack(action);
        if (scn.GetCurrentDecision() != null)
            scn.PlayerPass(scn.GetDecidingPlayer());
        else
            scn.game().carryOutPendingActionsUntilDecisionNeeded();
        scn.PassAllResponses();
    }

    private void finishBattle(VirtualTableScenario scn) {
        for (int i = 0; i < 45 && scn.gameState().isDuringBattle(); i++) {
            scn.PassAllResponses();
            if (!scn.gameState().isDuringBattle())
                break;
            if (scn.AwaitingLSAttritionPayment())
                scn.LSChooseCard(scn.GetLSCard("luke").getZone().isInPlay() ? scn.GetLSCard("luke") : scn.GetLSCard("ywing"));
            else if (scn.AwaitingDSAttritionPayment())
                scn.DSChooseCard(scn.GetDSCard("vader").getZone().isInPlay() ? scn.GetDSCard("vader") : scn.GetDSCard("destroyer"));
            else if (scn.AwaitingLSBattleDamagePayment())
                scn.LSPayBattleDamageFromReserveDeck();
            else if (scn.AwaitingDSBattleDamagePayment())
                scn.DSPayBattleDamageFromReserveDeck();
            else if (scn.LSDecisionAvailable("battle destiny?"))
                scn.LSChooseNo();
            else if (scn.DSDecisionAvailable("battle destiny?"))
                scn.DSChooseNo();
            else
                scn.PlayerPass(scn.GetDecidingPlayer());
        }
        scn.PassAllResponses();
        assertFalse("Battle must end", scn.gameState().isDuringBattle());
    }
}
