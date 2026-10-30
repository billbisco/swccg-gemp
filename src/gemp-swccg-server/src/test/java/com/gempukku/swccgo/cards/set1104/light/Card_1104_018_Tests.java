package com.gempukku.swccgo.cards.set1104.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1104_018_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("tam", "1104_18");
                    put("xwing", "1_146");
                    put("tatooine", "1_127");
                    put("echoBay", "3_59");
                    put("dsBay", "1_124");
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void TamizanderReyStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("tam").getBlueprint();

        assertEquals("Tamizander Rey", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(1, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(3, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.REBEL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REBEL);
            add(Icon.PILOT);
            add(Icon.HOTH);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void TamizanderReyAddsTwoToPowerOfAnythingHePilots() {
        var scn = GetScenario();
        var tam = scn.GetLSCard("tam");
        var xwing = scn.GetLSCard("xwing");
        var tatooine = scn.GetLSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, xwing);
        scn.BoardAsPilot(xwing, tam);
        assertEquals(xwing.getBlueprint().getPower().intValue() + 2, scn.GetPower(xwing));
    }

    @Test
    public void TamizanderReyIsPowerAndDefenseValuePlusTwoWhileAtADockingBay() {
        var scn = GetScenario();
        var tam = scn.GetLSCard("tam");
        var echoBay = scn.GetLSCard("echoBay");

        scn.StartGame();
        scn.MoveLocationToTable(echoBay);
        scn.MoveCardsToLocation(echoBay, tam);
        assertEquals(3, scn.GetPower(tam));
        assertEquals(4, scn.GetDefense(tam));
    }

    @Test
    public void TamizanderReyDockingBayTransitRequiresMinusThreeForce() {
        var scn = GetScenario();
        var tam = scn.GetLSCard("tam");
        var luke = scn.GetLSCard("luke");
        var echoBay = scn.GetLSCard("echoBay");
        var dsBay = scn.GetLSCard("dsBay");

        scn.StartGame();
        scn.MoveLocationToTable(echoBay);
        scn.MoveLocationToTable(dsBay);
        scn.MoveCardsToLocation(echoBay, tam, luke);
        float lukeCost = scn.game().getModifiersQuerying().getDockingBayTransitCost(
                scn.gameState(), luke, echoBay, dsBay, 0);
        float tamCost = scn.game().getModifiersQuerying().getDockingBayTransitCost(
                scn.gameState(), tam, echoBay, dsBay, 0);
        assertTrue(lukeCost > tamCost);
        assertEquals(Math.max(0, lukeCost - 3), tamCost, scn.epsilon);
    }
}
