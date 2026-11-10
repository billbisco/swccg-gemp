package com.gempukku.swccgo.async.internal;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class InternalAuthKeysV2Test {
    static final String SECRET = "0123456789abcdef0123456789abcdef";

    @Test
    void knownAnswerVectorMatchesSwccgLogin() {
        // Same vector as swccg-login internal/gemp TestKnownAnswerV2 (Go).
        InternalAuthKeysV2 keys = InternalAuthKeysV2.parse("swccg-login-test=" + SECRET);
        String req = InternalAuthKeysV2.requestCanonical("swccg-login-test", "1760000000", "abcdefghijklmnop0123", "POST",
                "/internal/v2/verify", "{\"a\":1}".getBytes(StandardCharsets.UTF_8));
        assertEquals("v2=554c83ae7d4edea8379d9528afd8e446d3873e01abb7de847c11b8e09e42347d", keys.sign("swccg-login-test", req));
        String resp = InternalAuthKeysV2.responseCanonical("swccg-login-test", "abcdefghijklmnop0123", 200,
                "{\"ok\":true}".getBytes(StandardCharsets.UTF_8));
        assertEquals("v2=24d9cb079f911af1a4a414ecf9dfb213c2c265bec0f71da2274b1b314c4d220a", keys.sign("swccg-login-test", resp));
    }

    @Test
    void keyIdsAreSeparateAndRotationWorks() {
        InternalAuthKeysV2 keys = InternalAuthKeysV2.parse("a-site=" + SECRET + "," + SECRET.toUpperCase() + "; b-site=" + "x".repeat(40));
        String c = InternalAuthKeysV2.requestCanonical("a-site", "1", "abcdefghijklmnop", "POST", "/p", new byte[0]);
        String sig = keys.sign("a-site", c);
        assertTrue(keys.verify("a-site", c, sig));
        assertFalse(keys.verify("b-site", c, sig));
        // previous secret still verifies
        InternalAuthKeysV2 old = InternalAuthKeysV2.parse("a-site=" + SECRET.toUpperCase());
        assertTrue(keys.verify("a-site", c, old.sign("a-site", c)));
        assertFalse(keys.verify("a-site", c, "v1=" + sig.substring(3)));
    }

    @Test
    void rejectsShortSecretsAndBadIds() {
        assertThrows(IllegalArgumentException.class, () -> InternalAuthKeysV2.parse("a-site=short"));
        assertThrows(IllegalArgumentException.class, () -> InternalAuthKeysV2.parse("Bad Id=" + SECRET));
        assertNull(InternalAuthKeysV2.parse(" "));
    }
}
