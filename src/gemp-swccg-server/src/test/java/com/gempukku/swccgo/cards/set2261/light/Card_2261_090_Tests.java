package com.gempukku.swccgo.cards.set2261.light;

import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.gempukku.swccgo.cards.LegacyVersionAssertions.assertLegacyVersion;

/** Ascension Guns (V) original version text of 1261_90 (plays the same as its neighbouring version). */
public class Card_2261_090_Tests {
    @Test
    public void IdentityAndVersionInfo() {
        var scn = new VirtualTableScenario(new HashMap<>(Map.of("v", "2261_90", "current", "1261_90")), new HashMap<>(Map.of()),
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        assertLegacyVersion(scn.GetLSCard("v").getBlueprint(), scn.GetLSCard("current").getBlueprint(), "1261_90", "",
                LocalDate.of(2009, 9, 4), LocalDate.of(2012, 8, 21),
                "USED: If opponent occupies your site, peek at the top two cards of Reserve Deck and take one into hand. \nLOST: During any move phase, lose 2 Force to relocate up to 3 of your characters from an interior Theed Palace site to a related Throne Room (or vice versa).");
    }
}
