package com.gempukku.swccgo.service;

import com.gempukku.swccgo.db.LoginInvalidException;
import com.gempukku.swccgo.db.PasswordResetCode;
import com.gempukku.swccgo.db.PlayerDAO;
import com.gempukku.swccgo.db.RegisterNotAllowedException;
import com.gempukku.swccgo.game.Player;
import com.gempukku.swccgo.security.PasswordHasher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Player table in memory, mirroring the DbPlayerDAO rules that AuthService depends on
 * (case-insensitive lookup with exact-case preference, compare-and-set hash update, reset marker).
 */
class InMemoryPlayerDAO implements PlayerDAO {
    static final String RESET_MARKER = "!reset";
    private final Map<Integer, Player> _players = new HashMap<>();
    private final Map<Integer, PasswordResetCode> _codes = new HashMap<>();
    private final Map<Integer, String> _codeCreatedBy = new HashMap<>();
    private final PasswordHasher _hasher;
    private int _nextId = 1;
    int hashUpdates;

    InMemoryPlayerDAO(PasswordHasher hasher) {
        _hasher = hasher;
    }

    Player addWithStoredHash(String name, String storedHash, String type, Date bannedUntil) {
        Player p = new Player(_nextId++, name, storedHash, type, null, bannedUntil, null, null);
        _players.put(p.getId(), p);
        return p;
    }

    String storedHash(String name) {
        for (Player p : _players.values())
            if (p.getName().equals(name))
                return p.getPassword();
        return null;
    }

    String codeCreatedBy(int playerId) {
        return _codeCreatedBy.get(playerId);
    }

    private void replace(Player old, String newHash, String newType) {
        _players.put(old.getId(), new Player(old.getId(), old.getName(), newHash, newType, old.getLastLoginReward(),
                old.getBannedUntil(), old.getCreateIp(), old.getLastIp()));
    }

    @Override
    public Player getPlayer(int id) {
        return _players.get(id);
    }

    @Override
    public Player getPlayer(String playerName) {
        return getPlayer(playerName, false);
    }

    @Override
    public Player getPlayer(String playerName, boolean includeDeactivated) {
        for (Player p : _players.values()) {
            if (p.getName().equals(playerName) && (includeDeactivated || !p.hasType(Player.Type.DEACTIVATED)))
                return p;
        }
        return null;
    }

    @Override
    public boolean registerPlayer(String playerName, String password, String remoteAddr) throws LoginInvalidException, RegisterNotAllowedException {
        for (Player p : _players.values())
            if (p.getName().equalsIgnoreCase(playerName))
                throw new LoginInvalidException();
        addWithStoredHash(playerName, _hasher.hash(password), "u", null);
        if (remoteAddr != null)
            signupsByIp.merge(remoteAddr, 1, Integer::sum);
        return true;
    }

    @Override
    public Player findPlayerForLogin(String login) {
        List<Player> matches = new ArrayList<>();
        for (Player p : _players.values()) {
            if (p.getName().equals(login))
                return p;
            if (p.getName().equalsIgnoreCase(login))
                matches.add(p);
        }
        return matches.size() == 1 ? matches.get(0) : null;
    }

    @Override
    public boolean updatePasswordHash(int playerId, String expectedCurrentHash, String newHash) {
        Player p = _players.get(playerId);
        if (p == null || !p.getPassword().equals(expectedCurrentHash))
            return false;
        replace(p, newHash, p.getType());
        hashUpdates++;
        return true;
    }

    final Map<String, Integer> signupsByIp = new HashMap<>();

    @Override
    public int countPlayersCreatedFromIpSince(String ip, long sinceMillis) {
        return signupsByIp.getOrDefault(ip, 0);
    }

    @Override
    public void storePasswordResetCode(int playerId, String codeHash, long expiresAt, String createdBy) {
        _codes.put(playerId, new PasswordResetCode(playerId, codeHash, expiresAt));
        _codeCreatedBy.put(playerId, createdBy);
    }

    @Override
    public PasswordResetCode getPasswordResetCode(int playerId) {
        return _codes.get(playerId);
    }

    @Override
    public void deletePasswordResetCode(int playerId) {
        _codes.remove(playerId);
    }

    @Override
    public boolean updateLastLoginIp(String playerName, String remoteAddr) {
        return true;
    }

    @Override
    public boolean updateLastReward(Player player, Integer previousReward, int currentReward) {
        return true;
    }

    @Override
    public boolean resetUserPassword(String playerName) {
        Player p = getPlayer(playerName, true);
        if (p == null)
            return false;
        replace(p, RESET_MARKER, p.getType());
        return true;
    }

    @Override
    public boolean setPlayerFlag(String playerName, Player.Type flag, boolean status) {
        return getPlayer(playerName, true) != null;
    }

    @Override
    public List<Player> findPlayersWithFlag(Player.Type flag) {
        return Collections.emptyList();
    }

    @Override
    public List<String> findPlayerNamesByPrefix(String prefix, int limit) {
        return Collections.emptyList();
    }

    @Override
    public boolean banPlayerPermanently(String playerName) {
        return false;
    }

    @Override
    public boolean banPlayerTemporarily(String playerName, long dateTo) {
        return false;
    }

    @Override
    public boolean unBanPlayer(String playerName) {
        return false;
    }

    @Override
    public List<Player> findSimilarAccounts(String playerName) {
        return Collections.emptyList();
    }
}
