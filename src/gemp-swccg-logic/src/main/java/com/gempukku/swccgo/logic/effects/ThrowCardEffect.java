package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

/**
 * An effect that 'throws' a card (for example, a vehicle or starship 'thrown' by Vader or Galen). A 'thrown' card is
 * 'hit' (through {@link HitCardEffect}, so "about to be 'hit'" responses still apply), unless a
 * {@link com.gempukku.swccgo.logic.modifiers.NotHitWhenThrownModifier} says it is not 'hit' when 'thrown'.
 */
public class ThrowCardEffect extends AbstractSubActionEffect {
    private final PhysicalCard _cardThrown;
    private final PhysicalCard _thrownByCard;
    private boolean _hitPrevented;

    /**
     * Creates an effect that 'throws' a card.
     * @param action the action performing this effect
     * @param cardThrown the card 'thrown'
     * @param thrownByCard the card 'throwing' it (shown as the source of the 'hit')
     */
    public ThrowCardEffect(Action action, PhysicalCard cardThrown, PhysicalCard thrownByCard) {
        super(action);
        _cardThrown = cardThrown;
        _thrownByCard = thrownByCard;
    }

    @Override
    public String getText(SwccgGame game) {
        return "'Throw' " + GameUtils.getCardLink(_cardThrown);
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    /**
     * Determines if the 'thrown' card was not 'hit' because it is not 'hit' when 'thrown'.
     * @return true or false
     */
    public boolean isHitPreventedBecauseNotHitWhenThrown() {
        return _hitPrevented;
    }

    @Override
    protected SubAction getSubAction(SwccgGame game) {
        final SubAction subAction = new SubAction(_action);
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        game.getGameState().sendMessage(GameUtils.getCardLink(_cardThrown) + " is 'thrown' by " + GameUtils.getCardLink(_thrownByCard));
                        if (game.getModifiersQuerying().isNotHitWhenThrown(game.getGameState(), _cardThrown)) {
                            _hitPrevented = true;
                            game.getGameState().sendMessage(GameUtils.getCardLink(_cardThrown) + " is not 'hit' when 'thrown'");
                        }
                        else {
                            subAction.appendEffect(new HitCardEffect(subAction, _cardThrown, _thrownByCard));
                        }
                    }
                });
        return subAction;
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return true;
    }
}
