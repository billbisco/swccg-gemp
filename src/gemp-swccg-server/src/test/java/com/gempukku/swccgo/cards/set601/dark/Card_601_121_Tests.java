package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

/** Twi'lek Advisor (V) (1230_51): the Virtual Block 3 sheet text, the latest version. */
public class Card_601_121_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                }},
                new HashMap<>() {{
                    put("advisor", "601_121");
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
    public void TwilekAdvisorHasTheLatestText() {
        var scn = GetScenario();
        var card = scn.GetDSCard("advisor").getBlueprint();
        assertEquals("USED: Use 3 Force to deploy a unique (•) alien (for free) from hand or Used Pile; reshuffle. STARTING: Take into hand and/or deploy from Reserve Deck up to three Effects of any kind with 'Jabba' in lore or game text; reshuffle. Place this Interrupt in Reserve Deck.",
                card.getGameText());
        assertNull(card.getLegacyVersion());
        assertFalse(card.getGameText().contains("on table"));
    }
}
