package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;

/**
 * An effect that causes the specified player to 'steal' a card from the opponent's Lost Pile into the player's own
 * Lost Pile (the player becomes the card's owner).
 */
public class StealCardIntoOwnLostPileEffect extends AbstractSuccessfulEffect {
    private final String _playerId;
    private final PhysicalCard _card;

    /**
     * Creates an effect that causes the player to 'steal' the card from the opponent's Lost Pile into own Lost Pile.
     * @param action the action performing this effect
     * @param playerId the player stealing the card
     * @param card the card (in the opponent's Lost Pile)
     */
    public StealCardIntoOwnLostPileEffect(Action action, String playerId, PhysicalCard card) {
        super(action);
        _playerId = playerId;
        _card = card;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        if (GameUtils.getZoneFromZoneTop(_card.getZone()) != Zone.LOST_PILE || _playerId.equals(_card.getZoneOwner()))
            return;

        gameState.sendMessage(_playerId + " 'steals' " + GameUtils.getCardLink(_card) + " into own Lost Pile");
        gameState.removeCardsFromZone(Collections.singleton(_card));
        _card.setOwner(_playerId);
        gameState.addCardToZone(_card, Zone.LOST_PILE, _playerId);
        gameState.reapplyAffectingForCard(game, _card);
    }
}
