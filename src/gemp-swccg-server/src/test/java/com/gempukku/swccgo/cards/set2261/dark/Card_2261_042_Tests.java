package com.gempukku.swccgo.cards.set2261.dark;

import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.gempukku.swccgo.cards.LegacyVersionAssertions.assertLegacyVersion;

/** Lone Pilot (V) original version text of 1261_42 (plays the same as its neighbouring version). */
public class Card_2261_042_Tests {
    @Test
    public void IdentityAndVersionInfo() {
        var scn = new VirtualTableScenario(new HashMap<>(Map.of()), new HashMap<>(Map.of("v", "2261_42", "current", "1261_42")),
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        assertLegacyVersion(scn.GetDSCard("v").getBlueprint(), scn.GetDSCard("current").getBlueprint(), "1261_42", "",
                LocalDate.of(2009, 9, 4), LocalDate.of(2010, 4, 20),
                "USED: Activate 1 Force. STARTING: ▲ Black Leader, Bossk, Dengar, DS-61-2, or Guri (may also ▲ a system with 1 [Dark Side Force], a matching starship, and an always [Immune to Alter] Effect.) When drawing your starting hand, draw four cards instead of eight. Place this Interrupt in Lost Pile.");
    }
}
