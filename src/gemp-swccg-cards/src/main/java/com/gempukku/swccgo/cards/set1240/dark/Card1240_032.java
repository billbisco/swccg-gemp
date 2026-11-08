package com.gempukku.swccgo.cards.set1240.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.ReactActionOption;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.modifiers.ExtraForceCostToFlipBluffCardModifier;
import com.gempukku.swccgo.logic.modifiers.IconModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 4
 * Type: Effect
 * Title: Quietly Observing
 */
public class Card1240_032 extends AbstractNormalEffect {
    public Card1240_032() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Quietly_Observing, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_4, Rarity.V);
        setLore("On her assignment to kill Sharad Hett, Aurra used her patience and cunning to help track down the Jedi Master.");
        setGameText("Deploy on table. Once per game, may deploy Bluffs from Reserve Deck; reshuffle. Bluffs gains one [Light Side] and one [Dark Side]. Once per battle, may deploy a bluff card (if it is a character or vehicle) as a 'react.' To reveal a bluff card, opponent must first use X Force, where X = 4 - number of bluff cards. (Immune to Alter.)");
        addIcons(Icon.EPISODE_I, Icon.TATOOINE);
        setVirtualSuffix(true);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new IconModifier(self, Filters.Bluffs, Icon.LIGHT_FORCE, 1));
        modifiers.add(new IconModifier(self, Filters.Bluffs, Icon.DARK_FORCE, 1));
        modifiers.add(new ExtraForceCostToFlipBluffCardModifier(self, Filters.Bluffs, null,
                new BaseEvaluator() {
                    @Override
                    public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard location) {
                        return 4 - Filters.filter(gameState.getStackedCards(location), gameState.getGame(), Filters.bluffCard).size();
                    }
                }, opponent));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__QUIETLY_OBSERVING_V__DEPLOY_BLUFFS;
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Title.Bluffs)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Bluffs from Reserve Deck");
            action.appendUsage(
                    new OncePerGameEffect(action));
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.Bluffs, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    /** Your 'bluff cards' that are characters or vehicles. */
    private static Filter bluffCharactersAndVehicles(PhysicalCard self) {
        return Filters.and(Filters.owner(self.getOwner()), Filters.bluffCard, Filters.or(Filters.character, Filters.vehicle));
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__QUIETLY_OBSERVING_V__DEPLOY_BLUFF_CARD_AS_REACT;
        String opponent = game.getOpponent(playerId);
        if (!TriggerConditions.battleInitiated(game, effectResult, opponent)
                || !GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId))
            return null;
        final PhysicalCard battleLocation = game.getGameState().getBattleLocation();
        if (battleLocation == null)
            return null;
        final Filter targetFilter = Filters.locationAndCardsAtLocation(Filters.sameCardId(battleLocation));
        final List<PhysicalCard> deployable = new LinkedList<>();
        for (PhysicalCard bluffs : Filters.filterTopLocationsOnTable(game, Filters.Bluffs)) {
            for (PhysicalCard stacked : game.getGameState().getStackedCards(bluffs)) {
                if (bluffCharactersAndVehicles(self).accepts(game, stacked)
                        && Filters.deployableToLocation(self, Filters.sameCardId(battleLocation), false, 0).accepts(game, stacked)) {
                    deployable.add(stacked);
                }
            }
        }
        if (deployable.isEmpty())
            return null;

        final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, gameTextActionId);
        action.setText("Deploy a 'bluff card' as a 'react'");
        action.appendUsage(
                new OncePerBattleEffect(action));
        action.appendEffect(
                new ChooseCardEffect(action, playerId, "Choose 'bluff card' to deploy as a 'react'", deployable) {
                    @Override
                    protected void cardSelected(final PhysicalCard bluffCard) {
                        final ReactActionOption react = new ReactActionOption(self, false, 0, false, "Deploy as a 'react'",
                                Filters.sameCardId(bluffCard), targetFilter, null, true);
                        action.appendEffect(
                                new PassthruEffect(action) {
                                    @Override
                                    protected void doPlayEffect(SwccgGame game) {
                                        PlayCardAction play = bluffCard.getBlueprint().getPlayCardAction(playerId, game, bluffCard, self, false, 0, null, null, null, react, null, false, 0, targetFilter, null);
                                        if (play != null) {
                                            game.getActionsEnvironment().addActionToStack(play);
                                        }
                                    }
                                }
                        );
                    }
                });
        return Collections.singletonList(action);
    }
}
