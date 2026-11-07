package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier that prevents cards that just retrieved Force (for example, Interrupts that retrieve exactly 1 Force,
 * judged by the amount the card says to retrieve before modifiers)
 * from being stacked on specified cards (for example, Do They Have A Code Clearance?).
 */
public class RetrievalSourceMayNotBeStackedOnModifier extends AbstractModifier {
    private final Filter _retrievalSourceFilter;
    private final Filter _stackOnFilter;
    private final int _exactAmountRetrieved;

    /**
     * Creates a modifier that prevents cards accepted by the retrieval source filter that just retrieved exactly the
     * specified amount of Force from being stacked on cards accepted by the stack-on filter.
     * @param source the source of the modifier
     * @param retrievalSourceFilter the filter for cards that retrieved Force
     * @param exactAmountRetrieved the exact amount of Force retrieved, or -1 for any amount
     * @param stackOnFilter the filter for cards that may not have them stacked on
     * @param condition the condition that must be fulfilled for the modifier to be in effect, or null
     */
    public RetrievalSourceMayNotBeStackedOnModifier(PhysicalCard source, Filterable retrievalSourceFilter, int exactAmountRetrieved, Filterable stackOnFilter, Condition condition) {
        super(source, null, null, condition, ModifierType.RETRIEVAL_SOURCE_MAY_NOT_BE_STACKED_ON, true);
        _retrievalSourceFilter = Filters.and(retrievalSourceFilter);
        _stackOnFilter = Filters.and(stackOnFilter);
        _exactAmountRetrieved = exactAmountRetrieved;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return null;
    }

    /**
     * Determines if the card that just retrieved Force may not be stacked on the specified card.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param retrievalSource the card that just retrieved Force
     * @param amountRetrieved the amount of Force just retrieved
     * @param stackOn the card to stack on
     * @return true or false
     */
    public boolean mayNotBeStackedOn(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard retrievalSource, int amountRetrieved, PhysicalCard stackOn) {
        return retrievalSource != null && stackOn != null
                && (_exactAmountRetrieved < 0 || _exactAmountRetrieved == amountRetrieved)
                && _retrievalSourceFilter.accepts(gameState, modifiersQuerying, retrievalSource)
                && _stackOnFilter.accepts(gameState, modifiersQuerying, stackOn);
    }
}
