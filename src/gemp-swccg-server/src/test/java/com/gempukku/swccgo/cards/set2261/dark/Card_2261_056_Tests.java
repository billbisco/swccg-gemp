package com.gempukku.swccgo.cards.set2261.dark;

import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.gempukku.swccgo.cards.LegacyVersionAssertions.assertLegacyVersion;

/** Black 1 original version text of 1261_56 (plays the same as its neighbouring version). */
public class Card_2261_056_Tests {
    @Test
    public void IdentityAndVersionInfo() {
        var scn = new VirtualTableScenario(new HashMap<>(Map.of()), new HashMap<>(Map.of("v", "2261_56", "current", "1261_56")),
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        assertLegacyVersion(scn.GetDSCard("v").getBlueprint(), scn.GetDSCard("current").getBlueprint(), "1261_56", "",
                LocalDate.of(2009, 9, 4), LocalDate.of(2013, 4, 27),
                "May add Black Leader as pilot. TIEs are immune to Don't Get Cocky. Once per turn, may use 1 Force to ▲ a Black Squadron TIE. Draws one battle destiny if not able to otherwise. Immune to attrition < 5.");
    }
}
