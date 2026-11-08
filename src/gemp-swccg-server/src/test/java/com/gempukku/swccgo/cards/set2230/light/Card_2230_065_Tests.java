package com.gempukku.swccgo.cards.set2230.light;

import com.gempukku.swccgo.cards.LegacyVersionAsserts;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

/** Original Aved Luun (V) (Virtual Block 3 sheet, Dec 2009), a version of 1230_65. */
public class Card_2230_065_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("version", "2230_65");
                    put("current", "1230_65");
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
    public void AvedLuunIsTheSameCardWithTheOldText() {
        var scn = GetScenario();
        LegacyVersionAsserts.assertVersionOf(scn.GetLSCard("current").getBlueprint(), scn.GetLSCard("version").getBlueprint(),
                "Deploys only on Tatooine. Adds one battle destiny with Kalit. Your Jawas and sandcrawlers are destiny +2. May deploy one Jawa Camp, Sergeant Doallyn, or Thedit from Reserve Deck; reshuffle.",
                "1230_65", "", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21));
    }

    @Test
    public void AvedLuunAndItsCurrentPrintingAreTheSameUniqueCard() {
        var scn = GetScenario();
        var version = scn.GetLSCard("version");
        var current = scn.GetLSCard("current");
        scn.StartGame();
        assertFalse(scn.game().getModifiersQuerying().isUniquenessOnTableLimitReached(scn.gameState(), current));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), version);
        assertTrue(scn.game().getModifiersQuerying().isUniquenessOnTableLimitReached(scn.gameState(), current));
    }
}
