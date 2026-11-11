package com.gempukku.swccgo.async.oidc;

import com.gempukku.swccgo.async.internal.InternalAuthKeysV2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * Asks swccg-login whether Play's classic signup may take a name (swccg-guild/work#261): the name must not be
 * reserved, on its 90-day hold after a rename or close, or already issued by the sign-in service. Signed
 * GEMP-AUTH-V2 with the revoke key (key id "gemp") to {@code /internal/v1/name-check} on the same internal listener
 * as {@link SsoRevoker}, so it needs no new setting. Fails closed: anything but a signed "available" refuses.
 */
public final class SiteNameCheck {
    private static final Logger LOG = LogManager.getLogger(SiteNameCheck.class);
    static final String KEY_ID = "gemp";
    static final String PATH = "/internal/v1/name-check";

    public enum Answer { AVAILABLE, RESERVED, HELD, TAKEN, UNAVAILABLE }

    /** One HTTP exchange; replaced in tests. */
    public interface Transport {
        Response post(URI uri, Map<String, String> headers, byte[] body) throws Exception;
    }

    public record Response(int status, String signature, byte[] body) {
    }

    private final URI _uri;
    private final InternalAuthKeysV2 _keys;
    private final Transport _transport;
    private final SecureRandom _random = new SecureRandom();

    public SiteNameCheck(URI uri, String secret, Transport transport) {
        _uri = uri;
        _keys = new InternalAuthKeysV2(Map.of(KEY_ID, List.of(secret)));
        _transport = transport;
    }

    /** From SWCCG_LOGIN_REVOKE_URL / SWCCG_LOGIN_REVOKE_SECRET; null when the sign-in service is not set up. */
    public static SiteNameCheck fromEnvironment() {
        String revoke = System.getenv("SWCCG_LOGIN_REVOKE_URL");
        String secret = System.getenv("SWCCG_LOGIN_REVOKE_SECRET");
        if (revoke == null || revoke.isBlank() || secret == null || secret.isBlank())
            return null;
        URI uri = URI.create(revoke).resolve(PATH);
        HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(3))
                .followRedirects(HttpClient.Redirect.NEVER).build();
        return new SiteNameCheck(uri, secret, (u, headers, body) -> {
            HttpRequest.Builder b = HttpRequest.newBuilder(InternalUrl.reachable(u)).timeout(Duration.ofSeconds(5))
                    .POST(HttpRequest.BodyPublishers.ofByteArray(body));
            headers.forEach(b::header);
            HttpResponse<byte[]> r = http.send(b.build(), HttpResponse.BodyHandlers.ofByteArray());
            return new Response(r.statusCode(), r.headers().firstValue("X-Gemp-Signature").orElse(null), r.body());
        });
    }

    public Answer check(String name) {
        try {
            JSONObject in = new JSONObject();
            in.put("name", name);
            byte[] body = in.toJSONString().getBytes(StandardCharsets.UTF_8);
            String ts = Long.toString(System.currentTimeMillis() / 1000);
            byte[] n = new byte[24];
            _random.nextBytes(n);
            String nonce = Base64.getUrlEncoder().withoutPadding().encodeToString(n);
            String sig = _keys.sign(KEY_ID, InternalAuthKeysV2.requestCanonical(KEY_ID, ts, nonce, "POST", _uri.getPath(), body));
            Response r = _transport.post(_uri, Map.of("Content-Type", "application/json", "X-Gemp-Key-Id", KEY_ID,
                    "X-Gemp-Timestamp", ts, "X-Gemp-Nonce", nonce, "X-Gemp-Signature", sig), body);
            byte[] rb = r.body() == null ? new byte[0] : r.body();
            if (r.signature() == null || !_keys.verify(KEY_ID, InternalAuthKeysV2.responseCanonical(KEY_ID, nonce, r.status(), rb), r.signature())) {
                LOG.warn("swccg-login name check: unsigned or badly signed answer (HTTP " + r.status() + ")");
                return Answer.UNAVAILABLE;
            }
            if (r.status() != 200)
                return Answer.UNAVAILABLE;
            Object parsed = new JSONParser().parse(new String(rb, StandardCharsets.UTF_8));
            if (!(parsed instanceof JSONObject) || !Boolean.TRUE.equals(((JSONObject) parsed).get("ok")))
                return Answer.UNAVAILABLE;
            JSONObject out = (JSONObject) parsed;
            if (Boolean.TRUE.equals(out.get("available")))
                return Answer.AVAILABLE;
            Object reason = out.get("reason");
            if ("reserved".equals(reason))
                return Answer.RESERVED;
            if ("held".equals(reason))
                return Answer.HELD;
            if ("taken".equals(reason))
                return Answer.TAKEN;
            return Answer.UNAVAILABLE;
        } catch (Exception e) {
            LOG.warn("swccg-login name check failed: " + e.getClass().getSimpleName());
            return Answer.UNAVAILABLE;
        }
    }
}
