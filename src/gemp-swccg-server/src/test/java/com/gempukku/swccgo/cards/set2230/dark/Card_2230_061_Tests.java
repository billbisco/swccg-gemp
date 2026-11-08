package com.gempukku.swccgo.cards.set2230.dark;

import com.gempukku.swccgo.cards.LegacyVersionAsserts;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

/** Original Feltipern Trevagg's Stun Rifle (V) (Virtual Block 3 sheet, Dec 2009), a version of 1230_61. */
public class Card_2230_061_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                }},
                new HashMap<>() {{
                    put("version", "2230_61");
                    put("current", "1230_61");
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
    public void FeltipernTrevaggSStunRifleIsTheSameCardWithTheOldText() {
        var scn = GetScenario();
        LegacyVersionAsserts.assertVersionOf(scn.GetDSCard("current").getBlueprint(), scn.GetDSCard("version").getBlueprint(),
                "Use 1 Force to deploy on your bounty hunter (free on Feltipern Trevagg). May target a character for free. Draw destiny. Target captured (and may take one Interrupt from Lost Pile into hand) if destiny +2 > defense value.",
                "1230_61", "", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21));
    }
}
