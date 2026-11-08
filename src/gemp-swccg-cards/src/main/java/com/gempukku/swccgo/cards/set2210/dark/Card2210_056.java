package com.gempukku.swccgo.cards.set2210.dark;

import java.time.LocalDate;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.CancelImmunityToAttritionUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.RemoveKeywordModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.HitResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Virtual Block 1
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Coordinated Attack
 * Original version of 1210_56 (Virtual Block 1 release sheet, Dec 2009): "place target" without "may"; same play (playing the Interrupt is the choice). Anything Goes only (docs/legacy-versions.md).
 */
public class Card2210_056 extends AbstractUsedOrLostInterrupt {
    public Card2210_056() {
        super(Side.DARK, 2, Title.Coordinated_Attack, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("'Stay in attack formation.'");
        setGameText("USED: If you just hit a target with a non-lightsaber, non-[Permanent Weapon] weapon, place target (and all cards on it) in owner's Used Pile. LOST: Cancel Heroic (and any immunity to attrition) on a character or starship this turn.");
        setLegacyVersion(new LegacyVersion("1210_56", "",
                "Virtual Block 1 release sheet (2009-12-07), card #56; superseded by the PC card list text (by 2012-07-21)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        Filter weaponFilter = Filters.and(Filters.your(self), Filters.weapon, Filters.not(Filters.lightsaber), Filters.not(Icon.PERMANENT_WEAPON));
        if (TriggerConditions.justHitBy(game, effectResult, Filters.any, weaponFilter)) {
            final PhysicalCard cardHit = ((HitResult) effectResult).getCardHit();
            if (cardHit != null && GameConditions.canTarget(game, self, cardHit)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
                action.setText("Place target in Used Pile");
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose target to place in Used Pile", cardHit) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                                action.addAnimationGroup(targetedCard);
                                action.allowResponses("Place " + GameUtils.getCardLink(targetedCard) + " (and all cards on it) in owner's Used Pile",
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);
                                                action.appendEffect(
                                                        new PlaceCardInUsedPileFromTableEffect(action, finalTarget, false, Zone.USED_PILE));
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
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        Filter targetFilter = Filters.and(Filters.or(Filters.character, Filters.starship), Filters.canBeTargetedBy(self));
        if (GameConditions.canTarget(game, self, targetFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Cancel Heroic and immunity to attrition");
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose character or starship", targetFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            action.allowResponses("Cancel Heroic (and any immunity to attrition) on " + GameUtils.getCardLink(targetedCard),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);
                                            action.appendEffect(
                                                    new AddUntilEndOfTurnModifierEffect(action,
                                                            new RemoveKeywordModifier(self, finalTarget, Keyword.HEROIC),
                                                            "cancels Heroic on " + GameUtils.getCardLink(finalTarget)));
                                            action.appendEffect(
                                                    new CancelImmunityToAttritionUntilEndOfTurnEffect(action, finalTarget,
                                                            "Cancels " + GameUtils.getCardLink(finalTarget) + "'s immunity to attrition"));
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
