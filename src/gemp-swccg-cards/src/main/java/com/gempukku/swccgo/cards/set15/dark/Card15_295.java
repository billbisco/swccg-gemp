package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.set3.dark.Card3_129;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.TriggeringResultEffect;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.results.TrampleDestinySuccessfulResult;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Card15_295 extends Card3_129 {
    public Card15_295() {
        setCardSubtype(CardSubtype.USED);
        setAlternateDestiny(3);
        setLore("");
        setGameText("Target a character, 'crashed' vehicle or unpiloted vehicle with maneuver at same exterior site as one of your piloted AT-ATs or AT-STs. Draw destiny. Character lost if destiny > ability. Vehicle lost if destiny < 7. OR Cancel Lightsaber Proficiency. OR Target a character with ability < 4 using a lightsaber in a battle or a Force drain. Draw destiny. Target lost if destiny > ability. Lightsaber lost if destiny = ability.");
        setCredits(0);
        setAsHorizontal(false);
        for (Icon icon : Icon.values()) {
            if (icon.isExpansionIcon() || icon == Icon.EPISODE_I) addIcon(icon, 0);
        }
        addIcons(Icon.REFLECTIONS_GOLD);
        addComboCardTitles("Trample", "Lightsaber Deficiency");
    }
    @Override public String getTitle() { return "Trample & Lightsaber Deficiency"; }
    @Override public Float getDestiny() { return 3f; }
    @Override public Uniqueness getUniqueness() { return Uniqueness.UNRESTRICTED; }
    @Override public ExpansionSet getExpansionSet() { return ExpansionSet.REFLECTIONS_GOLD; }
    @Override public Rarity getRarity() { return Rarity.UNKNOWN; }

    private List<PlayInterruptAction> getGameTextTopLevelActionsFromCombinedCard(final String playerId, SwccgGame game, final PhysicalCard self) {
        Filter dewbackMaySubstitute = Filters.and(Filters.Dewback, Filters.hasGameTextModification(ModifyGameTextType.LEGACY__TRAMPLE__DEWBACK_MAY_SUBSTITUTE_FOR_PILOTED_ATAT));
        Filter trampler = Filters.and(Filters.your(self), Filters.or(Filters.and(Filters.piloted, Filters.or(Filters.AT_AT, Filters.AT_ST)), dewbackMaySubstitute));
        Filter filter = Filters.and(Filters.or(Filters.character, Filters.crashed_vehicle,
                Filters.and(Filters.unpiloted, Filters.vehicle, Filters.hasManeuver)),
                Filters.at(Filters.and(Filters.exterior_site, Filters.sameSiteAs(self, trampler))));

        TargetingReason targetingReason = TargetingReason.TO_BE_LOST;

        // Check condition(s)
        if (GameConditions.canTarget(game, self, targetingReason, filter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Make character or vehicle lost");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose character or vehicle", targetingReason, filter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Make " + GameUtils.getCardLink(targetedCard) + " lost",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new DrawDestinyEffect(action, playerId) {
                                                        @Override
                                                        protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                            return Collections.singletonList(finalTarget);
                                                        }
                                                        @Override
                                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                            GameState gameState = game.getGameState();
                                                            if (totalDestiny == null) {
                                                                gameState.sendMessage("Result: Failed due to failed destiny draw");
                                                                return;
                                                            }

                                                            gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                            if (Filters.character.accepts(game, finalTarget)) {

                                                                float ability = game.getModifiersQuerying().getAbility(game.getGameState(), finalTarget, self);
                                                                gameState.sendMessage("Ability: " + GuiUtils.formatAsString(ability));
                                                                if (totalDestiny > ability) {
                                                                    gameState.sendMessage("Result: Succeeded");
                                                                    action.appendEffect(
                                                                            new TriggeringResultEffect(action,
                                                                                    new TrampleDestinySuccessfulResult(playerId)));
                                                                    action.appendEffect(
                                                                            new LoseCardFromTableEffect(action, finalTarget));
                                                                }
                                                                else {
                                                                    gameState.sendMessage("Result: Failed");
                                                                }
                                                            }
                                                            else if (Filters.vehicle.accepts(game, finalTarget)) {
                                                                if (totalDestiny < 7) {
                                                                    gameState.sendMessage("Result: Succeeded");
                                                                    action.appendEffect(
                                                                            new TriggeringResultEffect(action,
                                                                                    new TrampleDestinySuccessfulResult(playerId)));
                                                                    action.appendEffect(
                                                                            new LoseCardFromTableEffect(action, finalTarget));
                                                                }
                                                                else {
                                                                    gameState.sendMessage("Result: Failed");
                                                                }
                                                            }
                                                            else {
                                                                gameState.sendMessage("Result: No result");
                                                            }
                                                        }
                                                    });
                                        }
                                    });
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> result = new java.util.ArrayList<>();
        List<PlayInterruptAction> first = super.getGameTextTopLevelActions(playerId, game, self);
        if (first != null) result.addAll(first);
        List<PlayInterruptAction> second = getGameTextTopLevelActionsFromCombinedCard(playerId, game, self);
        if (second != null) result.addAll(second);
        return result;
    }
}
