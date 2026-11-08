package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

/** Imperial Propaganda (V): "Whenever you lose Force during opponent's control phase (except from a Force drain at a battleground or
 * your card), that loss is cumulatively -1 (to a minimum of 1)." (the clause was commented out before). */
public class Card_601_088_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                }},
                new HashMap<>() {{
                    put("propaganda", "601_088");
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
        scn.MoveCardsToDSSideOfTable(scn.GetDSCard("propaganda"));
        if (opponentsControlPhase) scn.SkipToLSTurn(Phase.CONTROL); else scn.SkipToDSTurn(Phase.CONTROL);
        String actingPlayer = opponentsControlPhase ? scn.LS : scn.DS;
        PhysicalCardImpl source = fromOwnCard ? scn.GetDSStartingLocation() : scn.GetLSStartingLocation();
        int before = scn.GetDSLostPileCount();
        var action = new TopLevelGameTextAction(source, actingPlayer, source.getCardId());
        action.appendEffect(new LoseForceEffect(action, scn.DS, amount));
        scn.carryOutEffectInPhaseActionByPlayer(actingPlayer, action);
        scn.PassAllResponses();
        scn.DSPayRemainingForceLossFromReserveDeck();
        scn.PassAllResponses();
        return scn.GetDSLostPileCount() - before;
    }

    @Test
    public void ImperialPropagandaReducesForceLossFromOpponentsCardDuringOpponentsControlPhaseByOne() {
        assertEquals(2, forceLost(true, false, 3));
    }

    @Test
    public void ImperialPropagandaDoesNotReduceForceLossBelowOne() {
        assertEquals(1, forceLost(true, false, 1));
    }

    @Test
    public void ImperialPropagandaDoesNotReduceForceLossFromYourOwnCard() {
        assertEquals(3, forceLost(true, true, 3));
    }

    @Test
    public void ImperialPropagandaDoesNotReduceForceLossDuringYourOwnControlPhase() {
        assertEquals(3, forceLost(false, false, 3));
    }
}
