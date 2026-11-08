package com.gempukku.swccgo.cards.set2210.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.LegacyVersion;
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

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;


/**
 * Set: Virtual Block 1
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Precise Hit
 *
 * Original version of 1210_159 (Virtual Block 1 release sheet, Dec 2009, card #160). LOST cancels Heroic (and any
 * immunity to attrition) on any character or starship, Heroic or not (as the Virtual Set 12 card with this text,
 * 1112_18, plays it); GEMP's current text (an undated PC Legacy JSON revision) targets only a Heroic character or starship.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2210_159 extends AbstractUsedOrLostInterrupt {
    public Card2210_159() {
        super(Side.LIGHT, 2, Title.Precise_Hit, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("'Luke, trust me.'");
        setGameText("USED: If you just hit a target with a non-lightsaber non-[Permanent Weapon] weapon, place target (and all cards on it) in owner's Used Pile. LOST: Cancel Heroic (and any immunity to attrition) on a character or starship this turn.");
        setLegacyVersion(new LegacyVersion("1210_159", "",
                "Virtual Block 1 release sheet (2009-12-07), card #160", LocalDate.of(2009, 12, 7), LocalDate.of(2014, 2, 4)));
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
                            action.allowResponses("Cancel Heroic (and any immunity to attrition) on " + GameUtils.getCardLink(targetedCard) + " this turn",
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
