package com.gempukku.swccgo.cards.set1106.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Set 6: City In The Clouds
 * Type: Interrupt
 * Subtype: Lost
 * Title: Wookiee Strangle
 */
public class Card1106_025 extends AbstractLostInterrupt {
    public Card1106_025() {
        super(Side.LIGHT, 3, Title.Wookiee_Strangle, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_6, Rarity.V);
        setVirtualSuffix(true);
        setLore("Wookiees have been known to dislocate more than just arms.");
        setGameText("During your move phase, target your Wookiee and opponent's character present with it. Both players draw destiny. Add target's defense value and power. Subtract target's destiny number. Opponent's character lost if your total destiny +2 > opponent's total destiny.");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);

        Filter targetFilter = Filters.and(Filters.opponents(self), Filters.character, Filters.presentWith(self, Filters.and(Filters.your(self), Filters.Wookiee)));
        TargetingReason targetingReason = TargetingReason.TO_BE_LOST;

        if (GameConditions.isDuringYourPhase(game, self, Phase.MOVE)
                && GameConditions.canTarget(game, self, targetingReason, targetFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Target a character");
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose opponent's character", targetingReason, targetFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId1, final PhysicalCard targetCard) {
                            action.appendTargeting(
                                    new TargetCardOnTableEffect(action, playerId, "Choose Wookiee", Filters.and(Filters.your(self), Filters.Wookiee, Filters.presentWith(targetCard))) {
                                        @Override
                                        protected void cardTargeted(final int targetGroupId2, PhysicalCard wookiee) {
                                            action.addAnimationGroup(targetCard, wookiee);
                                            action.allowResponses("Target " + GameUtils.getCardLink(wookiee) + " and " + GameUtils.getCardLink(targetCard),
                                                    new RespondablePlayCardEffect(action) {
                                                        @Override
                                                        protected void performActionResults(Action targetingAction) {
                                                            final PhysicalCard finalTarget = targetingAction.getPrimaryTargetCard(targetGroupId1);
                                                            final PhysicalCard finalWookiee = targetingAction.getPrimaryTargetCard(targetGroupId2);

                                                            action.appendEffect(
                                                                    new DrawDestinyEffect(action, playerId) {
                                                                        @Override
                                                                        protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                                            return Arrays.asList(finalTarget, finalWookiee);
                                                                        }
                                                                        @Override
                                                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, final Float playersTotalDestiny) {
                                                                            action.appendEffect(
                                                                                    new DrawDestinyEffect(action, opponent) {
                                                                                        @Override
                                                                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float opponentsTotalDestiny) {
                                                                                            GameState gameState = game.getGameState();

                                                                                            gameState.sendMessage(playerId + "'s destiny: " + (playersTotalDestiny != null ? GuiUtils.formatAsString(playersTotalDestiny) : "Failed destiny draw"));
                                                                                            gameState.sendMessage(opponent + "'s destiny: " + (opponentsTotalDestiny != null ? GuiUtils.formatAsString(opponentsTotalDestiny) : "Failed destiny draw"));
                                                                                            float wookieeDefense = game.getModifiersQuerying().getDefenseValue(gameState, finalWookiee);
                                                                                            float wookieePower = game.getModifiersQuerying().getPower(gameState, finalWookiee);
                                                                                            float wookieeDestiny = game.getModifiersQuerying().getDestiny(gameState, finalWookiee);
                                                                                            float targetDefense = game.getModifiersQuerying().getDefenseValue(gameState, finalTarget);
                                                                                            float targetPower = game.getModifiersQuerying().getPower(gameState, finalTarget);
                                                                                            float targetDestiny = game.getModifiersQuerying().getDestiny(gameState, finalTarget);
                                                                                            gameState.sendMessage(GameUtils.getCardLink(finalWookiee) + "'s defense value: " + GuiUtils.formatAsString(wookieeDefense));
                                                                                            gameState.sendMessage(GameUtils.getCardLink(finalWookiee) + "'s power: " + GuiUtils.formatAsString(wookieePower));
                                                                                            gameState.sendMessage(GameUtils.getCardLink(finalWookiee) + "'s destiny number: " + GuiUtils.formatAsString(wookieeDestiny));
                                                                                            gameState.sendMessage(GameUtils.getCardLink(finalTarget) + "'s defense value: " + GuiUtils.formatAsString(targetDefense));
                                                                                            gameState.sendMessage(GameUtils.getCardLink(finalTarget) + "'s power: " + GuiUtils.formatAsString(targetPower));
                                                                                            gameState.sendMessage(GameUtils.getCardLink(finalTarget) + "'s destiny number: " + GuiUtils.formatAsString(targetDestiny));

                                                                                            if (playersTotalDestiny == null && opponentsTotalDestiny == null) {
                                                                                                gameState.sendMessage("Both players failed due to failed destiny draws");
                                                                                                gameState.sendMessage("Result: No result");
                                                                                            } else if (playersTotalDestiny == null) {
                                                                                                gameState.sendMessage(playerId + "'s total failed due to failed destiny draw");
                                                                                                gameState.sendMessage("Result: No result");
                                                                                            } else if (opponentsTotalDestiny == null) {
                                                                                                gameState.sendMessage(opponent + "'s total failed due to failed destiny draw");
                                                                                                gameState.sendMessage("Result: " + GameUtils.getCardLink(finalTarget) + " to be lost");
                                                                                                action.appendEffect(
                                                                                                        new LoseCardFromTableEffect(action, finalTarget));
                                                                                            } else {
                                                                                                float playersTotal = playersTotalDestiny + wookieeDefense + wookieePower - wookieeDestiny;
                                                                                                gameState.sendMessage(playerId + "'s total: " + GuiUtils.formatAsString(playersTotal));
                                                                                                float opponentsTotal = opponentsTotalDestiny + targetDefense + targetPower - targetDestiny;
                                                                                                gameState.sendMessage(opponent + "'s total: " + GuiUtils.formatAsString(opponentsTotal));
                                                                                                if (playersTotal + 2 > opponentsTotal) {
                                                                                                    gameState.sendMessage("Result: " + GameUtils.getCardLink(finalTarget) + " to be lost");
                                                                                                    action.appendEffect(
                                                                                                            new LoseCardFromTableEffect(action, finalTarget));
                                                                                                } else {
                                                                                                    gameState.sendMessage("Result: No result");
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    }
                                                                            );
                                                                        }
                                                                    }
                                                            );
                                                        }
                                                    }
                                            );
                                        }
                                    }
                            );
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
