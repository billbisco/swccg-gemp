package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgBuiltInCardBlueprint;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;

/**
 * Adjusts a weapon forfeit reset when Sudden Impact / Ineffective Maneuver text is in play:
 * those weapons may not reset forfeit to 0; hit characters are forfeit = printed forfeit - 3.
 */
public final class PermanentWeaponForfeitReset {
    private PermanentWeaponForfeitReset() {
    }

    public static float adjustResetValue(SwccgGame game, GameState gameState, SwccgBuiltInCardBlueprint permanentWeapon,
                                         float requestedReset, PhysicalCard target) {
        return adjustResetValue(game, gameState, permanentWeapon, null, requestedReset, target);
    }

    public static float adjustResetValue(SwccgGame game, GameState gameState, SwccgBuiltInCardBlueprint permanentWeapon,
                                         PhysicalCard weaponCard, float requestedReset, PhysicalCard target) {
        if (requestedReset != 0 || !target.getBlueprint().hasForfeitAttribute()) {
            return requestedReset;
        }
        Float printed = target.getBlueprint().getForfeit();
        if (printed == null) {
            return 0;
        }
        if (permanentWeapon != null
                && game.getModifiersQuerying().hasFlagActive(gameState, ModifierFlag.PERMANENT_WEAPONS_MAY_NOT_RESET_FORFEIT_TO_ZERO)) {
            return Math.max(0, printed - 3);
        }
        if (isLightsaberWeapon(game, permanentWeapon, weaponCard)
                && game.getModifiersQuerying().hasFlagActive(gameState, ModifierFlag.LIGHTSABER_WEAPONS_MAY_NOT_RESET_FORFEIT_TO_ZERO)) {
            return Math.max(0, printed - 3);
        }
        return requestedReset;
    }

    private static boolean isLightsaberWeapon(SwccgGame game, SwccgBuiltInCardBlueprint permanentWeapon, PhysicalCard weaponCard) {
        if (permanentWeapon != null && permanentWeapon.hasKeyword(Keyword.LIGHTSABER)) {
            return true;
        }
        return weaponCard != null && Filters.lightsaber.accepts(game, weaponCard);
    }
}
