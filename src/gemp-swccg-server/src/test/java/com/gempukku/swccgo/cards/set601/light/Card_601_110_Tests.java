package com.gempukku.swccgo.cards.set601.light;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

/** Imperial Atrocity (V): "Whenever you lose Force during opponent's control phase (except from a Force drain at a battleground or
 * your card), that loss is cumulatively -1 (to a minimum of 1)." (the clause was commented out before). */
public class Card_601_110_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("atrocity", "601_110");
                }},
                new HashMap<>() {{
                }},
                20, 20,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    /** With the Effect on the owner's side of table, a card makes the owner lose {amount} Force; returns the cards lost. */
    private int forceLost(boolean opponentsControlPhase, boolean fromOwnCard, int amount) {
        var scn = GetScenario();
        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(scn.GetLSCard("atrocity"));
        if (opponentsControlPhase) scn.SkipToDSTurn(Phase.CONTROL); else scn.SkipToLSTurn(Phase.CONTROL);
        String actingPlayer = opponentsControlPhase ? scn.DS : scn.LS;
        PhysicalCardImpl source = fromOwnCard ? scn.GetLSStartingLocation() : scn.GetDSStartingLocation();
        int before = scn.GetLSLostPileCount();
        var action = new TopLevelGameTextAction(source, actingPlayer, source.getCardId());
        action.appendEffect(new LoseForceEffect(action, scn.LS, amount));
        scn.carryOutEffectInPhaseActionByPlayer(actingPlayer, action);
        scn.PassAllResponses();
        scn.LSPayRemainingForceLossFromReserveDeck();
        scn.PassAllResponses();
        return scn.GetLSLostPileCount() - before;
    }

    @Test
    public void ImperialAtrocityReducesForceLossFromOpponentsCardDuringOpponentsControlPhaseByOne() {
        assertEquals(2, forceLost(true, false, 3));
    }

    @Test
    public void ImperialAtrocityDoesNotReduceForceLossBelowOne() {
        assertEquals(1, forceLost(true, false, 1));
    }

    @Test
    public void ImperialAtrocityDoesNotReduceForceLossFromYourOwnCard() {
        assertEquals(3, forceLost(true, true, 3));
    }

    @Test
    public void ImperialAtrocityDoesNotReduceForceLossDuringYourOwnControlPhase() {
        assertEquals(3, forceLost(false, false, 3));
    }
}
