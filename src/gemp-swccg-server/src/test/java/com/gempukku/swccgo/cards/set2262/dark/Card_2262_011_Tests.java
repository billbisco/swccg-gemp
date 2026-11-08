package com.gempukku.swccgo.cards.set2262.dark;

import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.gempukku.swccgo.cards.LegacyVersionAssertions.assertLegacyVersion;

/** So Be It, Jedi original version of 1262_11. */
public class Card_2262_011_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of()), new HashMap<>(Map.of("v", "2262_11", "current", "1262_11")),
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void IdentityAndVersionInfo() {
        var scn = scenario();
        assertLegacyVersion(scn.GetDSCard("v").getBlueprint(), scn.GetDSCard("current").getBlueprint(), "1262_11", "",
                LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21),
                "Deploy on table. When Luke deploys using your Objective, he deploys to Vader's site (if possible). Duels initiated with your Objective may not be canceled. Once per game, may ▲ Rise, My Friend. If this Effect canceled by opponent, stack it on Insignificant Rebellion.");
    }
}
