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

    public AdminService(PlayerDAO playerDAO, IpBanDAO ipBanDAO, LoggedUserHolder loggedUserHolder, AuthService authService) {
        _playerDAO = playerDAO;
        _ipBanDAO = ipBanDAO;
        _loggedUserHolder = loggedUserHolder;
        _authService = authService;
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
        return code;
    }

    public boolean deactivateUser(String login) throws SQLException {
        final boolean success = _playerDAO.setPlayerFlag(login, Player.Type.DEACTIVATED, true);
        if (!success) {
            return false;
        }
        _loggedUserHolder.forceLogoutUser(login);
        return true;
    }

    public boolean banUser(String login) {
        try {
            final boolean success = _playerDAO.banPlayerPermanently(login);
            if (!success) {
                return false;
            }
            _loggedUserHolder.forceLogoutUser(login);
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
