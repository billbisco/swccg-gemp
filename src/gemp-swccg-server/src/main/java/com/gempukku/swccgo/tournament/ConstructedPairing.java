package com.gempukku.swccgo.tournament;

import com.gempukku.swccgo.common.Side;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * SWCCG Constructed pairing: Swiss 3.7 (re-pair after game 1, Dark pile vs Light pile,
 * never the same opponent on the same side) and Single Elimination Match Play
 * (same opponent twice, switch sides).
 */
public class ConstructedPairing {
    public static final int MAX_PLAYERS = 128;

    public static class GamePair {
        public final String darkPlayer;
        public final String lightPlayer;

        public GamePair(String darkPlayer, String lightPlayer) {
            this.darkPlayer = darkPlayer;
            this.lightPlayer = lightPlayer;
        }
    }

    public static class PairingResult {
        public final List<GamePair> pairs;
        public final Set<String> byes;
        public final boolean cannotContinue;

        public PairingResult(List<GamePair> pairs, Set<String> byes, boolean cannotContinue) {
            this.pairs = pairs;
            this.byes = byes;
            this.cannotContinue = cannotContinue;
        }

        public static PairingResult finished() {
            return new PairingResult(Collections.emptyList(), Collections.emptySet(), true);
        }
    }

    public static int recommendedTotalGames(int playerCount) {
        if (playerCount <= 2)
            return 2;
        if (playerCount <= 4)
            return 4;
        if (playerCount <= 8)
            return 6;
        if (playerCount <= 16)
            return 8;
        if (playerCount <= 32)
            return 10;
        if (playerCount <= 64)
            return 12;
        return 14;
    }

    public static int clampTotalGames(int totalGames) {
        if (totalGames < 2)
            return 2;
        if (totalGames > 14)
            return 14;
        if (totalGames % 2 != 0)
            return totalGames + 1;
        return totalGames;
    }

    public static int clampMaxPlayers(int maxPlayers) {
        if (maxPlayers < 2)
            return 2;
        return Math.min(MAX_PLAYERS, maxPlayers);
    }

    /**
     * First game of a Swiss round: pair by standings, randomly assign Dark/Light,
     * never repeating the same opponent on the same side.
     */
    public static PairingResult pairSwissOpening(List<String> activePlayersOrdered,
                                                 Map<String, Map<Side, Set<String>>> previouslySameSide,
                                                 Set<String> playersWithByes,
                                                 Random random) {
        if (activePlayersOrdered.size() < 2)
            return PairingResult.finished();

        List<String> players = new ArrayList<String>(activePlayersOrdered);
        PairingResult found = trySwissOpening(players, previouslySameSide, playersWithByes, random, 0);
        if (found != null)
            return found;
        return PairingResult.finished();
    }

    /**
     * Second game of a Swiss round: players who were Dark play Light, Light play Dark.
     * Previous bye is eligible and may take either side.
     */
    public static PairingResult pairSwissSwitch(List<String> activePlayers,
                                                Map<String, Side> lastSide,
                                                Map<String, Map<Side, Set<String>>> previouslySameSide,
                                                Set<String> playersWithByes,
                                                Random random) {
        if (activePlayers.size() < 2)
            return PairingResult.finished();

        List<String> switchToLight = new ArrayList<String>();
        List<String> switchToDark = new ArrayList<String>();
        List<String> free = new ArrayList<String>();
        for (String player : activePlayers) {
            Side side = lastSide.get(player);
            if (side == Side.DARK)
                switchToLight.add(player);
            else if (side == Side.LIGHT)
                switchToDark.add(player);
            else
                free.add(player);
        }
        Collections.shuffle(switchToLight, random);
        Collections.shuffle(switchToDark, random);
        Collections.shuffle(free, random);

        PairingResult found = trySwitchPair(switchToDark, switchToLight, free, previouslySameSide, playersWithByes, 0);
        if (found != null)
            return found;
        return PairingResult.finished();
    }

