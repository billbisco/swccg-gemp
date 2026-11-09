package com.gempukku.swccgo.cards.set2261.light;

import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.gempukku.swccgo.cards.LegacyVersionAssertions.assertLegacyVersion;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** Pre-errata Strong Is Vader (Sep 2009 sheet), a version of 1261_87. */
public class Card_2261_087_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of("strong", "2261_87", "current", "1261_87", "luke", "1_19")),
                new HashMap<>(), 40, 40, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void StrongIsVaderOriginalIdentity() {
        var scn = scenario();
        assertLegacyVersion(scn.GetLSCard("strong").getBlueprint(), scn.GetLSCard("current").getBlueprint(), "1261_87", "",
                LocalDate.of(2009, 9, 4), LocalDate.of(2009, 9, 21),
                "If [Virtual] Mind What You Have Learned on table, deploy on table with up to 6 different Jedi Tests under here from outside your deck. You may not Force drain at Dagobah locations or deploy asteroid sectors, [Maintenance] cards, or [Permanent Weapon] cards. Whenever you draw training destiny, draw two and choose one. You may retrieve 3 Force whenever you complete a Jedi Test (cannot be canceled while you occupy a battleground). Completed Jedi Tests are placed on table and are suspended (not lost) whenever character who completed them not on table.");
    }

    @Test
    public void StrongIsVaderOriginalRetrievalMayNotBeCanceledWhileYouOccupyABattleground() {
        for (boolean original : java.util.List.of(true, false)) {
            var scn = scenario();
            var strong = scn.GetLSCard(original ? "strong" : "current");
            scn.StartGame();
            scn.MoveCardsToLSSideOfTable(strong);
            var q = scn.game().getModifiersQuerying();
            assertFalse(q.isForceRetrievalFromCardMayNotBeCanceled(scn.gameState(), strong, scn.LS));
            scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetLSCard("luke"));
            assertTrue(original == q.isForceRetrievalFromCardMayNotBeCanceled(scn.gameState(), strong, scn.LS));
        }
    }
}
