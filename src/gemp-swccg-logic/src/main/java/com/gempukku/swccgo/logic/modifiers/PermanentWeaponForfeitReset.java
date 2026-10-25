package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgBuiltInCardBlueprint;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;

/**
 * Adjusts a permanent-weapon forfeit reset when Sudden Impact leftover text is in play:
 * [Permanent Weapon] weapons may not reset forfeit to 0; hit characters are forfeit = printed forfeit - 3.
 */
public final class PermanentWeaponForfeitReset {
    private PermanentWeaponForfeitReset() {
    }

    public static float adjustResetValue(SwccgGame game, GameState gameState, SwccgBuiltInCardBlueprint permanentWeapon,
                                         float requestedReset, PhysicalCard target) {
        if (permanentWeapon != null && requestedReset == 0
                && game.getModifiersQuerying().hasFlagActive(gameState, ModifierFlag.PERMANENT_WEAPONS_MAY_NOT_RESET_FORFEIT_TO_ZERO)
                && target.getBlueprint().hasForfeitAttribute()) {
            Float printed = target.getBlueprint().getForfeit();
            if (printed == null) {
                return 0;
            }
            return Math.max(0, printed - 3);
        }
        return requestedReset;
    }
}
