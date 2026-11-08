package com.gempukku.swccgo.cards.set2250.light;

import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.gempukku.swccgo.cards.LegacyVersionAssertions.assertLegacyVersion;

/** Clone Pilot original version of 1250_35. */
public class Card_2250_035_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of("v", "2250_35", "current", "1250_35")), new HashMap<>(Map.of()),
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void IdentityAndVersionInfo() {
        var scn = scenario();
        assertLegacyVersion(scn.GetLSCard("v").getBlueprint(), scn.GetLSCard("current").getBlueprint(), "1250_35", "",
                LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21),
                "[Pilot] 2. While piloting a [Republic] starship at a battleground, forfeit +1 and your Force drains are +1 here. Anything he pilots is manuever +1 and immune to attrition < 3 (< 5 if all your pilot characters here are clones and/or Jedi).");
    }
}
