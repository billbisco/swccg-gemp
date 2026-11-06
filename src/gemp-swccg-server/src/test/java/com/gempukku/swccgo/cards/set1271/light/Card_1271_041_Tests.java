package com.gempukku.swccgo.cards.set1271.light;

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
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1271_041_Tests {
    private VirtualTableScenario scenario() {
        var own = new HashMap<String, String>() {{
            put("effect", "1271_41");
            put("copy", "1271_41");
            put("character", "1_21");
            put("grabber", "12_37");
            put("search", "6_77");
            put("wrong", "1_11");
        }};
        var other = new HashMap<String, String>() {{ put("prerequisite", "1_168"); }};
        var scn = new VirtualTableScenario(own, other, 40, 40,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        // A Starting Interrupt fixture must not interrupt the no-starting-interrupt setup.
        scn.MoveOutOfPlay(scn.GetLSCard("search"));
        return scn;
    }

    private boolean pileCardSelectable(VirtualTableScenario scn, com.gempukku.swccgo.game.PhysicalCardImpl card) {
        String[] ids = scn.LSGetADParam("blueprintId");
        String[] selectable = scn.LSGetADParam("selectable");
        org.junit.Assert.assertNotNull(ids);
        org.junit.Assert.assertNotNull(selectable);
        for (int i = 0; i < ids.length; i++)
            if (ids[i].equals(card.getBlueprintId(true)) && Boolean.parseBoolean(selectable[i]))
                return true;
        return false;
    }

    @Test
    public void OhHesNotDeadNotYetStatsIconsAndKeywordsAreCorrect() {
        var scn = scenario();
        var card = scn.GetLSCard("effect").getBlueprint();
        assertEquals("Oh, He's Not Dead, Not Yet", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{ add(CardType.EFFECT); }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT); add(Icon.SETUP); add(Icon.LEGACY_BLOCK_7);
        }});
    }

    private VirtualTableScenario ready(boolean prerequisite) {
        var scn = scenario();
        scn.StartGame();
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToLSSideOfTable(scn.GetLSCard("effect"));
        if (prerequisite)
            scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("prerequisite"));
        scn.EnsureLSForcePile(20);
        scn.DrainLSForcePileTo(20);
        scn.MoveCardsToTopOfLSReserveDeck(scn.GetLSCard("character"), scn.GetLSCard("grabber"), scn.GetLSCard("wrong"));
        scn.SkipToPhase(Phase.DEPLOY);
        return scn;
    }

    private void deployCharacter(VirtualTableScenario scn) {
        scn.LSUseCardAction(scn.GetLSCard("effect"), "Deploy Obi-Wan");
        scn.PassAllResponses();
        assertTrue(pileCardSelectable(scn, scn.GetLSCard("character")));
        assertFalse(pileCardSelectable(scn, scn.GetLSCard("wrong")));
        scn.LSChooseCard(scn.GetLSCard("character"));
        scn.PassAllResponses();
        scn.LSChooseCard(scn.GetLSStartingLocation());
        scn.PassAllResponses();
    }

    private boolean retryAvailable(VirtualTableScenario scn) {
        return scn.LSGetDecision() instanceof com.gempukku.swccgo.logic.decisions.CardActionSelectionDecision
                && scn.LSCardActionAvailable(scn.GetLSCard("effect"), "repeat the search");
    }

    private void reachRetry(VirtualTableScenario scn) {
        for (int i = 0; i < 30; i++) {
            if (retryAvailable(scn))
                return;
            var decision = scn.GetCurrentDecision();
            if (decision == null)
                return;
            String text = decision.getText().toLowerCase();
            if (!text.contains("verify") && !text.contains("optional response"))
                return;
            scn.PlayerPass(scn.GetDecidingPlayer());
        }
    }

    @Test
    public void OhHesNotDeadNotYetDeploysOnTableWithoutUsingForce() {
        var scn = scenario();
        scn.StartGame();
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToLSHand(scn.GetLSCard("effect"));
        scn.SkipToPhase(Phase.DEPLOY);
        int force = scn.GetLSForcePileCount();
        scn.LSPlayCard(scn.GetLSCard("effect"));
        scn.PassAllResponses();
        assertEquals(Zone.SIDE_OF_TABLE, scn.GetLSCard("effect").getZone());
        assertEquals(force, scn.GetLSForcePileCount());
    }

    @Test
    public void OhHesNotDeadNotYetRequiresTheNamedCharacterOnTableForTheRescue() {
        var scn = ready(false);
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("effect"), "Deploy Obi-Wan"));
        assertTrue(scn.LSCardActionAvailable(scn.GetLSCard("effect"), "Grabber"));
    }

    @Test
    public void OhHesNotDeadNotYetDeploysTheMatchingCharacterForItsNormalCost() {
        var scn = ready(true);
        int force = scn.GetLSForcePileCount();
        deployCharacter(scn);
        assertEquals(scn.GetLSStartingLocation(), scn.GetLSCard("character").getAtLocation());
        assertEquals(force - 5, scn.GetLSForcePileCount());
        assertEquals(Zone.SIDE_OF_TABLE, scn.GetLSCard("effect").getZone());
    }

    @Test
    public void OhHesNotDeadNotYetTheCharacterSearchRemainsUsedAfterReturningTheCharacterToReserve() {
        var scn = ready(true);
        deployCharacter(scn);
        scn.MoveCardsToTopOfLSReserveDeck(scn.GetLSCard("character"));
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("effect"), "Deploy Obi-Wan"));
        assertTrue(scn.LSCardActionAvailable(scn.GetLSCard("effect"), "Grabber"));
    }

    @Test
    public void OhHesNotDeadNotYetASecondCopyCannotRescueAgainInTheSameGame() {
        var scn = ready(true);
        deployCharacter(scn);
        scn.MoveCardsToTopOfLSReserveDeck(scn.GetLSCard("character"));
        scn.MoveCardsToTopOfLSLostPile(scn.GetLSCard("effect"));
        scn.MoveCardsToLSSideOfTable(scn.GetLSCard("copy"));
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("copy"), "Deploy Obi-Wan"));
        assertTrue(scn.LSCardActionAvailable(scn.GetLSCard("copy"), "Grabber"));
    }

    @Test
    public void OhHesNotDeadNotYetPlacesItselfInUsedPileToTakeOnlyAGrabber() {
        var scn = ready(false);
        scn.LSUseCardAction(scn.GetLSCard("effect"), "Grabber");
        scn.PassAllResponses();
        assertTrue(scn.GetLSUsedPile().contains(scn.GetLSCard("effect")));
        assertTrue(pileCardSelectable(scn, scn.GetLSCard("grabber")));
        assertFalse(pileCardSelectable(scn, scn.GetLSCard("character")));
        assertFalse(pileCardSelectable(scn, scn.GetLSCard("wrong")));
        scn.LSChooseCard(scn.GetLSCard("grabber"));
        scn.PassAllResponses();
        assertEquals(Zone.HAND, scn.GetLSCard("grabber").getZone());
    }

    @Test
    public void OhHesNotDeadNotYetMayRepeatAnActualFailedDeploymentInItsForcePile() {
        var scn = ready(true);
        scn.MoveCardsToTopOfLSForcePile(scn.GetLSCard("character"));
        scn.LSUseCardAction(scn.GetLSCard("effect"), "Deploy Obi-Wan");
        scn.PassAllResponses();
        reachRetry(scn);
        assertTrue(retryAvailable(scn));
        scn.LSUseCardAction(scn.GetLSCard("effect"), "repeat the search");
        scn.PassAllResponses();
        assertTrue(scn.GetLSLostPile().contains(scn.GetLSCard("effect")));
        assertTrue(pileCardSelectable(scn, scn.GetLSCard("character")));
        assertFalse(pileCardSelectable(scn, scn.GetLSCard("wrong")));
        scn.LSChooseCard(scn.GetLSCard("character"));
        scn.PassAllResponses();
        scn.LSChooseCard(scn.GetLSStartingLocation());
        scn.PassAllResponses();
        assertEquals(scn.GetLSStartingLocation(), scn.GetLSCard("character").getAtLocation());
    }

    @Test
    public void OhHesNotDeadNotYetMayDeclineTheRetryAndKeepItselfOnTable() {
        var scn = ready(true);
        scn.MoveCardsToTopOfLSForcePile(scn.GetLSCard("character"));
        scn.LSUseCardAction(scn.GetLSCard("effect"), "Deploy Obi-Wan");
        scn.PassAllResponses();
        reachRetry(scn);
        assertTrue(retryAvailable(scn));
        scn.PassAllResponses();
        assertEquals(Zone.SIDE_OF_TABLE, scn.GetLSCard("effect").getZone());
        assertTrue(scn.GetLSForcePile().contains(scn.GetLSCard("character")));
    }

    @Test
    public void OhHesNotDeadNotYetRepeatsAnActualFailedTakeIntoHandWithTheOriginalFilter() {
        var scn = ready(false);
        scn.MoveCardsToLSHand(scn.GetLSCard("search"));
        scn.MoveCardsToTopOfLSLostPile(scn.GetLSCard("copy"));
        scn.MoveCardsToTopOfLSForcePile(scn.GetLSCard("grabber"), scn.GetLSCard("wrong"));
        // The search Interrupt costs3 Force: keep the desired retry target below that payment.
        scn.MoveCardsToTopOfLSForcePile(scn.GetLSCard("filler-01"), scn.GetLSCard("filler-02"), scn.GetLSCard("filler-03"));
        // Refresh the current action list after arranging the hidden-zone fixtures.
        scn.SkipToPhase(Phase.MOVE);
        scn.LSPlayCard(scn.GetLSCard("search"));
        scn.PassAllResponses();
        reachRetry(scn);
        assertTrue(retryAvailable(scn));
        scn.LSUseCardAction(scn.GetLSCard("effect"), "repeat the search");
        scn.PassAllResponses();
        assertTrue(scn.GetLSLostPile().contains(scn.GetLSCard("effect")));
        assertTrue(pileCardSelectable(scn, scn.GetLSCard("grabber")));
        assertFalse(pileCardSelectable(scn, scn.GetLSCard("wrong")));
        scn.LSChooseCard(scn.GetLSCard("grabber"));
        scn.PassAllResponses();
        assertEquals(Zone.HAND, scn.GetLSCard("grabber").getZone());
        assertTrue(scn.GetLSUsedPile().contains(scn.GetLSCard("search")));
    }

    @Test
    public void OhHesNotDeadNotYetDoesNotOfferTheRetryOutsideAFailedSearchResponse() {
        var scn = ready(false);
        assertFalse(retryAvailable(scn));
    }

    @Test
    public void OhHesNotDeadNotYetCannotDeployASecondCopyOfItsCharacterPersona() {
        var scn = ready(true);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("character"));
        scn.SkipToPhase(Phase.MOVE);
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("effect"), "Deploy Obi-Wan"));
    }
}
