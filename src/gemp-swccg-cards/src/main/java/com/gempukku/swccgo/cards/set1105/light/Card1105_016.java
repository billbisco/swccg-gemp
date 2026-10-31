package com.gempukku.swccgo.cards.set1105.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.StackCardFromVoidEffect;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PutStackedCardInLostPileEffect;
import com.gempukku.swccgo.logic.effects.PutStackedCardInUsedPileEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayingCardEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardEffect;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 5: A Dark Time
 * Type: Effect
 * Title: Polarized Negative Power Coupling
 */
public class Card1105_016 extends AbstractNormalEffect {
    public Card1105_016() {
        super(Side.LIGHT, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Polarized_Negative_Power_Coupling, Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_ORIGINAL_SET_5, Rarity.V);
        setVirtualSuffix(true);
        setLore("Imbalance in a converter's inverse energy field can improperly polarize the negative axis, causing hyperdrive failure. Replacement often requires an inverted engineer.");
        setGameText("Deploy on table. Whenever a player plays a Used Interrupt, their opponent may stack it here. Either player may use 2 Force to place a card here in owner's Used Pile or may lose 2 Force to place a card here in owner's Lost Pile.");
        addIcons(Icon.DAGOBAH, Icon.GRABBER);
    }

    private boolean isPlayingUsedInterrupt(Effect effect) {
        if (effect.getType() != Effect.Type.PLAYING_CARD_EFFECT || !(effect.getAction() instanceof PlayInterruptAction)) {
            return false;
        }
        return ((PlayInterruptAction) effect.getAction()).getPlayedAsSubtype() == CardSubtype.USED;
    }

    private OptionalGameTextTriggerAction grabUsedInterrupt(String grabbingPlayer, SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        PhysicalCard cardBeingPlayed = ((RespondablePlayingCardEffect) effect).getCard();
        if (!GameConditions.canBeGrabbed(game, self, cardBeingPlayed)) {
            return null;
        }
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__POLARIZED_NEGATIVE_POWER_COUPLING_V__STACK_USED_INTERRUPT;
        OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, grabbingPlayer, gameTextSourceCardId, gameTextActionId);
        action.setText("'Grab' " + GameUtils.getFullName(cardBeingPlayed));
        action.setActionMsg("'Grab' " + GameUtils.getCardLink(cardBeingPlayed));
        action.appendEffect(
                new StackCardFromVoidEffect(action, cardBeingPlayed, self));
        return action;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalBeforeTriggers(final String playerId, SwccgGame game, Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);
        if (isPlayingUsedInterrupt(effect) && TriggerConditions.isPlayingCard(game, effect, opponent, Filters.Interrupt)) {
            OptionalGameTextTriggerAction action = grabUsedInterrupt(playerId, game, effect, self, gameTextSourceCardId);
            if (action != null) {
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getOpponentsCardGameTextOptionalBeforeTriggers(final String playerId, SwccgGame game, Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        String owner = self.getOwner();
        if (isPlayingUsedInterrupt(effect) && TriggerConditions.isPlayingCard(game, effect, owner, Filters.Interrupt)) {
            OptionalGameTextTriggerAction action = grabUsedInterrupt(playerId, game, effect, self, gameTextSourceCardId);
            if (action != null) {
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    private List<TopLevelGameTextAction> placeStackedCards(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();
        if (!GameConditions.hasStackedCards(game, self)) {
            return actions;
        }

        GameTextActionId usedId = GameTextActionId.LEGACY__POLARIZED_NEGATIVE_POWER_COUPLING_V__PLACE_IN_USED_PILE;
        if (GameConditions.canUseForce(game, playerId, 2)) {
            final TopLevelGameTextAction usedAction = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, usedId);
            usedAction.setText("Place a card here in Used Pile");
            usedAction.appendCost(
                    new UseForceEffect(usedAction, playerId, 2));
            usedAction.appendEffect(
                    new ChooseStackedCardEffect(usedAction, playerId, self) {
                        @Override
                        protected void cardSelected(PhysicalCard selectedCard) {
                            usedAction.appendEffect(
                                    new PutStackedCardInUsedPileEffect(usedAction, selectedCard.getOwner(), selectedCard, false));
                        }
                    });
            actions.add(usedAction);
        }

        GameTextActionId lostId = GameTextActionId.LEGACY__POLARIZED_NEGATIVE_POWER_COUPLING_V__PLACE_IN_LOST_PILE;
        final TopLevelGameTextAction lostAction = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, lostId);
        lostAction.setText("Place a card here in Lost Pile");
        lostAction.appendCost(
                new LoseForceEffect(lostAction, playerId, 2));
        lostAction.appendEffect(
                new ChooseStackedCardEffect(lostAction, playerId, self) {
                    @Override
                    protected void cardSelected(PhysicalCard selectedCard) {
                        lostAction.appendEffect(
                                new PutStackedCardInLostPileEffect(lostAction, selectedCard.getOwner(), selectedCard, false));
                    }
                });
        actions.add(lostAction);
        return actions;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        return placeStackedCards(playerId, game, self, gameTextSourceCardId);
    }

    @Override
    protected List<TopLevelGameTextAction> getOpponentsCardGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        return placeStackedCards(playerId, game, self, gameTextSourceCardId);
    }
}
