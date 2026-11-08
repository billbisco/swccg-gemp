package com.gempukku.swccgo.cards.set2261.dark;

import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.gempukku.swccgo.cards.LegacyVersionAssertions.assertLegacyVersion;

/** Darth Vader, Betrayer Of The Jedi PC-E1 version text of 1261_10 (plays the same as its neighbouring version). */
public class Card_2261_010_Tests {
    @Test
    public void IdentityAndVersionInfo() {
        var scn = new VirtualTableScenario(new HashMap<>(Map.of()), new HashMap<>(Map.of("v", "2261_10", "current", "1261_10")),
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        assertLegacyVersion(scn.GetDSCard("v").getBlueprint(), scn.GetDSCard("current").getBlueprint(), "1261_10", "PC-E1",
                LocalDate.of(2013, 4, 27), null,
                "[Pilot] 3. The Circle Is Now Complete is canceled. While present with [Reflections III] Obi-Wan or a Jedi Council Member, may add one battle destiny, lightsabers here may not be 'swung' twice, and their immunity to attrition is canceled. Immune to attrition < 5.");
    }
}
