package com.gempukku.swccgo.events;

import com.gempukku.swccgo.cache.Cached;
import com.gempukku.swccgo.db.LeagueDAO;
import com.gempukku.swccgo.db.vo.League;
import com.gempukku.swccgo.draft2.SoloDraftDefinitions;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.game.SwccgFormat;
import com.gempukku.swccgo.game.formats.SwccgoFormatLibrary;
import com.gempukku.swccgo.league.LeagueSeriesData;
import com.gempukku.swccgo.tournament.TournamentDAO;
import com.gempukku.swccgo.tournament.TournamentInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * One month of completed events at a time, for the event browser (GET /eventHistory).  Ported from the LOTR GEMP
 * EventHistoryService and rewritten for this database: league days are {@code yyyyMMdd} integers and tournament
 * starts are epoch milliseconds (see {@link EventDates}).
 * <p>
 * <b>The bucketing is deliberately asymmetric.</b>  {@code league} has a real {@code end} day, so leagues are
 * bucketed by the month they <i>ended</i> in.  {@code tournament} has no finish column at all - the only temporal
 * column is {@code start}, and "finished" is {@code stage = 'FINISHED'} - so tournaments are bucketed by the month
 * they <i>started</i> in.  In practice a tournament starts and finishes the same day, so the two agree; a league
 * spanning March into April, however, appears only under April.
 * <p>
 * Everything is read from raw rows.  Hydrating a {@code Tournament} costs several queries and deserialises every
 * stored decklist, which a month listing must never pay, and it would also disturb the live tournament cache.
 * <p>
 * A single bad row must never take out a whole month: a league whose stored definition can no longer be built and a
 * format code that has since been retired are caught per event and either degraded (the raw format code stands in
 * for its name) or skipped with a warning.
 */
public class EventHistoryService implements Cached {
    private static final Logger _log = LogManager.getLogger(EventHistoryService.class);

    public static final String KIND_LEAGUE = "league";
    public static final String KIND_TOURNAMENT = "tournament";
    /** What a format is called when neither a name nor a code is known. */
    public static final String UNKNOWN_FORMAT = "Unknown";

    private final LeagueDAO _leagueDao;
    private final TournamentDAO _tournamentDao;
    private final SwccgCardBlueprintLibrary _library;
    private final SwccgoFormatLibrary _formatLibrary;
    private final SoloDraftDefinitions _soloDraftDefinitions;

    /**
     * Keyed by {@link #cacheKey} - "yyyy-MM/kind" - so that a tournament panel never pays for the league queries and
     * vice versa.  Values are immutable lists of plain value objects, never live domain objects.
     */
    private final Map<String, List<EventSummary>> _months = new ConcurrentHashMap<>();
    /** Keyed by kind: the months holding at least one completed event of that kind, newest first. */
    private final Map<String, List<String>> _availableMonths = new ConcurrentHashMap<>();
    /**
     * When each "live" entry was cached: the month lists, and the current month's events.  Those change as events
     * finish, so they are re-read after {@link #LIVE_TTL_MS}; a month that is over never changes and is kept.
     */
    private final Map<String, Long> _liveCachedAt = new ConcurrentHashMap<>();
    static final long LIVE_TTL_MS = 5 * 60 * 1000L;

    public EventHistoryService(LeagueDAO leagueDao, TournamentDAO tournamentDao, SwccgCardBlueprintLibrary library,
                               SwccgoFormatLibrary formatLibrary, SoloDraftDefinitions soloDraftDefinitions) {
        _leagueDao = leagueDao;
        _tournamentDao = tournamentDao;
        _library = library;
        _formatLibrary = formatLibrary;
        _soloDraftDefinitions = soloDraftDefinitions;
    }

    @Override
    public void clearCache() {
        _months.clear();
        _availableMonths.clear();
        _liveCachedAt.clear();
    }

    private boolean isFresh(String liveKey) {
        Long at = _liveCachedAt.get(liveKey);
        return at != null && System.currentTimeMillis() - at < LIVE_TTL_MS;
    }

