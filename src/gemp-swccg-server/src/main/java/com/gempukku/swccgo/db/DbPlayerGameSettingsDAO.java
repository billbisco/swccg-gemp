package com.gempukku.swccgo.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;

/** {@link PlayerGameSettingsDAO} on the {@code player_game_settings} table, which it creates when missing (#135). */
public class DbPlayerGameSettingsDAO implements PlayerGameSettingsDAO {
    private final DbAccess _dbAccess;
    private volatile boolean _tableReady;

    public DbPlayerGameSettingsDAO(DbAccess dbAccess) {
        _dbAccess = dbAccess;
    }

    private void ensureTable(Connection connection) throws SQLException {
        if (_tableReady)
            return;
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS player_game_settings ("
                    + "player_id INT NOT NULL, "
                    + "form VARCHAR(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL, "
                    + "format VARCHAR(100) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL, "
                    + "options TEXT CHARACTER SET utf8 COLLATE utf8_bin NOT NULL, "
                    + "updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, "
                    + "PRIMARY KEY (player_id, form, format))");
        }
        _tableReady = true;
    }

    @Override
    public Map<String, String> load(int playerId) {
        Map<String, String> result = new LinkedHashMap<>();
        try (Connection connection = _dbAccess.getDataSource().getConnection()) {
            ensureTable(connection);
            try (PreparedStatement statement = connection.prepareStatement(
                    "select form, format, options from player_game_settings where player_id=? order by form, format")) {
                statement.setInt(1, playerId);
                try (ResultSet rs = statement.executeQuery()) {
                    while (rs.next())
                        result.put(rs.getString(1) + "|" + rs.getString(2), rs.getString(3));
                }
            }
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to load game settings", exp);
        }
        return result;
    }

    @Override
    public void save(int playerId, String form, String format, String optionsJson) {
        try (Connection connection = _dbAccess.getDataSource().getConnection()) {
            ensureTable(connection);
            if (optionsJson == null || optionsJson.trim().isEmpty() || "{}".equals(optionsJson.trim())) {
                try (PreparedStatement statement = connection.prepareStatement(
                        "delete from player_game_settings where player_id=? and form=? and format=?")) {
                    statement.setInt(1, playerId);
                    statement.setString(2, form);
                    statement.setString(3, format);
                    statement.execute();
                }
                return;
            }
            try (PreparedStatement statement = connection.prepareStatement(
                    "insert into player_game_settings (player_id, form, format, options) values (?, ?, ?, ?) "
                            + "on duplicate key update options=values(options)")) {
                statement.setInt(1, playerId);
                statement.setString(2, form);
                statement.setString(3, format);
                statement.setString(4, optionsJson);
                statement.execute();
            }
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to save game settings", exp);
        }
    }
}
