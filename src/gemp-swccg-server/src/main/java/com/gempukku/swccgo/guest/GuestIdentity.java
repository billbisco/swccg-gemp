package com.gempukku.swccgo.guest;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Who a guest is. The {@code guestSession} cookie holds a random 32-character token; the guest's id is derived
 * from it ({@code guest-} plus 12 characters of its SHA-256), so a returning browser finds its games again after
 * a restart without any session table. Registered names can't start with "guest", so the id never clashes with an
 * account.
 */
public final class GuestIdentity {
    public static final String COOKIE = "guestSession";
    public static final String ID_PREFIX = "guest-";
    /** The cookie lasts as long as a saved game is kept. */
    public static final int COOKIE_DAYS = 15;

    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int TOKEN_LENGTH = 32;
    private static final int ID_LENGTH = 12;
    private static final SecureRandom RANDOM = new SecureRandom();

    private GuestIdentity() {
    }

    public static String newToken() {
        StringBuilder sb = new StringBuilder(TOKEN_LENGTH);
        for (int i = 0; i < TOKEN_LENGTH; i++) {
            sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }

    /** Whether a cookie value looks like a token we issued. Anything else is ignored. */
    public static boolean isToken(String value) {
        if (value == null || value.length() != TOKEN_LENGTH) {
            return false;
        }
        for (int i = 0; i < value.length(); i++) {
            if (ALPHABET.indexOf(value.charAt(i)) < 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * The guest id for a token.
     * @throws IllegalArgumentException if the value is not a token
     */
    public static String guestIdFor(String token) {
        if (!isToken(token)) {
            throw new IllegalArgumentException("Not a guest token");
        }
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.US_ASCII));
            StringBuilder sb = new StringBuilder(ID_PREFIX);
            for (int i = 0; i < ID_LENGTH; i++) {
                sb.append(ALPHABET.charAt((digest[i] & 0xff) % ALPHABET.length()));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    public static boolean isGuestId(String owner) {
        return owner != null && owner.startsWith(ID_PREFIX);
    }
}
