package com.gempukku.swccgo.cards.set2250.light;

import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.gempukku.swccgo.cards.LegacyVersionAssertions.assertLegacyVersion;

/** Jedi Survivor original version of 1250_41. */
public class Card_2250_041_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of("v", "2250_41", "current", "1250_41")), new HashMap<>(Map.of()),
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void IdentityAndVersionInfo() {
        var scn = scenario();
        assertLegacyVersion(scn.GetLSCard("v").getBlueprint(), scn.GetLSCard("current").getBlueprint(), "1250_41", "",
                LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21),
                "When deployed, you may not initiate battle here this turn. While at opponent's site, adds one [Light Side Force] and one [Dark Side Force]. May 'swing' lightsabers for free. May never be won in sabacc. Immune to attrition < 5. End of your turn: [Maintenance] Use 1 or [Recycle] Lose 1 or [Skull].");
    }
}
