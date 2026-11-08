package com.gempukku.swccgo.cards.set2261.light;

import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.gempukku.swccgo.cards.LegacyVersionAssertions.assertLegacyVersion;

/** I've Decided To Go Back (V) original version of 1261_96. */
public class Card_2261_096_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of("v", "2261_96", "current", "1261_96")), new HashMap<>(Map.of()),
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void IdentityAndVersionInfo() {
        var scn = scenario();
        assertLegacyVersion(scn.GetLSCard("v").getBlueprint(), scn.GetLSCard("current").getBlueprint(), "1261_96", "",
                LocalDate.of(2009, 9, 4), LocalDate.of(2012, 7, 21),
                "Use 1 Force to ▼ (or retrieve) Panaka, Ric Olie, or non-[Virtual] Amidala. OR If your ability = 3 character defending a battle alone at a site (except Senate), character is power +2, adds one destiny to power, and may draw one battle destiny if unable to otherwise. (Immune to Sense.)");
    }
}
