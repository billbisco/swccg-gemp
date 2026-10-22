package com.gempukku.swccgo.cards.set1210.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1210_114_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("wedge", "1210_114");
                    put("skate", "10_20");
                    put("luke", "1_19");
                    put("tatooine", "1_127");
                }},
                new HashMap<>() {{
                    put("setforstun", "1_268");
                    put("overwhelmed", "7_258");
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
    public void WedgeAntillesStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("wedge").getBlueprint();

        assertEquals("Wedge Antilles", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(6, card.getForfeit(), scn.epsilon);
        assertEquals(Species.CORELLIAN, card.getSpecies());
        assertTrue(card.getPersonas().contains(Persona.WEDGE));
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.REBEL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.SMUGGLER);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REBEL);
            add(Icon.PILOT);
            add(Icon.WARRIOR);
            add(Icon.A_NEW_HOPE);
            add(Icon.LEGACY_BLOCK_1);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void WedgePilotingPulsarSkateIsPowerPlusThreeAndManeuverPlusTwo() {
        var scn = GetScenario();
        var wedge = scn.GetLSCard("wedge");
        var skate = scn.GetLSCard("skate");
        var tatooine = scn.GetLSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, skate);
        scn.BoardAsPilot(skate, wedge);
        assertEquals(5, scn.GetPower(skate));
        assertEquals(6, scn.GetManeuver(skate));
    }

    @Test
    public void WedgeAtTatooinePreventsSetForStunFromTargetingCharactersHere() {
        var scn = GetScenario();
        var wedge = scn.GetLSCard("wedge");
        var luke = scn.GetLSCard("luke");
        var setforstun = scn.GetDSCard("setforstun");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, wedge, luke);
        assertFalse(scn.game().getModifiersQuerying().canBeTargetedBy(scn.gameState(), luke, setforstun));
    }

    @Test
    public void WedgeAtTatooineSystemPreventsOverwhelmedFromTargetingThatSystem() {
        var scn = GetScenario();
        var wedge = scn.GetLSCard("wedge");
        var skate = scn.GetLSCard("skate");
        var tatooine = scn.GetLSCard("tatooine");
        var overwhelmed = scn.GetDSCard("overwhelmed");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, skate);
        scn.BoardAsPilot(skate, wedge);
        assertFalse(scn.game().getModifiersQuerying().canBeTargetedBy(scn.gameState(), tatooine, overwhelmed));
    }
}
