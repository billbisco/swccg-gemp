package com.gempukku.swccgo.db;

import com.gempukku.swccgo.db.vo.League;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.league.LockedDeckType;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DbLeagueDAO implements LeagueDAO {
    private DbAccess _dbAccess;

    public DbLeagueDAO(DbAccess dbAccess) {
        _dbAccess = dbAccess;
    }

    public void addLeague(int cost, String name, String type, String clazz, String parameters, int start, int endTime, boolean allowSpectators, boolean allowTimeExtensions, boolean showPlayerNames, boolean invitationOnly, String registrationInfo, int decisionTimeoutSeconds, int timePerPlayerMinutes, LockedDeckType lockedDeckType) throws SQLException, IOException {
        Connection conn = _dbAccess.getDataSource().getConnection();
        try {
            PreparedStatement statement = conn.prepareStatement("insert into league (name, type, class, parameters, start, end, status, cost, allowSpectators, allowTimeExtensions, showPlayerNames, decisionTimeoutSeconds, timePerPlayerMinutes, invitationOnly, registrationInfo, lockedDeckType) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            try {
                statement.setString(1, name);
                statement.setString(2, type);
                statement.setString(3, clazz);
                statement.setString(4, parameters);
                statement.setInt(5, start);
                statement.setInt(6, endTime);
                statement.setInt(7, 0);
                statement.setInt(8, cost);
                statement.setBoolean(9, allowSpectators);
                statement.setBoolean(10, allowTimeExtensions);
                statement.setBoolean(11, showPlayerNames);
                statement.setInt(12, decisionTimeoutSeconds);
                statement.setInt(13, timePerPlayerMinutes);
                statement.setBoolean(14, invitationOnly);
                statement.setString(15, registrationInfo);
                statement.setString(16, lockedDeckType == null ? null : lockedDeckType.getCode());
                statement.execute();
            } finally {
                statement.close();
            }
        } finally {
            conn.close();
        }
    }

    public List<League> loadActiveLeagues(SwccgCardBlueprintLibrary library, int currentTime) throws SQLException, IOException {
        Connection conn = _dbAccess.getDataSource().getConnection();
        try {
            PreparedStatement statement = conn.prepareStatement("select name, type, class, parameters, status, cost, allowSpectators, allowTimeExtensions, showPlayerNames, decisionTimeoutSeconds, timePerPlayerMinutes, invitationOnly, registrationInfo, lockedDeckType from league where end>=? order by start desc");
            try {
                statement.setInt(1, currentTime);
                ResultSet rs = statement.executeQuery();
                try {
                    List<League> activeLeagues = new ArrayList<League>();
                    while (rs.next()) {
                        String name = rs.getString(1);
                        String type = rs.getString(2);
                        String clazz = rs.getString(3);
                        String parameters = rs.getString(4);
                        int status = rs.getInt(5);
                        int cost = rs.getInt(6);
                        boolean allowSpectators = rs.getBoolean(7);
                        boolean allowTimeExtensions = rs.getBoolean(8);
                        boolean showPlayerNames = rs.getBoolean(9);
                        int decisionTimeoutSeconds = rs.getInt(10);
                        int timePerPlayerMinutes = rs.getInt(11);
                        boolean invitationOnly = rs.getBoolean(12);
                        String registrationInfo = rs.getString(13);
                        LockedDeckType lockedDeckType = LockedDeckType.getLockedDeckType(rs.getString(14));
                        activeLeagues.add(new League(library, cost, name, type, clazz, parameters, status, allowSpectators, allowTimeExtensions, showPlayerNames, invitationOnly, registrationInfo, decisionTimeoutSeconds, timePerPlayerMinutes, lockedDeckType));
                    }
                    return activeLeagues;
                } finally {
                    rs.close();
                }
            } finally {
                statement.close();
            }
        } finally {
            conn.close();
        }
    }

    // ---- the Events tab (calendar and completed-event browser) ----

    @Override
    public League loadLeagueByType(SwccgCardBlueprintLibrary library, String type) throws SQLException {
        List<League> leagues = loadLeagues(library, EventQuery.leagueByType(type));
        return leagues.isEmpty() ? null : leagues.get(0);
    }

    @Override
    public List<League> loadLeaguesOverlapping(SwccgCardBlueprintLibrary library, int from, int to) throws SQLException {
        return loadLeagues(library, EventQuery.leaguesOverlapping(from, to));
    }

    @Override
    public List<League> loadLeaguesEndingBetween(SwccgCardBlueprintLibrary library, int from, int to) throws SQLException {
        return loadLeagues(library, EventQuery.leaguesEndingBetween(from, to));
    }

    @Override
    public Map<String, Integer> getParticipantCountsForLeaguesEndingBetween(int from, int to) throws SQLException {
        EventQuery query = EventQuery.leagueParticipantCounts(from, to);
        try (Connection conn = _dbAccess.getDataSource().getConnection();
             PreparedStatement statement = conn.prepareStatement(query.sql())) {
            query.bind(statement);
            try (ResultSet rs = statement.executeQuery()) {
                Map<String, Integer> counts = new HashMap<>();
                while (rs.next())
                    counts.put(rs.getString(1), rs.getInt(2));
                return counts;
            }
        }
    }

    @Override
    public List<String> getLeagueEndMonths(int before) throws SQLException {
        EventQuery query = EventQuery.leagueEndMonths(before);
        try (Connection conn = _dbAccess.getDataSource().getConnection();
             PreparedStatement statement = conn.prepareStatement(query.sql())) {
            query.bind(statement);
            try (ResultSet rs = statement.executeQuery()) {
                List<String> months = new ArrayList<>();
                while (rs.next()) {
                    int yyyymm = rs.getInt(1);
                    months.add(String.format("%04d-%02d", yyyymm / 100, yyyymm % 100));
                }
                return months;
            }
        }
    }

    /** Runs a query selecting {@link EventQuery#LEAGUE_COLUMNS} and maps every row to a League (with its stored start / end). */
    private List<League> loadLeagues(SwccgCardBlueprintLibrary library, EventQuery query) throws SQLException {
        try (Connection conn = _dbAccess.getDataSource().getConnection();
             PreparedStatement statement = conn.prepareStatement(query.sql())) {
            query.bind(statement);
            try (ResultSet rs = statement.executeQuery()) {
                List<League> leagues = new ArrayList<>();
                while (rs.next()) {
                    leagues.add(new League(library, rs.getInt(6), rs.getString(1), rs.getString(2), rs.getString(3),
                            rs.getString(4), rs.getInt(5), rs.getBoolean(7), rs.getBoolean(8), rs.getBoolean(9),
                            rs.getBoolean(12), rs.getString(13), rs.getInt(10), rs.getInt(11),
                            LockedDeckType.getLockedDeckType(rs.getString(14)), rs.getInt(15), rs.getInt(16)));
                }
                return leagues;
            }
        }
    }

    public void setStatus(League league, int newStatus) {
        try {
            Connection connection = _dbAccess.getDataSource().getConnection();
            try {
                String sql = "update league set status=? where type=?";

                PreparedStatement statement = connection.prepareStatement(sql);
                try {
                    statement.setInt(1, newStatus);
                    statement.setString(2, league.getType());
                    statement.execute();
                } finally {
                    statement.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to update league status", exp);
        }
    }
}
