package com.gempukku.swccgo.cards.set2250.light;

import com.gempukku.swccgo.cards.LegacyVersionAsserts;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.logic.modifiers.ModifierType;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** Pre-errata Master Qui-Gon (V) (AI) (#45AI), a version of the AI printing 1250_44ai: immune to the card Disarmed only. */
public class Card_2250_044ai_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of("v", "2250_44ai", "ai", "1250_44ai", "base", "1250_44", "baseVersion", "2250_44")), new HashMap<>(Map.of()),
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void SameCardAsTheAiPrintingWithTheOldText() {
        var scn = scenario();
        LegacyVersionAsserts.assertVersionOf(scn.GetLSCard("ai").getBlueprint(), scn.GetLSCard("v").getBlueprint(),
                "Deploys -1 to [Episode I] locations. If you just lost Force from an opponent's Effect, may ▲ an [Episode I] Interrupt (except Control), or place a card from hand under Credits Will Do Fine. Immune to Disarmed, You Are Beaten, and attrition.",
                "1250_44ai", "", LocalDate.of(2009, 12, 7), LocalDate.of(2013, 4, 27));
    }

    @Test
    public void SameTextAndDatesAsTheBasePrintingsVersion() {
        var scn = scenario();
        var version = scn.GetLSCard("v").getBlueprint();
        var baseVersion = scn.GetLSCard("baseVersion").getBlueprint();
        assertEquals(baseVersion.getGameText(), version.getGameText());
        assertEquals(baseVersion.getLegacyVersion().printedOn(), version.getLegacyVersion().printedOn());
        // the AI printing kept the old text on the PC card list until the 2013 Master List (legacy batch B16)
        // the base printing's original was superseded by the PC forum errata of 2011-03-21 (legacy batch B37)
        assertEquals(LocalDate.of(2011, 3, 21), baseVersion.getLegacyVersion().supersededOn());
        assertEquals(LocalDate.of(2013, 4, 27), version.getLegacyVersion().supersededOn());
        assertEquals("1250_44", baseVersion.getLegacyVersion().currentPrintingId());
        assertEquals(scn.GetLSCard("base").getBlueprint().getTitle(), version.getTitle());
    }

    @Test
    public void SameUniqueCardAsTheBaseAndAiPrintings() {
        for (String other : new String[]{"base", "ai", "baseVersion"}) {
            var scn = scenario();
            var version = scn.GetLSCard("v");
            var otherCard = scn.GetLSCard(other);
            scn.StartGame();
            var q = scn.game().getModifiersQuerying();
            assertFalse(other, q.isUniquenessOnTableLimitReached(scn.gameState(), otherCard));
            scn.MoveCardsToLocation(scn.GetLSStartingLocation(), version);
            assertTrue(other, q.isUniquenessOnTableLimitReached(scn.gameState(), otherCard));
        }
    }

    @Test
    public void ImmuneToTheCardDisarmedButMayBeDisarmedOtherwiseUnlikeTheCurrentAiPrinting() {
        var scn = scenario();
        var version = scn.GetLSCard("v");
        var ai = scn.GetLSCard("ai");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), version);
        var q = scn.game().getModifiersQuerying();
        assertTrue(q.isImmuneToCardTitle(scn.gameState(), version, Title.Disarmed));
        assertTrue(q.isImmuneToCardTitle(scn.gameState(), version, Title.You_Are_Beaten));
        assertTrue(q.getModifiersAffectingCard(scn.gameState(), ModifierType.MAY_NOT_BE_DISARMED, version).isEmpty());
        scn.MoveCardsToLSHand(version);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), ai);
        assertFalse(q.getModifiersAffectingCard(scn.gameState(), ModifierType.MAY_NOT_BE_DISARMED, ai).isEmpty());
    }
}
