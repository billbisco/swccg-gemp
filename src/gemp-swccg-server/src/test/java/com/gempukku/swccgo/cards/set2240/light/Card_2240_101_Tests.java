package com.gempukku.swccgo.cards.set2240.light;

import com.gempukku.swccgo.cards.LegacyVersionAsserts;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

/** Original Outpost (Dec 2009 sheet), a version of 1240_101. */
public class Card_2240_101_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("outpost", "2240_101");
                    put("current", "1240_101");
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
    public void OutpostIsTheSameCardWithTheOldText() {
        var scn = GetScenario();
        LegacyVersionAsserts.assertVersionOf(scn.GetLSCard("current").getBlueprint(), scn.GetLSCard("outpost").getBlueprint(),
                "Light: May not deploy to Bespin, Coruscant, Dantooine, Kessel or Tatooine. Once per game, may ▼ a device (except a comlink) or Effect that deploys on a character here. Dark: Unless opponent's matching operative at same or related site, those sites are immune to [Premium] No Escape.",
                "1240_101", "", LocalDate.of(2009, 12, 7), LocalDate.of(2011, 8, 26));
    }

    @Test
    public void OutpostSideTextsAreTheSheetTexts() {
        var scn = GetScenario();
        var outpost = scn.GetLSCard("outpost").getBlueprint();
        assertEquals("May not deploy to Bespin, Coruscant, Dantooine, Kessel or Tatooine. Once per game, may ▼ a device (except a comlink) or Effect that deploys on a character here.",
                outpost.getLocationLightSideGameText());
        assertEquals("Unless opponent's matching operative at same or related site, those sites are immune to [Premium] No Escape.",
                outpost.getLocationDarkSideGameText());
    }
}
