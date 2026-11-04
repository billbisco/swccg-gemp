package com.gempukku.swccgo.cards.set1282.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.InsteadOfFiringWeaponEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.TitleComponent;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.CancelDestinyAndCauseRedrawEffect;
import com.gempukku.swccgo.logic.effects.CancelReactEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeDestinyCardIntoHandEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.ImmunityToAttritionChangeModifier;
import com.gempukku.swccgo.logic.modifiers.ImmunityToAttritionLimitedToModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.2
 * Type: Interrupt
 * Subtype: Used
 * Title: Sith Fury & End This Destructive Conflict
 */
public class Card1282_004 extends AbstractUsedInterrupt {
    public Card1282_004() {
        super(Side.DARK, 3, Title.Sith_Fury + " & " + Title.End_This_Destructive_Conflict, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_8_2, Rarity.V);
        setTitleComponents(new TitleComponent(Title.Sith_Fury, Uniqueness.UNIQUE), new TitleComponent(Title.End_This_Destructive_Conflict, Uniqueness.UNIQUE));
        setGameText("During a battle at opponent's site (or your non-<> site), instead of firing one of your character weapons at a target, cause that target to be immunity to attrition -3 (or limited to immunity to attrition < 5) for remainder of turn. OR If you just drew a character for destiny, take that card into hand to cancel and redraw that destiny. OR Cancel a 'react.' OR Place Projection Of A Skywalker in opponent's Used Pile.");
        addIcons(Icon.TATOOINE);
    }

    /**
     * Opponent's site, or your site that is not a <> (diamond) site.
     */
    private static Filter battleSiteFilter(PhysicalCard self) {
        return Filters.and(Filters.site, Filters.or(Filters.opponents(self), Filters.and(Filters.your(self), Filters.not(Filters.uniqueness(Uniqueness.DIAMOND_1)))));
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Instead of firing a character weapon, reduce (or limit) immunity to attrition
        final Filter targetFilter = Filters.and(Filters.opponents(self), Filters.character, Filters.presentInBattle);
        if (GameConditions.isDuringBattleAt(game, battleSiteFilter(self))
                && GameConditions.canTarget(game, self, targetFilter)) {
            Collection<PhysicalCard> weapons = Filters.filterActive(game, self,
                    Filters.and(Filters.your(self), Filters.character_weapon_or_character_with_permanent_character_weapon, Filters.canBeFiredForFree(self, 0)));
            if (!weapons.isEmpty()) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Reduce immunity to attrition instead of firing");
                // Choose target(s)
                action.appendTargeting(
                        new ChooseCardOnTableEffect(action, playerId, "Choose character weapon", Filters.in(weapons)) {
                            @Override
                            protected void cardSelected(final PhysicalCard selectedWeapon) {
                                action.addAnimationGroup(selectedWeapon);
                                action.appendTargeting(
                                        new TargetCardOnTableEffect(action, playerId, "Choose target", targetFilter) {
                                            @Override
                                            protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                                                action.addAnimationGroup(targetedCard);
                                                // Allow response(s)
                                                action.allowResponses("Make " + GameUtils.getCardLink(targetedCard) + " immunity to attrition -3 (and limited to < 5), instead of firing " + GameUtils.getCardLink(selectedWeapon) + ",",
                                                        new RespondablePlayCardEffect(action) {
                                                            @Override
                                                            protected void performActionResults(Action targetingAction) {
                                                                // Get the targeted card(s) from the action using the targetGroupId.
                                                                // This needs to be done in case the target(s) were changed during the responses.
                                                                PhysicalCard finalTarget = targetingAction.getPrimaryTargetCard(targetGroupId);

                                                                // Perform result(s)
                                                                action.appendEffect(
                                                                        new InsteadOfFiringWeaponEffect(action, selectedWeapon,
                                                                                new AddUntilEndOfTurnModifierEffect(action,
                                                                                        new ImmunityToAttritionChangeModifier(self, finalTarget, -3),
                                                                                        GameUtils.getCardLink(finalTarget) + " is immunity to attrition -3")));
                                                                action.appendEffect(
                                                                        new AddUntilEndOfTurnModifierEffect(action,
                                                                                new ImmunityToAttritionLimitedToModifier(self, finalTarget, 5),
                                                                                GameUtils.getCardLink(finalTarget) + " is limited to immunity to attrition < 5"));
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
        }

        // Place Projection Of A Skywalker in opponent's Used Pile
        final Filter projectionFilter = Filters.and(Filters.opponents(self), Filters.title(Title.Projection_Of_A_Skywalker));
        if (GameConditions.canTarget(game, self, projectionFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Place Projection Of A Skywalker in Used Pile");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose Projection Of A Skywalker", projectionFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Place " + GameUtils.getCardLink(targetedCard) + " in opponent's Used Pile",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            PhysicalCard finalTarget = targetingAction.getPrimaryTargetCard(targetGroupId);
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new PlaceCardInUsedPileFromTableEffect(action, finalTarget));
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
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        GameTextActionId gameTextActionId = GameTextActionId.ANY_CARD__CANCEL_AND_REDRAW_A_DESTINY;

        // Check condition(s)
        if (TriggerConditions.isDestinyJustDrawnBy(game, effectResult, playerId)
                && GameConditions.canCancelDestinyAndCauseRedraw(game, playerId)
                && GameConditions.isDestinyCardMatchTo(game, Filters.character)
                && GameConditions.canTakeDestinyCardIntoHand(game, playerId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Take destiny card into hand and cause re-draw");
            // Pay cost(s)
            action.appendEffect(
                    new TakeDestinyCardIntoHandEffect(action));
            // Allow response(s)
            action.allowResponses("Cancel destiny and cause re-draw",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new CancelDestinyAndCauseRedrawEffect(action));
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
        if (TriggerConditions.isReact(game, effect)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Cancel 'react'");
            // Allow response(s)
            action.allowResponses("Cancel 'react'",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new CancelReactEffect(action));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
