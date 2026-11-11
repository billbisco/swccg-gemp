package com.gempukku.swccgo.async.internal;

import com.gempukku.swccgo.db.GempSettingDAO;
import com.gempukku.swccgo.db.LoginInvalidException;
import com.gempukku.swccgo.db.RegisterNotAllowedException;
import com.gempukku.swccgo.game.Player;
import com.gempukku.swccgo.service.AuthService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * GEMP-AUTH-V2 for swccg-login (docs/swccg-login-auth-api.md): POST /internal/v2/verify,
 * /internal/v2/register, /internal/v2/players, /internal/v2/status and, only when switched on
 * (GEMP_INTERNAL_AUTH_V2_SET_PASSWORD=true), /internal/v2/password, /internal/v2/close (a
 * member's self-serve close from swccg-login's account page: deactivate, never delete), and
 * /internal/v2/rename (a rename from the account page; game history keeps the name at the table).
 *
 * Same order of checks as {@link WikiAuthService}: path, method, size, key id + signature,
 * timestamp skew, nonce replay, rate limits, then GEMP. Answers never carry an admin flag or any
 * other rights: swccg-login must not infer rights from GEMP. Never logs bodies or passwords.
 */
public class SsoAuthService {
    public static final String VERIFY = "/internal/v2/verify";
    public static final String REGISTER = "/internal/v2/register";
    public static final String PLAYERS = "/internal/v2/players";
    public static final String STATUS = "/internal/v2/status";
    /** Set a new password by player id (swccg-login's self-service reset). Off unless switched on. */
    public static final String PASSWORD = "/internal/v2/password";
    /** Self-serve close by player id (#261): deactivates the player and ends their GEMP sessions. */
    public static final String CLOSE = "/internal/v2/close";
    /** Rename by player id (#261, #264): refused while the player is at a table; ends their GEMP sessions. */
    public static final String RENAME = "/internal/v2/rename";
    public static final Set<String> PATHS = Set.of(VERIFY, REGISTER, PLAYERS, STATUS, PASSWORD, CLOSE, RENAME);

    private static final Pattern NONCE = Pattern.compile("^[A-Za-z0-9_-]{16,64}$");
    private static final Pattern IP = Pattern.compile("^[0-9A-Fa-f:.]{2,45}$");
    private static final Logger LOG = LogManager.getLogger(SsoAuthService.class);

    private final AuthService _authService;
    private final GempSettingDAO _settings;
    private final InternalAuthKeysV2 _keys;
    private final NonceCache _nonces = new NonceCache(2 * WikiAuthService.MAX_SKEW_SECONDS * 1000, 100_000);
    final KeyedRateLimiter _userFailures = new KeyedRateLimiter(10, 15 * 60_000L);
    final KeyedRateLimiter _ipAttempts = new KeyedRateLimiter(30, 5 * 60_000L);
    final KeyedRateLimiter _signupsPerIp = new KeyedRateLimiter(5, 60 * 60_000L);
    final KeyedRateLimiter _global = new KeyedRateLimiter(1200, 60_000L);
    final KeyedRateLimiter _passwordSets = new KeyedRateLimiter(5, 60 * 60_000L);
    final KeyedRateLimiter _renames = new KeyedRateLimiter(5, 60 * 60_000L);
    private final boolean _setPassword;
    /** Ends every GEMP session (cookie) of a player by name; set when the async server has a LoggedUserHolder. */
    private volatile java.util.function.Consumer<String> _sessionEnder;
    /** True while a player (by name) is at a table, waiting or playing; a rename is refused then. */
    private volatile java.util.function.Predicate<String> _atTable;
    /** Run after a rename, to drop cached copies of the old name (player, league and tournament caches). */
    private volatile Runnable _afterRename;

    public SsoAuthService(AuthService authService, GempSettingDAO settings, InternalAuthKeysV2 keys) {
        this(authService, settings, keys, false);
    }

    public SsoAuthService(AuthService authService, GempSettingDAO settings, InternalAuthKeysV2 keys, boolean setPassword) {
        _authService = authService;
        _settings = settings;
        _keys = keys;
        _setPassword = setPassword;
    }

    /** With {@code "end_sessions": true}, a password set also ends the player's GEMP sessions (classic and SSO). */
    public void setSessionEnder(java.util.function.Consumer<String> ender) {
        _sessionEnder = ender;
    }

