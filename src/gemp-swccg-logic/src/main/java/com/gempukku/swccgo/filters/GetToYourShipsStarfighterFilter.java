package com.gempukku.swccgo.filters;

import com.gempukku.swccgo.game.MayUseGetToYourShips;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/** Starfighters allowed by the ordinary restriction or their own explicit permission. */
public class GetToYourShipsStarfighterFilter extends Filter {
    @Override
    public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card) {
        return Filters.starfighter.accepts(gameState, modifiersQuerying, card)
                && (Filters.unpiloted.accepts(gameState, modifiersQuerying, card)
                || card.getBlueprint() instanceof MayUseGetToYourShips);
    }
}
