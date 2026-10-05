package com.gempukku.swccgo.tournament;

import com.gempukku.swccgo.competitive.PlayerStanding;

public class ConstructedPlayerStanding extends PlayerStanding {
    private final int _differential;
    private final int _lostPile;
    private final int _handCards;
    private final long _randomTiebreak;

    public ConstructedPlayerStanding(String playerName, int points, int gamesPlayed,
                                     int differential, int lostPile, int handCards, long randomTiebreak) {
        super(playerName, points, gamesPlayed);
        _differential = differential;
        _lostPile = lostPile;
        _handCards = handCards;
        _randomTiebreak = randomTiebreak;
    }

    public int getDifferential() {
        return _differential;
    }

    public int getLostPile() {
        return _lostPile;
    }

    public int getHandCards() {
        return _handCards;
    }

    public long getRandomTiebreak() {
        return _randomTiebreak;
    }
}
