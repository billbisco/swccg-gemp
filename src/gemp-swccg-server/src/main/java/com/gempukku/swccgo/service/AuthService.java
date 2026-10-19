package com.gempukku.swccgo.service;

import com.gempukku.swccgo.db.LoginInvalidException;
import com.gempukku.swccgo.db.PasswordResetCode;
import com.gempukku.swccgo.db.PlayerDAO;
import com.gempukku.swccgo.db.RegisterNotAllowedException;
import com.gempukku.swccgo.game.Player;
import com.gempukku.swccgo.security.PasswordHasher;
import com.gempukku.swccgo.security.PasswordPolicy;
import com.gempukku.swccgo.security.ResetCodes;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.LongSupplier;

/**
 * The single place that checks GEMP credentials.
 *
 * <p>Used by the login handler today, and meant to be called by the internal
 * auth-check endpoint for the wiki ({@link #verifyCredentials}) so both sites share
 * one hash format, one rehash path, one throttle and one ban check.</p>
 *
 * <p>Also owns the admin one-time reset code flow:
 * {@link #issueResetCode} (admin) -> player logs in with the code ->
 * {@link #createResetTicket} (short-lived, held in an HttpOnly cookie) ->
 * {@link #completeReset} sets the new password and burns the code.</p>
 */
public class AuthService {

    public enum Status {
        /** Name and password are right and the account may play. */
        OK,
        /** Unknown name or wrong password (deliberately not distinguished). */
        INVALID,
        /** The password was a valid admin reset code; the player must choose a new password. */
        RESET_REQUIRED,
        BANNED,
        TEMP_BANNED,
        DEACTIVATED,
        /** Too many recent failures for this account or IP; try again later. */
        THROTTLED
    }

    public static final class AuthResult {
        private final Status _status;
        private final Player _player;

        AuthResult(Status status, Player player) {
            _status = status;
            _player = player;
        }

        public Status getStatus() {
            return _status;
        }

        /** True only for {@link Status#OK}. */
        public boolean isOk() {
            return _status == Status.OK;
        }

        /** The player for OK / RESET_REQUIRED / ban states; null for INVALID and THROTTLED. */
        public Player getPlayer() {
            return _player;
        }

        /** Canonical stored name (exact case), or null. Callers must use this, not the typed login. */
        public String getCanonicalName() {
            return _player != null ? _player.getName() : null;
        }

        public Date getBannedUntil() {
            return _player != null ? _player.getBannedUntil() : null;
        }
    }

    public enum ResetOutcome { OK, INVALID_TICKET, WEAK_PASSWORD }

    static final long RESET_TICKET_TTL_MILLIS = 15L * 60 * 1000;
    public static final String RESET_COOKIE = "gempPasswordReset";

    private final PlayerDAO _playerDAO;
    private final PasswordHasher _hasher;
    private final LoginThrottle _throttle;
    private final LongSupplier _clock;
    private final SecureRandom _random = new SecureRandom();
    private final Map<String, ResetTicket> _resetTickets = new ConcurrentHashMap<>();
    /** Burned on unknown names so a miss costs about the same time as a hit. */
    private final String _dummyHash;

    private static final class ResetTicket {
        final int playerId;
        final String playerName;
        final long expiresAt;

        ResetTicket(int playerId, String playerName, long expiresAt) {
            this.playerId = playerId;
            this.playerName = playerName;
            this.expiresAt = expiresAt;
        }
    }

    public AuthService(PlayerDAO playerDAO, PasswordHasher hasher, LoginThrottle throttle) {
        this(playerDAO, hasher, throttle, System::currentTimeMillis);
    }

    public AuthService(PlayerDAO playerDAO, PasswordHasher hasher, LoginThrottle throttle, LongSupplier clock) {
        _playerDAO = playerDAO;
        _hasher = hasher;
        _throttle = throttle;
        _clock = clock;
        _dummyHash = hasher.hash("dummy-password-for-timing");
    }

    /**
     * Checks a name + password. Handles both the new PBKDF2 hashes and legacy unsalted SHA-256
     * (upgrading the latter in place on success), admin reset codes, bans and deactivation,
     * and per-account / per-IP failure throttling.
     *
     * @param login the name as typed (matched case-insensitively)
     * @param password the password as typed
     * @param remoteIp the client IP (already resolved through trusted proxies), for throttling
     */
    public AuthResult verifyCredentials(String login, String password, String remoteIp) throws SQLException {
        if (login == null || password == null || password.length() > 1024)
            return new AuthResult(Status.INVALID, null);

        long now = _clock.getAsLong();
        if (_throttle.isBlocked(login, remoteIp, now))
            return new AuthResult(Status.THROTTLED, null);

        Player player = _playerDAO.findPlayerForLogin(login);
        if (player == null) {
            _hasher.verify(password, _dummyHash);
            _throttle.recordFailure(login, remoteIp, now);
            return new AuthResult(Status.INVALID, null);
        }

        String stored = player.getPassword();
        boolean passwordOk = _hasher.verify(password, stored);

        if (!passwordOk) {
            if (isResetCodeValid(player, password, now)) {
                _throttle.recordSuccess(login, remoteIp);
                return new AuthResult(Status.RESET_REQUIRED, player);
            }
            _throttle.recordFailure(login, remoteIp, now);
            return new AuthResult(Status.INVALID, null);
        }

        _throttle.recordSuccess(login, remoteIp);

        if (_hasher.needsRehash(stored)) {
            // Lazy upgrade. Compare-and-set so a reset that raced this login is not undone.
            _playerDAO.updatePasswordHash(player.getId(), stored, _hasher.hash(password));
        }

        if (player.hasType(Player.Type.DEACTIVATED))
            return new AuthResult(Status.DEACTIVATED, player);

        if (!player.hasType(Player.Type.UNBANNED)) {
            Date bannedUntil = player.getBannedUntil();
            if (bannedUntil == null)
                return new AuthResult(Status.BANNED, player);
            if (bannedUntil.after(new Date(now)))
                return new AuthResult(Status.TEMP_BANNED, player);
        }
        return new AuthResult(Status.OK, player);
    }

