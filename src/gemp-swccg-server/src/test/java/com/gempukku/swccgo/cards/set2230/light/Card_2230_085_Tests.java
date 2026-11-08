package com.gempukku.swccgo.cards.set2230.light;

import com.gempukku.swccgo.cards.LegacyVersionAsserts;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

/** Original Commando Training (V) (Virtual Block 3 sheet, Dec 2009), a version of 1230_85. */
public class Card_2230_085_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("version", "2230_85");
                    put("current", "1230_85");
                }},
                new HashMap<>() {{
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
    public void CommandoTrainingIsTheSameCardWithTheOldText() {
        var scn = GetScenario();
        LegacyVersionAsserts.assertVersionOf(scn.GetLSCard("current").getBlueprint(), scn.GetLSCard("version").getBlueprint(),
                "Deploy on table. Ben Kenobi is deploy +1. Your C-3PO, Lando, and Rebels (except [Death Star II] Jedi) ignore location deployment restrictions (and 'power -1' in their game text) and apply their Endor deployment modifiers at any location. Once per game, may deploy a [Virtual Block 4] Effect from Reserve Deck; reshuffle. (Immune to Alter.)",
                "1230_85", "", LocalDate.of(2009, 12, 7), LocalDate.of(2010, 6, 28));
    }

    @Test
    public void CommandoTrainingAndItsCurrentPrintingAreTheSameUniqueCard() {
        var scn = GetScenario();
        var version = scn.GetLSCard("version");
        var current = scn.GetLSCard("current");
        scn.StartGame();
        assertFalse(scn.game().getModifiersQuerying().isUniquenessOnTableLimitReached(scn.gameState(), current));
        scn.MoveCardsToLSSideOfTable(version);
        assertTrue(scn.game().getModifiersQuerying().isUniquenessOnTableLimitReached(scn.gameState(), current));
    }
}
