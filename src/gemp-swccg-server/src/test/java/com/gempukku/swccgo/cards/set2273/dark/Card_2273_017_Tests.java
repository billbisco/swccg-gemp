package com.gempukku.swccgo.cards.set2273.dark;

import com.gempukku.swccgo.framework.VirtualTableScenario;

import org.junit.Test;

import java.time.LocalDate;

import static com.gempukku.swccgo.cards.set2271.VersionAsserts.assertLocationVersion;
import static org.junit.Assert.*;

/** Original Coruscant: Palpatine's Quarters (2011-06-03 sheet), a version of 1273_17: only Cyborg Commander (Grievous, Hunter Of Jedi) is immune to attrition. */
public class Card_2273_017_Tests {
    @Test
    public void OriginalPalpatinesQuartersIsTheCurrentCardWithItsOwnText() {
        var version = VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("2273_17");
        var current = VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("1273_17");
        assertLocationVersion(version, current, "1273_17", "", LocalDate.of(2011, 6, 3), LocalDate.of(2013, 4, 27),
                "Force drain -1 here . Jedi here may not have their power reduced.",
                "While Insidious Prisoner on table, Cyborg Commander is immune to attrition.", false);
    }

    @Test
    public void OriginalPalpatinesQuartersProtectsOnlyTheCardPrintedAsCyborgCommander() {
        var s = scenario();
        var quarters = s.GetDSCard("version");
        var hunter = s.GetDSCard("hunter");
        var general = s.GetDSCard("grievous");
        s.StartGame();
        s.MoveLocationToTable(quarters);
        s.AttachCardsTo(quarters, s.GetDSCard("prisoner"));
        var q = s.game().getModifiersQuerying();
        s.MoveCardsToLocation(quarters, hunter);
        assertEquals(Float.MAX_VALUE, q.getImmunityToAttritionLessThan(s.gameState(), hunter), s.epsilon);
        s.MoveCardsToDSHand(hunter);
        s.MoveCardsToLocation(quarters, general);
        assertTrue(q.getImmunityToAttritionLessThan(s.gameState(), general) < Float.MAX_VALUE);
    }

    @Test
    public void CurrentPalpatinesQuartersProtectsAnyGrievous() {
        var s = scenario();
        var quarters = s.GetDSCard("current");
        var general = s.GetDSCard("grievous");
        s.StartGame();
        s.MoveLocationToTable(quarters);
        s.AttachCardsTo(quarters, s.GetDSCard("prisoner"));
        s.MoveCardsToLocation(quarters, general);
        assertEquals(Float.MAX_VALUE, s.game().getModifiersQuerying().getImmunityToAttritionLessThan(s.gameState(), general), s.epsilon);
    }

    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new java.util.HashMap<>() {{
        }}, new java.util.HashMap<>() {{
            put("hunter", "1271_1");
            put("grievous", "203_27");
            put("prisoner", "211_11");
            put("version", "2273_17");
            put("current", "1273_17");
        }}, 30, 30, com.gempukku.swccgo.framework.StartingSetup.DefaultLSGroundLocation,
                com.gempukku.swccgo.framework.StartingSetup.DefaultDSGroundLocation, com.gempukku.swccgo.framework.StartingSetup.NoLSStartingInterrupts,
                com.gempukku.swccgo.framework.StartingSetup.NoDSStartingInterrupts, com.gempukku.swccgo.framework.StartingSetup.NoLSShields,
                com.gempukku.swccgo.framework.StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void OriginalPalpatinesQuartersIsTheSameUniqueCardAsTheCurrentCard() {
        com.gempukku.swccgo.cards.set2271.VersionAsserts.assertSameUniqueCard(this::scenario, true, "version", "current", (s, c) -> {
            if (c != null) s.MoveLocationToTable(c);
        });
    }
}
