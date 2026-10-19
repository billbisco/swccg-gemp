package com.gempukku.swccgo.db;

import com.gempukku.swccgo.game.Player;
import com.gempukku.swccgo.security.PasswordHasher;
import com.gempukku.swccgo.security.PasswordSettings;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * A player database access object that accesses the database.
 */
public class DbPlayerDAO implements PlayerDAO {
    private final String _selectPlayer = "select id, name, password, type, last_login_reward, banned_until, create_ip, last_ip from player";
    private final String _notDeactivated = " and not type like '%" + Player.Type.DEACTIVATED + "%'";
    /** Stored in place of a hash while an account waits for a reset code. Never matches any password. */
    public static final String RESET_MARKER = "!reset";
    private DbAccess _dbAccess;
    private final PasswordHasher _passwordHasher;

    /**
     * Creates a player data access object that access the database.
     * @param dbAccess the database access
     */
    public DbPlayerDAO(DbAccess dbAccess) {
        this(dbAccess, new PasswordHasher(PasswordSettings.iterations()));
    }

    public DbPlayerDAO(DbAccess dbAccess, PasswordHasher passwordHasher) {
        _dbAccess = dbAccess;
        _passwordHasher = passwordHasher;
    }

    @Override
    public Player getPlayer(int id) {
        try {
            return getPlayerFromDBById(id);
        } catch (SQLException exp) {
            throw new RuntimeException("Error while retrieving player", exp);
        }
    }

    @Override
    public Player getPlayer(String playerName) {
        return getPlayer(playerName, false);
    }

    @Override
    public Player getPlayer(String playerName, boolean includeDeactivated) {
        try {
            return getPlayerFromDBByName(playerName, includeDeactivated);
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to get player from DB", exp);
        }
    }

    @Override
    public synchronized boolean registerPlayer(String playerName, String password, String remoteAddr) throws SQLException, LoginInvalidException, RegisterNotAllowedException {
        // Accounts in the reset state are no longer re-claimed here. That old path let anyone who knew
        // the name set a new password; a reset is now finished only with a one-time code (AuthService).
        if (!validateNewUser(playerName)) {
            return false;
        }

        try (Connection conn = _dbAccess.getDataSource().getConnection()) {
            try (PreparedStatement statement = conn.prepareStatement(
                    "INSERT INTO player (name, password, type, create_ip) VALUES (?, ?, ?, ?)")) {
                statement.setString(1, playerName);
                statement.setString(2, _passwordHasher.hash(password));
                statement.setString(3, Player.Type.UNBANNED.getValue());
                statement.setString(4, remoteAddr);
                statement.execute();
                return true;
            }
        }
    }

    @Override
    public Player findPlayerForLogin(String login) throws SQLException {
        if (login == null || login.isEmpty() || login.length() > 64)
            return null;
        try (Connection conn = _dbAccess.getDataSource().getConnection()) {
            try (PreparedStatement statement = conn.prepareStatement(
                    _selectPlayer + " WHERE LOWER(name)=LOWER(?) LIMIT 10")) {
                statement.setString(1, login);
                try (ResultSet rs = statement.executeQuery()) {
                    List<Player> matches = new ArrayList<>();
                    while (rs.next()) {
                        matches.add(getPlayerFromResultSet(rs));
                    }
                    for (Player match : matches) {
                        if (match.getName().equals(login))
                            return match;
                    }
                    return matches.size() == 1 ? matches.get(0) : null;
                }
            }
        }
    }

    @Override
    public boolean updatePasswordHash(int playerId, String expectedCurrentHash, String newHash) throws SQLException {
        try (Connection conn = _dbAccess.getDataSource().getConnection()) {
            try (PreparedStatement statement = conn.prepareStatement(
                    "UPDATE player SET password=? WHERE id=? AND password=?")) {
                statement.setString(1, newHash);
                statement.setInt(2, playerId);
                statement.setString(3, expectedCurrentHash);
                return statement.executeUpdate() == 1;
            }
        }
    }

    @Override
    public void storePasswordResetCode(int playerId, String codeHash, long expiresAt, String createdBy) throws SQLException {
        try (Connection conn = _dbAccess.getDataSource().getConnection()) {
            try (PreparedStatement statement = conn.prepareStatement(
                    "REPLACE INTO player_password_reset (player_id, code_hash, expires_at, created_by, created_at) VALUES (?, ?, ?, ?, ?)")) {
                statement.setInt(1, playerId);
                statement.setString(2, codeHash);
                statement.setLong(3, expiresAt);
                statement.setString(4, createdBy);
                statement.setLong(5, System.currentTimeMillis());
                statement.executeUpdate();
            }
        }
    }

