package com.gempukku.swccgo.cards.set2273.light;

import com.gempukku.swccgo.framework.VirtualTableScenario;

import org.junit.Test;

import java.time.LocalDate;

import static com.gempukku.swccgo.cards.set2271.VersionAsserts.assertLocationVersion;
import static org.junit.Assert.*;

/** Coruscant (V) PC-E1 (2011-06-23 errata), a version of 1273_41: same play, "Force generation +1 here". */
public class Card_2273_041_Tests {
    @Test
    public void ErrataCoruscantIsTheCurrentCardWithItsOwnText() {
        var version = VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("2273_41");
        var current = VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("1273_41");
        assertLocationVersion(version, current, "1273_41", "PC-E1", LocalDate.of(2011, 6, 22), LocalDate.of(2012, 3, 15),
                "If just converted, may raise this system to the top.",
                "If your Coruscant system was converted, Force generation +1 here.", false);
    }

    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new java.util.HashMap<>() {{
            put("version", "2273_41");
            put("current", "1273_41");
        }}, new java.util.HashMap<>() {{
        }}, 30, 30, com.gempukku.swccgo.framework.StartingSetup.DefaultLSGroundLocation,
                com.gempukku.swccgo.framework.StartingSetup.DefaultDSGroundLocation, com.gempukku.swccgo.framework.StartingSetup.NoLSStartingInterrupts,
                com.gempukku.swccgo.framework.StartingSetup.NoDSStartingInterrupts, com.gempukku.swccgo.framework.StartingSetup.NoLSShields,
                com.gempukku.swccgo.framework.StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void ErrataCoruscantIsTheSameUniqueCardAsTheCurrentCard() {
        com.gempukku.swccgo.cards.set2271.VersionAsserts.assertSameUniqueCard(this::scenario, false, "version", "current", (s, c) -> {
            if (c != null) s.MoveLocationToTable(c);
        });
    }
}
