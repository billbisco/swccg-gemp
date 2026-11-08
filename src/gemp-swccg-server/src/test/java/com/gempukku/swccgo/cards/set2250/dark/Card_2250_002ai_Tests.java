package com.gempukku.swccgo.cards.set2250.dark;

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

/** Original Aurra Sing (V) (AI) (#02AI), a version of the AI printing 1250_2ai. */
public class Card_2250_002ai_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of()), new HashMap<>(Map.of("v", "2250_2ai", "ai", "1250_2ai", "base", "1250_2", "baseVersion", "2250_2")),
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void SameCardAsTheAiPrintingWithTheOldText() {
        var scn = scenario();
        LegacyVersionAsserts.assertVersionOf(scn.GetDSCard("ai").getBlueprint(), scn.GetDSCard("v").getBlueprint(),
                "Once during opponent's activate phase, may relocate Aurra to same site as a Jedi. Once per game, during your move phase, may fire her weapon at a Jedi; hit target is lost. Immune to attrition < 4.",
                "1250_2ai", "", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21));
    }

    @Test
    public void SameTextAndDatesAsTheBasePrintingsVersion() {
        var scn = scenario();
        var version = scn.GetDSCard("v").getBlueprint();
        var baseVersion = scn.GetDSCard("baseVersion").getBlueprint();
        assertEquals(baseVersion.getGameText(), version.getGameText());
        assertEquals(baseVersion.getLegacyVersion().printedOn(), version.getLegacyVersion().printedOn());
        assertEquals(baseVersion.getLegacyVersion().supersededOn(), version.getLegacyVersion().supersededOn());
        assertEquals("1250_2", baseVersion.getLegacyVersion().currentPrintingId());
        assertEquals(scn.GetDSCard("base").getBlueprint().getTitle(), version.getTitle());
    }

    @Test
    public void SameUniqueCardAsTheBaseAndAiPrintings() {
        for (String other : new String[]{"base", "ai", "baseVersion"}) {
            var scn = scenario();
            var version = scn.GetDSCard("v");
            var otherCard = scn.GetDSCard(other);
            scn.StartGame();
            var q = scn.game().getModifiersQuerying();
            assertFalse(other, q.isUniquenessOnTableLimitReached(scn.gameState(), otherCard));
            scn.MoveCardsToLocation(scn.GetDSStartingLocation(), version);
            assertTrue(other, q.isUniquenessOnTableLimitReached(scn.gameState(), otherCard));
        }
    }
}
