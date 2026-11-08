package com.gempukku.swccgo.cards.set2230.light;

import com.gempukku.swccgo.cards.LegacyVersionAsserts;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

/** Original Red Squadron 7 (V) (Virtual Block 3 sheet, Dec 2009), a version of 1230_113. */
public class Card_2230_113_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("version", "2230_113");
                    put("current", "1230_113");
                }},
                new HashMap<>() {{
                }},
                20, 20,
                StartingSetup.DefaultLSSpaceSystem,
                StartingSetup.DefaultDSSpaceSystem,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void RedSquadronIsTheSameCardWithTheOldText() {
        var scn = GetScenario();
        LegacyVersionAsserts.assertVersionOf(scn.GetLSCard("current").getBlueprint(), scn.GetLSCard("version").getBlueprint(),
                "Heroic. May add 1 pilot. Once per game may place a non-[Immune to Alter] Effect at same system in owner's Used Pile. Corran Horn and Keir Santage deploy -1 aboard; if either piloting, immune to attrition < 4.",
                "1230_113", "", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21));
    }

    @Test
    public void RedSquadronAndItsCurrentPrintingAreTheSameUniqueCard() {
        var scn = GetScenario();
        var version = scn.GetLSCard("version");
        var current = scn.GetLSCard("current");
        scn.StartGame();
        assertFalse(scn.game().getModifiersQuerying().isUniquenessOnTableLimitReached(scn.gameState(), current));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), version);
        assertTrue(scn.game().getModifiersQuerying().isUniquenessOnTableLimitReached(scn.gameState(), current));
    }
}
