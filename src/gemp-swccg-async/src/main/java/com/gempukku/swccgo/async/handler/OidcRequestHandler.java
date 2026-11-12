package com.gempukku.swccgo.async.handler;

import com.gempukku.swccgo.async.HttpProcessingException;
import com.gempukku.swccgo.async.ResponseWriter;
import com.gempukku.swccgo.async.oidc.JwtVerifier;
import com.gempukku.swccgo.async.oidc.OidcClient;
import com.gempukku.swccgo.async.oidc.PendingStarts;
import com.gempukku.swccgo.game.Player;
import com.gempukku.swccgo.hall.HallServer;
import com.gempukku.swccgo.service.AuthService;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.cookie.CookieHeaderNames;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;

import java.lang.reflect.Type;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Sign in to GEMP with swccg-login (OpenID Connect authorization code flow with PKCE, state,
 * nonce and RFC 9207 iss). Only routed when SWCCG_LOGIN_ENABLED=true.
 *
 *   GET  /gemp-swccg-server/oidc/status       {"enabled":true} (the login page shows its button on this)
 *   GET  /gemp-swccg-server/oidc/start        redirect to swccg-login
 *   GET  /gemp-swccg-server/oidc/box          the OIDC request for the in-page sign-in box (JSON);
 *        the box posts it with the name and password (or a passkey) to swccg-login, or appends
 *        it to /authorize with idp=google etc. Same pending state, cookie binding and limits as start.
 *   GET  /gemp-swccg-server/oidc/callback     code -> ID token -> GEMP session by player id (sub);
 *        an error (bad password etc.) goes back to the login page as ?sso_error=<reason>
 *   POST /gemp-swccg-server/oidc/backchannel-logout  OpenID back-channel logout: ends the player's GEMP sessions
 *        ("backchannel" is still accepted as an alias)
 *
 * The player is found by the ID token's sub (the GEMP player id), never by name or email, and
 * GEMP's own ban/deactivation state is checked again here. No rights come from the token.
 */
public class OidcRequestHandler extends SwccgoServerRequestHandler implements UriRequestHandler {
    private static final Logger LOG = LogManager.getLogger(OidcRequestHandler.class);
    static final String BIND_COOKIE = "gempOidcBind";
    private static final long PENDING_TTL_MS = 10 * 60_000L;
    private static final int MAX_PENDING = 20_000;
    private static final int MAX_PENDING_PER_IP = 20;

    /** A new account's first Play session counts as signing up only within this long of its creation. */
    static final int FIRST_SESSION_MAX_AGE_SECONDS = 24 * 60 * 60;
    /** Query parameter the hall reads to show RegisterRequestHandler.SIGNUP_ADOPT_FAILED_NOTICE. */
    static final String ADOPT_FAILED_PARAM = "guest_notice=adopt_failed";

    private final OidcClient _client;
    private final HallServer _hallServer;
    private final PendingStarts<Pending> _pending = new PendingStarts<>(MAX_PENDING, MAX_PENDING_PER_IP, PENDING_TTL_MS);
    private final Map<String, Long> _seenJti = new ConcurrentHashMap<>();

    static final class Pending {
        final String nonce, verifier, bindHash;

        Pending(String nonce, String verifier, String bindHash) {
            this.nonce = nonce;
            this.verifier = verifier;
            this.bindHash = bindHash;
        }
    }

    public OidcRequestHandler(Map<Type, Object> context, OidcClient client) {
        super(context);
        _client = client;
        _hallServer = extractObject(context, HallServer.class);
    }

    @Override
    public void handleRequest(String uri, HttpRequest request, Map<Type, Object> context, ResponseWriter responseWriter, String remoteIp) throws Exception {
        String path = uri;
        int q = path.indexOf('?');
        if (q >= 0)
            path = path.substring(0, q);
        if (path.equals("status") && request.method() == HttpMethod.GET) {
            // The login page shows "Sign in with swccg.com" only when this answers.
            responseWriter.writeJsonResponse("{\"enabled\":true}");
        } else if (path.equals("start") && request.method() == HttpMethod.GET)
            start(responseWriter, remoteIp);
        else if (path.equals("box") && request.method() == HttpMethod.GET)
            box(responseWriter, remoteIp);
        else if (path.equals("callback") && request.method() == HttpMethod.GET)
            callback(request, responseWriter, remoteIp);
        else if ((path.equals("backchannel-logout") || path.equals("backchannel")) && request.method() == HttpMethod.POST)
            backchannel(request, responseWriter);
        else
            throw new HttpProcessingException(404);
    }

