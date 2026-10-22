package com.gempukku.swccgo.metrics;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Writes metric events, deletes raw rows after 90 days, and rolls finished UTC days into id-free totals.
 * Nothing here stores IP addresses, usernames, emails, user agents, location, chat or decklists.
 */
public class MetricsService {
    private static final Logger LOG = LogManager.getLogger(MetricsService.class);

    public static final int RAW_RETENTION_DAYS = 90;
    /** How often the maintenance tick does the real work. */
    public static final long RUN_INTERVAL_MS = 60L * 60 * 1000;
    static final long MINUTE_MS = 60_000L;

    private final MetricsDAO _dao;
    private final Clock _clock;
    private long _nextRunAt;

    public MetricsService(MetricsDAO dao, Clock clock) {
        _dao = dao;
        _clock = clock;
    }

    public MetricsService(MetricsDAO dao) {
        this(dao, Clock.systemUTC());
    }

    /** Truncates UTC time to the minute. */
    public static long truncateToMinute(long epochMillis) {
        return (epochMillis / MINUTE_MS) * MINUTE_MS;
    }

    /**
     * Stores one event. Guest id is emptied when the browser sent Do Not Track / Global Privacy Control,
     * and when the player is logged in (the row still records {@code logged_in = true}).
     */
    public void record(MetricsRecord record) {
        if (record == null || record.event == null) {
            throw new IllegalArgumentException("Metrics event is required");
        }
        Map<String, String> details = record.event.validatedDetails(record.details);
        boolean loggedIn = record.loggedIn;
        String guestId = record.guestId;
        if (record.doNotTrack || loggedIn) {
            guestId = null;
        } else if (guestId != null && guestId.isEmpty()) {
            guestId = null;
        }
        String gameId = record.gameId;
        if (gameId != null && gameId.isEmpty()) {
            gameId = null;
        }
        long eventTime = truncateToMinute(_clock.millis());
        _dao.insert(new MetricsEvent(0, eventTime, record.event, guestId, gameId, loggedIn, details));
    }

    /**
     * Purges raw rows older than 90 days and rolls up each finished UTC day that does not yet have daily totals.
     * Cheap to call often: the real work runs about hourly.
     */
    public synchronized void processDueJobsIfTime() {
        long now = _clock.millis();
        if (now < _nextRunAt) {
            return;
        }
        _nextRunAt = now + RUN_INTERVAL_MS;
        runMaintenance(Instant.ofEpochMilli(now));
    }

    /** Purge + rollup immediately (tests and the scheduled job). */
    public void runMaintenance(Instant now) {
        long cutoff = Instant.ofEpochMilli(now.toEpochMilli())
                .atZone(ZoneOffset.UTC)
                .minusDays(RAW_RETENTION_DAYS)
                .toInstant()
                .toEpochMilli();
        int removed = _dao.deleteOlderThan(cutoff);
        if (removed > 0) {
            LOG.info("Purged " + removed + " metrics events older than " + RAW_RETENTION_DAYS + " days");
        }

        LocalDate today = LocalDate.ofInstant(now, ZoneOffset.UTC);
        LocalDate yesterday = today.minusDays(1);
        LocalDate first = today.minusDays(RAW_RETENTION_DAYS);
        for (LocalDate day = first; day.isBefore(today); day = day.plusDays(1)) {
            if (_dao.hasDailyCounts(day)) {
                continue;
            }
            long start = startOfDay(day);
            long end = startOfDay(day.plusDays(1));
            List<MetricsEvent> events = _dao.listBetween(start, end);
            if (!events.isEmpty() || day.equals(yesterday)) {
                rollupDay(day, events);
            }
        }
    }

    static long startOfDay(LocalDate day) {
        return day.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
    }

    void rollupDay(LocalDate day) {
        rollupDay(day, _dao.listBetween(startOfDay(day), startOfDay(day.plusDays(1))));
    }

    private void rollupDay(LocalDate day, List<MetricsEvent> events) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        counts.put("_rolled", 1);
        for (MetricsEvent event : events) {
            add(counts, event.event.wireName());
            switch (event.event) {
                case PLAY_CLICK -> add(counts, "play_click_" + event.details.get("source"));
                case GAME_START -> {
                    String side = event.details.get("side");
                    String bot = event.details.get("bot");
                    add(counts, "game_start_" + side);
                    add(counts, "game_start_" + bot);
                    add(counts, "game_start_" + side + "_" + bot);
                }
                case GAME_ABANDONED -> add(counts, "game_abandoned_turn_" + event.details.get("last_turn"));
                case OUTBOUND_CLICK -> add(counts, "outbound_click_" + event.details.get("target"));
                default -> {
                }
            }
        }
        _dao.replaceDailyCounts(day, counts);
    }

    private static void add(Map<String, Integer> counts, String metric) {
        counts.merge(metric, 1, Integer::sum);
    }
}
