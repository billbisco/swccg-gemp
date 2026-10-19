package com.gempukku.swccgo.security;

/**
 * Server-side rules for new passwords (registration and password reset).
 * Existing shorter passwords keep working at login; the rule applies only when a password is set.
 */
public final class PasswordPolicy {
    public static final int MIN_LENGTH = 8;
    /** Upper bound so a huge POST body cannot make hashing expensive. */
    public static final int MAX_LENGTH = 128;

    private PasswordPolicy() {
    }

    public static boolean isAcceptable(String password) {
        return password != null && password.length() >= MIN_LENGTH && password.length() <= MAX_LENGTH;
    }
}
