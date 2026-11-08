package com.gempukku.swccgo.cards.set2263.dark;

import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.gempukku.swccgo.cards.LegacyVersionAssertions.assertLegacyVersion;

/** Mercenary Shadow Pirate PC-E1 version text of 1263_6 (plays the same as its neighbouring version). */
public class Card_2263_006_Tests {
    @Test
    public void IdentityAndVersionInfo() {
        var scn = new VirtualTableScenario(new HashMap<>(Map.of()), new HashMap<>(Map.of("v", "2263_6", "current", "1263_6")),
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        assertLegacyVersion(scn.GetDSCard("v").getBlueprint(), scn.GetDSCard("current").getBlueprint(), "1263_6", "PC-E1",
                LocalDate.of(2013, 4, 27), null,
                "[Pilot] 2. Opponent may not add destiny draws to power or attrition here. Sandwhirl and Ice Storm are canceled at same and related locations.");
    }
}
