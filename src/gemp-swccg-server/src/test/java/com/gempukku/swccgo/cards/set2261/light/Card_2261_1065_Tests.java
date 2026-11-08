package com.gempukku.swccgo.cards.set2261.light;

import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.gempukku.swccgo.cards.LegacyVersionAssertions.assertLegacyVersion;

/** BoShek, Brash Smuggler PC-E1 version of 1261_65. */
public class Card_2261_1065_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of("v", "2261_1065", "current", "1261_65")), new HashMap<>(Map.of()),
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void IdentityAndVersionInfo() {
        var scn = scenario();
        assertLegacyVersion(scn.GetLSCard("v").getBlueprint(), scn.GetLSCard("current").getBlueprint(), "1261_65", "PC-E1",
                LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27),
                "Adds 3 to power of anything he pilots. When piloting a freighter at a battleground system, adds 1 to attrition against opponent here and their starships here may not 'cloak.' Opponent loses no more than 3 Force to your Force drains at same system.");
    }
}
