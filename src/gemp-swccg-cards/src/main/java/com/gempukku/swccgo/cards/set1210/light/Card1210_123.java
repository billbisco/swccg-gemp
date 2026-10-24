package com.gempukku.swccgo.cards.set1210.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.CardSubtype;
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
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployStackedCardEffect;
import com.gempukku.swccgo.logic.effects.choose.StackCardsFromOutsideDeckEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 1
 * Type: Effect
 * Title: Hidden Fortress
 */
public class Card1210_123 extends AbstractNormalEffect {
    public Card1210_123() {
        super(Side.LIGHT, 0, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Hidden Fortress", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_1, Rarity.V);
        setGameText("Deploy on table (only at start of game) with up to three systems from outside your deck face-down under here. If non-[Virtual Block 4] Hidden Base on table: Once per game, may deploy a system from here and/or exchange Hidden Base indicator with a system (from parsec 1 to 8) under here. (Immune to Alter.)");
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return GameConditions.isDuringStartOfGame(game);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();

        if (TriggerConditions.justDeployed(game, effectResult, self)) {
            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Stack systems from outside deck");
            action.setActionMsg("Stack up to three systems from outside your deck face-down here");
            action.appendEffect(
                    new StackCardsFromOutsideDeckEffect(action, playerId, 0, 3, self, true, Filters.system));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__HIDDEN_FORTRESS__DEPLOY_SYSTEM_AND_OR_EXCHANGE_INDICATOR;
        Filter stackedSystem = stackedSystemFilter();
        Filter parsecSystem = stackedParsecSystemFilter();
        final PhysicalCard hiddenBase = findNonVb4HiddenBase(game);

        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && hiddenBase != null) {
            final boolean canDeploy = !game.getGameState().getStackedCards(self).isEmpty();
            final boolean canExchange = !game.getGameState().getStackedCards(hiddenBase).isEmpty()
                    && hasStackedMatching(game, self, parsecSystem);

            if (canDeploy || canExchange) {
                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
                action.setText("Deploy system and/or exchange indicator");
                action.appendUsage(
                        new OncePerGameEffect(action));

                if (canDeploy && canExchange) {
                    action.appendEffect(
                            new PlayoutDecisionEffect(action, playerId,
                                    new MultipleChoiceAwaitingDecision("Choose", new String[]{"Deploy a system", "Exchange Hidden Base indicator", "Both"}) {
                                        @Override
                                        protected void validDecisionMade(int index, String result) {
                                            if (index == 0 || index == 2) {
                                                action.appendEffect(
                                                        new DeployStackedCardEffect(action, self, stackedSystem, true, false));
                                            }
                                            if (index == 1 || index == 2) {
                                                appendExchange(action, playerId, self, hiddenBase, parsecSystem);
                                            }
                                        }
                                    }));
                } else if (canDeploy) {
                    action.appendEffect(
                            new DeployStackedCardEffect(action, self, stackedSystem, true, false));
                } else {
                    appendExchange(action, playerId, self, hiddenBase, parsecSystem);
                }
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    private Filter stackedSystemFilter() {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return physicalCard.getBlueprint().getCardCategory() == CardCategory.LOCATION
                        && physicalCard.getBlueprint().getCardSubtype() == CardSubtype.SYSTEM;
            }
        };
    }

    private Filter stackedParsecSystemFilter() {
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

    private boolean hasStackedMatching(SwccgGame game, PhysicalCard self, Filter filter) {
        for (PhysicalCard stacked : game.getGameState().getStackedCards(self)) {
            if (filter.accepts(game, stacked)) {
                return true;
            }
        }
        return false;
    }

    private PhysicalCard findNonVb4HiddenBase(SwccgGame game) {
        Filter nonVb4HiddenBase = Filters.and(Filters.or(Filters.Hidden_Base, Filters.titleContains("Hidden Base")), Filters.not(Filters.icon(Icon.LEGACY_BLOCK_4)));
        PhysicalCard hiddenBase = Filters.findFirstFromAllOnTable(game, nonVb4HiddenBase);
        if (hiddenBase != null) {
            return hiddenBase;
        }
        for (PhysicalCard card : game.getGameState().getCardsInPlay()) {
            if (card.getBlueprint().getIconCount(Icon.LEGACY_BLOCK_4) > 0) {
                continue;
            }
            String title = card.getTitle();
            if (title != null && title.toLowerCase().contains("hidden base")) {
                return card;
            }
            for (String extraTitle : card.getTitles()) {
                if (extraTitle != null && extraTitle.toLowerCase().contains("hidden base")) {
                    return card;
                }
            }
        }
        return null;
    }

    private void appendExchange(final TopLevelGameTextAction action, final String playerId, final PhysicalCard self, final PhysicalCard hiddenBase, Filter parsecSystem) {
        action.appendEffect(
                new ChooseStackedCardEffect(action, playerId, self, parsecSystem, false) {
                    @Override
                    public String getChoiceText(int numCardsToChoose) {
                        return "Choose a system (parsec 1 to 8) to become the Hidden Base indicator";
                    }

                    @Override
                    protected boolean getUseShortcut() {
                        return true;
                    }

                    @Override
                    protected void cardSelected(final PhysicalCard fortressSystem) {
                        action.addAnimationGroup(fortressSystem);
                        action.appendEffect(
                                new PassthruEffect(action) {
                                    @Override
                                    protected void doPlayEffect(SwccgGame game) {
                                        Collection<PhysicalCard> indicators = game.getGameState().getStackedCards(hiddenBase);
                                        if (indicators.isEmpty()) {
                                            return;
                                        }
                                        PhysicalCard indicator = indicators.iterator().next();
                                        game.getGameState().sendMessage(playerId + " exchanges Hidden Base indicator with " + GameUtils.getCardLink(fortressSystem));
                                        game.getGameState().relocateCardAsStacked(fortressSystem, hiddenBase, true, true);
                                        game.getGameState().relocateCardAsStacked(indicator, self, true, true);
                                    }
                                });
                    }
                });
    }
}
