package com.gempukku.swccgo.cards.set2230.light;

import com.gempukku.swccgo.cards.LegacyVersionAsserts;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

/** Original Rebel Leadership (V) (Dec 2009 sheet), a version of 1230_109. */
public class Card_2230_109_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("leadership", "2230_109");
                    put("current", "1230_109");
                }},
                new HashMap<>() {{
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
    public void RebelLeadershipIsTheSameCardWithTheOldText() {
        var scn = GetScenario();
        LegacyVersionAsserts.assertVersionOf(scn.GetLSCard("current").getBlueprint(), scn.GetLSCard("leadership").getBlueprint(),
                "▲ an admiral or a non-Jedi general. OR Once per game, ▼ a war room. OR If your admiral or non-[Episode I] general is in battle, may add one battle destiny or prevent opponent from drawing more than one battle destiny (that destiny may not be canceled).",
                "1230_109", "", LocalDate.of(2009, 12, 7), LocalDate.of(2013, 4, 13));
    }
}
