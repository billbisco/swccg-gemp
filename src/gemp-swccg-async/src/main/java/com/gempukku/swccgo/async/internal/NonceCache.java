package com.gempukku.swccgo.async.internal;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/** Remembers request nonces for the timestamp-skew window so a captured request cannot be replayed. */
public final class NonceCache {
    private final long _ttlMillis;
    private final int _maxEntries;
    private final LinkedHashMap<String, Long> _seen = new LinkedHashMap<>();

    public NonceCache(long ttlMillis, int maxEntries) {
        _ttlMillis = ttlMillis;
        _maxEntries = maxEntries;
    }

    /** @return true if the nonce is new (and is now remembered), false if it was already used. */
    public synchronized boolean firstUse(String nonce, long now) {
        Iterator<Map.Entry<String, Long>> it = _seen.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Long> e = it.next();
            if (e.getValue() > now - _ttlMillis && _seen.size() <= _maxEntries)
                break;
            it.remove();
        }
        if (_seen.containsKey(nonce))
            return false;
        _seen.put(nonce, now);
        return true;
    }
}
