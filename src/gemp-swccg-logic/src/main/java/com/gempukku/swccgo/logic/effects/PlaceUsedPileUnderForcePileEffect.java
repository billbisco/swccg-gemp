package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.timing.AbstractStandardEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.ArrayList;
import java.util.List;

/**
 * Places a player's Used Pile under that player's Force Pile, keeping Used Pile order.
 */
public class PlaceUsedPileUnderForcePileEffect extends AbstractStandardEffect {
    private String _player;

    public PlaceUsedPileUnderForcePileEffect(Action action, String player) {
        super(action);
        _player = player;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return !game.getGameState().getUsedPile(_player).isEmpty();
    }

    @Override
    protected FullEffectResult playEffectReturningResult(SwccgGame game) {
        GameState gameState = game.getGameState();
        List<PhysicalCard> usedPile = gameState.getUsedPile(_player);
        if (usedPile.isEmpty()) {
            return new FullEffectResult(false);
        }
        List<PhysicalCard> cardsToMove = new ArrayList<PhysicalCard>(usedPile);
        while (!usedPile.isEmpty()) {
            gameState.removeCardFromZone(usedPile.get(usedPile.size() - 1), usedPile.size() > 1, false);
        }
        boolean firstCard = true;
        while (!cardsToMove.isEmpty()) {
            PhysicalCard card = cardsToMove.get(0);
            gameState.addCardToZone(card, Zone.FORCE_PILE, _player, true, false, !firstCard);
            cardsToMove.remove(card);
            firstCard = false;
        }
        gameState.sendMessage(_player + " places Used Pile under Force Pile");
        return new FullEffectResult(true);
    }
}
