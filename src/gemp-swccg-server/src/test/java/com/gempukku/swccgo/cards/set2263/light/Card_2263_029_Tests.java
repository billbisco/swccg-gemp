package com.gempukku.swccgo.cards.set2263.light;

import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.gempukku.swccgo.cards.LegacyVersionAssertions.assertLegacyVersion;

/** I'm Here To Rescue You (V) original version of 1263_29. */
public class Card_2263_029_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of("v", "2263_29", "current", "1263_29")), new HashMap<>(Map.of()),
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void IdentityAndVersionInfo() {
        var scn = scenario();
        assertLegacyVersion(scn.GetLSCard("v").getBlueprint(), scn.GetLSCard("current").getBlueprint(), "1263_29", "",
                LocalDate.of(2010, 3, 15), LocalDate.of(2012, 7, 21),
                "If Rescue The Princess on table, LOST: Once per game, choose: Cancel an attempt to make non-Heroic R2-D2 lost. OR Lose 2 Force to cancel a battle just initiated if Leia defending alone. (Immune to Sense.) STARTING: ▼ Cell 2187 and one always-[Immune to Alter] Effect. Place this Interrupt in hand.");
    }
}
