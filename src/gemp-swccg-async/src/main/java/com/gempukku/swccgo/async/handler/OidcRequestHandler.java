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
 *   POST /gemp-swccg-server/oidc/logout       Sign out (#504): ends this Play session first, then 303 to
 *        swccg-login's end_session with this session's ID token as id_token_hint (one click, no page).
 *        Without a kept token (classic sign-in, after a restart) it still ends the Play session and goes
 *        to end_session without a hint, which asks. Same-origin POST only (checked by the root handler).
 *   POST /gemp-swccg-server/oidc/backchannel-logout  OpenID back-channel logout: a token with a sid ends only the
 *        Play sessions signed in from that sign-in session (this device, #506); a token without a sid ends all of
 *        the player's Play sessions (block, reset, Sign out everywhere). "backchannel" is still accepted as an alias.
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
        /** #518: a silent (prompt=none) restore after Play forgot the session; failure goes back quietly. */
        final boolean silent;
        /** #518: the Play page to return to (same-origin path under Play's front page), or null. */
        final String returnTo;

        Pending(String nonce, String verifier, String bindHash) {
            this(nonce, verifier, bindHash, false, null);
        }

        Pending(String nonce, String verifier, String bindHash, boolean silent, String returnTo) {
            this.nonce = nonce;
            this.verifier = verifier;
            this.bindHash = bindHash;
            this.silent = silent;
            this.returnTo = returnTo;
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
            start(request, responseWriter, remoteIp);
        else if (path.equals("box") && request.method() == HttpMethod.GET)
            box(responseWriter, remoteIp);
        else if (path.equals("callback") && request.method() == HttpMethod.GET)
            callback(request, responseWriter, remoteIp);
        else if (path.equals("logout") && request.method() == HttpMethod.POST)
            logout(request, responseWriter);
        else if ((path.equals("backchannel-logout") || path.equals("backchannel")) && request.method() == HttpMethod.POST)
            backchannel(request, responseWriter);
        else
            throw new HttpProcessingException(404);
    }

    private void start(HttpRequest request, ResponseWriter rw, String remoteIp) throws Exception {
        Map<String, List<String>> params = new QueryStringDecoder(request.uri()).parameters();
        boolean silent = "none".equals(first(params, "prompt"));
        String returnTo = safeReturn(first(params, "return"), playFrontPage(_client.config().redirectUri));
        String state = _client.random(), nonce = _client.random(), verifier = _client.random(), bind = _client.random();
        String url;
        try {
            url = _client.authorizeUrl(state, nonce, verifier) + (silent ? "&prompt=none" : "");
        } catch (Exception e) {
            LOG.warn("swccg-login discovery failed: " + e.getClass().getSimpleName());
            throw new HttpProcessingException(503);
        }
        if (!_pending.put(state, new Pending(nonce, verifier, sha256(bind), silent, returnTo), remoteIp, System.currentTimeMillis()))
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

    /**
     * #630: one line per sign-in callback, so swccg-signin-trace can follow a Play sign-in: the result, the sign-in's
     * tracking id (swccg_tid, from the verified ID token) and the state fingerprint (sfp, the first 16 hex of SHA-256
     * of the state, as swccg-login logs it), for a refusal before the ID token. Ids only: no name, email, code or token.
     */
    static String callbackLine(String result, String tid, String state, Integer playerId) {
        String r = result != null && result.matches("[a-z0-9_:]{1,64}") ? result : "other";
        String t = tid != null && tid.matches("[0-9a-f]{16,64}") ? tid : "-";
        String f = state != null && !state.isEmpty() ? sha256(state).substring(0, 16) : "-";
        return "sso-callback result=" + r + " tid=" + t + " sfp=" + f + " player=" + (playerId != null ? playerId : "-");
    }

    private void callback(HttpRequest request, ResponseWriter rw, String remoteIp) throws Exception {
        Map<String, List<String>> params = new QueryStringDecoder(request.uri()).parameters();
        String state = first(params, "state");
        Pending p = _pending.take(state, System.currentTimeMillis());
        String bind = getCookieValue(request, BIND_COOKIE);
        if (p == null || bind == null
                || !MessageDigest.isEqual(p.bindHash.getBytes(StandardCharsets.US_ASCII), sha256(bind).getBytes(StandardCharsets.US_ASCII))) {
            LOG.info(callbackLine("bad_state", null, state, null));
            throw new HttpProcessingException(400);
        }
        String iss = first(params, "iss");
        if (iss == null || !iss.equals(_client.config().issuer)) {
            LOG.info(callbackLine("iss_mismatch", null, state, null));
            throw new HttpProcessingException(400);
        }
        if (first(params, "error") != null) {
            String err = first(params, "error");
            LOG.info(callbackLine((p.silent ? "silent:" : "error:") + (err.matches("[a-z_]{1,40}") ? err : "other"), null, state, null));
            if (p.silent) {   // #518: not signed in at swccg.com either; back to the page, quietly signed out
                redirect(rw, p.returnTo != null ? p.returnTo : playFrontPage(_client.config().redirectUri), null);
                return;
            }
            redirect(rw, "/gemp-swccg/?sso_error=" + ssoErrorReason(err, first(params, "error_description")), null);
            return;
        }
        String code = first(params, "code");
        if (code == null || code.length() > 512) {
            LOG.info(callbackLine("bad_code", null, state, null));
            throw new HttpProcessingException(400);
        }
        int playerId;
        String idToken, sid, tid;
        try {
            idToken = _client.exchange(code, p.verifier);
            JSONObject claims = _client.verifier().verify(idToken, System.currentTimeMillis() / 1000, null);
            if (!p.nonce.equals(claims.get("nonce")))
                throw new JwtVerifier.InvalidTokenException("nonce");
            playerId = JwtVerifier.playerId(claims);
            sid = claims.get("sid") instanceof String ? (String) claims.get("sid") : null;
            tid = claims.get("swccg_tid") instanceof String ? (String) claims.get("swccg_tid") : null;
        } catch (JwtVerifier.InvalidTokenException e) {
            LOG.warn("swccg-login: ID token refused (" + e.getMessage() + ")");
            LOG.info(callbackLine("id_token", null, state, null));
            throw new HttpProcessingException(403);
        } catch (Exception e) {
            LOG.warn("swccg-login: token exchange failed: " + e.getClass().getSimpleName());
            LOG.info(callbackLine("token_exchange", null, state, null));
            throw new HttpProcessingException(503);
        }
        AuthService.AuthResult status = _authService.accountStatus(playerId);
        if (status.getStatus() != AuthService.Status.OK || status.getPlayer() == null || Player.isBot(status.getPlayer().getName())) {
            LOG.info(callbackLine("account:" + String.valueOf(status.getStatus()).toLowerCase(), tid, state, playerId));
            throw new HttpProcessingException(403);
        }
        LOG.info(callbackLine("ok", tid, state, playerId));
        completeSignIn(request, rw, remoteIp, status.getPlayer().getName(), playerId, idToken, sid, p.returnTo);
    }

    /** The verified player signs in: a GEMP session, a new account's guest games, and back to Play. */
    void completeSignIn(HttpRequest request, ResponseWriter rw, String remoteIp, String name, int playerId) throws Exception {
        completeSignIn(request, rw, remoteIp, name, playerId, null, null, null);
    }

    void completeSignIn(HttpRequest request, ResponseWriter rw, String remoteIp, String name, int playerId, String idToken, String sid,
                        String returnTo) throws Exception {
        // #264 R6/AC3 (Bill 2026-11-11, t135u): a membership just made through the sign-in service (Google, Discord
        // or a password signup in the sign-in box) takes this browser's guest Play now games on its first Play
        // session, as a classic signup does. Only that first session of a new account; an existing account
        // signing in never takes them. Only this browser's guest cookie's games move; never another account's.
        boolean firstSession = getGuestId(request) != null && _hallServer != null
                && _playerDao.claimFirstPlaySession(name, remoteIp, FIRST_SESSION_MAX_AGE_SECONDS);
        Map<String, String> h = logUserReturningHeaders(remoteIp, name, idToken, sid);
        String after = returnTo != null ? returnTo : _client.config().afterLoginUrl;
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
            Object sid = c.get("sid");
            int ended = 0;
            if (sid instanceof String && !((String) sid).isEmpty()) {
                // This device only (#506): the Play sessions signed in from that sign-in session.
                ended = _loggedUserHolder.forceLogoutSid((String) sid);
            } else {
                // Account-wide (block, reset, Sign out everywhere): every Play session of the player.
                Player player = _playerDao.getPlayer(playerId);
                if (player != null)
                    ended = _loggedUserHolder.forceLogoutUser(player.getName());
            }
            LOG.info("swccg-login: back-channel logout for player id " + playerId + " (" + (sid instanceof String ? "one sign-in session" : "account-wide")
                    + "), Play sessions ended: " + ended);
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

    /** Sign out (#504): end this Play session first, then hand the browser to end_session with the hint. */
    private void logout(HttpRequest request, ResponseWriter rw) throws Exception {
        String sessionId = getCookieValue(request, "loggedUser");
        String idToken = _loggedUserHolder.getIdToken(sessionId);
        boolean ended = _loggedUserHolder.logoutSession(sessionId);
        String url = endSessionUrl(idToken);
        Map<String, String> h = new HashMap<>();
        h.put("Location", url);
        h.put(HttpHeaderNames.SET_COOKIE.toString(), encodeProtectedCookie("loggedUser", "", 0, CookieHeaderNames.SameSite.Lax));
        h.put("Cache-Control", "no-store");
        LOG.info("swccg-login: Play sign-out (session ended: " + ended + ", hint: " + (idToken != null) + ")");
        rw.writeError(303, h);
    }

    /** end_session with client_id, post_logout_redirect_uri (Play's front page), state and, when kept, the hint. */
    String endSessionUrl(String idToken) {
        String endpoint = null;
        try {
            Object e = _client.discovery().get("end_session_endpoint");
            if (e instanceof String && ((String) e).startsWith("https://"))
                endpoint = (String) e;
        } catch (Exception ignored) {
            // discovery down: the issuer's standard path still works
        }
        if (endpoint == null)
            endpoint = _client.config().issuer + "/end_session";
        StringBuilder u = new StringBuilder(endpoint).append("?client_id=").append(enc(_client.config().clientId))
                .append("&post_logout_redirect_uri=").append(enc(playFrontPage(_client.config().redirectUri)))
                .append("&state=").append(enc(_client.random()));
        if (idToken != null)
            u.append("&id_token_hint=").append(enc(idToken));
        return u.toString();
    }

    /** https://swccg.com/play/ from https://swccg.com/play/gemp-swccg-server/oidc/callback (older hosts: /gemp-swccg/). */
    static String playFrontPage(String redirectUri) {
        String tail = "gemp-swccg-server/oidc/callback";
        if (redirectUri.endsWith(tail) && redirectUri.length() > tail.length() + 8) {
            String root = redirectUri.substring(0, redirectUri.length() - tail.length());
            if (!root.endsWith("://") && !root.matches("https://[^/]+/"))
                return root;
            return root + "gemp-swccg/";
        }
        int slash = redirectUri.indexOf('/', 8);
        return (slash > 0 ? redirectUri.substring(0, slash) : redirectUri) + "/gemp-swccg/";
    }

    /**
     * A return path for after sign-in: only a path under Play's own front page on this origin
     * (no scheme, no "//", no "..", no backslash, short), else null. Prevents open redirects.
     */
    static String safeReturn(String ret, String frontPage) {
        if (ret == null || ret.length() > 300)
            return null;
        int slash = frontPage.indexOf('/', 8);
        String rootPath = slash > 0 ? frontPage.substring(slash) : "/";
        if (!ret.startsWith(rootPath) || ret.startsWith("//") || ret.contains("..") || ret.contains("\\")
                || ret.contains("//") || !ret.matches("[A-Za-z0-9._~!&()*+=:@%/?,;$'-]*"))
            return null;
        return ret;
    }

    private static String enc(String s) {
        return java.net.URLEncoder.encode(s, StandardCharsets.UTF_8);
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
