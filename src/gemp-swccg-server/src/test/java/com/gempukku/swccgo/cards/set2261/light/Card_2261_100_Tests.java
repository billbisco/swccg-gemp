package com.gempukku.swccgo.cards.set2261.light;

import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.gempukku.swccgo.cards.LegacyVersionAssertions.assertLegacyVersion;

/** Scomp Link Access (V) original version of 1261_100. */
public class Card_2261_100_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of("v", "2261_100", "current", "1261_100")), new HashMap<>(Map.of()),
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void IdentityAndVersionInfo() {
        var scn = scenario();
        assertLegacyVersion(scn.GetLSCard("v").getBlueprint(), scn.GetLSCard("current").getBlueprint(), "1261_100", "",
                LocalDate.of(2009, 9, 4), LocalDate.of(2012, 7, 21),
                "USED: ▲ a non-[Episode I] droid (except IL-19). OR Place a card from hand on Used Pile. STARTING: ▼ two always [Immune to Alter] Effects. If you have only one location (with < 3 [Light Side Force]) (except Massassi Throne Room) on table, may ▼ a [Cloud City] site. Place this Interrupt in hand.");
    }
}
