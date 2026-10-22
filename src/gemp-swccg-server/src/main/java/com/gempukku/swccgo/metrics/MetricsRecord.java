package com.gempukku.swccgo.metrics;

import java.util.Map;

/**
 * One event to count. The writer empties {@code guestId} when {@code doNotTrack} is set or the player is logged in.
 * Callers must not pass IP addresses, usernames, emails, user agents, location, chat or decklists.
 */
public final class MetricsRecord {
    public final MetricsEventType event;
    public final String guestId;
    public final String gameId;
    public final boolean loggedIn;
    public final boolean doNotTrack;
    public final Map<String, String> details;

    public MetricsRecord(MetricsEventType event, String guestId, String gameId, boolean loggedIn,
                         boolean doNotTrack, Map<String, String> details) {
        this.event = event;
        this.guestId = guestId;
        this.gameId = gameId;
        this.loggedIn = loggedIn;
        this.doNotTrack = doNotTrack;
        this.details = details;
    }
}
