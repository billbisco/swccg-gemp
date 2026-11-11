package com.gempukku.swccgo.cards.set2250.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.decisions.CardActionSelectionDecision;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.*;

/** Batch R8-P: the card-list version 2250_1018 of 1250_18 keeps the behaviour the current card had before the Master List text (2013) became current; these are the former Card_1250_018_Tests tests run on 2250_1018. */
public class Card_2250_1018_Tests {
    private static final String CANCEL = "Cancel battle destiny";

    private VirtualTableScenario scenario() {
        var scn = new VirtualTableScenario(
                new HashMap<>(Map.of("obi", "1_21", "luke", "1_19", "droid", "1_5", "cancel", "5_50")),
                new HashMap<>(Map.of("enemy", "2250_1018", "sidious", "1116_46", "senate", "12_167",
                        "vader", "1_168", "saber", "1_324", "sniper", "2_139", "upload", "1_194")),
                28, 28, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        scn.StartGame();
        scn.MoveLocationToTable(scn.GetDSCard("senate"));
        scn.MoveCardsToDSHand(scn.GetDSCard("enemy"));
        return scn;
    }

    @Test public void AnEnemyOfTheRepublicPrintedMetadataMatches() {
        var scn = scenario();
        var b = scn.GetDSCard("enemy").getBlueprint();
        assertEquals("An Enemy Of The Republic", b.getTitle());
        assertEquals(Side.DARK, b.getSide());
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness());
        assertEquals(6, b.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.NORMAL, b.getCardSubtype());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, b.getExpansionSet());
        assertEquals(Rarity.V, b.getRarity());
        assertFalse(b.hasVirtualSuffix());
        assertTrue(b.isLegacy());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(List.of(CardType.EFFECT)));
        scn.BlueprintIconCheck(b, new ArrayList<>(List.of(Icon.EFFECT, Icon.EPISODE_I,
                Icon.SIDIOUS, Icon.LEGACY_BLOCK_5)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>());
    }

    @Test public void AnEnemyOfTheRepublicActuallyDeploysOnlyOnSidious() {
        var scn = scenario();
        var enemy = scn.GetDSCard("enemy");
        var sidious = scn.GetDSCard("sidious");
        var vader = scn.GetDSCard("vader");
        scn.MoveCardsToLocation(scn.GetDSCard("senate"), sidious);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), vader);
        scn.SkipToPhase(Phase.DEPLOY);
        assertTrue(scn.DSDeployAvailable(enemy));
        scn.DSDeployCard(enemy);
        if (scn.DSHasCardChoiceAvailable(sidious)) {
            assertFalse(scn.DSHasCardChoiceAvailable(vader));
            scn.DSChooseCard(sidious);
        }
        scn.PassAllResponses();
        assertSame(sidious, enemy.getAttachedTo());
    }

    @Test public void AnEnemyOfTheRepublicRelocatesToNonDroidNotParticipatingDroidInBattleAwayFromSenate() {
        var scn = battle(true, true);
        var enemy = scn.GetDSCard("enemy");
        assertNotSame(scn.GetLSCard("droid"), enemy.getAttachedTo());
        assertSame(scn.GetLSCard("obi"), enemy.getAttachedTo());
        assertSame(scn.GetDSStartingLocation(), scn.gameState().getBattleLocation());
    }

    @Test public void AnEnemyOfTheRepublicDoesNotRelocateWhenSidiousIsAwayFromSenate() {
        var scn = battle(false, true);
        assertSame(scn.GetDSCard("sidious"), scn.GetDSCard("enemy").getAttachedTo());
    }

    private VirtualTableScenario battle(boolean atSenate, boolean withDroid) {
        var scn = scenario();
        var sidious = scn.GetDSCard("sidious");
        var site = scn.GetDSStartingLocation();
        var obi = scn.GetLSCard("obi");
        var droid = scn.GetLSCard("droid");
        var offSite = scn.GetLSCard("luke");
        scn.MoveCardsToLocation(atSenate ? scn.GetDSCard("senate") : scn.GetLSStartingLocation(), sidious);
        scn.AttachCardsTo(sidious, scn.GetDSCard("enemy"));
        scn.MoveCardsToLocation(site, scn.GetDSCard("vader"), obi);
        if (withDroid) scn.MoveCardsToLocation(site, droid);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), offSite);
        scn.MoveCardsToLSHand(scn.GetLSCard("cancel"));
        scn.EnsureDSForcePile(1);
        scn.EnsureLSForcePile(3);
        scn.SkipToPhase(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        for (int i = 0; i < 35 && !scn.AwaitingDSWeaponsSegmentActions(); i++) {
            if (scn.LSDecisionAvailable("Choose your participating character")) {
                assertTrue(scn.LSHasCardChoiceAvailable(obi));
                assertFalse(scn.LSHasCardChoiceAvailable(droid));
                assertFalse(scn.LSHasCardChoiceAvailable(offSite));
                scn.LSChooseCard(obi);
            } else if (scn.LSGetDecision() instanceof CardActionSelectionDecision) scn.LSPass();
            else if (scn.DSGetDecision() instanceof CardActionSelectionDecision) scn.DSPass();
            else fail("Unexpected battle-start decision: " + scn.GetCurrentDecision().getText());
        }
        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        return scn;
    }

    @Test public void AnEnemyOfTheRepublicPreventsActualCancellationOfDarkDestinyOnlyInBattleHere() {
        for (boolean protectedHere : List.of(false, true)) {
            var scn = battle(protectedHere, false);
            var cancel = scn.GetLSCard("cancel");
            scn.PrepareDSDestiny(3);
            scn.SkipToPowerSegment();
            assertTrue(scn.DSDecisionAvailable("battle destiny?"));
            scn.DSChooseYes();
            boolean offered = false;
            for (int i = 0; i < 35 && !scn.LSDecisionAvailable("battle destiny?"); i++) {
                if (scn.LSGetDecision() instanceof CardActionSelectionDecision) {
                    if (scn.LSCardPlayAvailable(cancel, CANCEL)) {
                        assertFalse(protectedHere);
                        offered = true;
                        scn.LSPlayCard(cancel, CANCEL);
                    } else scn.LSPass();
                } else if (scn.DSGetDecision() instanceof CardActionSelectionDecision) scn.DSPass();
                else fail("Unexpected destiny decision: " + scn.GetCurrentDecision().getText());
            }
            assertEquals(!protectedHere, offered);
            assertEquals(protectedHere ? 4 : 0, scn.GetDSTotalDestiny());
            assertInZone(protectedHere ? Zone.HAND : Zone.LOST_PILE, cancel);
        }
    }

    @Test public void AnEnemyOfTheRepublicReturnsBeforeActualLossAndMaySearchOrDecline() {
        snipe(true, true);
        snipe(true, false);
    }

    @Test public void AnEnemyOfTheRepublicCannotReturnOrSearchWithoutSidious() {
        snipe(false, false);
    }

    private void snipe(boolean withSidious, boolean take) {
        var scn = scenario();
        var sidious = scn.GetDSCard("sidious");
        var site = scn.GetDSStartingLocation();
        var vader = scn.GetDSCard("vader");
        var victim = scn.GetLSCard("luke");
        var enemy = scn.GetDSCard("enemy");
        var saber = scn.GetDSCard("saber");
        var sniper = scn.GetDSCard("sniper");
        var upload = scn.GetDSCard("upload");
        if (withSidious) scn.MoveCardsToLocation(scn.GetDSCard("senate"), sidious);
        scn.MoveCardsToLocation(site, vader, victim);
        scn.AttachCardsTo(victim, enemy);
        scn.AttachCardsTo(vader, saber);
        scn.MoveCardsToDSHand(sniper);
        scn.EnsureDSForcePile(4);
        scn.SkipToPhase(Phase.CONTROL);
        scn.MoveCardsToTopOfDSReserveDeck(upload);
        scn.PrepareDSDestiny(5);
        scn.PrepareDSDestiny(6);
        assertTrue(scn.DSCardPlayAvailable(sniper));
        scn.DSPlayCard(sniper);
        boolean offered = false;
        List<PhysicalCard> remaining = null;
        for (int i = 0; i < 100 && !scn.AwaitingDSControlPhaseActions(); i++) {
            if (scn.DSDecisionAvailable("Take a card into hand from Reserve Deck?")) {
                assertSame(sidious, enemy.getAttachedTo());
                offered = true;
                remaining = new ArrayList<>(scn.GetDSReserveDeck());
                if (take) {
                    assertTrue(remaining.remove(upload));
                    scn.gameState().getRandom().setSeed(4096);
                    scn.DSChooseYes();
                } else scn.DSChooseNo();
            } else if (scn.DSHasCardChoiceAvailable(saber)) scn.DSChooseCard(saber);
            else if (scn.DSHasCardChoiceAvailable(victim)) scn.DSChooseCard(victim);
            else if (scn.DSHasCardChoiceAvailable(upload)) scn.DSChooseCard(upload);
            else if (scn.LSGetDecision() instanceof CardActionSelectionDecision) scn.LSPass();
            else if (scn.DSGetDecision() instanceof CardActionSelectionDecision) scn.DSPass();
            else fail("Unexpected Sniper or reserve decision: " + scn.GetCurrentDecision().getText());
        }
        assertTrue(scn.AwaitingDSControlPhaseActions());
        assertEquals(withSidious, offered);
        assertInZone(Zone.LOST_PILE, victim);
        if (withSidious) assertSame(sidious, enemy.getAttachedTo());
        else assertInZone(Zone.LOST_PILE, enemy);
        assertInZone(take ? Zone.HAND : Zone.RESERVE_DECK, upload);
        if (take) {
            assertNotNull(remaining);
            assertEquals(new HashSet<>(remaining), new HashSet<>(scn.GetDSReserveDeck()));
            assertNotEquals(remaining, scn.GetDSReserveDeck());
        } else if (remaining != null) assertEquals(remaining, scn.GetDSReserveDeck());
    }
}
