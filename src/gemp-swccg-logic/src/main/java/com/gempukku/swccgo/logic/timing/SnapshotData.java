package com.gempukku.swccgo.logic.timing;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Defines data that is used while creating a snapshot.
 */
public class SnapshotData {
    // Identity: a copy is put in the map before its fields (and so its hashCode) are set.
    private Map<Snapshotable, Snapshotable> snapshotableMap = new IdentityHashMap<Snapshotable, Snapshotable>();

    /**
     * Gets the snapshotable to store in the snapshot given a snapshotable. This will return the same snapshotable
     * if a snapshot of it is already in the snapshot data, otherwise a new snapshot is taken and returned.
     * @param data the snapshotable
     */
    public <T extends Snapshotable> T getDataForSnapshot(T data) {
        if (data == null) {
            return null;
        }
        Snapshotable dataToReturn = snapshotableMap.get(data);
        if (dataToReturn == null) {
            try {
                dataToReturn = data.getClass().getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            snapshotableMap.put(data, dataToReturn);
            snapshotableMap.put(dataToReturn, dataToReturn);
            data.generateSnapshot(dataToReturn, this);
        }
        return (T) dataToReturn;
    }
}
