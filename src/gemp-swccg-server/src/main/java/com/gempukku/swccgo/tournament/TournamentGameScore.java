package com.gempukku.swccgo.tournament;

/**
 * Per-game scoring snapshot for player-hosted Constructed events.
 * Timeout (and concede) differential is the winner's Life Force plus cards in hand.
 * Natural empty-Life-Force wins use remaining Life Force only, capped at 59.
 */
public class TournamentGameScore {
    public static final int NATURAL_DIFFERENTIAL_CAP = 59;

    private final int winnerDifferential;
    private final int winnerLostPile;
    private final int winnerHand;
    private final int loserLostPile;
    private final int loserHand;
    private final boolean timeoutOrConcede;

    public TournamentGameScore(int winnerDifferential, int winnerLostPile, int winnerHand,
                               int loserLostPile, int loserHand, boolean timeoutOrConcede) {
        this.winnerDifferential = winnerDifferential;
        this.winnerLostPile = winnerLostPile;
        this.winnerHand = winnerHand;
        this.loserLostPile = loserLostPile;
        this.loserHand = loserHand;
        this.timeoutOrConcede = timeoutOrConcede;
    }

    public int getWinnerDifferential() {
        return winnerDifferential;
    }

    public int getWinnerLostPile() {
        return winnerLostPile;
    }

    public int getWinnerHand() {
        return winnerHand;
    }

    public int getLoserLostPile() {
        return loserLostPile;
    }

    public int getLoserHand() {
        return loserHand;
    }

    public boolean isTimeoutOrConcede() {
        return timeoutOrConcede;
    }

    public static boolean isTimeoutOrConcedeReason(String winReason) {
        if (winReason == null)
            return false;
        String lower = winReason.toLowerCase();
        return lower.contains("timeout") || lower.contains("ran out of time") || lower.contains("conceded");
    }

    public static int differentialFor(int winnerLifeForce, int winnerHand, boolean timeoutOrConcede) {
        if (timeoutOrConcede)
            return Math.max(0, winnerLifeForce) + Math.max(0, winnerHand);
        return Math.min(NATURAL_DIFFERENTIAL_CAP, Math.max(0, winnerLifeForce));
    }

    public static TournamentGameScore fromPiles(int winnerLifeForce, int winnerHand, int winnerLostPile,
                                                int loserHand, int loserLostPile, String winReason) {
        boolean timeoutOrConcede = isTimeoutOrConcedeReason(winReason);
        return new TournamentGameScore(
                differentialFor(winnerLifeForce, winnerHand, timeoutOrConcede),
                Math.max(0, winnerLostPile),
                Math.max(0, winnerHand),
                Math.max(0, loserLostPile),
                Math.max(0, loserHand),
                timeoutOrConcede);
    }
}
