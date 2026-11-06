package com.gempukku.swccgo.filters;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/** Occupation prerequisites in a specified card's game text, including text replacements. */
public final class GameTextOccupationFilters {
    private GameTextOccupationFilters() {
    }

    public static Filter occupies(String playerId, PhysicalCard textSource) {
        final Filter occupied = Filters.occupies(playerId);
        final Filter controlled = Filters.controls(playerId);
        return new Filter() {
            @Override
            public boolean accepts(GameState state, ModifiersQuerying modifiers, PhysicalCard location) {
                boolean useControl = modifiers.hasGameTextModification(state, textSource,
                        ModifyGameTextType.OCCUPY_IN_GAME_TEXT_IS_CONTROL);
                return (useControl ? controlled : occupied).accepts(state, modifiers, location);
            }
        };
    }

    public static Filter occupiesWith(String playerId, PhysicalCard textSource, Filter cards) {
        return Filters.and(occupies(playerId, textSource),
                Filters.sameLocationAs(textSource, Filters.and(Filters.owner(playerId), Filters.not(Filters.location), cards)));
    }
}
