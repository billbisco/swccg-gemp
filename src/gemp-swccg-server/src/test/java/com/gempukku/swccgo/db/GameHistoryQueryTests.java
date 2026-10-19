package com.gempukku.swccgo.db;

import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GameHistoryQueryTests {
    private static final long OCT_1_2026_UTC = 1790812800000L;
    private static final long DAY = 24L * 60 * 60 * 1000;

    @Test
    public void GameHistoryQueryWithoutFilterMatchesBothSeats() {
        GameHistoryQuery query = GameHistoryQuery.forPlayer("Bill", GameHistoryFilter.NONE);

        assertEquals("(winner = ? OR loser = ?)", query.where());
        assertEquals(List.of("Bill", "Bill"), query.parameters());
    }

    @Test
    public void GameHistoryQueryNullFilterIsNoFilter() {
        assertEquals(GameHistoryQuery.forPlayer("Bill", GameHistoryFilter.NONE).where(),
                GameHistoryQuery.forPlayer("Bill", null).where());
    }

    @Test
    public void GameHistoryQueryFormatIsExactMatch() {
        GameHistoryQuery query = GameHistoryQuery.forPlayer("Bill", filter("Open", null, false, null, null, null, null, null));

        assertEquals("(winner = ? OR loser = ?) AND format_name = ?", query.where());
        assertEquals(List.of("Bill", "Bill", "Open"), query.parameters());
    }

    @Test
    public void GameHistoryQueryOpponentPrefixIgnoresCaseAndEscapesWildcards() {
        //test1: the opponent is matched in the other seat only, as a lower-cased prefix
        //test2: the player's own % and _ are escaped, not wildcards
        GameHistoryQuery query = GameHistoryQuery.forPlayer("Bill", filter(null, "Jo_n%", false, null, null, null, null, null));

        assertTrue(query.where().contains("((winner = ? AND LOWER(loser) LIKE ? ESCAPE '!') OR (loser = ? AND LOWER(winner) LIKE ? ESCAPE '!'))")); //test1
        assertEquals(List.of("Bill", "Bill", "Bill", "jo!_n!%%", "Bill", "jo!_n!%%"), query.parameters()); //test2
    }

    @Test
    public void GameHistoryQueryOpponentExactUsesEquality() {
        GameHistoryQuery query = GameHistoryQuery.forPlayer("Bill", filter(null, "Vader", true, null, null, null, null, null));

        assertTrue(query.where().contains("((winner = ? AND LOWER(loser) = ?) OR (loser = ? AND LOWER(winner) = ?))"));
        assertEquals(List.of("Bill", "Bill", "Bill", "vader", "Bill", "vader"), query.parameters());
    }

    @Test
    public void GameHistoryQueryCasualEventMatchesCasualAndPrivateCasual() {
        //test1: casual selects untagged games and "Casual" / "Casual (Private)"
        //test2: no value is bound for it
        GameHistoryQuery query = GameHistoryQuery.forPlayer("Bill", filter(null, null, false, "casual", null, null, null, null));

        assertTrue(query.where().endsWith("AND (tournament IS NULL OR tournament LIKE 'Casual%')")); //test1
        assertEquals(List.of("Bill", "Bill"), query.parameters()); //test2
    }

    @Test
    public void GameHistoryQueryEventIsLowerCasedPrefix() {
        GameHistoryQuery query = GameHistoryQuery.forPlayer("Bill", filter(null, null, false, "Hoth League", null, null, null, null));

        assertTrue(query.where().endsWith("AND LOWER(tournament) LIKE ? ESCAPE '!'"));
        assertEquals(List.of("Bill", "Bill", "hoth league%"), query.parameters());
    }

    @Test
    public void GameHistoryQueryDarkSideMeansWonAsDarkOrLostToLight() {
        GameHistoryQuery query = GameHistoryQuery.forPlayer("Bill", filter(null, null, false, null, "Dark", null, null, null));

        assertTrue(query.where().endsWith("AND ((winner = ? AND winner_side = ?) OR (loser = ? AND winner_side = ?))"));
        assertEquals(List.of("Bill", "Bill", "Bill", "Dark", "Bill", "Light"), query.parameters());
    }

    @Test
    public void GameHistoryQueryLightSideMeansWonAsLightOrLostToDark() {
        GameHistoryQuery query = GameHistoryQuery.forPlayer("Bill", filter(null, null, false, null, "Light", null, null, null));

        assertEquals(List.of("Bill", "Bill", "Bill", "Light", "Bill", "Dark"), query.parameters());
    }

    @Test
    public void GameHistoryQueryResultPicksTheSeat() {
        //test1: W keeps games the player won
        //test2: L keeps games the player lost
        GameHistoryQuery won = GameHistoryQuery.forPlayer("Bill", filter(null, null, false, null, null, "W", null, null));
        GameHistoryQuery lost = GameHistoryQuery.forPlayer("Bill", filter(null, null, false, null, null, "L", null, null));

        assertTrue(won.where().endsWith("AND winner = ?")); //test1
        assertTrue(lost.where().endsWith("AND loser = ?")); //test2
        assertEquals(List.of("Bill", "Bill", "Bill"), won.parameters()); //test1
    }

    @Test
    public void GameHistoryQueryDayRangeIsInclusiveInUtcMillis() {
        //test1: from binds midnight UTC of the first day
        //test2: to binds midnight UTC of the day after the last day (exclusive bound)
        GameHistoryQuery query = GameHistoryQuery.forPlayer("Bill", filter(null, null, false, null, null, null,
                LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 3)));

        assertTrue(query.where().endsWith("AND end_date >= ? AND end_date < ?"));
        assertEquals(OCT_1_2026_UTC, query.parameters().get(2)); //test1
        assertEquals(OCT_1_2026_UTC + 3 * DAY, query.parameters().get(3)); //test2
    }

    @Test
    public void GameHistoryQueryPlayerValuesNeverReachTheSqlText() {
        GameHistoryQuery query = GameHistoryQuery.forPlayer("Bill'; DROP TABLE game_history; --",
                filter("x' OR '1'='1", "y' OR 1=1 --", false, "z')--", "Dark", "W", null, null));

        assertFalse(query.selectSql().contains("DROP"));
        assertFalse(query.selectSql().contains("'1'='1"));
        assertFalse(query.countSql().contains("1=1"));
        assertFalse(query.countSql().contains("z')"));
    }

    @Test
    public void GameHistoryQueryPagesNewestFirst() {
        GameHistoryQuery query = GameHistoryQuery.forPlayer("Bill", GameHistoryFilter.NONE);

        assertTrue(query.selectSql().endsWith("ORDER BY end_date DESC, id DESC LIMIT ?, ?"));
        assertTrue(query.countSql().startsWith("SELECT COUNT(*) FROM game_history WHERE "));
    }

    @Test
    public void GameHistoryQueryEventListSkipsCasualAndPlaytesting() {
        //test1: casual and playtesting labels are not offered as events
        //test2: most recent event first, limited
        assertTrue(GameHistoryQuery.eventsSql().contains("tournament NOT LIKE 'Casual%' AND tournament NOT LIKE 'Playtesting%'")); //test1
        assertTrue(GameHistoryQuery.eventsSql().endsWith("ORDER BY MAX(end_date) DESC LIMIT ?")); //test2
    }

    private static GameHistoryFilter filter(String format, String opponent, boolean exact, String event, String side,
                                            String result, LocalDate from, LocalDate to) {
        return new GameHistoryFilter(format, opponent, exact, event, side, result, from, to);
    }
}
