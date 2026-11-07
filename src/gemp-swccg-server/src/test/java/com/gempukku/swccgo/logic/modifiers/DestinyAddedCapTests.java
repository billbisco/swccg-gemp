package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.state.BattleState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Engine tests for "may not add more than X destiny to power / attrition" caps
 * (MayNotAddMoreThanDestinyToPowerModifier, MayNotAddMoreThanDestinyToAttritionModifier) and the per-source bonus
 * on a destiny drawn to power (NumDestinyDrawsToPowerOnlyModifier.setBonusForEachDestinyDraw).
 */
public class DestinyAddedCapTests {

    private VirtualTableScenario battle() {
        var scn = new VirtualTableScenario(new HashMap<>(Map.of("han", "108_1", "luke", "1_19")),
                new HashMap<>(Map.of("ig88", "109_11", "bossk", "4_92")),
                42, 42, StartingSetup.LSStartingLocation("1_136"), StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, "legacy");
        scn.StartGame();
        var site = scn.GetLSStartingLocation();
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToLocation(site, scn.GetLSCard("luke"), scn.GetLSCard("han"));
        scn.MoveCardsToLocation(site, scn.GetDSCard("ig88"), scn.GetDSCard("bossk"));
        scn.LSActivateForceCheat(4);
        scn.SkipToPhase(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        assertNotNull(scn.gameState().getBattleState());
        return scn;
    }

    private void add(VirtualTableScenario scn, Modifier modifier) {
        scn.game().getModifiersEnvironment().addUntilEndOfBattleModifier(modifier);
    }

    private Modifier powerSources(VirtualTableScenario scn, String player, int count) {
        return new AddsDestinyToPowerModifier(scn.GetLSCard("luke"), count, player, true);
    }

    /**
     * Passes through the battle, drawing (or not) battle destiny, until the damage segment is reached or the battle
     * ends after its power segment (for example in a tie).
     * @return the battle's state, which keeps the destiny added to power and attrition after the battle ends
     */
    public static BattleState advanceToDamageSegment(VirtualTableScenario scn, boolean drawBattleDestiny) {
        BattleState last = null;
        for (int i = 0; i < 120; i++) {
            BattleState battleState = scn.gameState().getBattleState();
            if (battleState == null) {
                if (last != null && last.isReachedPowerSegment())
                    return last;
                fail("Battle ended before the power segment: " + scn.gameState().getLastMessages());
            }
            last = battleState;
            if (battleState.isReachedDamageSegment())
                return battleState;
            String player = scn.GetDecidingPlayer();
            if (scn.DecisionAvailable(player, "battle destiny?")) {
                if (drawBattleDestiny) scn.PlayerChooseYes(player); else scn.PlayerChooseNo(player);
            } else {
                scn.PlayerPass(player);
            }
        }
        fail("Did not reach the damage segment");
        return null;
    }

    private BattleState state;

    private void advance(VirtualTableScenario scn, boolean drawBattleDestiny) {
        state = advanceToDamageSegment(scn, drawBattleDestiny);
    }

    private int drawnToPower(VirtualTableScenario scn, String player) {
        return state.getNumDestinyToTotalPowerDrawn(player);
    }

    private float totalToPower(VirtualTableScenario scn, String player) {
        return state.getTotalDestinyToPowerOnly(player);
    }

    @Test public void withoutCapEverySourceDrawsItsDestinyToPower() {
        var scn = battle();
        add(scn, powerSources(scn, scn.LS, 3));
        scn.PrepareLSDestiny(1); scn.PrepareLSDestiny(2); scn.PrepareLSDestiny(4);
        advance(scn, false);
        assertEquals(3, drawnToPower(scn, scn.LS));
        assertEquals(7, totalToPower(scn, scn.LS), scn.epsilon);
    }

    @Test public void capOfTwoLimitsThreeAddedDestinyToTwo() {
        var scn = battle();
        add(scn, powerSources(scn, scn.LS, 3));
        add(scn, new MayNotAddMoreThanDestinyToPowerModifier(scn.GetLSCard("luke"), null, 2, null));
        assertEquals(Integer.valueOf(2), scn.game().getModifiersQuerying().getMaxDestinyAddedToPower(scn.gameState(), scn.LS));
        scn.PrepareLSDestiny(1); scn.PrepareLSDestiny(2); scn.PrepareLSDestiny(4);
        advance(scn, false);
        assertEquals(2, drawnToPower(scn, scn.LS));
        assertEquals(6, totalToPower(scn, scn.LS), scn.epsilon);
    }

    @Test public void capOfTwoDoesNotReduceExactlyTwoAddedDestiny() {
        var scn = battle();
        add(scn, powerSources(scn, scn.LS, 2));
        add(scn, new MayNotAddMoreThanDestinyToPowerModifier(scn.GetLSCard("luke"), null, 2, null));
        advance(scn, false);
        assertEquals(2, drawnToPower(scn, scn.LS));
    }

    @Test public void capIsPlayerScoped() {
        var scn = battle();
        add(scn, powerSources(scn, scn.LS, 3));
        add(scn, powerSources(scn, scn.DS, 3));
        add(scn, new MayNotAddMoreThanDestinyToPowerModifier(scn.GetLSCard("luke"), null, 1, scn.LS));
        assertNull(scn.game().getModifiersQuerying().getMaxDestinyAddedToPower(scn.gameState(), scn.DS));
        advance(scn, false);
        assertEquals(1, drawnToPower(scn, scn.LS));
        assertEquals(3, drawnToPower(scn, scn.DS));
    }

    @Test public void capDoesNotApplyWhileItsConditionIsUnfulfilled() {
        var scn = battle();
        Condition never = (gameState, modifiersQuerying) -> false;
        add(scn, powerSources(scn, scn.LS, 3));
        add(scn, new MayNotAddMoreThanDestinyToPowerModifier(scn.GetLSCard("luke"), never, 1, null));
        advance(scn, false);
        assertEquals(3, drawnToPower(scn, scn.LS));
    }

    @Test public void lowestOfSeveralCapsApplies() {
        var scn = battle();
        add(scn, powerSources(scn, scn.LS, 3));
        add(scn, new MayNotAddMoreThanDestinyToPowerModifier(scn.GetLSCard("luke"), null, 2, null));
        add(scn, new MayNotAddMoreThanDestinyToPowerModifier(scn.GetLSCard("han"), null, 1, scn.LS));
        scn.PrepareLSDestiny(4);
        advance(scn, false);
        assertEquals(1, drawnToPower(scn, scn.LS));
    }

    @Test public void addingDestinyToPowerIsUnavailableOnceAtTheCap() {
        var scn = battle();
        var game = scn.game();
        assertTrue(GameConditions.canAddDestinyDrawsToPower(game, scn.LS));
        add(scn, new MayNotAddMoreThanDestinyToPowerModifier(scn.GetLSCard("luke"), null, 2, null));
        assertTrue(GameConditions.canAddDestinyDrawsToPower(game, scn.LS));
        add(scn, powerSources(scn, scn.LS, 1));
        assertTrue(GameConditions.canAddDestinyDrawsToPower(game, scn.LS));
        add(scn, new AddsDestinyToPowerModifier(scn.GetLSCard("han"), 1, scn.LS, true));
        assertFalse(GameConditions.canAddDestinyDrawsToPower(game, scn.LS));
        assertTrue(GameConditions.canAddDestinyDrawsToPower(game, scn.DS));
    }

    @Test public void ordinaryBattleDestinyIsNotLimitedByTheCap() {
        var scn = battle();
        var querying = scn.game().getModifiersQuerying();
        int before = querying.getNumBattleDestinyDraws(scn.gameState(), scn.LS, false, false);
        add(scn, new MayNotAddMoreThanDestinyToPowerModifier(scn.GetLSCard("luke"), null, 0, null));
        add(scn, new MayNotAddMoreThanDestinyToAttritionModifier(scn.GetLSCard("luke"), null, 0, null));
        assertEquals(before, querying.getNumBattleDestinyDraws(scn.gameState(), scn.LS, false, false));
        assertEquals(Integer.MAX_VALUE, querying.getNumBattleDestinyDraws(scn.gameState(), scn.LS, true, false));
        scn.PrepareLSDestiny(3); scn.PrepareLSDestiny(3);
        advance(scn, true);
        assertEquals(2, state.getNumBattleDestinyDrawn(scn.LS));
    }

    @Test public void attritionCapLimitsAddedDestinyToAttrition() {
        var uncapped = battle();
        add(uncapped, new AddsDestinyToAttritionModifier(uncapped.GetLSCard("luke"), 2, uncapped.LS, true));
        assertEquals(2, advanceToDamageSegment(uncapped, true).getNumDestinyToAttritionDrawn(uncapped.LS));

        var capped = battle();
        add(capped, new AddsDestinyToAttritionModifier(capped.GetLSCard("luke"), 2, capped.LS, true));
        add(capped, new MayNotAddMoreThanDestinyToAttritionModifier(capped.GetLSCard("luke"), null, 1, null));
        assertFalse(GameConditions.canAddDestinyDrawsToAttrition(capped.game(), capped.LS));
        assertEquals(1, advanceToDamageSegment(capped, true).getNumDestinyToAttritionDrawn(capped.LS));
    }

    @Test public void attritionCapBlocksAddingWhenReached() {
        var scn = battle();
        add(scn, new MayNotAddMoreThanDestinyToAttritionModifier(scn.GetLSCard("luke"), null, 1, null));
        assertTrue(GameConditions.canAddDestinyDrawsToAttrition(scn.game(), scn.LS));
        add(scn, new AddsDestinyToAttritionModifier(scn.GetLSCard("luke"), 1, scn.LS, true));
        assertFalse(GameConditions.canAddDestinyDrawsToAttrition(scn.game(), scn.LS));
    }

    @Test public void bonusIsAddedToTheSourcesDrawnDestiny() {
        var scn = battle();
        add(scn, new AddsDestinyToPowerModifier(scn.GetLSCard("luke"), 1, scn.LS, true).setBonusForEachDestinyDraw(new ConstantEvaluator(3)));
        scn.PrepareLSDestiny(2);
        advance(scn, false);
        assertEquals(1, drawnToPower(scn, scn.LS));
        assertEquals(5, totalToPower(scn, scn.LS), scn.epsilon);
    }

    @Test public void bonusIsNotAddedWhenTheSourcesDestinyIsNotDrawn() {
        var scn = battle();
        add(scn, powerSources(scn, scn.LS, 1));
        add(scn, new AddsDestinyToPowerModifier(scn.GetLSCard("han"), 1, scn.LS, true).setBonusForEachDestinyDraw(new ConstantEvaluator(3)));
        add(scn, new MayNotAddMoreThanDestinyToPowerModifier(scn.GetLSCard("luke"), null, 1, null));
        scn.PrepareLSDestiny(2);
        advance(scn, false);
        assertEquals(1, drawnToPower(scn, scn.LS));
        assertEquals(2, totalToPower(scn, scn.LS), scn.epsilon);
    }
}
