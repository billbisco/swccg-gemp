package com.gempukku.swccgo.cards.set200.dark;

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
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_200_076_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>(),
                new HashMap<>()
                {{
                    put("dooku", "200_076"); //Count Dooku (V)
                }},
                10,
                10,
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
    public void CountDookuStatsAndKeywordsAreCorrect() {
        /**
         * Title: Count Dooku
         * Uniqueness: Unique
         * Side: Dark
         * Type: Character
         * Subtype: Dark Jedi Master
         * Destiny: 1
         * Deploy: 6
         * Power: 5
         * Ability: 7
         * Forfeit: 8
         * Icons: Episode I, Pilot, Warrior, Separatist, Virtual Set 0
         * Lore: Serennian leader. Trade Federation.
         * Set: Set 0
         * Rarity: V
         */
        var scn = GetScenario();
        var card = scn.GetDSCard("dooku").getBlueprint();

        assertEquals("Count Dooku", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        assertEquals(6, card.getDeployCost(), scn.epsilon);
        assertEquals(5, card.getPower(), scn.epsilon);
        assertEquals(7, card.getAbility(), scn.epsilon);
        assertEquals(8, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.DARK_JEDI_MASTER);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DARK_JEDI_MASTER);
            add(Icon.EPISODE_I);
            add(Icon.PILOT);
            add(Icon.WARRIOR);
            add(Icon.SEPARATIST);
            add(Icon.VIRTUAL_SET_0);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.LEADER);
        }});
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
            add(Persona.DOOKU);
        }});
        assertEquals(ExpansionSet.SET_0, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }
}
