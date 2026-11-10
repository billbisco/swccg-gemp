package com.gempukku.swccgo.security;

/**
 * Server-side rules for new passwords (registration and password reset).
 * Any non-empty password up to {@link #MAX_LENGTH} characters is accepted (Bill, 2026-11-09:
 * no minimum length). The rule applies only when a password is set; login is unaffected.
 */
public final class PasswordPolicy {
    public static final int MIN_LENGTH = 1;
    /** Upper bound so a huge POST body cannot make hashing expensive. */
    public static final int MAX_LENGTH = 128;

    private PasswordPolicy() {
    }

    public static boolean isAcceptable(String password) {
        return password != null && password.length() >= MIN_LENGTH && password.length() <= MAX_LENGTH;
    }

    /** The message shown when a new password is rejected (empty or too long). */
    public static String rejectionMessage() {
        return "Please enter a password of up to " + MAX_LENGTH + " characters.";
    }
}
