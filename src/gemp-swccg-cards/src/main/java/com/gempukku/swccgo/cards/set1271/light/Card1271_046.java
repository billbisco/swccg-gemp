package com.gempukku.swccgo.cards.set1271.light;

import com.gempukku.swccgo.cards.AbstractMission;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.cards.evaluators.OccupiesEvaluator;
import com.gempukku.swccgo.cards.evaluators.OnTableEvaluator;
import com.gempukku.swccgo.common.DestinyType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.DrawDestinyState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.PutStackedCardsInUsedPileEffect;
import com.gempukku.swccgo.logic.effects.choose.StackOneCardFromPileEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.TotalPowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.ForceDrainInitiatedResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 7.1
 * Type: Mission
 * Subtype: Critical
 * Title: Restore Freedom To The Galaxy
 */
public class Card1271_046 extends AbstractMission {
    public Card1271_046() {
        super(Side.LIGHT, 0, PlayCardZoneOption.ATTACHED, "Restore Freedom To The Galaxy", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_1, Rarity.V, true);
        setGameText("Deploy on Yavin 4. Once per turn, may deploy a battleground system or an interior Yavin 4 site. Whenever you Force drain at another battleground system, may draw destiny. Add 1 for each Yavin 4 site on table (2 if you occupy). If total destiny > 6, stack top card from either player's Reserve Deck there (limit one per system) to 'liberate' the system. At 'liberated' systems: Force drains are +1, and at same and related locations your total power is +3 and you draw one battle destiny if not able to otherwise. If opponent Force drains at a 'liberated' system, place stacked card in owner's Used Pile.");
        addIcons(Icon.SETUP, Icon.DEATH_STAR_II);
        addKeywords(Keyword.DEPLOYS_ON_LOCATION);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Yavin_4_system;
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedTo(final SwccgGame game, final PhysicalCard self) {
        return Filters.Yavin_4_system;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Filter liberatedSameAndRelated = Filters.or(Filters.liberated_system, Filters.relatedLocationTo(self, Filters.liberated_system));
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, Filters.liberated_system, 1, playerId));
        modifiers.add(new TotalPowerModifier(self, liberatedSameAndRelated, 3, playerId));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, Filters.and(Filters.your(self), Filters.at(liberatedSameAndRelated)), 1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.RESTORE_FREEDOM__DEPLOY_LOCATION;
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy battleground system or interior Yavin 4 site");
            action.appendUsage(new OncePerTurnEffect(action));
            action.appendEffect(new DeployCardFromReserveDeckEffect(action, Filters.or(Filters.battleground_system, Filters.and(Filters.interior_site, Filters.Yavin_4_site)), true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.forceDrainInitiatedBy(game, effectResult, playerId, Filters.and(Filters.battleground_system, Filters.except(Filters.or(Filters.Yavin_4_system, Filters.liberated_system))))
                && GameConditions.canDrawDestiny(game, playerId)) {
            final PhysicalCard system = ((ForceDrainInitiatedResult) effectResult).getLocation();
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Draw destiny to liberate");
            action.appendEffect(
                    new DrawDestinyEffect(action, playerId, 1, DestinyType.DESTINY) {
                        @Override
                        protected List<Modifier> getDrawDestinyModifiers(SwccgGame game, DrawDestinyState drawDestinyState) {
                            Modifier addSites = new TotalDestinyModifier(self, drawDestinyState.getId(),
                                    new OnTableEvaluator(self, Filters.Yavin_4_site));
                            Modifier extraIfOccupy = new TotalDestinyModifier(self, drawDestinyState.getId(),
                                    new OccupiesEvaluator(playerId, Filters.Yavin_4_site));
                            List<Modifier> modifiers = new LinkedList<Modifier>();
                            modifiers.add(addSites);
                            modifiers.add(extraIfOccupy);
                            return modifiers;
                        }

                        @Override
                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                            GameState gameState = game.getGameState();
                            if (totalDestiny == null || totalDestiny <= 6) {
                                gameState.sendMessage("Result: Failed");
                                return;
                            }
                            if (Filters.hasStacked(Filters.any).accepts(game, system)) {
                                gameState.sendMessage("Result: System already has a stacked card");
                                return;
                            }
                            gameState.sendMessage("Result: Succeeded");
                            action.appendEffect(
                                    new PassthruEffect(action) {
                                        @Override
                                        protected void doPlayEffect(SwccgGame game) {
                                            PhysicalCard fromYours = game.getGameState().getTopOfReserveDeck(playerId);
                                            final PhysicalCard top = (fromYours != null) ? fromYours : game.getGameState().getTopOfReserveDeck(game.getOpponent(playerId));
                                            if (top != null) {
                                                action.appendEffect(new StackOneCardFromPileEffect(action, playerId, top, system, false, false, false));
                                                action.appendEffect(new PassthruEffect(action) {
                                                    @Override
                                                    protected void doPlayEffect(SwccgGame game) {
                                                        top.setLiberationCard(true);
                                                    }
                                                });
                                            }
                                        }
                                    });
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        final String opponent = game.getOpponent(self.getOwner());
        if (TriggerConditions.forceDrainInitiatedBy(game, effectResult, opponent, Filters.liberated_system)) {
            final PhysicalCard system = ((ForceDrainInitiatedResult) effectResult).getLocation();
            Collection<PhysicalCard> liberationCards = Filters.filterStacked(game, Filters.and(Filters.liberationCard, Filters.stackedOn(system)));
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place liberation card in Used Pile");
            action.appendEffect(new PutStackedCardsInUsedPileEffect(action, opponent, liberationCards, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
