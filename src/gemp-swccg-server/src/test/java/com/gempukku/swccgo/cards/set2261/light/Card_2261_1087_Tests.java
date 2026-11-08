package com.gempukku.swccgo.cards.set2261.light;

import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.gempukku.swccgo.cards.LegacyVersionAssertions.assertLegacyVersion;

/** Strong Is Vader PC-E1 version of 1261_87. */
public class Card_2261_1087_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of("v", "2261_1087", "current", "1261_87")), new HashMap<>(Map.of()),
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void IdentityAndVersionInfo() {
        var scn = scenario();
        assertLegacyVersion(scn.GetLSCard("v").getBlueprint(), scn.GetLSCard("current").getBlueprint(), "1261_87", "PC-E1",
                LocalDate.of(2010, 4, 20), LocalDate.of(2012, 7, 21),
                "If [Virtual] Mind What You Have Learned on table, deploy on table with up to 6 Jedi Tests under here from outside your deck. You may not Force drain at Dagobah locations or deploy asteroid sectors, [Maintenance] cards, or [Permanent Weapon] cards. Whenever you draw training destiny, draw two and choose one. You may retrieve 3 Force whenever you complete a Jedi Test. Completed Jedi Tests are placed on table and are suspended (not lost) whenever character who completed them not on table.");
    }
}
