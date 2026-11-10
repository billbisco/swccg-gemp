package com.gempukku.swccgo.async.oidc;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Signature;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class JwtVerifierTest {
    static KeyPair kp, other;
    static final String ISS = "https://login.example/test";
    static final long NOW = 1_760_000_000L;

    @BeforeAll
    static void keys() throws Exception {
        KeyPairGenerator g = KeyPairGenerator.getInstance("RSA");
        g.initialize(2048);
        kp = g.generateKeyPair();
        other = g.generateKeyPair();
    }

    static String b64(byte[] b) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(b);
    }

    static String b64u(BigInteger i) {
        byte[] b = i.toByteArray();
        if (b[0] == 0) {
            byte[] t = new byte[b.length - 1];
            System.arraycopy(b, 1, t, 0, t.length);
            b = t;
        }
        return b64(b);
    }

    static String jwks() {
        RSAPublicKey k = (RSAPublicKey) kp.getPublic();
        return "{\"keys\":[{\"kty\":\"RSA\",\"use\":\"sig\",\"alg\":\"RS256\",\"kid\":\"k1\",\"n\":\"" + b64u(k.getModulus()) + "\",\"e\":\"" + b64u(k.getPublicExponent()) + "\"}]}";
    }

    static String jwt(String header, String claims, KeyPair key) throws Exception {
        String si = b64(header.getBytes(StandardCharsets.UTF_8)) + "." + b64(claims.getBytes(StandardCharsets.UTF_8));
        Signature s = Signature.getInstance("SHA256withRSA");
        s.initSign(key.getPrivate());
        s.update(si.getBytes(StandardCharsets.US_ASCII));
        return si + "." + b64(s.sign());
    }

    static String claims(String aud, long iat, long exp, String extra) {
        return "{\"iss\":\"" + ISS + "\",\"aud\":" + aud + ",\"sub\":\"42\",\"iat\":" + iat + ",\"exp\":" + exp + extra + "}";
    }

    JwtVerifier v = new JwtVerifier(ISS, "gemp-test", JwtVerifierTest::jwks);
    final String H = "{\"alg\":\"RS256\",\"kid\":\"k1\"}";

    @Test
    void acceptsGoodToken() throws Exception {
        var c = v.verify(jwt(H, claims("\"gemp-test\"", NOW, NOW + 600, ",\"nonce\":\"n\""), kp), NOW, null);
        assertEquals(42, JwtVerifier.playerId(c));
    }

    @Test
    void refusesBadTokens() throws Exception {
        String good = claims("\"gemp-test\"", NOW, NOW + 600, "");
        assertThrows(JwtVerifier.InvalidTokenException.class, () -> v.verify(jwt(H, good, other), NOW, null), "wrong key");
        assertThrows(JwtVerifier.InvalidTokenException.class, () -> v.verify(jwt("{\"alg\":\"none\",\"kid\":\"k1\"}", good, kp), NOW, null), "alg none");
        assertThrows(JwtVerifier.InvalidTokenException.class, () -> v.verify(jwt("{\"alg\":\"HS256\",\"kid\":\"k1\"}", good, kp), NOW, null), "alg hs");
        assertThrows(JwtVerifier.InvalidTokenException.class, () -> v.verify(jwt(H, claims("\"forum\"", NOW, NOW + 600, ""), kp), NOW, null), "aud");
        assertThrows(JwtVerifier.InvalidTokenException.class, () -> v.verify(jwt(H, claims("[\"gemp-test\",\"forum\"]", NOW, NOW + 600, ""), kp), NOW, null), "azp");
        assertThrows(JwtVerifier.InvalidTokenException.class, () -> v.verify(jwt(H, claims("\"gemp-test\"", NOW - 4000, NOW - 3000, ""), kp), NOW, null), "expired");
        assertThrows(JwtVerifier.InvalidTokenException.class, () -> v.verify(jwt(H, good.replace(ISS, "https://evil.example"), kp), NOW, null), "iss");
        assertThrows(JwtVerifier.InvalidTokenException.class, () -> v.verify(jwt("{\"alg\":\"RS256\",\"kid\":\"k9\"}", good, kp), NOW, null), "kid");
        String tampered = jwt(H, good, kp);
        String[] p = tampered.split("\\.");
        String evil = p[0] + "." + b64(good.replace("\"42\"", "\"1\"").getBytes(StandardCharsets.UTF_8)) + "." + p[2];
        assertThrows(JwtVerifier.InvalidTokenException.class, () -> v.verify(evil, NOW, null), "tampered");
        String jwtTyp = jwt("{\"alg\":\"RS256\",\"kid\":\"k1\",\"typ\":\"JWT\"}", good, kp);
        assertThrows(JwtVerifier.InvalidTokenException.class, () -> v.verify(jwtTyp, NOW, "logout+jwt"), "typ");
    }

    @Test
    void subMustBeDecimal() throws Exception {
        var c = v.verify(jwt(H, claims("\"gemp-test\"", NOW, NOW + 600, "").replace("\"42\"", "\"042\""), kp), NOW, null);
        assertThrows(JwtVerifier.InvalidTokenException.class, () -> JwtVerifier.playerId(c));
    }
}
