package com.gempukku.swccgo.cards.set2262.dark;

import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.gempukku.swccgo.cards.LegacyVersionAssertions.assertLegacyVersion;

/** Deployment Orders original version of 1262_6. */
public class Card_2262_006_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of()), new HashMap<>(Map.of("v", "2262_6", "current", "1262_6")),
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void IdentityAndVersionInfo() {
        var scn = scenario();
        assertLegacyVersion(scn.GetDSCard("v").getBlueprint(), scn.GetDSCard("current").getBlueprint(), "1262_6", "",
                LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21),
                "Deploy on table. If your Landing Craft just landed, may use 1 Force to ▼ a [Presence] droid or Neimoidian aboard. Once per turn, may use 1 Force: reveal a [Theed Palace] vehicle (or Landing Craft) from hand to ▲ a [Presence] droid pilot (or vice versa), and deploy both simultaneously. (Immune to Alter.)");
    }
}
