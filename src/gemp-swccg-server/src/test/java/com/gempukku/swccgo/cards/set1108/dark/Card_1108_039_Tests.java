package com.gempukku.swccgo.cards.set1108.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1108_039_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("cruiser", "1108_39");
                }},
                10, 10,
                StartingSetup.DefaultLSSpaceSystem,
                StartingSetup.DefaultDSSpaceSystem,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void JabbasSpaceCruiserStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("cruiser").getBlueprint();
        assertEquals("Jabba's Space Cruiser", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(4, card.getDeployCost(), scn.epsilon);
        assertEquals(5, card.getPower(), scn.epsilon);
        assertEquals(5, card.getArmor(), scn.epsilon);
        assertEquals(4, card.getHyperspeed(), scn.epsilon);
        assertEquals(6, card.getForfeit(), scn.epsilon);
        assertEquals(2, card.getPilotCapacity());
        assertEquals(6, card.getPassengerCapacity());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.STARSHIP);
        }});
        assertEquals(CardSubtype.CAPITAL, card.getCardSubtype());
        scn.BlueprintModelTypeCheck(card, new ArrayList<>() {{
            add(ModelType.UBRIKKIAN_LUXURY_SPACE_YACHT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.CRUISER);
            add(Keyword.SHIP_DOCKING_CAPABILITY);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.STARSHIP);
            add(Icon.INDEPENDENT);
            add(Icon.NAV_COMPUTER);
            add(Icon.SCOMP_LINK);
            add(Icon.SPECIAL_EDITION);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_8, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void JabbasSpaceCruiserMaySitAtASystem() {
        var scn = GetScenario();
        var cruiser = scn.GetDSCard("cruiser");
        var system = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(system, cruiser);
        assertAtLocation(system, cruiser);
    }
}
