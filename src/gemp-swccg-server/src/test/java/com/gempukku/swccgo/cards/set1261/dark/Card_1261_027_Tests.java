package com.gempukku.swccgo.cards.set1261.dark;

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

public class Card_1261_027_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    
                }},
                new HashMap<>() {{
                    put("card", "1261_27");
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
    public void YouCannotHideForeverVStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("card").getBlueprint();

        assertEquals("You Cannot Hide Forever (V)", card.getTitle());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertTrue(card.isLegacy());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.DEFENSIVE_SHIELD);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DEFENSIVE_SHIELD);
            add(Icon.REFLECTIONS_III);
            add(Icon.LEGACY_BLOCK_6);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
    }
}
