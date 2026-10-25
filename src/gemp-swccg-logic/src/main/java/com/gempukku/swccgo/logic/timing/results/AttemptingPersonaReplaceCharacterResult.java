package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.effects.PreventableCardEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * Emitted before a persona replacement is carried out, so it can still be canceled.
 */
public class AttemptingPersonaReplaceCharacterResult extends EffectResult {
    private PhysicalCard _oldCharacter;
    private PhysicalCard _newCharacter;
    private PreventableCardEffect _effect;

    public AttemptingPersonaReplaceCharacterResult(String playerId, PhysicalCard oldCharacter, PhysicalCard newCharacter, PreventableCardEffect effect) {
        super(Type.ATTEMPTING_PERSONA_REPLACE_CHARACTER, playerId);
        _oldCharacter = oldCharacter;
        _newCharacter = newCharacter;
        _effect = effect;
    }

    public PhysicalCard getOldCharacter() {
        return _oldCharacter;
    }

    public PhysicalCard getNewCharacter() {
        return _newCharacter;
    }

    public PreventableCardEffect getPreventableCardEffect() {
        return _effect;
    }

    @Override
    public String getText(SwccgGame game) {
        return "Attempting to replace " + GameUtils.getCardLink(_oldCharacter) + " with " + GameUtils.getCardLink(_newCharacter);
    }
}
