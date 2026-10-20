package com.gempukku.swccgo.game.save;

/**
 * A saved game: the header (enough to rebuild from scratch), the move log, and optionally a snapshot taken at a
 * quiet point after {@code snapshotLogSize} answers, readable only by the build with {@code snapshotFingerprint}.
 */
public final class GameSave {
    private final GameSaveHeader _header;
    private final DecisionLog _log;
    private final byte[] _snapshot;
    private final int _snapshotLogSize;
    private final String _snapshotFingerprint;

    public GameSave(GameSaveHeader header, DecisionLog log, byte[] snapshot, int snapshotLogSize, String snapshotFingerprint) {
        _header = header;
        _log = log;
        _snapshot = snapshot;
        _snapshotLogSize = snapshotLogSize;
        _snapshotFingerprint = snapshotFingerprint;
    }

    public GameSaveHeader getHeader() {
        return _header;
    }

    public DecisionLog getLog() {
        return _log;
    }

    /** The compressed Kryo snapshot, or null. */
    public byte[] getSnapshot() {
        return _snapshot;
    }

    public int getSnapshotLogSize() {
        return _snapshotLogSize;
    }

    public String getSnapshotFingerprint() {
        return _snapshotFingerprint;
    }
}
