package com.gempukku.swccgo.metrics;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Storage for raw metric rows ({@code metrics_event}) and daily totals ({@code metrics_daily}).
 */
public interface MetricsDAO {
    void insert(MetricsEvent event);

    int deleteOlderThan(long cutoffMillis);

    List<MetricsEvent> listBetween(long startMillisInclusive, long endMillisExclusive);

    boolean hasDailyCounts(LocalDate day);

    void replaceDailyCounts(LocalDate day, Map<String, Integer> counts);

    Map<String, Integer> getDailyCounts(LocalDate day);
}
