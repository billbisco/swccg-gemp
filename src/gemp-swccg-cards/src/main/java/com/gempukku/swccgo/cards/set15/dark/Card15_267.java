package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.set9.dark.Card9_138;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.CancelGameTextUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.MayNotUseAbilityTowardDrawingBattleDestinyUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.ResetPowerUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.HitResult;
import java.util.Collections;
import java.util.List;

public class Card15_267 extends Card9_138 {
    public Card15_267() {
        setCardSubtype(CardSubtype.USED_OR_STARTING);
        setAlternateDestiny(3);
        setLore("");
        setGameText("USED: Cancel Leia's game text for remainder of turn. OR If your weapon (except a lightsaber) just 'hit' an opponent's character, for rest of turn that character is power = 0 and may not apply ability toward drawing battle destiny. OR Place one card from hand on top of Force Pile. STARTING: If Endor system on table, take Death Star II system, Jerjerrod and any Effect into hand from Reserve Deck. When you draw your starting hand, draw only six more cards. Place Interrupt in Lost Pile.");
        setCredits(0);
        setAsHorizontal(false);
        for (Icon icon : Icon.values()) {
            if (icon.isExpansionIcon() || icon == Icon.EPISODE_I) addIcon(icon, 0);
        }
        addIcons(Icon.REFLECTIONS_GOLD);
        addComboCardTitles("Operational As Planned", "Wounded Warrior");
    }
    @Override public String getTitle() { return "Operational As Planned & Wounded Warrior"; }
    @Override public Float getDestiny() { return 3f; }
    @Override public Uniqueness getUniqueness() { return Uniqueness.UNIQUE; }
    @Override public ExpansionSet getExpansionSet() { return ExpansionSet.REFLECTIONS_GOLD; }
    @Override public Rarity getRarity() { return Rarity.UNKNOWN; }

    private List<PlayInterruptAction> getGameTextTopLevelActionsFromCombinedCard(final String playerId, SwccgGame game, final PhysicalCard self) {
        Filter leiaFilter = Filters.Leia;

        // Check condition(s)
        if (GameConditions.canTarget(game, self, leiaFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Cancel Leia's game text");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose Leia", leiaFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Cancel " + GameUtils.getCardLink(targetedCard) + "'s game text",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new CancelGameTextUntilEndOfTurnEffect(action, finalTarget));
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
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> result = new java.util.ArrayList<>();
        List<PlayInterruptAction> first = super.getGameTextTopLevelActions(playerId, game, self);
        if (first != null) result.addAll(first);
        List<PlayInterruptAction> second = getGameTextTopLevelActionsFromCombinedCard(playerId, game, self);
        if (second != null) result.addAll(second);
        return result;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.justHitBy(game, effectResult, Filters.and(Filters.opponents(self), Filters.character),
                Filters.and(Filters.your(self), Filters.weapon, Filters.except(Filters.lightsaber)))) {
            final PhysicalCard characterHit = ((HitResult) effectResult).getCardHit();
            PhysicalCard cardHit = ((HitResult) effectResult).getCardHit();
            if (GameConditions.canTarget(game, self, cardHit)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
                action.setText("Reset " + GameUtils.getFullName(characterHit) + "'s power to 0");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Target character", cardHit) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, PhysicalCard cardTargeted) {
                                action.addAnimationGroup(cardTargeted);
                                // Allow response(s)
                                action.allowResponses("Reset " + GameUtils.getCardLink(characterHit) + "'s power to 0 and may not apply ability toward drawing battle destiny",
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Get the targeted card(s) from the action using the targetGroupId.
                                                // This needs to be done in case the target(s) were changed during the responses.
                                                PhysicalCard finalTarget = targetingAction.getPrimaryTargetCard(targetGroupId);

                                                // Perform result(s)
                                                action.appendEffect(
                                                        new ResetPowerUntilEndOfTurnEffect(action, finalTarget, 0));
                                                action.appendEffect(
                                                        new MayNotUseAbilityTowardDrawingBattleDestinyUntilEndOfTurnEffect(action, finalTarget));
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
}
