package com.gempukku.swccgo.cards.set2261.dark;

import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.gempukku.swccgo.cards.LegacyVersionAssertions.assertLegacyVersion;

/** One Bright Spot PC-E1 version text of 1261_45 (plays the same as its neighbouring version). */
public class Card_2261_1045_Tests {
    @Test
    public void IdentityAndVersionInfo() {
        var scn = new VirtualTableScenario(new HashMap<>(Map.of()), new HashMap<>(Map.of("v", "2261_1045", "current", "1261_45")),
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        assertLegacyVersion(scn.GetDSCard("v").getBlueprint(), scn.GetDSCard("current").getBlueprint(), "1261_45", "PC-E1",
                LocalDate.of(2013, 4, 27), null,
                "If Galen was just lost and Juno Eclipse is on table (or vice versa), place the just-lost character on your Used Pile instead. OR If Juno Eclipse is participating in a battle, may lose Galen from hand to satisfy all battle damage and attrition against you.");
    }
}
