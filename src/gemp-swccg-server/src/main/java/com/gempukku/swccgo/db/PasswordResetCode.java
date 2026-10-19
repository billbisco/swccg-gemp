package com.gempukku.swccgo.db;

/**
 * A stored one-time password reset code (hash only) for a player.
 */
public class PasswordResetCode {
    private final int _playerId;
    private final String _codeHash;
    private final long _expiresAt;

    public PasswordResetCode(int playerId, String codeHash, long expiresAt) {
        _playerId = playerId;
        _codeHash = codeHash;
        _expiresAt = expiresAt;
    }

    public int getPlayerId() {
        return _playerId;
    }

    public String getCodeHash() {
        return _codeHash;
    }

    public long getExpiresAt() {
        return _expiresAt;
    }

    public boolean isExpired(long now) {
        return now >= _expiresAt;
    }
}