    /** Wires the rename call to the hall (at-table check) and the caches to clear after a rename. */
    public void setRenameHooks(java.util.function.Predicate<String> atTable, Runnable afterRename) {
        _atTable = atTable;
        _afterRename = afterRename;
    }

    public WikiAuthService.Result handle(String method, String path, Map<String, String> headers, byte[] body, long nowMillis) {
        String nonce = headers.get("x-gemp-nonce");
        String keyId = headers.get("x-gemp-key-id");
        if (!PATHS.contains(path) || (PASSWORD.equals(path) && !_setPassword))
            return result(404, error("not_found"), null, null);
        if (!"POST".equals(method))
            return result(405, error("method_not_allowed"), null, null);
        if (body == null || body.length > WikiAuthService.MAX_BODY_BYTES)
            return result(413, error("body_too_large"), null, null);
        String timestamp = headers.get("x-gemp-timestamp");
        if (!_keys.hasKey(keyId) || nonce == null || !NONCE.matcher(nonce).matches()
                || timestamp == null || !timestamp.matches("^[0-9]{1,12}$"))
            return result(401, error("bad_signature"), null, null);
        if (!_keys.verify(keyId, InternalAuthKeysV2.requestCanonical(keyId, timestamp, nonce, method, path, body),
                headers.get("x-gemp-signature"))) {
            LOG.warn("sso-auth: rejected request with bad signature");
            return result(401, error("bad_signature"), null, null);
        }
        if (Math.abs(nowMillis / 1000 - Long.parseLong(timestamp)) > WikiAuthService.MAX_SKEW_SECONDS)
            return result(401, error("stale_timestamp"), keyId, nonce);
        if (!_nonces.firstUse(keyId + ":" + nonce, nowMillis))
            return result(401, error("replayed_nonce"), keyId, nonce);

        JSONObject json;
        try {
            Object parsed = new JSONParser().parse(new String(body, StandardCharsets.UTF_8));
            if (!(parsed instanceof JSONObject))
                return result(400, error("bad_request"), keyId, nonce);
            json = (JSONObject) parsed;
        } catch (Exception e) {
            return result(400, error("bad_request"), keyId, nonce);
        }
        long retry = _global.retryAfterSeconds("all", nowMillis);
        if (retry > 0)
            return throttled(retry, keyId, nonce);
        _global.record("all", nowMillis);
        try {
            switch (path) {
                case VERIFY: return verify(json, keyId, nonce, nowMillis);
                case REGISTER: return register(json, true, keyId, nonce, nowMillis);
                case PLAYERS: return register(json, false, keyId, nonce, nowMillis);
                case PASSWORD: return setPassword(json, keyId, nonce, nowMillis);
                case CLOSE: return close(json, keyId, nonce);
                case RENAME: return rename(json, keyId, nonce, nowMillis);
                default: return status(json, keyId, nonce);
            }
        } catch (Exception e) {
            LOG.error("sso-auth: " + path + " failed: " + e.getClass().getSimpleName());
            return result(503, error("unavailable"), keyId, nonce);
        }
    }

    private WikiAuthService.Result verify(JSONObject json, String keyId, String nonce, long now) throws Exception {
        String username = asString(json.get("username"));
        String password = asString(json.get("password"));
        String clientIp = asString(json.get("client_ip"));
        if (username == null || password == null || username.isEmpty() || username.length() > 64
                || password.isEmpty() || password.length() > 1024 || !validIp(clientIp))
            return result(400, error("bad_request"), keyId, nonce);
        String userKey = username.toLowerCase(Locale.ROOT);
        long retry = Math.max(_ipAttempts.retryAfterSeconds(clientIp, now), _userFailures.retryAfterSeconds(userKey, now));
        if (retry > 0)
            return throttled(retry, keyId, nonce);
        _ipAttempts.record(clientIp, now);
        AuthService.AuthResult auth = Player.isBot(username) ? null : _authService.verifyCredentials(username, password, clientIp);
        AuthService.Status status = auth == null ? AuthService.Status.INVALID : auth.getStatus();
        String outcome = WikiAuthService.reasonFor(status);
        JSONObject response;
        if (status == AuthService.Status.OK && auth.getPlayer() != null) {
            response = okUser(auth.getPlayer());
            _userFailures.clear(userKey);
        } else {
            if ("ok".equals(outcome))
                outcome = "invalid_credentials";
            if ("invalid_credentials".equals(outcome))
                _userFailures.record(userKey, now);
            response = error(outcome);
            addUntil(response, auth);
        }
        LOG.info("sso-auth: verify user=" + safe(username) + " ip=" + clientIp + " key=" + keyId + " result=" + outcome);
        return result(200, response, keyId, nonce);
    }

