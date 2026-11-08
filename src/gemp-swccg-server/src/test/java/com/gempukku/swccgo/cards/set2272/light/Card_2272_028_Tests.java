package com.gempukku.swccgo.cards.set2272.light;

import com.gempukku.swccgo.framework.VirtualTableScenario;

import org.junit.Test;

import java.time.LocalDate;

import static com.gempukku.swccgo.cards.set2271.VersionAsserts.assertLocationVersion;
import static org.junit.Assert.*;

/** Original Roche (V) (V7 preview, 2010-07-26), a version of 1272_28: same play, "may not target it". */
public class Card_2272_028_Tests {
    @Test
    public void OriginalRocheIsTheCurrentCardWithItsOwnText() {
        var version = VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("2272_28");
        var current = VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("1272_28");
        assertLocationVersion(version, current, "1272_28", "", LocalDate.of(2010, 7, 26), LocalDate.of(2010, 12, 15),
                "Unless opponent controls this system, Commence Primary Ignition may not target it.",
                "Force Drain -1 here. If you control, Asteroid Sanctuary at related sector is canceled.", false);
    }

    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new java.util.HashMap<>() {{
            put("version", "2272_28");
            put("current", "1272_28");
        }}, new java.util.HashMap<>() {{
        }}, 30, 30, com.gempukku.swccgo.framework.StartingSetup.DefaultLSGroundLocation,
                com.gempukku.swccgo.framework.StartingSetup.DefaultDSGroundLocation, com.gempukku.swccgo.framework.StartingSetup.NoLSStartingInterrupts,
                com.gempukku.swccgo.framework.StartingSetup.NoDSStartingInterrupts, com.gempukku.swccgo.framework.StartingSetup.NoLSShields,
                com.gempukku.swccgo.framework.StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void OriginalRocheIsTheSameUniqueCardAsTheCurrentCard() {
        com.gempukku.swccgo.cards.set2271.VersionAsserts.assertSameUniqueCard(this::scenario, false, "version", "current", (s, c) -> {
            if (c != null) s.MoveLocationToTable(c);
        });
    }
}
