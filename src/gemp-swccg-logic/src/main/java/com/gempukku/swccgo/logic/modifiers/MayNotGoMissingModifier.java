package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that prevents affected characters from going 'missing', except when caused by a card owned by the
 * owner of this modifier's source card ("may not go missing (except from your card)").
 */
public class MayNotGoMissingModifier extends AbstractModifier {
    private final boolean _exceptFromOwnersCards;

    /**
     * Creates a modifier that prevents characters accepted by the filter from going 'missing' (except from cards owned
     * by the owner of the source card).
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect, or null
     */
    public MayNotGoMissingModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        this(source, affectFilter, condition, true);
    }

    /**
     * Creates a modifier that prevents characters accepted by the filter from going 'missing'.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect, or null
     * @param exceptFromOwnersCards true if cards owned by the owner of the source card may still cause the 'missing'
     */
    public MayNotGoMissingModifier(PhysicalCard source, Filterable affectFilter, Condition condition, boolean exceptFromOwnersCards) {
        super(source, "May not go missing" + (exceptFromOwnersCards ? " (except from own cards)" : ""), Filters.and(Filters.character, affectFilter), condition, ModifierType.MAY_NOT_GO_MISSING, true);
        _exceptFromOwnersCards = exceptFromOwnersCards;
    }

    /**
     * Determines if this modifier prevents the affected card from going 'missing' due to the specified card.
     * @param gameState the game state
     * @param causingCard the card causing the character to go 'missing', or null
     * @return true if prevented
     */
    public boolean preventsGoingMissingFrom(GameState gameState, PhysicalCard causingCard) {
        if (!_exceptFromOwnersCards) {
            return true;
        }
        PhysicalCard source = getSource(gameState);
        return causingCard == null || source == null || !source.getOwner().equals(causingCard.getOwner());
    }
}
