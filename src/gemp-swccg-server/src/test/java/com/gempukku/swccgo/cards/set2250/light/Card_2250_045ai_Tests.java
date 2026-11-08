package com.gempukku.swccgo.cards.set2250.light;

import com.gempukku.swccgo.cards.LegacyVersionAsserts;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** Original Mace Windu (V) (AI) (#44EAI), a version of the AI printing 1250_45ai. */
public class Card_2250_045ai_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of("v", "2250_45ai", "ai", "1250_45ai", "base", "1250_45", "baseVersion", "2250_45")), new HashMap<>(Map.of()),
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void SameCardAsTheAiPrintingWithTheOldText() {
        var scn = scenario();
        LegacyVersionAsserts.assertVersionOf(scn.GetLSCard("ai").getBlueprint(), scn.GetLSCard("v").getBlueprint(),
                "Mace's game text may not be canceled (except at Senate). During battle, Mace may fire one weapon twice. Boba Fett may not add battle destiny draws here. Immune to attrition.",
                "1250_45ai", "", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21));
    }

    @Test
    public void SameTextAndDatesAsTheBasePrintingsVersion() {
        var scn = scenario();
        var version = scn.GetLSCard("v").getBlueprint();
        var baseVersion = scn.GetLSCard("baseVersion").getBlueprint();
        assertEquals(baseVersion.getGameText(), version.getGameText());
        assertEquals(baseVersion.getLegacyVersion().printedOn(), version.getLegacyVersion().printedOn());
        assertEquals(baseVersion.getLegacyVersion().supersededOn(), version.getLegacyVersion().supersededOn());
        assertEquals("1250_45", baseVersion.getLegacyVersion().currentPrintingId());
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
}