    @Override
    public PasswordResetCode getPasswordResetCode(int playerId) throws SQLException {
        try (Connection conn = _dbAccess.getDataSource().getConnection()) {
            try (PreparedStatement statement = conn.prepareStatement(
                    "SELECT code_hash, expires_at FROM player_password_reset WHERE player_id=?")) {
                statement.setInt(1, playerId);
                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next())
                        return new PasswordResetCode(playerId, rs.getString(1), rs.getLong(2));
                    return null;
                }
            }
        }
    }

    @Override
    public void deletePasswordResetCode(int playerId) throws SQLException {
        try (Connection conn = _dbAccess.getDataSource().getConnection()) {
            try (PreparedStatement statement = conn.prepareStatement(
                    "DELETE FROM player_password_reset WHERE player_id=?")) {
                statement.setInt(1, playerId);
                statement.executeUpdate();
            }
        }
    }

    @Override
    public synchronized boolean updateLastLoginIp(String playerName, String remoteAddr) throws SQLException {
        try (Connection conn = _dbAccess.getDataSource().getConnection()) {
            try (PreparedStatement statement = conn.prepareStatement("update player set last_ip=? where name=?")) {
                statement.setString(1, remoteAddr);
                statement.setString(2, playerName);
                return (statement.executeUpdate() == 1);
            }
        }
    }

    @Override
    public synchronized boolean updateLastReward(Player player, Integer previousReward, int currentReward) throws SQLException {
        try (Connection conn = _dbAccess.getDataSource().getConnection()) {
            try (PreparedStatement statement = conn.prepareStatement(
                    "update player set last_login_reward=? where id=?" + (previousReward != null ? " and last_login_reward=?" : ""))) {
                statement.setInt(1, currentReward);
                statement.setInt(2, player.getId());
                if (previousReward != null) {
                    statement.setInt(3, previousReward);
                }
                if (statement.executeUpdate() == 1) {
                    player.setLastLoginReward(currentReward);
                    return true;
                }
                return false;
            }
        }
    }

    @Override
    public synchronized boolean resetUserPassword(String playerName) throws SQLException {
        try (Connection conn = _dbAccess.getDataSource().getConnection()) {
            try (PreparedStatement statement = conn.prepareStatement("UPDATE player SET password=? WHERE name=?")) {
                statement.setString(1, RESET_MARKER);
                statement.setString(2, playerName);
                return statement.executeUpdate() == 1;
            }
        }
    }


    @Override
    public synchronized boolean setPlayerFlag(String playerName, Player.Type flag, boolean status) throws SQLException {
        try (Connection conn = _dbAccess.getDataSource().getConnection()) {
            final Player player = getPlayerFromDBByName(playerName, true);
            if (player == null) {
                return false;
            }

            // Add/remove type
            List<Player.Type> types = Player.Type.getTypes(player.getType());
            if (status) {
                if (!types.contains(flag)) {
                    types.add(flag);
                }
            } else {
                types.remove(flag);
            }

            try (PreparedStatement statement = conn.prepareStatement("UPDATE player SET type=? WHERE id=?")) {
                statement.setString(1, Player.Type.getTypeString(types));
                statement.setInt(2, player.getId());
                return statement.executeUpdate() == 1;
            }
        }
    }

    @Override
    public List<String> findPlayerNamesByPrefix(String prefix, int limit) {
        if (prefix == null || prefix.isEmpty() || limit <= 0) {
            return Collections.emptyList();
        }
        String escaped = prefix.replace("!", "!!").replace("%", "!%").replace("_", "!_") + "%";
        try (Connection conn = _dbAccess.getDataSource().getConnection()) {
            try (PreparedStatement statement = conn.prepareStatement(
                    "select name from player where name like ? escape '!' " + _notDeactivated
                            + " order by name limit ?")) {
                statement.setString(1, escaped);
                statement.setInt(2, limit);
                try (ResultSet rs = statement.executeQuery()) {
                    List<String> names = new LinkedList<>();
                    while (rs.next()) {
                        names.add(rs.getString(1));
                    }
                    return names;
                }
            }
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to search players by prefix", exp);
        }
    }

    @Override
    public List<Player> findPlayersWithFlag(Player.Type flag) {
        try {
            List<Player> players = new LinkedList<Player>();
            try (Connection conn = _dbAccess.getDataSource().getConnection()) {
                try (PreparedStatement statement = conn.prepareStatement(
                        _selectPlayer + " WHERE type LIKE '%" + flag + "%'" )) {
                    try (ResultSet rs = statement.executeQuery()) {
                        while (rs.next()) {
                            players.add(getPlayerFromResultSet(rs));
                        }
                        return players;
                    }
                }
            }
        } catch (SQLException exp) {
            throw new RuntimeException("Error while retrieving players with flag " + flag, exp);
        }
    }

    @Override
    public boolean banPlayerPermanently(String playerName) throws SQLException {
        try (Connection conn = _dbAccess.getDataSource().getConnection()) {
            final Player player = getPlayerFromDBByName(playerName, true);
            if (player == null) {
                return false;
            }

            // Remove unbanned type (and set banned_until to null)
            List<Player.Type> types = Player.Type.getTypes(player.getType());
            types.remove(Player.Type.UNBANNED);

            try (PreparedStatement statement = conn.prepareStatement(
                    "update player set type=?, banned_until=null where id=?")) {
                statement.setString(1, Player.Type.getTypeString(types));
                statement.setInt(2, player.getId());
                return statement.executeUpdate() == 1;
            }
        }
    }

    @Override
    public boolean banPlayerTemporarily(String playerName, long dateTo) throws SQLException {
        try (Connection conn = _dbAccess.getDataSource().getConnection()) {
            final Player player = getPlayerFromDBByName(playerName, true);
            if (player == null) {
                return false;
            }

            // Remove unbanned type (and set banned_until to specified date)
            List<Player.Type> types = Player.Type.getTypes(player.getType());
            types.remove(Player.Type.UNBANNED);

            try (PreparedStatement statement = conn.prepareStatement(
                    "update player set type=?, banned_until=? where id=?")) {
                statement.setString(1, Player.Type.getTypeString(types));
                statement.setLong(2, dateTo);
                statement.setInt(3, player.getId());
                return statement.executeUpdate() == 1;
            }
        }
    }

    @Override
    public boolean unBanPlayer(String playerName) throws SQLException {
        Connection conn = _dbAccess.getDataSource().getConnection();
        try {
            final Player player = getPlayerFromDBByName(playerName, true);
            if (player == null) {
                return false;
            }

            // Add unbanned type (and set banned_until to null)
            List<Player.Type> types = Player.Type.getTypes(player.getType());
            types.add(Player.Type.UNBANNED);

            try (PreparedStatement statement = conn.prepareStatement(
                    "update player set type=?, banned_until=null where id=?")) {
                statement.setString(1, Player.Type.getTypeString(types));
                statement.setInt(2, player.getId());
                return statement.executeUpdate() == 1;
            }
        } finally {
            conn.close();
        }
    }

    @Override
    public List<Player> findSimilarAccounts(String playerName) {
        try {
            Player player = getPlayerFromDBByName(playerName, true);
            if (player == null) {
                return Collections.emptyList();
            }
            Set<Player> players = new HashSet<>();
            players.add(player);
            int prevCount = 0;
            int curCount = players.size();
            while (curCount > prevCount) {
                players.addAll(findSimilarAccountsInner(players));
                prevCount = curCount;
                curCount = players.size();
            }
            return Collections.unmodifiableList(new LinkedList<>(players));
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to find similar accounts from DB", exp);
        }
    }

    /**
     * Find all accounts that share various factors with the given player.  Looks for matching passwords and matching
     * creation/login IP addresses.
     * This is intended to be used progressively, so that the return value is fed back into the function until no further
     * accounts are found.  This is to find similar accounts, and accounts similar to those accounts, and so on until
     * no further suspicious activity is found.
     * This was temporarily (for five years) kneecapped due to IP addresses not being forwarded properly, meaning that
     * the server's IP was used instead of the user's IP.
     * This has likely poisoned the player db to the point that automatic signup prevention probably cannot be used with
     * this function. See: https://github.com/PlayersCommittee/gemp-swccg/pull/87
     * @param players the player accounts to compare against the existing player base
     * @return the similar player accounts found
     */
    private Set<Player> findSimilarAccountsInner(Set<Player> players) throws SQLException {
        Set<Player> similarPlayers = new HashSet<>(players);
        try (Connection conn = _dbAccess.getDataSource().getConnection()) {
            Set<String> ipAddresses = new HashSet<>();

            // Determine IP addresses to compare
            for (Player player : players) {
                if (player.getCreateIp() != null) {
                    ipAddresses.add(player.getCreateIp());
                }
                if (player.getLastIp() != null) {
                    ipAddresses.add(player.getLastIp());
                }
            }
            if (ipAddresses.isEmpty()) {
                return similarPlayers;
            }

            //Build the where clause fragments for ip address comparison
            StringBuilder whereIP = new StringBuilder(" IN (");
            for (String ipAddr : ipAddresses) {
                whereIP.append("'").append(ipAddr).append("', ");
            }
            whereIP.setLength(whereIP.length() - 2);
            whereIP.append(")");

            //Build the where clause fragments for password comparison
            StringBuilder wherePassword = new StringBuilder(" IN (");
            for (String ipAddr : ipAddresses) {
                wherePassword.append("'").append(ipAddr).append("', ");
            }
            wherePassword.setLength(wherePassword.length() - 2);
            wherePassword.append(")");

            String sql = _selectPlayer + " WHERE password" + wherePassword +
                    " OR (create_ip" + whereIP + " or last_ip" + whereIP + ")" +
                    " LIMIT 200";

            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                try (ResultSet rs = statement.executeQuery()) {
                    while (rs.next()) {
                        similarPlayers.add(getPlayerFromResultSet(rs));
                    }
                    return similarPlayers;
                }
            }
        }
    }

    /**
     * Generate a player from the result set.
     * @param rs the result set.
     * @return the player
     * @throws SQLException an SQL exception
     */
    private Player getPlayerFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt(1);
        String name = rs.getString(2);
        String password = rs.getString(3);
        String type = rs.getString(4);
        Integer lastLoginReward = rs.getInt(5);
        if (rs.wasNull())
            lastLoginReward = null;
        Long bannedUntilLong = rs.getLong(6);
        if (rs.wasNull())
            bannedUntilLong = null;

        Date bannedUntil = null;
        if (bannedUntilLong != null)
            bannedUntil = new Date(bannedUntilLong);
        String createIp = rs.getString(7);
        String lastIp = rs.getString(8);

        return new Player(id, name, password, type, lastLoginReward, bannedUntil, createIp, lastIp);
    }

    /**
     * Checks to see if the requested username is a valid new player name.
     * @param playerName the player name
     * @return true if valid, otherwise false
     * @throws SQLException an SQL exception
     * @throws LoginInvalidException an invalid login exception
     */
    private boolean validateNewUser(String playerName) throws SQLException, LoginInvalidException, RegisterNotAllowedException {
        final String validLoginChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_";

        if (playerName.length() < 2 || playerName.length() > 10) {
            throw new LoginInvalidException();
        }

        for (int i = 0; i < playerName.length(); i++) {
            char c = playerName.charAt(i);
            if (!validLoginChars.contains("" + c)) {
                throw new LoginInvalidException();
            }
        }

        String lowerCase = playerName.toLowerCase();
        if (lowerCase.startsWith("admin") || lowerCase.startsWith("guest") || lowerCase.startsWith("system") || lowerCase.startsWith("bye")) {
            return false;
        }

        // Names are unique ignoring case, so "Bill" cannot be registered next to "bill".
        if (isNameTakenIgnoringCase(playerName))
            throw new LoginInvalidException();

        //Deactivating the automatic rejection.  Practically everyone has the same server IP as their origin, so
        // this comparison doesn't work.

//        int tickMarks = 0;
//        for (Player similarPlayer : findSimilarAccounts(playerName)) {
//            if (similarPlayer.getId() != 0) {
//                if (!similarPlayer.hasType(Player.Type.UNBANNED) && similarPlayer.getBannedUntil() == null){
//                    tickMarks+=10;
//                }
//                else {
//                    tickMarks++;
//                }
//            }
//        }

//        if (tickMarks >= 10) {
//            throw new RegisterNotAllowedException();
//        }
        return true;
    }

    private boolean isNameTakenIgnoringCase(String playerName) throws SQLException {
        try (Connection conn = _dbAccess.getDataSource().getConnection()) {
            try (PreparedStatement statement = conn.prepareStatement(
                    "select 1 from player where LOWER(name)=LOWER(?) limit 1")) {
                statement.setString(1, playerName);
                try (ResultSet rs = statement.executeQuery()) {
                    return rs.next();
                }
            }
        }
    }

    /**
     * Gets the player from the database by ID.
     * @param id the ID
     * @return the player, or null if no player found
     * @throws SQLException an SQL exception
     */
    private Player getPlayerFromDBById(int id) throws SQLException {
        try (Connection conn = _dbAccess.getDataSource().getConnection()) {
            try (PreparedStatement statement = conn.prepareStatement(_selectPlayer + " where id=?" + _notDeactivated)) {
                statement.setInt(1, id);
                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) {
                        return getPlayerFromResultSet(rs);
                    } else {
                        return null;
                    }
                }
            }
        }
    }

    /**
     * Gets the player from the database by name.
     * @param playerName the player name
     * @param includeDeactivated true to include deactivated, otherwise false
     * @return the player, or null if no player found
     * @throws SQLException an SQL exception
     */
    private Player getPlayerFromDBByName(String playerName, boolean includeDeactivated) throws SQLException {
        try (Connection conn = _dbAccess.getDataSource().getConnection()) {
            try (PreparedStatement statement = conn.prepareStatement(
                    _selectPlayer + " where name=?" + (!includeDeactivated ? _notDeactivated : ""))) {
                statement.setString(1, playerName);
                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) {
                        return getPlayerFromResultSet(rs);
                    } else {
                        return null;
                    }
                }
            }
        }
    }
}
