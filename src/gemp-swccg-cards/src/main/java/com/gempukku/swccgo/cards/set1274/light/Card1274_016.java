package com.gempukku.swccgo.cards.set1274.light;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.RetargetWeaponEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WeaponFiringState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfWeaponFiringModifierEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.WeaponTargetingMayNotBeCanceledModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 7.4
 * Type: Character
 * Subtype: Droid
 * Title: Artoo & Threepio (V)
 */
public class Card1274_016 extends AbstractDroid {
    public Card1274_016() {
        super(Side.LIGHT, 3, 3, 2, 6, "Artoo & Threepio", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_4, Rarity.V);
        setVirtualSuffix(true);
        addComboCardTitles("Artoo", "Threepio");
        setLore("Threepio's extensive experience 'talking' to the Falcon's computer allowed him to pilot the freighter while on Coruscant. Artoo, an R-unit droid, provided moral support.");
        setGameText("When deployed, may draw top card of Reserve Deck. Unless hit, may re-target a weapon targeting Chewie, Leia, or Luke (of ability < 5) to this droid (targeting may not be canceled). If this droid was just forfeited during opponent's turn, may take it into hand.");
        addPersonas(Persona.R2D2, Persona.C3PO);
        addIcons(Icon.NAV_COMPUTER);
        addModelTypes(ModelType.ASTROMECH, ModelType.PROTOCOL);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalBeforeTriggers(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        Filter protectedCharacters = Filters.and(Filters.your(self), Filters.or(Filters.Chewie, Filters.Leia, Filters.Luke), Filters.abilityLessThan(5));

        // Check condition(s)
        if (TriggerConditions.isTargetedByWeapon(game, effect, protectedCharacters, Filters.weapon)
                && !GameConditions.isHit(game, self)) {
            WeaponFiringState weaponFiringState = game.getGameState().getWeaponFiringState();
            final Collection<PhysicalCard> originalTargets = Filters.filter(weaponFiringState.getTargets(), game, protectedCharacters);
            Filter retargetFilter = Filters.none;
            for (PhysicalCard originalTarget : originalTargets) {
                retargetFilter = Filters.or(retargetFilter, Filters.weaponMayRetargetTo(originalTarget));
            }
            final PhysicalCard weapon = weaponFiringState.getPermanentWeaponFiring() != null ? weaponFiringState.getCardFiringWeapon() : weaponFiringState.getCardFiring();
            final PhysicalCard cardFiring = weaponFiringState.getCardFiring();
            if (Filters.and(retargetFilter).accepts(game, self)) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, GameTextActionId.LEGACY__ARTOO_AND_THREEPIO_V__RETARGET_WEAPON);
                action.setText("Re-target " + GameUtils.getFullName(weapon) + " to this droid");
                // Choose target(s)
                action.appendTargeting(
                        new ChooseCardOnTableEffect(action, playerId, "Choose target to re-target from", Filters.and(Filters.in(originalTargets), Filters.not(Filters.sameCardId(self)))) {
                            @Override
                            protected void cardSelected(final PhysicalCard originalTarget) {
                                action.addAnimationGroup(self);
                                // Allow response(s)
                                action.allowResponses("Re-target " + GameUtils.getCardLink(weapon) + " to " + GameUtils.getCardLink(self),
                                        new UnrespondableEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new RetargetWeaponEffect(action, originalTarget, self));
                                                action.appendEffect(
                                                        new AddUntilEndOfWeaponFiringModifierEffect(action,
                                                                new WeaponTargetingMayNotBeCanceledModifier(self, Filters.sameCardId(cardFiring)),
                                                                "Targeting by " + GameUtils.getCardLink(weapon) + " may not be canceled"));
                                            }
                                        }
                                );
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<OptionalGameTextTriggerAction>();

        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)
                && GameConditions.hasReserveDeck(game, playerId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, GameTextActionId.LEGACY__ARTOO_AND_THREEPIO_V__DRAW_TOP_CARD);
            action.setText("Draw top card of Reserve Deck");
            // Perform result(s)
            action.appendEffect(
                    new DrawCardIntoHandFromReserveDeckEffect(action, playerId));
            actions.add(action);
        }

        return actions.isEmpty() ? null : actions;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextLeavesTableOptionalTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justForfeited(game, effectResult, playerId, self)
                && GameConditions.isOpponentsTurn(game, playerId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, GameTextActionId.LEGACY__ARTOO_AND_THREEPIO_V__TAKE_INTO_HAND);
            action.setText("Take into hand from Lost Pile");
            action.setActionMsg("Take " + GameUtils.getCardLink(self) + " into hand from Lost Pile");
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromLostPileEffect(action, playerId, self, false, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
