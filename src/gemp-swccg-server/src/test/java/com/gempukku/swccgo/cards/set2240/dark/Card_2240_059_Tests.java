package com.gempukku.swccgo.cards.set2240.dark;

import com.gempukku.swccgo.cards.LegacyVersionAsserts;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

/** Original Rogue Shadow (Dec 2009 sheet, printed as Galen's Fighter), a version of 1240_59. */
public class Card_2240_059_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                }},
                new HashMap<>() {{
                    put("shadow", "2240_59");
                    put("current", "1240_59");
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
    public void RogueShadowIsTheSameCardWithTheOldText() {
        var scn = GetScenario();
        LegacyVersionAsserts.assertVersionOf(scn.GetDSCard("current").getBlueprint(), scn.GetDSCard("shadow").getBlueprint(),
                "May add 2 pilots and 2 passengers. If Black Leader piloting, adds one battle destiny. If it just took off, may use its hyperspeed and land. Immune to attrition < 7 if Galen on table, even while landed.",
                "1240_59", "", LocalDate.of(2009, 12, 7), LocalDate.of(2013, 4, 27));
    }
}
