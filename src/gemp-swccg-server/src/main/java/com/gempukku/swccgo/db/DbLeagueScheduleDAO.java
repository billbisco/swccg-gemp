package com.gempukku.swccgo.db;

import com.gempukku.swccgo.db.vo.LeagueScheduleRow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link LeagueScheduleDAO} on the {@code league_schedule} table.  Ported from LOTR GEMP's DbLeagueScheduleDAO
 * (sql2o there) to this server's plain JDBC style; all values are bound as parameters.
 */
public class DbLeagueScheduleDAO implements LeagueScheduleDAO {
    private static final String SELECT_COLUMNS = "select id, name, league_type, template, events, name_pattern, next_event_date, next_event_index, "
            + "interval_months, lead_days, enabled, last_created_league_type, last_run, last_error, created_by, created_at from league_schedule";

    private final DbAccess _dbAccess;

    public DbLeagueScheduleDAO(DbAccess dbAccess) {
        _dbAccess = dbAccess;
    }

    @Override
    public List<LeagueScheduleRow> getAllSchedules() {
        try (Connection conn = _dbAccess.getDataSource().getConnection();
             PreparedStatement statement = conn.prepareStatement(SELECT_COLUMNS + " order by name, id");
             ResultSet rs = statement.executeQuery()) {
            List<LeagueScheduleRow> result = new ArrayList<>();
            while (rs.next())
                result.add(read(rs));
            return result;
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to retrieve league schedules", exp);
        }
    }

    @Override
    public LeagueScheduleRow getSchedule(int id) {
        try (Connection conn = _dbAccess.getDataSource().getConnection();
             PreparedStatement statement = conn.prepareStatement(SELECT_COLUMNS + " where id = ?")) {
            statement.setInt(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() ? read(rs) : null;
            }
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to retrieve league schedule " + id, exp);
        }
    }

    @Override
    public int saveSchedule(LeagueScheduleRow schedule) {
        try (Connection conn = _dbAccess.getDataSource().getConnection()) {
            if (schedule.id == 0) {
                String sql = "insert into league_schedule (name, league_type, template, events, name_pattern, next_event_date, next_event_index, "
                        + "interval_months, lead_days, enabled, created_by) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    int i = bindDefinition(statement, schedule);
                    statement.setString(i, schedule.createdBy);
                    statement.executeUpdate();
                    try (ResultSet keys = statement.getGeneratedKeys()) {
                        if (!keys.next())
                            throw new SQLException("No id generated for the league schedule");
                        return keys.getInt(1);
                    }
                }
            } else {
                String sql = "update league_schedule set name = ?, league_type = ?, template = ?, events = ?, name_pattern = ?, next_event_date = ?, "
                        + "next_event_index = ?, interval_months = ?, lead_days = ?, enabled = ?, last_error = null where id = ?";
                try (PreparedStatement statement = conn.prepareStatement(sql)) {
                    int i = bindDefinition(statement, schedule);
                    statement.setInt(i, schedule.id);
                    statement.executeUpdate();
                    return schedule.id;
                }
            }
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to save league schedule", exp);
        }
    }

    /** Binds the ten definition columns in the order the insert and update statements name them; returns the next index. */
    private static int bindDefinition(PreparedStatement statement, LeagueScheduleRow schedule) throws SQLException {
        statement.setString(1, schedule.name);
        statement.setString(2, schedule.leagueType);
        statement.setString(3, schedule.template);
        statement.setString(4, schedule.events);
        statement.setString(5, schedule.namePattern);
        statement.setInt(6, schedule.nextEventDate);
        statement.setInt(7, schedule.nextEventIndex);
        statement.setDouble(8, schedule.intervalMonths);
        statement.setInt(9, schedule.leadDays);
        statement.setBoolean(10, schedule.enabled);
        return 11;
    }

    @Override
    public void setEnabled(int id, boolean enabled) {
        try (Connection conn = _dbAccess.getDataSource().getConnection();
             PreparedStatement statement = conn.prepareStatement("update league_schedule set enabled = ?, last_error = null where id = ?")) {
            statement.setBoolean(1, enabled);
            statement.setInt(2, id);
            statement.executeUpdate();
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to update league schedule " + id, exp);
        }
    }

    @Override
    public void updateProgress(LeagueScheduleRow schedule) {
        String sql = "update league_schedule set next_event_date = ?, next_event_index = ?, last_created_league_type = ?, last_run = ?, last_error = ? where id = ?";
        try (Connection conn = _dbAccess.getDataSource().getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, schedule.nextEventDate);
            statement.setInt(2, schedule.nextEventIndex);
            statement.setString(3, schedule.lastCreatedLeagueType);
            statement.setTimestamp(4, schedule.lastRun == null ? null : new Timestamp(schedule.lastRun.getTime()));
            statement.setString(5, schedule.lastError);
            statement.setInt(6, schedule.id);
            statement.executeUpdate();
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to update league schedule progress", exp);
        }
    }

    @Override
    public void deleteSchedule(int id) {
        try (Connection conn = _dbAccess.getDataSource().getConnection()) {
            // leagues the schedule already created stay, but no longer point at a schedule that does not exist
            try (PreparedStatement statement = conn.prepareStatement("update league set schedule_id = null where schedule_id = ?")) {
                statement.setInt(1, id);
                statement.executeUpdate();
            }
            try (PreparedStatement statement = conn.prepareStatement("delete from league_schedule where id = ?")) {
                statement.setInt(1, id);
                statement.executeUpdate();
            }
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to delete league schedule " + id, exp);
        }
    }

    private static LeagueScheduleRow read(ResultSet rs) throws SQLException {
        LeagueScheduleRow row = new LeagueScheduleRow();
        row.id = rs.getInt(1);
        row.name = rs.getString(2);
        row.leagueType = rs.getString(3);
        row.template = rs.getString(4);
        row.events = rs.getString(5);
        row.namePattern = rs.getString(6);
        row.nextEventDate = rs.getInt(7);
        row.nextEventIndex = rs.getInt(8);
        row.intervalMonths = rs.getDouble(9);
        row.leadDays = rs.getInt(10);
        row.enabled = rs.getBoolean(11);
        row.lastCreatedLeagueType = rs.getString(12);
        Timestamp lastRun = rs.getTimestamp(13);
        row.lastRun = lastRun == null ? null : new java.util.Date(lastRun.getTime());
        row.lastError = rs.getString(14);
        row.createdBy = rs.getString(15);
        Timestamp createdAt = rs.getTimestamp(16);
        row.createdAt = createdAt == null ? null : new java.util.Date(createdAt.getTime());
        return row;
    }
}
