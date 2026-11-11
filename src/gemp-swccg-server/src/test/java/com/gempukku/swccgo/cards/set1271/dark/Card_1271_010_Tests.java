package com.gempukku.swccgo.cards.set1271.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.cards.set1271.LegacyShuffleObserver;
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

public class Card_1271_010_Tests {
    private VirtualTableScenario scenario() {
        var own = new HashMap<String, String>() {{
            put("effect", "1271_10");
            put("copy", "1271_10");
            put("character", "1_168");
            put("grabber", "12_128");
            put("search", "6_160");
            put("wrong", "1_194");
        }};
        var other = new HashMap<String, String>() {{ put("prerequisite", "1_21"); }};
        var scn = new VirtualTableScenario(other, own, 40, 40,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        // A Starting Interrupt fixture must not interrupt the no-starting-interrupt setup.
        scn.MoveOutOfPlay(scn.GetDSCard("search"));
        return scn;
    }

    private boolean pileCardSelectable(VirtualTableScenario scn, com.gempukku.swccgo.game.PhysicalCardImpl card) {
        String[] ids = scn.DSGetADParam("blueprintId");
        String[] selectable = scn.DSGetADParam("selectable");
        org.junit.Assert.assertNotNull(ids);
        org.junit.Assert.assertNotNull(selectable);
        for (int i = 0; i < ids.length; i++)
            if (ids[i].equals(card.getBlueprintId(true)) && Boolean.parseBoolean(selectable[i]))
                return true;
        return false;
    }

    @Test
    public void IWantEveryPartOfThisShipCheckedStatsIconsAndKeywordsAreCorrect() {
        var scn = scenario();
        var card = scn.GetDSCard("effect").getBlueprint();
        assertEquals("I Want Every Part Of This Ship Checked", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
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
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.MoveCardsToDSSideOfTable(scn.GetDSCard("effect"));
        if (prerequisite)
            scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("prerequisite"));
        scn.EnsureDSForcePile(20);
        scn.DrainDSForcePileTo(20);
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSCard("character"), scn.GetDSCard("grabber"), scn.GetDSCard("wrong"));
        scn.SkipToPhase(Phase.DEPLOY);
        return scn;
    }

    private void deployCharacter(VirtualTableScenario scn) {
        var shuffles = LegacyShuffleObserver.observe(scn);
        scn.DSUseCardAction(scn.GetDSCard("effect"), "Deploy Vader");
        scn.PassAllResponses();
        assertTrue(pileCardSelectable(scn, scn.GetDSCard("character")));
        assertFalse(pileCardSelectable(scn, scn.GetDSCard("wrong")));
        scn.DSChooseCard(scn.GetDSCard("character"));
        scn.PassAllResponses();
        scn.DSChooseCard(scn.GetDSStartingLocation());
        scn.PassAllResponses();
        assertTrue(shuffles.sawOwnedShuffle(scn.DS, Zone.RESERVE_DECK));
    }

