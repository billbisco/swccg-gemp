package com.gempukku.swccgo.cards.set2261.light;

import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.gempukku.swccgo.cards.LegacyVersionAssertions.assertLegacyVersion;

/** Wedge In Red Squadron 1 original version text of 1261_107 (plays the same as its neighbouring version). */
public class Card_2261_107_Tests {
    @Test
    public void IdentityAndVersionInfo() {
        var scn = new VirtualTableScenario(new HashMap<>(Map.of("v", "2261_107", "current", "1261_107")), new HashMap<>(Map.of()),
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        assertLegacyVersion(scn.GetLSCard("v").getBlueprint(), scn.GetLSCard("current").getBlueprint(), "1261_107", "",
                LocalDate.of(2009, 9, 4), LocalDate.of(2013, 4, 27),
                "Permanent pilot is •Wedge, who provides ability of 3. During a battle with opponent's [Independent] starship, may add one destiny to attrition or total power. Immune to Come With Me, Tallon Roll, and attrition < 4.");
    }
}
