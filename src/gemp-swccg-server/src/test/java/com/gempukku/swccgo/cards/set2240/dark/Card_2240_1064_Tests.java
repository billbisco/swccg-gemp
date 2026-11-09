package com.gempukku.swccgo.cards.set2240.dark;

import com.gempukku.swccgo.cards.LegacyVersionAsserts;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

import static org.junit.Assert.assertTrue;

import static org.junit.Assert.assertFalse;

/** PC-E1 Galen's Lightsaber (PC website card list, by Jul 2012), a version of 1240_64. */
public class Card_2240_1064_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                }},
                new HashMap<>() {{
                    put("version", "2240_1064");
                    put("current", "1240_64");
                    put("galen", "601_81");
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
    public void GalensLightsaberCardListIsTheSameCardWithTheOldText() {
        var scn = GetScenario();
        LegacyVersionAsserts.assertVersionOf(scn.GetDSCard("current").getBlueprint(), scn.GetDSCard("version").getBlueprint(),
                "Deploy on Galen. While Galen defending a battle, his and Black Leader's immunity to atttition is +2. May target a character or creature for free. Draw two destiny. If total destiny > defense value, target hit, power -3, and may not be used to satisfy attrition.",
                "1240_64", "PC-E1", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27));
    }

    @Test
    public void GalensLightsaberAndItsCurrentPrintingAreTheSameUniqueCard() {
        var scn = GetScenario();
        var version = scn.GetDSCard("version");
        var current = scn.GetDSCard("current");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("galen"));
        assertFalse(scn.game().getModifiersQuerying().isUniquenessOnTableLimitReached(scn.gameState(), current));
        scn.AttachCardsTo(scn.GetDSCard("galen"), version);
        assertTrue(scn.game().getModifiersQuerying().isUniquenessOnTableLimitReached(scn.gameState(), current));
    }
}
