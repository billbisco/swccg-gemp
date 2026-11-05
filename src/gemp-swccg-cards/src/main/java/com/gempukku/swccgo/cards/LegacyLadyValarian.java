package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * Lady Valarian (Virtual Block 8.3): "Unless Jabba on table, 'Jabba' in the game text of your cards (except Hutt
 * Bounty) may be treated as 'Lady Valarian.'"
 * <p>
 * Lady Valarian puts a game text modification on your cards; a curated set of Dark Side cards that mention Jabba
 * use {@link #jabba(PhysicalCard)} in place of {@code Filters.Jabba}.
 */
public final class LegacyLadyValarian {

    public static final String TITLE = "Lady Valarian";

    private LegacyLadyValarian() {
    }

    /**
     * @return true if 'Jabba' in this card's game text may currently be treated as 'Lady Valarian'
     */
    public static boolean mayTreatJabbaAsLadyValarian(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return modifiersQuerying.hasGameTextModification(gameState, self, ModifyGameTextType.LEGACY__LADY_VALARIAN__JABBA_MAY_BE_TREATED_AS_LADY_VALARIAN);
    }

    public static boolean mayTreatJabbaAsLadyValarian(SwccgGame game, PhysicalCard self) {
        return mayTreatJabbaAsLadyValarian(game.getGameState(), game.getModifiersQuerying(), self);
    }

    /**
     * 'Jabba' in the game text of the specified card: Jabba, or Lady Valarian while that card's 'Jabba' may be
     * treated as 'Lady Valarian'.
     * @param self the card whose game text says 'Jabba'
     * @return the filter
     */
    public static Filter jabba(final PhysicalCard self) {
        final Filter ladyValarian = Filters.title(TITLE);
        return Filters.or(Filters.Jabba, new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return ladyValarian.accepts(gameState, modifiersQuerying, physicalCard)
                        && mayTreatJabbaAsLadyValarian(gameState, modifiersQuerying, self);
            }
        });
    }
}
