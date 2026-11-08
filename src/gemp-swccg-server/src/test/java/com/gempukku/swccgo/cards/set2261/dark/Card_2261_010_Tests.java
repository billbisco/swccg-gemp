package com.gempukku.swccgo.cards.set2261.dark;

import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.gempukku.swccgo.cards.LegacyVersionAssertions.assertLegacyVersion;

/** Darth Vader, Betrayer Of The Jedi original version of 1261_10. */
public class Card_2261_010_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of()), new HashMap<>(Map.of("v", "2261_10", "current", "1261_10")),
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void IdentityAndVersionInfo() {
        var scn = scenario();
        assertLegacyVersion(scn.GetDSCard("v").getBlueprint(), scn.GetDSCard("current").getBlueprint(), "1261_10", "",
                LocalDate.of(2009, 9, 4), LocalDate.of(2013, 4, 27),
                "[Pilot] 3. The Circle Is Now Complete is canceled. While present with [Reflections III] Obi-Wan or a Jedi Council Member, lightsabers here may not be 'swung' twice, their immunity to attrition is canceled and you may add one battle destiny. Immune to attrition < 5.");
    }
}
