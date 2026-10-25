package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.timing.AbstractSubActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToPersonaReplaceCharacterResult;
import com.gempukku.swccgo.logic.timing.results.AttemptingPersonaReplaceCharacterResult;
import com.gempukku.swccgo.logic.timing.results.PersonaReplacedCharacterResult;

import java.util.Collection;


/**
 * The effect to persona replace a character with another character of the same persona.
 */
public class PersonaReplaceCharacterEffect extends AbstractSubActionEffect implements PreventableCardEffect {
    private PhysicalCard _oldCharacter;
    private PhysicalCard _newCharacter;
    private java.util.Set<PhysicalCard> _preventedCards = new java.util.HashSet<PhysicalCard>();
    private PersonaReplaceCharacterEffect _that;

    /**
     * Create an effect to persona replace a character with another character of the same persona.
     * @param action the action performing this effect
     * @param oldCharacter the character to be replaced
     * @param newCharacter the character to replace the old character
     */
    public PersonaReplaceCharacterEffect(Action action, PhysicalCard oldCharacter, PhysicalCard newCharacter) {
        super(action);
        _oldCharacter = oldCharacter;
        _newCharacter = newCharacter;
        _that = this;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected SubAction getSubAction(final SwccgGame game) {
        final GameState gameState = game.getGameState();
        final String performingPlayerId = _action.getPerformingPlayer();

        final SubAction subAction = new SubAction(_action);
        subAction.appendEffect(
                new TriggeringResultEffect(subAction, new AttemptingPersonaReplaceCharacterResult(performingPlayerId, _oldCharacter, _newCharacter, _that)));
        subAction.appendEffect(
                new PassthruEffect(subAction) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        if (isEffectOnCardPrevented(_oldCharacter) || isEffectOnCardPrevented(_newCharacter)) {
                            return;
                        }
                        boolean wasUndercover = _oldCharacter.isUndercover();

                        Collection<PhysicalCard> cardsToPlaceInLostPile = gameState.replaceCharacterOnTable(_oldCharacter, _newCharacter);
                        gameState.sendMessage(performingPlayerId + " persona replaces " + GameUtils.getCardLink(_oldCharacter) + " with " + GameUtils.getCardLink(_newCharacter) + (!wasUndercover && _newCharacter.isUndercover() ? " as 'undercover'" : ""));
                        // "When replacing" (See-Threepio) fires while the old persona is still not in Lost Pile.
                        // "Just persona replaced" stays after Lost Pile so later cards can assume that last step is done.
                        subAction.appendEffect(
                                new TriggeringResultEffect(subAction, new AboutToPersonaReplaceCharacterResult(performingPlayerId, _oldCharacter, _newCharacter)));
                        if (!cardsToPlaceInLostPile.isEmpty()) {
                            subAction.appendEffect(
                                    new PutCardsInCardPileEffect(subAction, game, cardsToPlaceInLostPile, Zone.LOST_PILE));
                        }
                        subAction.appendEffect(
                                new TriggeringResultEffect(subAction, new PersonaReplacedCharacterResult(performingPlayerId, _oldCharacter, _newCharacter)));
                    }
                }
        );
        return subAction;
    }

    @Override
    public void preventEffectOnCard(PhysicalCard card) {
        _preventedCards.add(card);
    }

    @Override
    public boolean isEffectOnCardPrevented(PhysicalCard card) {
        return _preventedCards.contains(card);
    }

    @Override
    protected boolean wasActionCarriedOut() {
        return _preventedCards.isEmpty();
    }
}
