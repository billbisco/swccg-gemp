package com.gempukku.swccgo.cards.set1220.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.RelocateFromLocationToWeatherVane;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
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
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RestoreCardToNormalEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Interrupt
 * Subtype: Used
 * Title: Slip Sliding Away
 */
public class Card1220_068 extends AbstractUsedInterrupt {
    public Card1220_068() {
        super(Side.DARK, 3, Title.Slip_Sliding_Away, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("Luke got the shaft.");
        setGameText("Cancel Path Of Least Resistance. (Immune to Sense.) OR During your move phase, target opponent's character at an interior Cloud City site. Draw destiny. Relocate target to Weather Vane if destiny > target's ability. OR Relocate your character at an interior Cloud City site to Weather Vane (if 'hit,' character is first restored to normal).");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        Filter interiorCloudCitySite = Filters.and(Filters.interior_site, Filters.Cloud_City_site);

        if (GameConditions.canTargetToCancel(game, self, Filters.Path_Of_Least_Resistance)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setImmuneTo(Title.Sense);
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Path_Of_Least_Resistance, Title.Path_Of_Least_Resistance);
            actions.add(action);
        }

        Filter opponentsCharacter = Filters.and(Filters.opponents(self), Filters.character, Filters.at(interiorCloudCitySite));
        if (GameConditions.isDuringYourPhase(game, playerId, Phase.MOVE)
                && GameConditions.canSpot(game, self, Filters.Weather_Vane)
                && GameConditions.canTarget(game, self, opponentsCharacter)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Relocate opponent's character to Weather Vane");
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose opponent's character", opponentsCharacter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            action.allowResponses("Draw destiny to relocate " + GameUtils.getCardLink(targetedCard) + " to Weather Vane",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);
                                            action.appendEffect(
                                                    new DrawDestinyEffect(action, playerId) {
                                                        @Override
                                                        protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                            return Collections.singletonList(finalTarget);
                                                        }

                                                        @Override
                                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                            GameState gameState = game.getGameState();
                                                            if (totalDestiny == null || finalTarget == null) {
                                                                gameState.sendMessage("Result: Failed due to failed destiny draw");
                                                                return;
                                                            }
                                                            float ability = game.getModifiersQuerying().getAbility(gameState, finalTarget);
                                                            gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                            gameState.sendMessage("Ability: " + GuiUtils.formatAsString(ability));
                                                            if (totalDestiny > ability) {
                                                                gameState.sendMessage("Result: Succeeded");
                                                                action.appendEffect(
                                                                        new RelocateFromLocationToWeatherVane(action, finalTarget));
                                                            }
                                                            else {
                                                                gameState.sendMessage("Result: Failed");
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

        Filter yourCharacter = Filters.and(Filters.your(self), Filters.character, Filters.at(interiorCloudCitySite));
        if (GameConditions.canSpot(game, self, Filters.Weather_Vane)
                && GameConditions.canTarget(game, self, yourCharacter)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Relocate your character to Weather Vane");
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose your character", yourCharacter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            action.allowResponses("Relocate " + GameUtils.getCardLink(targetedCard) + " to Weather Vane",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            PhysicalCard finalCharacter = action.getPrimaryTargetCard(targetGroupId);
                                            if (Filters.hit.accepts(game, finalCharacter)) {
                                                action.appendEffect(
                                                        new RestoreCardToNormalEffect(action, finalCharacter));
                                            }
                                            action.appendEffect(
                                                    new RelocateFromLocationToWeatherVane(action, finalCharacter));
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self) {
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Path_Of_Least_Resistance)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setImmuneTo(Title.Sense);
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }
}
