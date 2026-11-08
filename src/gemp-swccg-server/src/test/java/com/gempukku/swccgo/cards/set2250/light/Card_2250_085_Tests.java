package com.gempukku.swccgo.cards.set2250.light;

import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.gempukku.swccgo.cards.LegacyVersionAssertions.assertLegacyVersion;

/** Elegant Lightsaber original version of 1250_85. */
public class Card_2250_085_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of("v", "2250_85", "current", "1250_85")), new HashMap<>(Map.of()),
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void IdentityAndVersionInfo() {
        var scn = scenario();
        assertLegacyVersion(scn.GetLSCard("v").getBlueprint(), scn.GetLSCard("current").getBlueprint(), "1250_85", "",
                LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21),
                "Deploy on a Jedi. May add 1 to Force drain at opponent's site where present. May target a character or creature for free. Draw two destiny. Target hit, and may not be used to satisfy attrition If total destiny > defense value.");
    }
}
