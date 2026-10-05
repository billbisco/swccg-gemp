package com.gempukku.swccgo.tournament;

import com.gempukku.swccgo.logic.vo.SwccgDeck;

import java.util.Collection;

public interface TournamentCallback {
    public void createGame(String playerOne, SwccgDeck deckOne, String playerTwo, SwccgDeck deckTwo, boolean allowSpectators);

    public void broadcastMessage(String message);

    default void broadcastMessage(String message, Collection<String> toWhom) {
        broadcastMessage(message);
    }
}
