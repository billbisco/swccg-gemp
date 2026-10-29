package com.gempukku.swccgo.cards.set1220.light;

import com.gempukku.swccgo.cards.AbstractVehicleWeapon;
import com.gempukku.swccgo.cards.evaluators.AbilityOfPilotAboardEvaluator;
import com.gempukku.swccgo.cards.evaluators.MinEvaluator;
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
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.filters.LegacyFilters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.FireWeaponActionBuilder;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalWeaponDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Weapon
 * Subtype: Vehicle
 * Title: Dual Laser Cannon
 */
public class Card1220_168 extends AbstractVehicleWeapon {
    public Card1220_168() {
        super(Side.LIGHT, 3, Title.Dual_Laser_Cannon, Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("Sund x2-100 laser cannon. Popular among outlaw organizations because it is both cheap and powerful. Frequently mounted on T-47 airspeeders.");
        setGameText("Deploy on your T-47. May target a character or vehicle for free. Draw destiny. Character hit, and its forfeit = 0, if destiny +2 > defense value. Vehicle hit if destiny +X > defense value, where X = T-47 pilot's ability (maximum 5).");
        addIcons(Icon.HOTH);
        addKeywords(Keyword.LASER_CANNON);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.T_47);
    }

    @Override
    protected Filter getGameTextValidToUseWeaponFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.T_47;
    }

    @Override
    protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, final SwccgGame game, final PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                .targetForFree(Filters.or(Filters.character, targetedAsCharacter, Filters.vehicle, LegacyFilters.canBeTargetedByWeaponAsVehicle()), TargetingReason.TO_BE_HIT).finishBuildPrep();
        if (actionBuilder != null) {
            Condition characterTargeted = new Condition() {
                @Override
                public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
                    if (gameState.getWeaponFiringState() == null) {
                        return false;
                    }
                    for (PhysicalCard target : gameState.getWeaponFiringState().getTargets()) {
                        if (Filters.character.accepts(gameState.getGame(), target)) {
                            return true;
                        }
                    }
                    return false;
                }
            };
            FireWeaponAction action = actionBuilder.buildFireWeaponWithHitAction(1, Statistic.DEFENSE_VALUE, characterTargeted, true, 0);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalWeaponDestinyModifier(self, 2, Filters.character));
        modifiers.add(new TotalWeaponDestinyModifier(self,
                new MinEvaluator(new AbilityOfPilotAboardEvaluator(self), new ConstantEvaluator(5)),
                Filters.or(Filters.vehicle, LegacyFilters.canBeTargetedByWeaponAsVehicle())));
        return modifiers;
    }
}
