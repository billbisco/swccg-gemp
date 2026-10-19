package com.gempukku.swccgo.game;

import com.gempukku.swccgo.db.GameHistoryDAO;
import com.gempukku.swccgo.db.GameHistoryFilter;
import com.gempukku.swccgo.db.LeagueDecklistEntry;
import com.gempukku.swccgo.db.PlayerStatistic;
import com.gempukku.swccgo.db.vo.GameHistoryEntry;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class GameHistoryPageTests {
    private static final Player BILL = new Player(1, "Bill", "", "u", null, null, null, null);
    private static final GameHistoryFilter DARK_ONLY = new GameHistoryFilter(null, null, false, null, "Dark", null, null, null);

    @Test
    public void GameHistoryPageWithoutFilterUsesTotalAsMatching() {
        //test1: no filtered count query when nothing is filtered
        //test2: matching and total are both the player's game count
        FakeHistoryDao dao = new FakeHistoryDao(42, 7);
        GameHistoryService.HistoryPage page = new GameHistoryService(dao).getGameHistoryPage(BILL, null, 0, 25);

        assertEquals(0, dao.filteredCountCalls); //test1
        assertEquals(42, page.matching()); //test2
        assertEquals(42, page.total()); //test2
        assertSame(GameHistoryFilter.NONE, dao.lastFilter);
    }

    @Test
    public void GameHistoryPageWithFilterCountsMatchingGames() {
        FakeHistoryDao dao = new FakeHistoryDao(42, 7);
        GameHistoryService.HistoryPage page = new GameHistoryService(dao).getGameHistoryPage(BILL, DARK_ONLY, 0, 25);

        assertEquals(1, dao.filteredCountCalls);
        assertEquals(7, page.matching());
        assertEquals(42, page.total());
        assertSame(DARK_ONLY, dao.lastFilter);
    }

    @Test
    public void GameHistoryPageClampsPageSizeAndStart() {
        //test1: page size above the maximum is cut to the maximum
        //test2: page size below 1 becomes 1
        //test3: a negative start becomes 0
        FakeHistoryDao dao = new FakeHistoryDao(500, 500);
        GameHistoryService service = new GameHistoryService(dao);

        assertEquals(GameHistoryService.MAX_HISTORY_PAGE_SIZE, service.getGameHistoryPage(BILL, null, 0, 1000).count()); //test1
        assertEquals(GameHistoryService.MAX_HISTORY_PAGE_SIZE, dao.lastCount); //test1
        assertEquals(1, service.getGameHistoryPage(BILL, null, 0, 0).count()); //test2
        assertEquals(0, service.getGameHistoryPage(BILL, null, -5, 10).start()); //test3
        assertEquals(0, dao.lastStart); //test3
    }

    @Test
    public void GameHistoryPagePastTheLastGameDoesNotQuery() {
        //test1: a start at or past the matching count returns no entries
        //test2: the page query is skipped
        FakeHistoryDao dao = new FakeHistoryDao(42, 7);
        GameHistoryService.HistoryPage page = new GameHistoryService(dao).getGameHistoryPage(BILL, DARK_ONLY, 7, 25);

        assertTrue(page.entries().isEmpty()); //test1
        assertEquals(0, dao.pageCalls); //test2
    }

    @Test
    public void GameHistoryEntryGivesEachPlayerTheirSide() {
        //test1: the winner played the winning side
        //test2: the loser played the other side
        //test3: someone in neither seat has no side
        GameHistoryEntry entry = entry("Bill", "Vader", "Dark");

        assertEquals("Dark", entry.getSideOf("Bill")); //test1
        assertEquals("Light", entry.getSideOf("Vader")); //test2
        assertNull(entry.getSideOf("Lando")); //test3
    }

    @Test
    public void GameHistoryEntryHasNoSideWhenGameWasCancelled() {
        GameHistoryEntry entry = entry("Bill", "Vader", "None");

        assertNull(entry.getSideOf("Bill"));
        assertNull(entry.getSideOf("Vader"));
    }

    private static GameHistoryEntry entry(String winner, String loser, String winnerSide) {
        return new GameHistoryEntry(winner, "Opponent lost all Force", null, loser, "Lost all Force", null, "Open",
                "Casual", "w deck", "", "l deck", "", new Date(0), new Date(1), winnerSide, "");
    }

    private static class FakeHistoryDao implements GameHistoryDAO {
        private final int _total;
        private final int _matching;
        int filteredCountCalls;
        int pageCalls;
        int lastStart = -1;
        int lastCount = -1;
        GameHistoryFilter lastFilter;

        FakeHistoryDao(int total, int matching) {
            _total = total;
            _matching = matching;
        }

        @Override
        public List<GameHistoryEntry> getGameHistoryForPlayer(Player player, GameHistoryFilter filter, int start, int count) {
            pageCalls++;
            lastFilter = filter;
            lastStart = start;
            lastCount = count;
            return new ArrayList<>();
        }

        @Override
        public int getGameHistoryForPlayerCount(Player player, GameHistoryFilter filter) {
            filteredCountCalls++;
            return _matching;
        }

        @Override
        public int getGameHistoryForPlayerCount(Player player) {
            return _total;
        }

        @Override
        public List<String> getGameHistoryFormats(Player player) {
            return List.of();
        }

        @Override
        public List<String> getGameHistoryEvents(Player player, int limit) {
            return List.of();
        }

        @Override
        public void addGameHistory(String winner, String loser, String winReason, String loseReason, String winRecordingId, String loseRecordingId, String formatName, String tournament, String winnerDeckName, String loserDeckName, String winnerDeckArchetype, String loserDeckArchetype, String winningSide, String darkDeckString, String lightDeckString, String leagueType, String sealedLeagueType, Date startDate, Date endDate) {
        }

        @Override
        public List<GameHistoryEntry> getGameHistoryForPlayer(Player player, int start, int count) {
            return List.of();
        }

        @Override
        public int getActivePlayersCount() {
            return 0;
        }

        @Override
        public int getActivePlayersCount(long from, long duration) {
            return 0;
        }

        @Override
        public int getGamesPlayedCount() {
            return 0;
        }

        @Override
        public int getGamesPlayedCount(long from, long duration) {
            return 0;
        }

        @Override
        public Map<String, Integer> getCasualGamesPlayedPerFormat(long from, long duration) {
            return Map.of();
        }

        @Override
        public List<PlayerStatistic> getCasualPlayerStatistics(Player player) {
            return List.of();
        }

        @Override
        public List<PlayerStatistic> getCompetitivePlayerStatistics(Player player) {
            return List.of();
        }

        @Override
        public List<LeagueDecklistEntry> getLeagueDecklists(String leagueId) {
            return List.of();
        }
    }
}
