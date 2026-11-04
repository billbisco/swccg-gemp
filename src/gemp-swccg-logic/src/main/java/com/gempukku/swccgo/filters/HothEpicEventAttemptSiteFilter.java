package com.gempukku.swccgo.filters;

import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.ModifierFlag;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * Filter for the card a player attempts a [Hoth] Epic Event from (e.g. the AT-AT Cannon fired by Target The Main
 * Generator). While that player has {@link ModifierFlag#LEGACY__MAY_NOT_ATTEMPT_HOTH_EPIC_EVENTS_BEYOND_THIRD_MARKER}
 * (Hoth: Main Power Generators (1st Marker) (V), Virtual Block 8.2), cards at a marker site beyond the 3rd Marker
 * (4th through 7th Marker) are rejected.
 */
public class HothEpicEventAttemptSiteFilter extends Filter {
    private static final Filter BEYOND_THIRD_MARKER = Filters.or(Keyword.MARKER_4, Keyword.MARKER_5, Keyword.MARKER_6, Keyword.MARKER_7);

    private final String _playerId;

    private HothEpicEventAttemptSiteFilter(String playerId) {
        _playerId = playerId;
    }

    /**
     * @param playerId the player attempting the Epic Event
     * @return a filter accepting cards that player may attempt a [Hoth] Epic Event from
     */
    public static Filter mayAttemptFrom(String playerId) {
        return new HothEpicEventAttemptSiteFilter(playerId);
    }

    @Override
    public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        if (!modifiersQuerying.hasFlagActive(gameState, ModifierFlag.LEGACY__MAY_NOT_ATTEMPT_HOTH_EPIC_EVENTS_BEYOND_THIRD_MARKER, _playerId))
            return true;
        PhysicalCard location = modifiersQuerying.getLocationThatCardIsAt(gameState, physicalCard);
        return location == null || !BEYOND_THIRD_MARKER.accepts(gameState, modifiersQuerying, location);
    }
}