    @Override
    public int getItemCount() {
        return _months.size() + _availableMonths.size();
    }

    /**
     * The canonical kind ({@link #KIND_LEAGUE} or {@link #KIND_TOURNAMENT}), or null when the text is missing or
     * names neither - which the handler turns into a 400.
     */
    public static String parseKind(String value) {
        if (value == null)
            return null;
        String kind = value.trim().toLowerCase(Locale.ROOT);
        return KIND_LEAGUE.equals(kind) || KIND_TOURNAMENT.equals(kind) ? kind : null;
    }

    /**
     * A month, or null when the text is missing or is not a {@code yyyy-MM} month - which the handler turns into a
     * 400.  Here rather than in the handler so that it can be tested: the async module has no test tree.
     */
    public static YearMonth parseMonth(String value) {
        if (value == null || value.isBlank())
            return null;
        try {
            return YearMonth.parse(value.trim());
        } catch (DateTimeParseException exp) {
            return null;
        }
    }

    /**
     * Every month holding at least one completed event of {@code kind}, newest first.  Cached per kind for
     * {@link #LIVE_TTL_MS}, so a new month appears once its first event completes.  A failing query propagates (the
     * endpoint then fails) and nothing is cached, rather than caching an empty list that would hide every month until
     * the next cache clear.
     */
    public List<String> getAvailableMonths(String kind) {
        requireKind(kind);
        List<String> cached = _availableMonths.get(kind);
        if (cached != null && isFresh("months/" + kind))
            return cached;

        List<String> rows;
        try {
            rows = KIND_TOURNAMENT.equals(kind)
                    ? _tournamentDao.getFinishedTournamentMonths()
                    : _leagueDao.getLeagueEndMonths(today());
        } catch (java.sql.SQLException exp) {
            throw new RuntimeException("Unable to list the months with completed " + kind + "s", exp);
        }

        var months = new TreeSet<String>(Comparator.reverseOrder());
        if (rows != null) {
            for (String month : rows) {
                if (month != null && !month.isBlank())
                    months.add(month);
            }
        }

        List<String> result = List.copyOf(months);
        _availableMonths.put(kind, result);
        _liveCachedAt.put("months/" + kind, System.currentTimeMillis());
        return result;
    }

    /**
     * Every completed event of {@code kind} in the given month, sorted by start date descending and then by name.
     * Cached per (month, kind): the second call does not query.  A finished month is cached for good; the current
     * month is re-read every {@link #LIVE_TTL_MS}, so events finishing later in it appear within minutes.
     * <p>
     * If the month's main query fails, the exception propagates and nothing is cached.  If only the player counts
     * cannot be read, the events are returned with null counts but not cached, so the next request tries again.
     */
    public List<EventSummary> getMonth(YearMonth month, String kind) {
        requireKind(kind);
        String key = cacheKey(month, kind);
        YearMonth current = YearMonth.from(EventDates.fromDateInt(today()));
        boolean over = month.isBefore(current);
        List<EventSummary> cached = _months.get(key);
        if (cached != null && (over || isFresh(key)))
            return cached;

        var events = new ArrayList<EventSummary>();
        boolean complete = KIND_TOURNAMENT.equals(kind)
                ? addTournaments(events, month)
                : addLeagues(events, month);

        events.sort(Comparator.comparing((EventSummary event) -> event.startDate).reversed()
                .thenComparing(event -> event.name == null ? "" : event.name));

        List<EventSummary> result = Collections.unmodifiableList(events);
        // a finished month is kept; the current one is re-read after LIVE_TTL_MS; a future one is never cached (the
        // month comes from the request, so caching those would let the map grow without limit)
        if (complete && !month.isAfter(current)) {
            _months.put(key, result);
            if (!over)
                _liveCachedAt.put(key, System.currentTimeMillis());
        }
        return result;
    }

