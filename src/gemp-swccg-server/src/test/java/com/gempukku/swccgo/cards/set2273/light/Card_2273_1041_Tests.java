package com.gempukku.swccgo.cards.set2273.light;

import com.gempukku.swccgo.framework.VirtualTableScenario;

import org.junit.Test;

import java.time.LocalDate;

import static com.gempukku.swccgo.cards.set2271.VersionAsserts.assertLocationVersion;
import static org.junit.Assert.*;

/** Original Coruscant (V) (first printing 2011-06-03), a version of 1273_41: same text as PC-E1, no Dark Force icon. */
public class Card_2273_1041_Tests {
    @Test
    public void OriginalCoruscantIsTheCurrentCardWithItsOwnText() {
        var version = VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("2273_1041");
        var current = VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("1273_41");
        assertLocationVersion(version, current, "1273_41", "", LocalDate.of(2011, 6, 3), LocalDate.of(2011, 6, 22),
                "If just converted, may raise this system to the top.",
                "If your Coruscant system was converted, Force generation +1 here.", true);
    }

    @Test
    public void OriginalCoruscantHasNoDarkForceIcon() {
        var version = VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("2273_1041");
        var current = VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("1273_41");
        assertEquals(0, version.getIconCount(com.gempukku.swccgo.common.Icon.DARK_FORCE));
        assertEquals(1, current.getIconCount(com.gempukku.swccgo.common.Icon.DARK_FORCE));
        assertEquals(current.getIconCount(com.gempukku.swccgo.common.Icon.LIGHT_FORCE), version.getIconCount(com.gempukku.swccgo.common.Icon.LIGHT_FORCE));
        assertEquals(current.getIconCount(com.gempukku.swccgo.common.Icon.PLANET), version.getIconCount(com.gempukku.swccgo.common.Icon.PLANET));
    }

    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new java.util.HashMap<>() {{
            put("version", "2273_1041");
            put("current", "1273_41");
        }}, new java.util.HashMap<>() {{
        }}, 30, 30, com.gempukku.swccgo.framework.StartingSetup.DefaultLSGroundLocation,
                com.gempukku.swccgo.framework.StartingSetup.DefaultDSGroundLocation, com.gempukku.swccgo.framework.StartingSetup.NoLSStartingInterrupts,
                com.gempukku.swccgo.framework.StartingSetup.NoDSStartingInterrupts, com.gempukku.swccgo.framework.StartingSetup.NoLSShields,
                com.gempukku.swccgo.framework.StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void OriginalCoruscantIsTheSameUniqueCardAsTheCurrentCard() {
        com.gempukku.swccgo.cards.set2271.VersionAsserts.assertSameUniqueCard(this::scenario, false, "version", "current", (s, c) -> {
            if (c != null) s.MoveLocationToTable(c);
        });
    }
}
