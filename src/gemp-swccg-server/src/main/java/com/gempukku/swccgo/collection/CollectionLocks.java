package com.gempukku.swccgo.collection;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Bounded stripes so each (playerId, collectionType) serializes with itself
 * without a process-wide collections lock and without a lock object per account.
 */
public final class CollectionLocks {
    static final int STRIPE_COUNT = 64;

    private final ReentrantReadWriteLock[] _stripes;

    public CollectionLocks() {
        _stripes = new ReentrantReadWriteLock[STRIPE_COUNT];
        for (int i = 0; i < STRIPE_COUNT; i++)
            _stripes[i] = new ReentrantReadWriteLock();
    }

    public ReentrantReadWriteLock forCollection(int playerId, String type) {
        int typeHash = type == null ? 0 : type.hashCode();
        int index = Math.floorMod(31 * playerId + typeHash, STRIPE_COUNT);
        return _stripes[index];
    }
}
