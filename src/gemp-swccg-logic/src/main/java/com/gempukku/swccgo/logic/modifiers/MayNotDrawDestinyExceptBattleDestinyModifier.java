package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.DestinyType;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier for "player may not draw any destinies (except battle destiny)", e.g. Occupied Territory
 * (Virtual Block 7.4): "Prevent opponent from drawing any destinies during the power segment of that battle
 * (except battle destiny)". The condition limits when it applies (e.g. the power segment of a battle).
 *
 * Read by {@link com.gempukku.swccgo.logic.effects.DrawDestinyEffect} through {@link #mayNotDrawDestiny}.
 */
public class MayNotDrawDestinyExceptBattleDestinyModifier extends AbstractModifier {
    /**
     * Creates a modifier that prevents the player from drawing any destiny except battle destiny.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param playerId the player that may not draw destiny
     */
    public MayNotDrawDestinyExceptBattleDestinyModifier(PhysicalCard source, Condition condition, String playerId) {
        super(source, null, null, condition, ModifierType.LEGACY__MAY_NOT_DRAW_DESTINY_EXCEPT_BATTLE_DESTINY, true);
        _playerId = playerId;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return gameState.getSide(_playerId) + " may not draw destiny (except battle destiny)";
    }

    /**
     * Determines if the player is prevented from drawing a destiny of the specified type.
     * @param game the game
     * @param playerId the player drawing destiny
     * @param destinyType the destiny type
     * @return true if the draw is prevented, otherwise false
     */
    public static boolean mayNotDrawDestiny(SwccgGame game, String playerId, DestinyType destinyType) {
        if (destinyType == DestinyType.BATTLE_DESTINY) {
            return false;
        }
        for (Modifier modifier : game.getModifiersQuerying().getModifiers(game.getGameState(), ModifierType.LEGACY__MAY_NOT_DRAW_DESTINY_EXCEPT_BATTLE_DESTINY)) {
            if (modifier.isForPlayer(playerId)) {
                return true;
            }
        }
        return false;
    }
}
