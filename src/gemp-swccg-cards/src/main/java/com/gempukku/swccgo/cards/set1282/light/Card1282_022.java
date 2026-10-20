package com.gempukku.swccgo.cards.set1282.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelWeaponTargetingEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.CancelCardBeingPlayedEffect;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayingCardEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.ExchangeCardInHandWithCardInLostPileEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByWeaponsModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.TargetingActionUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.2
 * Type: Interrupt
 * Subtype: Lost
 * Title: Corellian Slip
 */
public class Card1282_022 extends AbstractLostInterrupt {
    public Card1282_022() {
        super(Side.LIGHT, 4, Title.Corellian_Slip, Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_8_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("First perfected by Corellian starship battle tacticians, this dangerous counter-maneuver has saved numerous hot-shot pilots in life-or-death situations.");
        setGameText("Use 2 Force to take an Interrupt from your Reserve Deck; reshuffle. OR Cancel an attempt to target your starfighter with Tallon Roll (or a weapon) Starfighter is protected from further attempts for remainder of turn. OR Cancel [Coruscant Icon] Watch Your Back! OR Exchange a card from hand for any 'spice' card you own. (Immune to Sense).");
        addIcons(Icon.A_NEW_HOPE);
    }

    private Filter spiceFilter() {
        return Filters.or(Filters.titleContains("spice"), Filters.titleContains("Glitterstim"), Filters.titleContains("Ryll"), Filters.titleContains("Andoan"));
    }

    private Filter coruscantWatchYourBack() {
        return Filters.and(Filters.titleContains("Watch Your Back"), Icon.CORUSCANT);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        GameTextActionId takeId = GameTextActionId.LEGACY__CORELLIAN_SLIP_V__TAKE_INTERRUPT;
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, takeId)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 2)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, takeId);
            action.setText("Take Interrupt into hand from Reserve Deck");
            action.setImmuneTo(Title.Sense);
            action.appendCost(new UseForceEffect(action, playerId, 2));
            action.allowResponses("Take an Interrupt into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.Interrupt, true));
                        }
                    });
            actions.add(action);
        }

        if (GameConditions.canTargetToCancel(game, self, coruscantWatchYourBack())) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setImmuneTo(Title.Sense);
            CancelCardActionBuilder.buildCancelCardAction(action, coruscantWatchYourBack(), Title.Watch_Your_Back);
            actions.add(action);
        }

        if (GameConditions.hasHand(game, playerId)
                && GameConditions.hasLostPile(game, playerId)) {
            final Filter yourSpice = Filters.and(Filters.your(playerId), spiceFilter());
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Exchange a card in hand for a 'spice' card");
            action.setImmuneTo(Title.Sense);
            action.allowResponses("Exchange a card from hand for a 'spice' card you own",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(new ExchangeCardInHandWithCardInLostPileEffect(action, playerId, Filters.any, yourSpice));
                        }
                    });
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        Filter yourStarfighter = Filters.and(Filters.your(self), Filters.starfighter);

        if (TriggerConditions.isPlayingCard(game, effect, coruscantWatchYourBack())
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setImmuneTo(Title.Sense);
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            actions.add(action);
        }

        boolean tallonRoll = TriggerConditions.isPlayingCardTargeting(game, effect, Filters.Tallon_Roll, yourStarfighter)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)
                && effect instanceof RespondablePlayingCardEffect;
        boolean weapon = TriggerConditions.isTargetedByWeapon(game, effect, yourStarfighter, Filters.weapon);
        if (tallonRoll || weapon) {
            PhysicalCard targetedStarfighter = null;
            if (effect instanceof RespondableEffect) {
                List<PhysicalCard> targeted = TargetingActionUtils.getCardsTargeted(game, ((RespondableEffect) effect).getTargetingAction(), yourStarfighter);
                if (!targeted.isEmpty()) {
                    targetedStarfighter = targeted.get(0);
                }
            }
            final PhysicalCard starfighter = targetedStarfighter;
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Cancel targeting");
            action.setImmuneTo(Title.Sense);
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            if (weapon) {
                                action.appendEffect(new CancelWeaponTargetingEffect(action));
                            }
                            else {
                                action.appendEffect(new CancelCardBeingPlayedEffect(action, (RespondablePlayingCardEffect) effect));
                            }
                            if (starfighter != null) {
                                action.appendEffect(new AddUntilEndOfTurnModifierEffect(action,
                                        new MayNotBeTargetedByWeaponsModifier(self, starfighter),
                                        "Starfighter is protected from weapons"));
                                action.appendEffect(new AddUntilEndOfTurnModifierEffect(action,
                                        new MayNotBeTargetedByModifier(self, starfighter, Filters.Tallon_Roll),
                                        "Starfighter is protected from Tallon Roll"));
                            }
                        }
                    });
            actions.add(action);
        }

        return actions;
    }
}
