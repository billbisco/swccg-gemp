package com.gempukku.swccgo.cards.set1108.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.CaptureCharacterOnTableEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.RestoreCardToNormalEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardIntoHandEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.UsedInterruptModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.AboutToForfeitCardFromTableResult;
import com.gempukku.swccgo.logic.timing.results.DeliveredCaptiveToPrisonResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 8
 * Type: Effect
 * Title: All Wrapped Up
 */
public class Card1108_026 extends AbstractNormalEffect {
    public Card1108_026() {
        super(Side.DARK, 2, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.All_Wrapped_Up, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_8, Rarity.V);
        setVirtualSuffix(true);
        setLore("A capture cable is a quick and effective way for bounty hunters to suddenly snare their target.");
        setGameText("Deploy on table. Double Back is a Used Interrupt. Once per turn, may take Bounty into hand from Reserve Deck; reshuffle. If present, your bounty hunter may capture a character opponent just forfeited at same site. Whenever you collect a bounty, may place that Effect out of play to take a retrieved card into hand. (Immune to Alter.)");
        addIcons(Icon.JABBAS_PALACE);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new UsedInterruptModifier(self, Filters.Double_Back));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__ALL_WRAPPED_UP_V__TAKE_BOUNTY;
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Take Bounty into hand from Reserve Deck");
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.Bounty, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<OptionalGameTextTriggerAction>();
        String opponent = game.getOpponent(playerId);
        TargetingReason targetingReason = TargetingReason.TO_BE_CAPTURED;
        Filter opponentsCharacterFilter = Filters.and(Filters.opponents(self), Filters.character, Filters.not(Filters.captive),
                Filters.canBeTargetedBy(self, targetingReason),
                Filters.at(Filters.wherePresent(self, Filters.and(Filters.your(self), Filters.bounty_hunter))));

        if (TriggerConditions.isAboutToBeForfeited(game, effectResult, opponentsCharacterFilter)
                && opponent.equals(effectResult.getPerformingPlayerId())) {
            final AboutToForfeitCardFromTableResult result = (AboutToForfeitCardFromTableResult) effectResult;
            final PhysicalCard cardToBeForfeited = result.getCardToBeForfeited();
            final OptionalGameTextTriggerAction capture = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            capture.setText("Capture " + GameUtils.getFullName(cardToBeForfeited));
            capture.appendTargeting(
                    new TargetCardOnTableEffect(capture, playerId, "Choose character", targetingReason, cardToBeForfeited) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            capture.addAnimationGroup(targetedCard);
                            capture.allowResponses("Capture " + GameUtils.getCardLink(targetedCard),
                                    new RespondableEffect(capture) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            PhysicalCard finalTarget = capture.getPrimaryTargetCard(targetGroupId);
                                            result.getForfeitCardEffect().preventEffectOnCard(finalTarget);
                                            capture.appendEffect(
                                                    new RestoreCardToNormalEffect(capture, finalTarget));
                                            capture.appendEffect(
                                                    new CaptureCharacterOnTableEffect(capture, finalTarget));
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(capture);
        }

        if (effectResult instanceof DeliveredCaptiveToPrisonResult
                && playerId.equals(effectResult.getPerformingPlayerId())) {
            final OptionalGameTextTriggerAction collect = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            collect.setText("Place out of play to take retrieved card into hand");
            collect.appendCost(
                    new PlaceCardOutOfPlayFromTableEffect(collect, self));
            collect.appendEffect(
                    new RetrieveCardIntoHandEffect(collect, playerId));
            actions.add(collect);
        }

        return actions.isEmpty() ? null : actions;
    }
}
