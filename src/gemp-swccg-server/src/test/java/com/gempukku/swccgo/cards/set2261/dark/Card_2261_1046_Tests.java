package com.gempukku.swccgo.cards.set2261.dark;

import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.gempukku.swccgo.cards.LegacyVersionAssertions.assertLegacyVersion;

/** One Beautiful Thing PC-E1 version of 1261_46. */
public class Card_2261_1046_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of()), new HashMap<>(Map.of("v", "2261_1046", "current", "1261_46")),
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void IdentityAndVersionInfo() {
        var scn = scenario();
        assertLegacyVersion(scn.GetDSCard("v").getBlueprint(), scn.GetDSCard("current").getBlueprint(), "1261_46", "PC-E1",
                LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27),
                "Lose 1 Force to take Black Leader into hand from Reserve Deck (or retrieve); reshuffle. OR During battle where you have no Dark Jedi or droids and only two participating cards with ability (a male and a female), add one destiny to total power (and attrition against you = 0).");
    }
}
