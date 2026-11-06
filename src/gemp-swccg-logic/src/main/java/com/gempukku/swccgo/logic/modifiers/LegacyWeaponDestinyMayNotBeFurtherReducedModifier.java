package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.game.state.WeaponFiringState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collection;

/**
 * Rogue Insertion (Virtual Block 7.2): "weapon destiny draws targeting them are -1 (may not be further reduced)".
 * While a weapon targets a card accepted by the affect filter, each of its weapon destiny draws may not end up lower
 * than the drawn value minus the maximum reduction held by this modifier (the -1 itself is a separate
 * {@link EachWeaponDestinyForWeaponTargetingModifier}). Applied by {@link #applyLimit} from the weapon destiny
 * calculation.
 */
public class LegacyWeaponDestinyMayNotBeFurtherReducedModifier extends AbstractModifier {
    private final float _maxReduction;

    /**
     * Creates the modifier.
     * @param source the source of the modifier
     * @param affectFilter the filter for weapon targets whose weapon destiny draws are protected
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param maxReduction the largest total reduction allowed
     */
    public LegacyWeaponDestinyMayNotBeFurtherReducedModifier(PhysicalCard source, Filterable affectFilter, Condition condition, float maxReduction) {
        super(source, null, affectFilter, condition, ModifierType.LEGACY__WEAPON_DESTINY_TARGETING_MAY_NOT_BE_FURTHER_REDUCED, true);
        _maxReduction = maxReduction;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "Weapon destiny draws targeting it may not be reduced by more than " + GuiUtils.formatAsString(_maxReduction);
    }

    @Override
    public float getValue(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return _maxReduction;
    }

    /**
     * Limits a weapon destiny value when the current weapon targets a card affected by this modifier.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param destinyCard the card drawn for weapon destiny
     * @param value the modified weapon destiny value
     * @return the limited value
     */
    public static float applyLimit(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard destinyCard, float value) {
        WeaponFiringState weaponFiringState = gameState.getWeaponFiringState();
        if (weaponFiringState == null || destinyCard == null)
            return value;
        Collection<PhysicalCard> targets = weaponFiringState.getTargets();
        if (targets == null || targets.isEmpty())
            return value;
        Float drawnValue = destinyCard.getDestinyValueToUse();
        if (drawnValue == null)
            return value;

        Float maxReduction = null;
        for (PhysicalCard target : targets) {
            for (Modifier modifier : modifiersQuerying.getModifiersAffectingCard(gameState, ModifierType.LEGACY__WEAPON_DESTINY_TARGETING_MAY_NOT_BE_FURTHER_REDUCED, target)) {
                float amount = modifier.getValue(gameState, modifiersQuerying, target);
                maxReduction = (maxReduction == null) ? amount : Math.min(maxReduction, amount);
            }
        }
        if (maxReduction == null)
            return value;
        return Math.max(value, drawnValue - maxReduction);
    }
}
