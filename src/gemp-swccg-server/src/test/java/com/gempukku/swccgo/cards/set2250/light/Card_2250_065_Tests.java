package com.gempukku.swccgo.cards.set2250.light;

import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.gempukku.swccgo.cards.LegacyVersionAssertions.assertLegacyVersion;

/** Ambush (V) original version of 1250_65. */
public class Card_2250_065_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of("v", "2250_65", "current", "1250_65")), new HashMap<>(Map.of()),
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void IdentityAndVersionInfo() {
        var scn = scenario();
        assertLegacyVersion(scn.GetLSCard("v").getBlueprint(), scn.GetLSCard("current").getBlueprint(), "1250_65", "",
                LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21),
                "Use 1 Force to deploy up to two clones from Reserve Deck; reshuffle. OR If your clones occupy three related battlegrounds during your control phase, use 2 Force (free if at Coruscant) to retrieve any one card into hand. OR If your clone in battle, add one battle destiny.");
    }
}
