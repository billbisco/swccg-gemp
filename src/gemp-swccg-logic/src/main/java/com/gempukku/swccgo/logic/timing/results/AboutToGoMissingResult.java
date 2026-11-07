package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.effects.PreventableCardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result that is emitted when a character is about to go 'missing'.
 */
public class AboutToGoMissingResult extends EffectResult {
    private PhysicalCard _source;
    private PhysicalCard _cardToGoMissing;
    private PreventableCardEffect _effect;

    /**
     * Creates an effect result that is emitted when the specified character is about to go 'missing'.
     * @param action the action
     * @param cardToGoMissing the character about to go 'missing'
     * @param effect the effect that can be used to prevent the character from going 'missing'
     */
    public AboutToGoMissingResult(Action action, PhysicalCard cardToGoMissing, PreventableCardEffect effect) {
        super(Type.ABOUT_TO_GO_MISSING, action.getPerformingPlayer());
        _source = action.getActionSource();
        _cardToGoMissing = cardToGoMissing;
        _effect = effect;
    }

    /**
     * Gets the source card of the action.
     * @return the source card
     */
    public PhysicalCard getSourceCard() {
        return _source;
    }

    /**
     * Gets the character about to go 'missing'.
     * @return the card
     */
    public PhysicalCard getCardToGoMissing() {
        return _cardToGoMissing;
    }

    /**
     * Gets the interface that can be used to prevent the character from going 'missing'.
     * @return the interface
     */
    public PreventableCardEffect getPreventableCardEffect() {
        return _effect;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "About to make " + GameUtils.getCardLink(_cardToGoMissing) + " go 'missing'";
    }
}
