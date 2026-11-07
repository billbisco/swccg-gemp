package com.gempukku.swccgo.cards.set1240.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1240_044_Tests {
    private static final String DOWNLOAD = "Deploy a pit from Reserve Deck";
    private static final String RELEASE = "Release captive to a related pit";
    private static final String CANCEL = "Cancel weapon destiny during attack";

    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of(
                "luke", "1_19", "armedLuke", "108_3", "leia", "1_17", "han", "1_11")),
                new HashMap<>(Map.ofEntries(
                        Map.entry("worse", "1240_44"), Map.entry("palace", "6_171"),
                        Map.entry("rancorPit", "6_166"), Map.entry("greatPit", "6_170"),
                        Map.entry("otherSite", "1_293"), Map.entry("vader", "1_168"),
                        Map.entry("guard", "1_194"), Map.entry("remoteGuard", "1_194"),
                        Map.entry("pitGuard", "1_194"), Map.entry("extraGuard", "1_194"),
                        Map.entry("bubo", "6_138"))),
                12, 12, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void ItsWorseHasPrintedUsedInterruptStatsIconsAndKeywords() {
        var scn = scenario();
        var card = scn.GetDSCard("worse").getBlueprint();
        assertEquals("It's Worse", card.getTitle());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Side.DARK, card.getSide());
        assertTrue(card.getCardTypes().contains(CardType.INTERRUPT));
        assertEquals(CardSubtype.USED, card.getCardSubtype());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
        assertEquals(6, card.getDestiny(), scn.epsilon);
        scn.BlueprintIconCheck(card, new ArrayList<>(List.of(Icon.INTERRUPT, Icon.LEGACY_BLOCK_4)));
        scn.BlueprintKeywordCheck(card, new ArrayList<>());
    }

    @Test
    public void ItsWorseUsesExactlyOneForceToDownloadAndShuffleRealPitInDeployPhase() {
        var scn = scenario();
        var worse = scn.GetDSCard("worse");
        var pit = scn.GetDSCard("greatPit");
        var other = scn.GetDSCard("otherSite");
        scn.StartGame();
        scn.MoveLocationToTable(scn.GetDSCard("palace"));
        scn.MoveCardsToDSHand(worse);
        scn.EnsureDSForcePile(2);
        scn.MoveCardsToTopOfDSReserveDeck(pit, other);
        scn.SkipToDSTurn(Phase.CONTROL);
        assertFalse(scn.DSCardActionAvailable(worse, DOWNLOAD));
        scn.SkipToPhase(Phase.DEPLOY);
        int force = scn.GetDSForcePileCount();
        assertTrue(scn.DSCardActionAvailable(worse, DOWNLOAD));
        scn.DSUseCardAction(worse, DOWNLOAD);
        scn.PassAllResponses();
        assertTrue(scn.DSHasCardChoiceAvailable(pit));
        assertFalse(scn.DSHasCardChoiceAvailable(other));
        scn.DSChooseCard(pit);
        if (scn.DSDecisionAvailable("On which side")) {
            scn.DSChoose("Left");
        }
        scn.PassAllResponses();

        assertEquals(Zone.LOCATIONS, pit.getZone());
        assertEquals(force - 1, scn.GetDSForcePileCount());
        assertTrue(scn.GetDSUsedPile().contains(worse));
        assertTrue(scn.gameState().getLastMessages().stream().anyMatch(m -> m.contains("shuffles Reserve Deck")));
    }

    @Test
    public void ItsWorseCannotDownloadWithoutTheOneForceCost() {
        var scn = scenario();
        var worse = scn.GetDSCard("worse");
        scn.StartGame();
        scn.MoveCardsToDSHand(worse);
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSCard("greatPit"));
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertEquals(0, scn.GetDSForcePileCount());
        assertFalse(scn.DSCardActionAvailable(worse, DOWNLOAD));
    }

    @Test
    public void ItsWorseReleasesOnlyEligibleCaptiveToRelatedPitWithoutOwnPresenceAtEndOfDeploy() {
        var scn = releaseScenario(false);
        var worse = scn.GetDSCard("worse");
        var luke = scn.GetLSCard("luke");
        var leia = scn.GetLSCard("leia");
        var han = scn.GetLSCard("han");
        var pit = scn.GetDSCard("rancorPit");
        assertFalse(scn.DSCardActionAvailable(worse, RELEASE));
        assertTrue(advanceToRelease(scn));
        scn.DSUseCardAction(worse, RELEASE);
        assertTrue(scn.DSHasCardChoiceAvailable(luke));
        assertFalse(scn.DSHasCardChoiceAvailable(leia));
        assertFalse(scn.DSHasCardChoiceAvailable(han));
        scn.DSChooseCard(luke);
        assertTrue(scn.DSHasCardChoiceAvailable(pit));
        assertFalse(scn.DSHasCardChoiceAvailable(scn.GetDSCard("greatPit")));
        scn.DSChooseCard(pit);
        scn.PassAllResponses();

        assertFalse(luke.isCaptive());
        assertAtLocation(pit, luke);
        assertTrue(leia.isFrozen());
        assertTrue(han.isCaptive());
        assertTrue(scn.GetDSUsedPile().contains(worse));
    }

    @Test
    public void ItsWorseDoesNotOfferReleaseWhenAllRelatedPitsAreOccupied() {
        var scn = releaseScenario(true);
        assertFalse(advanceToRelease(scn));
        assertTrue(scn.GetLSCard("luke").isCaptive());
    }

    private VirtualTableScenario releaseScenario(boolean occupyBoth) {
        var scn = scenario();
        scn.StartGame();
        var palace = scn.GetDSCard("palace");
        var great = scn.GetDSCard("greatPit");
        var rancor = scn.GetDSCard("rancorPit");
        var remote = scn.GetLSStartingLocation();
        scn.MoveLocationToTable(palace);
        scn.MoveLocationToTable(great);
        scn.MoveLocationToTable(rancor);
        scn.MoveCardsToLocation(palace, scn.GetDSCard("vader"), scn.GetDSCard("guard"),
                scn.GetLSCard("luke"), scn.GetLSCard("leia"));
        scn.MoveCardsToLocation(remote, scn.GetDSCard("remoteGuard"), scn.GetLSCard("han"));
        scn.MoveCardsToLocation(great, scn.GetDSCard("pitGuard"));
        if (occupyBoth) {
            scn.MoveCardsToLocation(rancor, scn.GetDSCard("extraGuard"));
        }
        scn.CaptureCardWith(scn.GetDSCard("vader"), scn.GetLSCard("luke"));
        scn.CaptureCardWith(scn.GetDSCard("guard"), scn.GetLSCard("leia"));
        scn.FreezeCard(scn.GetLSCard("leia"));
        scn.CaptureCardWith(scn.GetDSCard("remoteGuard"), scn.GetLSCard("han"));
        scn.MoveCardsToDSHand(scn.GetDSCard("worse"));
        scn.SkipToDSTurn(Phase.DEPLOY);
        return scn;
    }

    private static boolean advanceToRelease(VirtualTableScenario scn) {
        for (int i = 0; i < 20; i++) {
            if (scn.DSCardActionAvailable(scn.GetDSCard("worse"), RELEASE)) {
                return true;
            }
            if (scn.gameState().getCurrentPhase() != Phase.DEPLOY) {
                return false;
            }
            if (scn.DSGetDecision() != null) {
                scn.DSPass();
            } else if (scn.LSGetDecision() != null) {
                scn.LSPass();
            } else {
                throw new AssertionError("No decision before end of deploy phase");
            }
        }
        throw new AssertionError("Did not reach end of deploy phase");
    }

    @Test
    public void ItsWorseCancelsActualLightsaberDestinyInCreatureAttack() {
        verifyAttack(false);
        verifyAttack(true);
    }

    private void verifyAttack(boolean cancel) {
        var scn = scenario();
        scn.StartGame();
        var site = scn.GetDSStartingLocation();
        var luke = scn.GetLSCard("armedLuke");
        var bubo = scn.GetDSCard("bubo");
        var worse = scn.GetDSCard("worse");
        scn.MoveCardsToLocation(site, luke, bubo);
        if (cancel) {
            scn.MoveCardsToDSHand(worse);
        }
        scn.SkipToLSTurn(Phase.ACTIVATE);
        scn.EnsureLSForcePile(2);
        scn.SkipToPhase(Phase.BATTLE);
        scn.LSUseCardAction(site, "Initiate attack on creature");
        if (scn.LSHasCardChoiceAvailable(bubo)) {
            scn.LSChooseCard(bubo);
        }
        scn.PassAllResponses();
        assertTrue(scn.gameState().isDuringAttack());
        assertEquals(5, scn.game().getModifiersQuerying().getDefenseValue(scn.gameState(), bubo), scn.epsilon);

        fire(scn, luke, bubo);
        boolean offered = advanceToCancel(scn);
        assertEquals(cancel, offered);
        if (cancel) {
            scn.DSUseCardAction(worse, CANCEL);
        }
        scn.PassAllResponses();
        assertEquals(!cancel, bubo.isHit());
        if (cancel) {
            assertTrue(scn.GetDSUsedPile().contains(worse));
        }
    }

    @Test
    public void ItsWorseDoesNotCancelWeaponDestinyInNormalBattle() {
        var scn = scenario();
        scn.StartGame();
        var site = scn.GetDSStartingLocation();
        var luke = scn.GetLSCard("armedLuke");
        var vader = scn.GetDSCard("vader");
        var worse = scn.GetDSCard("worse");
        scn.MoveCardsToLocation(site, luke, vader);
        scn.MoveCardsToDSHand(worse);
        scn.EnsureLSForcePile(2);
        scn.LSStartBattleAndSkipToWeaponsSegment(site);
        fire(scn, luke, vader);
        assertFalse(advanceToCancel(scn));
        scn.PassAllResponses();
        assertTrue(vader.isHit());
        assertTrue(scn.GetDSHand().contains(worse));
    }

    private static void fire(VirtualTableScenario scn, PhysicalCardImpl shooter, PhysicalCardImpl target) {
        scn.PrepareLSDestiny(3);
        scn.PrepareLSDestiny(4);
        scn.LSUseCardAction(shooter, "Fire");
        scn.LSChooseCard(target);
    }

    private static boolean advanceToCancel(VirtualTableScenario scn) {
        for (int i = 0; i < 20; i++) {
            if (scn.DSCardActionAvailable(scn.GetDSCard("worse"), CANCEL)) {
                return true;
            }
            if (scn.DSDecisionAvailable("optional response")) {
                scn.DSPass();
            } else if (scn.LSDecisionAvailable("optional response")) {
                scn.LSPass();
            } else {
                return false;
            }
        }
        throw new AssertionError("Weapon draw did not finish");
    }
}
