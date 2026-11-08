package com.gempukku.swccgo.cards.set2261.light;

import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.gempukku.swccgo.cards.LegacyVersionAssertions.assertLegacyVersion;

/** Don't Underestimate Our Chances (V) original version of 1261_94. */
public class Card_2261_094_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of("v", "2261_94", "current", "1261_94")), new HashMap<>(Map.of()),
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void IdentityAndVersionInfo() {
        var scn = scenario();
        assertLegacyVersion(scn.GetLSCard("v").getBlueprint(), scn.GetLSCard("current").getBlueprint(), "1261_94", "",
                LocalDate.of(2009, 9, 4), LocalDate.of(2012, 7, 21),
                "USED: Initiate a battle for free. STARTING: If Dantooine Base Operations on table, ▼ Operations Center, Dantooine Engineering Corps, and up to two Effects which deploy on table (or your side of table), are always [Immune to Alter], and have no deploy cost. Place this Interrupt in Lost Pile.");
    }
}
