package com.gempukku.swccgo.cards.set2230.dark;

import com.gempukku.swccgo.cards.LegacyVersionAsserts;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

/** Original Dead Ewok (V) (Virtual Block 3 sheet, Dec 2009), a version of 1230_41. */
public class Card_2230_041_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                }},
                new HashMap<>() {{
                    put("version", "2230_41");
                    put("current", "1230_41");
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
    public void DeadEwokIsTheSameCardWithTheOldText() {
        var scn = GetScenario();
        LegacyVersionAsserts.assertVersionOf(scn.GetDSCard("current").getBlueprint(), scn.GetDSCard("version").getBlueprint(),
                "Cancel Ellorrs Madak. (Immune to Sense.) OR Use 2 Force to cancel the game text of an opponent's alien for remainder of turn. OR Place an Ewok Catapult in opponent's Used Pile. OR Make an Ewok power and forfeit = 0 for remainder of turn.",
                "1230_41", "", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21));
    }
}
