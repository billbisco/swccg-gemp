package com.gempukku.swccgo.cards.set1220.light;

import com.gempukku.swccgo.cards.AbstractEpicEventDeployable;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.TurnStackedCardsFaceDownInsteadOfUsingForce;
import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.EpicEventState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.PlayEpicEventAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.StackCardFromHandEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DuelTotalModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalBattleDestinyModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.ArrayList;
import java.util.List;

/** Block 2: It Is The Future You See (V), final Light master p31. */
public class Card1220_138 extends AbstractEpicEventDeployable {
    private static final Filter EXCLUDED_START = Filters.or(Icon.CORUSCANT, Icon.REFLECTIONS_III, Icon.SPECIAL_EDITION);
    private static final Filter INSERT_PRINTING = new Filter() {
        @Override
        public boolean accepts(GameState state, ModifiersQuerying querying, PhysicalCard card) {
            // An insert-capable printing is lost even if it has not yet been inserted.
            return card.isInserted() || card.getBlueprint() instanceof AbstractNormalEffect effect
                    && effect.getInsertCardRevealedAction(state.getGame(), card) != null;
        }
    };
    private static final Filter BOTH_PLAYERS_LOSE = Filters.or(Filters.title(Title.Revolution), INSERT_PRINTING);
    private static final Filter LIGHT_PLAYER_LOSES = Filters.or(BOTH_PLAYERS_LOSE, Icon.MAINTENANCE, Icon.PERMANENT_WEAPON);

    public Card1220_138() {
        super(Side.LIGHT, 0, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.It_Is_The_Future_You_See,
                Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setGameText("If you deployed only 1 location (unless it is [Coruscant], [Reflections III], or [Special Edition]), play instead of a Starting Interrupt. [download] Battle Plan, Do, Or Do Not, and an [Immune to Alter] Effect. Revolution, insert cards, and your [Maintenance] and [Permanent Weapon] cards are lost. You go first. Choose one: Jedi: Deploy on table. At start of your turn, turn all cards here face up. During your turn, may use 1 Force for each card stacked here to stack a card from hand face up here. Instead of using X Force, may turn X cards here face down. Agent of Evil: [upload] Luke and a site. Deploy on opponent's side of table; their total battle and duel destiny is +2.");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected boolean canPlayCardDuringCurrentPhase(String playerId, SwccgGame game, PhysicalCard self) {
        return false; // The printed Epic Event is played only instead of a Starting Interrupt.
    }

    @Override
    public boolean playableAsStartingInterrupt(SwccgGame game, PhysicalCard self) {
        var locations = Filters.filterAllOnTable(game, Filters.and(Filters.owner(self.getOwner()), Filters.location));
        return locations.size() == 1 && !EXCLUDED_START.accepts(game, locations.iterator().next());
    }

    @Override
    public PlayCardAction getStartingInterruptAction(String playerId, SwccgGame game, PhysicalCard self) {
        if (!playableAsStartingInterrupt(game, self)) return null;
        var action = new PlayEpicEventAction(self);
        action.setEpicEventState(new EpicEventState(self, EpicEventState.Type.PLAY_INSTEAD_OF_STARTING_INTERRUPT) {});
        action.setText("Play instead of a Starting Interrupt");
        action.allowResponses("Play instead of a Starting Interrupt", new RespondablePlayCardEffect(action) {
            @Override
            protected void performActionResults(Action targetingAction) {
                action.appendEffect(new DeployCardFromReserveDeckEffect(action, Filters.Battle_Plan, true, false));
                action.appendEffect(new DeployCardFromReserveDeckEffect(action, Filters.Do_Or_Do_Not, true, false));
                action.appendEffect(new DeployCardFromReserveDeckEffect(action, Filters.and(Filters.Effect, Filters.always_immune_to_Alter), true, false));
                action.appendEffect(new ShuffleReserveDeckEffect(action, playerId));
                appendPurge(action, playerId);
                action.appendEffect(new LightSideGoesFirstEffect(action));
                action.appendEffect(new PlayoutDecisionEffect(action, playerId,
                        new MultipleChoiceAwaitingDecision("Choose It Is The Future You See mode", new String[]{"Jedi", "Agent of Evil"}) {
                            @Override
                            protected void validDecisionMade(int index, String result) {
                                boolean agent = index == 1;
                                if (agent) {
                                    action.appendEffect(new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.Luke, false));
                                    action.appendEffect(new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.site, false));
                                    action.appendEffect(new ShuffleReserveDeckEffect(action, playerId));
                                }
                                action.appendEffect(new DeploySingleCardEffect(action, self, Zone.SIDE_OF_TABLE, agent, null, false));
                            }
                        }));
            }
        });
        return action;
    }

