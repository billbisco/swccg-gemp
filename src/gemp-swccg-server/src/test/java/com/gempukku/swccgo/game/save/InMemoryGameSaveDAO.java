package com.gempukku.swccgo.game.save;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** GameSaveDAO kept in memory, storing the same bytes the database would. */
public class InMemoryGameSaveDAO implements GameSaveDAO {
    private record Row(String owner, String header, byte[] log, byte[] snapshot, int snapshotLogSize, String fingerprint, long updatedAt) {
    }

    private final Map<String, Row> _rows = new HashMap<>();
    public boolean failSaves;
    public final java.util.Set<String> failSavesFor = new java.util.HashSet<>();
    public int saves;

    @Override
    public void save(String gameId, String owner, GameSave save) {
        if (failSaves || failSavesFor.contains(gameId)) {
            throw new RuntimeException("database unavailable");
        }
        saves++;
        _rows.put(gameId, new Row(owner, save.getHeader().toJson(), save.getLog().toBytes(), save.getSnapshot(), save.getSnapshotLogSize(), save.getSnapshotFingerprint(), save.getHeader().updatedAt));
    }

    @Override
    public GameSave load(String gameId) {
        Row r = _rows.get(gameId);
        return r == null ? null : new GameSave(GameSaveHeader.fromJson(r.header()), DecisionLog.fromBytes(r.log()), r.snapshot(), r.snapshotLogSize(), r.fingerprint());
    }

    @Override
    public GameSaveHeader loadHeader(String gameId) {
        Row r = _rows.get(gameId);
        return r == null ? null : GameSaveHeader.fromJson(r.header());
    }

    @Override
    public String getOwner(String gameId) {
        Row r = _rows.get(gameId);
        return r == null ? null : r.owner();
    }

    @Override
    public List<String> getGameIds(String owner) {
        List<String> ids = new ArrayList<>();
        _rows.forEach((id, r) -> {
            if (r.owner().equals(owner)) {
                ids.add(id);
            }
        });
        return ids;
    }

    @Override
    public void delete(String gameId) {
        _rows.remove(gameId);
    }

    @Override
    public void changeOwner(String fromOwner, String toOwner) {
        _rows.replaceAll((id, r) -> r.owner().equals(fromOwner) ? new Row(toOwner, r.header(), r.log(), r.snapshot(), r.snapshotLogSize(), r.fingerprint(), r.updatedAt()) : r);
    }

    @Override
    public int deleteLastPlayedAtOrBefore(long cutoffMillis) {
        int before = _rows.size();
        _rows.values().removeIf(r -> r.updatedAt() <= cutoffMillis);
        return before - _rows.size();
    }

    boolean has(String gameId) {
        return _rows.containsKey(gameId);
    }

    int snapshotLogSize(String gameId) {
        return _rows.get(gameId).snapshotLogSize();
    }
}
