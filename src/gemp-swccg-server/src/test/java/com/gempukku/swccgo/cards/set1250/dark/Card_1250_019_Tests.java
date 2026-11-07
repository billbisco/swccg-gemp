package com.gempukku.swccgo.cards.set1250.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.decisions.CardActionSelectionDecision;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class Card_1250_019_Tests {
    private static final String CAPTURE = "Capture and seize the character";

    private VirtualTableScenario scenario() {
        var scn = new VirtualTableScenario(
                new HashMap<>(Map.of("amidala", "14_25", "senator", "12_28", "other", "1_19", "jedi", "1_21")),
                new HashMap<>(Map.of("effect", "1250_19", "senator", "14_81", "trooper", "1_194",
                        "bane", "6_95", "boba", "105_4", "jabba", "6_109", "clientsBounty", "225_31")),
                30, 30, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        scn.StartGame();
        return scn;
    }

    @Test public void NuteGunraysBountyPrintedMetadataMatches() {
        var scn = scenario();
        var b = scn.GetDSCard("effect").getBlueprint();
        assertEquals("Nute Gunray's Bounty", b.getTitle());
        assertEquals("Palpatine suggested to the Queen that she acquiesce to the Trade Federation, just for the time being.", b.getLore());
        assertEquals(4, b.getDestiny(), scn.epsilon);
        assertEquals(Side.DARK, b.getSide());
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness());
        assertEquals(CardSubtype.NORMAL, b.getCardSubtype());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, b.getExpansionSet());
        assertEquals(Rarity.V, b.getRarity());
        assertFalse(b.hasVirtualSuffix());
        assertTrue(b.isLegacy());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(List.of(CardType.EFFECT)));
        scn.BlueprintIconCheck(b, new ArrayList<>(List.of(Icon.EFFECT, Icon.EPISODE_I, Icon.CORUSCANT, Icon.LEGACY_BLOCK_5)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>());
    }

    @Test public void NuteGunraysBountyActuallyDeploysOnAmidalaOrOpposingSenator() {
        for (String target : List.of("amidala", "senator")) {
            var scn = scenario();
            var effect = scn.GetDSCard("effect");
            var bearer = scn.GetLSCard(target);
            var site = scn.GetDSStartingLocation();
            scn.MoveCardsToLocation(site, bearer, scn.GetLSCard("other"), scn.GetDSCard("senator"),
                    scn.GetDSCard("trooper"));
            scn.MoveCardsToDSHand(effect);
            scn.SkipToPhase(Phase.DEPLOY);
            scn.DSDeployCard(effect);
            assertTrue(scn.DSHasCardChoiceAvailable(bearer));
            assertFalse(scn.DSHasCardChoiceAvailable(scn.GetDSCard("senator")));
            assertFalse(scn.DSHasCardChoiceAvailable(scn.GetLSCard("other")));
            scn.DSChooseCard(bearer);
            for (int i = 0; i < 35 && !scn.AwaitingDSDeployPhaseActions(); i++) {
                if (scn.LSGetDecision() instanceof CardActionSelectionDecision) scn.LSPass();
                else if (scn.DSGetDecision() instanceof CardActionSelectionDecision) scn.DSPass();
                else fail("Unexpected Bounty deployment decision: " + scn.GetCurrentDecision().getText());
            }
            assertSame(bearer, effect.getAttachedTo());
        }
    }

    private VirtualTableScenario onAmidala(boolean huntersHere, boolean jediHere) {
        var scn = scenario();
        var site = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetLSCard("amidala"), scn.GetDSCard("trooper"),
                scn.GetDSCard("senator"), scn.GetDSCard("jabba"));
        scn.MoveCardsToLocation(huntersHere ? site : scn.GetLSStartingLocation(), scn.GetDSCard("bane"), scn.GetDSCard("boba"));
        scn.MoveCardsToLocation(jediHere ? site : scn.GetLSStartingLocation(), scn.GetLSCard("jedi"));
        var bounty = scn.GetDSCard("effect");
        scn.MoveCardsToDSHand(bounty);
        scn.SkipToPhase(Phase.DEPLOY);
        scn.DSDeployCard(bounty);
        if (scn.DSHasCardChoiceAvailable(scn.GetLSCard("amidala"))) scn.DSChooseCard(scn.GetLSCard("amidala"));
        scn.PassAllResponses();
        assertSame(scn.GetLSCard("amidala"), bounty.getAttachedTo());
        return scn;
    }

    private void captureWith(VirtualTableScenario scn, String escortKey) {
        var bounty = scn.GetDSCard("effect");
        var amidala = scn.GetLSCard("amidala");
        PhysicalCardImpl escort = scn.GetDSCard(escortKey);
        assertTrue(scn.AwaitingDSMovePhaseActions());
        assertTrue(scn.DSCardActionAvailable(bounty, CAPTURE));
        scn.DSUseCardAction(bounty, CAPTURE);
        assertTrue(scn.DSHasCardChoiceAvailable(scn.GetDSCard("bane")));
        assertTrue(scn.DSHasCardChoiceAvailable(scn.GetDSCard("boba")));
        assertFalse(scn.DSHasCardChoiceAvailable(scn.GetDSCard("trooper")));
        assertFalse(scn.DSHasCardChoiceAvailable(scn.GetDSCard("senator")));
        assertFalse(scn.DSHasCardChoiceAvailable(scn.GetDSCard("jabba")));
        scn.DSChooseCard(escort);
        scn.PassAllResponses();
        assertTrue(amidala.isCaptive());
        assertSame(escort, amidala.getEscort());
        assertSame(amidala, bounty.getAttachedTo());
        if (scn.AwaitingLSMovePhaseActions()) scn.LSPass();
        assertTrue(scn.AwaitingDSMovePhaseActions());
        assertFalse(scn.DSCardActionAvailable(bounty, CAPTURE));
    }

    @Test public void NuteGunraysBountyCapturesAndSeizesWithTheChosenAlienHunterInMove() {
        for (String escort : List.of("bane", "boba")) {
            var scn = onAmidala(true, false);
            if (scn.AwaitingLSDeployPhaseActions()) scn.LSPass();
            assertTrue(scn.AwaitingDSDeployPhaseActions());
            assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("effect"), CAPTURE));
            scn.SkipToPhase(Phase.MOVE);
            captureWith(scn, escort);
        }
    }

    @Test public void NuteGunraysBountyDoesNotCaptureWithJediHereOrHuntersElsewhere() {
        for (var scn : List.of(onAmidala(true, true), onAmidala(false, false))) {
            scn.SkipToPhase(Phase.MOVE);
            assertTrue(scn.AwaitingDSMovePhaseActions());
            assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("effect"), CAPTURE));
            assertFalse(scn.GetLSCard("amidala").isCaptive());
        }
    }

    @Test public void NuteGunraysBountyMayDeclineCapture() {
        var scn = onAmidala(true, false);
        scn.SkipToPhase(Phase.MOVE);
        assertTrue(scn.DSCardActionAvailable(scn.GetDSCard("effect"), CAPTURE));
        scn.DSPass();
        assertFalse(scn.GetLSCard("amidala").isCaptive());
    }

    @Test public void NuteGunraysBountyAllowsNativeAboutToBeCapturedResponse() {
        var scn = onAmidala(true, false);
        var response = scn.GetDSCard("clientsBounty");
        var amidala = scn.GetLSCard("amidala");
        scn.AttachCardsTo(amidala, response);
        scn.MoveCardsToTopOfDSLostPile(scn.GetTopOfDSReserveDeck());
        var retrieved = scn.GetDSLostPile().getFirst();
        scn.SkipToPhase(Phase.MOVE);
        captureWith(scn, "boba");
        assertEquals(Zone.HAND, response.getZone());
        assertTrue(scn.GetDSUsedPile().contains(retrieved));
    }

    @Test public void NuteGunraysBountyCausesOneForceLossInOwnControlWhileAlienEscorts() {
        var scn = onAmidala(true, false);
        scn.SkipToPhase(Phase.MOVE);
        captureWith(scn, "bane");
        scn.SkipToLSTurn(Phase.CONTROL);
        assertFalse(scn.LSDecisionAvailable("Choose Force to lose"));
        for (int i = 0; i < 2; i++) {
            scn.SkipToDSTurn(Phase.ACTIVATE);
            scn.SkipToPhase(Phase.CONTROL);
            payOneForce(scn);
        }
    }

    @Test public void NuteGunraysBountyControlLossDoesNotRequireTheAlienEscortToBeAHunter() {
        var scn = onAmidala(false, false);
        var amidala = scn.GetLSCard("amidala");
        scn.CaptureCardWith(scn.GetDSCard("jabba"), amidala);
        assertSame(scn.GetDSCard("jabba"), amidala.getEscort());
        scn.SkipToDSTurn(Phase.ACTIVATE);
        scn.SkipToPhase(Phase.CONTROL);
        payOneForce(scn);
    }

    private void payOneForce(VirtualTableScenario scn) {
        scn.PassAllResponses();
        assertTrue(scn.LSDecisionAvailable("Choose Force to lose"));
        var card = scn.GetTopOfLSReserveDeck();
        scn.LSChooseCard(card);
        scn.PassAllResponses();
        assertTrue(scn.GetLSLostPile().contains(card));
        assertFalse(scn.LSDecisionAvailable("Choose Force to lose"));
    }

    @Test public void NuteGunraysBountyDoesNotCauseControlLossWithoutAlienEscort() {
        for (boolean capturedByNonAlien : List.of(false, true)) {
            var scn = onAmidala(true, false);
            var amidala = scn.GetLSCard("amidala");
            if (capturedByNonAlien) scn.CaptureCardWith(scn.GetDSCard("trooper"), amidala);
            assertSame(amidala, scn.GetDSCard("effect").getAttachedTo());
            scn.SkipToDSTurn(Phase.ACTIVATE);
            scn.SkipToPhase(Phase.CONTROL);
            scn.PassAllResponses();
            assertFalse(scn.LSDecisionAvailable("Choose Force to lose"));
        }
    }
}
