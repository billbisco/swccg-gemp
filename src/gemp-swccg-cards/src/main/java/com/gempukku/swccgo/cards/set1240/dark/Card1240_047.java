package com.gempukku.swccgo.cards.set1240.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.ExcludeFromBattleEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Block 4: Expanded Universe
 * Type: Interrupt
 * Subtype: Used
 * Title: Unexpected Interruption (V)
 */
public class Card1240_047 extends AbstractUsedInterrupt {
    public Card1240_047() {
        super(Side.DARK, 2, "Unexpected Interruption", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_4, Rarity.V);
        setVirtualSuffix(true);
        setLore("'Sir! Sir! I've isolated the reverse power flux coupling!'");
        setGameText("If a battle was just initiated, target an opponent's female non-droid character present with Xizor. Draw destiny. If destiny +2 > target's ability, target excluded from battle and adds her power to Xizor's for remainder of turn.");
        addIcons(Icon.DAGOBAH, Icon.LEGACY_BLOCK_4);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        final Filter targetFilter = Filters.and(Filters.opponents(self), Filters.female, Filters.non_droid_character, Filters.presentWith(self, Filters.Xizor), Filters.presentInBattle);
        final TargetingReason targetingReason = TargetingReason.TO_BE_EXCLUDED_FROM_BATTLE;

        if (TriggerConditions.battleInitiated(game, effectResult)
                && GameConditions.canTarget(game, self, targetingReason, targetFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Target female present with Xizor");
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose female non-droid character", targetingReason, targetFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            action.allowResponses("Target " + GameUtils.getCardLink(targetedCard),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            final PhysicalCard target = action.getPrimaryTargetCard(targetGroupId);
                                            if (target == null) {
                                                return;
                                            }
                                            final PhysicalCard xizor = Filters.findFirstActive(game, self,
                                                    Filters.and(Filters.Xizor, Filters.presentWith(target)));
                                            if (xizor == null) {
                                                return;
                                            }
                                            action.appendEffect(
                                                    new DrawDestinyEffect(action, playerId) {
                                                        @Override
                                                        protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                            return Collections.singletonList(target);
                                                        }

                                                        @Override
                                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                            GameState gameState = game.getGameState();
                                                            if (totalDestiny == null) {
                                                                gameState.sendMessage("Result: Failed due to failed destiny draw");
                                                                return;
                                                            }
                                                            float ability = game.getModifiersQuerying().getAbility(game.getGameState(), target);
                                                            float power = game.getModifiersQuerying().getPower(game.getGameState(), target);
                                                            gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                            gameState.sendMessage("Ability: " + GuiUtils.formatAsString(ability));
                                                            if (totalDestiny + 2 > ability) {
                                                                gameState.sendMessage("Result: Succeeded");
                                                                action.appendEffect(
                                                                        new ExcludeFromBattleEffect(action, target));
                                                                action.appendEffect(
                                                                        new AddUntilEndOfTurnModifierEffect(action,
                                                                                new PowerModifier(self, Filters.sameCardId(xizor), power),
                                                                                GameUtils.getCardLink(target) + "'s power added to Xizor"));
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
            return Collections.singletonList(action);
        }
        return null;
    }
}
