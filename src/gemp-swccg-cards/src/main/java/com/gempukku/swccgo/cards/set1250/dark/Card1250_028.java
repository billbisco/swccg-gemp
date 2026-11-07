package com.gempukku.swccgo.cards.set1250.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.RetargetWeaponEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
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
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.HitCardEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByWeaponsModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotTargetToBeLostModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.AboutToBeHitResult;
import com.gempukku.swccgo.logic.timing.results.AboutToLoseCardFromTableResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 5
 * Type: Interrupt
 * Subtype: Used
 * Title: Trade Federation Tactics (V)
 */
public class Card1250_028 extends AbstractUsedInterrupt {
    private static final String SIL_UNCH = "Sil Unch";

    public Card1250_028() {
        super(Side.DARK, 6, "Trade Federation Tactics", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_5, Rarity.V);
        setVirtualSuffix(true);
        setLore("Organizations as wealthy as the Trade Federation can afford large amounts of military hardware, all purchased under the guise of protecting their commercial interests.");
        setGameText("Retarget an attempt to make your [Episode I] character 'hit' or lost to your other [Episode I] character present. OR Retarget opponent's just drawn destiny result about to make your starship 'hit' or lost to your character aboard; starship is protected from all such attempts this turn. OR deploy Sil Unch.");
        addIcons(Icon.EPISODE_I, Icon.THEED_PALACE);
    }

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
                if (GameConditions.canSpot(game, self, newTargetFilter(self, yourEpisodeICharacter, originalTarget))) {
                    originalTargetWithAlternative = Filters.or(originalTargetWithAlternative, Filters.sameCardId(originalTarget));
                }
            }
            final Filter originalTargetFilter = Filters.and(Filters.in(originalTargets), originalTargetWithAlternative);
            if (!GameConditions.canSpot(game, self, originalTargetFilter)) {
                return null;
            }
            final PlayInterruptAction action = new PlayInterruptAction(game, self, GameTextActionId.LEGACY__TRADE_FEDERATION_TACTICS_V__RETARGET_WEAPON);
            action.setText("Re-target " + GameUtils.getFullName(weapon));
            action.appendTargeting(
                    new ChooseCardOnTableEffect(action, playerId, "Choose character to re-target from", originalTargetFilter) {
                        @Override
                        protected void cardSelected(final PhysicalCard originalTarget) {
                            action.appendTargeting(
                                    new ChooseCardOnTableEffect(action, playerId, "Choose character to re-target to", newTargetFilter(self, yourEpisodeICharacter, originalTarget)) {
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

    private static Filter newTargetFilter(PhysicalCard self, Filter yourEpisodeICharacter, PhysicalCard originalTarget) {
        return Filters.and(yourEpisodeICharacter, Filters.not(Filters.sameCardId(originalTarget)), Filters.presentWith(originalTarget),
                Filters.weaponMayRetargetTo(originalTarget));
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);
        final Filter yourStarship = Filters.and(Filters.your(self), Filters.starship);
        final Filter yourEpisodeICharacter = Filters.and(Filters.your(self), Filters.character, Icon.EPISODE_I);
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Starship about to be 'hit' by opponent's weapon destiny
        if (TriggerConditions.isAboutToBeHitBy(game, effectResult, yourStarship, Filters.owner(opponent))) {
            final AboutToBeHitResult result = (AboutToBeHitResult) effectResult;
            final PhysicalCard starship = result.getCardToBeHit();
            final Filter characterAboard = Filters.and(Filters.your(self), Filters.character, Filters.aboard(starship));
            if (isHitByWeapon(game, result) && GameConditions.canSpot(game, self, characterAboard)) {
                final PlayInterruptAction action = new PlayInterruptAction(game, self, GameTextActionId.LEGACY__TRADE_FEDERATION_TACTICS_V__RETARGET_STARSHIP_HIT);
                action.setText("Re-target 'hit' on " + GameUtils.getFullName(starship));
                action.appendTargeting(
                        new ChooseCardOnTableEffect(action, playerId, "Choose character aboard " + GameUtils.getCardLink(starship), characterAboard) {
                            @Override
                            protected void cardSelected(final PhysicalCard character) {
                                action.addAnimationGroup(starship, character);
                                action.allowResponses("Re-target 'hit' from " + GameUtils.getCardLink(starship) + " to " + GameUtils.getCardLink(character),
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                result.getPreventableCardEffect().preventEffectOnCard(starship);
                                                action.appendEffect(new HitCardEffect(action, character, result.getHitByCard(), result.getHitByPermanentWeapon(), result.getCardFiringWeapon()));
                                                appendStarshipProtection(action, self, starship);
                                            }
                                        });
                            }
                        });
                actions.add(action);
            }
        }

        // Starship about to be lost due to opponent's just drawn destiny (weapon destiny or asteroid destiny)
        if (isStarshipAboutToBeLostDueToOpponentsDestiny(game, effectResult, yourStarship, opponent)) {
            final AboutToLoseCardFromTableResult result = (AboutToLoseCardFromTableResult) effectResult;
            final PhysicalCard starship = result.getCardToBeLost();
            final Filter characterAboard = Filters.and(Filters.your(self), Filters.character, Filters.aboard(starship));
            if (GameConditions.canSpot(game, self, characterAboard)) {
                final PlayInterruptAction action = new PlayInterruptAction(game, self, GameTextActionId.LEGACY__TRADE_FEDERATION_TACTICS_V__RETARGET_STARSHIP_HIT);
                action.setText("Re-target loss of " + GameUtils.getFullName(starship));
                action.appendTargeting(
                        new ChooseCardOnTableEffect(action, playerId, "Choose character aboard " + GameUtils.getCardLink(starship), characterAboard) {
                            @Override
                            protected void cardSelected(final PhysicalCard character) {
                                action.addAnimationGroup(starship, character);
                                action.allowResponses("Re-target loss from " + GameUtils.getCardLink(starship) + " to " + GameUtils.getCardLink(character),
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                result.getPreventableCardEffect().preventEffectOnCard(starship);
                                                action.appendEffect(new LoseCardFromTableEffect(action, character));
                                                appendStarshipProtection(action, self, starship);
                                            }
                                        });
                            }
                        });
                actions.add(action);
            }
        }

        // Episode I character about to be 'hit' by opponent other than by a weapon (weapon attempts are re-targeted when targeted)
        if (TriggerConditions.isAboutToBeHitBy(game, effectResult, yourEpisodeICharacter, Filters.owner(opponent))) {
            final AboutToBeHitResult result = (AboutToBeHitResult) effectResult;
            final PhysicalCard hitByCard = result.getHitByCard();
            if (result.getHitByPermanentWeapon() == null && result.getCardFiringWeapon() == null
                    && (hitByCard == null || !Filters.weapon.accepts(game, hitByCard))) {
                final PhysicalCard original = result.getCardToBeHit();
                final Filter newTarget = otherEpisodeICharacterPresent(yourEpisodeICharacter, original);
                if (GameConditions.canSpot(game, self, newTarget)) {
                    final PlayInterruptAction action = new PlayInterruptAction(game, self);
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
            // Weapon attempts (including permanent weapons) are re-targeted when the weapon targets, so are not offered again here
            if (!Filters.weapon.accepts(game, result.getSourceCard()) && !isWeaponTarget(game, original)
                    && GameConditions.canSpot(game, self, newTarget)) {
                final PlayInterruptAction action = new PlayInterruptAction(game, self);
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

    /**
     * An attempt by the opponent to make the card lost: the opponent performs the action, the action comes from one of
     * opponent's cards, and the card is not just lost along with another card (all cards situation).
     */
    private static boolean isAboutToBeLostByOpponent(SwccgGame game, EffectResult effectResult, Filter filter, String opponent) {
        if (!TriggerConditions.isAboutToBeLost(game, effectResult, filter))
            return false;
        AboutToLoseCardFromTableResult result = (AboutToLoseCardFromTableResult) effectResult;
        PhysicalCard source = result.getSourceCard();
        return opponent.equals(result.getPerformingPlayerId())
                && source != null && opponent.equals(source.getOwner())
                && result.getPreventableCardEffect() != null;
    }

    /** The 'hit' comes from a weapon (weapon destiny), including a permanent weapon. */
    private static boolean isHitByWeapon(SwccgGame game, AboutToBeHitResult result) {
        PhysicalCard hitByCard = result.getHitByCard();
        return result.getHitByPermanentWeapon() != null || result.getCardFiringWeapon() != null
                || (hitByCard != null && Filters.weapon.accepts(game, hitByCard));
    }

    /** The card is a target of the weapon currently being fired. */
    private static boolean isWeaponTarget(SwccgGame game, PhysicalCard card) {
        WeaponFiringState weaponFiringState = game.getGameState().getWeaponFiringState();
        return weaponFiringState != null && weaponFiringState.getTargets() != null
                && weaponFiringState.getTargets().contains(card);
    }

    /**
     * The starship is about to be lost as the result of a destiny just drawn by the opponent: opponent's weapon
     * (weapon destiny, including permanent weapons) targeting it, or asteroid destiny drawn against it.
     */
    private static boolean isStarshipAboutToBeLostDueToOpponentsDestiny(SwccgGame game, EffectResult effectResult, Filter filter, String opponent) {
        if (!TriggerConditions.isAboutToBeLost(game, effectResult, filter))
            return false;
        AboutToLoseCardFromTableResult result = (AboutToLoseCardFromTableResult) effectResult;
        if (result.getPreventableCardEffect() == null)
            return false;
        PhysicalCard starship = result.getCardToBeLost();
        String performingPlayer = result.getPerformingPlayerId();
        // Asteroid destiny
        if (starship.equals(game.getGameState().getStarshipDrawingAsteroidDestinyAgainst())
                && (performingPlayer == null || opponent.equals(performingPlayer)))
            return true;
        // Weapon destiny
        PhysicalCard source = result.getSourceCard();
        if (source != null && Filters.weapon.accepts(game, source) && opponent.equals(source.getOwner()))
            return true;
        WeaponFiringState weaponFiringState = game.getGameState().getWeaponFiringState();
        return isWeaponTarget(game, starship) && weaponFiringState.getCardFiring() != null
                && opponent.equals(weaponFiringState.getCardFiring().getOwner());
    }

    private static Filter otherEpisodeICharacterPresent(Filter yourEpisodeICharacter, PhysicalCard original) {
        return Filters.and(yourEpisodeICharacter, Filters.not(Filters.sameCardId(original)), Filters.presentWith(original));
    }

    private static void appendStarshipProtection(PlayInterruptAction action, PhysicalCard self, PhysicalCard starship) {
        action.appendEffect(new AddUntilEndOfTurnModifierEffect(action,
                new MayNotBeTargetedByWeaponsModifier(self, Filters.sameCardId(starship)),
                GameUtils.getCardLink(starship) + " may not be targeted by weapons"));
        action.appendEffect(new AddUntilEndOfTurnModifierEffect(action,
                new MayNotTargetToBeLostModifier(self, Filters.sameCardId(starship), null),
                GameUtils.getCardLink(starship) + " may not be targeted to be lost"));
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__TRADE_FEDERATION_TACTICS_V__DEPLOY_SIL_UNCH;
        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, SIL_UNCH)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Deploy Sil Unch from Reserve Deck");
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(new DeployCardFromReserveDeckEffect(action, Filters.title(SIL_UNCH), true));
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
