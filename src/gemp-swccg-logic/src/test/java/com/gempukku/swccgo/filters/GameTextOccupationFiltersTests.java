package com.gempukku.swccgo.filters;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameTextOccupationFiltersTests {
    @Test
    void TextReplacementRequiresControlOnlyForItsSourceAndDoesNotChangeGlobalControl() {
        GameState state = mock(GameState.class);
        ModifiersQuerying modifiers = mock(ModifiersQuerying.class);
        PhysicalCard source = mock(PhysicalCard.class);
        PhysicalCard otherSource = mock(PhysicalCard.class);
        PhysicalCard location = mock(PhysicalCard.class);
        when(modifiers.occupiesLocation(state, location, "light")).thenReturn(true);
        when(modifiers.controlsLocation(state, location, "light")).thenReturn(false);
        Filter condition = GameTextOccupationFilters.occupies("light", source);
        assertTrue(condition.accepts(state, modifiers, location));
        when(modifiers.hasGameTextModification(state, source,
                ModifyGameTextType.OCCUPY_IN_GAME_TEXT_IS_CONTROL)).thenReturn(true);
        assertFalse(condition.accepts(state, modifiers, location));
        assertTrue(GameTextOccupationFilters.occupies("light", otherSource).accepts(state, modifiers, location));
        assertFalse(modifiers.controlsLocation(state, location, "light"));
        when(modifiers.controlsLocation(state, location, "light")).thenReturn(true);
        assertTrue(condition.accepts(state, modifiers, location));
    }

    @Test
    void TextReplacementUsesThePlayerWhosePrerequisiteIsBeingChecked() {
        GameState state = mock(GameState.class);
        ModifiersQuerying modifiers = mock(ModifiersQuerying.class);
        PhysicalCard source = mock(PhysicalCard.class);
        PhysicalCard location = mock(PhysicalCard.class);
        when(modifiers.hasGameTextModification(state, source,
                ModifyGameTextType.OCCUPY_IN_GAME_TEXT_IS_CONTROL)).thenReturn(true);
        when(modifiers.controlsLocation(state, location, "dark")).thenReturn(true);
        assertTrue(GameTextOccupationFilters.occupies("dark", source).accepts(state, modifiers, location));
        assertFalse(GameTextOccupationFilters.occupies("light", source).accepts(state, modifiers, location));
    }
}
