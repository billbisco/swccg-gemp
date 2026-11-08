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

/** Original Juno Eclipse, Black Leader (AI) (Virtual Block 4 sheet, #02AI), a version of the AI printing 1240_3. */
public class Card_2240_003_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of()), new HashMap<>(Map.of("v", "2240_3", "ai", "1240_3", "base", "1240_2", "baseVersion", "2240_2")),
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void SameCardAsTheAiPrintingWithTheOldText() {
        var scn = scenario();
        LegacyVersionAsserts.assertVersionOf(scn.GetDSCard("ai").getBlueprint(), scn.GetDSCard("v").getBlueprint(),
                "Adds 3 to power of anything she pilots.  Deploys -1 to starships.  Naboo Blaster is a matching weapon for Black Leader.  While with Galen at a site, adds one battle destiny.  During your control phase, may use 2 Force to retrieve a card with 'back' in title.  Immune to attrition < 3.",
                "1240_3", "", LocalDate.of(2009, 12, 7), LocalDate.of(2013, 4, 27));
    }

    @Test
    public void SameTextAndDatesAsTheBasePrintingsVersion() {
        var scn = scenario();
        var version = scn.GetDSCard("v").getBlueprint();
        var baseVersion = scn.GetDSCard("baseVersion").getBlueprint();
        assertEquals(baseVersion.getGameText(), version.getGameText());
        assertEquals(baseVersion.getLegacyVersion().printedOn(), version.getLegacyVersion().printedOn());
        assertEquals(baseVersion.getLegacyVersion().supersededOn(), version.getLegacyVersion().supersededOn());
        assertEquals("1240_2", baseVersion.getLegacyVersion().currentPrintingId());
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
