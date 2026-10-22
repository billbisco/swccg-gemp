package com.gempukku.swccgo.metrics;

import java.util.Map;

/** A stored raw metrics row. Time is UTC epoch milliseconds truncated to the minute. */
public final class MetricsEvent {
    public final long id;
    public final long eventTimeMillis;
    public final MetricsEventType event;
    public final String guestId;
    public final String gameId;
    public final boolean loggedIn;
    public final Map<String, String> details;

    public MetricsEvent(long id, long eventTimeMillis, MetricsEventType event, String guestId, String gameId,
                        boolean loggedIn, Map<String, String> details) {
        this.id = id;
        this.eventTimeMillis = eventTimeMillis;
        this.event = event;
        this.guestId = guestId;
        this.gameId = gameId;
        this.loggedIn = loggedIn;
        this.details = details;
    }
}
