package com.gempukku.swccgo.cards.set2261.dark;

import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.gempukku.swccgo.cards.LegacyVersionAssertions.assertLegacyVersion;

/** TIE Sentry Ships (V) original version of 1261_50. */
public class Card_2261_050_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of()), new HashMap<>(Map.of("v", "2261_50", "current", "1261_50")),
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void IdentityAndVersionInfo() {
        var scn = scenario();
        assertLegacyVersion(scn.GetDSCard("v").getBlueprint(), scn.GetDSCard("current").getBlueprint(), "1261_50", "",
                LocalDate.of(2009, 9, 4), LocalDate.of(2012, 7, 21),
                "USED: If you just drew a unique (•) Imperial starship for destiny, take that starship into hand to cancel and redraw that destiny. LOST: Use 3 Force to retrieve a unique (•) Imperial starship into hand.");
    }
}
