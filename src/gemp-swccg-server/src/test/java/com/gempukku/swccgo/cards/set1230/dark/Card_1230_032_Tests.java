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

public class Card_1230_032_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("flagship", "1230_32");
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
    public void FlagshipOperationsStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("flagship").getBlueprint();
        assertEquals("Flagship Operations", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.DEPLOYS_ON_LOCATION);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DEATH_STAR_II);
            add(Icon.EFFECT);
            add(Icon.LEGACY_BLOCK_3);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void FlagshipOperationsDeploysOnASystem() {
        var scn = GetScenario();
        var flagship = scn.GetDSCard("flagship");
        var tatooine = scn.GetDSCard("tatooine");
        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToDSHand(flagship);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSCardPlayAvailable(flagship));
        scn.DSPlayCard(flagship);
        scn.DSChooseCard(tatooine);
        scn.PassAllResponses();
        assertEquals(tatooine, flagship.getAttachedTo());
    }
}
