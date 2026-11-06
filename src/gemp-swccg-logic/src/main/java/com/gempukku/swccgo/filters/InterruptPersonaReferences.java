package com.gempukku.swccgo.filters;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import java.util.Set;
import java.util.List;

/** Interprets persona references in one Interrupt's modified game text. */
public final class InterruptPersonaReferences {
    private InterruptPersonaReferences() {
    }

    public static boolean mayTreatVaderAsMaulOrMara(SwccgGame game, PhysicalCard source) {
        return source.getBlueprint().getCardCategory() == CardCategory.INTERRUPT
                && game.getModifiersQuerying().hasGameTextModification(game.getGameState(), source,
                ModifyGameTextType.LEGACY__INTERRUPTS__VADER_MAY_BE_MAUL_OR_MARA_JADE);
    }

    /** Capture the interpretation when constructing the action/filter, including lasting results. */
    public static Filter vader(SwccgGame game, PhysicalCard source) {
        return mayTreatVaderAsMaulOrMara(game, source)
                ? Filters.or(Filters.Vader, Filters.Maul, Filters.Mara_Jade) : Filters.Vader;
    }

    /** Personas whose uniqueness/deployment restrictions may satisfy a Vader reference. */
    public static Set<Persona> vaderPersonas(SwccgGame game, PhysicalCard source) {
        return mayTreatVaderAsMaulOrMara(game, source)
                ? Set.of(Persona.VADER, Persona.MAUL, Persona.MARA_JADE) : Set.of(Persona.VADER);
    }

    /** Separate optional interpretations for negated references, counts and lasting results. */
    public static List<Filter> vaderInterpretations(SwccgGame game, PhysicalCard source) {
        return mayTreatVaderAsMaulOrMara(game, source)
                ? List.of(Filters.Vader, Filters.or(Filters.Maul, Filters.Mara_Jade))
                : List.of(Filters.Vader);
    }

    public static boolean isVaderName(SwccgGame game, PhysicalCard source, String name) {
        return "Vader".equals(name) || (mayTreatVaderAsMaulOrMara(game, source)
                && ("Maul".equals(name) || "Mara Jade".equals(name)));
    }
}
