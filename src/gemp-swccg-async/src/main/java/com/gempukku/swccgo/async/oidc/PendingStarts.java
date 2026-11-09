package com.gempukku.swccgo.async.oidc;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Sign-ins started with /oidc/start and not yet finished, keyed by state. Bounded two ways so an
 * unauthenticated flood can't block real sign-ins (review M5): at most {@code perIp} open starts per
 * client IP (more are refused), and at most {@code max} overall (when full, the oldest are evicted
 * instead of refusing new ones).
 */
public final class PendingStarts<V> {
    private static final class Entry<V> {
        final V value;
        final String ip;
        final long expires;

        Entry(V value, String ip, long expires) {
            this.value = value;
            this.ip = ip;
            this.expires = expires;
        }
    }

    private final int _max, _perIp;
    private final long _ttlMs;
    private final Map<String, Entry<V>> _byState = new ConcurrentHashMap<>();
    private final Map<String, Integer> _perIpCount = new ConcurrentHashMap<>();

    public PendingStarts(int max, int perIp, long ttlMs) {
        _max = max;
        _perIp = perIp;
        _ttlMs = ttlMs;
    }

    /** Adds a start; false when this IP already has too many open. */
    public synchronized boolean put(String state, V value, String ip, long now) {
        purge(now);
        String key = ip == null ? "" : ip;
        if (_perIpCount.getOrDefault(key, 0) >= _perIp)
            return false;
        while (_byState.size() >= _max)
            evictOldest();
        _byState.put(state, new Entry<>(value, key, now + _ttlMs));
        _perIpCount.merge(key, 1, Integer::sum);
        return true;
    }

    /** Removes and returns the start for state, or null if unknown or expired. */
    public synchronized V take(String state, long now) {
        Entry<V> e = state == null ? null : _byState.remove(state);
        if (e == null)
            return null;
        release(e.ip);
        return e.expires < now ? null : e.value;
    }

    public synchronized int size() {
        return _byState.size();
    }

    private void purge(long now) {
        for (Iterator<Map.Entry<String, Entry<V>>> it = _byState.entrySet().iterator(); it.hasNext(); ) {
            Entry<V> e = it.next().getValue();
            if (e.expires < now) {
                it.remove();
                release(e.ip);
            }
        }
    }

    private void evictOldest() {
        String oldest = null;
        long t = Long.MAX_VALUE;
        for (Map.Entry<String, Entry<V>> e : _byState.entrySet())
            if (e.getValue().expires < t) {
                t = e.getValue().expires;
                oldest = e.getKey();
            }
        if (oldest != null)
            release(_byState.remove(oldest).ip);
    }

    private void release(String ip) {
        _perIpCount.computeIfPresent(ip, (k, n) -> n <= 1 ? null : n - 1);
    }
}
