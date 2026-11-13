package com.gempukku.swccgo.cards.set205.dark;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.SetForRemainderOfGameDataEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.DeployAsCaptiveOption;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.ForRemainderOfGameData;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.SendMessageEffect;
import com.gempukku.swccgo.logic.effects.ShowCardOnScreenEffect;
import com.gempukku.swccgo.logic.effects.StackActionEffect;
import com.gempukku.swccgo.logic.effects.choose.StackCardFromOutsideDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotBeFlippedModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTransferredModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToLeaveTableResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 5
 * Type: Character
 * Subtype: Rebel
 * Title: Luke Skywalker, The Emperor's Prize
 */
public class Card205_014 extends AbstractRebel {
    public Card205_014() {
        super(Side.DARK, 0, 0, 0, 0, 0, "Luke Skywalker, The Emperor's Prize", Uniqueness.UNIQUE, ExpansionSet.SET_5, Rarity.V);
        setFrontOfDoubleSidedCard(true);
        // Wording and play of the current PC card image (E1, 16 August 2023); the PC card data still has the 2018 text.
        setGameText("Luke Skywalker, The Emperor's Prize is a Dark Side card and is a persona of Luke only while on table. Plays only during start of game by revealing from outside your deck if Your Destiny is on table and suspended; deploy to Throne Room as a frozen captive. If not revealed, place under your Starting Effect. While this side up, Bring Him Before Me may not flip. May not be escorted. Flip this card if Vader here and not escorting a captive. Place out of play if released or about to leave table.");
        setDoesNotCountTowardDeckLimit(true);
        addPersona(Persona.LUKE);
        setCharacterPersonaOnlyWhileOnTable(true);
        addIcons(Icon.VIRTUAL_SET_5);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        // Plays only during start of game, from outside your deck.
        return self.getZone() == Zone.OUTSIDE_OF_DECK && GameConditions.isDuringStartOfGame(game);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredOutsideOfDeckAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        final String playerId = self.getOwner();

        // If not revealed, place under your Starting Effect.
        if (TriggerConditions.isStartingLocationsAndObjectivesCompletedStep(game, effectResult)) {
            final PhysicalCard startingEffect = Filters.findFirstFromAllOnTable(game, Filters.and(Filters.your(self), Filters.Starting_Effect));
            if (startingEffect != null) {
                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Place under " + GameUtils.getCardLink(startingEffect));
                action.setActionMsg("Place " + GameUtils.getCardLink(self) + " under " + GameUtils.getCardLink(startingEffect));
                // Perform result(s)
                action.appendEffect(
                        new StackCardFromOutsideDeckEffect(action, playerId, startingEffect, self));
                return Collections.singletonList(action);
            }
            return null;
        }

        // Plays only during start of game by revealing from outside your deck if Your Destiny is on table and suspended;
        // deploy to Throne Room as a frozen captive.
        if (GameConditions.isDuringStartOfGame(game)
                && !GameConditions.cardHasAnyForRemainderOfGameDataSet(self)
                && TriggerConditions.isTableChanged(game, effectResult)) {
            final PhysicalCard throneRoom = Filters.findFirstFromTopLocationsOnTable(game, Filters.Throne_Room);
            if (throneRoom != null
                    && GameConditions.canSpot(game, self, SpotOverride.INCLUDE_SUSPENDED, Filters.and(Filters.Your_Destiny, Filters.suspended))) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Reveal and deploy");
                action.setActionMsg(null);
                action.skipInitialMessageAndAnimation();
                // Perform result(s)
                action.appendEffect(
                        new PlayoutDecisionEffect(action, playerId,
                                new YesNoDecision("Reveal " + GameUtils.getCardLink(self) + " and deploy it to " + GameUtils.getCardLink(throneRoom) + " as a frozen captive?") {
                                    @Override
                                    protected void yes() {
                                        action.appendEffect(
                                                new SendMessageEffect(action, playerId + " reveals " + GameUtils.getCardLink(self)));
                                        action.appendEffect(
                                                new ShowCardOnScreenEffect(action, self));
                                        action.appendEffect(
                                                new PassthruEffect(action) {
                                                    @Override
                                                    protected void doPlayEffect(SwccgGame game) {
                                                        PlayCardAction deployAction = self.getBlueprint().getPlayCardAction(playerId, game, self, self, true, 0, null, null,
                                                                DeployAsCaptiveOption.deployAsUnattendedFrozenCaptive(), null, null, false, 0, Filters.sameLocationId(throneRoom), null);
                                                        if (deployAction != null) {
                                                            action.appendEffect(
                                                                    new StackActionEffect(action, deployAction));
                                                        }
                                                    }
                                                });
                                    }

                                    @Override
                                    protected void no() {
                                        // Not revealed: it is placed under the Starting Effect when the start of game step completes.
                                        action.appendEffect(
                                                new SetForRemainderOfGameDataEffect(action, self, new ForRemainderOfGameData()));
                                    }
                                }));
                return Collections.singletonList(action);
            }
        }

        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileInactiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotBeFlippedModifier(self, Filters.Bring_Him_Before_Me));
        modifiers.add(new MayNotBeTransferredModifier(self));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggersAlwaysWhenInPlay(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isAboutToLeaveTable(game, effectResult, self)
                && !TriggerConditions.isAboutToBePlacedOutOfPlayFromTable(game, effectResult, self)) {
            final AboutToLeaveTableResult result = (AboutToLeaveTableResult) effectResult;

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place out of play");
            action.setActionMsg("Place " + GameUtils.getCardLink(self) + " out of play");
            // Perform result(s)
            action.appendEffect(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            result.getPreventableCardEffect().preventEffectOnCard(self);
                            for (PhysicalCard attachedCards : game.getGameState().getAllAttachedRecursively(self)) {
                                result.getPreventableCardEffect().preventEffectOnCard(attachedCards);
                            }
                        }
                    });
            action.appendEffect(
                    new PlaceCardOutOfPlayFromTableEffect(action, self));
            return Collections.singletonList(action);
        }

        // Check condition(s)
        if (TriggerConditions.released(game, effectResult, self)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place out of play");
            action.setActionMsg("Place " + GameUtils.getCardLink(self) + " out of play");
            // Perform result(s)
            action.appendEffect(
                    new PlaceCardOutOfPlayFromTableEffect(action, self));
            return Collections.singletonList(action);
        }

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canSpot(game, self, Filters.and(Filters.Vader, Filters.here(self), Filters.not(Filters.escort)))) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Flip");
            action.setActionMsg(null);
            // Perform result(s)
            action.appendEffect(
                    new FlipCardEffect(action, self));
            return Collections.singletonList(action);
        }

        return null;
    }
}
