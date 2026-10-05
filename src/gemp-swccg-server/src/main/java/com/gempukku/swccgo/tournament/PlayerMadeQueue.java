package com.gempukku.swccgo.tournament;

import com.gempukku.swccgo.collection.CollectionsManager;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.db.vo.CollectionType;
import com.gempukku.swccgo.game.Player;
import com.gempukku.swccgo.logic.vo.SwccgDeck;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PlayerMadeQueue extends AbstractTournamentQueue implements TournamentQueue {
    public static final String PAIRING_SWISS = "swiss";
    public static final String PAIRING_MATCH_PLAY = "matchPlay";

    private final String _queueId;
    private final String _tournamentQueueName;
    private final String _host;
    private final String _pairing;
    private final int _totalGames;
    private final int _maxPlayers;
    private final int _readyCheckSeconds;
    private final boolean _privateEvent;
    private final long _createdAt;
    private final Map<String, SwccgDeck> _lightDecks = new LinkedHashMap<String, SwccgDeck>();
    private final Map<String, SwccgDeck> _darkDecks = new LinkedHashMap<String, SwccgDeck>();
    private final Set<String> _readyPlayers = new HashSet<String>();
    private boolean _cancelled;
    private boolean _started;
    private long _readyCheckDeadline;

    public PlayerMadeQueue(String queueId, String tournamentQueueName, String host, String format,
                           String pairing, int totalGames, int maxPlayers, int readyCheckSeconds,
                           boolean privateEvent, TournamentPrizes tournamentPrizes) {
        super(0, true, CollectionType.ALL_CARDS, tournamentPrizes, null, format);
        _queueId = queueId;
        _tournamentQueueName = tournamentQueueName;
        _host = host;
        _pairing = pairing;
        _totalGames = ConstructedPairing.clampTotalGames(totalGames);
        _maxPlayers = ConstructedPairing.clampMaxPlayers(maxPlayers);
        _readyCheckSeconds = readyCheckSeconds;
        _privateEvent = privateEvent;
        _createdAt = System.currentTimeMillis();
    }

    public String getQueueId() {
        return _queueId;
    }

    public String getHost() {
        return _host;
    }

    public String getPairing() {
        return _pairing;
    }

    public int getTotalGames() {
        return _totalGames;
    }

    public Map<String, SwccgDeck> getLightDecks() {
        return new HashMap<String, SwccgDeck>(_lightDecks);
    }

    public Map<String, SwccgDeck> getDarkDecks() {
        return new HashMap<String, SwccgDeck>(_darkDecks);
    }

    public List<String> getSignedUpPlayers() {
        return new ArrayList<String>(_players);
    }

    @Override
    public String getPairingDescription() {
        return PAIRING_MATCH_PLAY.equals(_pairing) ? "Single Elimination Match Play" : "Swiss";
    }

    @Override
    public String getTournamentQueueName() {
        return _tournamentQueueName;
    }

    @Override
    public String getStartCondition() {
        if (_readyCheckDeadline > 0)
            return "Ready check";
        if (PAIRING_MATCH_PLAY.equals(_pairing))
            return "Host starts · max " + _maxPlayers;
        return "Host starts · " + _totalGames + " games · max " + _maxPlayers;
    }

    @Override
    public boolean isJoinable() {
        return !_cancelled && !_started && _players.size() < _maxPlayers && _readyCheckDeadline == 0;
    }

    @Override
    public boolean isPlayerMade() {
        return true;
    }

    @Override
    public boolean isHost(String player) {
        return _host.equals(player);
    }

    @Override
    public boolean isStartable(String player) {
        return isHost(player) && !_started && !_cancelled && _players.size() >= 2 && _readyCheckDeadline == 0;
    }

    @Override
    public boolean canCancel(String player) {
        return isHost(player) && !_started && !_cancelled;
    }

    @Override
    public String getSignedUpPlayersCsv() {
        return StringUtils.join(_players, ", ");
    }

    @Override
    public int getMaxPlayers() {
        return _maxPlayers;
    }

    @Override
    public int getReadyCheckSeconds() {
        return _readyCheckSeconds;
    }

    @Override
    public boolean isPrivateEvent() {
        return _privateEvent;
    }

    @Override
    public long getCreatedAt() {
        return _createdAt;
    }

    @Override
    public synchronized void joinPlayer(CollectionsManager collectionsManager, Player player, SwccgDeck lightDeck, SwccgDeck darkDeck) {
        if (!isJoinable())
            return;
        if (_players.contains(player.getName()))
            return;
        if (lightDeck == null || darkDeck == null)
            return;
        _players.add(player.getName());
        _lightDecks.put(player.getName(), lightDeck);
        _darkDecks.put(player.getName(), darkDeck);
        _playerDecks.put(player.getName(), lightDeck);
    }

    @Override
    public synchronized void leavePlayer(CollectionsManager collectionsManager, Player player) {
        if (_started)
            return;
        if (player.getName().equals(_host)) {
            _cancelled = true;
            _players.clear();
            _lightDecks.clear();
            _darkDecks.clear();
            _playerDecks.clear();
            _readyPlayers.clear();
            return;
        }
        super.leavePlayer(collectionsManager, player);
        _lightDecks.remove(player.getName());
        _darkDecks.remove(player.getName());
        _readyPlayers.remove(player.getName());
    }

    @Override
    public synchronized void requestStart(String player) {
        if (!isStartable(player))
            return;
        if (_readyCheckSeconds > 0) {
            _readyCheckDeadline = System.currentTimeMillis() + (_readyCheckSeconds * 1000L);
            _readyPlayers.clear();
        } else {
            _started = true;
        }
    }

    @Override
    public synchronized void confirmReady(String player) {
        if (_readyCheckDeadline <= 0 || _started || _cancelled)
            return;
        if (_players.contains(player))
            _readyPlayers.add(player);
        if (_readyPlayers.containsAll(_players))
            _started = true;
    }

    @Override
    public synchronized void cancel(String player) {
        if (canCancel(player))
            _cancelled = true;
    }

    @Override
    public synchronized boolean isReadyCheckActive() {
        return _readyCheckDeadline > 0 && !_started && !_cancelled;
    }

    @Override
    public synchronized int getReadyCheckSecsRemaining() {
        if (!isReadyCheckActive())
            return -1;
        long remaining = _readyCheckDeadline - System.currentTimeMillis();
        return (int) Math.max(0, remaining / 1000L);
    }

    @Override
    public synchronized boolean hasConfirmedReady(String player) {
        return player != null && _readyPlayers.contains(player);
    }

    @Override
    public synchronized boolean process(TournamentQueueCallback tournamentQueueCallback, CollectionsManager collectionsManager) {
        if (_cancelled) {
            leaveAllPlayers(collectionsManager);
            return true;
        }
        if (_started) {
            return startNow(tournamentQueueCallback);
        }
        if (_readyCheckDeadline > 0 && System.currentTimeMillis() >= _readyCheckDeadline) {
            if (_readyPlayers.size() >= 2) {
                List<String> drop = new ArrayList<String>();
                for (String player : _players) {
                    if (!_readyPlayers.contains(player))
                        drop.add(player);
                }
                for (String player : drop) {
                    _players.remove(player);
                    _lightDecks.remove(player);
                    _darkDecks.remove(player);
                    _playerDecks.remove(player);
                }
                _started = true;
                return startNow(tournamentQueueCallback);
            }
            _readyCheckDeadline = 0;
            _readyPlayers.clear();
        }
        if (!_started && _players.size() >= _maxPlayers && _readyCheckDeadline == 0) {
            if (_readyCheckSeconds > 0) {
                _readyCheckDeadline = System.currentTimeMillis() + (_readyCheckSeconds * 1000L);
                _readyPlayers.clear();
            } else {
                _started = true;
                return startNow(tournamentQueueCallback);
            }
        }
        return false;
    }

    private boolean startNow(TournamentQueueCallback tournamentQueueCallback) {
        if (_players.size() < 2) {
            _started = false;
            _readyCheckDeadline = 0;
            return false;
        }
        PlayerConstructedTournament tournament = new PlayerConstructedTournament(
                _queueId, _tournamentQueueName, _format, _pairing, _totalGames, _privateEvent,
                new ArrayList<String>(_players), _lightDecks, _darkDecks);
        tournamentQueueCallback.createTournament(tournament);
        return true;
    }

    public static Side requiredSide(SwccgDeck deck, com.gempukku.swccgo.game.SwccgCardBlueprintLibrary library, Side expected) {
        if (deck == null)
            return null;
        Side side = deck.getSide(library);
        return side == expected ? side : null;
    }
}
