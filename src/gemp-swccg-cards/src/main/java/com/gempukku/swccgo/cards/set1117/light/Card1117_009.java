package com.gempukku.swccgo.cards.set1117.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelDuelEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.PutCardFromHandOnBottomOfUsedPileEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromForcePileEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 17: The Force Unleashed
 * Type: Interrupt
 * Subtype: Used
 * Title: NOOOOOOOOOOOO!
 */
public class Card1117_009 extends AbstractUsedInterrupt {
    public Card1117_009() {
        super(Side.LIGHT, 5, Title.NOOOOOOOOOOOO, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_17, Rarity.V);
        setVirtualSuffix(true);
        setLore("Sometimes, the truth isn't very pretty.");
        setGameText("Cancel Broken Concentration, The Circle Is Now Complete, or a duel initiated during a battle. (Immune to Young Fool.) OR During any draw phase, place a card from hand under your Used Pile to take any one card into hand from Force Pile; reshuffle.");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        if (TriggerConditions.isPlayingCard(game, effect, Filters.or(Filters.Broken_Concentration, Filters.The_Circle_Is_Now_Complete))
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {
            PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setImmuneTo(Title.Young_Fool);
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        if (TriggerConditions.duelInitiated(game, effectResult)
                && GameConditions.isDuringBattle(game)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Cancel duel");
            action.setImmuneTo(Title.Young_Fool);
            action.allowResponses("Cancel a duel initiated during a battle",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new CancelDuelEffect(action));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        if (GameConditions.canTargetToCancel(game, self, Filters.Broken_Concentration)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setImmuneTo(Title.Young_Fool);
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Broken_Concentration, Title.Broken_Concentration);
            actions.add(action);
        }
        if (GameConditions.canTargetToCancel(game, self, Filters.The_Circle_Is_Now_Complete)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setImmuneTo(Title.Young_Fool);
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.The_Circle_Is_Now_Complete, Title.The_Circle_Is_Now_Complete);
            actions.add(action);
        }

        GameTextActionId takeId = GameTextActionId.LEGACY__NOOOOO_V__TAKE_FROM_FORCE_PILE;
        if (GameConditions.isEitherPlayersPhase(game, Phase.DRAW)
                && GameConditions.hasInHand(game, playerId, Filters.not(self))
                && GameConditions.canTakeCardsIntoHandFromForcePile(game, playerId, self, takeId)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, takeId);
            action.setText("Place card under Used Pile to take from Force Pile");
            action.appendCost(
                    new PutCardFromHandOnBottomOfUsedPileEffect(action, playerId, Filters.not(self), true));
            action.allowResponses("Take a card from Force Pile into hand",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new TakeCardIntoHandFromForcePileEffect(action, playerId, true));
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }
}
