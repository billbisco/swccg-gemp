package com.gempukku.swccgo.db;

import com.gempukku.swccgo.tournament.Tournament;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * The SQL behind the Events tab's calendar and completed-event browser (DbLeagueDAO / DbTournamentDAO), kept apart
 * from the DAOs so the statements and their bound values can be unit-tested without a database, as
 * {@link GameHistoryQuery} is.  Every value is bound as a positional parameter; the SQL text is fixed.
 * <p>
 * {@code league.start} / {@code league.end} are {@code yyyyMMdd} integers and {@code tournament.start} is epoch
 * milliseconds (see {@code EventDates}).  Ranges on an end / start column are half-open: [from, to).
 */
public final class EventQuery {
    static final String LEAGUE_COLUMNS = "name, type, class, parameters, status, cost, allowSpectators, allowTimeExtensions, "
            + "showPlayerNames, decisionTimeoutSeconds, timePerPlayerMinutes, invitationOnly, registrationInfo, lockedDeckType, "
            + "start, end";
    static final String TOURNAMENT_COLUMNS = "tournament_id, draft_type, name, format, collection, stage, pairing, round, prizes, start";
    /** One day in milliseconds: {@code FLOOR(start / 86400000)} is the tournament's UTC day counted from the epoch. */
    static final long DAY_MS = 86400000L;

    private final String _sql;
    private final List<Object> _parameters;

    private EventQuery(String sql, List<Object> parameters) {
        _sql = sql;
        _parameters = Collections.unmodifiableList(parameters);
    }

    public String sql() {
        return _sql;
    }

    public List<Object> parameters() {
        return _parameters;
    }

    /** Binds the parameters, in order, to a statement prepared from {@link #sql()}. */
    public void bind(PreparedStatement statement) throws SQLException {
        for (int i = 0; i < _parameters.size(); i++) {
            Object value = _parameters.get(i);
            if (value instanceof Integer)
                statement.setInt(i + 1, (Integer) value);
            else if (value instanceof Long)
                statement.setLong(i + 1, (Long) value);
            else
                statement.setString(i + 1, String.valueOf(value));
        }
    }

    // ---- leagues ----

    public static EventQuery leagueByType(String type) {
        return new EventQuery("select " + LEAGUE_COLUMNS + " from league where type = ?", List.of(type));
    }

    /** Leagues whose stored span touches the inclusive day range [from, to]. */
    public static EventQuery leaguesOverlapping(int from, int to) {
        return new EventQuery("select " + LEAGUE_COLUMNS + " from league where start <= ? and end >= ? order by start, name",
                List.of(to, from));
    }

    /** Leagues ending in [from, to), latest end first. */
    public static EventQuery leaguesEndingBetween(int from, int to) {
        return new EventQuery("select " + LEAGUE_COLUMNS + " from league where end >= ? and end < ? order by end desc, start desc",
                List.of(from, to));
    }

    /** (league type, player count) for the leagues ending in [from, to). */
    public static EventQuery leagueParticipantCounts(int from, int to) {
        return new EventQuery("select lp.league_type, count(*) from league_participation lp "
                + "join league l on l.type = lp.league_type where l.end >= ? and l.end < ? group by lp.league_type",
                List.of(from, to));
    }

    /** The distinct yyyyMM months ({@code end DIV 100}) of the leagues that ended before the day given. */
    public static EventQuery leagueEndMonths(int before) {
        return new EventQuery("select distinct (end div 100) from league where end < ?", List.of(before));
    }

    // ---- tournaments ----

    /** Tournaments of every stage starting in [from, to) milliseconds, earliest first. */
    public static EventQuery tournamentsStartingBetween(long from, long to) {
        return new EventQuery("select " + TOURNAMENT_COLUMNS + " from tournament where start >= ? and start < ? order by start",
                List.of(from, to));
    }

    /** Finished tournaments starting in [from, to) milliseconds, latest first. */
    public static EventQuery finishedTournamentsBetween(long from, long to) {
        return new EventQuery("select " + TOURNAMENT_COLUMNS + " from tournament where stage = ? and start >= ? and start < ? order by start desc",
                List.of(Tournament.Stage.FINISHED.name(), from, to));
    }

    /** (tournament id, player count) for the finished tournaments starting in [from, to) milliseconds. */
    public static EventQuery finishedTournamentPlayerCounts(long from, long to) {
        return new EventQuery("select tp.tournament_id, count(*) from tournament_player tp "
                + "join tournament t on t.tournament_id = tp.tournament_id where t.stage = ? and t.start >= ? and t.start < ? "
                + "group by tp.tournament_id",
                List.of(Tournament.Stage.FINISHED.name(), from, to));
    }

    /**
     * The distinct UTC days (counted from the epoch) on which a finished tournament with at least one round played
     * started; the months are derived from those in Java, so the database's time zone never enters into it.
     */
    public static EventQuery finishedTournamentDays() {
        return new EventQuery("select distinct floor(start / " + DAY_MS + ") from tournament where stage = ? and round > 0",
                List.of(Tournament.Stage.FINISHED.name()));
    }

    /** The ids of the tournaments starting in [from, to) milliseconds that the player is (or was) entered in. */
    public static EventQuery tournamentsPlayedBetween(String player, long from, long to) {
        return new EventQuery("select tp.tournament_id from tournament_player tp "
                + "join tournament t on t.tournament_id = tp.tournament_id where tp.player = ? and t.start >= ? and t.start < ?",
                List.of(player, from, to));
    }
}
