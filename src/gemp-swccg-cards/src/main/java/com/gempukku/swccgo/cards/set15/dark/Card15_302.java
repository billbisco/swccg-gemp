package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.cards.set11.dark.Card11_090;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.decisions.IntegerAwaitingDecision;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.ResetTotalBattleDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import java.util.Collections;
import java.util.List;

public class Card15_302 extends Card11_090 {
    public Card15_302() {
        setCardSubtype(CardSubtype.USED_OR_LOST);
        setAlternateDestiny(4);
        setLore("");
        setGameText("USED: If Watto is in a battle at a site, draw destiny. If destiny is odd, your total battle destiny = 0 this battle. If destiny is even, add two battle destiny. LOST: Opponent chooses: opponent may activate up to 2 Force and you may activate up to 4 Force, or opponent retrieves 1 Force and you retrieve 3 Force.");
        setCredits(0);
        setAsHorizontal(false);
        for (Icon icon : Icon.values()) {
            if (icon.isExpansionIcon() || icon == Icon.EPISODE_I) addIcon(icon, 0);
        }
        addIcons(Icon.REFLECTIONS_GOLD, Icon.EPISODE_I);
        addComboCardTitles("Watto's Chance Cube", "You Swindled Me!");
    }
    @Override public String getTitle() { return "Watto's Chance Cube & You Swindled Me!"; }
    @Override public Float getDestiny() { return 4f; }
    @Override public Uniqueness getUniqueness() { return Uniqueness.UNIQUE; }
    @Override public ExpansionSet getExpansionSet() { return ExpansionSet.REFLECTIONS_GOLD; }
    @Override public Rarity getRarity() { return Rarity.UNKNOWN; }

    private List<PlayInterruptAction> getGameTextTopLevelActionsFromCombinedCard(final String playerId, final SwccgGame game, PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);

        if (GameConditions.canActivateForce(game, playerId)
             && GameConditions.canActivateForce(game, opponent)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Make opponent choose");
            // Allow response(s)
            action.allowResponses("Make opponent choose to activate Force or retrieve Force",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new PlayoutDecisionEffect(action, opponent,
                                            new MultipleChoiceAwaitingDecision("Choose effect", new String[]{"Activate up to 2 Force", "Retrieve 1 Force"}) {
                                                @Override
                                                protected void validDecisionMade(int index, String result) {
                                                    final GameState gameState = game.getGameState();

                                                    if (index == 0) {
                                                        gameState.sendMessage(opponent + " chooses to activate up to 2 Force and allow " + playerId + " to activate up to 4 Force");
                                                        int maxForceForOpponentToActivate = Math.min(2, gameState.getReserveDeckSize(opponent));
                                                        if (maxForceForOpponentToActivate > 0) {
                                                            action.appendEffect(
                                                                    new PlayoutDecisionEffect(action, opponent,
                                                                            new IntegerAwaitingDecision("Choose amount of Force to activate", 0, maxForceForOpponentToActivate, maxForceForOpponentToActivate) {
                                                                                @Override
                                                                                public void decisionMade(final int result) throws DecisionResultInvalidException {
                                                                                    // Perform result(s)
                                                                                    action.appendEffect(
                                                                                            new ActivateForceEffect(action, opponent, result));
                                                                                    action.appendEffect(
                                                                                            new PassthruEffect(action) {
                                                                                                @Override
                                                                                                protected void doPlayEffect(SwccgGame game) {
                                                                                                    int maxForceForPlayerToActivate = Math.min(4, gameState.getReserveDeckSize(playerId));
                                                                                                    if (maxForceForPlayerToActivate > 0) {
                                                                                                        action.appendEffect(
                                                                                                                new PlayoutDecisionEffect(action, playerId,
                                                                                                                        new IntegerAwaitingDecision("Choose amount of Force to activate", 0, maxForceForPlayerToActivate, maxForceForPlayerToActivate) {
                                                                                                                            @Override
                                                                                                                            public void decisionMade(final int result) throws DecisionResultInvalidException {
                                                                                                                                // Perform result(s)
                                                                                                                                action.appendEffect(
                                                                                                                                        new ActivateForceEffect(action, playerId, result));
                                                                                                                            }
                                                                                                                        }
                                                                                                                )
                                                                                                        );
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                    );
                                                                                }
                                                                            }
                                                                    )
                                                            );
                                                        } else {
                                                            int maxForceForPlayerToActivate = Math.min(4, gameState.getReserveDeckSize(playerId));
                                                            if (maxForceForPlayerToActivate > 0) {
                                                                action.appendEffect(
                                                                        new PlayoutDecisionEffect(action, playerId,
                                                                                new IntegerAwaitingDecision("Choose amount of Force to activate", 0, maxForceForPlayerToActivate, maxForceForPlayerToActivate) {
                                                                                    @Override
                                                                                    public void decisionMade(final int result) throws DecisionResultInvalidException {
                                                                                        // Perform result(s)
                                                                                        action.appendEffect(
                                                                                                new ActivateForceEffect(action, playerId, result));
                                                                                    }
                                                                                }
                                                                        )
                                                                );
                                                            }
                                                        }
                                                    } else {
                                                        gameState.sendMessage(opponent + " chooses to retrieve 1 Force and " + playerId + " to retrieve 3 Force");
                                                        action.appendEffect(
                                                                new RetrieveForceEffect(action, opponent, 1));
                                                        action.appendEffect(
                                                                new RetrieveForceEffect(action, playerId, 3));
                                                    }
                                                }
                                            }
                                    )
                            );
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    private List<PlayInterruptAction> getGameTextTopLevelActionsPrimary(final String playerId, final SwccgGame game, final PhysicalCard self) {
        // Check condition(s)
        if (GameConditions.isDuringBattleAt(game, Filters.site)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.Watto)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Draw destiny");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new DrawDestinyEffect(action, playerId) {
                                        @Override
                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                            GameState gameState = game.getGameState();
                                            if (totalDestiny == null) {
                                                gameState.sendMessage("Result: Failed destiny draw");
                                                return;
                                            }

                                            if ((totalDestiny % 2 == 1)) {
                                                gameState.sendMessage("Result: Destiny is odd");
                                                action.appendEffect(
                                                        new ResetTotalBattleDestinyEffect(action, playerId, 0));
                                            }
                                            else if ((totalDestiny % 2 == 0)
                                                    && GameConditions.canAddBattleDestinyDraws(game, self)) {
                                                gameState.sendMessage("Result: Destiny is even");
                                                action.appendEffect(
                                                        new AddBattleDestinyEffect(action, 2));
                                            }
                                            else {
                                                gameState.sendMessage("Result: No result");
                                            }
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

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, PhysicalCard self) {
        List<PlayInterruptAction> result = new java.util.ArrayList<>();
        List<PlayInterruptAction> first = getGameTextTopLevelActionsPrimary(playerId, game, self);
        if (first != null) result.addAll(first);
        List<PlayInterruptAction> second = getGameTextTopLevelActionsFromCombinedCard(playerId, game, self);
        if (second != null) result.addAll(second);
        return result;
    }
}
