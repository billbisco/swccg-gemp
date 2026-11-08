package com.gempukku.swccgo.cards.set2261.light;

import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.gempukku.swccgo.cards.LegacyVersionAssertions.assertLegacyVersion;

/** Luke Skywalker, Strong In The Force original version of 1261_71. */
public class Card_2261_071_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of("v", "2261_71", "current", "1261_71")), new HashMap<>(Map.of()),
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void IdentityAndVersionInfo() {
        var scn = scenario();
        assertLegacyVersion(scn.GetLSCard("v").getBlueprint(), scn.GetLSCard("current").getBlueprint(), "1261_71", "",
                LocalDate.of(2009, 9, 4), LocalDate.of(2012, 7, 21),
                "[Pilot] 3. While piloting Red 5 or Rogue 1 (or armed with Luke's Lightsaber), may add one destiny to total power or attrition, and I Have You Now is canceled. When leaving table, place all your cards on Luke in Used Pile. Immune to attrition < 4.");
    }
}
