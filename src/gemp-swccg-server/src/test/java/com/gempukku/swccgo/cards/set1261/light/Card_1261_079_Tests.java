package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.cards.effects.CancelBattleEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class Card_1261_079_Tests {
    private VirtualTableScenario fixture() {
        var ls = new HashMap<>(Map.of("combo", "1261_79", "commander", "3_3", "luke", "108_3",
                "obi", "1_21", "han", "1_11", "ship", "1_146", "independent", "10_17",
                "space", "6_87", "ordinary", "1_66", "trooper", "1_28", "ordinaryLuke", "1_19"));
        var ds = new HashMap<>(Map.of("bad", "4_116", "vader", "1_168", "fighter", "1_299", "fighterPilot", "1_173", "alter", "1_234"));
        var scn = new VirtualTableScenario(ls, ds, 40, 40, StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation, StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts, StartingSetup.NoLSShields, StartingSetup.NoDSShields, "legacy");
        scn.StartGame();
        return scn;
    }

    @Test public void exactComboHasTwoUniqueTitlesAndOneDestiny() {
        var scn = fixture(); var card = scn.GetLSCard("combo"); var b = card.getBlueprint();
        assertEquals("Commando Training & K'lor'slug", b.getTitle());
        assertTrue(Filters.title(Title.Commando_Training).accepts(scn.game(), card));
        assertTrue(Filters.title(Title.Klorslug).accepts(scn.game(), card));
        assertEquals(1, b.getDestiny(), scn.epsilon); assertEquals(Uniqueness.UNIQUE, b.getUniqueness());
        assertFalse(b.hasVirtualSuffix()); assertEquals(Side.LIGHT, b.getSide()); assertEquals(Rarity.V, b.getRarity());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, b.getExpansionSet());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(List.of(CardType.EFFECT)));
        scn.BlueprintIconCheck(b, new ArrayList<>(List.of(Icon.EFFECT, Icon.ENDOR, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>());
    }

    @Test public void realCommanderDeploymentIgnoresHisOwnHothOnlyTextAndBadFeeling() {
        var scn = fixture(); var combo = scn.GetLSCard("combo"); var commander = scn.GetLSCard("commander");
        var bad = scn.GetDSCard("bad"); var luke = scn.GetLSCard("luke"); var obi = scn.GetLSCard("obi");
        scn.MoveCardsToLSSideOfTable(bad);
        scn.MoveCardsToLSHand(commander, luke, obi, combo);
        scn.LSActivateForceCheat(12); scn.SkipToLSTurn(Phase.DEPLOY);
        assertFalse(scn.LSCardPlayAvailable(commander));
        assertEquals(commander.getBlueprint().getDeployCost().intValue() + 2, scn.GetDeployCost(commander));
        scn.LSPlayCard(combo); scn.PassAllResponses();
        assertEquals(Zone.SIDE_OF_TABLE, combo.getZone());
        var mods = scn.game().getModifiersQuerying(); var site = scn.GetLSStartingLocation();
        assertTrue(mods.isImmuneToDeployCostToTargetModifierFromCard(scn.gameState(), commander, site, bad));
        assertFalse(mods.isImmuneToDeployCostToTargetModifierFromCard(scn.gameState(), luke, site, bad));
        assertFalse(mods.isImmuneToDeployCostToTargetModifierFromCard(scn.gameState(), obi, site, bad));
        assertEquals(luke.getBlueprint().getDeployCost().intValue() + 2, scn.GetDeployCost(luke));
        assertEquals(obi.getBlueprint().getDeployCost().intValue() + 2, scn.GetDeployCost(obi));
        if (scn.DSGetDecision() != null) scn.DSPass();
        assertTrue(scn.LSCardPlayAvailable(commander));
        int before = scn.GetLSForcePile().size();
        scn.LSPlayCard(commander); scn.PassForceUseResponses(); scn.PassAllResponses();
        if (scn.LSHasCardChoiceAvailable(scn.GetLSStartingLocation())) scn.LSChooseCard(scn.GetLSStartingLocation());
        scn.PassForceUseResponses(); scn.PassAllResponses();
        assertEquals(site, commander.getAtLocation());
        assertEquals(commander.getBlueprint().getDeployCost().intValue(), before - scn.GetLSForcePile().size());
    }

    @Test public void realAlterCannotTargetTheComboWhileAnotherEffectIsLegal() {
        var scn = fixture(); var combo = scn.GetLSCard("combo"); var ordinary = scn.GetLSCard("ordinary");
        var alter = scn.GetDSCard("alter");
        scn.MoveCardsToLSSideOfTable(combo, ordinary);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("vader"));
        scn.MoveCardsToDSHand(alter);
        scn.SkipToDSTurn(Phase.CONTROL); scn.EnsureDSForcePile(8);
        scn.DSPlayCard(alter, "Draw destiny to cancel Effect");
        assertFalse(scn.DSHasCardChoiceAvailable(combo));
        assertTrue(scn.DSHasCardChoiceAvailable(ordinary));
    }

    private void loseBeforeDamage(boolean inSpace, boolean independent) {
        var scn = fixture(); var combo = scn.GetLSCard("combo");
        var lost = scn.GetLSCard(inSpace ? (independent ? "independent" : "ship") : "han");
        var site = inSpace ? scn.GetLSCard("space") : scn.GetLSStartingLocation();
        var opponent = scn.GetDSCard(inSpace ? "fighter" : "vader");
        scn.MoveCardsToLSSideOfTable(combo);
        if (inSpace) scn.MoveLocationToTable(site);
        scn.MoveCardsToLocation(site, lost, opponent);
        if (inSpace) {
            scn.BoardAsPilot(lost, scn.GetLSCard("commander"));
            scn.BoardAsPilot(opponent, scn.GetDSCard("fighterPilot"));
        }
        scn.LSActivateForceCheat(8); scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSStartBattleAndSkipToWeaponsSegment(site);
        var action = new TopLevelGameTextAction(site, scn.LS, site.getCardId());
        action.appendEffect(new LoseCardFromTableEffect(action, lost));
        scn.carryOutEffectInPhaseActionByPlayer(scn.LS, action);
        scn.PassAllResponses();
        if (independent) {
            finishOrderedLosses(scn, lost);
            assertTrue(scn.GetLSLostPile().contains(lost));
        } else {
            assertTrue(lost.getZone().isInPlay());
            assertEquals(0, scn.game().getModifiersQuerying().getForfeitWhenForfeiting(scn.gameState(), lost), scn.epsilon);
            if (scn.DSGetDecision() != null) scn.DSPass();
            var cancel = new TopLevelGameTextAction(site, scn.LS, site.getCardId());
            cancel.appendEffect(new CancelBattleEffect(cancel));
            scn.carryOutEffectInPhaseActionByPlayer(scn.LS, cancel);
            finishOrderedLosses(scn, lost);
            assertTrue(scn.GetLSLostPile().contains(lost));
        }
    }

    private void finishOrderedLosses(VirtualTableScenario scn, com.gempukku.swccgo.game.PhysicalCardImpl lost) {
        for (int i = 0; i < 8; i++) {
            scn.PassAllResponses();
            if (!scn.LSDecisionAvailable("Choose card to be lost")) return;
            var pilot = scn.GetLSCard("commander");
            if (scn.LSHasCardChoiceAvailable(pilot)) scn.LSChooseCard(pilot);
            else if (scn.LSHasCardChoiceAvailable(lost)) scn.LSChooseCard(lost);
            else return;
        }
    }

    @Test public void participatingCharacterLossWaitsUntilBattleEnds() { loseBeforeDamage(false, false); }
    @Test public void participatingNonIndependentStarshipLossWaitsUntilBattleEnds() { loseBeforeDamage(true, false); }
    @Test public void independentStarshipIsLostImmediately() { loseBeforeDamage(true, true); }
    private void normalBattle(boolean chooseDeferredCharacterForAttrition) {
        var scn = fixture(); var combo = scn.GetLSCard("combo"); var han = scn.GetLSCard("han");
        var site = scn.GetLSStartingLocation(); var trooper = scn.GetLSCard("trooper");
        scn.MoveCardsToLSSideOfTable(combo);
        scn.MoveCardsToLocation(site, han, trooper, scn.GetLSCard("ordinaryLuke"), scn.GetDSCard("vader"));
        scn.LSActivateForceCheat(8); scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSStartBattleAndSkipToWeaponsSegment(site);
        var loss = new TopLevelGameTextAction(site, scn.LS, site.getCardId());
        loss.appendEffect(new LoseCardFromTableEffect(loss, han));
        scn.carryOutEffectInPhaseActionByPlayer(scn.LS, loss);
        scn.PassAllResponses();
        assertTrue(han.getZone().isInPlay());
        scn.PrepareLSDestiny(0); scn.PrepareDSDestiny(0);
        scn.SkipToDamageSegment(true);
        scn.PassDamageSegmentActions();
        assertEquals(1, scn.GetUnpaidLSAttrition());
        assertEquals(0, scn.GetUnpaidLSBattleDamage());
        if (chooseDeferredCharacterForAttrition) {
            scn.LSPayAttritionFromCardInPlay(han);
            assertTrue(scn.GetLSLostPile().contains(han));
            assertEquals(1, scn.GetUnpaidLSAttrition());
            assertTrue(scn.AwaitingLSAttritionPayment());
        } else {
            scn.LSPayAttritionFromCardInPlay(trooper);
            scn.PassAllResponses();
            assertTrue(scn.GetLSLostPile().contains(trooper));
            assertTrue(scn.GetLSLostPile().contains(han));
            assertFalse(scn.gameState().isDuringBattle());
        }
    }

    @Test public void deferredCharacterMayForfeitButPaysZeroActualAttrition() { normalBattle(true); }
    @Test public void deferredCharacterIsLostAtTheEndOfAnUncancelledBattle() { normalBattle(false); }
}
