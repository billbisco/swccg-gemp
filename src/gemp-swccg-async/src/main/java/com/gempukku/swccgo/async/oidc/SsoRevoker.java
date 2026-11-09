package com.gempukku.swccgo.async.oidc;

import com.gempukku.swccgo.async.internal.InternalAuthKeysV2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
import java.util.function.IntConsumer;

/**
 * Tells swccg-login to end every sign-in session of a player that was just banned, deactivated or
 * reset (signed GEMP-AUTH-V2, key id "gemp"). Best effort and asynchronous: swccg-login also
 * checks the account with GEMP before every sign-in, so a lost call only delays the cut-off.
 *
 * SWCCG_LOGIN_REVOKE_URL=http://swccg-login-test:8091/internal/v1/revoke
 * SWCCG_LOGIN_REVOKE_SECRET=... (at least 32 bytes; from a root-only env file)
 */
public final class SsoRevoker implements IntConsumer {
    private static final Logger LOG = LogManager.getLogger(SsoRevoker.class);
    private static final String KEY_ID = "gemp";
    private final String _url;
    private final InternalAuthKeysV2 _keys;
    private final HttpClient _http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(3))
            .followRedirects(HttpClient.Redirect.NEVER).build();
    private final SecureRandom _random = new SecureRandom();

    public SsoRevoker(String url, String secret) {
        _url = url;
        _keys = new InternalAuthKeysV2(Map.of(KEY_ID, List.of(secret)));
    }

    public static SsoRevoker fromEnvironment() {
        String url = System.getenv("SWCCG_LOGIN_REVOKE_URL");
        String secret = System.getenv("SWCCG_LOGIN_REVOKE_SECRET");
        if (url == null || url.isBlank() || secret == null || secret.isBlank())
            return null;
        return new SsoRevoker(url, secret);
    }

    @Override
    public void accept(int playerId) {
        try {
            URI uri = URI.create(_url);
            byte[] body = ("{\"gemp_id\":" + playerId + ",\"reason\":\"gemp_admin\"}").getBytes(StandardCharsets.UTF_8);
            String ts = Long.toString(System.currentTimeMillis() / 1000);
            byte[] n = new byte[24];
            _random.nextBytes(n);
            String nonce = Base64.getUrlEncoder().withoutPadding().encodeToString(n);
            String sig = _keys.sign(KEY_ID, InternalAuthKeysV2.requestCanonical(KEY_ID, ts, nonce, "POST", uri.getPath(), body));
            HttpRequest req = HttpRequest.newBuilder(uri).timeout(Duration.ofSeconds(5))
                    .header("Content-Type", "application/json")
                    .header("X-Gemp-Key-Id", KEY_ID).header("X-Gemp-Timestamp", ts)
                    .header("X-Gemp-Nonce", nonce).header("X-Gemp-Signature", sig)
                    .POST(HttpRequest.BodyPublishers.ofByteArray(body)).build();
            _http.sendAsync(req, HttpResponse.BodyHandlers.discarding()).whenComplete((r, e) -> {
                if (e != null || r.statusCode() != 200)
                    LOG.warn("swccg-login revoke for player id " + playerId + " failed: " + (e != null ? e.getClass().getSimpleName() : r.statusCode()));
            });
        } catch (Exception e) {
            LOG.warn("swccg-login revoke for player id " + playerId + " failed: " + e.getClass().getSimpleName());
        }
    }
}
