package com.gempukku.swccgo.cards.set1103.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.decisions.IntegerAwaitingDecision;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.ReduceForceLossEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.PlaceCardsOutOfPlayFromLostPileEffect;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.AboutToLoseForceResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 3: Revolution
 * Type: Effect
 * Title: Merc Sunlet
 */
public class Card1103_015 extends AbstractNormalEffect {
    public Card1103_015() {
        super(Side.LIGHT, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Merc Sunlet", Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_ORIGINAL_SET_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("A crafty purloiner from Tirac Munda with a heart of gold. Often hired to advise the wealthy on how to protect their own property. Well traveled and skilled in many languages.");
        setGameText("Deploy on table. Out Of Commission is canceled. Unless opponent has more cards in Lost Pile than you, whenever you lose Force, may use X Force and place any X cards from your Lost Pile out of play to reduce the loss by X. (Immune to Alter.)");
        addIcons(Icon.A_NEW_HOPE);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(final SwccgGame game, Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Out_Of_Commission)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();

        if (TriggerConditions.isTableChanged(game, effectResult)) {
            if (GameConditions.canTargetToCancel(game, self, Filters.Out_Of_Commission)) {
                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                CancelCardActionBuilder.buildCancelCardAction(action, Filters.Out_Of_Commission, Title.Out_Of_Commission);
                actions.add(action);
            }
        }

        return actions;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        final String opponent = game.getOpponent(playerId);

        if (TriggerConditions.isAboutToLoseForce(game, effectResult, playerId)
                && GameConditions.canReduceForceLoss(game)
                && GameConditions.numCardsInLostPile(game, opponent) <= GameConditions.numCardsInLostPile(game, playerId)) {
            AboutToLoseForceResult result = (AboutToLoseForceResult) effectResult;
            if (!result.isCannotBeReduced(game)) {
                final int lostPileSize = GameConditions.numCardsInLostPile(game, playerId);
                final int maxForceToUse = GameConditions.forceAvailableToUse(game, playerId);
                final int maxX = Math.min(lostPileSize, maxForceToUse);
                if (maxX > 0) {
                    final int defaultX = Math.min(maxX, (int) Math.ceil(result.getForceLossAmount(game)));
                    final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                    action.setText("Reduce Force loss");
                    action.appendCost(
                            new PlayoutDecisionEffect(action, playerId,
                                    new IntegerAwaitingDecision("Choose X (use X Force and place X Lost Pile cards out of play)", 1, maxX, defaultX) {
                                        @Override
                                        public void decisionMade(final int x) throws DecisionResultInvalidException {
                                            action.appendCost(
                                                    new UseForceEffect(action, playerId, x));
                                            action.appendCost(
                                                    new PlaceCardsOutOfPlayFromLostPileEffect(action, playerId, playerId, x, x, false));
                                            action.appendEffect(
                                                    new ReduceForceLossEffect(action, playerId, x));
                                        }
                                    }
                            ));
                    return Collections.singletonList(action);
                }
            }
        }
        return null;
    }
}
