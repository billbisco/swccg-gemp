package com.gempukku.swccgo.game;

import com.gempukku.swccgo.db.GameHistoryDAO;
import com.gempukku.swccgo.db.GameHistoryFilter;
import com.gempukku.swccgo.db.LeagueDecklistEntry;
import com.gempukku.swccgo.db.PlayerStatistic;
import com.gempukku.swccgo.db.vo.GameHistoryEntry;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameHistoryService {
    /** Page size of the History tab when the request names none. */
    public static final int DEFAULT_HISTORY_PAGE_SIZE = 25;
    /** Largest page the History tab may ask for. */
    public static final int MAX_HISTORY_PAGE_SIZE = 100;
    /** At most this many leagues / tournaments are offered as event filter suggestions. */
    public static final int MAX_EVENT_OPTIONS = 100;

    /**
     * One page of a player's history.
     *
     * @param entries  the games on this page, newest first
     * @param matching games matching the filter (what the pager counts)
     * @param total    all of the player's games
     * @param start    offset of the first entry, after clamping
     * @param count    page size used, after clamping
     */
    public record HistoryPage(List<GameHistoryEntry> entries, int matching, int total, int start, int count) {
    }

    private GameHistoryDAO _gameHistoryDAO;
    private Map<String, Integer> _playerGameCount = new ConcurrentHashMap<String, Integer>();

    public GameHistoryService(GameHistoryDAO gameHistoryDAO) {
        _gameHistoryDAO = gameHistoryDAO;
    }

    public void addGameHistory(String winner, String loser, String winReason, String loseReason, String winRecordingId, String loseRecordingId, String formatName, String tournament, String winnerDeckName, String loserDeckName, String winnerDeckArchetype, String loserDeckArchetype, String winningSide, String darkDeckString, String lightDeckString, String leagueType, String sealedLeagueType, Date startDate, Date endDate) {
        _gameHistoryDAO.addGameHistory(winner, loser, winReason, loseReason, winRecordingId, loseRecordingId, formatName, tournament, winnerDeckName, loserDeckName, winnerDeckArchetype, loserDeckArchetype, winningSide, darkDeckString, lightDeckString, leagueType, sealedLeagueType, startDate, endDate);
        Integer winnerCount = _playerGameCount.get(winner);
        Integer loserCount = _playerGameCount.get(loser);
        if (winnerCount != null)
            _playerGameCount.put(winner, winnerCount + 1);
        if (loserCount != null)
            _playerGameCount.put(loser, loserCount + 1);
    }

    public int getGameHistoryForPlayerCount(Player player) {
        Integer result = _playerGameCount.get(player.getName());
        if (result != null)
            return result;
        int count = _gameHistoryDAO.getGameHistoryForPlayerCount(player);
        _playerGameCount.put(player.getName(), count);
        return count;
    }

    public List<GameHistoryEntry> getGameHistoryForPlayer(Player player, int start, int count) {
        return _gameHistoryDAO.getGameHistoryForPlayer(player, start, count);
    }

    public int getActivePlayersCount() {
        return _gameHistoryDAO.getActivePlayersCount();
    }

    public int getActivePlayersCount(long from, long duration) {
        return _gameHistoryDAO.getActivePlayersCount(from, duration);
    }

    public int getGamesPlayedCount() {
        return _gameHistoryDAO.getGamesPlayedCount();
    }

    public int getGamesPlayedCount(long from, long duration) {
        return _gameHistoryDAO.getGamesPlayedCount(from, duration);
    }

    public GameHistoryStatistics getGameHistoryStatistics(long from, long duration) {
        GameHistoryStatistics stats = new GameHistoryStatistics(from, duration);
        stats.init(_gameHistoryDAO);
        return stats;
    }

    public List<PlayerStatistic> getCasualPlayerStatistics(Player player) {
        return _gameHistoryDAO.getCasualPlayerStatistics(player);
    }

    public List<PlayerStatistic> getCompetitivePlayerStatistics(Player player) {
        return _gameHistoryDAO.getCompetitivePlayerStatistics(player);
    }

    public List<LeagueDecklistEntry> getDeckCheck(String leagueId) {
        return _gameHistoryDAO.getLeagueDecklists(leagueId);
    }

    /**
     * The page of the player's games matching the filter.  The page size is clamped to 1..{@link #MAX_HISTORY_PAGE_SIZE}
     * and the start to 0 or more; with no filter the (cached) total doubles as the matching count, and a page past the
     * last game is not fetched.
     */
    public HistoryPage getGameHistoryPage(Player player, GameHistoryFilter filter, int start, int count) {
        if (filter == null)
            filter = GameHistoryFilter.NONE;
        int pageSize = Math.max(1, Math.min(MAX_HISTORY_PAGE_SIZE, count));
        int offset = Math.max(0, start);

        int total = getGameHistoryForPlayerCount(player);
        int matching = filter.isEmpty() ? total : _gameHistoryDAO.getGameHistoryForPlayerCount(player, filter);
        List<GameHistoryEntry> entries = (matching > 0 && offset < matching) || matching < 0
                ? _gameHistoryDAO.getGameHistoryForPlayer(player, filter, offset, pageSize)
                : List.of();
        return new HistoryPage(entries, matching, total, offset, pageSize);
    }

    /** Formats the player has games in, most played first. */
    public List<String> getGameHistoryFormats(Player player) {
        return _gameHistoryDAO.getGameHistoryFormats(player);
    }

    /** Leagues and tournaments the player has games in, most recent first (at most {@link #MAX_EVENT_OPTIONS}). */
    public List<String> getGameHistoryEvents(Player player) {
        return _gameHistoryDAO.getGameHistoryEvents(player, MAX_EVENT_OPTIONS);
    }
}
