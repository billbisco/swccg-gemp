package com.gempukku.swccgo.cards.set2230.dark;

import com.gempukku.swccgo.cards.LegacyVersionAsserts;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

/** Original Bantha Herd (V) (Virtual Block 3 sheet, Dec 2009), a version of 1230_24. */
public class Card_2230_024_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                }},
                new HashMap<>() {{
                    put("version", "2230_24");
                    put("current", "1230_24");
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
    public void BanthaHerdIsTheSameCardWithTheOldText() {
        var scn = GetScenario();
        LegacyVersionAsserts.assertVersionOf(scn.GetDSCard("current").getBlueprint(), scn.GetDSCard("version").getBlueprint(),
                "Deploy on a Tatooine site. Once per turn, may place up to 3 cards from hand face down here. If a battle was just initiated against your lone Tusken Raider, may 'react' by deploying (for free) any Tusken Raider(s) and/or Bantha(s) here. (Immune to Alter.)",
                "1230_24", "", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21));
    }
}
