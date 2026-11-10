package com.gempukku.swccgo.async.oidc;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Strict RS256 JWT checks for swccg-login's ID tokens and logout tokens. Only RS256 is accepted
 * (never "none" or HMAC), the key must be in the issuer's published JWKS, and iss, aud, azp, exp
 * and iat are always checked. Kept small and dependency-free on purpose.
 */
public final class JwtVerifier {
    public static final long SKEW_SECONDS = 60;
    public static final long MAX_AGE_SECONDS = 600;

    public static final class InvalidTokenException extends Exception {
        public InvalidTokenException(String m) {
            super(m);
        }
    }

    private final String _issuer;
    private final String _clientId;
    /** Fetches the JWKS JSON; called again (at most once a minute) when a kid is unknown. */
    private final Supplier<String> _jwksFetcher;
    private volatile Map<String, PublicKey> _keys = new HashMap<>();
    private volatile long _lastFetch;

    public JwtVerifier(String issuer, String clientId, Supplier<String> jwksFetcher) {
        _issuer = issuer;
        _clientId = clientId;
        _jwksFetcher = jwksFetcher;
    }

    /** Verifies signature and the standard claims; returns the claims. */
    public JSONObject verify(String jwt, long nowSeconds, String expectedTyp) throws InvalidTokenException {
        if (jwt == null || jwt.length() > 16384)
            throw new InvalidTokenException("missing");
        String[] parts = jwt.split("\\.", -1);
        if (parts.length != 3)
            throw new InvalidTokenException("malformed");
        JSONObject header = json(parts[0]);
        if (!"RS256".equals(header.get("alg")))
            throw new InvalidTokenException("alg");
        if (header.get("crit") != null)
            throw new InvalidTokenException("crit");
        if (expectedTyp != null && header.get("typ") != null && !expectedTyp.equalsIgnoreCase((String) header.get("typ")))
            throw new InvalidTokenException("typ");
        Object kid = header.get("kid");
        if (!(kid instanceof String))
            throw new InvalidTokenException("kid");
        PublicKey key = key((String) kid, nowSeconds);
        try {
            Signature sig = Signature.getInstance("SHA256withRSA");
            sig.initVerify(key);
            sig.update((parts[0] + "." + parts[1]).getBytes(StandardCharsets.US_ASCII));
            if (!sig.verify(Base64.getUrlDecoder().decode(parts[2])))
                throw new InvalidTokenException("signature");
        } catch (InvalidTokenException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidTokenException("signature");
        }
        JSONObject c = json(parts[1]);
        if (!_issuer.equals(c.get("iss")))
            throw new InvalidTokenException("iss");
        Object aud = c.get("aud");
        if (aud instanceof String) {
            if (!_clientId.equals(aud))
                throw new InvalidTokenException("aud");
        } else if (aud instanceof JSONArray) {
            JSONArray a = (JSONArray) aud;
            if (!a.contains(_clientId))
                throw new InvalidTokenException("aud");
            if (a.size() > 1 && !_clientId.equals(c.get("azp")))
                throw new InvalidTokenException("azp");
        } else {
            throw new InvalidTokenException("aud");
        }
        if (c.get("azp") != null && !_clientId.equals(c.get("azp")))
            throw new InvalidTokenException("azp");
        long iat = num(c.get("iat"), "iat");
        if (iat > nowSeconds + SKEW_SECONDS || iat < nowSeconds - MAX_AGE_SECONDS)
            throw new InvalidTokenException("iat");
        long exp = num(c.get("exp"), "exp");
        if (exp < nowSeconds - SKEW_SECONDS)
            throw new InvalidTokenException("exp");
        return c;
    }

    /** sub as a GEMP player id; refuses anything that is not a plain positive decimal. */
    public static int playerId(JSONObject claims) throws InvalidTokenException {
        Object sub = claims.get("sub");
        if (!(sub instanceof String) || !((String) sub).matches("^[1-9][0-9]{0,9}$"))
            throw new InvalidTokenException("sub");
        long v = Long.parseLong((String) sub);
        if (v > Integer.MAX_VALUE)
            throw new InvalidTokenException("sub");
        return (int) v;
    }

    private PublicKey key(String kid, long now) throws InvalidTokenException {
        PublicKey k = _keys.get(kid);
        if (k == null && (now - _lastFetch >= 60 || _keys.isEmpty())) {
            _lastFetch = now;
            _keys = parseJwks(_jwksFetcher.get());
            k = _keys.get(kid);
        }
        if (k == null)
            throw new InvalidTokenException("unknown kid");
        return k;
    }

    static Map<String, PublicKey> parseJwks(String json) {
        Map<String, PublicKey> out = new HashMap<>();
        try {
            JSONObject o = (JSONObject) new JSONParser().parse(json);
            for (Object ko : (JSONArray) o.get("keys")) {
                JSONObject k = (JSONObject) ko;
                if (!"RSA".equals(k.get("kty")) || !(k.get("kid") instanceof String))
                    continue;
                if (k.get("use") != null && !"sig".equals(k.get("use")))
                    continue;
                if (k.get("alg") != null && !"RS256".equals(k.get("alg")))
                    continue;
                BigInteger n = new BigInteger(1, Base64.getUrlDecoder().decode((String) k.get("n")));
                BigInteger e = new BigInteger(1, Base64.getUrlDecoder().decode((String) k.get("e")));
                if (n.bitLength() < 2048)
                    continue;
                out.put((String) k.get("kid"), KeyFactory.getInstance("RSA").generatePublic(new RSAPublicKeySpec(n, e)));
            }
        } catch (Exception e) {
            return new HashMap<>();
        }
        return out;
    }

    private static JSONObject json(String b64) throws InvalidTokenException {
        try {
            Object o = new JSONParser().parse(new String(Base64.getUrlDecoder().decode(b64), StandardCharsets.UTF_8));
            if (o instanceof JSONObject)
                return (JSONObject) o;
        } catch (Exception ignored) {
        }
        throw new InvalidTokenException("json");
    }

    private static long num(Object o, String name) throws InvalidTokenException {
        if (o instanceof Long)
            return (Long) o;
        throw new InvalidTokenException(name);
    }
}
