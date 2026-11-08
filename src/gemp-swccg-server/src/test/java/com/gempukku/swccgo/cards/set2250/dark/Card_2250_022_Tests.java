package com.gempukku.swccgo.cards.set2250.dark;

import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.gempukku.swccgo.cards.LegacyVersionAssertions.assertLegacyVersion;

/** With Thunderous Applause original version of 1250_22. */
public class Card_2250_022_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of()), new HashMap<>(Map.of("v", "2250_22", "current", "1250_22")),
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void IdentityAndVersionInfo() {
        var scn = scenario();
        assertLegacyVersion(scn.GetDSCard("v").getBlueprint(), scn.GetDSCard("current").getBlueprint(), "1250_22", "",
                LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21),
                "Deploy on Sidious. At start of your turn, if at Senate, may draw 2 cards from Reserve Deck. During your move phase, relocate this Effect to a Rebel or Jedi at a battleground; that character loses all immunity to attrition. If this Effect just lost, relocate it to Sidious.");
    }
}
