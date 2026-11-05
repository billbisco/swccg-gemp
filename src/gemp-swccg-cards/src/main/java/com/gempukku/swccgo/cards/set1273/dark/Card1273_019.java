package com.gempukku.swccgo.cards.set1273.dark;

import com.gempukku.swccgo.cards.AbstractMission;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.LegacySpiceCards;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.decisions.IntegerAwaitingDecision;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.CancelCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.DrawOneCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.PutStackedCardInLostPileEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.StackCardFromLostPileEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 7.3
 * Type: Mission
 * Subtype: Critical
 * Title: Spice Mine Operations
 */
public class Card1273_019 extends AbstractMission {
    static final Filter EXTRACTION_FACILITY = Filters.and(Filters.site, Filters.titleContains("Extraction Facility"));
    static final Filter KESSEL_LOCATION = Filters.or(Filters.Kessel_system, Filters.partOfSystem(Title.Kessel));

    public Card1273_019() {
        super(Side.DARK, 0, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, LegacySpiceCards.SPICE_MINE_OPERATIONS, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_3, Rarity.V, true);
        setGameText("If your [Virtual Block 7] office on table, deploy on table. Program Trap is canceled. Cards stacked here are 'spice' cards. Once per turn, if you control Extraction Facility, may place a 'spice' card in owner's Lost Pile to draw up to 2 cards from Reserve Deck. Once during your turn, may: use 2 Force to stack a card from Lost Pile here, deploy a non-character card with \"Kessel\" in title from Reserve Deck; reshuffle, and/or 'mine' (opponent draws destiny; draw up to X destiny, where X = 1 plus number of Kessel locations you occupy; if any of the printed destiny numbers match, stack opponent's destiny or top card of your Lost Pile here).");
        addIcons(Icon.SETUP);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return GameConditions.canSpot(game, self, Filters.and(Filters.your(playerId), Filters.site, Icon.LEGACY_BLOCK_7, Filters.titleContains("Office")));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.isTableChanged(game, effectResult)) {
            PhysicalCard programTrap = Filters.findFirstActive(game, self, Filters.Program_Trap);
            if (programTrap != null) {
                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setSingletonTrigger(true);
                action.setText("Cancel Program Trap");
                action.setActionMsg("Cancel " + GameUtils.getCardLink(programTrap));
                action.appendEffect(
                        new CancelCardOnTableEffect(action, programTrap));
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        // Once per turn, if you control Extraction Facility, place a 'spice' card in owner's Lost Pile to draw up to 2 cards
        GameTextActionId extractId = GameTextActionId.LEGACY__SPICE_MINE_OPERATIONS__DRAW_CARDS;
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, extractId)
                && GameConditions.controls(game, playerId, EXTRACTION_FACILITY)
                && GameConditions.hasStackedCards(game, self)
                && GameConditions.hasReserveDeck(game, playerId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, extractId);
            action.setText("Place a 'spice' card in Lost Pile to draw cards");
            action.setActionMsg("Place a 'spice' card in owner's Lost Pile to draw up to 2 cards from Reserve Deck");
            action.appendUsage(new OncePerTurnEffect(action));
            final int maxDraw = Math.min(2, game.getGameState().getReserveDeckSize(playerId));
            action.appendTargeting(
                    new ChooseStackedCardEffect(action, playerId, self) {
                        @Override
                        protected void cardSelected(PhysicalCard selectedCard) {
                            action.appendCost(
                                    new PutStackedCardInLostPileEffect(action, playerId, selectedCard, false));
                            action.appendEffect(
                                    new PlayoutDecisionEffect(action, playerId,
                                            new IntegerAwaitingDecision("Choose number of cards to draw from Reserve Deck", 0, maxDraw, maxDraw) {
                                                @Override
                                                public void decisionMade(int result) throws DecisionResultInvalidException {
                                                    for (int i = 0; i < result; i++) {
                                                        action.appendEffect(
                                                                new DrawOneCardFromReserveDeckEffect(action, playerId));
                                                    }
                                                }
                                            }));
                        }
                    });
            actions.add(action);
        }

        // Once during your turn: use 2 Force to stack a card from Lost Pile here
        GameTextActionId stackId = GameTextActionId.LEGACY__SPICE_MINE_OPERATIONS__STACK_FROM_LOST_PILE;
        if (GameConditions.isOnceDuringYourTurn(game, self, playerId, gameTextSourceCardId, stackId)
                && GameConditions.canUseForce(game, playerId, 2)
                && GameConditions.hasLostPile(game, playerId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, stackId);
            action.setText("Stack a card from Lost Pile here");
            action.setActionMsg("Stack a card from Lost Pile on " + GameUtils.getCardLink(self));
            action.appendUsage(new OncePerTurnEffect(action));
            action.appendCost(
                    new UseForceEffect(action, playerId, 2));
            action.appendEffect(
                    new StackCardFromLostPileEffect(action, self, Filters.any, false));
            actions.add(action);
        }