    private boolean isResetCodeValid(Player player, String candidate, long now) throws SQLException {
        if (!ResetCodes.looksLikeCode(candidate))
            return false;
        PasswordResetCode code = _playerDAO.getPasswordResetCode(player.getId());
        if (code == null)
            return false;
        if (code.isExpired(now)) {
            _playerDAO.deletePasswordResetCode(player.getId());
            return false;
        }
        return MessageDigest.isEqual(
                code.getCodeHash().getBytes(StandardCharsets.US_ASCII),
                ResetCodes.hash(candidate).getBytes(StandardCharsets.US_ASCII));
    }

    /**
     * Admin reset: the old password stops working and a one-time code is returned for the admin
     * to pass to the player privately. The code expires after 24 hours and works once.
     * @return the plain code (shown once, never stored), or null if the player does not exist
     */
    public String issueResetCode(String playerName, String adminName) throws SQLException {
        Player player = _playerDAO.getPlayer(playerName, true);
        if (player == null)
            return null;
        if (!_playerDAO.resetUserPassword(player.getName()))
            return null;
        String code = ResetCodes.newCode();
        _playerDAO.storePasswordResetCode(player.getId(), ResetCodes.hash(code),
                _clock.getAsLong() + ResetCodes.DEFAULT_TTL_MILLIS, adminName);
        _resetTickets.values().removeIf(t -> t.playerId == player.getId());
        return code;
    }

    /** Called after {@link Status#RESET_REQUIRED}; the value goes into an HttpOnly cookie. */
    public String createResetTicket(Player player) {
        purgeExpiredTickets();
        byte[] bytes = new byte[32];
        _random.nextBytes(bytes);
        String ticket = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        _resetTickets.put(ticket, new ResetTicket(player.getId(), player.getName(), _clock.getAsLong() + RESET_TICKET_TTL_MILLIS));
        return ticket;
    }

    /** True if this ticket is live and belongs to the named account (case-insensitive). */
    public boolean hasValidResetTicket(String ticket, String login) {
        ResetTicket t = ticket != null ? _resetTickets.get(ticket) : null;
        return t != null && t.expiresAt > _clock.getAsLong() && login != null && t.playerName.equalsIgnoreCase(login);
    }

    /**
     * Sets the new password for a player holding a valid reset ticket, then burns the ticket and code.
     */
    public ResetOutcome completeReset(String ticket, String login, String newPassword) throws SQLException {
        if (!hasValidResetTicket(ticket, login))
            return ResetOutcome.INVALID_TICKET;
        if (!PasswordPolicy.isAcceptable(newPassword))
            return ResetOutcome.WEAK_PASSWORD;
        ResetTicket t = _resetTickets.remove(ticket);
        if (t == null)
            return ResetOutcome.INVALID_TICKET;
        Player player = _playerDAO.getPlayer(t.playerId);
        if (player == null) {
            player = _playerDAO.findPlayerForLogin(t.playerName);
        }
        if (player == null || player.getId() != t.playerId || _playerDAO.getPasswordResetCode(t.playerId) == null)
            return ResetOutcome.INVALID_TICKET;
        _playerDAO.deletePasswordResetCode(t.playerId);
        if (!_playerDAO.updatePasswordHash(t.playerId, player.getPassword(), _hasher.hash(newPassword)))
            return ResetOutcome.INVALID_TICKET;
        _throttle.recordSuccess(t.playerName, null);
        return ResetOutcome.OK;
    }

    /**
     * Registers a new account after applying the password policy.
     * @throws WeakPasswordException if the password breaks {@link PasswordPolicy}
     */
    public boolean register(String login, String password, String remoteIp)
            throws SQLException, LoginInvalidException, RegisterNotAllowedException, WeakPasswordException {
        if (!PasswordPolicy.isAcceptable(password))
            throw new WeakPasswordException();
        return _playerDAO.registerPlayer(login, password, remoteIp);
    }

    public static class WeakPasswordException extends Exception {
        public WeakPasswordException() {
            super("Password must be " + PasswordPolicy.MIN_LENGTH + " to " + PasswordPolicy.MAX_LENGTH + " characters.");
        }
    }

    private void purgeExpiredTickets() {
        long now = _clock.getAsLong();
        Iterator<Map.Entry<String, ResetTicket>> it = _resetTickets.entrySet().iterator();
        while (it.hasNext()) {
            if (it.next().getValue().expiresAt <= now)
                it.remove();
        }
    }
}
