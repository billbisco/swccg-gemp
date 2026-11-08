package com.gempukku.swccgo.cards.set2263.dark;

import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.gempukku.swccgo.cards.LegacyVersionAssertions.assertLegacyVersion;

/** IG-88, Renegade Droid original version of 1263_3. */
public class Card_2263_003_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of()), new HashMap<>(Map.of("v", "2263_3", "current", "1263_3")),
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void IdentityAndVersionInfo() {
        var scn = scenario();
        assertLegacyVersion(scn.GetDSCard("v").getBlueprint(), scn.GetDSCard("current").getBlueprint(), "1263_3", "",
                LocalDate.of(2010, 3, 15), LocalDate.of(2012, 7, 21),
                "[Pilot] 2, 3: IG-2000. Deploys -3 aboard IG-2000. Adds one battle destiny if alone or with a smuggler. During battle, if piloting a starship, may draw a destiny. Starship is immune to attrition < X, or its immunity is +X (limit +2), where X = destiny.");
    }
}
