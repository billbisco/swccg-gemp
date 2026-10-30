package com.gempukku.swccgo.cards.set1103.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelWeaponTargetingEffect;
import com.gempukku.swccgo.cards.evaluators.NegativeEvaluator;
import com.gempukku.swccgo.cards.evaluators.OnTableEvaluator;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.DrawDestinyState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.game.state.WeaponFiringState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.RefreshPrintedDestinyValuesEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Set 3: Revolution
 * Type: Interrupt
 * Subtype: Lost
 * Title: Sabotage
 */
public class Card1103_019 extends AbstractLostInterrupt {
    public Card1103_019() {
        super(Side.LIGHT, 5, Title.Sabotage, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Poorly equipped Rebels must rely on clandestine strikes against the massive Imperial military. Computer 'slicing' and system tampering have proven most effective.");
        setGameText("Target an opponent's device or non-superlaser weapon card. Draw destiny. Target lost if destiny -X < target's destiny number, where X = the number of your spies on table. OR Cancel the targeting of a [Permanent Weapon] weapon at same site as your spy.");
        addIcons(Icon.A_NEW_HOPE);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        Filter targetFilter = Filters.and(Filters.opponents(self), Filters.or(Filters.device, Filters.and(Filters.weapon, Filters.not(Filters.superlaser_weapon))));
        TargetingReason targetingReason = TargetingReason.TO_BE_LOST;

        // Check condition(s)
        if (GameConditions.canTarget(game, self, targetingReason, targetFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose device or weapon", targetingReason, targetFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard targetedCard) {
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
                                                        protected List<Modifier> getDrawDestinyModifiers(SwccgGame game, DrawDestinyState drawDestinyState) {
                                                            Modifier modifier = new TotalDestinyModifier(self, drawDestinyState.getId(),
                                                                    new NegativeEvaluator(new OnTableEvaluator(self, SpotOverride.INCLUDE_UNDERCOVER, Filters.and(Filters.your(self), Filters.spy))));
                                                            return Collections.singletonList(modifier);
                                                        }
                                                        @Override
                                                        protected void destinyDraws(final SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, final Float totalDestiny) {
                                                            final GameState gameState = game.getGameState();
                                                            final ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                                                            if (totalDestiny == null) {
                                                                gameState.sendMessage("Result: Failed due to failed destiny draw");
                                                                return;
                                                            }

                                                            action.appendEffect(
                                                                    new RefreshPrintedDestinyValuesEffect(action, Collections.singletonList(finalTarget)) {
                                                                        @Override
                                                                        protected void refreshedPrintedDestinyValues() {
                                                                            gameState.sendMessage("Total destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                                            float targetsDestiny = modifiersQuerying.getDestiny(gameState, finalTarget);
                                                                            gameState.sendMessage("Target's destiny: " + GuiUtils.formatAsString(targetsDestiny));

                                                                            if (totalDestiny < targetsDestiny) {
                                                                                gameState.sendMessage("Result: Succeeded");
                                                                                action.appendEffect(
                                                                                        new LoseCardFromTableEffect(action, finalTarget));
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
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.isTargetedByWeapon(game, effect, Filters.any, Icon.PERMANENT_WEAPON)) {
            WeaponFiringState weaponFiringState = game.getGameState().getWeaponFiringState();
            PhysicalCard firingCard = weaponFiringState != null ? weaponFiringState.getCardFiring() : null;
            if (firingCard != null
                    && GameConditions.canSpot(game, self, SpotOverride.INCLUDE_UNDERCOVER,
                    Filters.and(Filters.your(self), Filters.spy, Filters.atSameSite(firingCard)))) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
                action.setText("Cancel weapon targeting");
                // Allow response(s)
                action.allowResponses(
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new CancelWeaponTargetingEffect(action));
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
