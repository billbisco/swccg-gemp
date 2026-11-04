package com.gempukku.swccgo.cards.set1291.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrStartingInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.CancelDestinyAndCauseRedrawEffect;
import com.gempukku.swccgo.logic.effects.PutCardFromVoidInLostPileEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeDestinyCardIntoHandEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardsFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 9.1
 * Type: Interrupt
 * Subtype: Used Or Starting
 * Title: Everything Is Going As Planned
 */
public class Card1291_014 extends AbstractUsedOrStartingInterrupt {
    public Card1291_014() {
        super(Side.DARK, 3, "Everything Is Going As Planned", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_9_1, Rarity.V);
        setGameText("USED: If you just drew a [Separatist] card for destiny, take it into hand (may cancel and redraw it). STARTING: If Separatist Uprising on table, deploy Droid Racks and two Effects that deploy for free, are always [Immune to Alter], and deploy on table. Place Interrupt in Lost Pile.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__EVERYTHING_IS_GOING_AS_PLANNED__TAKE_DESTINY_INTO_HAND;

        // Check condition(s)
        if (TriggerConditions.isDestinyJustDrawnBy(game, effectResult, playerId)
                && GameConditions.isDestinyCardMatchTo(game, Icon.SEPARATIST)
                && GameConditions.canTakeDestinyCardIntoHand(game, playerId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId, CardSubtype.USED);
            action.setText("Take destiny card into hand");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeDestinyCardIntoHandEffect(action));
                        }
                    });
            actions.add(action);

            if (GameConditions.canCancelDestinyAndCauseRedraw(game, playerId)) {
                final PlayInterruptAction action2 = new PlayInterruptAction(game, self, GameTextActionId.ANY_CARD__CANCEL_AND_REDRAW_A_DESTINY, CardSubtype.USED);
                action2.setText("Take destiny card into hand and cancel and redraw it");
                // Allow response(s)
                action2.allowResponses(
                        new RespondablePlayCardEffect(action2) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action2.appendEffect(
                                        new TakeDestinyCardIntoHandEffect(action2));
                                action2.appendEffect(
                                        new CancelDestinyAndCauseRedrawEffect(action2));
                            }
                        });
                actions.add(action2);
            }
        }
        return actions;
    }

    @Override
    protected PlayInterruptAction getGameTextStartingAction(final String playerId, SwccgGame game, final PhysicalCard self) {
        if (!GameConditions.canSpot(game, self, Filters.or(Filters.title("Separatist Uprising"), Filters.title("At War With Itself")))) {
            return null;
        }
        final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.STARTING);
        action.setText("Deploy cards from Reserve Deck");
        // Allow response(s)
        action.allowResponses("Deploy Droid Racks and two Effects that deploy for free, are always Immune to Alter, and deploy on table",
                new RespondablePlayCardEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        // Perform result(s)
                        action.appendEffect(
                                new DeployCardFromReserveDeckEffect(action, Filters.Droid_Racks, true, false));
                        action.appendEffect(
                                new DeployCardsFromReserveDeckEffect(action, Filters.and(Filters.Effect, Filters.deploysForFree,
                                        Filters.always_immune_to_Alter, Filters.gameTextContains("deploy on table")), 2, 2, true, false));
                        action.appendEffect(
                                new PutCardFromVoidInLostPileEffect(action, playerId, self));
                    }
                });
        return action;
    }
}
