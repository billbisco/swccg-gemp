package com.gempukku.swccgo.db;

import com.gempukku.swccgo.game.Player;

import java.sql.SQLException;
import java.util.List;

/**
 * An interface to represent the player database access object.
 */
public interface PlayerDAO {

    /**
     * Gets the player from the player ID.
     * @param id the player ID
     * @return the player
     */
    Player getPlayer(int id);

    /**
     * Gets the player from the player name.
     * @param playerName the player name
     * @return the player
     */
    Player getPlayer(String playerName);

    /**
     * Gets the player from the player name, optionally including deactivated users.  Defaults to false.
     * @param playerName the player name
     * @param includeDeactivated if false, will return null if the player is deactivated, even if the player exists.
     * @return the player
     */
    Player getPlayer(String playerName, boolean includeDeactivated);

    /**
     * Registers a new player.
     * @param playerName the player name
     * @param password the password
     * @param remoteAddr the IP address
     * @return true if registered, otherwise false
     * @throws SQLException an SQL exception
     * @throws LoginInvalidException an invalid login exception
     * @throws RegisterNotAllowedException a register not allowed exception
     */
    boolean registerPlayer(String playerName, String password, String remoteAddr) throws SQLException, LoginInvalidException, RegisterNotAllowedException;

    /**
     * Finds the account a login name refers to, ignoring case and including deactivated accounts.
     * An exact (case-sensitive) match wins. Otherwise the single case-insensitive match is returned.
     * If several legacy accounts differ only by case and none matches exactly, returns null.
     * Credential checks belong in {@link com.gempukku.swccgo.service.AuthService}, not here.
     * @param login the name as typed
     * @return the player, or null
     * @throws SQLException an SQL exception
     */
    Player findPlayerForLogin(String login) throws SQLException;

    /**
     * Replaces the stored password hash, but only if it still equals expectedCurrentHash
     * (so a concurrent reset or password change is never overwritten by a login rehash).
     * @param playerId the player ID
     * @param expectedCurrentHash the hash read before verifying
     * @param newHash the new stored hash
     * @return true if updated
     * @throws SQLException an SQL exception
     */
    boolean updatePasswordHash(int playerId, String expectedCurrentHash, String newHash) throws SQLException;

    /**
     * Stores (or replaces) the one-time reset code hash for a player.
     */
    void storePasswordResetCode(int playerId, String codeHash, long expiresAt, String createdBy) throws SQLException;

    /**
     * @return the player's pending reset code, or null
     */
    PasswordResetCode getPasswordResetCode(int playerId) throws SQLException;

    /**
     * Deletes the player's reset code (after use, or when superseded).
     */
    void deletePasswordResetCode(int playerId) throws SQLException;

    /**
     * Updates the IP address that the player last used to login.
     * @param playerName the player name
     * @param remoteAddr the IP address
     * @return true if successful, otherwise false
     * @throws SQLException an SQL exception
     */
    boolean updateLastLoginIp(String playerName, String remoteAddr) throws SQLException;

    /**
     * Updates the date indicator of the last time the player received a reward for logging in.
     * @param player the player
     * @param previousReward the previous date indicator, or null
     * @param currentReward the current date indicator
     * @return true if successful, otherwise false
     * @throws SQLException an SQL exception
     */
    boolean updateLastReward(Player player, Integer previousReward, int currentReward) throws SQLException;

    /**
     * Puts the account into the reset state: the old password stops working, and only a valid
     * one-time reset code (see {@link #storePasswordResetCode}) lets the player set a new one.
     * @param playerName the player name
     * @return true if successful, otherwise false
     * @throws SQLException an SQL exception
     */
    boolean resetUserPassword(String playerName) throws SQLException;

    /**
     * Sets a player flag, such as "playtester" or "commentator".
     * @param playerName the player name
     * @param flag which flag to set
     * @param status true to add the flag, false to remove it
     * @return true if successful, otherwise false
     * @throws SQLException an SQL exception
     */
    boolean setPlayerFlag(String playerName, Player.Type flag, boolean status) throws SQLException;

    /**
     * Gets a list of players that have a given permission flag.
     * @param flag Which flag to search for
     * @return the list of play testers
     */
    List<Player> findPlayersWithFlag(Player.Type flag);

    /**
     * Names of active players whose name starts with prefix (case-insensitive), at most limit entries.
     */
    List<String> findPlayerNamesByPrefix(String prefix, int limit);


    /**
     * Permanently ban the specified player.
     * @param playerName the player name
     * @return true if successful, otherwise false
     * @throws SQLException an SQL exception
     */
    boolean banPlayerPermanently(String playerName) throws SQLException;

    /**
     * Temporarily ban the specified player.
     * @param playerName the player name
     * @param dateTo the date until which the player is banned
     * @return true if successful, otherwise false
     * @throws SQLException an SQL exception
     */
    boolean banPlayerTemporarily(String playerName, long dateTo) throws SQLException;

    /**
     * Removes the ban of the specified player.
     * @param playerName the player name
     * @return true if successful, otherwise false
     * @throws SQLException an SQL exception
     */
    boolean unBanPlayer(String playerName) throws SQLException;

    /**
     * Gets a list of players that appear to be similar (same IP address, etc.) to the specified player.
     * @param playerName the player
     * @return the list of similar players
     */
    List<Player> findSimilarAccounts(String playerName);
}