    private WikiAuthService.Result register(JSONObject json, boolean withPassword, String keyId, String nonce, long now) throws Exception {
        String username = asString(json.get("username"));
        String password = withPassword ? asString(json.get("password")) : null;
        String clientIp = asString(json.get("client_ip"));
        if (username == null || username.length() > 64 || !validIp(clientIp)
                || (withPassword && (password == null || password.isEmpty() || password.length() > 1024)))
            return result(400, error("bad_request"), keyId, nonce);
        if (_settings != null && !_settings.newAccountRegistrationEnabled())
            return result(200, error("signup_closed"), keyId, nonce);
        if (_signupsPerIp.retryAfterSeconds(clientIp, now) > 0)
            return result(200, error("signup_limited"), keyId, nonce);
        if (!com.gempukku.swccgo.service.PlayerNames.isWellFormed(username) || Player.isBot(username))
            return result(200, error("name_invalid"), keyId, nonce);
        Player p;
        try {
            p = withPassword ? _authService.registerAndGet(username, password, clientIp)
                    : _authService.createPlayerWithoutPassword(username, clientIp);
        } catch (AuthService.WeakPasswordException e) {
            return result(200, error("weak_password"), keyId, nonce);
        } catch (LoginInvalidException e) {
            // The format was checked above, so the name is taken (names are unique ignoring case).
            return result(200, error("name_taken"), keyId, nonce);
        } catch (RegisterNotAllowedException e) {
            return result(200, error("signup_limited"), keyId, nonce);
        }
        if (p == null)
            return result(200, error("name_invalid"), keyId, nonce); // reserved prefix
        _signupsPerIp.record(clientIp, now);
        LOG.info("sso-auth: " + (withPassword ? "register" : "players") + " user=" + safe(username) + " ip=" + clientIp + " key=" + keyId);
        return result(200, okUser(p), keyId, nonce);
    }

    private WikiAuthService.Result setPassword(JSONObject json, String keyId, String nonce, long now) throws Exception {
        Object id = json.get("id");
        String password = asString(json.get("password"));
        String clientIp = asString(json.get("client_ip"));
        if (!(id instanceof Long) || (Long) id <= 0 || (Long) id > Integer.MAX_VALUE || password == null
                || password.length() > 1024 || !validIp(clientIp))
            return result(400, error("bad_request"), keyId, nonce);
        String idKey = "id:" + id;
        long retry = Math.max(_ipAttempts.retryAfterSeconds(clientIp, now), _passwordSets.retryAfterSeconds(idKey, now));
        if (retry > 0)
            return throttled(retry, keyId, nonce);
        _ipAttempts.record(clientIp, now);
        Player p;
        try {
            p = _authService.setPassword(((Long) id).intValue(), password);
        } catch (AuthService.WeakPasswordException e) {
            return result(200, error("weak_password"), keyId, nonce);
        }
        if (p == null)
            return result(200, error("not_found"), keyId, nonce);
        _passwordSets.record(idKey, now);
        boolean ended = false;
        java.util.function.Consumer<String> ender = _sessionEnder;
        if (Boolean.TRUE.equals(json.get("end_sessions")) && ender != null) {
            ender.accept(p.getName());
            ended = true;
        }
        LOG.info("sso-auth: password id=" + id + " ip=" + clientIp + " key=" + keyId + (ended ? " sessions ended" : ""));
        JSONObject ok = okUser(p);
        ok.put("sessions_ended", ended);
        return result(200, ok, keyId, nonce);
    }

    private WikiAuthService.Result close(JSONObject json, String keyId, String nonce) throws Exception {
        Object id = json.get("id");
        String clientIp = asString(json.get("client_ip"));
        if (!(id instanceof Long) || (Long) id <= 0 || (Long) id > Integer.MAX_VALUE
                || (clientIp != null && !clientIp.isEmpty() && !validIp(clientIp)))
            return result(400, error("bad_request"), keyId, nonce);
        Player p = _authService.closeAccount(((Long) id).intValue());
        if (p == null)
            return result(200, error("not_found"), keyId, nonce);
        java.util.function.Consumer<String> ender = _sessionEnder;
        if (ender != null)
            ender.accept(p.getName());
        LOG.info("sso-auth: close id=" + id + " key=" + keyId);
        return result(200, okUser(p), keyId, nonce);
    }

