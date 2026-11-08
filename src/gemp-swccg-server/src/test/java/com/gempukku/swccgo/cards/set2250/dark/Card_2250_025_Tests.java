package com.gempukku.swccgo.cards.set2250.dark;

import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.gempukku.swccgo.cards.LegacyVersionAssertions.assertLegacyVersion;

/** Wipe Them Out, All Of Them (V) original version of 1250_25. */
public class Card_2250_025_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of()), new HashMap<>(Map.of("v", "2250_25", "current", "1250_25")),
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void IdentityAndVersionInfo() {
        var scn = scenario();
        assertLegacyVersion(scn.GetDSCard("v").getBlueprint(), scn.GetDSCard("current").getBlueprint(), "1250_25", "",
                LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21),
                "Deploy on a site. Unless a player controls a battleground site, that player's [Permanent Weapon] weapon destiny draws are -2. During opponent's draw phase, if opponent's non-battleground site (with no related locations) on table, may activate or retrieve 1 Force. (Immune to Alter.)");
    }
}
