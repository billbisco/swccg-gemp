package com.gempukku.swccgo.tournament;

import com.gempukku.swccgo.common.Side;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class ConstructedPairingTests {
    @Test
    public void recommendedTotalGamesFollowsPlayerBuckets() {
        assertEquals(2, ConstructedPairing.recommendedTotalGames(2));
        assertEquals(4, ConstructedPairing.recommendedTotalGames(3));
        assertEquals(4, ConstructedPairing.recommendedTotalGames(4));
        assertEquals(6, ConstructedPairing.recommendedTotalGames(5));
        assertEquals(6, ConstructedPairing.recommendedTotalGames(8));
        assertEquals(8, ConstructedPairing.recommendedTotalGames(9));
        assertEquals(8, ConstructedPairing.recommendedTotalGames(16));
        assertEquals(10, ConstructedPairing.recommendedTotalGames(17));
        assertEquals(12, ConstructedPairing.recommendedTotalGames(33));
        assertEquals(14, ConstructedPairing.recommendedTotalGames(65));
        assertEquals(14, ConstructedPairing.recommendedTotalGames(128));
    }

    @Test
    public void timeoutDifferentialAddsWinnerLifeForceAndHand() {
        assertEquals(17, TournamentGameScore.differentialFor(12, 5, true));
        TournamentGameScore score = TournamentGameScore.fromPiles(12, 5, 3, 8, 10, "Opponent ran out of time");
        assertEquals(17, score.getWinnerDifferential());
        assertTrue(score.isTimeoutOrConcede());
    }

    @Test
    public void concedeDifferentialAddsWinnerLifeForceAndHand() {
        TournamentGameScore score = TournamentGameScore.fromPiles(20, 4, 1, 6, 9, "Opponent conceded");
        assertEquals(24, score.getWinnerDifferential());
        assertTrue(score.isTimeoutOrConcede());
    }

    @Test
    public void naturalWinDifferentialIsLifeForceCappedAt59() {
        assertEquals(18, TournamentGameScore.differentialFor(18, 7, false));
        assertEquals(59, TournamentGameScore.differentialFor(80, 10, false));
        TournamentGameScore score = TournamentGameScore.fromPiles(18, 7, 2, 4, 5, "Depleted opponent's Life Force");
        assertEquals(18, score.getWinnerDifferential());
        assertFalse(score.isTimeoutOrConcede());
    }

    @Test
    public void swissOpeningAssignsOppositeSides() {
        List<String> players = Arrays.asList("Ann", "Ben");
        ConstructedPairing.PairingResult result = ConstructedPairing.pairSwissOpening(
                players, new HashMap<String, Map<Side, Set<String>>>(), new HashSet<String>(), new Random(1));
        assertFalse(result.cannotContinue);
        assertEquals(1, result.pairs.size());
        ConstructedPairing.GamePair pair = result.pairs.get(0);
        assertNotEquals(pair.darkPlayer, pair.lightPlayer);
    }

    @Test
    public void swissSwitchFlipsSidesAndAvoidsSameSideRematch() {
        Map<String, Map<Side, Set<String>>> previous = new HashMap<String, Map<Side, Set<String>>>();
        ConstructedPairing.recordFacing(previous, "Ann", "Ben");
        Map<String, Side> lastSide = new HashMap<String, Side>();
        lastSide.put("Ann", Side.DARK);
        lastSide.put("Ben", Side.LIGHT);
        ConstructedPairing.PairingResult result = ConstructedPairing.pairSwissSwitch(
                Arrays.asList("Ann", "Ben"), lastSide, previous, new HashSet<String>(), new Random(2));
        assertFalse(result.cannotContinue);
        assertEquals(1, result.pairs.size());
        assertEquals("Ben", result.pairs.get(0).darkPlayer);
        assertEquals("Ann", result.pairs.get(0).lightPlayer);
    }

    @Test
    public void swissEndsWhenSameSideRematchIsTheOnlyOption() {
        Map<String, Map<Side, Set<String>>> previous = new HashMap<String, Map<Side, Set<String>>>();
        ConstructedPairing.recordFacing(previous, "Ann", "Ben");
        ConstructedPairing.recordFacing(previous, "Ben", "Ann");
        ConstructedPairing.PairingResult result = ConstructedPairing.pairSwissOpening(
                Arrays.asList("Ann", "Ben"), previous, new HashSet<String>(), new Random(3));
        assertTrue(result.cannotContinue);
    }

    @Test
    public void matchPlayKeepsTheSameOpponentAndCanSwitchSides() {
        ConstructedPairing.PairingResult result = ConstructedPairing.pairMatchPlay(
                Arrays.asList("Ann", "Ben", "Cal"), new Random(4));
        assertFalse(result.cannotContinue);
        assertEquals(1, result.pairs.size());
        assertEquals(1, result.byes.size());
        ConstructedPairing.GamePair game2 = ConstructedPairing.switchSides(result.pairs.get(0));
        assertEquals(result.pairs.get(0).lightPlayer, game2.darkPlayer);
        assertEquals(result.pairs.get(0).darkPlayer, game2.lightPlayer);
    }

    @Test
    public void standingsSortByVpThenDifferentialThenLostPileThenHand() {
        ConstructedPlayerStanding a = new ConstructedPlayerStanding("Ann", 2, 2, 10, 5, 3, 1L);
        ConstructedPlayerStanding b = new ConstructedPlayerStanding("Ben", 2, 2, 8, 1, 0, 2L);
        List<ConstructedPlayerStanding> list = Arrays.asList(b, a);
        Collections.sort(list, ConstructedPairing.standingsComparator());
        assertEquals("Ann", list.get(0).getPlayerName());
        ConstructedPlayerStanding c = new ConstructedPlayerStanding("Cal", 2, 2, 10, 4, 9, 3L);
        list = Arrays.asList(a, c);
        Collections.sort(list, ConstructedPairing.standingsComparator());
        assertEquals("Cal", list.get(0).getPlayerName());
    }
}
