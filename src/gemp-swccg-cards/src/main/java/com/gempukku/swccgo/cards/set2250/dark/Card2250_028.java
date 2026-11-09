package com.gempukku.swccgo.cards.set2250.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.RetargetWeaponEffect;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WeaponFiringState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnActionProxyEffect;
import com.gempukku.swccgo.logic.effects.HitCardEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.AboutToBeHitResult;
import com.gempukku.swccgo.logic.timing.results.AboutToLoseCardFromTableResult;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 5
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Trade Federation Tactics (V)
 * Pre-errata version of 1250_28: the Virtual Block 5 release sheet text (slip 28E, sheet copy of 2010-04-01), replaced
 * by the AR 2.0 Appendix A rewrite (2012-08-27). Anything Goes only (docs/legacy-versions.md).
 * <p>
 * USED: for the rest of the turn, whenever your starship is about to be 'hit' or lost, you may make that 'hit' or loss
 * affect your character aboard instead. LOST: an attempt to make your [Episode I] character 'hit' or lost goes to your
 * other [Episode I] character present (as the current card handles it: weapons are re-targeted when they target,
 * other 'hits' and losses when they are about to happen, attempts by the opponent only).
 */
public class Card2250_028 extends AbstractUsedOrLostInterrupt {
    public Card2250_028() {
        super(Side.DARK, 6, "Trade Federation Tactics", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_5, Rarity.V);
        setVirtualSuffix(true);
        setLore("Organizations as wealthy as the Trade Federation can afford large amounts of military hardware, all purchased under the guise of protecting their commercial interests.");
        setGameText("USED: For remainder of turn, if your starship is about to be hit (or lost), that action may instead affect your character aboard that starship. LOST: If your [Episode I] character is about to be hit (or lost), that action instead affects your other [Episode I] character present.");
        addIcons(Icon.EPISODE_I, Icon.THEED_PALACE);
        setLegacyVersion(new LegacyVersion("1250_28", "",
                "Virtual Block 5 release sheet (2009-12-07; slip 28E, sheet copy 2010-04-01), card #28",
                LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }

    private static Filter yourStarshipWithYourCharacterAboard(PhysicalCard self) {
        return Filters.and(Filters.your(self), Filters.starship,
                Filters.hasAboard(self, Filters.and(Filters.your(self), Filters.character)));
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        if (!GameConditions.canSpot(game, self, yourStarshipWithYourCharacterAboard(self)))
            return null;
        final int gameTextSourceCardId = self.getCardId();
        final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
        action.setText("Let hits and losses of your starships affect your characters aboard this turn");
        action.allowResponses(
                new RespondablePlayCardEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        action.appendEffect(new AddUntilEndOfTurnActionProxyEffect(action, new AbstractActionProxy() {
                            @Override
                            public List<TriggerAction> getOptionalAfterTriggers(String playerId2, SwccgGame game, EffectResult effectResult) {
                                if (!playerId.equals(playerId2))
                                    return null;
                                return redirectStarshipTriggers(playerId, game, effectResult, self, gameTextSourceCardId);
                            }
                        }));
                    }
                });
        return Collections.singletonList(action);
    }

