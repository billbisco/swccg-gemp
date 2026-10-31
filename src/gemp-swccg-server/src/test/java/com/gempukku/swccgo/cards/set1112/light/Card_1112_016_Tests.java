package com.gempukku.swccgo.cards.set1112.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
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

public class Card_1112_016_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("quigon", "1112_16");
                    put("chamber", "12_076");
                }},
                new HashMap<>() {{
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
    public void MasterQuiGonStatsAndEpisodeIDeployDiscountAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("quigon");
        var blueprint = card.getBlueprint();
        var chamber = scn.GetLSCard("chamber");
        var site = scn.GetLSStartingLocation();

        assertEquals("Master Qui-Gon", blueprint.getTitle());
        assertTrue(blueprint.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, blueprint.getUniqueness());
        assertEquals(Side.LIGHT, blueprint.getSide());
        assertEquals(1, blueprint.getDestiny(), scn.epsilon);
        assertEquals(7, blueprint.getDeployCost(), scn.epsilon);
        assertEquals(6, blueprint.getPower(), scn.epsilon);
        assertEquals(7, blueprint.getAbility(), scn.epsilon);
        assertEquals(7, blueprint.getForfeit(), scn.epsilon);
        assertTrue(blueprint.hasPersona(Persona.QUIGON));
        scn.BlueprintCardTypeCheck(blueprint, new ArrayList<>() {{
            add(CardType.JEDI_MASTER);
        }});
        scn.BlueprintIconCheck(blueprint, new ArrayList<>() {{
            add(Icon.CORUSCANT);
            add(Icon.EPISODE_I);
            add(Icon.WARRIOR);
            add(Icon.JEDI_MASTER);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_12, blueprint.getExpansionSet());
        assertEquals(Rarity.V, blueprint.getRarity());

        scn.StartGame();
        scn.MoveLocationToTable(chamber);
        float episode = scn.game().getModifiersQuerying().getDeployCost(
                scn.gameState(), card, card, chamber, false, null, false, 0, null, false);
        float other = scn.game().getModifiersQuerying().getDeployCost(
                scn.gameState(), card, card, site, false, null, false, 0, null, false);
        assertEquals(6, episode, scn.epsilon);
        assertEquals(7, other, scn.epsilon);
    }
}
