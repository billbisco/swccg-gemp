package com.gempukku.swccgo.guest;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.LongSupplier;

/**
 * Limits on guest game starts: live games per IP, starts per IP per hour and starts per hour for the whole server.
 * IPs are kept in memory only, for one hour.
 */
public class GuestLimits {
    private static final long HOUR = 60L * 60 * 1000;

    /** Outcome of a check. {@code retryAfterSeconds} is set when the caller is rate limited. */
    public record Decision(boolean allowed, String reason, long retryAfterSeconds) {
        static final Decision OK = new Decision(true, null, 0);
    }

    private final GuestSettings _settings;
    private final LongSupplier _clock;
    private final Deque<Long> _globalStarts = new ArrayDeque<>();
    private final Map<String, Deque<Long>> _startsByIp = new HashMap<>();

    public GuestLimits(GuestSettings settings, LongSupplier clock) {
        _settings = settings;
        _clock = clock;
    }

    /**
     * Checks whether this IP may start another guest game, and if so counts the start.
     * @param liveGamesFromIp the IP's guest games now in memory
     */
    public synchronized Decision tryStart(String ip, int liveGamesFromIp) {
        long now = _clock.getAsLong();
        prune(_globalStarts, now);
        boolean allowlisted = ip != null && _settings.ipAllowlist().contains(ip);
        Deque<Long> mine = _startsByIp.computeIfAbsent(ip == null ? "" : ip, k -> new ArrayDeque<>());
        prune(mine, now);

        if (!allowlisted && liveGamesFromIp >= _settings.maxConcurrentPerIp()) {
            return new Decision(false, "too_many_games", 0);
        }
        if (!allowlisted && mine.size() >= _settings.startsPerIpPerHour()) {
            return new Decision(false, "rate_limited", secondsUntilFree(mine, now));
        }
        if (_globalStarts.size() >= _settings.startsPerHourGlobal()) {
            return new Decision(false, "rate_limited", secondsUntilFree(_globalStarts, now));
        }
        mine.addLast(now);
        _globalStarts.addLast(now);
        return Decision.OK;
    }

    /** Forgets IPs with no starts in the last hour. */
    public synchronized void cleanup() {
        long now = _clock.getAsLong();
        prune(_globalStarts, now);
        Iterator<Map.Entry<String, Deque<Long>>> it = _startsByIp.entrySet().iterator();
        while (it.hasNext()) {
            Deque<Long> starts = it.next().getValue();
            prune(starts, now);
            if (starts.isEmpty()) {
                it.remove();
            }
        }
    }

    synchronized int trackedIps() {
        return _startsByIp.size();
    }

    private static void prune(Deque<Long> starts, long now) {
        while (!starts.isEmpty() && starts.peekFirst() <= now - HOUR) {
            starts.removeFirst();
        }
    }

    private static long secondsUntilFree(Deque<Long> starts, long now) {
        long oldest = starts.isEmpty() ? now : starts.peekFirst();
        return Math.max(1, (oldest + HOUR - now + 999) / 1000);
    }
}
