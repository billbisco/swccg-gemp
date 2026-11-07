package com.gempukku.swccgo.cards.set1240.dark;

import com.gempukku.swccgo.cards.AbstractEpicEventDeployable;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
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
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.HitCardEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.RelocateBetweenLocationsEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardsOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardIntoHandFromUsedPileEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromUsedPileEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployAsLandedToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MovesForFreeModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 4
 * Type: Epic Event
 * Title: If The Trace Was Correct (V)
 */
public class Card1240_040 extends AbstractEpicEventDeployable {
    public Card1240_040() {
        super(Side.DARK, 0, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "If The Trace Was Correct", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_4, Rarity.V);
        setVirtualSuffix(true);
        setGameText("Deploy on table. Instead of firing Galen's Lightsaber, may 'throw' it to Used Pile: draw destiny; choose up to half that number (round up) of opponent's ability < 3 characters present to be hit. Rogue Shadow may deploy to sites, moves for free (unless relocating), is immune to attrition < 4, and you may use 3 Force to relocate it to a battleground site (as a regular move during your move phase). If it just moved to a site opponent occupies, may take top card of Used Pile into hand (or if it just landed, any one card in Used Pile; reshuffle).");
        addIcons(Icon.TATOOINE);
    }

    private static Filter rogueShadow(PhysicalCard self) {
        return Filters.and(Filters.your(self), Filters.title(Title.Rogue_Shadow));
    }

    private static Filter galensLightsaber(PhysicalCard self) {
        return Filters.and(Filters.your(self), Filters.titleContains("Galen's Lightsaber"));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Filter rogueShadow = rogueShadow(self);
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployAsLandedToLocationModifier(self, rogueShadow, Filters.site));
        modifiers.add(new MovesForFreeModifier(self, rogueShadow));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, rogueShadow, 4));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();
        final String opponent = game.getOpponent(playerId);

        // Instead of firing Galen's Lightsaber, may 'throw' it to Used Pile.
        final Filter throwableLightsaber = Filters.and(galensLightsaber(self),
                Filters.attachedTo(Filters.participatingInBattle), Filters.canBeFired(self, 0));
        if (GameConditions.isDuringBattle(game)
                && GameConditions.canSpot(game, self, throwableLightsaber)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, GameTextActionId.LEGACY__IF_THE_TRACE_WAS_CORRECT_V__THROW_LIGHTSABER);
            action.setText("'Throw' Galen's Lightsaber");
            action.appendTargeting(
                    new ChooseCardOnTableEffect(action, playerId, "Choose lightsaber to 'throw'", throwableLightsaber) {
                        @Override
                        protected boolean getUseShortcut() {
                            return true;
                        }

                        @Override
                        protected void cardSelected(final PhysicalCard lightsaber) {
                            final PhysicalCard wielder = lightsaber.getAttachedTo();
                            final PhysicalCard location = game.getModifiersQuerying().getLocationThatCardIsPresentAt(game.getGameState(), wielder);
                            action.setActionMsg("'Throw' " + GameUtils.getCardLink(lightsaber));
                            action.appendCost(
                                    new PlaceCardInUsedPileFromTableEffect(action, lightsaber));
                            action.appendEffect(
                                    new DrawDestinyEffect(action, playerId) {
                                        @Override
                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                            GameState gameState = game.getGameState();
                                            if (totalDestiny == null) {
                                                gameState.sendMessage("Result: Failed due to failed destiny draw");
                                                return;
                                            }
                                            int numToHit = (int) Math.ceil(Math.max(0, totalDestiny) / 2f);
                                            gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny) + "; may choose up to " + numToHit + " character(s) to be hit");
                                            if (numToHit < 1 || location == null) {
                                                return;
                                            }
                                            Filter targets = Filters.and(Filters.owner(opponent), Filters.character,
                                                    Filters.abilityLessThan(3), Filters.present(location));
                                            if (!Filters.canSpot(game, self, targets)) {
                                                return;
                                            }
                                            action.appendEffect(
                                                    new ChooseCardsOnTableEffect(action, playerId, "Choose up to " + numToHit + " character(s) to be hit", 0, numToHit, targets) {
                                                        @Override
                                                        protected void cardsSelected(Collection<PhysicalCard> targetedCards) {
                                                            for (PhysicalCard target : targetedCards) {
                                                                action.appendEffect(
                                                                        new HitCardEffect(action, target, lightsaber));
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
            actions.add(action);
        }

        // You may use 3 Force to relocate Rogue Shadow to a battleground site (as a regular move during your move phase).
        final PhysicalCard rogue = Filters.findFirstActive(game, self, rogueShadow(self));
        if (rogue != null
                && GameConditions.isDuringYourPhase(game, self, Phase.MOVE)
                && GameConditions.canUseForce(game, playerId, 3)) {
            final Filter battlegroundSite = Filters.and(Filters.battleground_site, Filters.not(Filters.sameLocation(rogue)),
                    Filters.locationCanBeRelocatedTo(rogue, false, false, true, 0, false));
            if (GameConditions.canSpot(game, self, battlegroundSite)) {
                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, GameTextActionId.LEGACY__IF_THE_TRACE_WAS_CORRECT_V__RELOCATE_ROGUE_SHADOW);
                action.setText("Relocate Rogue Shadow");
                action.appendTargeting(
                        new ChooseCardOnTableEffect(action, playerId, "Choose battleground site to relocate " + GameUtils.getCardLink(rogue) + " to", battlegroundSite) {
                            @Override
                            protected void cardSelected(final PhysicalCard siteSelected) {
                                action.addAnimationGroup(rogue);
                                action.addAnimationGroup(siteSelected);
                                // "Moves for free (unless relocating)": this relocation always costs 3 Force.
                                action.appendCost(
                                        new UseForceEffect(action, playerId, 3));
                                action.allowResponses("Relocate " + GameUtils.getCardLink(rogue) + " to " + GameUtils.getCardLink(siteSelected),
                                        new UnrespondableEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                action.appendEffect(
                                                        new RelocateBetweenLocationsEffect(action, rogue, siteSelected, true));
                                            }
                                        }
                                );
                            }
                        }
                );
                actions.add(action);
            }
        }
        return actions;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);
        Filter siteOpponentOccupies = Filters.and(Filters.site, Filters.occupies(opponent));
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__IF_THE_TRACE_WAS_CORRECT_V__TAKE_CARD_FROM_USED_PILE;

        if (TriggerConditions.justLandedAt(game, effectResult, rogueShadow(self), siteOpponentOccupies)
                && GameConditions.hasUsedPile(game, playerId)) {
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card from Used Pile into hand");
            action.setActionMsg("Take a card from Used Pile into hand");
            action.appendEffect(
                    new TakeCardIntoHandFromUsedPileEffect(action, playerId, true));
            return Collections.singletonList(action);
        }
        if (TriggerConditions.movedToLocation(game, effectResult, rogueShadow(self), siteOpponentOccupies)
                && GameConditions.hasUsedPile(game, playerId)) {
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take top card of Used Pile into hand");
            action.setActionMsg("Take top card of Used Pile into hand");
            action.appendEffect(
                    new DrawCardIntoHandFromUsedPileEffect(action, playerId));
            return Collections.singletonList(action);
        }
        return null;
    }
}
