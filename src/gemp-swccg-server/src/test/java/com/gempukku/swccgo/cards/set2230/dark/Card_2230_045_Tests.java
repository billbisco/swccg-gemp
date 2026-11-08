package com.gempukku.swccgo.cards.set2230.dark;

import com.gempukku.swccgo.cards.LegacyVersionAsserts;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

/** Original Imperial Tyranny (V) (Virtual Block 3 sheet, Dec 2009), a version of 1230_45. */
public class Card_2230_045_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                }},
                new HashMap<>() {{
                    put("version", "2230_45");
                    put("current", "1230_45");
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
    public void ImperialTyrannyIsTheSameCardWithTheOldText() {
        var scn = GetScenario();
        LegacyVersionAsserts.assertVersionOf(scn.GetDSCard("current").getBlueprint(), scn.GetDSCard("version").getBlueprint(),
                "USED: Deploy Empire's New Order from Reserve Deck; reshuffle. OR If Vader just won a battle at the same site as an alien, place that alien out of play. LOST: If a battle was just initiated where you have a character weapon card, place the top card of your Lost Pile on top of Reserve Deck.",
                "1230_45", "", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21));
    }
}
