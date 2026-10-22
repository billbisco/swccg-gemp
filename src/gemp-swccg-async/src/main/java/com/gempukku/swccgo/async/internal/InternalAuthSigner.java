package com.gempukku.swccgo.async.internal;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * HMAC-SHA256 request/response signing for the private wiki -> GEMP auth API.
 * See docs/wiki-auth-api.md (contract "GEMP-AUTH-V1"). The PHP side (MediaWiki
 * GempAuth extension) computes byte-identical strings; both test suites share
 * the same known-answer vector.
 */
public final class InternalAuthSigner {
    public static final String REQUEST_PREFIX = "GEMP-AUTH-V1";
    public static final String RESPONSE_PREFIX = "GEMP-AUTH-V1-RESP";
    public static final int MIN_SECRET_BYTES = 32;

    private final List<byte[]> _secrets;

    /**
     * @param current  the active shared secret (at least 32 bytes)
     * @param previous an optional previous secret still accepted on incoming requests (rotation), or null
     */
    public InternalAuthSigner(String current, String previous) {
        if (current == null || current.getBytes(StandardCharsets.UTF_8).length < MIN_SECRET_BYTES)
            throw new IllegalArgumentException("internal auth secret must be at least " + MIN_SECRET_BYTES + " bytes");
        List<byte[]> secrets = new ArrayList<>();
        secrets.add(current.getBytes(StandardCharsets.UTF_8));
        if (previous != null && previous.getBytes(StandardCharsets.UTF_8).length >= MIN_SECRET_BYTES)
            secrets.add(previous.getBytes(StandardCharsets.UTF_8));
        _secrets = Collections.unmodifiableList(secrets);
    }

    public static String requestCanonical(String timestamp, String nonce, String method, String path, byte[] body) {
        return REQUEST_PREFIX + "\n" + timestamp + "\n" + nonce + "\n" + method + "\n" + path + "\n" + sha256Hex(body);
    }

    public static String responseCanonical(String nonce, int status, byte[] body) {
        return RESPONSE_PREFIX + "\n" + nonce + "\n" + status + "\n" + sha256Hex(body);
    }

    /** Signs with the current secret; returns "v1=" + lowercase hex. */
    public String sign(String canonical) {
        return "v1=" + hex(hmac(_secrets.get(0), canonical));
    }

    /** Constant-time check against the current and (if set) previous secret. */
    public boolean verify(String canonical, String signatureHeader) {
        if (signatureHeader == null || !signatureHeader.startsWith("v1="))
            return false;
        byte[] given = signatureHeader.substring(3).getBytes(StandardCharsets.US_ASCII);
        boolean ok = false;
        for (byte[] secret : _secrets) {
            byte[] expected = hex(hmac(secret, canonical)).getBytes(StandardCharsets.US_ASCII);
            ok |= MessageDigest.isEqual(expected, given);
        }
        return ok;
    }

    private static byte[] hmac(byte[] secret, String canonical) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret, "HmacSHA256"));
            return mac.doFinal(canonical.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new IllegalStateException("HmacSHA256 unavailable", e);
        }
    }

    public static String sha256Hex(byte[] data) {
        try {
            return hex(MessageDigest.getInstance("SHA-256").digest(data == null ? new byte[0] : data));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private static String hex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes)
            sb.append(Character.forDigit((b >> 4) & 0xF, 16)).append(Character.forDigit(b & 0xF, 16));
        return sb.toString();
    }
}
