package com.gempukku.swccgo.cards.set12.dark;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_12_106_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
            new HashMap<>() {{
            }},
            new HashMap<>() {{
                put("gardulla", "12_106");
            }},
            10,
            10,
            StartingSetup.DefaultLSGroundLocation,
            StartingSetup.DefaultDSGroundLocation,
            StartingSetup.NoLSStartingInterrupts,
            StartingSetup.NoDSStartingInterrupts,
            StartingSetup.NoLSShields,
            StartingSetup.NoDSShields,
            VirtualTableScenario.Open
        );
    }

    private static void EmptyLSReserveDeck(VirtualTableScenario scn) {
        while (scn.GetLSReserveDeckCount() > 0) {
            scn.MoveCardsToTopOfLSUsedPile(scn.GetTopOfLSReserveDeck());
        }
    }

    @Test
    public void GardullaTheHuttDrawActionOfferedWhenOpponentHasNoReserveDeck() {
        var scn = GetScenario();

        var gardulla = scn.GetDSCard("gardulla");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, gardulla);

        scn.SkipToPhase(Phase.CONTROL);
        EmptyLSReserveDeck(scn);

        assertTrue(scn.DSCardActionAvailable(gardulla));
    }

    @Test
    public void GardullaTheHuttRetrievesWhenOpponentFailsDestinyDraw() {
        //test1: opponent's failed destiny draw resolves in Gardulla's favor (retrieve 1 Force)
        var scn = GetScenario();

        var gardulla = scn.GetDSCard("gardulla");
        var lostCard = scn.GetDSFiller(1);
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, gardulla);
        scn.MoveCardsToTopOfDSLostPile(lostCard);

        scn.SkipToPhase(Phase.CONTROL);
        EmptyLSReserveDeck(scn);
        int lsLostBefore = scn.GetLSLostPileCount();

        scn.DSUseCardAction(gardulla);
        scn.PassAllResponses();

        assertEquals(0, scn.GetDSLostPileCount()); //test1
        assertEquals(lsLostBefore, scn.GetLSLostPileCount()); //test1
        assertFalse(scn.AwaitingDSForceLossPayment()); //test1
    }

    @Test
    public void GardullaTheHuttRetrievesWhenDestinyGreaterThanTwo() {
        var scn = GetScenario();

        var gardulla = scn.GetDSCard("gardulla");
        var lostCard = scn.GetDSFiller(1);
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, gardulla);
        scn.MoveCardsToTopOfDSLostPile(lostCard);

        scn.SkipToPhase(Phase.CONTROL);
        scn.PrepareLSDestiny(3);

        scn.DSUseCardAction(gardulla);
        scn.PassAllResponses();

        assertEquals(0, scn.GetDSLostPileCount());
    }

    @Test
    public void GardullaTheHuttLosesForceWhenDestinyTwoOrLess() {
        var scn = GetScenario();

        var gardulla = scn.GetDSCard("gardulla");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, gardulla);

        scn.SkipToPhase(Phase.CONTROL);
        scn.PrepareLSDestiny(2);

        scn.DSUseCardAction(gardulla);
        scn.PassAllResponses();

        scn.DSPayForceLossFromReserveDeck();
        assertEquals(1, scn.GetDSLostPileCount());
    }
}
