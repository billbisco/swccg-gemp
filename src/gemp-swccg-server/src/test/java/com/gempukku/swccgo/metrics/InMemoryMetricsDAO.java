package com.gempukku.swccgo.metrics;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** MetricsDAO kept in memory, storing the same fields the database would. */
public class InMemoryMetricsDAO implements MetricsDAO {
    private final List<MetricsEvent> _events = new ArrayList<>();
    private final Map<LocalDate, Map<String, Integer>> _daily = new LinkedHashMap<>();
    private long _nextId = 1;

    @Override
    public synchronized void insert(MetricsEvent event) {
        _events.add(new MetricsEvent(_nextId++, event.eventTimeMillis, event.event, event.guestId, event.gameId,
                event.loggedIn, event.details));
    }

    @Override
    public synchronized int deleteOlderThan(long cutoffMillis) {
        int before = _events.size();
        _events.removeIf(event -> event.eventTimeMillis < cutoffMillis);
        return before - _events.size();
    }

    @Override
    public synchronized List<MetricsEvent> listBetween(long startMillisInclusive, long endMillisExclusive) {
        List<MetricsEvent> result = new ArrayList<>();
        for (MetricsEvent event : _events) {
            if (event.eventTimeMillis >= startMillisInclusive && event.eventTimeMillis < endMillisExclusive) {
                result.add(event);
            }
        }
        return result;
    }

    @Override
    public synchronized boolean hasDailyCounts(LocalDate day) {
        return _daily.containsKey(day);
    }

    @Override
    public synchronized void replaceDailyCounts(LocalDate day, Map<String, Integer> counts) {
        _daily.put(day, new LinkedHashMap<>(counts == null ? Map.of() : counts));
    }

    @Override
    public synchronized Map<String, Integer> getDailyCounts(LocalDate day) {
        Map<String, Integer> counts = _daily.get(day);
        return counts == null ? Map.of() : new LinkedHashMap<>(counts);
    }

    public synchronized List<MetricsEvent> allEvents() {
        return new ArrayList<>(_events);
    }
}
