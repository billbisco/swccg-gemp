package com.gempukku.swccgo.cards.legacy;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.conditions.FalseCondition;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.LegacyMayPlayInterruptAsModifier;
import org.junit.Test;

import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInHand;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Engine tests for LegacyMayPlayInterruptAsModifier, the hook behind "Interrupts ... may be played as follows: 'USED: ...'".
 * A stand-in grant lets Look Sir, Droids (a Lost Interrupt) be played as "USED: Use 1 Force to take Shocking Revelation
 * into hand from Reserve Deck; reshuffle."
 */
public class LegacyMayPlayInterruptAsTests {
    private static final String GRANTED_TEXT = "Take Shocking Revelation into hand from Reserve Deck";

    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("plans", "7_240");
                    put("looksir", "1_259");
                    put("shocking", "5_153");
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    /**
     * The stand-in granted play: "USED: Use 1 Force to take Shocking Revelation into hand from Reserve Deck; reshuffle."
     */
    private static class TakeShockingRevelation extends LegacyMayPlayInterruptAsModifier.PlayMode {
        @Override
        public PlayInterruptAction createAction(String playerId, SwccgGame game, PhysicalCard source, PhysicalCard interrupt) {
            if (!GameConditions.canUseForce(game, playerId, 1)
                    || !GameConditions.hasReserveDeck(game, playerId)) {
                return null;
            }
            final PlayInterruptAction action = new PlayInterruptAction(game, interrupt);
            action.setText("USED: " + GRANTED_TEXT);
            action.appendCost(new UseForceEffect(action, playerId, 1));
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.Shocking_Revelation, true));
                        }
                    });
            return action;
        }
    }

    @Test
    public void LegacyMayPlayInterruptAsPlaysLostInterruptAsGrantedUsedText() {
        //test1: the granted play is offered for the affected Interrupt
        //test2: playing it uses 1 Force and takes the card into hand
        //test3: the Interrupt was played as USED, so it goes to Used Pile, not Lost Pile
        var scn = GetScenario();
        var plans = scn.GetDSCard("plans");
        var looksir = scn.GetDSCard("looksir");
        var shocking = scn.GetDSCard("shocking");

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(plans);
        scn.ApplyAdHocModifier(new LegacyMayPlayInterruptAsModifier(plans, Filters.Look_Sir_Droids, CardSubtype.USED, new TakeShockingRevelation()));
        scn.MoveCardsToHand(looksir);
        scn.SkipToPhase(Phase.DEPLOY);
        scn.MoveCardsToTopOfDSReserveDeck(shocking);
        int forcePile = scn.GetDSForcePileCount();

        assertTrue(scn.DSCardPlayAvailable(looksir, GRANTED_TEXT)); //test1
        scn.DSPlayCard(looksir, GRANTED_TEXT);
        scn.PassAllResponses();
        scn.DSChooseCard(shocking);
        scn.PassAllResponses();

        assertInHand(shocking); //test2
        assertEquals(forcePile - 1, scn.GetDSForcePileCount()); //test2
        assertTrue(looksir.getZone() == Zone.USED_PILE || looksir.getZone() == Zone.TOP_OF_USED_PILE); //test3
    }

    @Test
    public void LegacyMayPlayInterruptAsDoesNotGrantPlayToOtherInterrupts() {
        //test1: an Interrupt the filter does not accept keeps only its own plays
        var scn = GetScenario();
        var plans = scn.GetDSCard("plans");
        var looksir = scn.GetDSCard("looksir");

        scn.StartGame();
        scn.MoveCardsToTopOfLSUsedPile(scn.GetLSCard("luke"));
        scn.MoveCardsToDSSideOfTable(plans);
        scn.ApplyAdHocModifier(new LegacyMayPlayInterruptAsModifier(plans, Filters.Imperial_Barrier, CardSubtype.USED, new TakeShockingRevelation()));
        scn.MoveCardsToHand(looksir);
        scn.SkipToPhase(Phase.DEPLOY);

        assertTrue(scn.DSCardPlayAvailable(looksir, "Search opponent's Used Pile")); //test1
        assertFalse(scn.DSCardPlayAvailable(looksir, GRANTED_TEXT)); //test1
    }

    @Test
    public void LegacyMayPlayInterruptAsIsNotOfferedWhileConditionIsNotFulfilled() {
        //test1: the grant follows the modifier's condition
        var scn = GetScenario();
        var plans = scn.GetDSCard("plans");
        var looksir = scn.GetDSCard("looksir");

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(plans);
        scn.ApplyAdHocModifier(new LegacyMayPlayInterruptAsModifier(plans, new FalseCondition(), Filters.Look_Sir_Droids, CardSubtype.USED, new TakeShockingRevelation()));
        scn.MoveCardsToHand(looksir);
        scn.SkipToPhase(Phase.DEPLOY);

        assertFalse(scn.DSCardPlayAvailable(looksir, GRANTED_TEXT)); //test1
    }

    @Test
    public void LegacyMayPlayInterruptAsIsNotOfferedWithoutTheModifier() {
        //test1: without the grant, Look Sir, Droids has no such play
        var scn = GetScenario();
        var looksir = scn.GetDSCard("looksir");

        scn.StartGame();
        scn.MoveCardsToHand(looksir);
        scn.SkipToPhase(Phase.DEPLOY);

        assertFalse(scn.DSCardPlayAvailable(looksir, GRANTED_TEXT)); //test1
    }
}
