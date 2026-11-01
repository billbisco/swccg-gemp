package com.gempukku.swccgo.cards.set1106.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
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
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployStackedCardEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotBeCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 6: City In The Clouds
 * Type: Effect
 * Title: Rebel Planners
 */
public class Card1106_018 extends AbstractNormalEffect {
    public Card1106_018() {
        super(Side.LIGHT, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Rebel_Planners, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_6, Rarity.V);
        setVirtualSuffix(true);
        setLore("Rebel strategists worked under master tactician General Dodonna. They devised an unorthodox battle plan to destroy the Death Star at the Battle of Yavin.");
        setGameText("Deploy on table. If Hidden Base on table: once per turn, may deploy a system from under your Starting Effect; and, once per game, may exchange Hidden Base indicator with a system (from parsec 1 to 8) under your Starting Effect. May not be canceled. (Immune to Alter.)");
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotBeCanceledModifier(self, self));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();
        final PhysicalCard hiddenBase = Filters.findFirstFromAllOnTable(game, Filters.Hidden_Base);
        final PhysicalCard startingEffect = Filters.findFirstActive(game, self, Filters.and(Filters.your(self), Filters.Starting_Effect));
        if (hiddenBase == null || startingEffect == null) {
            return null;
        }
        final Filter system = systemFilter();
        final Filter parsecSystem = parsecOneToEightSystemFilter();

        GameTextActionId deployId = GameTextActionId.OVS__REBEL_PLANNERS__DEPLOY_SYSTEM_FROM_STARTING_EFFECT;
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, deployId)
                && hasStacked(game, startingEffect, system)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, deployId);
            action.setText("Deploy system from under Starting Effect");
            action.setActionMsg("Deploy a system from under " + GameUtils.getCardLink(startingEffect));
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendEffect(
                    new DeployStackedCardEffect(action, startingEffect, system, true, false));
            actions.add(action);
        }

        GameTextActionId exchangeId = GameTextActionId.OVS__REBEL_PLANNERS__EXCHANGE_HIDDEN_BASE_INDICATOR;
        if (GameConditions.isOncePerGame(game, self, exchangeId)
                && !game.getGameState().getStackedCards(hiddenBase).isEmpty()
                && hasStacked(game, startingEffect, parsecSystem)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, exchangeId);
            action.setText("Exchange Hidden Base indicator");
            action.setActionMsg("Exchange Hidden Base indicator with a system under " + GameUtils.getCardLink(startingEffect));
            action.appendUsage(
                    new OncePerGameEffect(action));
            action.appendEffect(
                    new ChooseStackedCardEffect(action, playerId, startingEffect, parsecSystem, false) {
                        @Override
                        public String getChoiceText(int numCardsToChoose) {
                            return "Choose a system (parsec 1 to 8) to become the Hidden Base indicator";
                        }

                        @Override
                        protected boolean getUseShortcut() {
                            return true;
                        }

                        @Override
                        protected void cardSelected(final PhysicalCard newIndicator) {
                            action.addAnimationGroup(newIndicator);
                            action.appendEffect(
                                    new PassthruEffect(action) {
                                        @Override
                                        protected void doPlayEffect(SwccgGame game) {
                                            Collection<PhysicalCard> indicators = game.getGameState().getStackedCards(hiddenBase);
                                            if (indicators.isEmpty()) {
                                                return;
                                            }
                                            PhysicalCard oldIndicator = indicators.iterator().next();
                                            game.getGameState().sendMessage(playerId + " exchanges Hidden Base indicator with " + GameUtils.getCardLink(newIndicator));
                                            game.getGameState().relocateCardAsStacked(newIndicator, hiddenBase, true, true);
                                            game.getGameState().relocateCardAsStacked(oldIndicator, startingEffect, true, true);
                                        }
                                    });
                        }
                    });
            actions.add(action);
        }
        return actions;
    }

    private static Filter systemFilter() {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return physicalCard.getBlueprint().getCardSubtype() == CardSubtype.SYSTEM;
            }
        };
    }

    private static Filter parsecOneToEightSystemFilter() {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                if (physicalCard.getBlueprint().getCardSubtype() != CardSubtype.SYSTEM) {
                    return false;
                }
                int parsec = physicalCard.getBlueprint().getParsec();
                return parsec >= 1 && parsec <= 8;
            }
        };
    }

    private static boolean hasStacked(SwccgGame game, PhysicalCard stackedOn, Filter filter) {
        for (PhysicalCard stacked : game.getGameState().getStackedCards(stackedOn)) {
            if (filter.accepts(game, stacked)) {
                return true;
            }
        }
        return false;
    }
}
