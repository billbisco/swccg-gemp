package com.gempukku.swccgo.cards.set2240.light;

import com.gempukku.swccgo.cards.LegacyVersionAsserts;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

/** Original Careful Planning (V) (Virtual Block 4 sheet, Dec 2009), a version of 1240_94. */
public class Card_2240_094_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("version", "2240_94");
                    put("current", "1240_94");
                }},
                new HashMap<>() {{
                }},
                20, 20,
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
    public void CarefulPlanningIsTheSameCardWithTheOldText() {
        var scn = GetScenario();
        LegacyVersionAsserts.assertVersionOf(scn.GetLSCard("current").getBlueprint(), scn.GetLSCard("version").getBlueprint(),
                "If your starting location was a system, [download] a related site (must be a battleground if the system is a non-battleground or Endor) with < 3 [Light Side Force] and up to three Effects that are always immune to Alter. Place this Interrupt in Lost Pile.",
                "1240_94", "", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21));
    }
}
