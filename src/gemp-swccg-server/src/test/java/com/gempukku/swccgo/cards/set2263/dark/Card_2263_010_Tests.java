package com.gempukku.swccgo.cards.set2263.dark;

import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.gempukku.swccgo.cards.LegacyVersionAssertions.assertLegacyVersion;

/** Revenge Of The Sith original version of 1263_10. */
public class Card_2263_010_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of()), new HashMap<>(Map.of("v", "2263_10", "current", "1263_10")),
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void IdentityAndVersionInfo() {
        var scn = scenario();
        assertLegacyVersion(scn.GetDSCard("v").getBlueprint(), scn.GetDSCard("current").getBlueprint(), "1263_10", "",
                LocalDate.of(2010, 3, 15), LocalDate.of(2012, 7, 21),
                "Deploy on Vader. [Coruscant] holograms are lost. Opponent may not add destiny draws to total power or attrition here. While with a Skywalker or Jedi, Vader's defense value is +2 (maximum of 8) and his immunity to attrition is +2. When Vader leaves table, place Effect in Used Pile. (Immune to Alter.)");
    }
}
