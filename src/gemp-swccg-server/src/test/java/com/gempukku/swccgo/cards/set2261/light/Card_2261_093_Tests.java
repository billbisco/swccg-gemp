package com.gempukku.swccgo.cards.set2261.light;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;


/** Armed And Dangerous & Krayt Dragon Howl original version of 1261_93. */
public class Card_2261_093_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of("v", "2261_93", "current", "1261_93")), new HashMap<>(Map.of()),
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void IdentityAndVersionInfo() {
        // Title-order version (batch B41): same game text as the current card, display title "Krayt Dragon Howl & Armed And Dangerous (V)".
        var scn = scenario();
        var version = scn.GetLSCard("v").getBlueprint();
        var current = scn.GetLSCard("current").getBlueprint();
        assertEquals(current.getGameText(), version.getGameText());
        assertEquals("Krayt Dragon Howl & Armed And Dangerous", version.getDisplayTitle());
        assertNull(current.getLegacyVersion());
        var info = version.getLegacyVersion();
        assertEquals("1261_93", info.currentPrintingId());
        assertEquals("", info.label());
        assertEquals(LocalDate.of(2009, 9, 4), info.printedOn());
        assertEquals(LocalDate.of(2011, 7, 14), info.supersededOn());
        assertEquals(current.getTitle(), version.getTitle());
        assertTrue(version.isLegacy());
    }
}
