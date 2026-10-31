package com.gempukku.swccgo.cards.set1112.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1112_021_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("yoda", "1112_21");
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
    public void YodaDeploysOnlyToEpisodeISites() {
        var scn = GetScenario();
        var yoda = scn.GetLSCard("yoda");
        var blueprint = yoda.getBlueprint();
        var chamber = scn.GetLSCard("chamber");
        var other = scn.GetLSStartingLocation();

        assertEquals("Yoda, Senior Council Member", blueprint.getTitle());
        assertTrue(blueprint.hasVirtualSuffix());
        assertEquals(1, blueprint.getDestiny(), scn.epsilon);
        assertEquals(4, blueprint.getDeployCost(), scn.epsilon);
        assertEquals(3, blueprint.getPower(), scn.epsilon);
        assertEquals(7, blueprint.getAbility(), scn.epsilon);
        assertEquals(7, blueprint.getForfeit(), scn.epsilon);
        assertEquals(Uniqueness.UNIQUE, blueprint.getUniqueness());
        assertEquals(Side.LIGHT, blueprint.getSide());
        assertTrue(blueprint.hasPersona(Persona.YODA));
        scn.BlueprintCardTypeCheck(blueprint, new ArrayList<>() {{
            add(CardType.JEDI_MASTER);
        }});
        scn.BlueprintKeywordCheck(blueprint, new ArrayList<>() {{
            add(Keyword.JEDI_COUNCIL_MEMBER);
        }});
        scn.BlueprintIconCheck(blueprint, new ArrayList<>() {{
            add(Icon.CORUSCANT);
            add(Icon.EPISODE_I);
            add(Icon.JEDI_MASTER);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_12, blueprint.getExpansionSet());
        assertEquals(Rarity.V, blueprint.getRarity());

        scn.StartGame();
        scn.MoveLocationToTable(chamber);
        scn.MoveCardsToLSHand(yoda);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSPlayCard(yoda);
        assertTrue(scn.LSHasCardChoiceAvailable(chamber));
        assertFalse(scn.LSHasCardChoiceAvailable(other));
    }
}
