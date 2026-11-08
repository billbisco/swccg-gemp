package com.gempukku.swccgo.cards.set2261.dark;

import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.gempukku.swccgo.cards.LegacyVersionAssertions.assertLegacyVersion;

/** Epic Duel (V) original version of 1261_39. */
public class Card_2261_039_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of()), new HashMap<>(Map.of("v", "2261_39", "current", "1261_39")),
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void IdentityAndVersionInfo() {
        var scn = scenario();
        assertLegacyVersion(scn.GetDSCard("v").getBlueprint(), scn.GetDSCard("current").getBlueprint(), "1261_39", "",
                LocalDate.of(2009, 9, 4), LocalDate.of(2012, 7, 21),
                "Deploy on table. A Jedi's Resilience may not be played during move phases. The Circle Is Now Complete or Vader's Obsession may be played during any move phase to initiate an epic duel as follows: If Vader present with specified target, each player draws two destiny (if armed with a lightsaber, each destiny is +1); add ability. Loser (lower total) is lost, losing player loses 2 Force, and winning player may retrieve 2 Force.");
    }
}
