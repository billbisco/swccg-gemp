package com.gempukku.swccgo.cards.set1220.dark;

import com.gempukku.swccgo.cards.AbstractImmediateEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.SetTargetedCardEffect;
import com.gempukku.swccgo.cards.effects.StackCardFromVoidEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetId;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromLostPileEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromLostPileEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.DestinyDrawnResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Effect
 * Subtype: Immediate
 * Title: Awwww, Cannot Get Your Ship Out
 */
public class Card1220_026 extends AbstractImmediateEffect {
    public Card1220_026() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Awwww_Cannot_Get_Your_Ship_Out, Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("'Listen, friend, we didn't mean to land in that puddle, and if we could get our ship out, we would, but we can't so why don't you just...'");
        setGameText("If opponent just drew a starship or vehicle for destiny, deploy on table, and stack that card here. Opponent may place a card (your choice) from your Lost Pile into your hand to place this Effect (and stacked cards) into owner's Used Pile.");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected List<PlayCardAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);
        if (TriggerConditions.isDestinyJustDrawnBy(game, effectResult, opponent)) {
            PhysicalCard drawn = ((DestinyDrawnResult) effectResult).getCard();
            if (drawn != null && Filters.or(Filters.starship, Filters.vehicle).accepts(game, drawn)) {
                PlayCardAction action = getPlayCardAction(playerId, game, self, self, false, 0, null, null, null, null, null, false, 0, Filters.none, null);
                if (action != null) {
                    action.setText("Deploy to stack " + GameUtils.getFullName(drawn));
                    action.appendTargeting(
                            new SetTargetedCardEffect(action, self, TargetId.IMMEDIATE_EFFECT_TARGET_1, null, drawn, Filters.samePermanentCardId(drawn)));
                    return Collections.singletonList(action);
                }
            }
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.justDeployed(game, effectResult, self)) {
            PhysicalCard toStack = self.getTargetedCard(game.getGameState(), TargetId.IMMEDIATE_EFFECT_TARGET_1);
            if (toStack != null) {
                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Stack " + GameUtils.getFullName(toStack));
                action.appendEffect(
                        new StackCardFromVoidEffect(action, toStack, self));
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getOpponentsCardGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final String owner = self.getOwner();
        if (GameConditions.numCardsInLostPile(game, owner) >= 1) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId);
            action.setText("Place stacked cards in Used Pile");
            action.setActionMsg("Place a card from " + owner + "'s Lost Pile into hand to place " + GameUtils.getCardLink(self) + " in Used Pile");
            action.appendCost(
                    new ChooseCardFromLostPileEffect(action, owner) {
                        @Override
                        protected void cardSelected(SwccgGame game, PhysicalCard selectedCard) {
                            action.appendEffect(
                                    new TakeCardIntoHandFromLostPileEffect(action, owner, selectedCard, false, false));
                            action.appendEffect(
                                    new PlaceCardInUsedPileFromTableEffect(action, self));
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
