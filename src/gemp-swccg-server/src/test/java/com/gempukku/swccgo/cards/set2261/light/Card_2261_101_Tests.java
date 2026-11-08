package com.gempukku.swccgo.cards.set2261.light;

import com.gempukku.swccgo.cards.set1261.light.SoloDefenseTestSupport;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.gempukku.swccgo.cards.LegacyVersionAssertions.assertLegacyVersion;

/** Original Solo Han (V) (Virtual Block 6.1 sheet), a version of 1261_101: the shared solo-defense tests run on it. */
public class Card_2261_101_Tests extends SoloDefenseTestSupport {
    public Card_2261_101_Tests() { super("2261_101", "1_11", "13_21", "5_79"); }

    @Test
    public void IdentityAndVersionInfo() {
        var scn = new VirtualTableScenario(new HashMap<>(Map.of("v", "2261_101", "current", "1261_101")), new HashMap<>(),
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        assertLegacyVersion(scn.GetLSCard("v").getBlueprint(), scn.GetLSCard("current").getBlueprint(), "1261_101", "",
                LocalDate.of(2009, 9, 4), LocalDate.of(2012, 7, 21),
                "Use 1 Force to ▼ (or retrieve) non-[Reflections III] Han. OR If a battle just initiated, and Han defending alone at a site, his forfeit cannot be reduced, he adds one destiny to power (+1 for each character and vehicle present), and draws one battle destiny if not able to otherwise. (Immune to Sense.)");
    }
}
