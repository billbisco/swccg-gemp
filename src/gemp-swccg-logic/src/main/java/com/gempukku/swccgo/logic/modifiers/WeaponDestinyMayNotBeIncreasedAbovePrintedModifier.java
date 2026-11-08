package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.game.state.WeaponFiringState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * "Weapon destiny draws cannot be increased above their printed value" for the weapons accepted by the affect filter
 * (a weapon card, or, for a permanent weapon, the card it is built into). Each weapon destiny draw for such a weapon
 * may not end up higher than the printed destiny of the card drawn; reductions still apply. Applied by
 * {@link #applyLimit} from the weapon destiny calculation.
 */
public class WeaponDestinyMayNotBeIncreasedAbovePrintedModifier extends AbstractModifier {

    /**
     * Creates the modifier.
     * @param source the source of the modifier
     * @param weaponFilter the filter for weapons (or cards with a permanent weapon) whose weapon destiny draws are limited
     * @param condition the condition that must be fulfilled for the modifier to be in effect, or null
     */
    public WeaponDestinyMayNotBeIncreasedAbovePrintedModifier(PhysicalCard source, Filterable weaponFilter, Condition condition) {
        super(source, "Weapon destiny draws may not be increased above their printed value", weaponFilter, condition,
                ModifierType.WEAPON_DESTINY_MAY_NOT_BE_INCREASED_ABOVE_PRINTED_VALUE, true);
    }

    /**
     * Limits a weapon destiny value when the weapon being fired is affected by this modifier.
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
        Float printedValue = destinyCard.getDestinyValueToUse();
        if (printedValue == null || value <= printedValue)
            return value;
        // A weapon card, or (for a permanent weapon) the card firing it
        PhysicalCard weapon = weaponFiringState.getCardFiring();
        if (weapon == null && weaponFiringState.getPermanentWeaponFiring() != null)
            weapon = weaponFiringState.getCardFiringWeapon();
        if (weapon == null)
            return value;
        if (modifiersQuerying.getModifiersAffectingCard(gameState, ModifierType.WEAPON_DESTINY_MAY_NOT_BE_INCREASED_ABOVE_PRINTED_VALUE, weapon).isEmpty())
            return value;
        return printedValue;
    }
}
