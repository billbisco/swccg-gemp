package com.gempukku.swccgo.cards.set2240.dark;

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

/** Original Galen Marek, Starkiller (AI) (#04AI), a version of the AI printing 1240_8ai. */
public class Card_2240_008ai_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of()), new HashMap<>(Map.of("v", "2240_8ai", "ai", "1240_8ai", "base", "1240_8", "baseVersion", "2240_8")),
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void SameCardAsTheAiPrintingWithTheOldText() {
        var scn = scenario();
        LegacyVersionAsserts.assertVersionOf(scn.GetDSCard("ai").getBlueprint(), scn.GetDSCard("v").getBlueprint(),
                "Galen's forfeit may not be reduced. Power and defense value +2 while with Fallen Jedi. If defending a battle alone (or defending with Black Leader), may be targeted by Force Lightning. Immune to Clash Of Sabers and attrition < 4.",
                "1240_8ai", "", LocalDate.of(2009, 12, 7), LocalDate.of(2013, 4, 27));
    }

    @Test
    public void SameTextAndDatesAsTheBasePrintingsVersion() {
        var scn = scenario();
        var version = scn.GetDSCard("v").getBlueprint();
        var baseVersion = scn.GetDSCard("baseVersion").getBlueprint();
        assertEquals(baseVersion.getGameText(), version.getGameText());
        assertEquals(baseVersion.getLegacyVersion().printedOn(), version.getLegacyVersion().printedOn());
        assertEquals(baseVersion.getLegacyVersion().supersededOn(), version.getLegacyVersion().supersededOn());
        assertEquals("1240_8", baseVersion.getLegacyVersion().currentPrintingId());
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
