package com.gempukku.swccgo.db;

import com.gempukku.swccgo.DateUtils;
import com.gempukku.swccgo.db.vo.CollectionType;
import com.gempukku.swccgo.tournament.Tournament;
import com.gempukku.swccgo.tournament.TournamentDAO;
import com.gempukku.swccgo.tournament.TournamentInfo;
import com.gempukku.swccgo.tournament.TournamentQueueInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DbTournamentDAO implements TournamentDAO {
    private DbAccess _dbAccess;

    public DbTournamentDAO(DbAccess dbAccess) {
        _dbAccess = dbAccess;
    }

    @Override
    public void addTournament(String tournamentId, String draftType, String tournamentName, String format, CollectionType collectionType, Tournament.Stage stage, String pairingMechanism, String prizesScheme, Date start) {
        try {
            Connection conn = _dbAccess.getDataSource().getConnection();
            try {
                PreparedStatement statement = conn.prepareStatement("insert into tournament (tournament_id, draft_type, name, format, collection, stage, pairing, start, round, prizes) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                try {
                    statement.setString(1, tournamentId);
                    statement.setString(2, draftType);
                    statement.setString(3, tournamentName);
                    statement.setString(4, format);
                    statement.setString(5, collectionType.getCode()+":"+collectionType.getFullName());
                    statement.setString(6, stage.name());
                    statement.setString(7, pairingMechanism);
                    statement.setLong(8, start.getTime());
                    statement.setInt(9, 0);
                    statement.setString(10, prizesScheme);
                    statement.execute();
                } finally {
                    statement.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException exp) {
            throw new RuntimeException(exp);
        }
    }

    @Override
    public TournamentInfo getTournamentById(String tournamentId) {
        try {
            Connection connection = _dbAccess.getDataSource().getConnection();
            try {
                PreparedStatement statement = connection.prepareStatement("select draft_type, name, format, collection, stage, pairing, round, prizes, start from tournament where tournament_id=?");
                try {
                    statement.setString(1, tournamentId);
                    ResultSet rs = statement.executeQuery();
                    try {
                        if (rs.next()) {
                            String[] collectionTypeStr = rs.getString(4).split(":", 2);
                            return new TournamentInfo(
                                    tournamentId, rs.getString(1), rs.getString(2), rs.getString(3),
                                    new CollectionType(collectionTypeStr[0], collectionTypeStr[1]), Tournament.Stage.valueOf(rs.getString(5)),
                                    rs.getString(6), rs.getString(8), rs.getInt(7), rs.getLong(9));
                        } else
                            return null;
                    } finally {
                        rs.close();
                    }
                } finally {
                    statement.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException exp) {
            throw new RuntimeException(exp);
        }
    }

    @Override
    public List<TournamentInfo> getUnfinishedTournaments() {
        try {
            Connection connection = _dbAccess.getDataSource().getConnection();
            try {
                PreparedStatement statement = connection.prepareStatement("select tournament_id, draft_type, name, format, collection, stage, pairing, round, prizes from tournament where stage <> '"+ Tournament.Stage.FINISHED.name()+"'");
                try {
                    ResultSet rs = statement.executeQuery();
                    try {
                        List<TournamentInfo> result = new ArrayList<TournamentInfo>();
                        while (rs.next()) {
                            result.add(readTournament(rs));
                        }
                        return result;
                    } finally {
                        rs.close();
                    }
                } finally {
                    statement.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException exp) {
            throw new RuntimeException(exp);
        }
    }

    @Override
    public List<TournamentInfo> getFinishedTournamentsSince(long time) {
        try {
            Connection connection = _dbAccess.getDataSource().getConnection();
            try {
                PreparedStatement statement = connection.prepareStatement("select tournament_id, draft_type, name, format, collection, stage, pairing, round, prizes from tournament where stage = '"+ Tournament.Stage.FINISHED.name()+"' and start>?");
                try {
                    statement.setLong(1, time);
                    ResultSet rs = statement.executeQuery();
                    try {
                        List<TournamentInfo> result = new ArrayList<TournamentInfo>();
                         while (rs.next()) {
                            result.add(readTournament(rs));
                        }
                        return result;
                    } finally {
                        rs.close();
                    }
                } finally {
                    statement.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException exp) {
            throw new RuntimeException(exp);
        }
    }

    @Override
    public void updateTournamentStage(String tournamentId, Tournament.Stage stage) {
        try {
            Connection conn = _dbAccess.getDataSource().getConnection();
            try {
                PreparedStatement statement = conn.prepareStatement("update tournament set stage=? where tournament_id=?");
                try {
                    statement.setString(1, stage.name());
                    statement.setString(2, tournamentId);
                    statement.executeUpdate();
                } finally {
                    statement.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException exp) {
            throw new RuntimeException(exp);
        }
    }

    @Override
    public void updateTournamentRound(String tournamentId, int round) {
        try {
            Connection conn = _dbAccess.getDataSource().getConnection();
            try {
                PreparedStatement statement = conn.prepareStatement("update tournament set round=? where tournament_id=?");
                try {
                    statement.setInt(1, round);
                    statement.setString(2, tournamentId);
                    statement.executeUpdate();
                } finally {
                    statement.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException exp) {
            throw new RuntimeException(exp);
        }
    }

    /** One row of {@link EventQuery#TOURNAMENT_COLUMNS} (also the column order of the older selects above). */
    private static TournamentInfo readTournament(ResultSet rs) throws SQLException {
        String collection = rs.getString(5);
        String[] collectionTypeStr = (collection == null ? "" : collection).split(":", 2);
        return new TournamentInfo(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
                new CollectionType(collectionTypeStr[0], collectionTypeStr.length > 1 ? collectionTypeStr[1] : collectionTypeStr[0]),
                Tournament.Stage.valueOf(rs.getString(6)), rs.getString(7), rs.getString(9), rs.getInt(8), rs.getLong(10));
    }

    // ---- the Events tab (calendar and completed-event browser) ----

    @Override
    public List<TournamentInfo> getTournamentsStartingBetween(long from, long to) {
        return loadTournaments(EventQuery.tournamentsStartingBetween(from, to));
    }

    @Override
    public List<TournamentInfo> getFinishedTournamentsBetween(long from, long to) {
        return loadTournaments(EventQuery.finishedTournamentsBetween(from, to));
    }

    @Override
    public Map<String, Integer> getFinishedTournamentPlayerCountsBetween(long from, long to) {
        EventQuery query = EventQuery.finishedTournamentPlayerCounts(from, to);
        try (Connection conn = _dbAccess.getDataSource().getConnection();
             PreparedStatement statement = conn.prepareStatement(query.sql())) {
            query.bind(statement);
            try (ResultSet rs = statement.executeQuery()) {
                Map<String, Integer> counts = new HashMap<>();
                while (rs.next())
                    counts.put(rs.getString(1), rs.getInt(2));
                return counts;
            }
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to count tournament players", exp);
        }
    }

    @Override
    public List<String> getFinishedTournamentMonths() {
        EventQuery query = EventQuery.finishedTournamentDays();
        try (Connection conn = _dbAccess.getDataSource().getConnection();
             PreparedStatement statement = conn.prepareStatement(query.sql())) {
            query.bind(statement);
            try (ResultSet rs = statement.executeQuery()) {
                Set<String> months = new HashSet<>();
                while (rs.next())
                    months.add(com.gempukku.swccgo.events.EventDates.monthKeyOfEpochDay(rs.getLong(1)));
                return new ArrayList<>(months);
            }
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to list the months with finished tournaments", exp);
        }
    }

    @Override
    public Set<String> getTournamentsPlayedBetween(String player, long from, long to) {
        EventQuery query = EventQuery.tournamentsPlayedBetween(player, from, to);
        try (Connection conn = _dbAccess.getDataSource().getConnection();
             PreparedStatement statement = conn.prepareStatement(query.sql())) {
            query.bind(statement);
            try (ResultSet rs = statement.executeQuery()) {
                Set<String> ids = new HashSet<>();
                while (rs.next())
                    ids.add(rs.getString(1));
                return ids;
            }
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to list the tournaments of " + player, exp);
        }
    }

    private List<TournamentInfo> loadTournaments(EventQuery query) {
        try (Connection conn = _dbAccess.getDataSource().getConnection();
             PreparedStatement statement = conn.prepareStatement(query.sql())) {
            query.bind(statement);
            try (ResultSet rs = statement.executeQuery()) {
                List<TournamentInfo> result = new ArrayList<>();
                while (rs.next())
                    result.add(readTournament(rs));
                return result;
            }
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to load tournaments", exp);
        }
    }

    public List<TournamentQueueInfo> getUnstartedScheduledTournamentQueues(long tillDate) {
        try {
            Connection connection = _dbAccess.getDataSource().getConnection();
            try {
                PreparedStatement statement = connection.prepareStatement("select tournament_id, name, format, start, cost, playoff, prizes, minimum_players from scheduled_tournament where started = 0 and start<=?");
                try {
                    statement.setLong(1, tillDate);
                    ResultSet rs = statement.executeQuery();
                    try {
                        List<TournamentQueueInfo> result = new ArrayList<TournamentQueueInfo>();
                        while (rs.next()) {
                            result.add(new TournamentQueueInfo(rs.getString(1), rs.getString(2), rs.getString(3), rs.getLong(4),
                                    rs.getInt(5), rs.getString(6), rs.getString(7), rs.getInt(8)));
                        }
                        return result;
                    } finally {
                        rs.close();
                    }
                } finally {
                    statement.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException exp) {
            throw new RuntimeException(exp);
        }
    }

    @Override
    public void updateScheduledTournamentStarted(String scheduledTournamentId) {
        try {
            Connection conn = _dbAccess.getDataSource().getConnection();
            try {
                PreparedStatement statement = conn.prepareStatement("update scheduled_tournament set started=1 where tournament_id=?");
                try {
                    statement.setString(1, scheduledTournamentId);
                    statement.executeUpdate();
                } finally {
                    statement.close();
                }
            } finally {
                conn.close();
            }
        } catch (SQLException exp) {
            throw new RuntimeException(exp);
        }
    }

    public static void main(String[] args) throws ParseException {
        System.out.println(DateUtils.parseDateWithHour("2013-01-05 17:30:00").getTime());
    }
}
