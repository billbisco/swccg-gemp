package com.gempukku.swccgo.cards.set2230.dark;

import com.gempukku.swccgo.cards.LegacyVersionAsserts;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

/** Original Outflank (V) (Virtual Block 3 sheet, Dec 2009), a version of 1230_46. */
public class Card_2230_046_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                }},
                new HashMap<>() {{
                    put("version", "2230_46");
                    put("current", "1230_46");
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
    public void OutflankIsTheSameCardWithTheOldText() {
        var scn = GetScenario();
        LegacyVersionAsserts.assertVersionOf(scn.GetDSCard("current").getBlueprint(), scn.GetDSCard("version").getBlueprint(),
                "If opponent just initiated battle at a site where their total power is greater than yours, may use 1 Force (or cause opponent to use 1 Force) to relocate one of your characters or vehicles to that site from a related location.",
                "1230_46", "", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21));
    }
}