    /**
     * One month of completed events of one kind in the shape the endpoint returns them.  The admin extras are added
     * on the way out rather than cached, so that one cached month serves admins and everyone else alike.
     */
    public List<Map<String, Object>> getMonthEvents(YearMonth month, String kind, boolean isAdmin) {
        var events = new ArrayList<Map<String, Object>>();
        for (EventSummary summary : getMonth(month, kind)) {
            var event = new LinkedHashMap<String, Object>();
            event.put("id", summary.id);
            event.put("name", summary.name);
            event.put("startDate", summary.startDate);
            event.put("endDate", summary.endDate);
            event.put("format", summary.format);
            event.put("playerCount", summary.playerCount);
            event.put("rounds", summary.rounds);
            if (isAdmin)
                event.put("adminLinks", adminLinks(summary));
            events.add(event);
        }
        return events;
    }

    static String cacheKey(YearMonth month, String kind) {
        return month + "/" + kind;
    }

    private static void requireKind(String kind) {
        if (!KIND_LEAGUE.equals(kind) && !KIND_TOURNAMENT.equals(kind))
            throw new IllegalArgumentException("Unknown event kind '" + kind + "'");
    }

    /**
     * The admin-only extras: the event's id (a league's type code, a tournament's id) as text a click selects, for
     * pasting into the admin pages.  Neither has a report page on this server, so neither is a link.  Both go to
     * anyone who is ADMIN or LEAGUE_ADMIN.
     */
    private static List<Map<String, Object>> adminLinks(EventSummary summary) {
        var link = new LinkedHashMap<String, Object>();
        link.put("label", KIND_TOURNAMENT.equals(summary.kind) ? "Id" : "Code");
        link.put("text", summary.id);
        link.put("url", null);
        return List.of(link);
    }

    /** @return false when the player counts could not be read (the rows are then not to be cached) */
    private boolean addTournaments(List<EventSummary> events, YearMonth month) {
        // Half-open range: start is an instant, so an inclusive upper bound would drop every tournament that started
        // after midnight on the last day of the month.
        long from = EventDates.monthStartMillis(month);
        long to = EventDates.nextMonthStartMillis(month);

        List<TournamentInfo> rows = _tournamentDao.getFinishedTournamentsBetween(from, to);
        Map<String, Integer> playerCounts;
        boolean complete = true;
        try {
            playerCounts = _tournamentDao.getFinishedTournamentPlayerCountsBetween(from, to);
        } catch (Exception exp) {
            _log.warn("Unable to count tournament players for " + month + "; counts will be null", exp);
            playerCounts = Map.of();
            complete = false;
        }
        if (rows == null)
            return complete;
        if (playerCounts == null)
            playerCounts = Map.of();

        for (TournamentInfo row : rows) {
            try {
                // A finished tournament with no round played is not a real event (a draft against bots, for
                // instance).  The "finished tournaments" list of the old Events tab excluded these too.
                if (row.getTournamentRound() == 0)
                    continue;

                events.add(new EventSummary(
                        KIND_TOURNAMENT,
                        row.getTournamentId(),
                        row.getTournamentName(),
                        EventDates.isoDateOfMillis(row.getStartTime()),
                        null,   // tournament has no finish column; see the class comment
                        formatName(row.getTournamentFormat(), row.getDraftType()),
                        playerCounts.get(row.getTournamentId()),
                        row.getTournamentRound()));
            } catch (Exception exp) {
                _log.warn("Skipping tournament " + row.getTournamentId() + " in the event history: " + exp.getMessage(), exp);
            }
        }
        return complete;
    }

