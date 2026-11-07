package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class Card_1250_038_Tests {
    private VirtualTableScenario scenario() {
        StartingSetup originalObjectiveOutsideDeck = new StartingSetup() {
            @Override public HashMap<String, String> Cards() {
                return new HashMap<>() {{ put("original", "1116_11"); }};
            }
            @Override public void Setup(VirtualTableScenario s) { }
        };
        return new VirtualTableScenario(new HashMap<>() {{
            put("clone", "1250_38"); put("palpatine", "12_28"); put("luke", "1_19"); put("dock", "12_74");
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
}
