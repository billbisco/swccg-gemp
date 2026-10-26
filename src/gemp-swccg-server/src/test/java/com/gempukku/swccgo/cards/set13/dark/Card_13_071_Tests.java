package com.gempukku.swccgo.cards.set13.dark;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_13_071_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
            new HashMap<>() {{
            }},
            new HashMap<>() {{
                put("jabba", "13_71");
                put("bib", "13_55");
                put("arena", "11_94");
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

    private static void EmptyReserveDeck(VirtualTableScenario scn, boolean darkSide) {
        if (darkSide) {
            while (scn.GetDSReserveDeckCount() > 0)
                scn.MoveCardsToTopOfDSUsedPile(scn.GetTopOfDSReserveDeck());
        }
        else {
            while (scn.GetLSReserveDeckCount() > 0)
                scn.MoveCardsToTopOfLSUsedPile(scn.GetTopOfLSReserveDeck());
        }
    }

    private static VirtualTableScenario StartAtPodraceArenaControlPhase() {
        var scn = new Card_13_071_Tests().GetScenario();
        var jabba = scn.GetDSCard("jabba");
        var arena = scn.GetDSCard("arena");

        scn.StartGame();
        scn.MoveLocationToTable(arena);
        scn.MoveCardsToLocation(arena, jabba);
        scn.SkipToPhase(Phase.CONTROL);
        return scn;
    }

    @Test
    public void JabbaDesilijicTiureWagerYouLoseWhenYouFailDestinyDraw() {
        //test1: your failed draw resolves in opponent's favor, so you lose 1 Force
        //test2: opponent does not lose Force
        var scn = StartAtPodraceArenaControlPhase();
        var jabba = scn.GetDSCard("jabba");

        EmptyReserveDeck(scn, true);
        scn.PrepareLSDestiny(1);

        assertTrue(scn.DSCardActionAvailable(jabba, "Wager"));
        scn.DSUseCardAction(jabba, "Wager");
        scn.PassAllResponses();

        assertTrue(scn.AwaitingDSForceLossPayment()); //test1
        assertFalse(scn.AwaitingLSForceLossPayment()); //test2
        scn.DSPayForceLossFromForcePile();
        assertEquals(1, scn.GetDSLostPileCount()); //test1
        assertEquals(0, scn.GetLSLostPileCount()); //test2
    }

    @Test
    public void JabbaDesilijicTiureWagerOpponentLosesWhenOpponentFailsDestinyDraw() {
        //test1: opponent's failed draw resolves in your favor, so opponent loses 1 Force
        //test2: you do not lose Force
        var scn = StartAtPodraceArenaControlPhase();
        var jabba = scn.GetDSCard("jabba");

        EmptyReserveDeck(scn, false);
        scn.PrepareDSDestiny(1);

        scn.DSUseCardAction(jabba, "Wager");
        scn.PassAllResponses();

        assertTrue(scn.AwaitingLSForceLossPayment()); //test1
        assertFalse(scn.AwaitingDSForceLossPayment()); //test2
        scn.LSPayForceLossFromUsedPile();
        assertEquals(1, scn.GetLSLostPileCount()); //test1
        assertEquals(0, scn.GetDSLostPileCount()); //test2
    }

    @Test
    public void JabbaDesilijicTiureWagerLowerTotalLoses() {
        var scn = StartAtPodraceArenaControlPhase();
        var jabba = scn.GetDSCard("jabba");

        scn.PrepareDSDestiny(2);
        scn.PrepareLSDestiny(5);

        scn.DSUseCardAction(jabba, "Wager");
        scn.PassAllResponses();

        assertTrue(scn.AwaitingDSForceLossPayment());
        assertFalse(scn.AwaitingLSForceLossPayment());
    }

    @Test
    public void JabbaDesilijicTiureWagerWonOnOpponentsFailedDrawRetrievesWithBibFortuna() {
        //test1: opponent loses 1 Force
        //test2: you won the wager with Bib Fortuna present, so you retrieve 1 Force
        var scn = StartAtPodraceArenaControlPhase();
        var jabba = scn.GetDSCard("jabba");
        var bib = scn.GetDSCard("bib");
        var arena = scn.GetDSCard("arena");
        var lostCard = scn.GetDSFiller(1);

        scn.MoveCardsToLocation(arena, bib);
        scn.MoveCardsToTopOfDSLostPile(lostCard);
        EmptyReserveDeck(scn, false);
        scn.PrepareDSDestiny(1);

        scn.DSUseCardAction(jabba, "Wager");
        scn.PassAllResponses();

        scn.LSPayForceLossFromUsedPile();
        scn.PassAllResponses();
        assertEquals(1, scn.GetLSLostPileCount()); //test1
        assertEquals(0, scn.GetDSLostPileCount()); //test2
    }
}
