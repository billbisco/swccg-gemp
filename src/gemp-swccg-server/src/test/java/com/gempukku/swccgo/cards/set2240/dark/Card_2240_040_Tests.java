package com.gempukku.swccgo.cards.set2240.dark;

import com.gempukku.swccgo.cards.LegacyVersionAsserts;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

/** Original If The Trace Was Correct (V) (Dec 2009 sheet), a version of 1240_40. */
public class Card_2240_040_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                }},
                new HashMap<>() {{
                    put("trace", "2240_40");
                    put("current", "1240_40");
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
    public void IfTheTraceWasCorrectIsTheSameCardWithTheOldText() {
        var scn = GetScenario();
        LegacyVersionAsserts.assertVersionOf(scn.GetDSCard("current").getBlueprint(), scn.GetDSCard("trace").getBlueprint(),
                "Deploy on table. Instead of firing Galen's Lightsaber, may 'throw' it to Used Pile: draw destiny; choose up to half that number (round up) of opponent's ability < 3 characters present to be hit. Galen's Fighter may deploy to sites, moves for free (unless relocating), is immune to attrition < 4, and you may use 3 Force to relocate it to a battleground site (as a regular move during your move phase). If it just moved to a site opponent occupies, may take top card of Used Pile into hand (or if it just landed, any one card in Used Pile; reshuffle).",
                "1240_40", "", LocalDate.of(2009, 12, 7), LocalDate.of(2013, 4, 27));
    }
}
