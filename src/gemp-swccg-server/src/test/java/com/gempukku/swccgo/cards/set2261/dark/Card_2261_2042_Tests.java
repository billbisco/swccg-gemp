package com.gempukku.swccgo.cards.set2261.dark;

import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.gempukku.swccgo.cards.LegacyVersionAssertions.assertLegacyVersion;

/** Lone Pilot (V) PC-E2 version of 1261_42. */
public class Card_2261_2042_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of()), new HashMap<>(Map.of("v", "2261_2042", "current", "1261_42")),
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void IdentityAndVersionInfo() {
        var scn = scenario();
        assertLegacyVersion(scn.GetDSCard("v").getBlueprint(), scn.GetDSCard("current").getBlueprint(), "1261_42", "PC-E2",
                LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27),
                "USED: Activate 1 Force. STARTING: Take into hand from Reserve Deck Black Leader, Bossk, Dengar, DS-61-2, or Guri (may also take into hand from Reserve Deck a matching starship, a system with exactly 1 [Dark Side Force], and an always [Immune to Alter] Effect). When drawing your starting hand, draw four cards instead of eight. Place this Interrupt in Lost Pile.");
    }
}
