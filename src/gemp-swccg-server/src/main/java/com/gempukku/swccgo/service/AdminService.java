package com.gempukku.swccgo.service;

import com.gempukku.swccgo.db.IpBanDAO;
import com.gempukku.swccgo.db.PlayerDAO;
import com.gempukku.swccgo.game.Player;

import java.sql.SQLException;

public class AdminService {
    public static final int DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
    private PlayerDAO _playerDAO;
    private LoggedUserHolder _loggedUserHolder;
    private IpBanDAO _ipBanDAO;
    private AuthService _authService;
    /** Told about a ban, deactivation or reset (swccg-login revoke); may be null. */
    public interface RevocationListener {
        /** reason is "reset", "deactivate", "ban" or "temp_ban". */
        void revoked(int playerId, String reason);
    }

    private volatile RevocationListener _revocationListener;

    public AdminService(PlayerDAO playerDAO, IpBanDAO ipBanDAO, LoggedUserHolder loggedUserHolder, AuthService authService) {
        _playerDAO = playerDAO;
        _ipBanDAO = ipBanDAO;
        _loggedUserHolder = loggedUserHolder;
        _authService = authService;
    }

    public void setRevocationListener(RevocationListener listener) {
        _revocationListener = listener;
    }

    private void notifyRevoked(String login, String reason) {
        RevocationListener l = _revocationListener;
        if (l == null)
            return;
        try {
            Player p = _playerDAO.getPlayer(login, true);
            if (p != null)
                l.revoked(p.getId(), reason);
        } catch (Exception ignored) {
            // best effort: swccg-login re-checks the account with GEMP on every sign-in
        }
    }

    public boolean setUserFlag(String login, Player.Type flag, boolean status) throws SQLException {
        return _playerDAO.setPlayerFlag(login, flag, status);
    }

    /**
     * Admin password reset. The old password stops working and a one-time code (24h, single use)
     * is returned for the admin to give the player privately. The player logs in with the code
     * and is then asked for a new password.
     * @return the code, or null if the player was not found
     */
    public String resetUserPassword(String login, String adminName) throws SQLException {
        if (!_playerDAO.setPlayerFlag(login, Player.Type.DEACTIVATED, false))
            return null;
        String code = _authService.issueResetCode(login, adminName);
        if (code == null)
            return null;
        _loggedUserHolder.forceLogoutUser(login);
        // "reset" also makes swccg-login drop the account's passkeys and connected accounts.
        notifyRevoked(login, "reset");
        return code;
    }

    public boolean deactivateUser(String login) throws SQLException {
        final boolean success = _playerDAO.setPlayerFlag(login, Player.Type.DEACTIVATED, true);
        if (!success) {
            return false;
        }
        _loggedUserHolder.forceLogoutUser(login);
        notifyRevoked(login, "deactivate");
        return true;
    }

    public boolean banUser(String login) {
        try {
            final boolean success = _playerDAO.banPlayerPermanently(login);
            if (!success) {
                return false;
            }
            _loggedUserHolder.forceLogoutUser(login);
            notifyRevoked(login, "ban");
            return true;
        } catch (SQLException exp) {
            return false;
        }
    }

    public boolean banUserTemp(String login, int days) {
        try {
            final boolean success = _playerDAO.banPlayerTemporarily(login, System.currentTimeMillis() + days * DAY_IN_MILLIS);
            if (!success) {
                return false;
            }
            _loggedUserHolder.forceLogoutUser(login);
            notifyRevoked(login, "temp_ban");
            return true;
        } catch (SQLException exp) {
            return false;
        }
    }

    public boolean unBanUser(String login) {
        try {
            return _playerDAO.unBanPlayer(login);
        } catch (SQLException exp) {
            return false;
        }
    }

    public boolean banIp(String login) {
        final Player player = _playerDAO.getPlayer(login, true);
        if (player == null)
            return false;
        final String lastIp = player.getLastIp();
        
        _ipBanDAO.addIpBan(lastIp);
        
        return banUser(login);
    }

    public boolean banIpPrefix(String login) {
        final Player player = _playerDAO.getPlayer(login, true);
        if (player == null)
            return false;
        final String lastIp = player.getLastIp();
        String lastIpPrefix = lastIp.substring(0, lastIp.lastIndexOf(".")+1);

        _ipBanDAO.addIpPrefixBan(lastIpPrefix);

        return banUser(login);
    }
}
