package com.gempukku.swccgo.cards.set2261.dark;

import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.gempukku.swccgo.cards.LegacyVersionAssertions.assertLegacyVersion;

/** Tarkin's Bounty (V) original version of 1261_40. */
public class Card_2261_040_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of()), new HashMap<>(Map.of("v", "2261_40", "current", "1261_40")),
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void IdentityAndVersionInfo() {
        var scn = scenario();
        assertLegacyVersion(scn.GetDSCard("v").getBlueprint(), scn.GetDSCard("current").getBlueprint(), "1261_40", "",
                LocalDate.of(2009, 9, 4), LocalDate.of(2012, 7, 21),
                "Deploy on table. Each Amidala stacked on a Political Effect is a senator. While you have < 13 cards in hand, opponent may not peek at or remove them (except with Grimtaash). Once per turn, may ▲ [Virtual] Astromech Shortage or lose 2 Force to cancel a Political Effect. (Immune to Alter.)");
    }
}
