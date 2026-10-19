package com.gempukku.swccgo.cards.set1262.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameActionProxyEffect;
import com.gempukku.swccgo.logic.effects.CancelDestinyEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardEffect;
import com.gempukku.swccgo.logic.effects.choose.PlayStackedDefensiveShieldEffect;
import com.gempukku.swccgo.logic.modifiers.ForfeitValueToUseWhenForfeitedModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToLoseCardFromTableResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 6.2
 * Type: Interrupt
 * Subtype: Used
 * Title: Dark Approach
 */
public class Card1262_025 extends AbstractUsedInterrupt {
    public Card1262_025() {
        super(Side.LIGHT, 4, "Dark Approach", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("'The Force is with you, young Skywalker. But you are not a Jedi yet.'");
        setGameText("During the weapons segment, if your character is about to be lost, it is instead lost at end of battle (forfeit for 0). OR Play a Defensive Shield from under your Starting Effect. OR During the power segment, cancel a just drawn destiny (except battle destiny).");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        PhysicalCard startingEffect = Filters.findFirstActive(game, self, Filters.and(Filters.your(self), Filters.Starting_Effect));
        if (startingEffect != null) {
            Filter filter = Filters.and(Filters.Defensive_Shield, Filters.playable(self));
            if (GameConditions.hasStackedCards(game, startingEffect, filter)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self, GameTextActionId.LEGACY__DARK_APPROACH_V__PLAY_DEFENSIVE_SHIELD);
                action.setText("Play a Defensive Shield");
                action.appendTargeting(
                        new ChooseStackedCardEffect(action, playerId, startingEffect, filter) {
                            @Override
                            protected void cardSelected(final PhysicalCard selectedCard) {
                                action.allowResponses(
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                action.appendEffect(
                                                        new PlayStackedDefensiveShieldEffect(action, self, selectedCard));
                                            }
                                        });
                            }
                        });
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        if (TriggerConditions.isAboutToBeLost(game, effectResult, Filters.and(Filters.your(self), Filters.character, Filters.participatingInBattle))
                && GameConditions.isDuringBattle(game)
                && game.getGameState().getBattleState() != null
                && !game.getGameState().getBattleState().isReachedPowerSegment()) {
            final AboutToLoseCardFromTableResult result = (AboutToLoseCardFromTableResult) effectResult;
            final PhysicalCard cardToBeLost = result.getCardToBeLost();

            final PlayInterruptAction action = new PlayInterruptAction(game, self, GameTextActionId.LEGACY__DARK_APPROACH_V__LOST_AT_END_OF_BATTLE);
            action.setText("Make " + GameUtils.getFullName(cardToBeLost) + " lost at end of battle");
            action.setActionMsg("Make " + GameUtils.getCardLink(cardToBeLost) + " lost at end of battle instead");
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new PassthruEffect(action) {
                                        @Override
                                        protected void doPlayEffect(SwccgGame game) {
                                            result.getPreventableCardEffect().preventEffectOnCard(cardToBeLost);
                                            action.appendEffect(
                                                    new AddUntilEndOfBattleModifierEffect(action, new ForfeitValueToUseWhenForfeitedModifier(self, cardToBeLost, 0), null));
                                            final int permCardId = self.getPermanentCardId();
                                            final int permLostCardId = cardToBeLost.getPermanentCardId();
                                            action.appendEffect(
                                                    new AddUntilEndOfGameActionProxyEffect(action,
                                                            new AbstractActionProxy() {
                                                                @Override
                                                                public List<TriggerAction> getRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult) {
                                                                    List<TriggerAction> proxyActions = new LinkedList<TriggerAction>();
                                                                    PhysicalCard interrupt = game.findCardByPermanentId(permCardId);
                                                                    PhysicalCard stillOnTable = game.findCardByPermanentId(permLostCardId);
                                                                    if (stillOnTable != null
                                                                            && Filters.onTable.accepts(game, stillOnTable)
                                                                            && (TriggerConditions.battleEnded(game, effectResult)
                                                                                || TriggerConditions.battleCanceled(game, effectResult))) {
                                                                        final RequiredGameTextTriggerAction action2 = new RequiredGameTextTriggerAction(interrupt, self.getCardId(), GameTextActionId.OTHER_CARD_ACTION_2);
                                                                        action2.setText("Make " + GameUtils.getFullName(stillOnTable) + " lost");
                                                                        action2.appendEffect(new LoseCardFromTableEffect(action2, stillOnTable, true));
                                                                        proxyActions.add(action2);
                                                                    }
                                                                    return proxyActions;
                                                                }
                                                            }
                                                    ));
                                        }
                                    });
                        }
                    }
            );
            actions.add(action);
        }

        if (TriggerConditions.isDestinyJustDrawn(game, effectResult)
                && !TriggerConditions.isBattleDestinyJustDrawn(game, effectResult)
                && GameConditions.canCancelDestiny(game, playerId)
                && GameConditions.isDuringBattle(game)
                && game.getGameState().getBattleState() != null
                && game.getGameState().getBattleState().isReachedPowerSegment()
                && !game.getGameState().getBattleState().isReachedDamageSegment()) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, GameTextActionId.LEGACY__DARK_APPROACH_V__CANCEL_DESTINY);
            action.setText("Cancel destiny");
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new CancelDestinyEffect(action));
                        }
                    }
            );
            actions.add(action);
        }

        return actions.isEmpty() ? null : actions;
    }
}