    private void start(ResponseWriter rw, String remoteIp) throws Exception {
        String state = _client.random(), nonce = _client.random(), verifier = _client.random(), bind = _client.random();
        String url;
        try {
            url = _client.authorizeUrl(state, nonce, verifier);
        } catch (Exception e) {
            LOG.warn("swccg-login discovery failed: " + e.getClass().getSimpleName());
            throw new HttpProcessingException(503);
        }
        if (!_pending.put(state, new Pending(nonce, verifier, sha256(bind)), remoteIp, System.currentTimeMillis()))
            throw new HttpProcessingException(429);
        Map<String, String> h = new HashMap<>();
        h.put("Location", url);
        h.put(HttpHeaderNames.SET_COOKIE.toString(), encodeProtectedCookie(BIND_COOKIE, bind, 600, CookieHeaderNames.SameSite.Lax));
        h.put("Cache-Control", "no-store");
        rw.writeError(302, h);
    }

    private void box(ResponseWriter rw, String remoteIp) throws Exception {
        String state = _client.random(), nonce = _client.random(), verifier = _client.random(), bind = _client.random();
        String json = _client.boxParams(state, nonce, verifier).toJSONString();
        if (!_pending.put(state, new Pending(nonce, verifier, sha256(bind)), remoteIp, System.currentTimeMillis()))
            throw new HttpProcessingException(429);
        Map<String, String> h = new HashMap<>();
        h.put(HttpHeaderNames.CONTENT_TYPE.toString(), "application/json; charset=UTF-8");
        h.put(HttpHeaderNames.SET_COOKIE.toString(), encodeProtectedCookie(BIND_COOKIE, bind, 600, CookieHeaderNames.SameSite.Lax));
        h.put("Cache-Control", "no-store");
        rw.writeByteResponse(json.getBytes(StandardCharsets.UTF_8), h);
    }

    /** Reasons the login page knows how to show; anything else is "failed". */
    static final java.util.Set<String> SSO_REASONS = java.util.Set.of("bad_credentials", "rate_limited", "unavailable",
            "blocked", "reset_required", "passkey_failed", "expired", "name_invalid", "name_taken", "weak_password",
            "signup_limited", "signup_closed", "password_mismatch");

    static String ssoErrorReason(String error, String description) {
        if (description != null && SSO_REASONS.contains(description))
            return description;
        if ("access_denied".equals(error))
            return "cancelled";
        return "failed";
    }

    private void callback(HttpRequest request, ResponseWriter rw, String remoteIp) throws Exception {
        Map<String, List<String>> params = new QueryStringDecoder(request.uri()).parameters();
        String state = first(params, "state");
        Pending p = _pending.take(state, System.currentTimeMillis());
        String bind = getCookieValue(request, BIND_COOKIE);
        if (p == null || bind == null
                || !MessageDigest.isEqual(p.bindHash.getBytes(StandardCharsets.US_ASCII), sha256(bind).getBytes(StandardCharsets.US_ASCII)))
            throw new HttpProcessingException(400);
        String iss = first(params, "iss");
        if (iss == null || !iss.equals(_client.config().issuer))
            throw new HttpProcessingException(400);
        if (first(params, "error") != null) {
            redirect(rw, "/gemp-swccg/?sso_error=" + ssoErrorReason(first(params, "error"), first(params, "error_description")), null);
            return;
        }
        String code = first(params, "code");
        if (code == null || code.length() > 512)
            throw new HttpProcessingException(400);
        int playerId;
        try {
            String idToken = _client.exchange(code, p.verifier);
            JSONObject claims = _client.verifier().verify(idToken, System.currentTimeMillis() / 1000, null);
            if (!p.nonce.equals(claims.get("nonce")))
                throw new JwtVerifier.InvalidTokenException("nonce");
            playerId = JwtVerifier.playerId(claims);
        } catch (JwtVerifier.InvalidTokenException e) {
            LOG.warn("swccg-login: ID token refused (" + e.getMessage() + ")");
            throw new HttpProcessingException(403);
        } catch (Exception e) {
            LOG.warn("swccg-login: token exchange failed: " + e.getClass().getSimpleName());
            throw new HttpProcessingException(503);
        }
        AuthService.AuthResult status = _authService.accountStatus(playerId);
        if (status.getStatus() != AuthService.Status.OK || status.getPlayer() == null || Player.isBot(status.getPlayer().getName()))
            throw new HttpProcessingException(403);
        completeSignIn(request, rw, remoteIp, status.getPlayer().getName(), playerId);
    }

