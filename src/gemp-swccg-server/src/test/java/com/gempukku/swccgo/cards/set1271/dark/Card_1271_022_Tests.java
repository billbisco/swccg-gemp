package com.gempukku.swccgo.cards.set1271.dark;

import com.gempukku.swccgo.cards.set1271.LegacyShuffleObserver;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class Card_1271_022_Tests {
    private static final String DOWNLOAD = "Deploy Superlaser from Reserve Deck";
    private static final String RETRIEVE = "Retrieve 1 Force at the blown-away system";

    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(
            new HashMap<>(Map.of("alderaan", "1_121")),
            new HashMap<>(Map.of("core", "1271_22", "deathStar", "2_143", "laser", "2_161",
                "other", "1_282", "ship", "1_301", "lost", "1_194")), 40, 40,
            StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
            StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
            StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test public void DeathStarCentralCorePrintedStatsIconsAndKeywordsAreCorrect() {
        var s = scenario();
        var b = s.GetDSCard("core").getBlueprint();
        assertEquals(Title.Death_Star_Central_Core, b.getTitle());
        assertEquals(Side.DARK, b.getSide());
        assertEquals(CardSubtype.SITE, b.getCardSubtype());
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness());
        assertTrue(b.hasVirtualSuffix());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_1, b.getExpansionSet());
        s.BlueprintCardTypeCheck(b, List.of(CardType.LOCATION));
        s.BlueprintIconCheck(b, List.of(Icon.DARK_FORCE, Icon.INTERIOR_SITE, Icon.MOBILE, Icon.SCOMP_LINK, Icon.LEGACY_BLOCK_7));
        s.BlueprintKeywordCheck(b, List.of());
    }

    private VirtualTableScenario board(boolean includeCore) {
        var s = scenario();
        s.StartGame();
        s.MoveLocationToTable(s.GetDSCard("deathStar"));
        if (includeCore) s.MoveLocationToTable(s.GetDSCard("core"));
        s.MoveLocationToTable(s.GetDSCard("other"));
        s.MoveLocationToTable(s.GetLSCard("alderaan"));
        return s;
    }

    @Test public void DeathStarCentralCoreActuallyDeploysTheSuperlaserFromReserveAndShuffles() {
        var s = board(true);
        var laser = s.GetDSCard("laser");
        var deathStar = s.GetDSCard("deathStar");
        s.SkipToDSTurn(Phase.DEPLOY);
        s.MoveCardsToTopOfDSReserveDeck(laser);
        var observer = LegacyShuffleObserver.observe(s);
        s.DSUseCardAction(s.GetDSCard("core"), DOWNLOAD);
        s.PassAllResponses();
        if (s.DSHasCardChoiceAvailable(laser)) s.DSChooseCard(laser);
        s.PassAllResponses();
        if (s.DSHasCardChoiceAvailable(deathStar)) s.DSChooseCard(deathStar);
        s.PassAllResponses();
        assertEquals(deathStar, laser.getAttachedTo());
        assertTrue(observer.sawOwnedShuffle(s.DS, Zone.RESERVE_DECK));
    }

    @Test public void DeathStarCentralCoreAddsBothIconsAtADestroyedSystemOnly() {
        var s = board(true);
        var q = s.game().getModifiersQuerying();
        var alderaan = s.GetLSCard("alderaan");
        assertEquals(alderaan.getBlueprint().getIconCount(Icon.LIGHT_FORCE), q.getIconCount(s.gameState(), alderaan, Icon.LIGHT_FORCE));
        s.gameState().flipBlownAwayLocations(s.game(), List.of(alderaan));
        assertEquals(1, q.getIconCount(s.gameState(), alderaan, Icon.LIGHT_FORCE));
        assertEquals(1, q.getIconCount(s.gameState(), alderaan, Icon.DARK_FORCE));
    }

    private void drain(boolean orbiting) {
        var s = board(true);
        var alderaan = s.GetLSCard("alderaan");
        var deathStar = s.GetDSCard("deathStar");
        deathStar.setSystemOrbited(orbiting ? Title.Alderaan : Title.Dantooine);
        s.gameState().flipBlownAwayLocations(s.game(), List.of(alderaan));
        s.MoveCardsToLocation(alderaan, s.GetDSCard("ship"));
        var lost = s.GetDSCard("lost");
        s.MoveCardsToTopOfDSLostPile(lost);
        s.SkipToDSTurn(Phase.CONTROL);
        assertTrue(s.DSForceDrainAvailable(alderaan));
        s.DSForceDrainAt(alderaan);
        if (s.LSDecisionAvailable("FORCE_DRAIN_INITIATED")) s.LSPass();
        assertEquals(orbiting, s.DSAnyActionsAvailable() && s.DSCardActionAvailable(s.GetDSCard("core"), RETRIEVE));
        if (orbiting) {
            s.DSUseCardAction(s.GetDSCard("core"), RETRIEVE);
            s.PassAllResponses();
            assertTrue(s.GetDSUsedPile().contains(lost));
        } else {
            assertTrue(s.GetDSLostPile().contains(lost));
        }
    }

    @Test public void DeathStarCentralCoreRetrievesOneWhenDarkActuallyDrainsAtAnOrbitedWreck() { drain(true); }
    @Test public void DeathStarCentralCoreDoesNotRetrieveWhenDarkDrainsAtANonOrbitedWreck() { drain(false); }
}
