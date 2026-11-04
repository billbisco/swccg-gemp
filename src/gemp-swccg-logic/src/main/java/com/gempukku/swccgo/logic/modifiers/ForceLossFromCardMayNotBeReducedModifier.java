package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * A modifier that makes Force loss caused by the affected cards unable to be reduced
 * (e.g. Executor: Meditation Chamber (V): "Force loss from [Dagobah] holograms may not be reduced").
 * The affect filter is the card whose action causes the Force loss.
 */
public class ForceLossFromCardMayNotBeReducedModifier extends AbstractModifier {

    /**
     * Creates a modifier that makes Force loss from the affected cards unable to be reduced.
     * @param source the source of the modifier
     * @param affectFilter the cards whose Force loss may not be reduced
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public ForceLossFromCardMayNotBeReducedModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "Force loss may not be reduced", affectFilter, condition, ModifierType.FORCE_LOSS_FROM_CARD_MAY_NOT_BE_REDUCED, true);
    }

    /**
     * Determines if Force loss caused by the specified action may not be reduced because of this kind of modifier.
     * @param game the game
     * @param action the action causing the Force loss
     * @param playerLosingForce the player losing Force
     * @return true or false
     */
    public static boolean isForceLossMayNotBeReduced(SwccgGame game, Action action, String playerLosingForce) {
        if (action == null) {
            return false;
        }
        PhysicalCard sourceOfLoss = action.getActionSource();
        if (sourceOfLoss == null) {
            return false;
        }
        for (Modifier modifier : game.getModifiersQuerying().getModifiersAffectingCard(game.getGameState(), ModifierType.FORCE_LOSS_FROM_CARD_MAY_NOT_BE_REDUCED, sourceOfLoss)) {
            if (modifier.isForPlayer(playerLosingForce)) {
                return true;
            }
        }
        return false;
    }
}
