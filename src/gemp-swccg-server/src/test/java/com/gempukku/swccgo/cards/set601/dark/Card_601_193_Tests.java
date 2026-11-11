package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

/** Kuat Drive Yards (V) (1240_33): the Virtual Block 4 sheet text, the latest version. */
public class Card_601_193_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                }},
                new HashMap<>() {{
                    put("kdy", "601_193");
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
    public void KuatDriveYardsHasTheLatestText() {
        var scn = GetScenario();
        var card = scn.GetDSCard("kdy").getBlueprint();
        assertEquals("Deploy on table. Unique (•) Imperial-class Star Destroyers draw one battle destiny if not able to otherwise and are immune to attrition < 4. During your deploy phase, may use 2 Force to [download] a battleground system. We're In Attack Position Now is canceled. (Immune to Alter.)",
                card.getGameText());
        assertNull(card.getLegacyVersion());
        assertFalse(card.getGameText().contains("if unable to otherwise"));
    }
}
