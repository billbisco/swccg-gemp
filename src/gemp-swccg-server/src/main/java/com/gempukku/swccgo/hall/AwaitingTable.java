package com.gempukku.swccgo.hall;

import com.gempukku.swccgo.db.vo.CollectionType;
import com.gempukku.swccgo.db.vo.League;
import com.gempukku.swccgo.game.SwccgFormat;
import com.gempukku.swccgo.game.SwccgGameParticipant;
import com.gempukku.swccgo.league.LeagueSeriesData;
import com.gempukku.swccgo.logic.vo.SwccgDeck;

import java.util.*;

public class AwaitingTable {
    private CollectionType _collectionType;
    private SwccgFormat _swccgFormat;
    private League _league;
    private LeagueSeriesData _leagueSeries;
    private String _tableDesc;
    private Map<String, SwccgGameParticipant> _players = new HashMap<String, SwccgGameParticipant>();
    private boolean _isPrivate;
    private boolean _isInviteOnly;
    private HallGameTimer _gameTimer;
    private SwccgDeck _aiDeck;
    private String _aiPlayerId;
    private String _aiSkill;
    private String _startedGameId;

    private int _capacity = 2;

    public AwaitingTable(SwccgFormat swccgFormat, CollectionType collectionType, League league, LeagueSeriesData leagueSeries, String tableDesc, boolean isPrivate) {
        this(swccgFormat, collectionType, league, leagueSeries, tableDesc, isPrivate, false, null);
    }

    public AwaitingTable(SwccgFormat swccgFormat, CollectionType collectionType, League league, LeagueSeriesData leagueSeries,
            String tableDesc, boolean isPrivate, boolean isInviteOnly, HallGameTimer gameTimer) {
        _swccgFormat = swccgFormat;
        _collectionType = collectionType;
        _league = league;
        _leagueSeries = leagueSeries;
        _tableDesc = tableDesc;
        _isPrivate = isPrivate;
        _isInviteOnly = isInviteOnly;
        _gameTimer = gameTimer;
    }

    public boolean isPrivate() {
        return _isPrivate;
    }

    public boolean isInviteOnly() {
        return _isInviteOnly;
    }

    public HallGameTimer getGameTimer() {
        return _gameTimer;
    }

    public boolean addPlayer(SwccgGameParticipant player) {
        _players.put(player.getPlayerId(), player);
        return _players.size() == _capacity;
    }

    public boolean removePlayer(String playerId) {
        _players.remove(playerId);
        return _players.isEmpty();
    }

    public boolean hasPlayer(String playerId) {
        return _players.containsKey(playerId);
    }

    public List<String> getPlayerNames() {
        return new LinkedList<String>(_players.keySet());
    }

    public Set<SwccgGameParticipant> getPlayers() {
        return Collections.unmodifiableSet(new HashSet<SwccgGameParticipant>(_players.values()));
    }

    public CollectionType getCollectionType() {
        return _collectionType;
    }

    public SwccgFormat getSwccgoFormat() {
        return _swccgFormat;
    }

    public League getLeague() {
        return _league;
    }

    public LeagueSeriesData getLeagueSeries() {
        return _leagueSeries;
    }

    public String getTableDesc() {
        return _tableDesc;
    }

    public void setAiPlayer(String aiPlayerId, SwccgDeck aiDeck, String aiSkill) {
        _aiPlayerId = aiPlayerId;
        _aiDeck = aiDeck;
        _aiSkill = aiSkill;
    }

    public boolean hasAi() {
        return _aiPlayerId != null;
    }

    public String getAiPlayerId() {
        return _aiPlayerId;
    }

    public SwccgDeck getAiDeck() {
        return _aiDeck;
    }

    public String getAiSkill() {
        return _aiSkill;
    }

    public void setStartedGameId(String gameId) {
        _startedGameId = gameId;
    }

    public String getStartedGameId() {
        return _startedGameId;
    }
}
