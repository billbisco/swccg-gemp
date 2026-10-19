package com.gempukku.swccgo.async.internal;

import com.gempukku.swccgo.db.PlayerDAO;
import com.gempukku.swccgo.game.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Transport-independent core of POST /internal/v1/verify (contract: docs/wiki-auth-api.md).
 *
 * Checks, in order: method/path, size, HMAC signature, timestamp skew, nonce replay,
 * rate limits, then GEMP credentials via {@link PlayerDAO#loginPlayer}. Password hashing stays
 * inside PlayerDAO, so the bcrypt/Argon2 upgrade there applies here with no change.
 *
 * Never logs the request body or the password.
 */
public class WikiAuthService {
    public static final String PATH = "/internal/v1/verify";
    public static final int MAX_BODY_BYTES = 4096;
    public static final long MAX_SKEW_SECONDS = 60;
    private static final Pattern NONCE = Pattern.compile("^[A-Za-z0-9_-]{16,64}$");
    private static final Pattern IP = Pattern.compile("^[0-9A-Fa-f:.]{2,45}$");

    private static final Logger LOG = LogManager.getLogger(WikiAuthService.class);

    private final PlayerDAO _playerDao;
    private final InternalAuthSigner _signer;
    private final NonceCache _nonces = new NonceCache(2 * MAX_SKEW_SECONDS * 1000, 100_000);
    // Failed attempts per GEMP account (case-folded): 10 per 15 minutes.
    final KeyedRateLimiter _userFailures = new KeyedRateLimiter(10, 15 * 60_000L);
    // All attempts per end-user IP as reported by the wiki: 30 per 5 minutes.
    final KeyedRateLimiter _ipAttempts = new KeyedRateLimiter(30, 5 * 60_000L);
    // All attempts overall (a runaway or compromised caller): 600 per minute.
    final KeyedRateLimiter _global = new KeyedRateLimiter(600, 60_000L);

    public WikiAuthService(PlayerDAO playerDao, InternalAuthSigner signer) {
        _playerDao = playerDao;
        _signer = signer;
    }

    public static final class Result {
        public final int status;
        public final byte[] body;
        public final String signature;

        Result(int status, byte[] body, String signature) {
            this.status = status;
            this.body = body;
            this.signature = signature;
        }

        public String bodyAsString() {
            return new String(body, StandardCharsets.UTF_8);
        }
    }

    /**
     * @param headers request headers with lower-case names
     */
    public Result handle(String method, String path, Map<String, String> headers, byte[] body, long nowMillis) {
        String nonce = headers.get("x-gemp-nonce");
        if (!PATH.equals(path))
            return result(404, error("not_found"), nonce);
        if (!"POST".equals(method))
            return result(405, error("method_not_allowed"), nonce);
        if (body == null || body.length > MAX_BODY_BYTES)
            return result(413, error("body_too_large"), nonce);

        String timestamp = headers.get("x-gemp-timestamp");
        if (nonce == null || !NONCE.matcher(nonce).matches() || timestamp == null || !timestamp.matches("^[0-9]{1,12}$"))
            return result(401, error("bad_signature"), null);
        String canonical = InternalAuthSigner.requestCanonical(timestamp, nonce, method, path, body);
        if (!_signer.verify(canonical, headers.get("x-gemp-signature"))) {
            LOG.warn("wiki-auth: rejected request with bad signature");
            return result(401, error("bad_signature"), null);
        }
        long skew = Math.abs(nowMillis / 1000 - Long.parseLong(timestamp));
        if (skew > MAX_SKEW_SECONDS)
            return result(401, error("stale_timestamp"), nonce);
        if (!_nonces.firstUse(nonce, nowMillis))
            return result(401, error("replayed_nonce"), nonce);

        String username;
        String password;
        String clientIp;
        try {
            Object parsed = new JSONParser().parse(new String(body, StandardCharsets.UTF_8));
            if (!(parsed instanceof JSONObject))
                return result(400, error("bad_request"), nonce);
            JSONObject json = (JSONObject) parsed;
            username = asString(json.get("username"));
            password = asString(json.get("password"));
            clientIp = asString(json.get("client_ip"));
        } catch (Exception e) {
            return result(400, error("bad_request"), nonce);
        }
        if (username == null || password == null || username.isEmpty() || username.length() > 64
                || password.isEmpty() || password.length() > 1024
                || (clientIp != null && !IP.matcher(clientIp).matches()))
            return result(400, error("bad_request"), nonce);

        String userKey = username.toLowerCase(Locale.ROOT);
        String ipKey = clientIp == null ? "-" : clientIp;
        long retry = Math.max(_global.retryAfterSeconds("all", nowMillis),
                Math.max(_ipAttempts.retryAfterSeconds(ipKey, nowMillis), _userFailures.retryAfterSeconds(userKey, nowMillis)));
        if (retry > 0) {
            LOG.info("wiki-auth: rate limited user=" + safe(username) + " ip=" + ipKey);
            JSONObject err = error("rate_limited");
            err.put("retry_after", retry);
            return result(429, err, nonce);
        }
        _global.record("all", nowMillis);
        _ipAttempts.record(ipKey, nowMillis);

        String outcome;
        JSONObject response;
        try {
            Player player = Player.isBot(username) ? null : _playerDao.loginPlayer(username, password);
            if (player == null) {
                outcome = "invalid_credentials";
                response = error(outcome);
            } else if (player.getPassword() == null || player.getPassword().isEmpty()) {
                // Admin-reset account: PlayerDAO.loginPlayer matches ANY password here. Never accept it.
                outcome = "password_reset_required";
                response = error(outcome);
            } else if (!player.hasType(Player.Type.UNBANNED) && player.getBannedUntil() == null) {
                outcome = "banned";
                response = error(outcome);
            } else if (!player.hasType(Player.Type.UNBANNED) && player.getBannedUntil().after(new Date(nowMillis))) {
                outcome = "temp_banned";
                response = error(outcome);
                response.put("until", player.getBannedUntil().getTime() / 1000);
            } else {
                outcome = "ok";
                response = new JSONObject();
                response.put("ok", true);
                JSONObject user = new JSONObject();
                user.put("id", player.getId());
                user.put("name", player.getName());
                user.put("admin", player.hasType(Player.Type.ADMIN));
                response.put("user", user);
            }
        } catch (Exception e) {
            LOG.error("wiki-auth: lookup failed for user=" + safe(username) + ": " + e.getClass().getSimpleName());
            return result(503, error("unavailable"), nonce);
        }

        if ("ok".equals(outcome))
            _userFailures.clear(userKey);
        else if ("invalid_credentials".equals(outcome))
            _userFailures.record(userKey, nowMillis);
        LOG.info("wiki-auth: verify user=" + safe(username) + " ip=" + ipKey + " result=" + outcome);
        return result(200, response, nonce);
    }

    private Result result(int status, JSONObject json, String nonce) {
        byte[] bytes = json.toJSONString().getBytes(StandardCharsets.UTF_8);
        String sig = nonce == null ? null : _signer.sign(InternalAuthSigner.responseCanonical(nonce, status, bytes));
        return new Result(status, bytes, sig);
    }

    @SuppressWarnings("unchecked")
    private static JSONObject error(String code) {
        JSONObject o = new JSONObject();
        o.put("ok", false);
        o.put("error", code);
        return o;
    }

    private static String asString(Object o) {
        return o instanceof String ? (String) o : null;
    }

    /** Usernames are attacker-chosen; keep log lines single-line and short. */
    private static String safe(String s) {
        String t = s.replaceAll("[^A-Za-z0-9_~.-]", "?");
        return t.length() > 64 ? t.substring(0, 64) : t;
    }
}
