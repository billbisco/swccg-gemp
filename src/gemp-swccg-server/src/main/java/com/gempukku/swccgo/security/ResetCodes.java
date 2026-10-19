package com.gempukku.swccgo.security;

import java.security.SecureRandom;

/**
 * One-time password reset codes that an admin hands to a player privately.
 * Codes are random, use an alphabet without look-alike characters, and are stored only as a hash.
 */
public final class ResetCodes {
    /** No 0/O, 1/I/L, so a code read aloud or copied by hand survives. */
    private static final char[] ALPHABET = "ABCDEFGHJKMNPQRSTUVWXYZ23456789".toCharArray();
    public static final int CODE_LENGTH = 12; // ~59 bits
    public static final long DEFAULT_TTL_MILLIS = 24L * 60 * 60 * 1000;

    private static final SecureRandom RANDOM = new SecureRandom();

    private ResetCodes() {
    }

    public static String newCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH + 2);
        for (int i = 0; i < CODE_LENGTH; i++) {
            if (i > 0 && i % 4 == 0)
                sb.append('-');
            sb.append(ALPHABET[RANDOM.nextInt(ALPHABET.length)]);
        }
        return sb.toString();
    }

    /** Uppercases and drops spaces/dashes so "abcd efgh-jkmn" matches "ABCD-EFGH-JKMN". */
    public static String normalize(String code) {
        if (code == null)
            return "";
        StringBuilder sb = new StringBuilder();
        for (char c : code.toCharArray()) {
            if (c == '-' || Character.isWhitespace(c))
                continue;
            sb.append(Character.toUpperCase(c));
        }
        return sb.toString();
    }

    /**
     * The value stored in the database. The code is high-entropy, short-lived and single-use,
     * so a plain SHA-256 is enough; the stored hash is useless to someone who reads the DB.
     */
    public static String hash(String code) {
        return PasswordHasher.legacySha256("reset:" + normalize(code));
    }

    public static boolean looksLikeCode(String candidate) {
        String n = normalize(candidate);
        if (n.length() != CODE_LENGTH)
            return false;
        for (char c : n.toCharArray()) {
            if (new String(ALPHABET).indexOf(c) < 0)
                return false;
        }
        return true;
    }
}
