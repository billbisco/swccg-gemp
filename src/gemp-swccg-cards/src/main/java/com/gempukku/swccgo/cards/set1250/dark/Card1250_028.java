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
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByWeaponsModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotTargetToBeLostModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.AboutToBeHitResult;

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
        final Filter yourStarship = Filters.and(Filters.your(self), Filters.starship);
        if (TriggerConditions.isAboutToBeHitBy(game, effectResult, yourStarship, Filters.owner(game.getOpponent(playerId)))) {
            final AboutToBeHitResult result = (AboutToBeHitResult) effectResult;
            final PhysicalCard starship = result.getCardToBeHit();
            final Filter characterAboard = Filters.and(Filters.your(self), Filters.character, Filters.aboard(starship));
            if (GameConditions.canSpot(game, self, characterAboard)) {
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
                                                action.appendEffect(new AddUntilEndOfTurnModifierEffect(action,
                                                        new MayNotBeTargetedByWeaponsModifier(self, Filters.sameCardId(starship)),
                                                        GameUtils.getCardLink(starship) + " may not be targeted by weapons"));
                                                action.appendEffect(new AddUntilEndOfTurnModifierEffect(action,
                                                        new MayNotTargetToBeLostModifier(self, Filters.sameCardId(starship), null),
                                                        GameUtils.getCardLink(starship) + " may not be targeted to be lost"));
                                            }
                                        });
                            }
                        });
                return Collections.singletonList(action);
            }
        }
        return null;
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