        // Once during your turn: deploy a non-character card with "Kessel" in title from Reserve Deck
        GameTextActionId deployId = GameTextActionId.LEGACY__SPICE_MINE_OPERATIONS__DEPLOY_KESSEL_CARD;
        if (GameConditions.isOnceDuringYourTurn(game, self, playerId, gameTextSourceCardId, deployId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, deployId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, deployId);
            action.setText("Deploy a non-character Kessel card from Reserve Deck");
            action.setActionMsg("Deploy a non-character card with \"Kessel\" in title from Reserve Deck");
            action.appendUsage(new OncePerTurnEffect(action));
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.and(Filters.not(Filters.character), Filters.titleContains("Kessel")), true));
            actions.add(action);
        }

        // Once during your turn: 'mine'
        GameTextActionId mineId = GameTextActionId.LEGACY__SPICE_MINE_OPERATIONS__MINE;
        final String opponent = game.getOpponent(playerId);
        if (GameConditions.isOnceDuringYourTurn(game, self, playerId, gameTextSourceCardId, mineId)
                && GameConditions.canDrawDestiny(game, opponent)) {
            final int maxDraws = 1 + Filters.countTopLocationsOnTable(game, Filters.and(KESSEL_LOCATION, Filters.occupies(playerId)));
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, mineId);
            action.setText("'Mine'");
            action.setActionMsg("'Mine' (draw up to " + maxDraws + " destiny against opponent's destiny)");
            action.appendUsage(new OncePerTurnEffect(action));
            final PhysicalCard[] opponentsDestiny = new PhysicalCard[1];
            action.appendEffect(
                    new DrawDestinyEffect(action, opponent) {
                        @Override
                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                            if (!destinyCardDraws.isEmpty()) {
                                opponentsDestiny[0] = destinyCardDraws.get(0);
                            }
                        }
                    });
            action.appendEffect(
                    new PlayoutDecisionEffect(action, playerId,
                            new IntegerAwaitingDecision("Choose number of destiny to draw", 0, maxDraws, maxDraws) {
                                @Override
                                public void decisionMade(int result) throws DecisionResultInvalidException {
                                    if (result > 0 && opponentsDestiny[0] != null) {
                                        action.appendEffect(
                                                new DrawDestinyEffect(action, playerId, result) {
                                                    @Override
                                                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                        if (printedDestinyMatches(opponentsDestiny[0], destinyCardDraws)) {
                                                            game.getGameState().sendMessage("Printed destiny numbers match");
                                                            stackSpice(action, game, playerId, self, opponentsDestiny[0]);
                                                        }
                                                    }
                                                });
                                    }
                                }
                            }));
            actions.add(action);
        }
        return actions;
    }

    static boolean printedDestinyMatches(PhysicalCard opponentsDestiny, List<PhysicalCard> yourDestinies) {
        Float opponentsValue = opponentsDestiny.getBlueprint().getDestiny();
        if (opponentsValue == null) {
            return false;
        }
        for (PhysicalCard card : yourDestinies) {
            Float value = card.getBlueprint().getDestiny();
            if (value != null && Math.abs(value - opponentsValue) < 0.01) {
                return true;
            }
        }
        return false;
    }

    private static void stackSpice(final TopLevelGameTextAction action, SwccgGame game, String playerId, final PhysicalCard self, final PhysicalCard opponentsDestiny) {
        final GameState gameState = game.getGameState();
        final List<String> choices = new ArrayList<String>();
        choices.add("Stack opponent's destiny");
        if (!gameState.getLostPile(playerId).isEmpty()) {
            choices.add("Stack top card of your Lost Pile");
        }
        action.appendEffect(
                new PlayoutDecisionEffect(action, playerId,
                        new MultipleChoiceAwaitingDecision("Choose card to stack on " + GameUtils.getCardLink(self), choices.toArray(new String[0])) {
                            @Override
                            protected void validDecisionMade(int index, String result) {
                                final boolean lostPile = index == 1;
                                action.appendEffect(
                                        new PassthruEffect(action) {
                                            @Override
                                            protected void doPlayEffect(SwccgGame game) {
                                                PhysicalCard card = lostPile ? gameState.getTopOfLostPile(action.getPerformingPlayer()) : opponentsDestiny;
                                                if (card != null && Filters.onTable.accepts(game, self)) {
                                                    gameState.removeCardFromZone(card);
                                                    gameState.stackCard(card, self, false, false, false);
                                                    gameState.sendMessage(action.getPerformingPlayer() + " stacks " + GameUtils.getCardLink(card) + " on " + GameUtils.getCardLink(self));
                                                }
                                            }
                                        });
                            }
                        }));
    }
}
