package com.gempukku.swccgo.cards.set2230.dark;

import com.gempukku.swccgo.cards.LegacyVersionAsserts;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

/** Original Lieutenant Grond (V) (Virtual Block 3 sheet, Dec 2009), a version of 1230_10. */
public class Card_2230_010_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                }},
                new HashMap<>() {{
                    put("version", "2230_10");
                    put("current", "1230_10");
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
    public void LieutenantGrondIsTheSameCardWithTheOldText() {
        var scn = GetScenario();
        LegacyVersionAsserts.assertVersionOf(scn.GetDSCard("current").getBlueprint(), scn.GetDSCard("version").getBlueprint(),
                "Adds 2 to power of anything he pilots. Adds one battle destiny with Colonel Dyer or if driving a swoop. May place Lieutenant Grond in Used Pile to cancel a Force drain at a related location.",
                "1230_10", "", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21));
    }

    @Test
    public void LieutenantGrondAndItsCurrentPrintingAreTheSameUniqueCard() {
        var scn = GetScenario();
        var version = scn.GetDSCard("version");
        var current = scn.GetDSCard("current");
        scn.StartGame();
        assertFalse(scn.game().getModifiersQuerying().isUniquenessOnTableLimitReached(scn.gameState(), current));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), version);
        assertTrue(scn.game().getModifiersQuerying().isUniquenessOnTableLimitReached(scn.gameState(), current));
    }
}
