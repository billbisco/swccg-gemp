package com.gempukku.swccgo.cards.set2261.light;

import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.gempukku.swccgo.cards.LegacyVersionAssertions.assertLegacyVersion;

/** Maneuvering Flaps & Nick Of Time original version of 1261_82. */
public class Card_2261_082_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of("v", "2261_82", "current", "1261_82")), new HashMap<>(Map.of()),
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void IdentityAndVersionInfo() {
        var scn = scenario();
        assertLegacyVersion(scn.GetLSCard("v").getBlueprint(), scn.GetLSCard("current").getBlueprint(), "1261_82", "",
                LocalDate.of(2009, 9, 4), LocalDate.of(2012, 7, 21),
                "Deploy on table. Your vehicles with a pilot character or gunner aboard are not hit when 'thrown.' During your deploy phase, may reveal an unpiloted combat vehicle from hand to ▲ its matching pilot character (or vice versa) and deploy both simultaneously. Whenever you play Desperate Tactics, Lucky Shot, On Target, Rapid Fire, or Steady Aim, may draw top card from Reserve Deck. May not be suspended. (Immune to Alter.)");
    }
}
