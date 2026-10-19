package com.gempukku.swccgo.db;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Builds the SQL for one player's filtered game history.  Every value the player supplies is bound as a positional
 * parameter; the SQL text is put together only from the fixed fragments below, so no filter value can change the
 * statement.  Kept apart from {@link DbGameHistoryDAO} so the statements can be unit-tested without a database.
 * <p>
 * game_history uses a binary (case-sensitive) collation, so the case-insensitive matches compare LOWER(column) with a
 * value lower-cased here.  LIKE patterns escape the player's own % and _ with '!'.  start_date / end_date hold epoch
 * milliseconds, so the day filters bind UTC midnight as a long.
 */
public final class GameHistoryQuery {
    static final String COLUMNS = "winner, loser, win_reason, lose_reason, win_recording_id, lose_recording_id, format_name, "
            + "tournament, winner_deck_name, winner_deck_archetype, loser_deck_name, loser_deck_archetype, start_date, end_date, "
            + "winner_side, league_type";
    /** Casual games: no event, or "Casual" / "Casual (Private)" (the same split My Stats uses). */
    static final String CASUAL_CONDITION = "(tournament IS NULL OR tournament LIKE 'Casual%')";
    static final char LIKE_ESCAPE = '!';

    private final String _where;
    private final List<Object> _parameters;

    private GameHistoryQuery(String where, List<Object> parameters) {
        _where = where;
        _parameters = Collections.unmodifiableList(parameters);
    }

    public static GameHistoryQuery forPlayer(String playerName, GameHistoryFilter filter) {
        if (filter == null)
            filter = GameHistoryFilter.NONE;
        List<Object> params = new ArrayList<>();
        List<String> conditions = new ArrayList<>();

        conditions.add("(winner = ? OR loser = ?)");
        params.add(playerName);
        params.add(playerName);

        if (filter.format() != null) {
            conditions.add("format_name = ?");
            params.add(filter.format());
        }
        if (filter.opponent() != null) {
            String opponent = filter.opponent().toLowerCase(Locale.ROOT);
            String value = filter.opponentExact() ? opponent : escapeLike(opponent) + "%";
            String match = filter.opponentExact() ? "= ?" : "LIKE ? ESCAPE '!'";
            conditions.add("((winner = ? AND LOWER(loser) " + match + ") OR (loser = ? AND LOWER(winner) " + match + "))");
            params.add(playerName);
            params.add(value);
            params.add(playerName);
            params.add(value);
        }
        if (filter.casualOnly()) {
            conditions.add(CASUAL_CONDITION);
        } else if (filter.event() != null) {
            conditions.add("LOWER(tournament) LIKE ? ESCAPE '!'");
            params.add(escapeLike(filter.event().toLowerCase(Locale.ROOT)) + "%");
        }
        if (filter.side() != null) {
            // the player's side: the winner's side when they won, the other one when they lost
            String other = GameHistoryFilter.DARK.equals(filter.side()) ? GameHistoryFilter.LIGHT : GameHistoryFilter.DARK;
            conditions.add("((winner = ? AND winner_side = ?) OR (loser = ? AND winner_side = ?))");
            params.add(playerName);
            params.add(filter.side());
            params.add(playerName);
            params.add(other);
        }
        if (filter.result() != null) {
            conditions.add(GameHistoryFilter.WON.equals(filter.result()) ? "winner = ?" : "loser = ?");
            params.add(playerName);
        }
        if (filter.from() != null) {
            conditions.add("end_date >= ?");
            params.add(startOfDayMillis(filter.from()));
        }
        if (filter.to() != null) {
            conditions.add("end_date < ?");
            params.add(startOfDayMillis(filter.to().plusDays(1)));
        }
        return new GameHistoryQuery(String.join(" AND ", conditions), params);
    }

    /** One page, newest first; bind {@link #parameters()} and then the offset and the page size. */
    public String selectSql() {
        return "SELECT " + COLUMNS + " FROM game_history WHERE " + _where + " ORDER BY end_date DESC, id DESC LIMIT ?, ?";
    }

    public String countSql() {
        return "SELECT COUNT(*) FROM game_history WHERE " + _where;
    }

    public String where() {
        return _where;
    }

    /** Positional parameters for the WHERE clause, in order. */
    public List<Object> parameters() {
        return _parameters;
    }

    /** The formats the player has games in, most played first.  Binds the player name twice. */
    public static String formatsSql() {
        return "SELECT format_name FROM game_history WHERE (winner = ? OR loser = ?) AND format_name IS NOT NULL"
                + " GROUP BY format_name ORDER BY COUNT(*) DESC, format_name";
    }

    /**
     * The leagues and tournaments (not casual or playtesting) the player has games in, most recent first.  Binds the
     * player name twice and then the limit.
     */
    public static String eventsSql() {
        return "SELECT tournament FROM game_history WHERE (winner = ? OR loser = ?) AND tournament IS NOT NULL"
                + " AND tournament NOT LIKE 'Casual%' AND tournament NOT LIKE 'Playtesting%'"
                + " GROUP BY tournament ORDER BY MAX(end_date) DESC LIMIT ?";
    }

    static String escapeLike(String value) {
        StringBuilder result = new StringBuilder(value.length() + 4);
        for (char c : value.toCharArray()) {
            if (c == LIKE_ESCAPE || c == '%' || c == '_')
                result.append(LIKE_ESCAPE);
            result.append(c);
        }
        return result.toString();
    }

    static long startOfDayMillis(LocalDate day) {
        return day.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
    }
}