    /** The verified player signs in: a GEMP session, a new account's guest games, and back to Play. */
    void completeSignIn(HttpRequest request, ResponseWriter rw, String remoteIp, String name, int playerId) throws Exception {
        // #264 R6/AC3 (Bill 2026-11-11, t135u): a membership just made through the sign-in service (Google, Discord
        // or a password signup in the sign-in box) takes this browser's guest Play now games on its first Play
        // session, as a classic signup does. Only that first session of a new account; an existing account
        // signing in never takes them. Only this browser's guest cookie's games move; never another account's.
        boolean firstSession = getGuestId(request) != null && _hallServer != null
                && _playerDao.claimFirstPlaySession(name, remoteIp, FIRST_SESSION_MAX_AGE_SECONDS);
        Map<String, String> h = logUserReturningHeaders(remoteIp, name);
        String after = _client.config().afterLoginUrl;
        if (firstSession) {
            h = adoptGuestGamesSafely(_hallServer, request, name, h, RegisterRequestHandler.SIGNUP_ADOPT_FAILED_NOTICE);
            if (h.containsKey(GUEST_NOTICE_HEADER)) {   // a redirect can't carry the notice header: the hall shows it
                h = new HashMap<>(h);
                h.remove(GUEST_NOTICE_HEADER);
                after = after + (after.contains("?") ? "&" : "?") + ADOPT_FAILED_PARAM;
            }
            LOG.info("swccg-login: first Play session of new player id " + playerId + "; guest games offered");
        }
        LOG.info("swccg-login: signed in player id " + playerId);
        redirect(rw, after, h);
    }

    private void backchannel(HttpRequest request, ResponseWriter rw) throws Exception {
        HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(request);
        try {
            String token = getFormParameterSafely(decoder, "logout_token");
            long now = System.currentTimeMillis() / 1000;
            JSONObject c = _client.verifier().verify(token, now, "logout+jwt");
            Object events = c.get("events");
            if (!(events instanceof JSONObject) || !((JSONObject) events).containsKey("http://schemas.openid.net/event/backchannel-logout"))
                throw new JwtVerifier.InvalidTokenException("events");
            if (c.containsKey("nonce"))
                throw new JwtVerifier.InvalidTokenException("nonce");
            Object jti = c.get("jti");
            if (!(jti instanceof String) || _seenJti.putIfAbsent((String) jti, now) != null)
                throw new JwtVerifier.InvalidTokenException("jti");
            _seenJti.entrySet().removeIf(e -> e.getValue() < now - 2 * JwtVerifier.MAX_AGE_SECONDS);
            int playerId = JwtVerifier.playerId(c);
            Player player = _playerDao.getPlayer(playerId);
            if (player != null)
                _loggedUserHolder.forceLogoutUser(player.getName());
            LOG.info("swccg-login: back-channel logout for player id " + playerId);
            Map<String, String> h = new HashMap<>();
            h.put("Cache-Control", "no-store");
            rw.writeError(200, h);
        } catch (JwtVerifier.InvalidTokenException e) {
            LOG.warn("swccg-login: logout token refused (" + e.getMessage() + ")");
            throw new HttpProcessingException(400);
        } finally {
            decoder.destroy();
        }
    }

    private static void redirect(ResponseWriter rw, String location, Map<String, String> extra) {
        Map<String, String> h = new HashMap<>();
        if (extra != null)
            h.putAll(extra);
        h.put("Location", location);
        h.put("Cache-Control", "no-store");
        rw.writeError(302, h);
    }

    private static String first(Map<String, List<String>> p, String k) {
        List<String> v = p.get(k);
        return v == null || v.size() != 1 ? null : v.get(0);
    }

    private static String sha256(String s) {
        try {
            return java.util.HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(s.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
