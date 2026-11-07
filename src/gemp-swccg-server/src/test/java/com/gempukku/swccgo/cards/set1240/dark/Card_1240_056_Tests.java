package com.gempukku.swccgo.cards.set1240.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
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
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.*;

public class Card_1240_056_Tests {
    private static final String RETRIEVE = "Retrieve 3 Force";
    private static final String RELEASE = "Release imprisoned captives";

    private VirtualTableScenario Scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of("r2", "2_14", "ra7", "2_16", "luke", "1_19", "han", "1_11")),
                new HashMap<>(Map.of("prison", "1240_56", "tower", "5_172", "trooper", "1_194", "ig", "109_11")),
                40, 40, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    private VirtualTableScenario Started() {
        var scn = Scenario();
        scn.StartGame();
        scn.MoveLocationToTable(scn.GetDSCard("prison"));
        scn.MoveLocationToTable(scn.GetDSCard("tower"));
        return scn;
    }

    @Test
    public void KesselSpiceMinesPrisonMatchesBothPrintedSides() {
        var scn = Scenario();
        var card = scn.GetDSCard("prison").getBlueprint();
        assertEquals("Kessel: Spice Mines - Prison", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.SITE, card.getCardSubtype());
        assertEquals(1, card.getIconCount(Icon.LIGHT_FORCE));
        assertEquals(2, card.getIconCount(Icon.DARK_FORCE));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
        assertEquals("Whenever a droid is imprisoned here, may retrieve 3 Force (once per captive).", card.getLocationDarkSideGameText());
        assertEquals("If you control, may release any captives imprisoned here (may retrieve 1 Force for each).", card.getLocationLightSideGameText());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>(List.of(CardType.LOCATION)));
        scn.BlueprintIconCheck(card, new ArrayList<>(List.of(Icon.LIGHT_FORCE, Icon.DARK_FORCE, Icon.INTERIOR_SITE,
                Icon.PLANET, Icon.DAGOBAH, Icon.LEGACY_BLOCK_4)));
        scn.BlueprintKeywordCheck(card, new ArrayList<>(List.of(Keyword.PRISON)));
    }

    private void SeedDarkLost(VirtualTableScenario scn) {
        scn.MoveCardsToTopOfDSLostPile(scn.GetDSFiller(1), scn.GetDSFiller(2), scn.GetDSFiller(3), scn.GetDSFiller(4));
    }

    private boolean DeliverAndReachReward(VirtualTableScenario scn) {
        assertTrue(scn.DSActionAvailable("Deliver captive to prison"));
        scn.DSChooseAction("Deliver captive to prison");
        return ReachReward(scn);
    }

    private boolean ReachReward(VirtualTableScenario scn) {
        var prison = scn.GetDSCard("prison");
        for (int step = 0; step < 14; step++) {
            if (scn.DSDecisionAvailable("optional response") && scn.DSCardActionAvailable(prison, RETRIEVE)) return true;
            if (scn.LSDecisionAvailable("optional response")) scn.LSPass();
            else if (scn.DSDecisionAvailable("optional response")) scn.DSPass();
            else if (scn.AwaitingDSMovePhaseActions() || scn.AwaitingLSMovePhaseActions()) return false;
            else fail("Unexpected native delivery decision: " + scn.GetCurrentDecision().getText());
        }
        fail("Delivery never resolved");
        return false;
    }

    private VirtualTableScenario EscortAt(String location, String captiveName) {
        var scn = Started();
        var trooper = scn.GetDSCard("trooper");
        var captive = scn.GetLSCard(captiveName);
        scn.MoveCardsToLocation(scn.GetDSCard(location), trooper);
        // Ordinary escort state for testing the actual Move-phase delivery action.
        scn.CaptureCardWith(trooper, captive);
        SeedDarkLost(scn);
        scn.SkipToDSTurn(Phase.MOVE);
        return scn;
    }

    private void ChooseReward(VirtualTableScenario scn) {
        int lost = scn.GetDSLostPileCount();
        int used = scn.GetDSUsedPileCount();
        scn.DSUseCardAction(scn.GetDSCard("prison"), RETRIEVE);
        scn.PassAllResponses();
        assertEquals(lost - 3, scn.GetDSLostPileCount());
        assertEquals(used + 3, scn.GetDSUsedPileCount());
    }

    @Test
    public void ActualOriginalDroidDeliveryCanRetrieveExactlyThreeForce() {
        var scn = EscortAt("prison", "r2");
        assertTrue(DeliverAndReachReward(scn));
        ChooseReward(scn);
        var r2 = scn.GetLSCard("r2");
        assertTrue(r2.isImprisoned());
        assertEquals(scn.GetDSCard("prison"), r2.getAttachedTo());
    }

    @Test
    public void ActualDroidDeliveryRewardMayBeDeclined() {
        var scn = EscortAt("prison", "r2");
        assertTrue(DeliverAndReachReward(scn));
        scn.DSPass();
        scn.PassAllResponses();
        assertTrue(scn.GetLSCard("r2").isImprisoned());
        assertEquals(4, scn.GetDSLostPileCount());
    }

    @Test
    public void ActualNonDroidDeliveryAtKesselDoesNotOfferReward() {
        var scn = EscortAt("prison", "luke");
        assertFalse(DeliverAndReachReward(scn));
        assertTrue(scn.GetLSCard("luke").isImprisoned());
        assertEquals(4, scn.GetDSLostPileCount());
    }

    @Test
    public void ActualDroidDeliveryToAnotherPrisonDoesNotOfferKesselReward() {
        var scn = EscortAt("tower", "r2");
        assertFalse(DeliverAndReachReward(scn));
        assertTrue(scn.GetLSCard("r2").isImprisoned());
        assertEquals(scn.GetDSCard("tower"), scn.GetLSCard("r2").getAttachedTo());
        assertEquals(4, scn.GetDSLostPileCount());
    }

    private void AdvanceToNextDarkMove(VirtualTableScenario scn) {
        scn.SkipToLSTurn(Phase.MOVE);
        scn.SkipToDSTurn(Phase.MOVE);
    }

    @Test
    public void RepeatedDeliveryOfSameCaptiveWithoutReleaseDoesNotRetrieveAgain() {
        var scn = EscortAt("prison", "r2");
        var r2 = scn.GetLSCard("r2");
        var escort = scn.GetDSCard("trooper");
        assertTrue(DeliverAndReachReward(scn));
        ChooseReward(scn);
        AdvanceToNextDarkMove(scn);
        scn.DSChooseAction("Take imprisoned captive into custody");
        if (scn.DSHasCardChoiceAvailable(r2)) scn.DSChooseCard(r2);
        scn.PassAllResponses();
        assertEquals(escort, r2.getEscort());
        AdvanceToNextDarkMove(scn);
        assertFalse(DeliverAndReachReward(scn));
        assertTrue(r2.isImprisoned());
        assertEquals(1, scn.GetDSLostPileCount());
    }

    @Test
    public void SecondDroidIsADifferentCaptiveAndCanRetrieveItsOwnThreeForce() {
        var scn = EscortAt("prison", "r2");
        assertTrue(DeliverAndReachReward(scn));
        ChooseReward(scn);
        var ra7 = scn.GetLSCard("ra7");
        scn.CaptureCardWith(scn.GetDSCard("trooper"), ra7);
        SeedDarkLost(scn);
        AdvanceToNextDarkMove(scn);
        assertTrue(DeliverAndReachReward(scn));
        ChooseReward(scn);
        assertTrue(ra7.isImprisoned());
    }

    private VirtualTableScenario RiotGunCapturesDroid(boolean directlyImprison) {
        var scn = Started();
        var prison = scn.GetDSCard("prison");
        var ig = scn.GetDSCard("ig");
        var r2 = scn.GetLSCard("r2");
        scn.MoveCardsToLocation(prison, ig, r2, scn.GetLSCard("luke"));
        SeedDarkLost(scn);
        scn.EnsureDSForcePile(1);
        scn.DSStartBattleAndSkipToWeaponsSegment(prison);
        scn.PrepareDSDestiny(6);
        scn.DSUseCardAction(ig, "Fire riot gun");
        scn.DSChooseCard(r2);
        for (int i = 0; i < 16 && !r2.isCaptive(); i++) {
            if (scn.DSDecisionAvailable("Choose who will escort") || scn.DSDecisionAvailable("Choose escort")) scn.DSChooseCard(ig);
            else if (scn.DSDecisionAvailable("Choose option for capturing")) scn.DSChoose(directlyImprison ? "Imprisonment" : "Seize");
            else if (scn.DSDecisionAvailable("optional response")) scn.DSPass();
            else if (scn.LSDecisionAvailable("optional response")) scn.LSPass();
            else fail("Unexpected native capture decision: " + scn.GetCurrentDecision().getText());
        }
        assertTrue(r2.isCaptive());
        return scn;
    }

    @Test
    public void OriginalRiotGunCapturesOriginalDroidWhichCanThenBeDeliveredToKessel() {
        var scn = RiotGunCapturesDroid(false);
        var ig = scn.GetDSCard("ig");
        var r2 = scn.GetLSCard("r2");
        assertEquals(ig, r2.getEscort());
        scn.PrepareLSDestiny(0);
        scn.PrepareDSDestiny(0);
        scn.SkipToEndOfPowerSegment(true);
        for (int i = 0; i < 30 && scn.gameState().getCurrentPhase() != Phase.MOVE; i++) {
            if (scn.AwaitingLSBattleDamagePayment()) scn.LSPayRemainingBattleDamageFromReserveDeck();
            else if (scn.AwaitingDSBattleDamagePayment()) scn.DSPayRemainingBattleDamageFromReserveDeck();
            else if (scn.DSDecisionAvailable("Do you want to draw")) scn.DSChooseYes();
            else if (scn.LSDecisionAvailable("Do you want to draw")) scn.LSChooseYes();
            else scn.PassResponses();
        }
        assertEquals(Phase.MOVE, scn.gameState().getCurrentPhase());
        assertTrue(DeliverAndReachReward(scn));
        ChooseReward(scn);
        assertTrue(r2.isImprisoned());
    }

    @Test
    public void DirectImprisonmentDuringActualRiotGunCaptureAlsoOffersTheReward() {
        var scn = RiotGunCapturesDroid(true);
        var r2 = scn.GetLSCard("r2");
        assertTrue(r2.isImprisoned());
        assertEquals(scn.GetDSCard("prison"), r2.getAttachedTo());
        assertTrue(ReachReward(scn));
        ChooseReward(scn);
    }

    private VirtualTableScenario ReleaseReady(boolean control, boolean lostCards) {
        var scn = Started();
        var prison = scn.GetDSCard("prison");
        scn.gameState().imprisonCharacter(scn.game(), scn.GetLSCard("r2"), prison);
        scn.gameState().imprisonCharacter(scn.game(), scn.GetLSCard("ra7"), prison);
        scn.gameState().imprisonCharacter(scn.game(), scn.GetLSCard("han"), scn.GetDSCard("tower"));
        scn.MoveCardsToLocation(prison, scn.GetLSCard("luke"));
        if (!control) scn.MoveCardsToLocation(prison, scn.GetDSCard("trooper"));
        if (lostCards) scn.MoveCardsToTopOfLSLostPile(scn.GetLSFiller(1), scn.GetLSFiller(2), scn.GetLSFiller(3));
        scn.SkipToLSTurn(Phase.CONTROL);
        return scn;
    }

    private void Release(VirtualTableScenario scn, PhysicalCardImpl... selected) {
        var prison = scn.GetDSCard("prison");
        scn.LSUseCardAction(prison, RELEASE);
        assertFalse(scn.LSHasCardChoiceAvailable(scn.GetLSCard("han")));
        scn.LSChooseCards(selected);
        for (int step = 0; step < 20; step++) {
            if (scn.LSDecisionAvailable("Choose character to release")) {
                for (var card : selected) {
                    if (scn.LSHasCardChoiceAvailable(card)) {
                        scn.LSChooseCard(card);
                        break;
                    }
                }
            } else if (scn.LSDecisionAvailable("Choose where to rally")) scn.LSChooseCard(prison);
            else if (scn.LSDecisionAvailable("optional response") || scn.DSDecisionAvailable("optional response")) scn.PassAllResponses();
            else break;
        }
        for (var card : selected) {
            assertFalse(card.isCaptive());
            assertAtLocation(prison, card);
        }
        assertTrue(scn.GetLSCard("han").isImprisoned());
    }

    @Test
    public void LightControlCanReleaseTwoButMayDeclineAllRetrieval() {
        var scn = ReleaseReady(true, true);
        Release(scn, scn.GetLSCard("r2"), scn.GetLSCard("ra7"));
        assertTrue(scn.LSDecisionAvailable("Retrieve 2 Force?"));
        scn.LSChoose("No");
        scn.PassAllResponses();
        assertEquals(3, scn.GetLSLostPileCount());
    }

    @Test
    public void LightCanRetrieveExactlyOneForceForEachOfTwoReleasedCaptives() {
        var scn = ReleaseReady(true, true);
        int used = scn.GetLSUsedPileCount();
        Release(scn, scn.GetLSCard("r2"), scn.GetLSCard("ra7"));
        assertTrue(scn.LSDecisionAvailable("Retrieve 2 Force?"));
        scn.LSChoose("Yes");
        scn.PassAllResponses();
        assertEquals(1, scn.GetLSLostPileCount());
        assertEquals(used + 2, scn.GetLSUsedPileCount());
    }

    @Test
    public void LightCanReleaseOnlyOneEvenWithTwoPrisonersAndNoLostPile() {
        var scn = ReleaseReady(true, false);
        Release(scn, scn.GetLSCard("r2"));
        assertTrue(scn.GetLSCard("ra7").isImprisoned());
        assertFalse(scn.LSDecisionAvailable("Retrieve 1 Force?"));
        assertEquals(0, scn.GetLSLostPileCount());
    }

    @Test
    public void LightPresenceWithoutControlDoesNotOfferRelease() {
        var scn = ReleaseReady(false, true);
        assertFalse(scn.LSCardActionAvailable(scn.GetDSCard("prison"), RELEASE));
        assertTrue(scn.GetLSCard("r2").isImprisoned());
        assertTrue(scn.GetLSCard("ra7").isImprisoned());
        assertInZone(Zone.ATTACHED, scn.GetLSCard("r2"));
    }
}
