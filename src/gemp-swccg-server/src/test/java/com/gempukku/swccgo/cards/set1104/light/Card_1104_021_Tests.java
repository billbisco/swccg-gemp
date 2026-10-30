package com.gempukku.swccgo.cards.set1104.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1104_021_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("tigran", "1104_21");
                    put("echoBay", "3_59");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("dengar", "5_91");
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
    public void TigranJamiroStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("tigran").getBlueprint();

        assertEquals("Tigran Jamiro", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.REBEL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
            add(Persona.TIGRAN);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REBEL);
            add(Icon.WARRIOR);
            add(Icon.HOTH);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void TigranJamiroDeploysFreeToHoth() {
        var scn = GetScenario();
        var tigran = scn.GetLSCard("tigran");
        var echoBay = scn.GetLSCard("echoBay");

        scn.StartGame();
        scn.MoveLocationToTable(echoBay);
        assertEquals(0, deployCostTo(scn, tigran, echoBay), scn.epsilon);
    }

    @Test
    public void TigranJamiroMakesOpponentsAliensAndImperialsDeployPlusOneAtSameAndRelatedSites() {
        var scn = GetScenario();
        var tigran = scn.GetLSCard("tigran");
        var trooper = scn.GetDSCard("trooper");
        var dengar = scn.GetDSCard("dengar");
        var echoBay = scn.GetLSCard("echoBay");

        scn.StartGame();
        scn.MoveLocationToTable(echoBay);
        scn.MoveCardsToLocation(echoBay, tigran);
        assertEquals(trooper.getBlueprint().getDeployCost() + 1, deployCostTo(scn, trooper, echoBay), scn.epsilon);
        assertEquals(dengar.getBlueprint().getDeployCost() + 1, deployCostTo(scn, dengar, echoBay), scn.epsilon);
    }

    private float deployCostTo(VirtualTableScenario scn, PhysicalCardImpl card, PhysicalCardImpl target) {
        return scn.game().getModifiersQuerying().getDeployCost(
                scn.gameState(), card, card, target, false, null, false, 0, null, false);
    }
}
