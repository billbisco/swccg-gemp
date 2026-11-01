package com.gempukku.swccgo.cards.set1115.dark;

import com.gempukku.swccgo.cards.AbstractCharacterWeapon;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Statistic;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.FireWeaponActionBuilder;
import com.gempukku.swccgo.logic.actions.FireSingleWeaponAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.HitCardEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondableWeaponFiringEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.common.DestinyType;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 15: Galactic Frontier
 * Type: Weapon
 * Subtype: Character
 * Title: SD-17 Homing Missile
 */
public class Card1115_060 extends AbstractCharacterWeapon {
    public Card1115_060() {
        super(Side.DARK, 5, Title.SD_17_Homing_Missile, Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_ORIGINAL_SET_15, Rarity.V);
        setLore("Originally developed by smugglers and pirates as defense against starfighters. Adapted by the Empire to serve as onboard defense for slow moving bombers.");
        setGameText("Deploy on Boba Fett, Jodo Kast, or your character with a Jet Pack. May place this weapon in Used Pile to target a character or vehicle using 2 Force. Draw destiny. Character lost if destiny -1 > defense value. Vehicle hit if destiny +2 > defense value.");
        addIcons(Icon.DEATH_STAR_II);
        addKeywords(Keyword.MISSILE);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.or(Filters.Boba_Fett, Filters.Jodo, Filters.hasAttached(Filters.Jet_Pack)));
    }

    @Override
    protected Filter getGameTextValidToUseWeaponFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.or(Filters.Boba_Fett, Filters.Jodo, Filters.hasAttached(Filters.Jet_Pack));
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, final SwccgGame game, final PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetUsingForce(Filters.or(Filters.character, targetedAsCharacter, Filters.vehicle), 2, TargetingReason.TO_BE_HIT).finishBuildPrep();
        if (actionBuilder == null) {
            return null;
        }

        final FireSingleWeaponAction action = new FireSingleWeaponAction(sourceCard, self, null, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit);
        action.setText("Fire " + action.getWeaponTitle(game));
        action.appendCost(
                new PlaceCardInUsedPileFromTableEffect(action, self, false, Zone.LOST_PILE));
        action.appendTargeting(
                new TargetCardOnTableEffect(action, playerId, "Choose target", Filters.and(Filters.or(Filters.character, targetedAsCharacter, Filters.vehicle), Filters.canBeTargetedBy(self, TargetingReason.TO_BE_HIT), fireAtTargetFilter != null ? fireAtTargetFilter : Filters.any)) {
                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard cardTargeted) {
                        action.addAnimationGroup(cardTargeted);
                        game.getGameState().getWeaponFiringState().setTarget(cardTargeted);
                        action.appendCost(
                                new UseForceEffect(action, playerId, 2));
                        action.allowResponses("Fire " + GameUtils.getCardLink(self) + " at " + GameUtils.getCardLink(cardTargeted),
                                new RespondableWeaponFiringEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        final PhysicalCard cardFiredAt = targetingAction.getPrimaryTargetCard(targetGroupId);
                                        game.getGameState().getWeaponFiringState().setTarget(cardFiredAt);
                                        action.appendEffect(
                                                new DrawDestinyEffect(action, playerId, 1, DestinyType.WEAPON_DESTINY) {
                                                    @Override
                                                    protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                        return Collections.singletonList(cardFiredAt);
                                                    }
                                                    @Override
                                                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                        GameState gameState = game.getGameState();
                                                        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                                                        if (totalDestiny == null) {
                                                            gameState.sendMessage("Result: Failed due to failed weapon destiny draw");
                                                            return;
                                                        }
                                                        float valueToCompare;
                                                        if (targetedAsCharacter != null && targetedAsCharacter.accepts(game, cardFiredAt)) {
                                                            valueToCompare = defenseValueAsCharacter;
                                                        }
                                                        else {
                                                            valueToCompare = modifiersQuerying.getDefenseValue(game.getGameState(), cardFiredAt);
                                                        }
                                                        gameState.sendMessage("Total destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                        gameState.sendMessage("Defense value: " + GuiUtils.formatAsString(valueToCompare));
                                                        if (Filters.character.accepts(game, cardFiredAt)
                                                                || (targetedAsCharacter != null && targetedAsCharacter.accepts(game, cardFiredAt))) {
                                                            if ((totalDestiny - 1) > valueToCompare) {
                                                                gameState.sendMessage("Result: Succeeded");
                                                                game.getModifiersQuerying().hitOrMadeLostByWeapon(cardFiredAt, self);
                                                                action.appendEffect(
                                                                        new LoseCardFromTableEffect(action, cardFiredAt));
                                                            }
                                                            else {
                                                                gameState.sendMessage("Result: Failed");
                                                            }
                                                        }
                                                        else if (Filters.vehicle.accepts(game, cardFiredAt)) {
                                                            if ((totalDestiny + 2) > valueToCompare) {
                                                                gameState.sendMessage("Result: Succeeded");
                                                                action.appendEffect(
                                                                        new HitCardEffect(action, cardFiredAt, self));
                                                            }
                                                            else {
                                                                gameState.sendMessage("Result: Failed");
                                                            }
                                                        }
                                                    }
                                                }
                                        );
                                    }
                                });
                    }
                }
        );
        return Collections.singletonList(action);
    }
}
