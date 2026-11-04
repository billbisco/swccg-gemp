package com.gempukku.swccgo.cards.set1291.dark;

import com.gempukku.swccgo.cards.AbstractCombatVehicle;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractPermanentWeapon;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Statistic;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.FireWeaponActionBuilder;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalWeaponDestinyModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 9.1
 * Type: Vehicle
 * Subtype: Combat
 * Title: IG-227 'Hailfire' Droid Tank
 */
public class Card1291_032 extends AbstractCombatVehicle {
    public Card1291_032() {
        super(Side.DARK, 3, 4, 4, 5, null, 2, 5, "IG-227 'Hailfire' Droid Tank", Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_9_1, Rarity.V);
        setLore("Enclosed.");
        setGameText("Deploys -1 to same location as a [Separatist] card. Permanent weapon is Missile Battery (may target a character or vehicle for free; draw destiny; add 2 if targeting a vehicle; target hit if total destiny +1 > defense value).");
        addIcons(Icon.SEPARATIST, Icon.PRESENCE, Icon.PILOT, Icon.EPISODE_I, Icon.PERMANENT_WEAPON);
        addModelType(ModelType.HAILFIRE_CLASS_DROID_TANK);
        addKeywords(Keyword.ENCLOSED);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot() {});
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -1, Filters.sameLocationAs(self, Filters.icon(Icon.SEPARATIST))));
        return modifiers;
    }

    // Define "Missile Battery" permanent weapon
    @Override
    protected AbstractPermanentWeapon getGameTextPermanentWeapon() {
        return new AbstractPermanentWeapon("Missile Battery") {
            @Override
            public List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
                FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, this, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                        .targetForFree(Filters.or(Filters.character, targetedAsCharacter, Filters.vehicle), TargetingReason.TO_BE_HIT).finishBuildPrep();
                if (actionBuilder != null) {
                    FireWeaponAction action = actionBuilder.buildFireWeaponWithHitAction(1, 1, Statistic.DEFENSE_VALUE);
                    return Collections.singletonList(action);
                }
                return null;
            }
        };
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalWeaponDestinyModifier(self, Filters.permanentWeaponOf(self), 2, Filters.vehicle));
        return modifiers;
    }
}
