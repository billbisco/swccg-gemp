package com.gempukku.swccgo.cards.set1262.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.BattleState;
import com.gempukku.swccgo.game.state.AttackState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromForcePileEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.DestinyDrawnResult;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Legacy Block 6.2: Why Didn't You Tell Me? (V). */
public class Card1262_014 extends AbstractUsedInterrupt {
    public Card1262_014() {
        super(Side.DARK, 4, Title.Why_Didnt_You_Tell_Me, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("Lacking foresight, Luke suffered the consequences of rushing into battle unprepared.");
        setGameText("During any draw phase, place a card from hand under your Used Pile to take any one card into hand from Force Pile; reshuffle. OR Place IL-19 in owner's Used Pile. OR During the power segment, cancel a just drawn destiny (except battle destiny).");
        addIcons(Icon.CLOUD_CITY, Icon.LEGACY_BLOCK_6);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new ArrayList<>();
        GameTextActionId takeId = GameTextActionId.LEGACY__WHY_DIDNT_YOU_TELL_ME_V__TAKE_FROM_FORCE_PILE;
        if (GameConditions.isEitherPlayersPhase(game, Phase.DRAW)
                && GameConditions.hasInHand(game, playerId, Filters.not(self))
                && GameConditions.canTakeCardsIntoHandFromForcePile(game, playerId, self, takeId)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, takeId);
            action.setText("Place card under Used Pile to take from Force Pile");
            action.appendCost(new PutCardFromHandOnBottomOfUsedPileEffect(action, playerId, Filters.not(self), true));
            action.allowResponses(new RespondablePlayCardEffect(action) {
                @Override
                protected void performActionResults(Action targetingAction) {
                    action.appendEffect(new TakeCardIntoHandFromForcePileEffect(action, playerId, true));
                }
            });
            actions.add(action);
        }
        if (GameConditions.canTarget(game, self, Filters.title("IL-19"))) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Place IL-19 in owner's Used Pile");
            action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Choose IL-19", Filters.title("IL-19")) {
                @Override
                protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                    action.addAnimationGroup(targetedCard);
                    action.allowResponses(new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(new PlaceCardInUsedPileFromTableEffect(action, targetingAction.getPrimaryTargetCard(targetGroupId)));
                        }
                    });
                }
            });
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self) {
        BattleState battle = game.getGameState().getBattleState();
        AttackState attack = game.getGameState().getAttackState();
        boolean powerSegment = (battle != null && battle.isReachedPowerSegment() && !battle.isReachedDamageSegment())
                || (attack != null && attack.isReachedPowerSegment() && !attack.isReachedDamageSegment());
        if (powerSegment
                && TriggerConditions.isDestinyJustDrawn(game, effectResult)
                && ((DestinyDrawnResult) effectResult).getDestinyType() != DestinyType.BATTLE_DESTINY
                && GameConditions.canCancelDestiny(game, playerId)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Cancel just drawn destiny");
            action.allowResponses(new RespondablePlayCardEffect(action) {
                @Override
                protected void performActionResults(Action targetingAction) {
                    action.appendEffect(new CancelDestinyEffect(action));
                }
            });
            return Collections.singletonList(action);
        }
        return null;
    }
}
