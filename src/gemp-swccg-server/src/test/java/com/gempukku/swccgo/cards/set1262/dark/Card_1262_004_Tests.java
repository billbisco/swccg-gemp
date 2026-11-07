package com.gempukku.swccgo.cards.set1262.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.decisions.CardActionSelectionDecision;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.*;

public class Card_1262_004_Tests {
    private VirtualTableScenario scenario() {
        var scn = new VirtualTableScenario(new HashMap<>(Map.of("ben", "1_21")),
                new HashMap<>(Map.of("link", "1262_4", "vader", "1_168", "trooper", "1_194", "reserve", "1_194",
                        "alien", "6_121", "cantina", "1_290", "weapon", "1_324")),
                30, 30, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        scn.StartGame();
        scn.MoveLocationToTable(scn.GetDSCard("cantina"));
        return scn;
    }

    @Test public void ComlinkPrintedMetadataMatches() {
        var scn = scenario();
        var b = scn.GetDSCard("link").getBlueprint();
        assertEquals("Comlink", b.getTitle());
        assertEquals(3, b.getDestiny(), scn.epsilon);
        assertEquals(Side.DARK, b.getSide());
        assertEquals(Uniqueness.UNRESTRICTED, b.getUniqueness());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_2, b.getExpansionSet());
        assertEquals(Rarity.V, b.getRarity());
        assertTrue(b.hasVirtualSuffix());
        assertTrue(b.isLegacy());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(List.of(CardType.DEVICE)));
        scn.BlueprintIconCheck(b, new ArrayList<>(List.of(Icon.DEVICE, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>(List.of(Keyword.DEPLOYS_ON_CHARACTERS)));
    }

    @Test public void ComlinkActuallyDeploysForFreeOnlyOnAnImperialWarrior() {
        var scn = scenario();
        var site = scn.GetDSStartingLocation();
        var vader = scn.GetDSCard("vader");
        var alien = scn.GetDSCard("alien");
        var link = scn.GetDSCard("link");
        scn.MoveCardsToLocation(site, vader, alien, scn.GetLSCard("ben"));
        scn.MoveCardsToDSHand(link);
        scn.SkipToPhase(Phase.CONTROL);
        while (scn.GetDSForcePileCount() > 0) {
            scn.MoveCardsToTopOfDSReserveDeck((com.gempukku.swccgo.game.PhysicalCardImpl)
                    scn.gameState().getForcePile(scn.DS).getFirst());
        }
        scn.SkipToPhase(Phase.DEPLOY);
        assertEquals(0, scn.GetDSForcePileCount());
        scn.DSDeployCard(link);
        assertTrue(scn.DSHasCardChoiceAvailable(vader));
        assertFalse(scn.DSHasCardChoiceAvailable(alien));
        assertFalse(scn.DSHasCardChoiceAvailable(scn.GetLSCard("ben")));
        scn.DSChooseCard(vader);
        scn.PassAllResponses();
        assertSame(vader, link.getAttachedTo());
        assertEquals(0, scn.GetDSForcePileCount());
    }

    @Test public void ComlinkReactsWithACharacterToActualBattleAtSameOrAdjacentSiteOnly() {
        for (int distance : List.of(0, 1, 2)) {
            var scn = scenario();
            var site = scn.GetDSStartingLocation();
            var bearerSite = distance == 0 ? site : distance == 1 ? scn.GetDSCard("cantina") : scn.GetLSStartingLocation();
            if (distance == 1) assertTrue(scn.IsAdjacentTo(site, bearerSite));
            var vader = scn.GetDSCard("vader");
            var link = scn.GetDSCard("link");
            var backup = scn.GetDSCard("reserve");
            var weapon = scn.GetDSCard("weapon");
            scn.MoveCardsToLocation(bearerSite, vader);
            scn.AttachCardsTo(vader, link);
            scn.MoveCardsToLocation(site, scn.GetDSCard("trooper"), scn.GetLSCard("ben"));
            scn.MoveCardsToDSHand(backup, weapon);
            scn.SkipToLSTurn(Phase.ACTIVATE);
            scn.EnsureLSForcePile(2);
            scn.EnsureDSForcePile(3);
            scn.SkipToPhase(Phase.BATTLE);
            scn.LSInitiateBattle(site);
            boolean reacted = false;
            for (int i = 0; i < 50 && !scn.AwaitingLSWeaponsSegmentActions(); i++) {
                if (scn.DSHasCardChoiceAvailable(backup)) {
                    assertFalse(scn.DSHasCardChoiceAvailable(weapon));
                    scn.DSChooseCard(backup);
                } else if (scn.DSHasCardChoiceAvailable(site)) scn.DSChooseCard(site);
                else if (!reacted && scn.DSGetDecision() instanceof CardActionSelectionDecision
                        && scn.DSCardActionAvailable(link, "Deploy a character as a 'react'")) {
                    assertTrue(distance < 2);
                    scn.DSUseCardAction(link, "Deploy a character as a 'react'");
                    reacted = true;
                } else if (scn.LSGetDecision() instanceof CardActionSelectionDecision) scn.LSPass();
                else if (scn.DSGetDecision() instanceof CardActionSelectionDecision) scn.DSPass();
                else fail("Unexpected Comlink react decision: " + scn.GetCurrentDecision().getText());
            }
            assertEquals("Character react at distance " + distance, distance < 2, reacted);
            if (reacted) assertSame(site, backup.getAtLocation());
            else assertInZone(Zone.HAND, backup);
        }
    }
}
