package com.gempukku.swccgo.cards.set1250.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;

public class Card_1250_019_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{ put("amidala", "12_22"); put("senator", "12_8"); put("obiwan", "1_21"); put("luke", "1_19"); }},
                new HashMap<>() {{ put("bounty", "1250_19"); put("bossk", "110_5"); put("ig88", "4_101"); put("trooper", "1_194"); }},
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void NuteGunraysBountyStatsAndIdentityMatchPrintedCard() {
        var scn = scenario();
        var bp = scn.GetDSCard("bounty").getBlueprint();
        assertEquals("Nute Gunray's Bounty", bp.getTitle());
        assertFalse(bp.hasVirtualSuffix());
        assertTrue(bp.isLegacy());
        assertEquals(Side.DARK, bp.getSide());
        assertEquals(Uniqueness.UNIQUE, bp.getUniqueness());
        assertEquals(4, bp.getDestiny(), scn.epsilon);
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, bp.getExpansionSet());
        assertEquals(Rarity.V, bp.getRarity());
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>() {{ add(CardType.EFFECT); }});
        scn.BlueprintIconCheck(bp, new ArrayList<>() {{ add(Icon.EFFECT); add(Icon.EPISODE_I); add(Icon.CORUSCANT); add(Icon.LEGACY_BLOCK_5); }});
    }

    @Test
    public void NuteGunraysBountyDeploysOnlyOnAmidalaOrOpponentsSenator() {
        var scn = scenario();
        var site = scn.GetLSStartingLocation();
        var bounty = scn.GetDSCard("bounty");
        scn.StartGame();
        scn.MoveCardsToLocation(site, scn.GetLSCard("amidala"), scn.GetLSCard("senator"), scn.GetLSCard("luke"), scn.GetDSCard("trooper"));
        scn.MoveCardsToHand(bounty);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSActivateForceCheat(2);
        scn.DSDeployCard(bounty);
        assertTrue(scn.DSHasCardChoiceAvailable(scn.GetLSCard("amidala")));
        assertTrue(scn.DSHasCardChoiceAvailable(scn.GetLSCard("senator")));
        assertFalse(scn.DSHasCardChoiceAvailable(scn.GetLSCard("luke")));
        assertFalse(scn.DSHasCardChoiceAvailable(scn.GetDSCard("trooper")));
        scn.DSChooseCard(scn.GetLSCard("senator"));
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(scn.GetLSCard("senator"), bounty));
    }

    private VirtualTableScenario onSenator(String hunter, boolean jedi, Phase phase) {
        var scn = scenario();
        var site = scn.GetLSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, scn.GetLSCard("senator"), scn.GetDSCard(hunter));
        if (jedi) scn.MoveCardsToLocation(site, scn.GetLSCard("obiwan"));
        scn.AttachCardsTo(scn.GetLSCard("senator"), scn.GetDSCard("bounty"));
        scn.SkipToDSTurn(phase);
        return scn;
    }

    private void capture(VirtualTableScenario scn) {
        var bounty = scn.GetDSCard("bounty");
        assertTrue(scn.DSCardActionAvailable(bounty, "Capture and seize"));
        scn.DSUseCardAction(bounty, "Capture and seize");
        if (scn.DSHasCardChoiceAvailable(scn.GetDSCard("bossk"))) scn.DSChooseCard(scn.GetDSCard("bossk"));
        scn.PassAllResponses();
    }

    @Test
    public void NuteGunraysBountyAlienBountyHunterPresentCapturesAndSeizesDuringMovePhase() {
        var scn = onSenator("bossk", false, Phase.MOVE);
        capture(scn);
        var senator = scn.GetLSCard("senator");
        assertTrue(Filters.captive.accepts(scn.game(), senator));
        assertTrue(Filters.escortedBy(scn.GetDSCard("bossk")).accepts(scn.game(), senator));
    }

    @Test
    public void NuteGunraysBountyMayNotCaptureOutsideMovePhase() {
        var scn = onSenator("bossk", false, Phase.DEPLOY);
        assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("bounty"), "Capture and seize"));
    }

    @Test
    public void NuteGunraysBountyMayNotCaptureWhileJediHere() {
        var scn = onSenator("bossk", true, Phase.MOVE);
        assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("bounty"), "Capture and seize"));
    }

    @Test
    public void NuteGunraysBountyRequiresAlienBountyHunter() {
        var scn = onSenator("ig88", false, Phase.MOVE);
        assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("bounty"), "Capture and seize"));
    }

    @Test
    public void NuteGunraysBountyRequiresBountyHunterPresent() {
        var scn = scenario();
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("senator"));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("bossk"));
        scn.AttachCardsTo(scn.GetLSCard("senator"), scn.GetDSCard("bounty"));
        scn.SkipToDSTurn(Phase.MOVE);
        assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("bounty"), "Capture and seize"));
    }

    @Test
    public void NuteGunraysBountyOpponentLosesOneForceInControlPhaseWhileEscortedByAlien() {
        var scn = onSenator("bossk", false, Phase.MOVE);
        capture(scn);
        scn.SkipToDSTurn(Phase.CONTROL);
        var bounty = scn.GetDSCard("bounty");
        assertEquals(scn.GetLSCard("senator"), bounty.getAttachedTo());
        int life = scn.GetLSLifeForceRemaining();
        assertTrue(scn.DSCardActionAvailable(bounty, "Make opponent lose 1 Force"));
        scn.DSUseCardAction(bounty, "Make opponent lose 1 Force");
        scn.PassAllResponses();
        if (scn.AwaitingLSForceLossPayment()) scn.LSPayForceLossFromReserveDeck();
        scn.PassAllResponses();
        assertEquals(life - 1, scn.GetLSLifeForceRemaining());
        if (scn.LSAnyDecisionsAvailable()) scn.LSPass();
        assertFalse(scn.DSCardActionAvailable(bounty, "Make opponent lose 1 Force"));
    }

    @Test
    public void NuteGunraysBountyNoForceLossWhenNotEscorted() {
        var scn = onSenator("bossk", false, Phase.CONTROL);
        assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("bounty"), "Make opponent lose 1 Force"));
    }
}
