package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * A Lose Force effect whose Force loss is caused by (and 'targets') a specific card, e.g. "whenever a character hit by
 * Vader's Lightsaber leaves table, opponent loses 2 Force". Cards that care which card a Force loss is about (e.g.
 * "Force loss from an objective targeting your non-Jedi character") read it via {@link #getTargetedCard()}.
 */
public class LoseForceTargetingCardEffect extends LoseForceEffect {
    private final PhysicalCard _targetedCard;

    /**
     * Creates an effect that causes the specified player to lose Force because of the targeted card.
     * @param action the action performing this effect
     * @param playerToLoseForce the player to lose Force
     * @param amount the amount of Force to lose
     * @param targetedCard the card the Force loss is about
     */
    public LoseForceTargetingCardEffect(Action action, String playerToLoseForce, float amount, PhysicalCard targetedCard) {
        super(action, playerToLoseForce, amount);
        _targetedCard = targetedCard;
    }

    /**
     * Gets the card the Force loss is about.
     * @return the card
     */
    public PhysicalCard getTargetedCard() {
        return _targetedCard;
    }
}
