package com.gempukku.swccgo.cards.set1109.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1109_009_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("solo", "1109_9");
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
    public void StatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("solo").getBlueprint();
        assertEquals("General Solo", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        assertEquals(4, card.getDeployCost(), scn.epsilon);
        assertEquals(4, card.getPower(), scn.epsilon);
        assertEquals(3, card.getAbility(), scn.epsilon);
        assertEquals(8, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.REBEL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.SCOUT);
            add(Keyword.LEADER);
            add(Keyword.GENERAL);
        }});
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
            add(Persona.HAN);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REBEL);
            add(Icon.PILOT);
            add(Icon.WARRIOR);
            add(Icon.ENDOR);        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_9, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void Gameplay() {
        var scn = GetScenario();
        var card = scn.GetLSCard("solo");
        var site = scn.GetLSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, card);
        assertTrue(card.getZone() == Zone.AT_LOCATION || card.getZone() == Zone.ATTACHED);
    }
}
