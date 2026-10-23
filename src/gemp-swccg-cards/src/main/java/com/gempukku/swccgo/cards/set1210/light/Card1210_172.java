package com.gempukku.swccgo.cards.set1210.light;

import com.gempukku.swccgo.cards.AbstractCharacterWeapon;
import com.gempukku.swccgo.cards.evaluators.OnTableEvaluator;
import com.gempukku.swccgo.common.DestinyType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Statistic;
import com.gempukku.swccgo.common.TargetingReason;
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
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.ResetPowerUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.RespondableWeaponFiringEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalWeaponDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 1
 * Type: Weapon
 * Subtype: Character
 * Title: Luke's Hunting Rifle
 */
public class Card1210_172 extends AbstractCharacterWeapon {
    public Card1210_172() {
        super(Side.LIGHT, 5, "Luke's Hunting Rifle", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("Czerka 6-2Aug2. Extended barrel provides long-range targeting without expensive laser sight. Sturdy construction, but no match for a Tusken Raider's gaderffi stick.");
        setGameText("Use 1 Force to deploy on Crell, Luke (of ability < 5), or Owen Lars. May target a character or creature using 1 Force. Draw destiny. If destiny + X > defense value, target's power = 0 (lost if a creature), where X = the number of your devices at same and related locations.");
        addIcons(Icon.A_NEW_HOPE);
        addKeywords(Keyword.RIFLE);
        setMatchingCharacterFilter(Filters.or(Filters.Luke, Filters.Owen, Filters.Ardon_Crell));
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDeployCostModifier(self, 1));
        return modifiers;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.or(Filters.Ardon_Crell, Filters.and(Filters.Luke, Filters.abilityLessThan(5)), Filters.Owen));
    }

    @Override
    protected Filter getGameTextValidToUseWeaponFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.or(Filters.Ardon_Crell, Filters.and(Filters.Luke, Filters.abilityLessThan(5)), Filters.Owen);
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, final SwccgGame game, final PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        List<FireWeaponAction> actions = new LinkedList<FireWeaponAction>();

        FireWeaponActionBuilder creatureBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetUsingForce(Filters.creature, 1, TargetingReason.TO_BE_LOST).finishBuildPrep();
        if (creatureBuilder != null) {
            actions.add(creatureBuilder.buildFireWeaponLostAction(1, Statistic.DEFENSE_VALUE));
        }

        FireWeaponActionBuilder characterPrep = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit);
        if (characterPrep != null) {
            FireWeaponAction characterAction = buildResetPowerFireAction(playerId, game, self, forFree, extraForceRequired, sourceCard, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit);
            if (characterAction != null) {
                actions.add(characterAction);
            }
        }

        return actions.isEmpty() ? null : actions;
    }

    private FireWeaponAction buildResetPowerFireAction(final String playerId, final SwccgGame game, final PhysicalCard self, final boolean forFree, final int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, final Filter targetedAsCharacter, final Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        final FireSingleWeaponAction action = new FireSingleWeaponAction(sourceCard, self, null, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit);
        action.setText("Fire " + action.getWeaponTitle(game));

        Filter characterFilter = Filters.and(Filters.or(Filters.character, targetedAsCharacter), Filters.atSameLocation(self), fireAtTargetFilter);

        action.appendTargeting(
                new TargetCardOnTableEffect(action, playerId, "Choose target", TargetingReason.OTHER, characterFilter) {
                    @Override
                    protected boolean isIncludeStackedCardsTargetedByWeaponsAsIfPresent() {
                        return true;
                    }
                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard cardTargeted) {
                        action.addAnimationGroup(cardTargeted);
                        game.getGameState().getWeaponFiringState().setTarget(cardTargeted);

                        int forceToUse = (forFree ? 0 : 1) + extraForceRequired;
                        if (forceToUse > 0) {
                            action.appendCost(
                                    new UseForceEffect(action, playerId, forceToUse));
                        }

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

                                                        gameState.sendMessage("Total destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                        float valueToCompare;
                                                        if (targetedAsCharacter != null && targetedAsCharacter.accepts(game, cardFiredAt)) {
                                                            valueToCompare = defenseValueAsCharacter;
                                                        }
                                                        else {
                                                            valueToCompare = modifiersQuerying.getDefenseValue(gameState, cardFiredAt);
                                                        }
                                                        gameState.sendMessage("Defense value: " + GuiUtils.formatAsString(valueToCompare));

                                                        if (totalDestiny > valueToCompare) {
                                                            gameState.sendMessage("Result: Succeeded");
                                                            if (Filters.creature.accepts(game, cardFiredAt)) {
                                                                action.appendEffect(
                                                                        new LoseCardFromTableEffect(action, cardFiredAt));
                                                            }
                                                            else {
                                                                action.appendEffect(
                                                                        new ResetPowerUntilEndOfTurnEffect(action, cardFiredAt, 0));
                                                            }
                                                        }
                                                        else {
                                                            gameState.sendMessage("Result: Failed");
                                                        }
                                                    }
                                                }
                                        );
                                    }
                                });
                    }
                }
        );

        return action;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalWeaponDestinyModifier(self, new OnTableEvaluator(self, Filters.and(Filters.your(self), Filters.device, Filters.at(Filters.sameOrRelatedLocation(self))))));
        return modifiers;
    }
}
