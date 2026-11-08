package com.gempukku.swccgo.cards.set2250.light;

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

/** Pre-errata Master Qui-Gon (V) (Dec 2009 sheet), a version of 1250_44: immune to the card Disarmed only. */
public class Card_2250_044_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of("quigon", "2250_44", "current", "1250_44")), new HashMap<>(Map.of()),
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void MasterQuiGonOriginalIdentity() {
        var scn = scenario();
        assertLegacyVersion(scn.GetLSCard("quigon").getBlueprint(), scn.GetLSCard("current").getBlueprint(), "1250_44", "",
                LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21),
                "Deploys -1 to [Episode I] locations. If you just lost Force from an opponent's Effect, may ▲ an [Episode I] Interrupt (except Control), or place a card from hand under Credits Will Do Fine. Immune to Disarmed, You Are Beaten, and attrition.");
    }

    @Test
    public void MasterQuiGonOriginalIsImmuneToTheCardDisarmedButMayBeDisarmedOtherwise() {
        var scn = scenario();
        var quigon = scn.GetLSCard("quigon");
        var current = scn.GetLSCard("current");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), quigon);
        var q = scn.game().getModifiersQuerying();
        assertTrue(q.isImmuneToCardTitle(scn.gameState(), quigon, Title.Disarmed));
        assertTrue(q.isImmuneToCardTitle(scn.gameState(), quigon, Title.You_Are_Beaten));
        assertTrue(q.getModifiersAffectingCard(scn.gameState(), ModifierType.MAY_NOT_BE_DISARMED, quigon).isEmpty());
        scn.MoveCardsToLSHand(quigon);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), current);
        assertFalse(q.getModifiersAffectingCard(scn.gameState(), ModifierType.MAY_NOT_BE_DISARMED, current).isEmpty());
    }
}
