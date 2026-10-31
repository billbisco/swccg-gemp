package com.gempukku.swccgo.cards.set1108.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelTargetingEffect;
import com.gempukku.swccgo.cards.effects.CancelWeaponTargetingEffect;
import com.gempukku.swccgo.cards.effects.DrawsNoBattleDestinyEffect;
import com.gempukku.swccgo.common.CardSubtype;
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
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotFireWeaponsModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

/**
 * Set: Virtual Set 8
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Don't Forget The Droids (V)
 */
public class Card1108_006 extends AbstractUsedOrLostInterrupt {
    public Card1108_006() {
        super(Side.LIGHT, 6, "Don't Forget The Droids", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_8, Rarity.V);
        setVirtualSuffix(true);
        setLore("'We're on our way!'");
        setGameText("USED: Cancel an attempt to target a droid. LOST: If a battle was just initiated at a site, lose 1 Force and place your droid there out of play to prevent opponent from drawing weapon or battle destiny during this battle.");
        addIcons(Icon.JABBAS_PALACE);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self) {
        if (TriggerConditions.isTargetedByWeapon(game, effect, Filters.droid, Filters.any)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Cancel targeting");
            action.allowResponses("Cancel an attempt to target a droid",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new CancelWeaponTargetingEffect(action));
                        }
                    }
            );
            return Collections.singletonList(action);
        }

        if (effect instanceof RespondableEffect
                && effect.getAction() != null
                && TriggerConditions.isTargetedForReason(game, effect, effect.getAction().getPerformingPlayer(), Filters.droid, EnumSet.allOf(TargetingReason.class))) {
            final RespondableEffect respondableEffect = (RespondableEffect) effect;
            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Cancel targeting");
            action.allowResponses("Cancel an attempt to target a droid",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new CancelTargetingEffect(action, respondableEffect));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);
        Filter yourDroidAtBattle = Filters.and(Filters.your(self), Filters.droid, Filters.at(Filters.battleLocation));

        if (TriggerConditions.battleInitiatedAt(game, effectResult, Filters.site)
                && GameConditions.canSpot(game, self, yourDroidAtBattle)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Place droid out of play");
            action.appendTargeting(
                    new ChooseCardOnTableEffect(action, playerId, "Choose droid to place out of play", yourDroidAtBattle) {
                        @Override
                        protected void cardSelected(PhysicalCard droid) {
                            action.appendCost(
                                    new LoseForceEffect(action, playerId, 1, true));
                            action.appendCost(
                                    new PlaceCardOutOfPlayFromTableEffect(action, droid));
                            action.allowResponses("Prevent opponent from drawing weapon or battle destiny",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            action.appendEffect(
                                                    new DrawsNoBattleDestinyEffect(action, opponent));
                                            action.appendEffect(
                                                    new AddUntilEndOfBattleModifierEffect(action,
                                                            new MayNotFireWeaponsModifier(self, Filters.and(Filters.opponents(self), Filters.or(Filters.character, Filters.starship, Filters.vehicle, Filters.weapon))),
                                                            "Prevents opponent from drawing weapon destiny"));
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
