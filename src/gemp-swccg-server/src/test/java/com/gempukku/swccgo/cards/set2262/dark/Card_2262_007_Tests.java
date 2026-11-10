package com.gempukku.swccgo.cards.set2262.dark;

import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.modifiers.ModifierType;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.gempukku.swccgo.cards.LegacyVersionAssertions.assertLegacyVersion;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** Pre-errata Power Of The Sith (Dec 2009 sheet), a version of 1262_7: bearer immune to the card Disarmed only. */
public class Card_2262_007_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of()),
                new HashMap<>(Map.of("power", "2262_7", "current", "1262_7", "vader", "1_168")),
                26, 26, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void PowerOfTheSithOriginalIdentity() {
        var scn = scenario();
        assertLegacyVersion(scn.GetDSCard("power").getBlueprint(), scn.GetDSCard("current").getBlueprint(), "1262_7", "",
                LocalDate.of(2009, 12, 7), LocalDate.of(2011, 3, 21),
                "If your character just won an epic duel, deploy on that character. Character is immune to Clash Of Sabers, Disarmed, and Sorry About The Mess. During your control phase, if at a battleground, opponent loses 1 Force. If this card just lost, place it in Used Pile. (Immune to Control.)");
        assertTrue(scn.GetDSCard("power").getBlueprint().isImmuneToCardTitle(Title.Control));
    }

    @Test
    public void PowerOfTheSithOriginalMakesBearerImmuneToTheCardDisarmedOnly() {
        for (boolean original : java.util.List.of(true, false)) {
            var scn = scenario();
            var vader = scn.GetDSCard("vader");
            scn.StartGame();
            scn.MoveCardsToLocation(scn.GetDSStartingLocation(), vader);
            scn.AttachCardsTo(vader, scn.GetDSCard(original ? "power" : "current"));
            var q = scn.game().getModifiersQuerying();
            assertTrue(q.isImmuneToCardTitle(scn.gameState(), vader, Title.Clash_Of_Sabers));
            assertTrue(q.isImmuneToCardTitle(scn.gameState(), vader, Title.Sorry_About_The_Mess));
            assertTrue(original == q.isImmuneToCardTitle(scn.gameState(), vader, Title.Disarmed));
            assertTrue(original == q.getModifiersAffectingCard(scn.gameState(), ModifierType.MAY_NOT_BE_DISARMED, vader).isEmpty());
        }
    }
}
