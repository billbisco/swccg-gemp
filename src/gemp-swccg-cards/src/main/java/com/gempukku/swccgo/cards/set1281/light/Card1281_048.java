package com.gempukku.swccgo.cards.set1281.light;

import com.gempukku.swccgo.cards.AbstractCharacterWeapon;
import com.gempukku.swccgo.common.DestinyType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.FireSingleWeaponAction;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.FireWeaponActionBuilder;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.ExcludeFromBattleEffect;
import com.gempukku.swccgo.logic.effects.HitCardAndResetPowerEffect;
import com.gempukku.swccgo.logic.effects.RespondableWeaponFiringEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.1
 * Type: Weapon
 * Subtype: Character
 * Title: Jawa Ion Gun (V)
 */
public class Card1281_048 extends AbstractCharacterWeapon {
    public Card1281_048() {
        super(Side.LIGHT, 3, Title.Jawa_Ion_Gun, Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_8_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("Cobbled-together weapon built from salvaged power cells, droid restraining bolts and an ion accelerator. Fires a stream of ion energy that stuns droids and irritates others.");
        setGameText("Use 1 Force to deploy on your Jawa. May target a character or vehicle. Draw destiny. Vehicle hit and power = 0, if destiny + X > defense value, where X = the number of Jawas present. Character excluded from battle if destiny = defense value.");
        addKeywords(Keyword.JAWA_WEAPON);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDeployCostModifier(self, 1));
        return modifiers;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.Jawa);
    }

    @Override
    protected Filter getGameTextValidToUseWeaponFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.Jawa;
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(final String playerId, final SwccgGame game, final PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, final Filter targetedAsCharacter, final Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        final FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetForFree(Filters.or(Filters.character, targetedAsCharacter), TargetingReason.OTHER)
                .targetForFree(Filters.vehicle, TargetingReason.TO_BE_HIT).finishBuildPrep();
        if (actionBuilder == null) {
            return null;
        }

        final FireSingleWeaponAction action = new FireSingleWeaponAction(sourceCard, self, null, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit);
        action.setText("Fire " + action.getWeaponTitle(game));

        // Choose target(s)
        action.appendTargeting(
                new TargetCardOnTableEffect(action, playerId, "Choose target", actionBuilder.getTargetFiltersMap(action.getCardFiringWeapon())) {
                    @Override
                    protected boolean isIncludeStackedCardsTargetedByWeaponsAsIfPresent() {
                        return true;
                    }

                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard cardTargeted) {
                        action.addAnimationGroup(cardTargeted);
                        game.getGameState().getWeaponFiringState().setTarget(cardTargeted);

                        // Pay cost(s)
                        float forceToUse = actionBuilder.getUseForceCost(action.getCardFiringWeapon(), cardTargeted);
                        if (forceToUse > 0) {
                            action.appendCost(
                                    new UseForceEffect(action, playerId, forceToUse));
                        }

                        // Allow response(s)
                        action.allowResponses("Fire " + GameUtils.getCardLink(self) + " at " + GameUtils.getCardLink(cardTargeted),
                                new RespondableWeaponFiringEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        final PhysicalCard cardFiredAt = targetingAction.getPrimaryTargetCard(targetGroupId);
                                        game.getGameState().getWeaponFiringState().setTarget(cardFiredAt);

                                        // Perform result(s)
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
                                                        gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));

                                                        if (Filters.vehicle.accepts(game, cardFiredAt)) {
                                                            int x = Filters.countActive(game, self, Filters.and(Filters.Jawa, Filters.present(self)));
                                                            float defenseValue = modifiersQuerying.getDefenseValue(gameState, cardFiredAt);
                                                            gameState.sendMessage("X (Jawas present): " + x);
                                                            gameState.sendMessage("Defense value: " + GuiUtils.formatAsString(defenseValue));
                                                            if (totalDestiny + x > defenseValue) {
                                                                gameState.sendMessage("Result: Succeeded");
                                                                action.appendEffect(
                                                                        new HitCardAndResetPowerEffect(action, cardFiredAt, 0, self, null, action.getCardFiringWeapon()));
                                                            }
                                                            else {
                                                                gameState.sendMessage("Result: Failed");
                                                            }
                                                        }
                                                        else {
                                                            float defenseValue;
                                                            if (targetedAsCharacter != null && targetedAsCharacter.accepts(game, cardFiredAt)) {
                                                                defenseValue = defenseValueAsCharacter;
                                                            }
                                                            else {
                                                                defenseValue = modifiersQuerying.getDefenseValue(gameState, cardFiredAt);
                                                            }
                                                            gameState.sendMessage("Defense value: " + GuiUtils.formatAsString(defenseValue));
                                                            if (totalDestiny == defenseValue) {
                                                                gameState.sendMessage("Result: Succeeded");
                                                                action.appendEffect(
                                                                        new ExcludeFromBattleEffect(action, cardFiredAt));
                                                            }
                                                            else {
                                                                gameState.sendMessage("Result: Failed");
                                                            }
                                                        }
                                                    }
                                                });
                                    }
                                });
                    }
                });
        return Collections.singletonList(action);
    }
}
