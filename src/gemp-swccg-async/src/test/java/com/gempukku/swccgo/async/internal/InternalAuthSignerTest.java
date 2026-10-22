package com.gempukku.swccgo.async.internal;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class InternalAuthSignerTest {
    static final String SECRET = "0123456789abcdef0123456789abcdef";
    static final byte[] BODY = "{\"username\":\"Bill\",\"password\":\"hunter22\",\"client_ip\":\"203.0.113.7\"}".getBytes(StandardCharsets.UTF_8);

    /** Same known-answer vector as the PHP GempClientTest; keeps both sides byte-identical. */
    @Test
    void requestKnownAnswer() {
        InternalAuthSigner s = new InternalAuthSigner(SECRET, null);
        String c = InternalAuthSigner.requestCanonical("1760000000", "abcdefghijklmnop", "POST", "/internal/v1/verify", BODY);
        assertEquals("v1=46e1e96f0b78c038fe19bbe9c6fd7214d328d11f5261711b99876c3f7e22abf1", s.sign(c));
        assertTrue(s.verify(c, "v1=46e1e96f0b78c038fe19bbe9c6fd7214d328d11f5261711b99876c3f7e22abf1"));
    }

    @Test
    void responseKnownAnswer() {
        InternalAuthSigner s = new InternalAuthSigner(SECRET, null);
        byte[] body = "{\"ok\":false,\"error\":\"invalid_credentials\"}".getBytes(StandardCharsets.UTF_8);
        assertEquals("v1=bdc04e5d3a97a420aa22a9394c66ae2a5408d52ff1c6aded80ce6bae78c1486a",
                s.sign(InternalAuthSigner.responseCanonical("abcdefghijklmnop", 200, body)));
    }

    @Test
    void rejectsWrongSecretTamperAndMissing() {
        InternalAuthSigner good = new InternalAuthSigner(SECRET, null);
        InternalAuthSigner other = new InternalAuthSigner("ffffffffffffffffffffffffffffffff", null);
        String c = InternalAuthSigner.requestCanonical("1", "abcdefghijklmnop", "POST", "/p", BODY);
        assertFalse(good.verify(c, other.sign(c)));
        assertFalse(good.verify(c + "x", good.sign(c)));
        assertFalse(good.verify(c, null));
        assertFalse(good.verify(c, good.sign(c).substring(3)));
    }

    @Test
    void acceptsPreviousSecretDuringRotation() {
        String old = "oldoldoldoldoldoldoldoldoldoldold";
        InternalAuthSigner rotated = new InternalAuthSigner(SECRET, old);
        String c = InternalAuthSigner.requestCanonical("1", "abcdefghijklmnop", "POST", "/p", BODY);
        assertTrue(rotated.verify(c, new InternalAuthSigner(old, null).sign(c)));
        // Responses are always signed with the current secret.
        assertEquals(new InternalAuthSigner(SECRET, null).sign(c), rotated.sign(c));
    }

    @Test
    void refusesShortSecret() {
        assertThrows(IllegalArgumentException.class, () -> new InternalAuthSigner("short", null));
        assertThrows(IllegalArgumentException.class, () -> new InternalAuthSigner(null, null));
    }
}
