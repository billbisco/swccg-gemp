package com.gempukku.swccgo.cards.set2261.light;

import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.gempukku.swccgo.cards.LegacyVersionAssertions.assertLegacyVersion;

/** Jabba's Prize original version of 1261_67. */
public class Card_2261_067_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of("v", "2261_67", "current", "1261_67")), new HashMap<>(Map.of()),
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void IdentityAndVersionInfo() {
        var scn = scenario();
        assertLegacyVersion(scn.GetLSCard("v").getBlueprint(), scn.GetLSCard("current").getBlueprint(), "1261_67", "",
                LocalDate.of(2009, 9, 4), LocalDate.of(2012, 7, 21),
                "Jabba's Prize is a Light Side card and does not count towards your deck limit (but subtracts one from the number of cards you may place under your Starting Effect). Reveal to opponent when deploying your Starting Effect. For remainder of game, you may not deploy [Maintenance] Falcon. Deploys only at start of game if Jabba's Prize is at Security Tower (replaces opponent's Jabba's Prize imprisoned in Security Tower); otherwise place out of play. May not be placed in Reserve Deck. Jabba's Prize is a persona of Corran Horn only while on table. If Jabba's Prize was just released or leaves table, place it out of play. While Jabba's Prize is at Audience Chamber, opponent's battle destiny draws there are +1.");
    }
}