    private static List<TriggerAction> redirectStarshipTriggers(String playerId, SwccgGame game, EffectResult effectResult,
                                                                PhysicalCard self, int gameTextSourceCardId) {
        final Filter yourStarship = Filters.and(Filters.your(playerId), Filters.starship);
        List<TriggerAction> actions = new LinkedList<>();
        if (TriggerConditions.isAboutToBeHit(game, effectResult, yourStarship)) {
            final AboutToBeHitResult result = (AboutToBeHitResult) effectResult;
            final PhysicalCard starship = result.getCardToBeHit();
            final Filter characterAboard = Filters.and(Filters.your(playerId), Filters.character, Filters.aboard(starship));
            if (result.getPreventableCardEffect() != null && GameConditions.canSpot(game, self, characterAboard)) {
                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId);
                action.setText("Make 'hit' on " + GameUtils.getFullName(starship) + " affect a character aboard");
                action.appendEffect(
                        new ChooseCardOnTableEffect(action, playerId, "Choose character aboard " + GameUtils.getCardLink(starship), characterAboard) {
                            @Override
                            protected void cardSelected(final PhysicalCard character) {
                                result.getPreventableCardEffect().preventEffectOnCard(starship);
                                action.appendEffect(new HitCardEffect(action, character, result.getHitByCard(), result.getHitByPermanentWeapon(), result.getCardFiringWeapon()));
                            }
                        });
                actions.add(action);
            }
        }
        if (TriggerConditions.isAboutToBeLost(game, effectResult, yourStarship)) {
            final AboutToLoseCardFromTableResult result = (AboutToLoseCardFromTableResult) effectResult;
            final PhysicalCard starship = result.getCardToBeLost();
            final Filter characterAboard = Filters.and(Filters.your(playerId), Filters.character, Filters.aboard(starship));
            if (result.getPreventableCardEffect() != null && GameConditions.canSpot(game, self, characterAboard)) {
                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId);
                action.setText("Make loss of " + GameUtils.getFullName(starship) + " affect a character aboard");
                action.appendEffect(
                        new ChooseCardOnTableEffect(action, playerId, "Choose character aboard " + GameUtils.getCardLink(starship), characterAboard) {
                            @Override
                            protected void cardSelected(final PhysicalCard character) {
                                result.getPreventableCardEffect().preventEffectOnCard(starship);
                                action.appendEffect(new LoseCardFromTableEffect(action, character));
                            }
                        });
                actions.add(action);
            }
        }
        return actions;
    }

    // LOST: the [Episode I] character clause, as the current card (1250_28) implements it, played as a Lost Interrupt.

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, final SwccgGame game, final Effect effect, final PhysicalCard self) {
        final Filter yourEpisodeICharacter = Filters.and(Filters.your(self), Filters.character, Icon.EPISODE_I);
        if (TriggerConditions.isTargetedByWeapon(game, effect, yourEpisodeICharacter, Filters.weapon)) {
            WeaponFiringState weaponFiringState = game.getGameState().getWeaponFiringState();
            final PhysicalCard cardFiring = weaponFiringState.getCardFiring();
            if (cardFiring == null || !game.getOpponent(playerId).equals(cardFiring.getOwner())) {
                return null;
            }
            final Collection<PhysicalCard> originalTargets = Filters.filter(weaponFiringState.getTargets(), game, yourEpisodeICharacter);
            final PhysicalCard weapon = weaponFiringState.getPermanentWeaponFiring() != null ? weaponFiringState.getCardFiringWeapon() : cardFiring;
            Filter originalTargetWithAlternative = Filters.none;
            for (PhysicalCard originalTarget : originalTargets) {
                if (GameConditions.canSpot(game, self, newTargetFilter(yourEpisodeICharacter, originalTarget))) {
                    originalTargetWithAlternative = Filters.or(originalTargetWithAlternative, Filters.sameCardId(originalTarget));
                }
            }
            final Filter originalTargetFilter = Filters.and(Filters.in(originalTargets), originalTargetWithAlternative);
            if (!GameConditions.canSpot(game, self, originalTargetFilter)) {
                return null;
            }
            final PlayInterruptAction action = new PlayInterruptAction(game, self, GameTextActionId.LEGACY__TRADE_FEDERATION_TACTICS_V__RETARGET_WEAPON, CardSubtype.LOST);
            action.setText("Re-target " + GameUtils.getFullName(weapon));
            action.appendTargeting(
                    new ChooseCardOnTableEffect(action, playerId, "Choose character to re-target from", originalTargetFilter) {
                        @Override
                        protected void cardSelected(final PhysicalCard originalTarget) {
                            action.appendTargeting(
                                    new ChooseCardOnTableEffect(action, playerId, "Choose character to re-target to", newTargetFilter(yourEpisodeICharacter, originalTarget)) {
                                        @Override
                                        protected void cardSelected(final PhysicalCard newTarget) {
                                            action.addAnimationGroup(originalTarget, newTarget);
                                            action.allowResponses("Re-target " + GameUtils.getCardLink(weapon) + " from " + GameUtils.getCardLink(originalTarget) + " to " + GameUtils.getCardLink(newTarget),
                                                    new RespondablePlayCardEffect(action) {
                                                        @Override
                                                        protected void performActionResults(Action targetingAction) {
                                                            action.appendEffect(new RetargetWeaponEffect(action, originalTarget, newTarget));
                                                        }
                                                    });
                                        }
                                    });
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }

    private static Filter newTargetFilter(Filter yourEpisodeICharacter, PhysicalCard originalTarget) {
        return Filters.and(yourEpisodeICharacter, Filters.not(Filters.sameCardId(originalTarget)), Filters.presentWith(originalTarget),
                Filters.weaponMayRetargetTo(originalTarget));
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);
        final Filter yourEpisodeICharacter = Filters.and(Filters.your(self), Filters.character, Icon.EPISODE_I);
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Episode I character about to be 'hit' by opponent other than by a weapon (weapon attempts are re-targeted when targeted)
        if (TriggerConditions.isAboutToBeHitBy(game, effectResult, yourEpisodeICharacter, Filters.owner(opponent))) {
            final AboutToBeHitResult result = (AboutToBeHitResult) effectResult;
            final PhysicalCard hitByCard = result.getHitByCard();
            if (result.getHitByPermanentWeapon() == null && result.getCardFiringWeapon() == null
                    && (hitByCard == null || !Filters.weapon.accepts(game, hitByCard))) {
                final PhysicalCard original = result.getCardToBeHit();
                final Filter newTarget = otherEpisodeICharacterPresent(yourEpisodeICharacter, original);
                if (GameConditions.canSpot(game, self, newTarget)) {
                    final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
                    action.setText("Re-target 'hit' on " + GameUtils.getFullName(original));
                    action.appendTargeting(
                            new ChooseCardOnTableEffect(action, playerId, "Choose character to re-target to", newTarget) {
                                @Override
                                protected void cardSelected(final PhysicalCard character) {
                                    action.addAnimationGroup(original, character);
                                    action.allowResponses("Re-target 'hit' from " + GameUtils.getCardLink(original) + " to " + GameUtils.getCardLink(character),
                                            new RespondablePlayCardEffect(action) {
                                                @Override
                                                protected void performActionResults(Action targetingAction) {
                                                    result.getPreventableCardEffect().preventEffectOnCard(original);
                                                    action.appendEffect(new HitCardEffect(action, character, result.getHitByCard(), result.getHitByPermanentWeapon(), result.getCardFiringWeapon()));
                                                }
                                            });
                                }
                            });
                    actions.add(action);
                }
            }
        }

        // Episode I character about to be lost by opponent's action other than a weapon
        if (isAboutToBeLostByOpponent(game, effectResult, yourEpisodeICharacter, opponent)) {
            final AboutToLoseCardFromTableResult result = (AboutToLoseCardFromTableResult) effectResult;
            final PhysicalCard original = result.getCardToBeLost();
            final Filter newTarget = otherEpisodeICharacterPresent(yourEpisodeICharacter, original);
            if (!Filters.weapon.accepts(game, result.getSourceCard()) && !isWeaponTarget(game, original)
                    && GameConditions.canSpot(game, self, newTarget)) {
                final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
                action.setText("Re-target loss of " + GameUtils.getFullName(original));
                action.appendTargeting(
                        new ChooseCardOnTableEffect(action, playerId, "Choose character to re-target to", newTarget) {
                            @Override
                            protected void cardSelected(final PhysicalCard character) {
                                action.addAnimationGroup(original, character);
                                action.allowResponses("Re-target loss from " + GameUtils.getCardLink(original) + " to " + GameUtils.getCardLink(character),
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                result.getPreventableCardEffect().preventEffectOnCard(original);
                                                action.appendEffect(new LoseCardFromTableEffect(action, character));
                                            }
                                        });
                            }
                        });
                actions.add(action);
            }
        }
        return actions;
    }

    private static boolean isAboutToBeLostByOpponent(SwccgGame game, EffectResult effectResult, Filter filter, String opponent) {
        if (!TriggerConditions.isAboutToBeLost(game, effectResult, filter))
            return false;
        AboutToLoseCardFromTableResult result = (AboutToLoseCardFromTableResult) effectResult;
        PhysicalCard source = result.getSourceCard();
        return opponent.equals(result.getPerformingPlayerId())
                && source != null && opponent.equals(source.getOwner())
                && result.getPreventableCardEffect() != null;
    }

    private static boolean isWeaponTarget(SwccgGame game, PhysicalCard card) {
        WeaponFiringState weaponFiringState = game.getGameState().getWeaponFiringState();
        return weaponFiringState != null && weaponFiringState.getTargets() != null
                && weaponFiringState.getTargets().contains(card);
    }

    private static Filter otherEpisodeICharacterPresent(Filter yourEpisodeICharacter, PhysicalCard original) {
        return Filters.and(yourEpisodeICharacter, Filters.not(Filters.sameCardId(original)), Filters.presentWith(original));
    }
}
