package com.gempukku.swccgo.cards.set2250.light;

import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.gempukku.swccgo.cards.LegacyVersionAssertions.assertLegacyVersion;

/** Master Qui-Gon (V) PC-E2 version of 1250_44 (PC website card list; Batch S1 date order). */
public class Card_2250_1044_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of("v", "2250_1044", "current", "1250_44")), new HashMap<>(Map.of()),
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void IdentityAndVersionInfo() {
        var scn = scenario();
        assertLegacyVersion(scn.GetLSCard("v").getBlueprint(), scn.GetLSCard("current").getBlueprint(), "1250_44", "PC-E2",
                LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25),
                "Deploys -1 to [Episode I] locations. If you just lost Force from an opponent's Effect, may take an [Episode I] Interrupt (except Control) into hand from Reserve Deck; reshuffle, or place a card from hand under Credits Will Do Fine. Immune to You Are Beaten and attrition. May not be Disarmed.");
    }
}
