package com.gempukku.swccgo.cards.set2281.light;

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

/** Original Supreme Chancellor Valorum (V) (AI) (Virtual Block 8.1), a version of the AI printing 1281_30ai. */
public class Card_2281_030ai_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of("v", "2281_30ai", "ai", "1281_30ai", "base", "1281_30", "baseVersion", "2281_30")), new HashMap<>(Map.of()),
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void SameCardAsTheAiPrintingWithTheOldText() {
        var scn = scenario();
        LegacyVersionAsserts.assertVersionOf(scn.GetLSCard("ai").getBlueprint(), scn.GetLSCard("v").getBlueprint(),
                "Agenda: order. May be targeted by Might Of The Republic. Your characters with politics (except Wedge and senators) are deploy -1 (and gain [Republic]) here. During your turn, may use 1 Force to ▲ a card with 'governor,' 'political,' or 'Valorum' in lore.",
                "1281_30ai", "", LocalDate.of(2012, 5, 11), LocalDate.of(2012, 7, 21));
    }

    @Test
    public void SameTextAndDatesAsTheBasePrintingsVersion() {
        var scn = scenario();
        var version = scn.GetLSCard("v").getBlueprint();
        var baseVersion = scn.GetLSCard("baseVersion").getBlueprint();
        assertEquals(baseVersion.getGameText(), version.getGameText());
        assertEquals(baseVersion.getLegacyVersion().printedOn(), version.getLegacyVersion().printedOn());
        assertEquals(baseVersion.getLegacyVersion().supersededOn(), version.getLegacyVersion().supersededOn());
        assertEquals("1281_30", baseVersion.getLegacyVersion().currentPrintingId());
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
