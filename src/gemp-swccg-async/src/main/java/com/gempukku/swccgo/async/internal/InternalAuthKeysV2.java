package com.gempukku.swccgo.async.internal;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Per-site HMAC keys for contract "GEMP-AUTH-V2" (docs/swccg-login-auth-api.md). Each caller
 * (for example swccg-login Test and swccg-login production) has its own key id and secret, so
 * one site's key can be rotated or withdrawn without touching the others. The key id is part of
 * the signed string, so a signature made for one key id never verifies for another.
 *
 * Configured from GEMP_INTERNAL_AUTH_V2_KEYS = "keyid=secret[,previous];keyid2=secret2".
 */
public final class InternalAuthKeysV2 {
    public static final String REQUEST_PREFIX = "GEMP-AUTH-V2";
    public static final String RESPONSE_PREFIX = "GEMP-AUTH-V2-RESP";
    private static final Pattern KEY_ID = Pattern.compile("^[a-z0-9][a-z0-9-]{1,40}$");

    private final Map<String, List<byte[]>> _keys;

    public InternalAuthKeysV2(Map<String, List<String>> keys) {
        Map<String, List<byte[]>> m = new HashMap<>();
        for (Map.Entry<String, List<String>> e : keys.entrySet()) {
            if (!KEY_ID.matcher(e.getKey()).matches())
                throw new IllegalArgumentException("bad key id");
            List<byte[]> secrets = new ArrayList<>();
            for (String s : e.getValue()) {
                if (s == null || s.getBytes(StandardCharsets.UTF_8).length < InternalAuthSigner.MIN_SECRET_BYTES)
                    throw new IllegalArgumentException("internal auth secret for " + e.getKey() + " must be at least "
                            + InternalAuthSigner.MIN_SECRET_BYTES + " bytes");
                secrets.add(s.getBytes(StandardCharsets.UTF_8));
            }
            if (secrets.isEmpty())
                throw new IllegalArgumentException("no secret for " + e.getKey());
            m.put(e.getKey(), Collections.unmodifiableList(secrets));
        }
        _keys = Collections.unmodifiableMap(m);
    }

    /** Parses the environment format; returns null for null/blank input. Never logs secrets. */
    public static InternalAuthKeysV2 parse(String env) {
        if (env == null || env.isBlank())
            return null;
        Map<String, List<String>> keys = new HashMap<>();
        for (String entry : env.split(";")) {
            entry = entry.trim();
            if (entry.isEmpty())
                continue;
            int eq = entry.indexOf('=');
            if (eq <= 0)
                throw new IllegalArgumentException("GEMP_INTERNAL_AUTH_V2_KEYS: expected keyid=secret");
            List<String> secrets = new ArrayList<>();
            for (String s : entry.substring(eq + 1).split(","))
                if (!s.isBlank())
                    secrets.add(s.trim());
            keys.put(entry.substring(0, eq).trim(), secrets);
        }
        return keys.isEmpty() ? null : new InternalAuthKeysV2(keys);
    }

    public boolean hasKey(String keyId) {
        return keyId != null && _keys.containsKey(keyId);
    }

    public static String requestCanonical(String keyId, String timestamp, String nonce, String method, String path, byte[] body) {
        return REQUEST_PREFIX + "\n" + keyId + "\n" + timestamp + "\n" + nonce + "\n" + method + "\n" + path + "\n"
                + InternalAuthSigner.sha256Hex(body);
    }

    public static String responseCanonical(String keyId, String nonce, int status, byte[] body) {
        return RESPONSE_PREFIX + "\n" + keyId + "\n" + nonce + "\n" + status + "\n" + InternalAuthSigner.sha256Hex(body);
    }

    /** "v2=" + hex HMAC with the key id's current secret. */
    public String sign(String keyId, String canonical) {
        List<byte[]> s = _keys.get(keyId);
        if (s == null)
            throw new IllegalArgumentException("unknown key id");
        return "v2=" + hex(hmac(s.get(0), canonical));
    }

    public boolean verify(String keyId, String canonical, String header) {
        List<byte[]> secrets = _keys.get(keyId);
        if (secrets == null || header == null || !header.startsWith("v2="))
            return false;
        byte[] given = header.substring(3).getBytes(StandardCharsets.US_ASCII);
        boolean ok = false;
        for (byte[] secret : secrets)
            ok |= MessageDigest.isEqual(hex(hmac(secret, canonical)).getBytes(StandardCharsets.US_ASCII), given);
        return ok;
    }

    static byte[] hmac(byte[] secret, String canonical) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret, "HmacSHA256"));
            return mac.doFinal(canonical.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new IllegalStateException("HmacSHA256 unavailable", e);
        }
    }

    static String hex(byte[] bytes) {
        return java.util.HexFormat.of().formatHex(bytes);
    }
}
