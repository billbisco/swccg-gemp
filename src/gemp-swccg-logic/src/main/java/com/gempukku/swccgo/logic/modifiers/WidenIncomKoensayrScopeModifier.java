package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * Systems Will Slip Through Your Fingers (V): Koensayr Manufacturing and Incom Corporation instead affect
 * all your non-unique starships (except squadrons and corvettes) and T-47s.
 *
 * {@link #affectedShips} is a live filter so Hidden Base (V) flipping later is seen by Incom/Koensayr
 * without those cards leaving play.
 */
public class WidenIncomKoensayrScopeModifier extends AbstractModifier {

    public WidenIncomKoensayrScopeModifier(PhysicalCard source) {
        this(source, Filters.or(Filters.title("Incom Corporation"), Filters.title("Koensayr Manufacturing")));
    }

    public WidenIncomKoensayrScopeModifier(PhysicalCard source, Filterable affectFilter) {
        super(source, "Incom / Koensayr affect all non-unique starships (except squadrons and corvettes) and T-47s",
                affectFilter, ModifierType.WIDEN_INCOM_KOENSAYR_SCOPE);
    }

    public WidenIncomKoensayrScopeModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "Incom / Koensayr affect all non-unique starships (except squadrons and corvettes) and T-47s",
                affectFilter, condition, ModifierType.WIDEN_INCOM_KOENSAYR_SCOPE);
    }

    public static boolean isActive(SwccgGame game, PhysicalCard incomOrKoensayr) {
        return isActive(game.getGameState(), game.getModifiersQuerying(), incomOrKoensayr);
    }

    public static boolean isActive(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard incomOrKoensayr) {
        return !modifiersQuerying.getModifiersAffectingCard(gameState,
                ModifierType.WIDEN_INCOM_KOENSAYR_SCOPE, incomOrKoensayr).isEmpty();
    }

    /**
     * Printed Incom/Koensayr ship filter, or the Systems Will Slip Through Your Fingers (V) replacement
     * while this modifier affects {@code incomOrKoensayr}. Evaluated at accept-time.
     */
    public static Filter affectedShips(SwccgGame game, PhysicalCard incomOrKoensayr, Filter defaultShips) {
        return affectedShips(incomOrKoensayr, defaultShips);
    }

    public static Filter affectedShips(PhysicalCard incomOrKoensayr, Filter defaultShips) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                Filter ships = isActive(gameState, modifiersQuerying, incomOrKoensayr)
                        ? widenedShips()
                        : defaultShips;
                return ships.accepts(gameState, modifiersQuerying, physicalCard);
            }
        };
    }

    private static Filter widenedShips() {
        return Filters.or(
                Filters.and(Filters.starship, Filters.non_unique, Filters.except(Filters.or(Filters.squadron, Filters.corvette))),
                Filters.T_47);
    }
}
