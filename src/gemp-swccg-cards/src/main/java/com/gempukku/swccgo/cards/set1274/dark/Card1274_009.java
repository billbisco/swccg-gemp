package com.gempukku.swccgo.cards.set1274.dark;

import com.gempukku.swccgo.cards.AbstractImmediateEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.LegacyStackCardFaceDownFromHandOrVoidEffect;
import com.gempukku.swccgo.common.ExpansionSet;
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
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.PutStackedCardInUsedPileEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardEffect;
import com.gempukku.swccgo.logic.effects.choose.StackOneCardFromLostPileEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.FailCostEffect;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToRemoveJustLostCardFromLostPileResult;
import com.gempukku.swccgo.logic.timing.results.DestinyDrawnResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Block 7.4
 * Type: Effect
 * Subtype: Immediate
 * Title: Where Are You Taking This ... Thing?
 */
public class Card1274_009 extends AbstractImmediateEffect {
    public Card1274_009() {
        super(Side.DARK, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Where Are You Taking This ... Thing?", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_4, Rarity.V);
        setGameText("If opponent just drew destiny for Nabrun Leids (or removed a just-lost character from Lost Pile), deploy on table and stack that card here face down. Elis Helrot and Nabrun Leids are canceled. Opponent may use 2 Force to place card stacked here in Used Pile. (Immune to Control.)");
        addImmuneToCardTitle(Title.Control);
    }

    @Override
    protected List<PlayCardAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);

        // Opponent just drew destiny for Nabrun Leids: stack Nabrun Leids here face down (its 'transport' does not happen)
        if (TriggerConditions.isDestinyJustDrawnFor(game, effectResult, opponent, Filters.Nabrun_Leids)) {
            final Action nabrunAction = ((DestinyDrawnResult) effectResult).getSourceAction();
            final PhysicalCard nabrun = nabrunAction.getActionSource();
            final PlayCardAction action = getPlayCardAction(playerId, game, self, self, false, 0, null, null, null, null, null, false, 0, Filters.none, null);
            if (action != null) {
                action.setText("Deploy to stack " + GameUtils.getFullName(nabrun) + " here face down");
                action.appendAfterEffect(
                        new PassthruEffect(action) {
                            @Override
                            protected void doPlayEffect(SwccgGame game) {
                                if (self.getZone().isInPlay()) {
                                    // Nabrun Leids is stopped before it resolves
                                    nabrunAction.appendCost(new FailCostEffect(nabrunAction));
                                    action.appendAfterEffect(
                                            new LegacyStackCardFaceDownFromHandOrVoidEffect(action, nabrun, self));
                                }
                            }
                        });
                return Collections.singletonList(action);
            }
        }

        // Opponent is removing a just-lost character from Lost Pile: stack that character here face down instead
        if (TriggerConditions.isAboutToRemoveJustLostCardFromLostPile(game, effectResult, opponent, Filters.character)) {
            final AboutToRemoveJustLostCardFromLostPileResult result = (AboutToRemoveJustLostCardFromLostPileResult) effectResult;
            final PhysicalCard character = result.getCardToRemoveFromLostPile();
            final PlayCardAction action = getPlayCardAction(playerId, game, self, self, false, 0, null, null, null, null, null, false, 0, Filters.none, null);
            if (action != null) {
                action.setText("Deploy to stack " + GameUtils.getFullName(character) + " here face down");
                action.appendAfterEffect(
                        new PassthruEffect(action) {
                            @Override
                            protected void doPlayEffect(SwccgGame game) {
                                if (self.getZone().isInPlay()) {
                                    if (result.getPreventableCardEffect() != null) {
                                        result.getPreventableCardEffect().preventEffectOnCard(character);
                                    }
                                    action.appendAfterEffect(
                                            new StackOneCardFromLostPileEffect(action, character, self, true, false, false));
                                }
                            }
                        });
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.isPlayingCard(game, effect, Filters.or(Filters.Elis_Helrot, Filters.Nabrun_Leids))
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getOpponentsCardGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        if (GameConditions.hasStackedCards(game, self)
                && GameConditions.canUseForce(game, playerId, 2)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId);
            action.setText("Use 2 Force to place stacked card in Used Pile");
            action.appendTargeting(
                    new ChooseStackedCardEffect(action, playerId, self) {
                        @Override
                        protected void cardSelected(PhysicalCard selectedCard) {
                            action.appendCost(
                                    new UseForceEffect(action, playerId, 2));
                            action.appendEffect(
                                    new PutStackedCardInUsedPileEffect(action, playerId, selectedCard, false));
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
