package com.gempukku.swccgo.cards.set2261.light;

import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.gempukku.swccgo.cards.LegacyVersionAssertions.assertLegacyVersion;

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
        var scn = scenario();
        assertLegacyVersion(scn.GetLSCard("v").getBlueprint(), scn.GetLSCard("current").getBlueprint(), "1261_93", "",
                LocalDate.of(2009, 9, 4), LocalDate.of(2012, 7, 21),
                "USED: Cancel Disarmed or Stunning Leader. OR If a battle was just initiated, ▼ (or deploy from hand) a character weapon on your participating character. LOST: Use 1 Force to ▼ (or retrieve) non-[Reflections 3] Obi-Wan. OR If opponent just initiated a battle against non-[Reflections 3] Obi-Wan alone at a site, his forfeit cannot be reduced.");
    }
}
