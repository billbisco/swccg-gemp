package com.gempukku.swccgo.cards.set1107.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.DestinyType;
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
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Set 7: Shadows Of The Empire
 * Type: Interrupt
 * Subtype: Lost
 * Title: Gambler's Luck
 */
public class Card1107_003 extends AbstractLostInterrupt {
    public Card1107_003() {
        super(Side.LIGHT, 4, Title.Gamblers_Luck, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_7, Rarity.V);
        setVirtualSuffix(true);
        setLore("Works every time.");
        setGameText("During opponent's control phase, if your gambler on table, use 1 Force. Both players draw three destiny. Winner (higher total destiny) may target one opponent's character with a printed power matching one of the winner's destiny draws. Target is lost.");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);
        if (GameConditions.isDuringOpponentsPhase(game, playerId, Phase.CONTROL)
                && GameConditions.canSpot(game, self, Filters.and(Filters.your(self), Filters.gambler))
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Draw destiny against opponent");
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            action.allowResponses("Both players draw three destiny",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new DrawDestinyEffect(action, playerId, 3, DestinyType.DESTINY) {
                                        @Override
                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, final List<Float> destinyDrawValues, final Float totalDestiny) {
                                            action.appendEffect(
                                                    new DrawDestinyEffect(action, opponent, 3, DestinyType.DESTINY) {
                                                        @Override
                                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> oppDestinyCardDraws, final List<Float> oppDestinyDrawValues, final Float oppTotalDestiny) {
                                                            if (totalDestiny == null || oppTotalDestiny == null) {
                                                                return;
                                                            }
                                                            if (totalDestiny.equals(oppTotalDestiny)) {
                                                                return;
                                                            }
                                                            final boolean lightWins = totalDestiny > oppTotalDestiny;
                                                            final String winner = lightWins ? playerId : opponent;
                                                            final String loser = lightWins ? opponent : playerId;
                                                            final List<Float> winnerDraws = lightWins ? destinyDrawValues : oppDestinyDrawValues;
                                                            Filter matchFilter = Filters.and(Filters.opponents(winner), Filters.character, new Filter() {
                                                                @Override
                                                                public boolean accepts(com.gempukku.swccgo.game.state.GameState gameState, com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                                                                    Float printed = physicalCard.getBlueprint().getPower();
                                                                    if (printed == null || winnerDraws == null) {
                                                                        return false;
                                                                    }
                                                                    for (Float draw : winnerDraws) {
                                                                        if (draw != null && printed.equals(draw)) {
                                                                            return true;
                                                                        }
                                                                    }
                                                                    return false;
                                                                }
                                                            });
                                                            if (GameConditions.canTarget(game, self, TargetingReason.TO_BE_LOST, matchFilter)) {
                                                                action.appendEffect(
                                                                        new ChooseCardOnTableEffect(action, winner, "Choose character to be lost", matchFilter, 0) {
                                                                            @Override
                                                                            protected void cardSelected(PhysicalCard selectedCard) {
                                                                                if (selectedCard != null) {
                                                                                    action.appendEffect(
                                                                                            new LoseCardFromTableEffect(action, selectedCard));
                                                                                    game.getGameState().sendMessage(winner + " targets " + GameUtils.getCardLink(selectedCard) + " to be lost");
                                                                                }
                                                                            }
                                                                        }
                                                                );
                                                            }
                                                            else {
                                                                game.getGameState().sendMessage(loser + " has no character with printed power matching " + winner + "'s destiny draws");
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
            return Collections.singletonList(action);
        }
        return null;
    }
}
