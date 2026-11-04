package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier that makes cards deploy for half their printed deploy cost (round down), e.g. "deploy ... a Rebel starship
 * for half its printed deploy cost (round down)".
 */
public class ResetDeployCostToHalfPrintedModifier extends AbstractModifier {

    /**
     * Creates the modifier.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose deploy cost is reset
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public ResetDeployCostToHalfPrintedModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, null, affectFilter, condition, ModifierType.UNMODIFIABLE_DEPLOY_COST, true);
    }

    private static float halfPrinted(PhysicalCard card) {
        Float printed = card.getBlueprint().getDeployCost();
        if (printed == null)
            return 0;
        return (float) Math.floor(printed / 2);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "Deploy cost = half printed deploy cost (round down)";
    }

    @Override
    public float getValue(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return halfPrinted(physicalCard);
    }
}
