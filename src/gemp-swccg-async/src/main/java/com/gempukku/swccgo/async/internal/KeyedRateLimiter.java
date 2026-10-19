package com.gempukku.swccgo.async.internal;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory sliding-window counter per key. Single GEMP process, so no shared store is needed.
 * Bounded: keys whose events have all expired are dropped on each sweep.
 */
public final class KeyedRateLimiter {
    private final int _limit;
    private final long _windowMillis;
    private final Map<String, Deque<Long>> _events = new ConcurrentHashMap<>();
    private long _lastSweep;

    public KeyedRateLimiter(int limit, long windowMillis) {
        _limit = limit;
        _windowMillis = windowMillis;
    }

    /** @return 0 if the key is under its limit, otherwise the seconds until a slot frees up. */
    public synchronized long retryAfterSeconds(String key, long now) {
        sweep(now);
        Deque<Long> q = _events.get(key);
        if (q == null)
            return 0;
        prune(q, now);
        if (q.size() < _limit)
            return 0;
        long oldest = q.peekFirst();
        return Math.max(1, (oldest + _windowMillis - now + 999) / 1000);
    }

    public synchronized void record(String key, long now) {
        Deque<Long> q = _events.computeIfAbsent(key, k -> new ArrayDeque<>());
        prune(q, now);
        q.addLast(now);
    }

    public synchronized void clear(String key) {
        _events.remove(key);
    }

    private void prune(Deque<Long> q, long now) {
        while (!q.isEmpty() && q.peekFirst() <= now - _windowMillis)
            q.removeFirst();
    }

    private void sweep(long now) {
        if (now - _lastSweep < 60_000)
            return;
        _lastSweep = now;
        Iterator<Map.Entry<String, Deque<Long>>> it = _events.entrySet().iterator();
        while (it.hasNext()) {
            Deque<Long> q = it.next().getValue();
            prune(q, now);
            if (q.isEmpty())
                it.remove();
        }
    }
}
