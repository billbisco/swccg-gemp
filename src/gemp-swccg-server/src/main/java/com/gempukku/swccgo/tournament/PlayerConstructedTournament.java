package com.gempukku.swccgo.tournament;

import com.gempukku.swccgo.collection.CollectionsManager;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.competitive.PlayerStanding;
import com.gempukku.swccgo.db.vo.CollectionType;
import com.gempukku.swccgo.draft.Draft;
import com.gempukku.swccgo.logic.vo.SwccgDeck;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class PlayerConstructedTournament implements Tournament {
    public static final long PAIRING_WAIT_MS = 60 * 1000L;

    private final String _tournamentId;
    private final String _tournamentName;
    private final String _format;
    private final String _pairing;
    private final int _totalGames;
    private final boolean _privateEvent;
    private final Set<String> _players = new HashSet<String>();
    private final Set<String> _droppedPlayers = new HashSet<String>();
    private final Map<String, SwccgDeck> _lightDecks;
    private final Map<String, SwccgDeck> _darkDecks;
    private final Map<String, Integer> _points = new HashMap<String, Integer>();
    private final Map<String, Integer> _gamesPlayed = new HashMap<String, Integer>();
    private final Map<String, Integer> _differential = new HashMap<String, Integer>();
    private final Map<String, Integer> _lostPile = new HashMap<String, Integer>();
    private final Map<String, Integer> _handCards = new HashMap<String, Integer>();
    private final Map<String, Long> _randomTiebreak = new HashMap<String, Long>();
    private final Map<String, Integer> _playerByes = new HashMap<String, Integer>();
    private final Map<String, Map<Side, Set<String>>> _previouslySameSide = new HashMap<String, Map<Side, Set<String>>>();
    private final Map<String, Side> _lastSide = new HashMap<String, Side>();
    private final Set<String> _currentlyPlayingPlayers = new HashSet<String>();
    private final List<ConstructedPairing.GamePair> _currentPairs = new ArrayList<ConstructedPairing.GamePair>();
    private final List<ConstructedPairing.GamePair> _matchPairs = new ArrayList<ConstructedPairing.GamePair>();
    private final Map<String, Integer> _matchWins = new HashMap<String, Integer>();
    private final Map<String, Integer> _matchDiff = new HashMap<String, Integer>();
    private final Map<String, Integer> _matchLost = new HashMap<String, Integer>();
    private final Map<String, Integer> _matchHand = new HashMap<String, Integer>();
    private final Set<String> _finishedThisGame = new HashSet<String>();

    private Stage _stage = Stage.PLAYING_GAMES;
    private int _gameNumber;
    private boolean _matchPlayGame2;
    private TournamentTask _nextTask;
    private List<PlayerStanding> _currentStandings;
    private final ReadWriteLock _lock = new ReentrantReadWriteLock();
    private final Random _random = new Random();
    private CollectionType _collectionType = CollectionType.ALL_CARDS;
    private Draft _draft;
    private long _deckBuildStart;
    private String _cubeSoloType;

    public PlayerConstructedTournament(String tournamentId, String tournamentName, String format, String pairing,
                                       int totalGames, boolean privateEvent, List<String> players,
                                       Map<String, SwccgDeck> lightDecks, Map<String, SwccgDeck> darkDecks) {
        this(tournamentId, tournamentName, format, pairing, totalGames, privateEvent, players, lightDecks, darkDecks,
                CollectionType.ALL_CARDS, Stage.PLAYING_GAMES, null);
    }

    public static PlayerConstructedTournament limited(String tournamentId, String tournamentName, String format,
                                                      String pairing, int totalGames, boolean privateEvent,
                                                      List<String> players, CollectionType collectionType,
                                                      Stage stage, Draft draft) {
        return new PlayerConstructedTournament(tournamentId, tournamentName, format, pairing, totalGames, privateEvent,
                players, new HashMap<String, SwccgDeck>(), new HashMap<String, SwccgDeck>(),
                collectionType, stage, draft);
    }

    public PlayerConstructedTournament cubeSolo(String cubeSoloType) {
        _cubeSoloType = cubeSoloType;
        return this;
    }

    private PlayerConstructedTournament(String tournamentId, String tournamentName, String format, String pairing,
                                        int totalGames, boolean privateEvent, List<String> players,
                                        Map<String, SwccgDeck> lightDecks, Map<String, SwccgDeck> darkDecks,
                                        CollectionType collectionType, Stage stage, Draft draft) {
        _tournamentId = tournamentId;
        _tournamentName = tournamentName;
        _format = format;
        _pairing = pairing;
        _totalGames = ConstructedPairing.clampTotalGames(totalGames);
        _privateEvent = privateEvent;
        _lightDecks = new HashMap<String, SwccgDeck>(lightDecks);
        _darkDecks = new HashMap<String, SwccgDeck>(darkDecks);
        _collectionType = collectionType == null ? CollectionType.ALL_CARDS : collectionType;
        _stage = stage;
        _draft = draft;
        if (_stage == Stage.DECK_BUILDING)
            _deckBuildStart = System.currentTimeMillis();
        for (String player : players) {
            _players.add(player);
            _points.put(player, 0);
            _gamesPlayed.put(player, 0);
            _differential.put(player, 0);
            _lostPile.put(player, 0);
            _handCards.put(player, 0);
            _randomTiebreak.put(player, _random.nextLong());
            _playerByes.put(player, 0);
        }
    }

    public boolean isPrivateEvent() {
        return _privateEvent;
    }

    @Override
    public String getTournamentId() {
        return _tournamentId;
    }

    @Override
    public String getFormat() {
        return _format;
    }

    @Override
    public CollectionType getCollectionType() {
        return _collectionType;
    }

    public String getCubeSoloType() {
        return _cubeSoloType;
    }

    public long getDeckBuildEndsAt() {
        if (_stage != Stage.DECK_BUILDING)
            return 0;
        return _deckBuildStart + TournamentProduct.DECK_BUILD_MS;
    }

    public boolean isLimited() {
        return _collectionType != null && !CollectionType.ALL_CARDS.equals(_collectionType);
    }

    public boolean hasLockedDecks(String player) {
        return _lightDecks.get(player) != null && _darkDecks.get(player) != null;
    }

    @Override
    public String getTournamentName() {
        return _tournamentName;
    }

    @Override
    public String getPlayOffSystem() {
        return PlayerMadeQueue.PAIRING_MATCH_PLAY.equals(_pairing) ? "Single Elimination Match Play" : "Swiss";
    }

    @Override
    public Stage getTournamentStage() {
        return _stage;
    }

    @Override
    public int getCurrentRound() {
        return _gameNumber;
    }

    @Override
    public int getPlayersInCompetitionCount() {
        return _players.size() - _droppedPlayers.size();
    }

    @Override
    public boolean isPlayerInCompetition(String player) {
        _lock.readLock().lock();
        try {
            return _stage != Stage.FINISHED && _players.contains(player) && !_droppedPlayers.contains(player);
        } finally {
            _lock.readLock().unlock();
        }
    }

    @Override
    public void reportGameFinished(String winner, String loser, String winnerSide, String loserSide) {
        reportGameFinished(winner, loser, winnerSide, loserSide, null);
    }

    @Override
    public void reportGameFinished(String winner, String loser, String winnerSide, String loserSide, TournamentGameScore score) {
        _lock.writeLock().lock();
        try {
            if (_stage != Stage.PLAYING_GAMES)
                return;
            if (!_currentlyPlayingPlayers.contains(winner) || !_currentlyPlayingPlayers.contains(loser))
                return;
            _currentlyPlayingPlayers.remove(winner);
            _currentlyPlayingPlayers.remove(loser);
            _finishedThisGame.add(winner);
            _finishedThisGame.add(loser);
            addWin(winner, score);
            addLoss(loser, score);

            Side winnerForce = "Dark".equalsIgnoreCase(winnerSide) ? Side.DARK : Side.LIGHT;
            if (winnerForce == Side.DARK)
                ConstructedPairing.recordFacing(_previouslySameSide, winner, loser);
            else
                ConstructedPairing.recordFacing(_previouslySameSide, loser, winner);
            _lastSide.put(winner, winnerForce);
            _lastSide.put(loser, winnerForce == Side.DARK ? Side.LIGHT : Side.DARK);

            if (PlayerMadeQueue.PAIRING_MATCH_PLAY.equals(_pairing)) {
                bump( _matchWins, winner, 1);
                if (score != null) {
                    bump(_matchDiff, winner, score.getWinnerDifferential());
                    bump(_matchLost, winner, score.getWinnerLostPile());
                    bump(_matchLost, loser, score.getLoserLostPile());
                    bump(_matchHand, winner, score.getWinnerHand());
                    bump(_matchHand, loser, score.getLoserHand());
                }
            }
            _currentStandings = null;
        } finally {
            _lock.writeLock().unlock();
        }
    }

    @Override
    public void playerChosenCard(String playerName, String cardId) {
        if (_draft != null)
            _draft.playerChosenCard(playerName, cardId);
    }

    @Override
    public void playerSummittedDeck(String player, SwccgDeck deck) {
    }

    public void lockDecks(String player, SwccgDeck lightDeck, SwccgDeck darkDeck) {
        _lock.writeLock().lock();
        try {
            if (_stage != Stage.DECK_BUILDING)
                return;
            if (!_players.contains(player) || _droppedPlayers.contains(player))
                return;
            if (lightDeck != null)
                _lightDecks.put(player, lightDeck);
            if (darkDeck != null)
                _darkDecks.put(player, darkDeck);
        } finally {
            _lock.writeLock().unlock();
        }
    }

    @Override
    public SwccgDeck getPlayerDeck(String player) {
        return _lightDecks.get(player);
    }

    @Override
    public boolean dropPlayer(String player) {
        _lock.writeLock().lock();
        try {
            if (_currentlyPlayingPlayers.contains(player))
                return false;
            if (_stage == Stage.FINISHED)
                return false;
            if (_droppedPlayers.contains(player))
                return false;
            if (!_players.contains(player))
                return false;
            _droppedPlayers.add(player);
            return true;
        } finally {
            _lock.writeLock().unlock();
        }
    }

    @Override
    public Draft getDraft() {
        return _draft;
    }

    @Override
    public List<PlayerStanding> getCurrentStandings() {
        List<PlayerStanding> cached = _currentStandings;
        if (cached != null)
            return cached;
        _lock.readLock().lock();
        try {
            List<ConstructedPlayerStanding> list = new ArrayList<ConstructedPlayerStanding>();
            for (String player : _players) {
                ConstructedPlayerStanding standing = new ConstructedPlayerStanding(
                        player,
                        n(_points, player),
                        n(_gamesPlayed, player),
                        n(_differential, player),
                        n(_lostPile, player),
                        n(_handCards, player),
                        _randomTiebreak.containsKey(player) ? _randomTiebreak.get(player) : 0L);
                list.add(standing);
            }
            Collections.sort(list, ConstructedPairing.standingsComparator());
            int standing = 0;
            int position = 1;
            ConstructedPlayerStanding last = null;
            for (ConstructedPlayerStanding row : list) {
                if (last == null || ConstructedPairing.standingsComparator().compare(row, last) != 0)
                    standing = position;
                row.setStanding(standing);
                position++;
                last = row;
            }
            _currentStandings = new ArrayList<PlayerStanding>(list);
            return _currentStandings;
        } finally {
            _lock.readLock().unlock();
        }
    }

    @Override
    public boolean advanceTournament(TournamentCallback tournamentCallback, CollectionsManager collectionsManager) {
        _lock.writeLock().lock();
        try {
            boolean changed = false;
            if (_stage == Stage.DRAFT) {
                if (_draft != null) {
                    _draft.advanceDraft(tournamentCallback);
                    if (_draft.isFinished()) {
                        tournamentCallback.broadcastMessage("Drafting in tournament " + _tournamentName
                                + " is finished, starting deck building (30 minutes).", activePlayers());
                        _draft = null;
                        _stage = Stage.DECK_BUILDING;
                        _deckBuildStart = System.currentTimeMillis();
                        changed = true;
                    }
                } else if (_cubeSoloType != null && allCubeSoloFinished(collectionsManager)) {
                    tournamentCallback.broadcastMessage("Drafting in tournament " + _tournamentName
                            + " is finished, starting deck building (30 minutes).", activePlayers());
                    _stage = Stage.DECK_BUILDING;
                    _deckBuildStart = System.currentTimeMillis();
                    changed = true;
                }
            }
            if (_stage == Stage.DECK_BUILDING) {
                boolean timeUp = _deckBuildStart + TournamentProduct.DECK_BUILD_MS < System.currentTimeMillis();
                boolean allLocked = allActiveHaveDecks();
                if (timeUp || allLocked) {
                    dropUnlockedPlayers();
                    if (getPlayersInCompetitionCount() < 2) {
                        tournamentCallback.broadcastMessage("Tournament " + _tournamentName
                                + " cancelled: fewer than 2 players locked both decks.", new ArrayList<String>(_players));
                        finish(tournamentCallback);
                        return true;
                    }
                    _stage = Stage.PLAYING_GAMES;
                    tournamentCallback.broadcastMessage("Deck building in tournament " + _tournamentName
                            + " is finished, pairing games.", activePlayers());
                    changed = true;
                }
            }
            if (_nextTask == null && _stage == Stage.PLAYING_GAMES && _currentlyPlayingPlayers.isEmpty()) {
                if (PlayerMadeQueue.PAIRING_MATCH_PLAY.equals(_pairing) && !_matchPlayGame2 && !_matchPairs.isEmpty())
                    resolveMatchPlayLosers();
                if (isComplete()) {
                    finish(tournamentCallback);
                    return true;
                }
                long wait = waitBeforeNextPairing();
                String label = nextPairingLabel();
                tournamentCallback.broadcastMessage("Tournament " + _tournamentName + " " + label, activePlayers());
                _nextTask = new PairPlayers(wait);
                changed = true;
            }
            if (_nextTask != null && _nextTask.getExecuteAfter() <= System.currentTimeMillis()) {
                TournamentTask task = _nextTask;
                _nextTask = null;
                task.executeTask(tournamentCallback, collectionsManager);
                changed = true;
            }
            return changed;
        } finally {
            _lock.writeLock().unlock();
        }
    }

    private boolean allActiveHaveDecks() {
        for (String player : activePlayers()) {
            if (_lightDecks.get(player) == null || _darkDecks.get(player) == null)
                return false;
        }
        return !activePlayers().isEmpty();
    }

    private void dropUnlockedPlayers() {
        List<String> drop = new ArrayList<String>();
        for (String player : activePlayers()) {
            if (_lightDecks.get(player) == null || _darkDecks.get(player) == null)
                drop.add(player);
        }
        _droppedPlayers.addAll(drop);
    }

    private boolean allCubeSoloFinished(CollectionsManager collectionsManager) {
        if (_collectionType == null)
            return false;
        boolean any = false;
        for (String player : activePlayers()) {
            com.gempukku.swccgo.game.CardCollection collection =
                    collectionsManager.getPlayerCollection(player, _collectionType.getCode());
            if (collection == null)
                return false;
            Object finished = collection.getExtraInformation() == null
                    ? null : collection.getExtraInformation().get("finished");
            if (!isTrue(finished))
                return false;
            any = true;
        }
        return any;
    }

    private boolean isComplete() {
        int remaining = getPlayersInCompetitionCount();
        if (remaining < 2)
            return true;
        if (PlayerMadeQueue.PAIRING_MATCH_PLAY.equals(_pairing))
            return remaining < 2;
        return _gameNumber >= _totalGames;
    }

    private long waitBeforeNextPairing() {
        return PAIRING_WAIT_MS;
    }

    private String nextPairingLabel() {
        return "will start round " + (_gameNumber + 1) + " in 1 minute.";
    }

    private void doPairing(TournamentCallback tournamentCallback) {
        List<String> active = activePlayers();
        if (active.size() < 2) {
            finish(tournamentCallback);
            return;
        }

        ConstructedPairing.PairingResult result;
        if (PlayerMadeQueue.PAIRING_MATCH_PLAY.equals(_pairing))
            result = pairMatchPlay(active);
        else
            result = pairSwiss(active);

        if (result.cannotContinue) {
            tournamentCallback.broadcastMessage("Tournament " + _tournamentName
                    + " ended early: remaining players cannot be paired without repeating the same opponent on the same side.",
                    activePlayers());
            finish(tournamentCallback);
            return;
        }

        _gameNumber++;
        _currentPairs.clear();
        _currentlyPlayingPlayers.clear();
        _finishedThisGame.clear();
        _currentPairs.addAll(result.pairs);

        for (ConstructedPairing.GamePair pair : result.pairs) {
            _currentlyPlayingPlayers.add(pair.darkPlayer);
            _currentlyPlayingPlayers.add(pair.lightPlayer);
            SwccgDeck darkDeck = _darkDecks.get(pair.darkPlayer);
            SwccgDeck lightDeck = _lightDecks.get(pair.lightPlayer);
            tournamentCallback.createGame(pair.darkPlayer, darkDeck, pair.lightPlayer, lightDeck, !_privateEvent);
        }

        if (!result.byes.isEmpty()) {
            tournamentCallback.broadcastMessage("Bye awarded to: " + StringUtils.join(result.byes, ", "), activePlayers());
            for (String bye : result.byes)
                awardBye(bye);
        }
        _currentStandings = null;
    }

    private ConstructedPairing.PairingResult pairSwiss(List<String> active) {
        Set<String> byePlayers = new HashSet<String>();
        for (Map.Entry<String, Integer> entry : _playerByes.entrySet()) {
            if (entry.getValue() != null && entry.getValue() > 0)
                byePlayers.add(entry.getKey());
        }
        boolean opening = (_gameNumber % 2 == 0);
        if (opening) {
            _lastSide.clear();
            List<ConstructedPlayerStanding> standings = constructedStandingsSnapshot();
            List<String> ordered = ConstructedPairing.orderByStandings(standings, _droppedPlayers);
            return ConstructedPairing.pairSwissOpening(ordered, _previouslySameSide, byePlayers, _random);
        }
        return ConstructedPairing.pairSwissSwitch(active, _lastSide, _previouslySameSide, byePlayers, _random);
    }

    private ConstructedPairing.PairingResult pairMatchPlay(List<String> active) {
        if (!_matchPlayGame2) {
            ConstructedPairing.PairingResult opening = ConstructedPairing.pairMatchPlay(active, _random);
            _matchPairs.clear();
            _matchPairs.addAll(opening.pairs);
            _matchWins.clear();
            _matchDiff.clear();
            _matchLost.clear();
            _matchHand.clear();
            _matchPlayGame2 = !opening.cannotContinue && !opening.pairs.isEmpty();
            return opening;
        }
        List<ConstructedPairing.GamePair> switched = new ArrayList<ConstructedPairing.GamePair>();
        for (ConstructedPairing.GamePair pair : _matchPairs) {
            if (_droppedPlayers.contains(pair.darkPlayer) || _droppedPlayers.contains(pair.lightPlayer))
                continue;
            switched.add(ConstructedPairing.switchSides(pair));
        }
        _matchPlayGame2 = false;
        if (switched.isEmpty())
            return ConstructedPairing.PairingResult.finished();
        return new ConstructedPairing.PairingResult(switched, Collections.<String>emptySet(), false);
    }

    private void resolveMatchPlayLosers() {
        for (ConstructedPairing.GamePair pair : _matchPairs) {
            if (_droppedPlayers.contains(pair.darkPlayer) || _droppedPlayers.contains(pair.lightPlayer))
                continue;
            String a = pair.darkPlayer;
            String b = pair.lightPlayer;
            int cmp = compareMatch(a, b);
            String loser = cmp >= 0 ? b : a;
            _droppedPlayers.add(loser);
        }
        _matchPairs.clear();
    }

    private int compareMatch(String a, String b) {
        int wins = n(_matchWins, a) - n(_matchWins, b);
        if (wins != 0)
            return wins;
        int diff = n(_matchDiff, a) - n(_matchDiff, b);
        if (diff != 0)
            return diff;
        int lost = n(_matchLost, b) - n(_matchLost, a);
        if (lost != 0)
            return lost;
        int hand = n(_matchHand, b) - n(_matchHand, a);
        if (hand != 0)
            return hand;
        long ra = _randomTiebreak.containsKey(a) ? _randomTiebreak.get(a) : 0L;
        long rb = _randomTiebreak.containsKey(b) ? _randomTiebreak.get(b) : 0L;
        if (ra < rb)
            return -1;
        if (ra > rb)
            return 1;
        return a.compareTo(b);
    }

    private void awardBye(String player) {
        bump(_points, player, 1);
        bump(_gamesPlayed, player, 1);
        bump(_playerByes, player, 1);
        if (PlayerMadeQueue.PAIRING_MATCH_PLAY.equals(_pairing)) {
            bump(_points, player, 1);
            bump(_gamesPlayed, player, 1);
            bump(_playerByes, player, 1);
        }
    }

    private void addWin(String player, TournamentGameScore score) {
        bump(_points, player, 1);
        bump(_gamesPlayed, player, 1);
        if (score != null) {
            bump(_differential, player, score.getWinnerDifferential());
            bump(_lostPile, player, score.getWinnerLostPile());
            bump(_handCards, player, score.getWinnerHand());
        }
    }

    private void addLoss(String player, TournamentGameScore score) {
        bump(_gamesPlayed, player, 1);
        if (score != null) {
            bump(_lostPile, player, score.getLoserLostPile());
            bump(_handCards, player, score.getLoserHand());
        }
    }

    private void finish(TournamentCallback tournamentCallback) {
        _stage = Stage.FINISHED;
        _currentStandings = null;
        List<ConstructedPlayerStanding> rows = constructedStandingsSnapshot();
        tournamentCallback.broadcastMessage(ConstructedFinishTable.html(_tournamentName, rows), new ArrayList<String>(_players));
    }

    private List<String> activePlayers() {
        List<String> active = new ArrayList<String>();
        for (String player : _players) {
            if (!_droppedPlayers.contains(player))
                active.add(player);
        }
        return active;
    }

    private List<ConstructedPlayerStanding> constructedStandingsSnapshot() {
        List<PlayerStanding> standings = getCurrentStandings();
        List<ConstructedPlayerStanding> result = new ArrayList<ConstructedPlayerStanding>();
        for (PlayerStanding standing : standings) {
            if (standing instanceof ConstructedPlayerStanding)
                result.add((ConstructedPlayerStanding) standing);
        }
        return result;
    }

    private static boolean isTrue(Object value) {
        if (value instanceof Boolean)
            return (Boolean) value;
        return value != null && "true".equalsIgnoreCase(String.valueOf(value));
    }

    private static int n(Map<String, Integer> map, String key) {
        Integer value = map.get(key);
        return value == null ? 0 : value;
    }

    private static void bump(Map<String, Integer> map, String key, int amount) {
        Integer value = map.get(key);
        map.put(key, (value == null ? 0 : value) + amount);
    }

    private class PairPlayers implements TournamentTask {
        private final long _taskStart;

        public PairPlayers(long waitMs) {
            _taskStart = System.currentTimeMillis() + waitMs;
        }

        @Override
        public void executeTask(TournamentCallback tournamentCallback, CollectionsManager collectionsManager) {
            doPairing(tournamentCallback);
        }

        @Override
        public long getExecuteAfter() {
            return _taskStart;
        }
    }
}
