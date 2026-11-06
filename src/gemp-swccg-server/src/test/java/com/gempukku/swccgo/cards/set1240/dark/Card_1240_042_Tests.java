package com.gempukku.swccgo.cards.set1240.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.*;

public class Card_1240_042_Tests {
    private static final String LOST = "Add 5 to total battle destiny";

    private VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("luke", "1_19"); put("obiwan", "1_21");
        }}, new HashMap<>() {{
            put("frustration", "1240_42"); put("xizor", "10_45"); put("guri", "10_41");
            put("r2", "2_99"); put("ship", "1_301"); put("system", "1_282");
            put("through", "12_161"); put("box", "1_262"); put("tech", "2_105");
            put("dodo", "7_176"); put("scan", "1_224");
            put("a", "1_194"); put("b", "1_164"); put("c", "1_172");
        }}, 60, 60, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    private void Reach(VirtualTableScenario scn, PhysicalCardImpl card, String action) {
        for (int i = 0; i < 20 && !scn.DSCardActionAvailable(card, action); i++) {
            assertTrue(scn.GetCurrentDecision().getText().toLowerCase().contains("optional response"));
            scn.PlayerPass(scn.GetDecidingPlayer());
        }
        assertTrue(scn.DSCardActionAvailable(card, action));
    }

    @Test
    public void FrustrationPrintedStatsIconsKeywordsAndBothInterruptModesAreCorrect() {
        var scn = GetScenario();
        var bp = scn.GetDSCard("frustration").getBlueprint();
        assertEquals(Title.Frustration, bp.getTitle());
        assertTrue(bp.hasVirtualSuffix());
        assertEquals(Side.DARK, bp.getSide());
        assertEquals(Uniqueness.UNIQUE, bp.getUniqueness());
        assertEquals(3, bp.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.USED_OR_LOST, bp.getCardSubtype());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, bp.getExpansionSet());
        assertEquals(Rarity.V, bp.getRarity());
        assertEquals("'Rrraaaarrr!'", bp.getLore());
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>(List.of(CardType.INTERRUPT)));
        scn.BlueprintIconCheck(bp, new ArrayList<>(List.of(Icon.INTERRUPT, Icon.DAGOBAH, Icon.LEGACY_BLOCK_4)));
        scn.BlueprintKeywordCheck(bp, new ArrayList<Keyword>());
    }

    private VirtualTableScenario ReservePeekReady() {
        var scn = GetScenario();
        scn.StartGame();
        scn.SkipToDSTurn(Phase.ACTIVATE);
        scn.MoveLocationToTable(scn.GetDSCard("system"));
        scn.MoveCardsToLocation(scn.GetDSCard("system"), scn.GetDSCard("ship"));
        scn.BoardAsPassenger(scn.GetDSCard("ship"), scn.GetDSCard("r2"));
        scn.MoveCardsToDSHand(scn.GetDSCard("frustration"));
        scn.EnsureDSForcePile(10);
        scn.SkipToPhase(Phase.DRAW);
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSCard("c"), scn.GetDSCard("b"), scn.GetDSCard("a"));
        return scn;
    }

    private void PeekReserveAndPlay(VirtualTableScenario scn) {
        scn.DSUseCardAction(scn.GetDSCard("r2"), "Peek at top 3 cards of Reserve Deck");
        scn.PassAllResponses();
        assertTrue(scn.DSDecisionAvailable("Top cards of Reserve Deck"));
        scn.DSPass();
        Reach(scn, scn.GetDSCard("frustration"), "Peek at and reorder top 3");
        scn.DSPlayCard(scn.GetDSCard("frustration"), "Peek at and reorder top 3");
        scn.PassAllResponses();
    }

    private void ReorderThree(VirtualTableScenario scn) {
        scn.DSChooseCard(scn.GetDSCard("b"));
        scn.DSChooseCard(scn.GetDSCard("a"));
        scn.DSChooseCard(scn.GetDSCard("c"));
        assertTrue(scn.DSDecisionAvailable("Take one of those cards into hand?"));
    }

    @Test
    public void FrustrationUsedReordersThreeReserveCardsAndMayDeclineTakingOne() {
        var scn = ReservePeekReady();
        int hand = scn.GetDSHandCount();
        PeekReserveAndPlay(scn);
        ReorderThree(scn);
        scn.DSChooseNo();
        scn.PassAllResponses();
        assertEquals(List.of(scn.GetDSCard("c"), scn.GetDSCard("a"), scn.GetDSCard("b")),
                scn.GetDSReserveDeck().subList(0, 3));
        assertEquals(hand - 1, scn.GetDSHandCount());
        assertInZone(Zone.USED_PILE, scn.GetDSCard("frustration"));
    }

    @Test
    public void FrustrationUsedMayTakeTheCardReorderedToTopIntoHand() {
        var scn = ReservePeekReady();
        PeekReserveAndPlay(scn);
        ReorderThree(scn);
        scn.DSChooseYes();
        scn.PassAllResponses();
        assertInZone(Zone.HAND, scn.GetDSCard("c"));
        assertEquals(scn.GetDSCard("a"), scn.GetTopOfDSReserveDeck());
        assertInZone(Zone.USED_PILE, scn.GetDSCard("frustration"));
    }

    @Test
    public void FrustrationUsedHandlesAReserveDeckWithOnlyTwoCards() {
        var scn = ReservePeekReady();
        while (scn.GetDSReserveDeckCount() > 2) {
            scn.MoveCardsToDSHand((PhysicalCardImpl) scn.GetDSReserveDeck().getLast());
        }
        scn.DSUseCardAction(scn.GetDSCard("r2"), "Peek at top 3 cards of Reserve Deck");
        scn.PassAllResponses();
        scn.DSPass();
        Reach(scn, scn.GetDSCard("frustration"), "Peek at and reorder top 3");
        scn.DSPlayCard(scn.GetDSCard("frustration"), "Peek at and reorder top 3");
        scn.PassAllResponses();
        assertEquals(2, scn.DSGetCardChoiceCount());
        scn.DSChooseCard(scn.GetDSCard("a"));
        scn.DSChooseCard(scn.GetDSCard("b"));
        scn.DSChooseNo();
        scn.PassAllResponses();
        assertEquals(List.of(scn.GetDSCard("b"), scn.GetDSCard("a")), scn.GetDSReserveDeck());
    }

    @Test
    public void FrustrationUsedReordersForcePileAfterAnActualPeekAndTakesOne() {
        var scn = GetScenario();
        scn.StartGame();
        scn.SkipToDSTurn(Phase.ACTIVATE);
        scn.MoveCardsToDSHand(scn.GetDSCard("frustration"), scn.GetDSCard("through"));
        scn.EnsureDSForcePile(10);
        scn.SkipToPhase(Phase.CONTROL);
        scn.MoveCardsToTopOfDSForcePile(scn.GetDSCard("c"), scn.GetDSCard("b"), scn.GetDSCard("a"));
        scn.DSPlayCard(scn.GetDSCard("through"), "Peek at top card of Force Pile");
        scn.PassAllResponses();
        assertTrue(scn.DSDecisionAvailable("Top card of Force Pile"));
        scn.DSPass();
        Reach(scn, scn.GetDSCard("frustration"), "Peek at and reorder top 3");
        scn.DSPlayCard(scn.GetDSCard("frustration"), "Peek at and reorder top 3");
        scn.PassAllResponses();
        ReorderThree(scn);
        scn.DSChooseYes();
        scn.PassAllResponses();
        scn.DSChoose("Return it");
        scn.PassAllResponses();
        assertInZone(Zone.HAND, scn.GetDSCard("c"));
        assertEquals(scn.GetDSCard("a"), scn.GetTopOfDSForcePile());
        assertInZone(Zone.USED_PILE, scn.GetDSCard("frustration"));
    }

    private VirtualTableScenario MusicianPeekReady(Zone pile) {
        var scn = GetScenario();
        scn.StartGame();
        scn.SkipToDSTurn(Phase.ACTIVATE);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("tech"), scn.GetDSCard("dodo"));
        scn.MoveCardsToDSHand(scn.GetDSCard("frustration"), scn.GetDSCard("box"));
        scn.SkipToPhase(Phase.CONTROL);
        if (pile == Zone.USED_PILE) scn.MoveCardsToTopOfDSUsedPile(scn.GetDSCard("a"), scn.GetDSCard("b"), scn.GetDSCard("c"));
        else scn.MoveCardsToTopOfDSLostPile(scn.GetDSCard("a"), scn.GetDSCard("b"), scn.GetDSCard("c"));
        scn.DSPlayCard(scn.GetDSCard("box"));
        scn.DSChooseCard(pile == Zone.USED_PILE ? scn.GetTopOfDSUsedPile() : scn.GetTopOfDSLostPile());
        Reach(scn, scn.GetDSCard("tech"), "Peek at top cards of shuffled pile");
        scn.DSUseCardAction(scn.GetDSCard("tech"), "Peek at top cards of shuffled pile");
        assertTrue(scn.DSDecisionAvailable("Top card"));
        scn.DSPass();
        return scn;
    }

    @Test
    public void FrustrationUsedReordersUsedPileAfterAnActualPeekAndTakesOne() {
        var scn = MusicianPeekReady(Zone.USED_PILE);
        var top = new ArrayList<>(scn.GetDSUsedPile().subList(0, 3));
        Reach(scn, scn.GetDSCard("frustration"), "Peek at and reorder top 3");
        scn.DSPlayCard(scn.GetDSCard("frustration"), "Peek at and reorder top 3");
        scn.PassAllResponses();
        scn.DSChooseCard((PhysicalCardImpl) top.get(0));
        scn.DSChooseCard((PhysicalCardImpl) top.get(1));
        scn.DSChooseCard((PhysicalCardImpl) top.get(2));
        scn.DSChooseYes();
        scn.PassAllResponses();
        scn.DSChooseNo(); // Tech Mo'r does not reshuffle after Frustration's reorder.
        scn.PassAllResponses();
        assertInZone(Zone.HAND, (PhysicalCardImpl) top.get(2));
        assertInZone(Zone.USED_PILE, scn.GetDSCard("frustration"));
        assertTrue(scn.GetDSUsedPile().indexOf(top.get(1)) < scn.GetDSUsedPile().indexOf(top.get(0)));
    }

    @Test
    public void FrustrationUsedDoesNotRespondToAPeekAtTheLostPile() {
        var scn = MusicianPeekReady(Zone.LOST_PILE);
        scn.PassAllResponses();
        assertTrue(scn.DSDecisionAvailable("Do you want to re-shuffle this card pile?"));
        assertFalse(scn.DSCardPlayAvailable(scn.GetDSCard("frustration")));
        scn.DSChooseNo();
        scn.PassAllResponses();
        assertInZone(Zone.HAND, scn.GetDSCard("frustration"));
    }

    private VirtualTableScenario BattleReady(boolean xizorHere, boolean guriHere) {
        var scn = GetScenario();
        scn.StartGame();
        scn.SkipToDSTurn(Phase.ACTIVATE);
        var site = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetLSCard("luke"), scn.GetLSCard("obiwan"), scn.GetDSCard("b"));
        scn.MoveCardsToLocation(xizorHere ? site : scn.GetLSStartingLocation(), scn.GetDSCard("xizor"));
        scn.MoveCardsToLocation(guriHere ? site : scn.GetLSStartingLocation(), scn.GetDSCard("guri"));
        scn.MoveCardsToDSHand(scn.GetDSCard("frustration"));
        scn.MoveCardsToTopOfDSLostPile(scn.GetDSCard("a"));
        scn.EnsureDSForcePile(10);
        scn.SkipToPhase(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        return scn;
    }

    @Test
    public void FrustrationLostAddsFiveToActualBattleDestinyAndMayRetrieveOneForce() {
        var scn = BattleReady(true, true);
        scn.DSPlayCard(scn.GetDSCard("frustration"), LOST);
        scn.PassAllResponses();
        scn.DSChooseYes();
        scn.PassAllResponses();
        assertInZone(Zone.USED_PILE, scn.GetDSCard("a"));
        assertInZone(Zone.LOST_PILE, scn.GetDSCard("frustration"));
        scn.PrepareDSDestiny(2);
        scn.PrepareLSDestiny(0);
        scn.SkipToEndOfPowerSegment(true);
        assertEquals(7, scn.GetDSTotalDestiny());
    }

    @Test
    public void FrustrationLostMayDeclineRetrievalWithoutLosingTheBattleDestinyBonus() {
        var scn = BattleReady(true, true);
        scn.DSPlayCard(scn.GetDSCard("frustration"), LOST);
        scn.PassAllResponses();
        scn.DSChooseNo();
        scn.PassAllResponses();
        assertInZone(Zone.LOST_PILE, scn.GetDSCard("a"), scn.GetDSCard("frustration"));
        scn.PrepareDSDestiny(2);
        scn.PrepareLSDestiny(0);
        scn.SkipToEndOfPowerSegment(true);
        assertEquals(7, scn.GetDSTotalDestiny());
    }

    @Test
    public void FrustrationLostRequiresXizorAndGuriInTheSameBattle() {
        var noGuri = BattleReady(true, false);
        assertTrue(noGuri.AwaitingDSWeaponsSegmentActions());
        assertFalse(noGuri.DSCardPlayAvailable(noGuri.GetDSCard("frustration")));
        var noXizor = BattleReady(false, true);
        assertTrue(noXizor.AwaitingDSWeaponsSegmentActions());
        assertFalse(noXizor.DSCardPlayAvailable(noXizor.GetDSCard("frustration")));
    }

    @Test
    public void FrustrationDoesNotPlayOutsideBattleWithoutAJustCompletedPeek() {
        var scn = GetScenario();
        scn.StartGame();
        scn.SkipToDSTurn(Phase.ACTIVATE);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("xizor"), scn.GetDSCard("guri"));
        scn.MoveCardsToDSHand(scn.GetDSCard("frustration"));
        scn.SkipToPhase(Phase.CONTROL);
        assertTrue(scn.AwaitingDSControlPhaseActions());
        assertFalse(scn.DSCardPlayAvailable(scn.GetDSCard("frustration")));
    }

    @Test
    public void FrustrationLostBattleDestinyBonusExpiresBeforeTheNextBattle() {
        var scn = BattleReady(true, true);
        scn.DSPlayCard(scn.GetDSCard("frustration"), LOST);
        scn.PassAllResponses();
        scn.DSChooseNo();
        scn.PassAllResponses();
        scn.PrepareDSDestiny(2);
        scn.PrepareLSDestiny(0);
        scn.SkipToEndOfPowerSegment(true);
        assertEquals(7, scn.GetDSTotalDestiny());
        scn.PassResponses("INITIAL_ATTRITION_CALCULATED");
        scn.LSPayRemainingBattleDamageFromReserveDeck();
        scn.LSPayAttritionFromCardInPlay(scn.GetLSCard("luke"));
        scn.SkipToPhase(Phase.MOVE);
        assertAtLocation(scn.GetDSStartingLocation(), scn.GetLSCard("obiwan"));
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(scn.GetDSStartingLocation());
        scn.PrepareDSDestiny(2);
        scn.PrepareLSDestiny(0);
        scn.SkipToEndOfPowerSegment(true);
        assertEquals(2, scn.GetDSTotalDestiny());
    }

    @Test
    public void FrustrationUsedDoesNotRespondToYourPeekAtOpponentsReserveDeck() {
        var scn = GetScenario();
        scn.StartGame();
        scn.SkipToDSTurn(Phase.ACTIVATE);
        scn.MoveCardsToLSSideOfTable(scn.GetDSCard("scan"));
        scn.MoveCardsToDSHand(scn.GetDSCard("frustration"));
        scn.EnsureDSForcePile(5);
        scn.SkipToPhase(Phase.CONTROL);
        scn.DSUseCardAction(scn.GetDSCard("scan"), "Peek at top of opponent's Reserve Deck");
        scn.PassAllResponses();
        assertTrue(scn.DSDecisionAvailable("Top card"));
        scn.DSPass();
        scn.PassAllResponses();
        assertTrue(scn.AwaitingDSControlPhaseActions());
        assertFalse(scn.DSCardPlayAvailable(scn.GetDSCard("frustration")));
    }

    @Test
    public void FrustrationUsedRespondsToAPrintedPeekAndReorderAbility() {
        // Shared prerequisite requested in integration182; keep this real producer regression.
        var scn = GetScenario();
        scn.StartGame();
        scn.SkipToDSTurn(Phase.ACTIVATE);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("dodo"), scn.GetDSCard("tech"));
        scn.MoveCardsToDSHand(scn.GetDSCard("frustration"));
        scn.EnsureDSForcePile(5);
        scn.SkipToPhase(Phase.CONTROL);
        scn.MoveCardsToTopOfDSForcePile(scn.GetDSCard("c"), scn.GetDSCard("b"), scn.GetDSCard("a"));
        scn.DSUseCardAction(scn.GetDSCard("dodo"), "Peak at top 1 cards");
        scn.PassAllResponses();
        scn.DSChooseCard(scn.GetDSCard("a"));
        Reach(scn, scn.GetDSCard("frustration"), "Peek at and reorder top 3");
        scn.DSPlayCard(scn.GetDSCard("frustration"), "Peek at and reorder top 3");
        scn.PassAllResponses();
        ReorderThree(scn);
        scn.DSChooseNo();
        scn.PassAllResponses();
        assertEquals(List.of(scn.GetDSCard("c"), scn.GetDSCard("a"), scn.GetDSCard("b")),
                scn.GetDSForcePile().subList(0, 3));
        assertInZone(Zone.USED_PILE, scn.GetDSCard("frustration"));
    }
}