    /**
     * Match Play: pair remaining players into two-game matches. Odd player gets a match bye.
     * Game 1 Dark is random; caller switches sides for game 2.
     */
    public static PairingResult pairMatchPlay(List<String> activePlayers, Random random) {
        if (activePlayers.size() < 2)
            return PairingResult.finished();

        List<String> shuffled = new ArrayList<String>(activePlayers);
        Collections.shuffle(shuffled, random);
        List<GamePair> pairs = new ArrayList<GamePair>();
        Set<String> byes = new HashSet<String>();
        for (int i = 0; i + 1 < shuffled.size(); i += 2) {
            String a = shuffled.get(i);
            String b = shuffled.get(i + 1);
            if (random.nextBoolean())
                pairs.add(new GamePair(a, b));
            else
                pairs.add(new GamePair(b, a));
        }
        if (shuffled.size() % 2 == 1)
            byes.add(shuffled.get(shuffled.size() - 1));
        return new PairingResult(pairs, byes, false);
    }

    public static GamePair switchSides(GamePair pair) {
        return new GamePair(pair.lightPlayer, pair.darkPlayer);
    }

    public static boolean facedOnSide(Map<String, Map<Side, Set<String>>> previouslySameSide,
                                     String player, Side side, String opponent) {
        Map<Side, Set<String>> bySide = previouslySameSide.get(player);
        if (bySide == null)
            return false;
        Set<String> opponents = bySide.get(side);
        return opponents != null && opponents.contains(opponent);
    }

    public static void recordFacing(Map<String, Map<Side, Set<String>>> previouslySameSide,
                                    String darkPlayer, String lightPlayer) {
        addFacing(previouslySameSide, darkPlayer, Side.DARK, lightPlayer);
        addFacing(previouslySameSide, lightPlayer, Side.LIGHT, darkPlayer);
    }

    private static void addFacing(Map<String, Map<Side, Set<String>>> previouslySameSide,
                                  String player, Side side, String opponent) {
        Map<Side, Set<String>> bySide = previouslySameSide.get(player);
        if (bySide == null) {
            bySide = new HashMap<Side, Set<String>>();
            previouslySameSide.put(player, bySide);
        }
        Set<String> opponents = bySide.get(side);
        if (opponents == null) {
            opponents = new HashSet<String>();
            bySide.put(side, opponents);
        }
        opponents.add(opponent);
    }

    private static PairingResult trySwissOpening(List<String> remaining,
                                                 Map<String, Map<Side, Set<String>>> previouslySameSide,
                                                 Set<String> playersWithByes,
                                                 Random random, int depth) {
        if (remaining.isEmpty())
            return new PairingResult(new ArrayList<GamePair>(), new HashSet<String>(), false);
        if (remaining.size() == 1) {
            String bye = remaining.get(0);
            if (playersWithByes.contains(bye) && depth == 0)
                return null;
            Set<String> byes = new HashSet<String>();
            byes.add(bye);
            return new PairingResult(new ArrayList<GamePair>(), byes, false);
        }

        String first = remaining.get(0);
        for (int i = 1; i < remaining.size(); i++) {
            String opponent = remaining.get(i);
            boolean firstDarkOk = !facedOnSide(previouslySameSide, first, Side.DARK, opponent)
                    && !facedOnSide(previouslySameSide, opponent, Side.LIGHT, first);
            boolean firstLightOk = !facedOnSide(previouslySameSide, first, Side.LIGHT, opponent)
                    && !facedOnSide(previouslySameSide, opponent, Side.DARK, first);
            if (!firstDarkOk && !firstLightOk)
                continue;

            List<Boolean> darkFirstChoices = new ArrayList<Boolean>();
            if (firstDarkOk && firstLightOk) {
                if (random.nextBoolean()) {
                    darkFirstChoices.add(Boolean.TRUE);
                    darkFirstChoices.add(Boolean.FALSE);
                } else {
                    darkFirstChoices.add(Boolean.FALSE);
                    darkFirstChoices.add(Boolean.TRUE);
                }
            } else if (firstDarkOk) {
                darkFirstChoices.add(Boolean.TRUE);
            } else {
                darkFirstChoices.add(Boolean.FALSE);
            }

            List<String> next = new ArrayList<String>(remaining);
            next.remove(i);
            next.remove(0);

            for (Boolean darkFirst : darkFirstChoices) {
                PairingResult rest = trySwissOpening(next, previouslySameSide, playersWithByes, random, depth + 1);
                if (rest == null)
                    continue;
                GamePair pair = darkFirst ? new GamePair(first, opponent) : new GamePair(opponent, first);
                List<GamePair> pairs = new ArrayList<GamePair>();
                pairs.add(pair);
                pairs.addAll(rest.pairs);
                return new PairingResult(pairs, rest.byes, false);
            }
        }

        if (remaining.size() > 1 && !playersWithByes.contains(first)) {
            List<String> next = new ArrayList<String>(remaining);
            next.remove(0);
            PairingResult rest = trySwissOpening(next, previouslySameSide, playersWithByes, random, depth + 1);
            if (rest != null && rest.byes.isEmpty()) {
                Set<String> byes = new HashSet<String>();
                byes.add(first);
                return new PairingResult(rest.pairs, byes, false);
            }
        }
        return null;
    }

