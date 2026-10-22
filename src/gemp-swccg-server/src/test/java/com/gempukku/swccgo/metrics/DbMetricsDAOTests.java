package com.gempukku.swccgo.metrics;

import com.gempukku.swccgo.db.DbAccess;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Runs against a real MariaDB with the metrics tables, only when -Dgemp.test.dbUrl is set.
 * Skipped otherwise. The Docker integration CI job runs it against the stack's database.
 */
public class DbMetricsDAOTests {
    private DbMetricsDAO _dao;

    @BeforeEach
    public void connect() throws Exception {
        String url = System.getProperty("gemp.test.dbUrl");
        assumeTrue(url != null && !url.isEmpty());
        BasicDataSource ds = new BasicDataSource();
        ds.setUrl(url);
        ds.setUsername(System.getProperty("gemp.test.dbUser", "root"));
        ds.setPassword(System.getProperty("gemp.test.dbPassword", ""));
        try (Connection connection = ds.getConnection(); Statement statement = connection.createStatement()) {
            statement.execute("DELETE FROM metrics_event");
            statement.execute("DELETE FROM metrics_daily");
        }
        _dao = new DbMetricsDAO(new DbAccess(ds));
    }

    @Test
    public void DbMetricsDAOStoresPurgesAndRollsUp() {
        //test1: a row reads back without a guest id when none was stored
        //test2: rows older than the cutoff are deleted
        //test3: daily totals replace for that day and keep no ids
        MetricsEvent stored = new MetricsEvent(0, 1_000_000L, MetricsEventType.GAME_START, null, "g-db",
                true, Map.of("side", "light", "bot", "ozzel"));
        _dao.insert(stored);
        _dao.insert(new MetricsEvent(0, 2_000_000L, MetricsEventType.PLAY_CLICK, "guest-keep", null,
                false, Map.of("source", "direct")));

        List<MetricsEvent> between = _dao.listBetween(0, 1_500_000L);
        assertEquals(1, between.size()); //test1
        assertNull(between.get(0).guestId); //test1
        assertEquals("light", between.get(0).details.get("side")); //test1
        assertEquals("g-db", between.get(0).gameId); //test1
        assertEquals(true, between.get(0).loggedIn); //test1

        assertEquals(1, _dao.deleteOlderThan(1_500_000L)); //test2
        assertEquals(1, _dao.listBetween(0, 3_000_000L).size()); //test2

        LocalDate day = LocalDate.of(2026, 10, 20);
        _dao.replaceDailyCounts(day, Map.of("game_start", 2, "game_start_light_ozzel", 2));
        _dao.replaceDailyCounts(day, Map.of("game_start", 1));
        Map<String, Integer> counts = _dao.getDailyCounts(day);
        assertEquals(1, counts.get("game_start")); //test3
        assertEquals(null, counts.get("game_start_light_ozzel")); //test3
        assertEquals(1, counts.size()); //test3
    }
}
