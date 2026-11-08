package com.gempukku.swccgo.cards.set2273.dark;

import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

import static com.gempukku.swccgo.cards.set2271.VersionAsserts.assertVersion;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** Original IG-100 MagnaGuard (Jun 2011 slip, printed as IG Bodyguard Droid: "or with Cyborg Commander"), a version of 1273_6. */
public class Card_2273_006_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("luke", "1_19");
        }}, new HashMap<>() {{
            put("magnaguard", "2273_6");
            put("current", "1273_6");
            put("cyborgCommander", "1271_1");
            put("generalGrievous", "203_27");
        }}, 10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts, StartingSetup.NoLSShields,
                StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void OriginalMagnaGuardIsTheCurrentCardWithTheJune2011Text() {
        var s = scenario();
        assertVersion(s.GetDSCard("magnaguard").getBlueprint(), s.GetDSCard("current").getBlueprint(), "1273_6", "",
                LocalDate.of(2011, 6, 3), LocalDate.of(2013, 4, 27),
                "Shockstaff deploys and fires for free on this droid. While on Blockade Flagship or Coruscant (or with Cyborg Commander), this droid is Heroic and power +2. Your characters here are immune to Ambush, Clash Of Sabers, and attrition < 4.");
    }

    @Test
    public void OriginalMagnaGuardIsHeroicAndPowerPlusTwoWithCyborgCommander() {
        var s = scenario();
        var magnaguard = s.GetDSCard("magnaguard");
        s.StartGame();
        s.MoveCardsToLocation(s.GetDSStartingLocation(), magnaguard, s.GetDSCard("cyborgCommander"));
        assertEquals(5, s.GetPower(magnaguard));
        assertTrue(s.HasKeyword(magnaguard, Keyword.HEROIC));
    }

    @Test
    public void OriginalMagnaGuardGetsNothingWithAnotherGrievousButTheCurrentCardDoes() {
        var s = scenario();
        var magnaguard = s.GetDSCard("magnaguard");
        var current = s.GetDSCard("current");
        s.StartGame();
        s.MoveCardsToLocation(s.GetDSStartingLocation(), magnaguard, current, s.GetDSCard("generalGrievous"));
        assertEquals(3, s.GetPower(magnaguard));
        assertFalse(s.HasKeyword(magnaguard, Keyword.HEROIC));
        assertEquals(5, s.GetPower(current));
    }
}
