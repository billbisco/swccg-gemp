package com.gempukku.swccgo.cards.set2240.dark;

import com.gempukku.swccgo.cards.LegacyVersionAsserts;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

/** Original Galen's Lightsaber (Dec 2009 sheet), a version of 1240_64. */
public class Card_2240_064_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                }},
                new HashMap<>() {{
                    put("saber", "2240_64");
                    put("current", "1240_64");
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void GalensLightsaberIsTheSameCardWithTheOldText() {
        var scn = GetScenario();
        LegacyVersionAsserts.assertVersionOf(scn.GetDSCard("current").getBlueprint(), scn.GetDSCard("saber").getBlueprint(),
                "Deploy on Galen. While Galen defending a battle, his and Black Leader's immunity to attrition is +2. May target a character or creature for free. Draw two destiny. Target hit, power -3, and may not be used to satisfy attrition if total destiny > defense value.",
                "1240_64", "", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21));
    }
}