    private void appendPurge(PlayEpicEventAction action, String playerId) {
        action.appendEffect(new PassthruEffect(action) {
            @Override
            protected void doPlayEffect(SwccgGame game) {
                var opponent = game.getOpponent(playerId);
                var onTable = Filters.filterAllOnTable(game, Filters.or(BOTH_PLAYERS_LOSE,
                        Filters.and(Filters.owner(playerId), LIGHT_PLAYER_LOSES)));
                if (!onTable.isEmpty()) action.insertEffect(new LoseCardsFromTableEffect(action, onTable));
                appendPurgeFromPiles(action, game, playerId, LIGHT_PLAYER_LOSES);
                appendPurgeFromPiles(action, game, opponent, BOTH_PLAYERS_LOSE);
            }
        });
    }

    private void appendPurgeFromPiles(PlayEpicEventAction action, SwccgGame game, String playerId, Filter filter) {
        var state = game.getGameState();
        var piles = new ArrayList<>(state.getReserveDeck(playerId, false));
        piles.addAll(state.getForcePile(playerId));
        piles.addAll(state.getUsedPile(playerId));
        var pileMatches = Filters.filter(piles, game, filter);
        var handMatches = Filters.filter(state.getHand(playerId), game, filter);
        if (!pileMatches.isEmpty()) action.insertEffect(new LoseCardsFromCardPilesEffect(action, playerId, pileMatches));
        if (!handMatches.isEmpty()) action.insertEffect(new LoseCardsFromHandEffect(action, playerId, handMatches));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        if (self.getZone() == Zone.SIDE_OF_TABLE && game.getOpponent(self.getOwner()).equals(self.getZoneOwner())) {
            String opponent = self.getZoneOwner();
            return List.of(new TotalBattleDestinyModifier(self, 2, opponent),
                    new DuelTotalModifier(self, Filters.and(Filters.owner(opponent), Filters.character), 2));
        }
        if (onJediSide(self)) {
            return List.of(TurnStackedCardsFaceDownInsteadOfUsingForce.modifier(self));
        }
        return List.of();
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalBeforeTriggers(
            String playerId, SwccgGame game, Effect effect, PhysicalCard self, int sourceId) {
        // Instead of using X Force (any Force used during your turn), may turn X cards here face down.
        return onJediSide(self) ? TurnStackedCardsFaceDownInsteadOfUsingForce.optionalBeforeTriggers(playerId, game, effect, self, sourceId) : null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(
            SwccgGame game, Effect effect, PhysicalCard self, int sourceId) {
        return onJediSide(self) ? TurnStackedCardsFaceDownInsteadOfUsingForce.requiredBeforeTriggers(game, effect, self, sourceId) : null;
    }

    private boolean onJediSide(PhysicalCard self) {
        return self.getZone() == Zone.SIDE_OF_TABLE && self.getOwner().equals(self.getZoneOwner());
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(
            SwccgGame game, EffectResult result, PhysicalCard self, int sourceId) {
        if (!onJediSide(self) || !TriggerConditions.isStartOfYourTurn(game, result, self.getOwner())
                || Filters.filter(game.getGameState().getStackedCards(self), game, Zone.STACKED_FACE_DOWN).isEmpty()) {
            return List.of();
        }
        var action = new RequiredGameTextTriggerAction(self, sourceId);
        action.setText("Turn all stacked cards face up");
        action.appendEffect(new PassthruEffect(action) {
            @Override
            protected void doPlayEffect(SwccgGame game) {
                var state = game.getGameState();
                for (var card : Filters.filter(state.getStackedCards(self), game, Zone.STACKED_FACE_DOWN)) {
                    state.relocateCardAsStacked(card, self, false, false);
                }
            }
        });
        return List.of(action);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(
            String playerId, SwccgGame game, PhysicalCard self, int sourceId) {
        if (!playerId.equals(self.getOwner()) || !onJediSide(self)
                || !GameConditions.isDuringYourTurn(game, playerId) || !GameConditions.hasHand(game, playerId)) return List.of();
        int cost = game.getGameState().getStackedCards(self).size();
        // Force available includes face-up cards here, which may be turned face down instead of using that Force
        // (see TurnStackedCardsFaceDownInsteadOfUsingForce; applies to any Force used during your turn).
        if (cost > 0 && !GameConditions.canUseForce(game, playerId, cost)) return List.of();
        var action = new TopLevelGameTextAction(self, playerId, sourceId);
        action.setText("Stack a card from hand face up");
        if (cost > 0) action.appendCost(new UseForceEffect(action, playerId, cost));
        action.appendEffect(new StackCardFromHandEffect(action, playerId, self));
        return List.of(action);
    }
}