    private WikiAuthService.Result rename(JSONObject json, String keyId, String nonce, long now) throws Exception {
        Object id = json.get("id");
        String username = asString(json.get("username"));
        String clientIp = asString(json.get("client_ip"));
        if (!(id instanceof Long) || (Long) id <= 0 || (Long) id > Integer.MAX_VALUE || username == null
                || username.length() > 64 || (clientIp != null && !clientIp.isEmpty() && !validIp(clientIp)))
            return result(400, error("bad_request"), keyId, nonce);
        int playerId = ((Long) id).intValue();
        String idKey = "id:" + playerId;
        long retry = _renames.retryAfterSeconds(idKey, now);
        if (retry > 0)
            return throttled(retry, keyId, nonce);
        if (!com.gempukku.swccgo.service.PlayerNames.isWellFormed(username) || Player.isBot(username))
            return result(200, error("name_invalid"), keyId, nonce);
        Player before = _authService.getPlayer(playerId);
        java.util.function.Predicate<String> atTable = _atTable;
        if (before != null && atTable != null && atTable.test(before.getName()))
            return result(200, error("busy"), keyId, nonce);
        Player p;
        try {
            p = _authService.renameAccount(playerId, username);
        } catch (AuthService.RenameRefused e) {
            return result(200, error(e.code()), keyId, nonce);
        }
        boolean changed = before != null && !before.getName().equals(p.getName());
        if (changed) {
            _renames.record(idKey, now);
            java.util.function.Consumer<String> ender = _sessionEnder;
            if (ender != null)
                ender.accept(before.getName());
            Runnable after = _afterRename;
            if (after != null)
                after.run();
        }
        LOG.info("sso-auth: rename id=" + playerId + " key=" + keyId + (changed ? " renamed" : " unchanged"));
        return result(200, okUser(p), keyId, nonce);
    }

    private WikiAuthService.Result status(JSONObject json, String keyId, String nonce) {
        Object id = json.get("id");
        if (!(id instanceof Long) || (Long) id <= 0 || (Long) id > Integer.MAX_VALUE)
            return result(400, error("bad_request"), keyId, nonce);
        AuthService.AuthResult auth = _authService.accountStatus(((Long) id).intValue());
        JSONObject response;
        if (auth.getStatus() == AuthService.Status.OK && auth.getPlayer() != null) {
            response = okUser(auth.getPlayer());
        } else if (auth.getStatus() == AuthService.Status.INVALID) {
            response = error("not_found");
        } else {
            response = error(WikiAuthService.reasonFor(auth.getStatus()));
            addUntil(response, auth);
        }
        return result(200, response, keyId, nonce);
    }

    @SuppressWarnings("unchecked")
    private static JSONObject okUser(Player p) {
        JSONObject r = new JSONObject();
        r.put("ok", true);
        JSONObject user = new JSONObject();
        user.put("id", p.getId());
        user.put("name", p.getName());
        r.put("user", user);
        return r;
    }

    @SuppressWarnings("unchecked")
    private static void addUntil(JSONObject response, AuthService.AuthResult auth) {
        if (auth != null && auth.getStatus() == AuthService.Status.TEMP_BANNED && auth.getBannedUntil() != null)
            response.put("until", auth.getBannedUntil().getTime() / 1000);
    }

    @SuppressWarnings("unchecked")
    private WikiAuthService.Result throttled(long retry, String keyId, String nonce) {
        JSONObject err = error("throttled");
        err.put("retry_after", retry);
        return result(429, err, keyId, nonce);
    }

    private WikiAuthService.Result result(int status, JSONObject json, String keyId, String nonce) {
        byte[] bytes = json.toJSONString().getBytes(StandardCharsets.UTF_8);
        String sig = nonce == null || keyId == null ? null
                : _keys.sign(keyId, InternalAuthKeysV2.responseCanonical(keyId, nonce, status, bytes));
        return new WikiAuthService.Result(status, bytes, sig);
    }

    @SuppressWarnings("unchecked")
    private static JSONObject error(String code) {
        JSONObject o = new JSONObject();
        o.put("ok", false);
        o.put("error", code);
        return o;
    }

    private static boolean validIp(String ip) {
        return ip != null && IP.matcher(ip).matches();
    }

    private static String asString(Object o) {
        return o instanceof String ? (String) o : null;
    }

    private static String safe(String s) {
        String t = s.replaceAll("[^A-Za-z0-9_~.-]", "?");
        return t.length() > 64 ? t.substring(0, 64) : t;
    }
}
