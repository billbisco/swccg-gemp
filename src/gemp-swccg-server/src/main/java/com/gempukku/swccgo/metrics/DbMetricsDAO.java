package com.gempukku.swccgo.metrics;

import com.gempukku.swccgo.db.DbAccess;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * JDBC implementation of {@link MetricsDAO} on {@code metrics_event} and {@code metrics_daily}
 * (db-scripts/update_add_metrics.sql). Bound parameters only.
 */
public class DbMetricsDAO implements MetricsDAO {
    private static final Gson GSON = new Gson();
    private static final Type DETAILS_TYPE = new TypeToken<LinkedHashMap<String, String>>() { }.getType();

    private final DbAccess _dbAccess;

    public DbMetricsDAO(DbAccess dbAccess) {
        _dbAccess = dbAccess;
    }

    @Override
    public void insert(MetricsEvent event) {
        try (Connection connection = _dbAccess.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO metrics_event (event_time, event, guest_id, game_id, logged_in, details) "
                             + "VALUES (?, ?, ?, ?, ?, ?)")) {
            statement.setLong(1, event.eventTimeMillis);
            statement.setString(2, event.event.wireName());
            setNullableString(statement, 3, event.guestId);
            setNullableString(statement, 4, event.gameId);
            statement.setInt(5, event.loggedIn ? 1 : 0);
            setNullableString(statement, 6, detailsJson(event.details));
            statement.executeUpdate();
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to record metrics event " + event.event.wireName(), exp);
        }
    }

    @Override
    public int deleteOlderThan(long cutoffMillis) {
        try (Connection connection = _dbAccess.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "DELETE FROM metrics_event WHERE event_time < ?")) {
            statement.setLong(1, cutoffMillis);
            return statement.executeUpdate();
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to purge old metrics events", exp);
        }
    }

    @Override
    public List<MetricsEvent> listBetween(long startMillisInclusive, long endMillisExclusive) {
        try (Connection connection = _dbAccess.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT id, event_time, event, guest_id, game_id, logged_in, details "
                             + "FROM metrics_event WHERE event_time >= ? AND event_time < ? ORDER BY id")) {
            statement.setLong(1, startMillisInclusive);
            statement.setLong(2, endMillisExclusive);
            try (ResultSet rs = statement.executeQuery()) {
                List<MetricsEvent> result = new ArrayList<>();
                while (rs.next()) {
                    result.add(readEvent(rs));
                }
                return result;
            }
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to list metrics events", exp);
        }
    }

    @Override
    public boolean hasDailyCounts(LocalDate day) {
        try (Connection connection = _dbAccess.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT 1 FROM metrics_daily WHERE day = ? LIMIT 1")) {
            statement.setDate(1, Date.valueOf(day));
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to check daily metrics for " + day, exp);
        }
    }

    @Override
    public void replaceDailyCounts(LocalDate day, Map<String, Integer> counts) {
        try (Connection connection = _dbAccess.getDataSource().getConnection()) {
            boolean oldAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try {
                try (PreparedStatement delete = connection.prepareStatement("DELETE FROM metrics_daily WHERE day = ?")) {
                    delete.setDate(1, Date.valueOf(day));
                    delete.executeUpdate();
                }
                if (counts != null && !counts.isEmpty()) {
                    try (PreparedStatement insert = connection.prepareStatement(
                            "INSERT INTO metrics_daily (day, metric, count) VALUES (?, ?, ?)")) {
                        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
                            insert.setDate(1, Date.valueOf(day));
                            insert.setString(2, entry.getKey());
                            insert.setInt(3, entry.getValue());
                            insert.addBatch();
                        }
                        insert.executeBatch();
                    }
                }
                connection.commit();
            } catch (SQLException exp) {
                connection.rollback();
                throw exp;
            } finally {
                connection.setAutoCommit(oldAutoCommit);
            }
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to store daily metrics for " + day, exp);
        }
    }

    @Override
    public Map<String, Integer> getDailyCounts(LocalDate day) {
        try (Connection connection = _dbAccess.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT metric, count FROM metrics_daily WHERE day = ?")) {
            statement.setDate(1, Date.valueOf(day));
            try (ResultSet rs = statement.executeQuery()) {
                Map<String, Integer> result = new LinkedHashMap<>();
                while (rs.next()) {
                    result.put(rs.getString(1), rs.getInt(2));
                }
                return result;
            }
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to read daily metrics for " + day, exp);
        }
    }

    private static MetricsEvent readEvent(ResultSet rs) throws SQLException {
        return new MetricsEvent(
                rs.getLong("id"),
                rs.getLong("event_time"),
                MetricsEventType.fromWire(rs.getString("event")),
                emptyToNull(rs.getString("guest_id")),
                emptyToNull(rs.getString("game_id")),
                rs.getInt("logged_in") != 0,
                parseDetails(rs.getString("details")));
    }

    private static void setNullableString(PreparedStatement statement, int index, String value) throws SQLException {
        if (value == null || value.isEmpty()) {
            statement.setNull(index, Types.VARCHAR);
        } else {
            statement.setString(index, value);
        }
    }

    private static String detailsJson(Map<String, String> details) {
        if (details == null || details.isEmpty()) {
            return null;
        }
        return GSON.toJson(details);
    }

    private static Map<String, String> parseDetails(String json) {
        if (json == null || json.isEmpty()) {
            return Map.of();
        }
        Map<String, String> parsed = GSON.fromJson(json, DETAILS_TYPE);
        return parsed == null ? Map.of() : Collections.unmodifiableMap(parsed);
    }

    private static String emptyToNull(String value) {
        return value == null || value.isEmpty() ? null : value;
    }
}