    /** @return false when the participant counts could not be read (the rows are then not to be cached) */
    private boolean addLeagues(List<EventSummary> events, YearMonth month) {
        int today = today();
        int from = EventDates.monthStart(month);
        int to = EventDates.nextMonthStart(month);

        List<League> rows;
        try {
            rows = _leagueDao.loadLeaguesEndingBetween(_library, from, to);
        } catch (Exception exp) {
            throw new RuntimeException("Unable to load the leagues ending in " + month, exp);
        }
        Map<String, Integer> playerCounts;
        boolean complete = true;
        try {
            playerCounts = _leagueDao.getParticipantCountsForLeaguesEndingBetween(from, to);
        } catch (Exception exp) {
            _log.warn("Unable to count league participants for " + month + "; counts will be null", exp);
            playerCounts = Map.of();
            complete = false;
        }
        if (rows == null)
            return complete;
        if (playerCounts == null)
            playerCounts = Map.of();

        for (League row : rows) {
            try {
                // A league is complete once its end day has passed.  The status column is only ever advanced
                // while the league is still active, so it cannot be used to answer this.
                if (row.getEnd() <= 0 || row.getEnd() >= today)
                    continue;

                int start = row.getStart() > 0 ? row.getStart() : row.getEnd();
                int end = row.getEnd();
                String format = null;
                try {
                    List<LeagueSeriesData> series = row.getLeagueData(_soloDraftDefinitions).getSeries();
                    if (series != null && !series.isEmpty()) {
                        // the series carry the dates the hall shows for a league; the row's columns are the fallback
                        start = series.get(0).getStart();
                        end = series.get(series.size() - 1).getEnd();
                        format = formatName(series.get(0).getFormat(), null);
                    }
                } catch (Exception exp) {
                    // An old league whose stored definition references a product or format that no longer exists.
                    _log.debug("Unable to build the definition of league " + row.getType() + " for its format name", exp);
                }
                if (format == null)
                    format = formatName(rawLeagueFormatCode(row.getParameters()), leagueKind(row));

                events.add(new EventSummary(
                        KIND_LEAGUE,
                        row.getType(),
                        row.getName(),
                        EventDates.isoDate(start),
                        EventDates.isoDate(end),
                        format,
                        playerCounts.get(row.getType()),
                        null));  // leagues have series, not rounds
            } catch (Exception exp) {
                _log.warn("Skipping league " + row.getType() + " in the event history: " + exp.getMessage(), exp);
            }
        }
        return complete;
    }

    /**
     * The format code of a constructed league's first serie straight out of the stored parameters
     * ({@code 20261001,default,2,open,14,6,legacy,14,6}: start, collection, serie count, then format / duration /
     * max matches per serie), for the case where the league definition itself will no longer build.  Sealed and
     * solo-draft leagues store a product code there instead, which is not a format: null for those.
     */
    static String rawLeagueFormatCode(String parameters) {
        if (parameters == null || parameters.isBlank())
            return null;
        try {
            String[] parts = parameters.split(",");
            if (parts.length < 4)
                return null;
            if (!"default".equals(parts[1].trim()) && !"permanent".equals(parts[1].trim()))
                return null;   // not the constructed layout
            Integer.parseInt(parts[2].trim());
            return parts[3].trim();
        } catch (Exception exp) {
            return null;
        }
    }

    /** "Sealed", "Solo draft" or "Constructed" from the league's data class name, as the last-resort format label. */
    static String leagueKind(League row) {
        String clazz = row.getLeagueClass() == null ? "" : row.getLeagueClass();
        if (clazz.contains("SoloDraft"))
            return "Solo draft";
        if (clazz.contains("Sealed"))
            return "Sealed";
        return "Constructed";
    }

    /**
     * The human-readable name of a format code, the raw code when the format has since been retired from the
     * library, and {@code fallback} when there is no code at all.  Never null, never throws.
     */
    private String formatName(String code, String fallback) {
        if (code != null && !code.isBlank()) {
            try {
                SwccgFormat format = _formatLibrary.getFormat(code);
                if (format != null && format.getName() != null && !format.getName().isBlank())
                    return format.getName();
            } catch (Exception exp) {
                _log.debug("Unable to look up format " + code, exp);
            }
            return code;
        }
        return fallback == null || fallback.isBlank() ? UNKNOWN_FORMAT : fallback;
    }

    private static int today() {
        return EventDates.today();
    }
}
