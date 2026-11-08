package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

/** Mara Jade With Lightsaber (1240_10): the Virtual Block 4 sheet text, the latest version. */
public class Card_601_090_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                }},
                new HashMap<>() {{
                    put("mara", "601_090");
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
    public void MaraJadeWithLightsaberHasTheLatestText() {
        var scn = GetScenario();
        var card = scn.GetDSCard("mara").getBlueprint();
        assertEquals("While Luke or Emperor on table, power +1 and she moves for free. Permanent weapon is •Mara Jade's Lightsaber (may target a character or creature for free; draw two destiny; target hit, and its forfeit = 0, if total destiny > defense value).",
                card.getGameText());
        assertNull(card.getLegacyVersion());
        assertFalse(card.getGameText().startsWith("With "));
    }
}
