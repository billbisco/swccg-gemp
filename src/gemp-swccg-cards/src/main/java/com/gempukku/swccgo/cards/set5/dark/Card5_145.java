package com.gempukku.swccgo.cards.set5.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.ForfeitCardsFromTableSimultaneouslyEffect;
import com.gempukku.swccgo.logic.effects.HitCardEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RestoreCardToNormalEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardsOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToBeHitResult;
import com.gempukku.swccgo.logic.timing.results.HitResult;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Human Shield
 */
public class Card5_145 extends AbstractUsedOrLostInterrupt {
    public Card5_145() {
        super(Side.DARK, 5, "Human Shield", Uniqueness.UNRESTRICTED, ExpansionSet.CLOUD_CITY, Rarity.C);
        setLore("A shining example of Imperial honor and bravery.");
        setGameText("USED: If an opponent's weapon is about to 'hit' an Imperial present with a captive being escorted, the captive is 'hit' instead. LOST: During the damage segment of a battle at a site, you may forfeit any captives present (once per battle per captive).");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        // 'Imperial' may be treated as 'bounty hunter' when this game text is modified (e.g. by Hutt Influence (V)).
        Filter imperial = GameConditions.hasGameTextModification(game, self, ModifyGameTextType.LEGACY__HUTT_INFLUENCE__HUMAN_SHIELD_IMPERIAL_MAY_BE_BOUNTY_HUNTER)
                ? Filters.or(Filters.Imperial, Filters.bounty_hunter) : Filters.Imperial;
        Filter impPresentWithCaptive = Filters.and(imperial, Filters.presentInBattle, Filters.escorting(Filters.any));
        // Check condition(s)
        final boolean aboutToBeHit = playsWhenAboutToBeHit();
        Filter opponentsWeapon = Filters.and(Filters.opponents(playerId), Filters.weapon);
        if (aboutToBeHit ? TriggerConditions.isAboutToBeHitBy(game, effectResult, impPresentWithCaptive, opponentsWeapon)
                : TriggerConditions.justHitBy(game, effectResult, impPresentWithCaptive, opponentsWeapon)) {
            final PhysicalCard cardHit = aboutToBeHit ? ((AboutToBeHitResult) effectResult).getCardToBeHit() : ((HitResult) effectResult).getCardHit();
            Collection<PhysicalCard> validCaptives = Filters.filter(game.getGameState().getCaptivesOfEscort(cardHit), game, Filters.canBeTargetedBy(self, TargetingReason.TO_BE_HIT));
            if(!validCaptives.isEmpty()){
                final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
                action.setText("Make captive 'hit' instead");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose captive", SpotOverride.INCLUDE_CAPTIVE, TargetingReason.TO_BE_HIT, Filters.in(validCaptives)) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, final PhysicalCard cardTargeted) {
                                action.addAnimationGroup(cardTargeted);
                                // Allow response(s)
                                action.allowResponses("Make " + GameUtils.getCardLink(cardTargeted) + " 'hit' instead",
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Get the targeted card(s) from the action using the targetGroupId.
                                                // This needs to be done in case the target(s) were changed during the responses.
                                                PhysicalCard captiveToHit = targetingAction.getPrimaryTargetCard(targetGroupId);

                                                // Perform result(s)
                                                if (aboutToBeHit) {
                                                    // PC errata: the Imperial is not 'hit'; the captive is 'hit' instead
                                                    action.appendEffect(
                                                            new PassthruEffect(action) {
                                                                @Override
                                                                protected void doPlayEffect(SwccgGame game) {
                                                                    ((AboutToBeHitResult) effectResult).getPreventableCardEffect().preventEffectOnCard(cardHit);
                                                                }
                                                            });
                                                    action.appendEffect(
                                                            new HitCardEffect(action, captiveToHit, self));
                                                }
                                                else {
                                                    action.appendEffect(
                                                            new HitCardEffect(action, captiveToHit, self));
                                                    action.appendEffect(
                                                            new RestoreCardToNormalEffect(action, cardHit)
                                                    );
                                                }
                                            }
                                        });
                            }
                        }
                );
                actions.add(action);
            }
        }

        Filter yourCharacterEscortingACaptive = Filters.and(Filters.your(playerId), Filters.character, Filters.escorting(Filters.any));

        // Check condition(s)
        if (TriggerConditions.isResolvingBattleDamageAndAttrition(game, effectResult, playerId)
                && GameConditions.isDuringBattleWithParticipant(game, yourCharacterEscortingACaptive)
                && GameConditions.isDuringBattleAt(game, Filters.site)) {

            Collection<PhysicalCard> charactersEscortingCaptives = Filters.filterActive(game, self, yourCharacterEscortingACaptive);
            Collection<PhysicalCard> validCaptives = new LinkedList<>();
            for(PhysicalCard characterEscortingCaptives: charactersEscortingCaptives){
                validCaptives.addAll(game.getGameState().getCaptivesOfEscort(characterEscortingCaptives));
            }

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Forfeit captive(s)");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardsOnTableEffect(action, playerId, "Target captives(s) to forfeit", 1, validCaptives.size(), SpotOverride.INCLUDE_CAPTIVE, Filters.in(validCaptives)) {
                        @Override
                        protected void cardsTargeted(final int targetGroupId, final Collection<PhysicalCard> targetedCards) {
                            action.allowResponses("Forfeit " + GameUtils.getAppendedNames(targetedCards),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            action.addAnimationGroup(targetedCards);
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            final Collection<PhysicalCard> finalCharacters = action.getPrimaryTargetCards(targetGroupId);
                                            action.appendEffect(
                                                    new ForfeitCardsFromTableSimultaneouslyEffect(action, finalCharacters)
                                            );
                                        }
                                    });
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }

    /**
     * PC errata (Advanced Rulebook 2023 Appendix A), USED: "If an opponent's weapon is about to 'hit' an Imperial ..."
     * (printed: "just 'hit'", after which the Imperial is restored to normal).
     */
    protected boolean playsWhenAboutToBeHit() {
        return true;
    }
}
