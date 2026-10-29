package com.gempukku.swccgo.cards.evaluators;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.util.List;

/**
 * An evaluator that returns the highest ability of a pilot aboard the card this weapon (or device) is attached to.
 */
public class AbilityOfPilotAboardEvaluator extends BaseEvaluator {
    private Integer _permCardId;

    /**
     * Creates an evaluator that returns the highest ability of a pilot aboard the attached-to card.
     * @param card the weapon or device attached to the piloted card
     */
    public AbilityOfPilotAboardEvaluator(PhysicalCard card) {
        _permCardId = card.getPermanentCardId();
    }

    @Override
    public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard target) {
        PhysicalCard card = gameState.findCardByPermanentId(_permCardId);
        if (card == null) {
            return 0;
        }
        PhysicalCard attachedTo = card.getAttachedTo();
        if (attachedTo == null) {
            attachedTo = card;
        }
        List<Float> abilities = modifiersQuerying.getAbilityOfPilotsAboard(gameState, attachedTo);
        float ability = 0;
        for (Float curAbility : abilities) {
            if (curAbility != null) {
                ability = Math.max(ability, curAbility);
            }
        }
        return ability;
    }
}
