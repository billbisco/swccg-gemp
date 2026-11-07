package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class Card_1250_038_Tests {
    private VirtualTableScenario scenario() {
        StartingSetup originalObjectiveOutsideDeck = new StartingSetup() {
            @Override public HashMap<String, String> Cards() {
                return new HashMap<>() {{ put("original", "1116_11"); put("legacy", "1250_78"); }};
            }
            @Override public void Setup(VirtualTableScenario s) { }
        };
        return new VirtualTableScenario(new HashMap<>() {{
            put("clone", "1250_38"); put("palpatine", "12_28"); put("luke", "1_19"); put("dock", "12_74"); put("plains", "12_79");
        }}, new HashMap<>(), 35, 35, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                originalObjectiveOutsideDeck, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void FinalCloneTrooperHasArmorOneAbilityAndNoUnconditionalHeroic() {
        var s = scenario(); var bp = s.GetLSCard("clone").getBlueprint();
        assertEquals("Clone Trooper", bp.getTitle()); assertFalse(bp.hasVirtualSuffix());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, bp.getExpansionSet()); assertEquals(Uniqueness.UNRESTRICTED, bp.getUniqueness());
        assertEquals(3, bp.getDestiny(), s.epsilon); assertEquals(2, bp.getDeployCost(), s.epsilon);
        assertEquals(2, bp.getPower(), s.epsilon); assertEquals(1, bp.getAbility(), s.epsilon);
        assertEquals(3, bp.getForfeit(), s.epsilon); assertEquals(4, bp.getArmor(), s.epsilon);
        s.BlueprintCardTypeCheck(bp, new ArrayList<>(List.of(CardType.REPUBLIC)));
        s.BlueprintIconCheck(bp, new ArrayList<>(List.of(Icon.REPUBLIC, Icon.A_NEW_HOPE, Icon.EPISODE_I,
                Icon.WARRIOR, Icon.LEGACY_BLOCK_5)));
        s.BlueprintKeywordCheck(bp, new ArrayList<>(List.of(Keyword.CLONE_TROOPER)));
    }

    @Test
    public void CloneBecomesHeroicOnlyWhileWithPalpatine() {
        var s = scenario(); s.StartGame(); var clone = s.GetLSCard("clone"); var palpatine = s.GetLSCard("palpatine");
        var site = s.GetLSStartingLocation();
        s.MoveCardsToLocation(site, clone, s.GetLSCard("luke"));
        s.MoveCardsToLocation(s.GetDSStartingLocation(), palpatine);
        var query = s.game().getModifiersQuerying();
        assertFalse(query.hasKeyword(s.gameState(), clone, Keyword.HEROIC));
        s.MoveCardsToLocation(site, palpatine);
        assertTrue(query.hasKeyword(s.gameState(), clone, Keyword.HEROIC));
        s.MoveCardsToLocation(s.GetDSStartingLocation(), palpatine);
        assertFalse(query.hasKeyword(s.gameState(), clone, Keyword.HEROIC));
    }

    @Test
    public void CoruscantAddsExactlyOneEvenWithAJediOrLeader() {
        var s = scenario(); s.StartGame(); var clone = s.GetLSCard("clone");
        var dock = s.GetLSCard("dock"); s.MoveLocationToTable(dock);
        s.MoveCardsToLocation(dock, clone, s.GetLSCard("luke"));
        assertEquals(3, s.GetPower(clone)); assertEquals(4, s.GetForfeit(clone));
        s.MoveCardsToLocation(s.GetLSStartingLocation(), clone);
        assertEquals(2, s.GetPower(clone)); assertEquals(3, s.GetForfeit(clone));
    }

    @Test
    public void OriginalVirtualObjectiveBackDoesNotEnableThePrintedLegacyDrainBonus() {
        var s = scenario(); s.StartGame(); var dock = s.GetLSCard("dock");
        s.MoveLocationToTable(dock); s.MoveCardsToLocation(dock, s.GetLSCard("clone"));
        var objective = s.GetLSCard("original"); s.MoveCardsToLSSideOfTable(objective);
        objective.setObjectiveDeploymentComplete(true); s.gameState().flipCard(s.game(), objective, true);
        assertEquals("They Will Be Lost And Confused", objective.getBlueprint().getTitle());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_16, objective.getBlueprint().getExpansionSet());
        s.SkipToLSTurn(Phase.CONTROL);
        s.LSForceDrainAt(dock); s.PassForceDrainStartResponses();
        assertEquals(1, s.GetForceDrainTotal());
        assertTrue(s.AwaitingDSForceLossPayment());
        s.DSPayForceLossFromReserveDeck(); s.PassAllResponses();
        assertFalse(s.IsActiveForceDrain());
    }
    /** Puts the given objective on table with its They Will Be Lost And Confused side up. */
    private void flipObjective(VirtualTableScenario s, String name) {
        var objective = s.GetLSCard(name); s.MoveCardsToLSSideOfTable(objective);
        objective.setObjectiveDeploymentComplete(true); s.gameState().flipCard(s.game(), objective, true);
        assertEquals("They Will Be Lost And Confused", objective.getBlueprint().getTitle());
    }

    private int drainTotalAt(VirtualTableScenario s, PhysicalCardImpl site) {
        s.SkipToLSTurn(Phase.CONTROL);
        s.LSForceDrainAt(site); s.PassForceDrainStartResponses();
        return (int) s.GetForceDrainTotal();
    }

    @Test
    public void LegacyObjectiveBackAddsOneToDrainsAtTheClonesSite() {
        var s = scenario(); s.StartGame(); var dock = s.GetLSCard("dock");
        s.MoveLocationToTable(dock); s.MoveCardsToLocation(dock, s.GetLSCard("clone"));
        flipObjective(s, "legacy");
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, s.GetLSCard("legacy").getBlueprint().getExpansionSet());
        assertEquals(2, drainTotalAt(s, dock));
    }

    private int drainAtOtherSite(boolean legacyBack) {
        var s = scenario(); s.StartGame(); var dock = s.GetLSCard("dock"); var plains = s.GetLSCard("plains");
        s.MoveLocationToTable(dock); s.MoveLocationToTable(plains);
        s.MoveCardsToLocation(dock, s.GetLSCard("clone")); s.MoveCardsToLocation(plains, s.GetLSCard("luke"));
        if (legacyBack) flipObjective(s, "legacy");
        return drainTotalAt(s, plains);
    }

    @Test
    public void LegacyObjectiveBackDoesNotAddToDrainsAtAnotherSite() {
        assertEquals(drainAtOtherSite(false), drainAtOtherSite(true));
    }

    @Test
    public void LegacyObjectiveBackMakesPalpatineImmuneToAttritionBelowSix() {
        var s = scenario(); s.StartGame(); var palpatine = s.GetLSCard("palpatine");
        var site = s.GetLSStartingLocation();
        s.MoveCardsToLocation(site, s.GetLSCard("clone")); s.MoveCardsToLocation(s.GetDSStartingLocation(), palpatine);
        var query = s.game().getModifiersQuerying();
        float before = query.getImmunityToAttritionLessThan(s.gameState(), palpatine);
        assertTrue(before < 6);
        flipObjective(s, "legacy");
        assertEquals(6, query.getImmunityToAttritionLessThan(s.gameState(), palpatine), s.epsilon);
    }

    @Test
    public void OriginalVirtualObjectiveBackDoesNotGrantPalpatineImmunity() {
        var s = scenario(); s.StartGame(); var palpatine = s.GetLSCard("palpatine");
        s.MoveCardsToLocation(s.GetLSStartingLocation(), s.GetLSCard("clone"));
        s.MoveCardsToLocation(s.GetDSStartingLocation(), palpatine);
        var query = s.game().getModifiersQuerying();
        float before = query.getImmunityToAttritionLessThan(s.gameState(), palpatine);
        flipObjective(s, "original");
        assertEquals(before, query.getImmunityToAttritionLessThan(s.gameState(), palpatine), s.epsilon);
    }
}
