package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that prevents affected cards from being excluded from battle by the specified player's cards.
 */
public class MayNotBeExcludedFromBattleByPlayerModifier extends AbstractModifier {

    /**
     * Creates the modifier.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param playerId the player that may not exclude the affected cards from battle
     */
    public MayNotBeExcludedFromBattleByPlayerModifier(PhysicalCard source, Filterable affectFilter, String playerId) {
        this(source, affectFilter, null, playerId);
    }

    /**
     * Creates the modifier.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param playerId the player that may not exclude the affected cards from battle
     */
    public MayNotBeExcludedFromBattleByPlayerModifier(PhysicalCard source, Filterable affectFilter, Condition condition, String playerId) {
        super(source, "May not be excluded from battle by " + playerId, affectFilter, condition, ModifierType.MAY_NOT_BE_EXCLUDED_FROM_BATTLE_BY_PLAYER);
        _playerId = playerId;
    }
}
