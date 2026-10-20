package com.gempukku.swccgo.game.save;

import com.gempukku.swccgo.db.DbAccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC implementation of GameSaveDAO on the game_save table (db-scripts/update_add_game_save.sql).
 */
public class DbGameSaveDAO implements GameSaveDAO {
    private final DbAccess _dbAccess;

    public DbGameSaveDAO(DbAccess dbAccess) {
        _dbAccess = dbAccess;
    }

    @Override
    public void save(String gameId, String owner, GameSave save) {
        try (Connection connection = _dbAccess.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "REPLACE INTO game_save (game_id, owner, header, move_log, snapshot, snapshot_log_size, snapshot_fingerprint, updated_at) "
                             + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
            statement.setString(1, gameId);
            statement.setString(2, owner);
            statement.setString(3, save.getHeader().toJson());
            statement.setBytes(4, save.getLog().toBytes());
            if (save.getSnapshot() == null) {
                statement.setNull(5, Types.BLOB);
            } else {
                statement.setBytes(5, save.getSnapshot());
            }
            statement.setInt(6, save.getSnapshotLogSize());
            statement.setString(7, save.getSnapshotFingerprint());
            statement.setLong(8, save.getHeader().updatedAt);
            statement.execute();
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to save game " + gameId, exp);
        }
    }

    @Override
    public GameSave load(String gameId) {
        try (Connection connection = _dbAccess.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT header, move_log, snapshot, snapshot_log_size, snapshot_fingerprint FROM game_save WHERE game_id = ?")) {
            statement.setString(1, gameId);
            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return new GameSave(GameSaveHeader.fromJson(rs.getString(1)), DecisionLog.fromBytes(rs.getBytes(2)), rs.getBytes(3), rs.getInt(4), rs.getString(5));
            }
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to load game " + gameId, exp);
        }
    }

    @Override
    public String getOwner(String gameId) {
        try (Connection connection = _dbAccess.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT owner FROM game_save WHERE game_id = ?")) {
            statement.setString(1, gameId);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() ? rs.getString(1) : null;
            }
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to read owner of game " + gameId, exp);
        }
    }

    @Override
    public List<String> getGameIds(String owner) {
        try (Connection connection = _dbAccess.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT game_id FROM game_save WHERE owner = ? ORDER BY updated_at DESC")) {
            statement.setString(1, owner);
            List<String> ids = new ArrayList<>();
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getString(1));
                }
            }
            return ids;
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to list saved games", exp);
        }
    }

    @Override
    public void delete(String gameId) {
        update("DELETE FROM game_save WHERE game_id = ?", gameId);
    }

    @Override
    public void changeOwner(String fromOwner, String toOwner) {
        update("UPDATE game_save SET owner = ? WHERE owner = ?", toOwner, fromOwner);
    }

    @Override
    public int deleteLastPlayedAtOrBefore(long cutoffMillis) {
        try (Connection connection = _dbAccess.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM game_save WHERE updated_at <= ?")) {
            statement.setLong(1, cutoffMillis);
            return statement.executeUpdate();
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to delete old saved games", exp);
        }
    }

    private int update(String sql, String... args) {
        try (Connection connection = _dbAccess.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < args.length; i++) {
                statement.setString(i + 1, args[i]);
            }
            return statement.executeUpdate();
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to update saved games", exp);
        }
    }
}
