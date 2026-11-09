package com.gempukku.swccgo.cards.set2261.light;

import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.gempukku.swccgo.cards.LegacyVersionAssertions.assertLegacyVersion;

/** Incom Corporation & Koensayr Manufacturing original version of 1261_81. */
public class Card_2261_081_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of("v", "2261_81", "current", "1261_81")), new HashMap<>(Map.of()),
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void IdentityAndVersionInfo() {
        var scn = scenario();
        assertLegacyVersion(scn.GetLSCard("v").getBlueprint(), scn.GetLSCard("current").getBlueprint(), "1261_81", "",
                LocalDate.of(2009, 9, 4), LocalDate.of(2012, 7, 21),
                "Deploy on a Rebel Base war room. Echo Base Operations is canceled. Your ability-1 permanent pilots aboard T-47s, X-wings, Y-wings, and Z-95s (except squadrons) are replaced by ability-2 permanent pilots who add 1 to power. During your control phase, may retrieve one Y-wing or Z-95. Suspended if opponent controls this site. (Immune to Alter.)");
    }
}