    private static PairingResult trySwitchPair(List<String> playDark, List<String> playLight, List<String> free,
                                               Map<String, Map<Side, Set<String>>> previouslySameSide,
                                               Set<String> playersWithByes, int freeIndex) {
        if (freeIndex < free.size()) {
            String player = free.get(freeIndex);
            playDark.add(player);
            PairingResult asDark = trySwitchPair(playDark, playLight, free, previouslySameSide, playersWithByes, freeIndex + 1);
            playDark.remove(playDark.size() - 1);
            if (asDark != null)
                return asDark;
            playLight.add(player);
            PairingResult asLight = trySwitchPair(playDark, playLight, free, previouslySameSide, playersWithByes, freeIndex + 1);
            playLight.remove(playLight.size() - 1);
            return asLight;
        }

        List<String> darks = new ArrayList<String>(playDark);
        List<String> lights = new ArrayList<String>(playLight);
        List<GamePair> pairs = new ArrayList<GamePair>();
        Set<String> byes = new HashSet<String>();

        if (!assignSwitchPairs(darks, lights, previouslySameSide, pairs))
            return null;

        if (darks.size() + lights.size() > 1)
            return null;
        if (darks.size() + lights.size() == 1) {
            String bye = darks.isEmpty() ? lights.get(0) : darks.get(0);
            if (playersWithByes.contains(bye) && pairs.isEmpty())
                return null;
            byes.add(bye);
        }
        return new PairingResult(pairs, byes, false);
    }

    private static boolean assignSwitchPairs(List<String> darks, List<String> lights,
                                             Map<String, Map<Side, Set<String>>> previouslySameSide,
                                             List<GamePair> pairs) {
        if (darks.isEmpty() || lights.isEmpty())
            return true;
        String dark = darks.remove(0);
        for (int i = 0; i < lights.size(); i++) {
            String light = lights.get(i);
            if (facedOnSide(previouslySameSide, dark, Side.DARK, light)
                    || facedOnSide(previouslySameSide, light, Side.LIGHT, dark))
                continue;
            lights.remove(i);
            if (assignSwitchPairs(darks, lights, previouslySameSide, pairs)) {
                pairs.add(new GamePair(dark, light));
                return true;
            }
            lights.add(i, light);
        }
        darks.add(0, dark);
        return false;
    }

    public static List<String> orderByStandings(List<ConstructedPlayerStanding> standings, Set<String> dropped) {
        List<ConstructedPlayerStanding> copy = new ArrayList<ConstructedPlayerStanding>(standings);
        Collections.sort(copy, standingsComparator());
        List<String> names = new ArrayList<String>();
        for (ConstructedPlayerStanding standing : copy) {
            if (!dropped.contains(standing.getPlayerName()))
                names.add(standing.getPlayerName());
        }
        return names;
    }

    public static Comparator<ConstructedPlayerStanding> standingsComparator() {
        return new Comparator<ConstructedPlayerStanding>() {
            @Override
            public int compare(ConstructedPlayerStanding a, ConstructedPlayerStanding b) {
                int points = b.getPoints() - a.getPoints();
                if (points != 0)
                    return points;
                int diff = b.getDifferential() - a.getDifferential();
                if (diff != 0)
                    return diff;
                int lost = a.getLostPile() - b.getLostPile();
                if (lost != 0)
                    return lost;
                int hand = a.getHandCards() - b.getHandCards();
                if (hand != 0)
                    return hand;
                if (a.getRandomTiebreak() < b.getRandomTiebreak())
                    return -1;
                if (a.getRandomTiebreak() > b.getRandomTiebreak())
                    return 1;
                return a.getPlayerName().compareTo(b.getPlayerName());
            }
        };
    }

    public static Map<String, Integer> emptyIntMap() {
        return new LinkedHashMap<String, Integer>();
    }
}
