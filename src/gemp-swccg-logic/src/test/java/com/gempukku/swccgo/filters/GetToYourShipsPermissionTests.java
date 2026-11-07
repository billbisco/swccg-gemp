package com.gempukku.swccgo.filters;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.game.MayUseGetToYourShips;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GetToYourShipsPermissionTests {
    private final GameState state = mock(GameState.class);
    private final ModifiersQuerying modifiers = mock(ModifiersQuerying.class);
    private final Filter filter = new GetToYourShipsStarfighterFilter();

    private PhysicalCard card(CardSubtype subtype, boolean piloted, boolean permitted) {
        SwccgCardBlueprint blueprint = permitted
                ? mock(SwccgCardBlueprint.class, withSettings().extraInterfaces(MayUseGetToYourShips.class))
                : mock(SwccgCardBlueprint.class);
        when(blueprint.getCardSubtype()).thenReturn(subtype);
        PhysicalCard card = mock(PhysicalCard.class);
        when(card.getBlueprint()).thenReturn(blueprint);
        when(modifiers.isPiloted(state, card, false)).thenReturn(piloted);
        return card;
    }

    @Test
    void GetToYourShipsAcceptsOrdinaryUnpilotedStarfighters() {
        assertTrue(filter.accepts(state, modifiers, card(CardSubtype.STARFIGHTER, false, false)));
    }

    @Test
    void GetToYourShipsRejectsPilotedStarfightersWithoutExplicitPermission() {
        assertFalse(filter.accepts(state, modifiers, card(CardSubtype.STARFIGHTER, true, false)));
    }

    @Test
    void GetToYourShipsAcceptsExplicitlyPermittedPilotedStarfighters() {
        assertTrue(filter.accepts(state, modifiers, card(CardSubtype.STARFIGHTER, true, true)));
    }

    @Test
    void GetToYourShipsPermissionDoesNotAllowCapitalShips() {
        assertFalse(filter.accepts(state, modifiers, card(CardSubtype.CAPITAL, true, true)));
        assertFalse(filter.accepts(state, modifiers, card(CardSubtype.CAPITAL, false, false)));
    }
}
