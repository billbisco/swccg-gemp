package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

/** Stinger (V) (1240_63): the PC website card list text (PC-E1), the latest version. */
public class Card_601_211_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                }},
                new HashMap<>() {{
                    put("stinger", "601_211");
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
    public void StingerHasTheLatestText() {
        var scn = GetScenario();
        var card = scn.GetDSCard("stinger").getBlueprint();
        assertEquals("May add 1 pilot. Guri deploys -3 aboard. While Guri piloting, during any control phase may draw top card of Reserve Deck (if Leia or Chewie on table, Force Pile instead) and immune to attrition < 5.",
                card.getGameText());
        assertNull(card.getLegacyVersion());
        assertFalse(card.getGameText().contains("if Chewie or Leia"));
    }
}
