package com.gempukku.swccgo.filters;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import org.junit.jupiter.api.Test;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InterruptPersonaReferencesTests {
    private final SwccgGame game = mock(SwccgGame.class, RETURNS_DEEP_STUBS);
    private final PhysicalCard source = mock(PhysicalCard.class, RETURNS_DEEP_STUBS);
    private final PhysicalCard target = mock(PhysicalCard.class);

    private void permission(boolean enabled) {
        when(source.getBlueprint().getCardCategory()).thenReturn(CardCategory.INTERRUPT);
        when(game.getModifiersQuerying().hasGameTextModification(game.getGameState(), source,
                ModifyGameTextType.LEGACY__INTERRUPTS__VADER_MAY_BE_MAUL_OR_MARA_JADE)).thenReturn(enabled);
    }

    private void persona(Persona persona) {
        when(game.getModifiersQuerying().hasPersona(game.getGameState(), target, persona)).thenReturn(true);
    }

    @Test
    void ModifiedInterruptRetainsVaderAndAllowsMaulOrMara() {
        permission(true);
        for (Persona persona : new Persona[]{Persona.VADER, Persona.MAUL, Persona.MARA_JADE}) {
            persona(persona);
            assertTrue(InterruptPersonaReferences.vader(game, source).accepts(game, target));
            when(game.getModifiersQuerying().hasPersona(game.getGameState(), target, persona)).thenReturn(false);
        }
    }

    @Test
    void UnmodifiedInterruptDoesNotGainOtherPersonas() {
        permission(false);
        persona(Persona.MAUL);
        persona(Persona.MARA_JADE);
        assertFalse(InterruptPersonaReferences.vader(game, source).accepts(game, target));
    }

    @Test
    void NonInterruptCannotUsePersonaExpansion() {
        permission(true);
        when(source.getBlueprint().getCardCategory()).thenReturn(CardCategory.EFFECT);
        persona(Persona.MAUL);
        assertFalse(InterruptPersonaReferences.vader(game, source).accepts(game, target));
    }

    @Test
    void UnrelatedCharactersAndUnmodifiedInterruptsStayExcluded() {
        permission(true);
        assertFalse(InterruptPersonaReferences.vader(game, source).accepts(game, target));
        PhysicalCard other = mock(PhysicalCard.class, RETURNS_DEEP_STUBS);
        when(other.getBlueprint().getCardCategory()).thenReturn(CardCategory.INTERRUPT);
        persona(Persona.MAUL);
        assertFalse(InterruptPersonaReferences.vader(game, other).accepts(game, target));
    }

    @Test
    void ConstructedResultFilterRetainsItsChosenInterpretation() {
        permission(true);
        Filter resultFilter = InterruptPersonaReferences.vader(game, source);
        permission(false);
        persona(Persona.MARA_JADE);
        assertTrue(resultFilter.accepts(game, target));
        assertFalse(InterruptPersonaReferences.vader(game, source).accepts(game, target));
    }

    @Test
    void ApprenticeNameUsesSameScopedPermissionAndLeavesOtherNamesUnchanged() {
        permission(true);
        assertTrue(InterruptPersonaReferences.isVaderName(game, source, "Vader"));
        assertTrue(InterruptPersonaReferences.isVaderName(game, source, "Maul"));
        assertTrue(InterruptPersonaReferences.isVaderName(game, source, "Mara Jade"));
        assertFalse(InterruptPersonaReferences.isVaderName(game, source, "Anakin"));
        permission(false);
        assertFalse(InterruptPersonaReferences.isVaderName(game, source, "Maul"));
    }

    @Test
    void DeploymentPersonaRestrictionsUseAllPermittedAlternatives() {
        permission(true);
        assertEquals(Set.of(Persona.VADER, Persona.MAUL, Persona.MARA_JADE),
                InterruptPersonaReferences.vaderPersonas(game, source));
        permission(false);
        assertEquals(Set.of(Persona.VADER), InterruptPersonaReferences.vaderPersonas(game, source));
    }
}
