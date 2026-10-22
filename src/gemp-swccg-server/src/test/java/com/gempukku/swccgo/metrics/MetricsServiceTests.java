package com.gempukku.swccgo.metrics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MetricsServiceTests {
    private static final Instant NOON = Instant.parse("2026-10-21T12:00:30Z");
    private static final LocalDate YESTERDAY = LocalDate.of(2026, 10, 20);

    private InMemoryMetricsDAO dao;
    private MetricsService service;

    @BeforeEach
    public void setUp() {
        dao = new InMemoryMetricsDAO();
        service = new MetricsService(dao, Clock.fixed(NOON, ZoneOffset.UTC));
    }

    @Test
    public void RecordTruncatesTimeToTheMinuteAndKeepsAGuestId() {
        //test1: time is stored to the minute, not the second
        //test2: a guest who has not opted out of counting keeps their guest id
        service.record(playClick("guest-abc", false, false, "frontpage"));
        MetricsEvent stored = dao.allEvents().get(0);
        assertEquals(Instant.parse("2026-10-21T12:00:00Z").toEpochMilli(), stored.eventTimeMillis); //test1
        assertEquals(0, stored.eventTimeMillis % 60_000); //test1
        assertEquals("guest-abc", stored.guestId); //test2
        assertFalse(stored.loggedIn); //test2
        assertEquals("frontpage", stored.details.get("source")); //test2
    }

    @Test
    public void RecordEmptiesGuestIdWhenDoNotTrackOrLoggedIn() {
        //test1: Do Not Track leaves the guest id empty
        //test2: a logged-in player is counted as logged in with an empty guest id
        service.record(playClick("guest-abc", false, true, "direct"));
        service.record(playClick("guest-abc", true, false, "other"));
        List<MetricsEvent> events = dao.allEvents();
        assertNull(events.get(0).guestId); //test1
        assertFalse(events.get(0).loggedIn); //test1
        assertNull(events.get(1).guestId); //test2
        assertTrue(events.get(1).loggedIn); //test2
    }

    @Test
    public void RecordRefusesUnknownAndForbiddenDetails() {
        //test1: a key this event does not list is refused
        //test2: an IP address in the details is refused
        assertThrows(IllegalArgumentException.class, () -> service.record(new MetricsRecord(
                MetricsEventType.PLAY_CLICK, "guest-a", null, false, false,
                Map.of("source", "direct", "referrer", "https://example")))); //test1
        assertThrows(IllegalArgumentException.class, () -> service.record(new MetricsRecord(
                MetricsEventType.PLAY_CLICK, "guest-a", null, false, false,
                Map.of("source", "direct", "ip", "1.2.3.4")))); //test2
    }

    @Test
    public void PurgeDeletesRawRowsAfterNinetyDaysAndKeepsDailyTotals() {
        //test1: a row older than 90 days is deleted
        //test2: a row from 89 days ago is kept
        //test3: daily totals from the deleted day stay
        long old = NOON.minus(91, java.time.temporal.ChronoUnit.DAYS).toEpochMilli();
        long kept = NOON.minus(89, java.time.temporal.ChronoUnit.DAYS).toEpochMilli();
        dao.insert(event(old, MetricsEventType.GAME_START, "guest-old", Map.of("side", "light", "bot", "ozzel")));
        dao.insert(event(kept, MetricsEventType.GAME_START, "guest-kept", Map.of("side", "dark", "bot", "yoda")));
        dao.replaceDailyCounts(LocalDate.ofInstant(NOON.minus(91, java.time.temporal.ChronoUnit.DAYS), ZoneOffset.UTC),
                Map.of("game_start", 4, "_rolled", 1));

        service.runMaintenance(NOON);

        List<MetricsEvent> left = dao.allEvents();
        assertEquals(1, left.size()); //test1
        assertEquals("guest-kept", left.get(0).guestId); //test2
        assertEquals(4, dao.getDailyCounts(LocalDate.ofInstant(NOON.minus(91, java.time.temporal.ChronoUnit.DAYS), ZoneOffset.UTC)).get("game_start")); //test3
    }

    @Test
    public void RollupSplitsStartsBySideAndBotAndStoresNoIds() {
        //test1: yesterday's starts are split by side and by bot
        //test2: daily totals have no guest or game ids
        //test3: abandons are counted by last turn; outbound clicks by target
        long t = YESTERDAY.atTime(15, 5).toInstant(ZoneOffset.UTC).toEpochMilli();
        dao.insert(event(t, MetricsEventType.GAME_START, "guest-1", Map.of("side", "light", "bot", "ozzel")));
        dao.insert(event(t, MetricsEventType.GAME_START, "guest-2", Map.of("side", "light", "bot", "yoda")));
        dao.insert(event(t, MetricsEventType.GAME_START, "guest-3", Map.of("side", "dark", "bot", "ozzel")));
        dao.insert(event(t, MetricsEventType.GAME_ABANDONED, "guest-1",
                Map.of("last_turn", "4", "last_phase", "DEPLOY", "waiting_for", "deploy or pass",
                        "active_minutes", "3", "total_minutes", "10")));
        dao.insert(event(t, MetricsEventType.OUTBOUND_CLICK, "guest-2",
                Map.of("target", "wiki", "path", "/wiki/Han_Solo")));
        dao.insert(event(t, MetricsEventType.GAME_RESUME, "guest-2", Map.of("days_since_last_visit", "2")));
        dao.insert(event(t, MetricsEventType.SIGNUP_AFTER_GUEST, null, Map.of("turns_played", "6")));

        service.runMaintenance(NOON);

        Map<String, Integer> day = dao.getDailyCounts(YESTERDAY);
        assertEquals(3, day.get("game_start")); //test1
        assertEquals(2, day.get("game_start_light")); //test1
        assertEquals(1, day.get("game_start_dark")); //test1
        assertEquals(2, day.get("game_start_ozzel")); //test1
        assertEquals(1, day.get("game_start_yoda")); //test1
        assertEquals(1, day.get("game_start_light_ozzel")); //test1
        assertEquals(1, day.get("game_abandoned_turn_4")); //test3
        assertEquals(1, day.get("outbound_click_wiki")); //test3
        assertEquals(1, day.get("game_resume")); //test3
        assertEquals(1, day.get("signup_after_guest")); //test3
        assertFalse(day.containsKey("guest-1")); //test2
        assertFalse(day.values().stream().anyMatch(v -> v == null)); //test2
        for (String metric : day.keySet()) {
            assertFalse(metric.contains("guest-")); //test2
        }
    }

    @Test
    public void RollupIsIdempotentAndDoesNotRollToday() {
        //test1: running maintenance twice does not double yesterday's totals
        //test2: events from today are not rolled yet
        long yesterday = YESTERDAY.atTime(8, 0).toInstant(ZoneOffset.UTC).toEpochMilli();
        dao.insert(event(yesterday, MetricsEventType.PLAY_CLICK, "guest-a", Map.of("source", "frontpage")));
        service.record(playClick("guest-b", false, false, "direct"));

        service.runMaintenance(NOON);
        service.runMaintenance(NOON);

        assertEquals(1, dao.getDailyCounts(YESTERDAY).get("play_click")); //test1
        assertTrue(dao.getDailyCounts(LocalDate.of(2026, 10, 21)).isEmpty()); //test2
        assertEquals(2, dao.allEvents().size()); //test2: today's click is still raw
    }

    @Test
    public void ProcessDueJobsRunsAtMostOncePerInterval() {
        //test1: the first tick purges; a second tick in the same hour does nothing
        long old = YESTERDAY.minusDays(91).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
        dao.insert(event(old, MetricsEventType.GAME_RESUME, "guest-old", Map.of("days_since_last_visit", "1")));
        service.processDueJobsIfTime();
        assertEquals(0, dao.allEvents().size()); //test1
        dao.insert(event(old, MetricsEventType.GAME_RESUME, "guest-old", Map.of("days_since_last_visit", "1")));
        service.processDueJobsIfTime();
        assertEquals(1, dao.allEvents().size()); //test1
    }

    private static MetricsRecord playClick(String guestId, boolean loggedIn, boolean doNotTrack, String source) {
        return new MetricsRecord(MetricsEventType.PLAY_CLICK, guestId, null, loggedIn, doNotTrack,
                Map.of("source", source));
    }

    private static MetricsEvent event(long time, MetricsEventType type, String guestId, Map<String, String> details) {
        return new MetricsEvent(0, time, type, guestId, "g1", false, details);
    }
}
