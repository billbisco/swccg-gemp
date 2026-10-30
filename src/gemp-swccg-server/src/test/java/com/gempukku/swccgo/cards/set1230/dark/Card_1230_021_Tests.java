package com.gempukku.swccgo.cards.set1230.dark;

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

public class Card_1230_021_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("wooof", "1230_21");
                    put("devastator", "1_301");
                    put("tatooine", "1_289");
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
    public void WooofStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("wooof").getBlueprint();
        assertEquals("Wooof", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.ALIEN);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.SMUGGLER);
        }});
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ALIEN);
            add(Icon.PILOT);
            add(Icon.JABBAS_PALACE);
            add(Icon.LEGACY_BLOCK_3);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void WooofAddsThreeToPowerOfAnythingHePilots() {
        var scn = GetScenario();
        var wooof = scn.GetDSCard("wooof");
        var devastator = scn.GetDSCard("devastator");
        var tatooine = scn.GetDSCard("tatooine");
        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, devastator);
        int before = scn.GetPower(devastator);
        scn.BoardAsPilot(devastator, wooof);
        assertEquals(before + 3, scn.GetPower(devastator));
    }
}
