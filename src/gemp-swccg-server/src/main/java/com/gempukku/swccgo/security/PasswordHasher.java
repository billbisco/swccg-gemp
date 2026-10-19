package com.gempukku.swccgo.security;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.regex.Pattern;

/**
 * Hashes and verifies player passwords.
 *
 * New hashes are salted PBKDF2-HMAC-SHA256, stored as a self-describing string:
 * {@code pbkdf2-sha256$<iterations>$<base64 salt>$<base64 hash>} (about 90 characters).
 *
 * Older accounts held an unsalted SHA-256 hex digest (64 lowercase hex characters). Those are
 * converted in bulk, without knowing the password, to a wrapped form
 * {@code pbkdf2-sha256-legacy$<iterations>$<salt>$<hash>} = PBKDF2(sha256hex(password)), so no
 * unsalted hash stays in the database (see {@link LegacyHashMigration}).
 * {@link #verify} accepts all three, and {@link #needsRehash} tells the caller to replace a
 * legacy, wrapped or weaker hash with a plain PBKDF2 hash after a successful login.
 *
 * Uses only the JDK, so no extra dependency is needed.
 */
public final class PasswordHasher {
    public static final String PBKDF2_PREFIX = "pbkdf2-sha256";
    /** PBKDF2 over the old unsalted SHA-256 hex digest, for accounts migrated without their password. */
    public static final String WRAPPED_LEGACY_PREFIX = "pbkdf2-sha256-legacy";
    /** OWASP 2023 guidance for PBKDF2-HMAC-SHA256. */
    public static final int DEFAULT_ITERATIONS = 600_000;
    private static final int SALT_BYTES = 16;
    private static final int KEY_BITS = 256;
    private static final Pattern LEGACY_SHA256 = Pattern.compile("^[0-9a-f]{64}$");

    private final int _iterations;
    private final SecureRandom _random = new SecureRandom();

    public PasswordHasher() {
        this(DEFAULT_ITERATIONS);
    }

    public PasswordHasher(int iterations) {
        if (iterations < 1)
            throw new IllegalArgumentException("iterations must be positive");
        _iterations = iterations;
    }

    public int getIterations() {
        return _iterations;
    }

    /** Returns a new salted hash of the password. */
    public String hash(String password) {
        return format(PBKDF2_PREFIX, password);
    }

    /**
     * Wraps a stored legacy SHA-256 hex digest in salted PBKDF2, so it can be replaced without the password.
     * @throws IllegalArgumentException if the value is not a legacy digest
     */
    public String wrapLegacy(String legacySha256Hex) {
        if (!isLegacySha256(legacySha256Hex))
            throw new IllegalArgumentException("not a legacy SHA-256 hash");
        return format(WRAPPED_LEGACY_PREFIX, legacySha256Hex);
    }

    private String format(String prefix, String secret) {
        byte[] salt = new byte[SALT_BYTES];
        _random.nextBytes(salt);
        byte[] key = pbkdf2(secret, salt, _iterations);
        Base64.Encoder enc = Base64.getEncoder().withoutPadding();
        return prefix + "$" + _iterations + "$" + enc.encodeToString(salt) + "$" + enc.encodeToString(key);
    }

    /**
     * Checks a password against a stored hash in either the new or the legacy format.
     * Any other stored value (blank, reset marker, malformed) never matches.
     */
    public boolean verify(String password, String stored) {
        if (password == null || stored == null || stored.isEmpty())
            return false;
        if (isLegacySha256(stored)) {
            return MessageDigest.isEqual(
                    legacySha256(password).getBytes(StandardCharsets.US_ASCII),
                    stored.getBytes(StandardCharsets.US_ASCII));
        }
        if (stored.startsWith(PBKDF2_PREFIX + "$"))
            return verifyPbkdf2(password, stored);
        if (stored.startsWith(WRAPPED_LEGACY_PREFIX + "$"))
            return verifyPbkdf2(legacySha256(password), stored);
        return false;
    }

    private static boolean verifyPbkdf2(String secret, String stored) {
        String[] parts = stored.split("\\$");
        if (parts.length != 4)
            return false;
        try {
            int iterations = Integer.parseInt(parts[1]);
            if (iterations < 1 || iterations > 10_000_000)
                return false;
            Base64.Decoder dec = Base64.getDecoder();
            byte[] salt = dec.decode(parts[2]);
            byte[] expected = dec.decode(parts[3]);
            byte[] actual = pbkdf2(secret, salt, iterations);
            return MessageDigest.isEqual(expected, actual);
        } catch (IllegalArgumentException exp) {
            return false;
        }
    }

    /** True when the stored hash is legacy, wrapped legacy, or uses fewer iterations than this hasher. */
    public boolean needsRehash(String stored) {
        if (stored == null || !stored.startsWith(PBKDF2_PREFIX + "$"))
            return true;
        String[] parts = stored.split("\\$");
        try {
            return parts.length != 4 || Integer.parseInt(parts[1]) < _iterations;
        } catch (NumberFormatException exp) {
            return true;
        }
    }

    public static boolean isLegacySha256(String stored) {
        return stored != null && LEGACY_SHA256.matcher(stored).matches();
    }

    /** The original GEMP hash: unsalted SHA-256, lowercase hex. Kept only to verify old accounts. */
    public static String legacySha256(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return toHex(digest.digest(password.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(Character.forDigit((b >> 4) & 0xF, 16)).append(Character.forDigit(b & 0xF, 16));
        }
        return sb.toString();
    }

    private static byte[] pbkdf2(String password, byte[] salt, int iterations) {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, KEY_BITS);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (Exception e) {
            throw new RuntimeException("PBKDF2 unavailable", e);
        } finally {
            spec.clearPassword();
        }
    }
}
