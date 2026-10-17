package com.gempukku.swccgo.cards.set1200.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1200_008_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("card", "1200_8");
                }},
                new HashMap<>() {{
                    
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
    public void DontDoThatAgainVStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("card").getBlueprint();

        assertEquals("Don't Do That Again (V)", card.getTitle());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertTrue(card.isLegacy());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.DEFENSIVE_SHIELD);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DEFENSIVE_SHIELD);
            add(Icon.EPISODE_I);
            add(Icon.LEGACY_BLOCK_D);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
    }
}
