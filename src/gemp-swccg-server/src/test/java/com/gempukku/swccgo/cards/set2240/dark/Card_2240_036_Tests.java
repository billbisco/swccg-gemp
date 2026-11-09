package com.gempukku.swccgo.cards.set2240.dark;

import com.gempukku.swccgo.cards.LegacyVersionAsserts;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

/** Original There Is No Try & Oppressive Enforcement (V) (Virtual Block 4 sheet, Dec 2009), a version of 1240_36. */
public class Card_2240_036_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                }},
                new HashMap<>() {{
                    put("version", "2240_36");
                    put("current", "1240_36");
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
    public void ThereIsNoTryAndOppressiveEnforcementIsTheSameCardWithTheOldText() {
        var scn = GetScenario();
        LegacyVersionAsserts.assertVersionOf(scn.GetDSCard("current").getBlueprint(), scn.GetDSCard("version").getBlueprint(),
                "Deploy on Conference Room. Revolution is canceled. You may not deploy aliens (except bounty hunters and Mara Jade) or [Episode I] cards. Emperor and your admirals, generals, and moffs are Imperial Council Members and influence +1. While present, each Imperial Council Member adds one [Dark Side Force]. Once per turn, may use 1 Force to deploy one Laser Gate or a Political Effect from Reserve Deck; reshuffle. Your cards may deploy to table from Political Effects. May place your canceled Political Effect in Used Pile. (Immune to Alter.)",
                "1240_36", "", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 10, 19));
    }

    @Test
    public void ThereIsNoTryAndOppressiveEnforcementAndItsCurrentPrintingAreTheSameUniqueCard() {
        var scn = GetScenario();
        var version = scn.GetDSCard("version");
        var current = scn.GetDSCard("current");
        scn.StartGame();
        assertFalse(scn.game().getModifiersQuerying().isUniquenessOnTableLimitReached(scn.gameState(), current));
        scn.AttachCardsTo(scn.GetDSStartingLocation(), version);
        assertTrue(scn.game().getModifiersQuerying().isUniquenessOnTableLimitReached(scn.gameState(), current));
    }
}
