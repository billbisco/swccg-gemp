package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.DuelState;
import com.gempukku.swccgo.game.state.LightsaberCombatState;
import com.gempukku.swccgo.logic.effects.DuelDirections;
import com.gempukku.swccgo.logic.effects.LightsaberCombatDirections;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.timing.Action;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class Card_1250_039_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("advisor", "1250_39"); put("second", "1250_39"); put("chamber", "12_76"); put("dock", "12_74");
            put("obi", "1_21"); put("strength", "13_24");
        }}, new HashMap<>() {{ put("vader", "1_168"); }}, 35, 35,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void FinalAdvisorHasThreeDotsAndPrintedCouncilLore() {
        var s = scenario(); var bp = s.GetLSCard("advisor").getBlueprint();
        assertEquals("Jedi Advisor", bp.getTitle()); assertFalse(bp.hasVirtualSuffix());
        assertEquals(Uniqueness.RESTRICTED_3, bp.getUniqueness());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, bp.getExpansionSet());
        assertEquals("Female. Jedi Council Member.", bp.getLore());
        assertEquals(2, bp.getDestiny(), s.epsilon); assertEquals(3, bp.getDeployCost(), s.epsilon);
        assertEquals(4, bp.getPower(), s.epsilon); assertEquals(7, bp.getAbility(), s.epsilon);
        assertEquals(6, bp.getForfeit(), s.epsilon);
        s.BlueprintCardTypeCheck(bp, new ArrayList<>(List.of(CardType.JEDI_MASTER)));
        s.BlueprintIconCheck(bp, new ArrayList<>(List.of(Icon.SPECIAL_EDITION, Icon.EPISODE_I, Icon.JEDI_MASTER, Icon.LEGACY_BLOCK_5)));
        s.BlueprintKeywordCheck(bp, new ArrayList<>(List.of(Keyword.FEMALE, Keyword.JEDI_COUNCIL_MEMBER)));
    }

    @Test
    public void ActualAdvisorDeployCanChooseCoruscantButNotOtherPlanets() {
        var s = scenario(); s.StartGame(); var advisor = s.GetLSCard("advisor");
        var chamber = s.GetLSCard("chamber"); var dock = s.GetLSCard("dock");
        s.MoveLocationToTable(chamber); s.MoveLocationToTable(dock);
        s.MoveCardsToHand(advisor); s.LSActivateForceCheat(7); s.SkipToLSTurn(Phase.DEPLOY);
        int force = s.GetLSForcePileCount(); s.LSDeployCard(advisor);
        assertTrue(s.LSHasCardChoicesAvailable(chamber, dock));
        assertFalse(s.LSHasCardChoiceAvailable(s.GetLSStartingLocation()));
        assertFalse(s.LSHasCardChoiceAvailable(s.GetDSStartingLocation()));
        s.LSChooseCard(dock); s.PassAllResponses();
        assertEquals(dock, advisor.getAtLocation()); assertEquals(force - 3, s.GetLSForcePileCount());
    }

    private void checkTotals(int copies, boolean innerStrength, int bonus) {
        var s = scenario(); s.StartGame(); var duelSite = s.GetDSStartingLocation();
        var chamber = s.GetLSCard("chamber"); var obi = s.GetLSCard("obi"); var vader = s.GetDSCard("vader");
        s.MoveLocationToTable(chamber);
        if (copies > 0) s.MoveCardsToLocation(chamber, s.GetLSCard("advisor"));
        if (copies > 1) s.MoveCardsToLocation(chamber, s.GetLSCard("second"));
        if (innerStrength) s.MoveCardsToLSSideOfTable(s.GetLSCard("strength"));
        s.MoveCardsToLocation(duelSite, obi, vader);
        var query = s.game().getModifiersQuerying();
        s.gameState().beginDuel(s.DS, vader, duelSite, vader, obi, new DuelDirections() {
            @Override public boolean isEpicDuel() { return false; }
            @Override public boolean isCrossOverToDarkSideAttempt() { return false; }
            @Override public Evaluator getBaseDuelTotal(String player, DuelState duel) { return new ConstantEvaluator(5); }
            @Override public int getBaseNumDuelDestinyDraws(String player, DuelState duel) { return 0; }
            @Override public void performDuelDirections(Action action, SwccgGame game, DuelState duel) { }
            @Override public void performDuelResults(Action action, SwccgGame game, DuelState duel) { }
        });
        assertEquals(5 + bonus, query.getDuelTotal(s.gameState(), s.LS), s.epsilon);
        assertEquals(5, query.getDuelTotal(s.gameState(), s.DS), s.epsilon);
        s.gameState().endDuel();

        s.gameState().beginLightsaberCombat(s.DS, duelSite, vader, obi, new LightsaberCombatDirections() {
            @Override public Evaluator getBaseLightsaberCombatTotal(String player, LightsaberCombatState combat) { return new ConstantEvaluator(5); }
            @Override public int getBaseNumLightsaberCombatDestinyDraws(String player, LightsaberCombatState combat) { return 1; }
            @Override public void performLightsaberCombatDirections(Action action, SwccgGame game, LightsaberCombatState combat) { }
            @Override public void performLightsaberCombatResults(Action action, SwccgGame game, LightsaberCombatState combat) { }
        });
        var combat = s.gameState().getLightsaberCombatState();
        combat.increaseTotalLightsaberCombatDestinyFromDraws(s.LS, 2, 1);
        combat.increaseTotalLightsaberCombatDestinyFromDraws(s.DS, 2, 1);
        assertEquals(7 + bonus, query.getLightsaberCombatTotal(s.gameState(), s.LS), s.epsilon);
        assertEquals(7, query.getLightsaberCombatTotal(s.gameState(), s.DS), s.epsilon);
        s.gameState().endLightsaberCombat();
    }

    @Test public void OneAdvisorAddsOneToBothFriendlyTotalsEvenFromAnotherSite() { checkTotals(1, false, 1); }
    @Test public void TwoAdvisorsCumulativelyAddTwoToBothTotals() { checkTotals(2, false, 2); }
    @Test public void InnerStrengthDisablesBothBonuses() { checkTotals(2, true, 0); }
}
