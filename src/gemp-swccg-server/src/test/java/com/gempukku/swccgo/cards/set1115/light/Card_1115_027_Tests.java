package com.gempukku.swccgo.cards.set1115.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
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

public class Card_1115_027_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("trooper", "1115_27");
                }},
                new HashMap<>() {{
                    put("guard", "1_194");
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
    public void RepublicTrooperStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("trooper").getBlueprint();

        assertEquals("Republic Trooper with Blaster Rifle", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.REPUBLIC);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REPUBLIC);
            add(Icon.EPISODE_I);
            add(Icon.WARRIOR);
            add(Icon.PERMANENT_WEAPON);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_15, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }
}