    private boolean retryAvailable(VirtualTableScenario scn) {
        return scn.DSGetDecision() instanceof com.gempukku.swccgo.logic.decisions.CardActionSelectionDecision
                && scn.DSCardActionAvailable(scn.GetDSCard("effect"), "repeat the search");
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
    public void IWantEveryPartOfThisShipCheckedDeploysOnTableWithoutUsingForce() {
        var scn = scenario();
        scn.StartGame();
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.MoveCardsToDSHand(scn.GetDSCard("effect"));
        scn.SkipToPhase(Phase.DEPLOY);
        int force = scn.GetDSForcePileCount();
        scn.DSPlayCard(scn.GetDSCard("effect"));
        scn.PassAllResponses();
        assertEquals(Zone.SIDE_OF_TABLE, scn.GetDSCard("effect").getZone());
        assertEquals(force, scn.GetDSForcePileCount());
    }

    @Test
    public void IWantEveryPartOfThisShipCheckedRequiresTheNamedCharacterOnTableForTheRescue() {
        var scn = ready(false);
        assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("effect"), "Deploy Vader"));
        assertTrue(scn.DSCardActionAvailable(scn.GetDSCard("effect"), "Grabber"));
    }

    @Test
    public void IWantEveryPartOfThisShipCheckedDeploysTheMatchingCharacterForItsNormalCost() {
        var scn = ready(true);
        int force = scn.GetDSForcePileCount();
        deployCharacter(scn);
        assertEquals(scn.GetDSStartingLocation(), scn.GetDSCard("character").getAtLocation());
        assertEquals(force - 6, scn.GetDSForcePileCount());
        assertEquals(Zone.SIDE_OF_TABLE, scn.GetDSCard("effect").getZone());
    }

    @Test
    public void IWantEveryPartOfThisShipCheckedTheCharacterSearchRemainsUsedAfterReturningTheCharacterToReserve() {
        var scn = ready(true);
        deployCharacter(scn);
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSCard("character"));
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("effect"), "Deploy Vader"));
        assertTrue(scn.DSCardActionAvailable(scn.GetDSCard("effect"), "Grabber"));
    }

    @Test
    public void IWantEveryPartOfThisShipCheckedASecondCopyCannotRescueAgainInTheSameGame() {
        var scn = ready(true);
        deployCharacter(scn);
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSCard("character"));
        scn.MoveCardsToTopOfDSLostPile(scn.GetDSCard("effect"));
        scn.MoveCardsToDSSideOfTable(scn.GetDSCard("copy"));
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("copy"), "Deploy Vader"));
        assertTrue(scn.DSCardActionAvailable(scn.GetDSCard("copy"), "Grabber"));
    }

    @Test
    public void IWantEveryPartOfThisShipCheckedPlacesItselfInUsedPileToTakeOnlyAGrabber() {
        var scn = ready(false);
        var shuffles = LegacyShuffleObserver.observe(scn);
        scn.DSUseCardAction(scn.GetDSCard("effect"), "Grabber");
        scn.PassAllResponses();
        assertTrue(scn.GetDSUsedPile().contains(scn.GetDSCard("effect")));
        assertTrue(pileCardSelectable(scn, scn.GetDSCard("grabber")));
        assertFalse(pileCardSelectable(scn, scn.GetDSCard("character")));
        assertFalse(pileCardSelectable(scn, scn.GetDSCard("wrong")));
        scn.DSChooseCard(scn.GetDSCard("grabber"));
        scn.PassAllResponses();
        assertEquals(Zone.HAND, scn.GetDSCard("grabber").getZone());
        assertTrue(shuffles.sawOwnedShuffle(scn.DS, Zone.RESERVE_DECK));
    }

    @Test
    public void IWantEveryPartOfThisShipCheckedMayRepeatAnActualFailedDeploymentInItsForcePile() {
        var scn = ready(true);
        var shuffles = LegacyShuffleObserver.observe(scn);
        scn.MoveCardsToTopOfDSForcePile(scn.GetDSCard("character"));
        scn.DSUseCardAction(scn.GetDSCard("effect"), "Deploy Vader");
        scn.PassAllResponses();
        reachRetry(scn);
        assertTrue(retryAvailable(scn));
        scn.DSUseCardAction(scn.GetDSCard("effect"), "repeat the search");
        scn.PassAllResponses();
        assertTrue(scn.GetDSLostPile().contains(scn.GetDSCard("effect")));
        assertTrue(pileCardSelectable(scn, scn.GetDSCard("character")));
        assertFalse(pileCardSelectable(scn, scn.GetDSCard("wrong")));
        scn.DSChooseCard(scn.GetDSCard("character"));
        scn.PassAllResponses();
        scn.DSChooseCard(scn.GetDSStartingLocation());
        scn.PassAllResponses();
        assertEquals(scn.GetDSStartingLocation(), scn.GetDSCard("character").getAtLocation());
        assertTrue(shuffles.sawOwnedShuffle(scn.DS, Zone.FORCE_PILE));
    }

    @Test
    public void IWantEveryPartOfThisShipCheckedMayDeclineTheRetryAndKeepItselfOnTable() {
        var scn = ready(true);
        scn.MoveCardsToTopOfDSForcePile(scn.GetDSCard("character"));
        scn.DSUseCardAction(scn.GetDSCard("effect"), "Deploy Vader");
        scn.PassAllResponses();
        reachRetry(scn);
        assertTrue(retryAvailable(scn));
        scn.PassAllResponses();
        assertEquals(Zone.SIDE_OF_TABLE, scn.GetDSCard("effect").getZone());
        assertTrue(scn.GetDSForcePile().contains(scn.GetDSCard("character")));
    }

    @Test
    public void IWantEveryPartOfThisShipCheckedRepeatsAnActualFailedTakeIntoHandWithTheOriginalFilter() {
        var scn = ready(false);
        var shuffles = LegacyShuffleObserver.observe(scn);
        scn.MoveCardsToDSHand(scn.GetDSCard("search"));
        scn.MoveCardsToTopOfDSLostPile(scn.GetDSCard("copy"));
        scn.MoveCardsToTopOfDSForcePile(scn.GetDSCard("grabber"), scn.GetDSCard("wrong"));
        // The search Interrupt costs3 Force: keep the desired retry target below that payment.
        scn.MoveCardsToTopOfDSForcePile(scn.GetDSCard("filler-01"), scn.GetDSCard("filler-02"), scn.GetDSCard("filler-03"));
        // Refresh the current action list after arranging the hidden-zone fixtures.
        scn.SkipToPhase(Phase.MOVE);
        scn.DSPlayCard(scn.GetDSCard("search"));
        scn.PassAllResponses();
        reachRetry(scn);
        assertTrue(retryAvailable(scn));
        scn.DSUseCardAction(scn.GetDSCard("effect"), "repeat the search");
        scn.PassAllResponses();
        assertTrue(scn.GetDSLostPile().contains(scn.GetDSCard("effect")));
        assertTrue(pileCardSelectable(scn, scn.GetDSCard("grabber")));
        assertFalse(pileCardSelectable(scn, scn.GetDSCard("wrong")));
        scn.DSChooseCard(scn.GetDSCard("grabber"));
        scn.PassAllResponses();
        assertEquals(Zone.HAND, scn.GetDSCard("grabber").getZone());
        assertTrue(scn.GetDSUsedPile().contains(scn.GetDSCard("search")));
        assertTrue(shuffles.sawOwnedShuffle(scn.DS, Zone.FORCE_PILE));
    }

    @Test
    public void IWantEveryPartOfThisShipCheckedDoesNotOfferTheRetryOutsideAFailedSearchResponse() {
        var scn = ready(false);
        assertFalse(retryAvailable(scn));
    }

    @Test
    public void IWantEveryPartOfThisShipCheckedCannotDeployASecondCopyOfItsCharacterPersona() {
        var scn = ready(true);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("character"));
        scn.SkipToPhase(Phase.MOVE);
        assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("effect"), "Deploy Vader"));
    }

    @Test
    public void IWantEveryPartOfThisShipCheckedMayDeployTheCharacterDuringTheOpponentsTurn() {
        var scn = ready(true);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.LSPass();
        deployCharacter(scn);
        assertEquals(scn.GetDSStartingLocation(), scn.GetDSCard("character").getAtLocation());
    }

    @Test
    public void IWantEveryPartOfThisShipCheckedCannotRetryWhenItsForcePileIsEmpty() {
        var scn = ready(false);
        scn.MoveCardsToDSHand(scn.GetDSCard("search"));
        scn.MoveCardsToTopOfDSLostPile(scn.GetDSCard("copy"));
        scn.MoveOutOfPlay(scn.GetDSCard("grabber"));
        scn.DrainDSForcePileTo(3);
        scn.SkipToPhase(Phase.MOVE);
        scn.DSPlayCard(scn.GetDSCard("search"));
        scn.PassAllResponses();
        reachRetry(scn);
        assertEquals(0, scn.GetDSForcePileCount());
        assertFalse(retryAvailable(scn));
        assertEquals(Zone.SIDE_OF_TABLE, scn.GetDSCard("effect").getZone());
    }

    @Test
    public void IWantEveryPartShowsThePcCardListPcE1Text() {
        String t = com.gempukku.swccgo.framework.VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint("1271_10").getGameText();
        org.junit.Assert.assertEquals("Deploy on table. Once per game, if Obi-Wan on table, may [download] Vader. May place this Effect in Used Pile to [upload] a [Grabber]. If you just attempted to [download] or [upload] a card, and the card was not found, may place this Effect in Lost Pile to search your Force Pile for that card; reshuffle.", t);
        org.junit.Assert.assertFalse(t.contains("for that card; reshuffle"));
    }
}
