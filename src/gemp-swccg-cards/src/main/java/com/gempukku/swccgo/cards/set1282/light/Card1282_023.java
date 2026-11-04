package com.gempukku.swccgo.cards.set1282.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.InsteadOfFiringWeaponEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.CancelReactEffect;
import com.gempukku.swccgo.logic.effects.FindMissingCharacterEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ExchangeCardInHandWithCardInLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.ImmunityToAttritionChangeModifier;
import com.gempukku.swccgo.logic.modifiers.ImmunityToAttritionLimitedToModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.2
 * Type: Interrupt
 * Subtype: Used
 * Title: Found Someone You Have & Higher Ground
 */
public class Card1282_023 extends AbstractUsedInterrupt {
    public Card1282_023() {
        super(Side.LIGHT, 3, "Found Someone You Have & Higher Ground", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_8_2, Rarity.V);
        addComboCardTitles("Found Someone You Have", "Higher Ground");
        setGameText("During a battle at opponent's site (or your non-<> site), instead of firing one of your character weapons at a target, cause that target to be immunity to attrition -3 (or limited to immunity to attrition < 5) for remainder of turn. OR Find one of your missing characters. OR Cancel a 'react.' OR Once per game, use 1 Force to exchange a card in hand with the top most character of ability < 5 in your Lost Pile.");
    }

    /**
     * The topmost character of ability < 5 in the player's Lost Pile, or null.
     */
    private static PhysicalCard topmostCharacterOfAbilityLessThan5(SwccgGame game, String playerId) {
        Filter characterOfAbilityLessThan5 = Filters.and(Filters.character, Filters.abilityLessThan(5));
        for (PhysicalCard card : game.getGameState().getLostPile(playerId)) {
            if (characterOfAbilityLessThan5.accepts(game, card)) {
                return card;
            }
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Instead of firing one of your character weapons at a target
        Filter battleSite = Filters.and(Filters.site, Filters.or(Filters.opponents(self), Filters.and(Filters.your(self), Filters.unique)));
        final Filter targetFilter = Filters.and(Filters.opponents(self), Filters.character, Filters.presentInBattle);
        if (GameConditions.isDuringBattleAt(game, battleSite)
                && GameConditions.canTarget(game, self, targetFilter)) {
            Collection<PhysicalCard> weapons = Filters.filterActive(game, self,
                    Filters.and(Filters.your(self), Filters.character_weapon_or_character_with_permanent_character_weapon, Filters.canBeFiredForFree(self, 0)));
            if (!weapons.isEmpty()) {
                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Make a target immunity to attrition -3");
                action.appendTargeting(
                        new ChooseCardOnTableEffect(action, playerId, "Choose character weapon", Filters.in(weapons)) {
                            @Override
                            protected void cardSelected(final PhysicalCard selectedCard) {
                                action.addAnimationGroup(selectedCard);
                                action.appendTargeting(
                                        new TargetCardOnTableEffect(action, playerId, "Choose target", targetFilter) {
                                            @Override
                                            protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                                                action.addAnimationGroup(targetedCard);
                                                action.allowResponses("Make " + GameUtils.getCardLink(targetedCard) + " immunity to attrition -3 (and limited to < 5), instead of firing " + GameUtils.getCardLink(selectedCard) + ",",
                                                        new RespondablePlayCardEffect(action) {
                                                            @Override
                                                            protected void performActionResults(Action targetingAction) {
                                                                final PhysicalCard finalTarget = targetingAction.getPrimaryTargetCard(targetGroupId);
                                                                action.appendEffect(
                                                                        new InsteadOfFiringWeaponEffect(action, selectedCard,
                                                                                new PassthruEffect(action) {
                                                                                    @Override
                                                                                    protected void doPlayEffect(SwccgGame game) {
                                                                                        action.appendEffect(
                                                                                                new AddUntilEndOfTurnModifierEffect(action,
                                                                                                        new ImmunityToAttritionChangeModifier(self, finalTarget, -3),
                                                                                                        "Makes " + GameUtils.getCardLink(finalTarget) + " immunity to attrition -3"));
                                                                                        action.appendEffect(
                                                                                                new AddUntilEndOfTurnModifierEffect(action,
                                                                                                        new ImmunityToAttritionLimitedToModifier(self, finalTarget, 5),
                                                                                                        "Limits " + GameUtils.getCardLink(finalTarget) + "'s immunity to attrition to < 5"));
                                                                                    }
                                                                                }));
                                                            }
                                                        });
                                            }
                                        });
                            }
                        });
                actions.add(action);
            }
        }

        // Find one of your missing characters
        Filter missingFilter = Filters.and(Filters.your(self), Filters.missing, Filters.character);
        if (GameConditions.canTarget(game, self, SpotOverride.INCLUDE_MISSING, missingFilter)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Find missing character");
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose missing character", SpotOverride.INCLUDE_MISSING, missingFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.allowResponses("Find " + GameUtils.getCardLink(targetedCard),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);
                                            action.appendEffect(
                                                    new FindMissingCharacterEffect(action, finalTarget, false));
                                        }
                                    });
                        }
                    });
            actions.add(action);
        }

        // Once per game, exchange a card in hand with the topmost character of ability < 5 in your Lost Pile
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__FOUND_SOMEONE_YOU_HAVE_AND_HIGHER_GROUND__EXCHANGE;
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.hasHand(game, playerId)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)
                && topmostCharacterOfAbilityLessThan5(game, playerId) != null) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Exchange card in hand with topmost character of ability < 5 in Lost Pile");
            action.appendUsage(
                    new OncePerGameEffect(action));
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            action.allowResponses("Exchange a card in hand with the topmost character of ability < 5 in Lost Pile",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            PhysicalCard topmost = topmostCharacterOfAbilityLessThan5(game, playerId);
                            if (topmost != null) {
                                action.appendEffect(
                                        new ExchangeCardInHandWithCardInLostPileEffect(action, playerId, Filters.any, Filters.sameCardId(topmost)));
                            }
                        }
                    });
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        // Cancel a 'react'
        if (TriggerConditions.isReact(game, effect)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Cancel 'react'");
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new CancelReactEffect(action));
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
