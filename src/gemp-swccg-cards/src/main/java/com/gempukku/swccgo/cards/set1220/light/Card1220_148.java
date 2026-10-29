package com.gempukku.swccgo.cards.set1220.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelForceDrainEffect;
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
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromForcePileEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Levitation
 */
public class Card1220_148 extends AbstractUsedOrLostInterrupt {
    public Card1220_148() {
        super(Side.LIGHT, 6, Title.Levitation, Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("Telekinesis is one of the powers awakened during a Jedi's apprenticeship. Using this ability, a student of the Force can learn to levitate objects.");
        setGameText("USED: Take a droid or device from your Force Pile into hand; reshuffle. LOST: Once per game, if your apprentice is on Dagobah, cancel a Force drain at a non-Dagobah location.");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        GameTextActionId takeId = GameTextActionId.LEGACY__LEVITATION_V__TAKE_FROM_FORCE_PILE;
        if (GameConditions.canTakeCardsIntoHandFromForcePile(game, playerId, self, takeId)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, takeId, CardSubtype.USED);
            action.setText("Take a droid or device into hand from Force Pile");
            action.allowResponses("Take a droid or device from Force Pile into hand",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new TakeCardIntoHandFromForcePileEffect(action, playerId, Filters.or(Filters.droid, Filters.device), true));
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        GameTextActionId cancelId = GameTextActionId.LEGACY__LEVITATION_V__CANCEL_FORCE_DRAIN;
        if (TriggerConditions.forceDrainInitiatedBy(game, effectResult, game.getOpponent(playerId), Filters.not(Filters.Dagobah_location))
                && GameConditions.isOncePerGame(game, self, cancelId)
                && GameConditions.canSpot(game, self, Filters.and(Filters.your(self), Filters.apprentice, Filters.on(Title.Dagobah)))
                && GameConditions.canCancelForceDrain(game, self)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, cancelId, CardSubtype.LOST);
            action.setText("Cancel Force drain");
            action.appendUsage(
                    new com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect(action));
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new CancelForceDrainEffect(action));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
