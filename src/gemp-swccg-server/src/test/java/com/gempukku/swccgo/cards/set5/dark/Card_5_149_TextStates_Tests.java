package com.gempukku.swccgo.cards.set5.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class Card_5_149_TextStates_Tests {
    private VirtualTableScenario scenario(String id) {
        return new VirtualTableScenario(new HashMap<>() {{
            put("rebel", "1_28");
            put("anger", "4_16");
        }}, new HashMap<>() {{
            put("telepathy", id);
            put("alien", "1_171");
        }}, 20, 20, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, "anything_goes");
    }

    @Test
    public void ProjectiveTelepathyOriginalMatchesPrintedFace() {
        var scn = scenario("5_149o");
        var card = scn.GetDSCard("telepathy").getBlueprint();
        assertEquals("Projective Telepathy", card.getTitle());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3f, card.getDestiny(), 0f);
        assertEquals(Rarity.U, card.getRarity());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(CardSubtype.USED, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, List.of(CardType.INTERRUPT));
        scn.BlueprintIconCheck(card, List.of(Icon.CLOUD_CITY, Icon.INTERRUPT));
        scn.BlueprintKeywordCheck(card, List.of());
        assertEquals("Cancel Anger, Fear, Aggression when it is inserted or revealed. OR A player who just initiated a battle or Force drain must use 2 Force or it is canceled.", card.getGameText());
        assertEquals("'Luke.' 'Father.' 'Son, come with me.'", card.getLore());
        assertEquals("5_149", card.getTextState().currentId());
        assertEquals("Printed", card.getTextState().label());
        assertTrue(card.getTextState().source().contains("dates are bounds"));
    }

    private VirtualTableScenario battle(String id, boolean own, int remaining) {
        var scn = scenario(id);
        var site = scn.GetLSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, scn.GetLSCard("rebel"), scn.GetDSCard("alien"));
        scn.MoveCardsToDSHand(scn.GetDSCard("telepathy"));
        if (own)
            scn.SkipToDSTurn(Phase.BATTLE);
        else
            scn.SkipToLSTurn(Phase.BATTLE);
        force(scn, own ? remaining + 1 : 0, own ? 0 : remaining + 1);
        if (own)
            scn.DSInitiateBattle(site);
        else
            scn.LSInitiateBattle(site);
        return scn;
    }

    private void play(VirtualTableScenario scn) {
        var card = scn.GetDSCard("telepathy");
        if (scn.DSGetDecision() == null && scn.LSGetDecision() != null)
            scn.LSPass();
        assertTrue(scn.DSCardPlayAvailable(card));
        scn.DSPlayCard(card);
        scn.PassAllResponses();
    }

    private void assertUsed(VirtualTableScenario scn) {
        assertTrue(scn.GetDSUsedPile().contains(scn.GetDSCard("telepathy")));
    }

    private void force(VirtualTableScenario scn, int dark, int light) {
        scn.EnsureDSForcePile(dark);
        scn.EnsureLSForcePile(light);
        scn.DSUseForceCheat(scn.GetDSForcePileCount() - dark);
        scn.LSUseForceCheat(scn.GetLSForcePileCount() - light);
    }

    @Test
    public void ProjectiveTelepathyOriginalCancelsOwnBattleWithZeroForce() {
        var scn = battle("5_149o", true, 0);
        play(scn);
        assertFalse(scn.IsActiveBattle());
        assertEquals(0, scn.GetDSForcePileCount());
        assertUsed(scn);
    }

    @Test
    public void ProjectiveTelepathyOriginalCancelsOwnBattleWithOneForce() {
        var scn = battle("5_149o", true, 1);
        play(scn);
        assertFalse(scn.IsActiveBattle());
        assertEquals(1, scn.GetDSForcePileCount());
        assertUsed(scn);
    }

    @Test
    public void ProjectiveTelepathyOriginalMayCancelOwnBattleWithTwoForceAvailable() {
        var scn = battle("5_149o", true, 2);
        play(scn);
        scn.DSChoose("Cancel battle");
        scn.PassAllResponses();
        assertFalse(scn.IsActiveBattle());
        assertEquals(2, scn.GetDSForcePileCount());
        assertUsed(scn);
    }

    @Test
    public void ProjectiveTelepathyOriginalMayPayTwoForceToKeepOwnBattle() {
        var scn = battle("5_149o", true, 2);
        play(scn);
        scn.DSChoose("Use 2 Force");
        scn.PassAllResponses();
        assertTrue(scn.IsActiveBattle());
        assertEquals(0, scn.GetDSForcePileCount());
        assertUsed(scn);
    }

    @Test
    public void ProjectiveTelepathyOriginalAndCurrentLetOpponentPayToKeepBattle() {
        for (String id : List.of("5_149o", "5_149")) {
            var scn = battle(id, false, 2);
            play(scn);
            scn.LSChoose("Use 2 Force");
            scn.PassAllResponses();
            assertTrue(scn.IsActiveBattle());
            assertEquals(0, scn.GetLSForcePileCount());
            assertUsed(scn);
        }
    }

    @Test
    public void ProjectiveTelepathyOriginalCancelsOpponentBattleWhenPaymentIsUnavailable() {
        var scn = battle("5_149o", false, 1);
        play(scn);
        assertFalse(scn.IsActiveBattle());
        assertEquals(1, scn.GetLSForcePileCount());
        assertUsed(scn);
    }

    @Test
    public void ProjectiveTelepathyCurrentDoesNotOfferOwnBattleCancellation() {
        var scn = battle("5_149", true, 0);
        if (scn.LSGetDecision() != null)
            scn.LSPass();
        assertTrue(scn.IsActiveBattle());
        assertFalse(scn.DSCardPlayAvailable(scn.GetDSCard("telepathy")));
    }

    private VirtualTableScenario drain(boolean own, int force) {
        var scn = scenario("5_149o");
        scn.StartGame();
        scn.MoveCardsToDSHand(scn.GetDSCard("telepathy"));
        if (own) {
            scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetDSCard("alien"));
            scn.SkipToDSTurn(Phase.CONTROL);
        } else {
            scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetLSCard("rebel"));
            scn.SkipToLSTurn(Phase.CONTROL);
        }
        force(scn, own ? force : 0, own ? 0 : force);
        if (own)
            scn.DSForceDrainAt(scn.GetLSStartingLocation());
        else
            scn.LSForceDrainAt(scn.GetDSStartingLocation());
        return scn;
    }

    @Test
    public void ProjectiveTelepathyOriginalCancelsOwnForceDrainWithoutPayment() {
        var scn = drain(true, 1);
        int lost = scn.GetLSLostPileCount();
        play(scn);
        assertFalse(scn.IsActiveForceDrain());
        assertEquals(lost, scn.GetLSLostPileCount());
        assertEquals(1, scn.GetDSForcePileCount());
        assertUsed(scn);
    }

    @Test
    public void ProjectiveTelepathyOriginalLetsOpponentChooseToCancelTheirForceDrain() {
        var scn = drain(false, 2);
        int lost = scn.GetDSLostPileCount();
        play(scn);
        scn.LSChoose("Cancel Force drain");
        scn.PassAllResponses();
        assertFalse(scn.IsActiveForceDrain());
        assertEquals(lost, scn.GetDSLostPileCount());
        assertEquals(2, scn.GetLSForcePileCount());
        assertUsed(scn);
    }

    @Test
    public void ProjectiveTelepathyOriginalCancelsAngerFearAggressionAsItIsInserted() {
        var scn = scenario("5_149o");
        var anger = scn.GetLSCard("anger");
        scn.StartGame();
        scn.MoveCardsToLSHand(anger);
        scn.MoveCardsToDSHand(scn.GetDSCard("telepathy"));
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSPlayCard(anger);
        play(scn);
        assertTrue(scn.GetLSLostPile().contains(anger));
        assertFalse(scn.GetDSReserveDeck().contains(anger));
        assertUsed(scn);
    }

    @Test
    public void ProjectiveTelepathyOriginalMayPayToKeepOwnForceDrain() {
        var scn = drain(true, 2);
        int lost = scn.GetLSLostPileCount();
        play(scn);
        scn.DSChoose("Use 2 Force");
        scn.PassAllResponses();
        scn.PassForceDrainStartResponses();
        assertEquals(0, scn.GetDSForcePileCount());
        assertTrue(scn.IsActiveForceDrain());
        scn.LSChooseCard(scn.GetTopOfLSReserveDeck());
        scn.PassForceDrainEndResponses();
        assertEquals(lost + 1, scn.GetLSLostPileCount());
        assertFalse(scn.IsActiveForceDrain());
        assertUsed(scn);
    }

    @Test
    public void ProjectiveTelepathyOriginalMayDeclinePaymentForOwnForceDrain() {
        var scn = drain(true, 2);
        play(scn);
        scn.DSChoose("Cancel Force drain");
        scn.PassAllResponses();
        assertFalse(scn.IsActiveForceDrain());
        assertEquals(2, scn.GetDSForcePileCount());
        assertUsed(scn);
    }

    @Test
    public void ProjectiveTelepathyOriginalIsUnavailableOutsideItsResponseWindows() {
        var scn = scenario("5_149o");
        scn.StartGame();
        scn.MoveCardsToDSHand(scn.GetDSCard("telepathy"));
        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.AwaitingDSControlPhaseActions());
        assertFalse(scn.DSCardPlayAvailable(scn.GetDSCard("telepathy")));
    }

    @Test
    public void ProjectiveTelepathyOriginalCancelsAngerFearAggressionWhenRevealed() {
        var scn = scenario("5_149o");
        var anger = scn.GetLSCard("anger");
        scn.StartGame();
        scn.MoveCardsToLSHand(anger);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSPlayCard(anger);
        scn.PassAllResponses();
        assertTrue(scn.gameState().getReserveDeck(scn.DS, false).contains(anger));
        assertTrue(anger.isInserted());
        scn.SkipToDSTurn(Phase.ACTIVATE);
        scn.MoveCardsToDSHand(scn.GetDSCard("telepathy"));
        // Leave one ordinary card above the untouched insert; activating it reveals the insert normally.
        while (scn.gameState().getReserveDeck(scn.DS, false).indexOf(anger) > 1)
            scn.MoveCardsToBottomOfOwnReserveDeck((PhysicalCardImpl)
                    scn.gameState().getReserveDeck(scn.DS, false).getFirst());
        assertEquals(1, scn.gameState().getReserveDeck(scn.DS, false).indexOf(anger));
        assertTrue(anger.isInserted());
        scn.DSChooseAction("Activate Force");
        scn.DSDecided(1);
        if (scn.LSDecisionAvailable("Choose amount of Force"))
            scn.LSDecided(1);
        play(scn);
        scn.PassAllResponses();
        assertTrue(scn.GetLSLostPile().contains(anger));
        assertUsed(scn);
        int lost = scn.GetDSLostPileCount();
        scn.PassActivateActions();
        scn.SkipToPhase(Phase.MOVE);
        assertEquals(lost, scn.GetDSLostPileCount());
    }
}
