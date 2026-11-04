package com.gempukku.swccgo.cards.set1273.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardOfCardPileEffect;
import com.gempukku.swccgo.cards.effects.complete.ChooseExistingCardPileEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnActionProxyEffect;
import com.gempukku.swccgo.logic.effects.ModifyDestinyEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.ShufflePileEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardsIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Set: Virtual Block 7.3
 * Type: Interrupt
 * Subtype: Used
 * Title: Use The Force
 */
public class Card1273_034 extends AbstractUsedInterrupt {
    static final String PEEK = "Peek at the top card of any deck or pile";
    static final String DRAW = "Draw 2 cards from Reserve Deck";
    static final String RETRIEVE = "Retrieve 1 Force";
    static final String ACTIVATE = "Activate X Force";
    static final String DESTINY = "Your next destiny draw this turn is +X";
    static final String DONE = "Done";

    public Card1273_034() {
        super(Side.LIGHT, 4, "Use The Force", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_3, Rarity.V);
        setLore("'Use the Force. Yes... Now, the stone. Feel it.'");
        setGameText("Choose up to X of the following in any order, where X = number of cards stacked on Communing: Peek at the top card of any deck or pile; may then shuffle it. OR Draw 2 cards from Reserve Deck. OR Retrieve 1 Force. OR Activate X Force. OR Your next destiny draw this turn is +X.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        final PhysicalCard communing = Filters.findFirstActive(game, self, Filters.and(Filters.your(playerId), Filters.Communing));
        if (communing == null)
            return null;
        final int x = game.getGameState().getStackedCards(communing).size();
        if (x < 1)
            return null;

        final PlayInterruptAction action = new PlayInterruptAction(game, self);
        action.setText("Choose up to " + x + " options");
        // Allow response(s)
        action.allowResponses("Choose up to " + x + " options (X = " + x + ")",
                new RespondablePlayCardEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        // Perform result(s)
                        Set<String> remaining = new LinkedHashSet<String>();
                        remaining.add(PEEK);
                        remaining.add(DRAW);
                        remaining.add(RETRIEVE);
                        remaining.add(ACTIVATE);
                        remaining.add(DESTINY);
                        action.appendEffect(new ChooseNextOption(action, playerId, self, x, x, remaining));
                    }
                }
        );
        return Collections.singletonList(action);
    }

    /**
     * Lets the player choose the next of the remaining options (or stop).
     */
    private static class ChooseNextOption extends PassthruEffect {
        private final PlayInterruptAction _action;
        private final String _playerId;
        private final PhysicalCard _self;
        private final int _x;
        private final int _choicesLeft;
        private final Set<String> _remaining;

        ChooseNextOption(PlayInterruptAction action, String playerId, PhysicalCard self, int x, int choicesLeft, Set<String> remaining) {
            super(action);
            _action = action;
            _playerId = playerId;
            _self = self;
            _x = x;
            _choicesLeft = choicesLeft;
            _remaining = remaining;
        }

        @Override
        protected void doPlayEffect(SwccgGame game) {
            if (_choicesLeft <= 0 || _remaining.isEmpty())
                return;
            final List<String> choices = new ArrayList<String>(_remaining);
            choices.add(DONE);
            _action.appendEffect(
                    new PlayoutDecisionEffect(_action, _playerId,
                            new MultipleChoiceAwaitingDecision("Choose an option (" + _choicesLeft + " left)", choices.toArray(new String[0])) {
                                @Override
                                protected void validDecisionMade(int index, String result) {
                                    if (DONE.equals(result))
                                        return;
                                    Set<String> stillRemaining = new LinkedHashSet<String>(_remaining);
                                    stillRemaining.remove(result);
                                    appendOption(result);
                                    _action.appendEffect(new ChooseNextOption(_action, _playerId, _self, _x, _choicesLeft - 1, stillRemaining));
                                }
                            }));
        }

        private void appendOption(String option) {
            if (PEEK.equals(option)) {
                _action.appendEffect(
                        new ChooseExistingCardPileEffect(_action, _playerId) {
                            @Override
                            protected void pileChosen(SwccgGame game, final String cardPileOwner, final Zone cardPile) {
                                _action.appendEffect(
                                        new PeekAtTopCardOfCardPileEffect(_action, _playerId, cardPileOwner, cardPile));
                                _action.appendEffect(
                                        new PlayoutDecisionEffect(_action, _playerId,
                                                new YesNoDecision("Do you want to shuffle " + cardPileOwner + "'s " + cardPile.getHumanReadable() + "?") {
                                                    @Override
                                                    protected void yes() {
                                                        _action.insertEffect(
                                                                new ShufflePileEffect(_action, cardPileOwner, cardPile));
                                                    }
                                                }));
                            }
                        });
            }
            else if (DRAW.equals(option)) {
                _action.appendEffect(
                        new DrawCardsIntoHandFromReserveDeckEffect(_action, _playerId, 2));
            }
            else if (RETRIEVE.equals(option)) {
                _action.appendEffect(
                        new RetrieveForceEffect(_action, _playerId, 1));
            }
            else if (ACTIVATE.equals(option)) {
                _action.appendEffect(
                        new ActivateForceEffect(_action, _playerId, _x));
            }
            else if (DESTINY.equals(option)) {
                final boolean[] used = new boolean[]{false};
                final int amount = _x;
                final String playerId = _playerId;
                final PhysicalCard self = _self;
                _action.appendEffect(
                        new AddUntilEndOfTurnActionProxyEffect(_action,
                                new AbstractActionProxy() {
                                    @Override
                                    public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult) {
                                        if (!used[0]
                                                && TriggerConditions.isDestinyJustDrawnBy(game, effectResult, playerId)) {
                                            used[0] = true;
                                            RequiredGameTextTriggerAction trigger = new RequiredGameTextTriggerAction(self, self.getCardId());
                                            trigger.setPerformingPlayer(playerId);
                                            trigger.setText("Add " + amount + " to destiny");
                                            trigger.setActionMsg("Adds " + amount + " to destiny (Use The Force)");
                                            trigger.appendEffect(
                                                    new ModifyDestinyEffect(trigger, amount));
                                            return Collections.<TriggerAction>singletonList(trigger);
                                        }
                                        return null;
                                    }
                                }));
            }
        }
    }
}
