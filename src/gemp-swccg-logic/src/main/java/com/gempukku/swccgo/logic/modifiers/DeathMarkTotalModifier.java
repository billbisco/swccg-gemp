package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * Contract Killers (Virtual Block 8.1): characters add 1 to their (Virtual Block 8) Death Mark total for each weapon or
 * device they carry. Read by Death Mark through {@link #getDeathMarkTotalModifier}.
 */
public class DeathMarkTotalModifier extends AbstractModifier {

    /**
     * Creates a modifier that adds 1 to the Death Mark total of each affected character for each weapon or device it carries.
     * @param source the source of the modifier
     * @param affectFilter the characters affected
     */
    public DeathMarkTotalModifier(PhysicalCard source, Filterable affectFilter) {
        super(source, "Adds 1 to Death Mark total for each weapon or device carried", Filters.and(Filters.character, affectFilter), ModifierType.LEGACY__DEATH_MARK_TOTAL, true);
    }

    @Override
    public float getValue(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card) {
        return Filters.filterActive(gameState.getGame(), getSource(gameState),
                Filters.and(Filters.or(Filters.weapon, Filters.device), Filters.attachedTo(card))).size();
    }

    /**
     * Gets the total amount added to a character's Death Mark total by these modifiers.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param character the character whose Death Mark total is being calculated
     * @return the amount to add
     */
    public static float getDeathMarkTotalModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard character) {
        float total = 0;
        for (Modifier modifier : modifiersQuerying.getModifiersAffectingCard(gameState, ModifierType.LEGACY__DEATH_MARK_TOTAL, character)) {
            total += modifier.getValue(gameState, modifiersQuerying, character);
        }
        return total;
    }
}
