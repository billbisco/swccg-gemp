package com.gempukku.swccgo.cards.set601.light;

import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_601_158_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("leadership", "601_158");
                    put("leadershipMapped", "1230_109");
                }},
                new HashMap<>(),
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
    public void RebelLeadershipVLoadsFromTheLightPackageUnderBothIds() {
        var scn = GetScenario();
        var card = scn.GetLSCard("leadership").getBlueprint();
        var mapped = scn.GetLSCard("leadershipMapped").getBlueprint();

        assertEquals("Rebel Leadership", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(ExpansionSet.LEGACY, card.getExpansionSet());
        assertEquals("com.gempukku.swccgo.cards.set601.light.Card601_158", card.getClass().getName());

        // 1230_109 is mapped to 601_158 in blueprintMapping.txt
        assertEquals(card.getClass(), mapped.getClass());
    }
}
