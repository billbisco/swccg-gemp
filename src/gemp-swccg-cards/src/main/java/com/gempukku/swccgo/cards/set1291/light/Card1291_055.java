package com.gempukku.swccgo.cards.set1291.light;

import com.gempukku.swccgo.cards.AbstractUsedOrStartingInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
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
 * Title: The Dark Side Is Growing
 */
public class Card1291_055 extends AbstractUsedOrStartingInterrupt {
    public Card1291_055() {
        super(Side.LIGHT, 3, "The Dark Side Is Growing", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_9_1, Rarity.V);
        setLore("");
        setGameText("USED: If you just drew a [Clone Army] card for destiny, take it into hand (may cancel and redraw it). STARTING: If Republic At War on table, deploy Cloning Cylinders and two Effects that deploy for free, are always [Immune to Alter], and deploy on table. Place Interrupt in Lost Pile.");
        addIcons(Icon.EPISODE_I);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__THE_DARK_SIDE_IS_GROWING__TAKE_DESTINY_INTO_HAND;

        // Check condition(s)
        if (TriggerConditions.isDestinyJustDrawnBy(game, effectResult, playerId)
                && GameConditions.isDestinyCardMatchTo(game, Icon.CLONE_ARMY)
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
        if (!GameConditions.canSpot(game, self, Filters.or(Filters.title(Card1291_069.TITLE), Filters.title(Card1291_069_BACK.TITLE)))) {
            return null;
        }
        final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.STARTING);
        action.setText("Deploy cards from Reserve Deck");
        // Allow response(s)
        action.allowResponses("Deploy Cloning Cylinders and two Effects that deploy for free, are always Immune to Alter, and deploy on table",
                new RespondablePlayCardEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        // Perform result(s)
                        action.appendEffect(
                                new DeployCardFromReserveDeckEffect(action, Filters.title(Title.Cloning_Cylinders), true, false));
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
