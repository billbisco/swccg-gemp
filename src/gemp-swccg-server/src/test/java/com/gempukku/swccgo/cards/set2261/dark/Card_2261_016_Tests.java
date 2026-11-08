package com.gempukku.swccgo.cards.set2261.dark;

import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.gempukku.swccgo.cards.LegacyVersionAssertions.assertLegacyVersion;

/** Lieutenant Cecius (V) original version of 1261_16. */
public class Card_2261_016_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of()), new HashMap<>(Map.of("v", "2261_16", "current", "1261_16")),
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void IdentityAndVersionInfo() {
        var scn = scenario();
        assertLegacyVersion(scn.GetDSCard("v").getBlueprint(), scn.GetDSCard("current").getBlueprint(), "1261_16", "",
                LocalDate.of(2009, 9, 4), LocalDate.of(2012, 7, 21),
                "Opponent's characters are deploy +1 to same mobile site. At battlegrounds where you have a unique (•) and non-unique Imperial present (and no other Force drain bonus), your Force drains are +1. Tactical Support is a Used Interrupt.");
    }
}
