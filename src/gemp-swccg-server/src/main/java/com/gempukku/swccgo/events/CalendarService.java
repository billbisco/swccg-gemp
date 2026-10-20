package com.gempukku.swccgo.events;

import com.gempukku.swccgo.db.LeagueDAO;
import com.gempukku.swccgo.db.vo.League;
import com.gempukku.swccgo.draft2.SoloDraftDefinitions;
import com.gempukku.swccgo.game.Player;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.game.SwccgFormat;
import com.gempukku.swccgo.game.formats.SwccgoFormatLibrary;
import com.gempukku.swccgo.league.LeagueSchedule;
import com.gempukku.swccgo.league.LeagueScheduleService;
import com.gempukku.swccgo.league.LeagueService;
import com.gempukku.swccgo.league.LeagueSeriesData;
import com.gempukku.swccgo.tournament.Tournament;
import com.gempukku.swccgo.tournament.TournamentDAO;
import com.gempukku.swccgo.tournament.TournamentInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The events of a range of days for the Events tab's calendar (GET /calendar?from=yyyy-MM-dd&amp;to=yyyy-MM-dd),
 * in the JSON shape the LOTR GEMP calendar client renders.  Ported from the LOTR CalendarRequestHandler; the
 * assembly lives here, in the server module, so it can be unit-tested (the async module has no test tree).
 * <p>
 * Leagues are every league (running or over) whose span touches the range, with their series; tournaments are the
 * tournaments that started in the range, each a one-day event with its start time; and, for league admins only, the
 * leagues the enabled league schedules are projected to create ("Scheduled (not yet created)", kind
 * {@code projected}).  This server has no scheduled tournaments.
 */
public class CalendarService {
    private static final Logger _log = LogManager.getLogger(CalendarService.class);

    /** A calendar request may span at most this many days. */
    public static final int MAX_RANGE_DAYS = 400;

    private final LeagueDAO _leagueDao;
    private final LeagueService _leagueService;
    private final TournamentDAO _tournamentDao;
    private final SwccgCardBlueprintLibrary _library;
    private final SwccgoFormatLibrary _formatLibrary;
    private final SoloDraftDefinitions _soloDraftDefinitions;
    private final LeagueScheduleService _leagueScheduleService;

    /** How far before the range a projected league may start and still be shown (it may still be running). */
    static final int PROJECTION_LOOK_BACK_DAYS = 120;
    /** Projected occurrences per schedule per request, at most. */
    static final int PROJECTION_LIMIT = 50;

    public CalendarService(LeagueDAO leagueDao, LeagueService leagueService, TournamentDAO tournamentDao,
                           SwccgCardBlueprintLibrary library, SwccgoFormatLibrary formatLibrary,
                           SoloDraftDefinitions soloDraftDefinitions, LeagueScheduleService leagueScheduleService) {
        _leagueDao = leagueDao;
        _leagueService = leagueService;
        _tournamentDao = tournamentDao;
        _library = library;
        _formatLibrary = formatLibrary;
        _soloDraftDefinitions = soloDraftDefinitions;
        _leagueScheduleService = leagueScheduleService;
    }

    /** A day in yyyy-MM-dd form, or null when the text is missing or malformed (the handler's 400). */
    public static LocalDate parseDate(String value) {
        if (value == null || value.isBlank())
            return null;
        try {
            return LocalDate.parse(value.trim());
        } catch (DateTimeParseException exp) {
            return null;
        }
    }

    /** Why the range cannot be served (the handler's 400), or null when it can. */
    public static String validateRange(LocalDate from, LocalDate to) {
        if (from == null)
            return "Parameter 'from' must be a date in yyyy-MM-dd form.";
        if (to == null)
            return "Parameter 'to' must be a date in yyyy-MM-dd form.";
        if (to.isBefore(from))
            return "'to' must not be before 'from'.";
        if (from.plusDays(MAX_RANGE_DAYS).isBefore(to))
            return "A calendar request may span at most " + MAX_RANGE_DAYS + " days.";
        return null;
    }

    /**
     * The calendar of [from, to] (both inclusive days) as the client renders it: {from, to, eventAdmin, events}.
     * The range must have passed {@link #validateRange}.
     */
    public Map<String, Object> getCalendar(LocalDate from, LocalDate to, Player viewer) {
        boolean eventAdmin = viewer.hasType(Player.Type.ADMIN) || viewer.hasType(Player.Type.LEAGUE_ADMIN);

        var events = new ArrayList<Map<String, Object>>();
        addLeagues(events, from, to, viewer);
        addTournaments(events, from, to, viewer);
        if (eventAdmin)
            addProjectedLeagues(events, from, to);

        var result = new LinkedHashMap<String, Object>();
        result.put("from", from.toString());
        result.put("to", to.toString());
        result.put("eventAdmin", eventAdmin);
        result.put("events", events);
        return result;
    }

    /**
     * Leagues whose span overlaps the range.  The stored start / end bound the query; the series (the dates the hall
     * shows) decide the exact span.  {@code joined} tells the viewer which leagues they are in.
     */
    private void addLeagues(List<Map<String, Object>> events, LocalDate from, LocalDate to, Player viewer) {
        List<League> leagues;
        try {
            leagues = _leagueDao.loadLeaguesOverlapping(_library, EventDates.toDateInt(from), EventDates.toDateInt(to));
        } catch (Exception exp) {
            throw new RuntimeException("Unable to load the leagues between " + from + " and " + to, exp);
        }
        int fromInt = EventDates.toDateInt(from);
        int toInt = EventDates.toDateInt(to);
        for (League league : leagues) {
            List<LeagueSeriesData> series;
            try {
                series = league.getLeagueData(_soloDraftDefinitions).getSeries();
            } catch (Exception exp) {
                _log.warn("Skipping league " + league.getType() + " on the calendar: " + exp.getMessage());
                continue;
            }
            if (series == null || series.isEmpty())
                continue;

            int start = series.get(0).getStart();
            int end = series.get(series.size() - 1).getEnd();
            if (start > toInt || end < fromInt)
                continue;

            var event = new LinkedHashMap<String, Object>();
            event.put("kind", "league");
            event.put("id", league.getType());
            event.put("name", league.getName());
            event.put("leagueType", EventHistoryService.leagueKind(league));
            event.put("start", EventDates.isoDate(start));
            event.put("end", EventDates.isoDate(end));
            event.put("inviteOnly", league.getInvitationOnly());
            event.put("cost", league.getCost());
            event.put("description", null);
            event.put("scheduleId", league.getScheduleId());
            event.put("joined", isInLeague(league, viewer));

            var serieList = new ArrayList<Map<String, Object>>();
            for (LeagueSeriesData serie : series) {
                var s = new LinkedHashMap<String, Object>();
                s.put("name", serie.getName());
                s.put("start", EventDates.isoDate(serie.getStart()));
                s.put("end", EventDates.isoDate(serie.getEnd()));
                s.put("format", formatName(serie.getFormat()));
                s.put("limited", serie.isLimited());
                serieList.add(s);
            }
            event.put("series", serieList);
            events.add(event);
        }
    }

    private boolean isInLeague(League league, Player viewer) {
        try {
            return _leagueService.isPlayerInLeague(league, viewer);
        } catch (Exception exp) {
            _log.debug("Could not determine membership of league " + league.getType(), exp);
            return false;
        }
    }

    /**
     * Tournaments that started in the range, one day each.  A finished tournament with no round played (a draft
     * against bots, say) is not an event.  {@code joined} is true when the viewer is among its players.
     */
    private void addTournaments(List<Map<String, Object>> events, LocalDate from, LocalDate to, Player viewer) {
        long fromMs = EventDates.startOfDayMillis(from);
        long toMs = EventDates.startOfDayMillis(to.plusDays(1));
        List<TournamentInfo> tournaments = _tournamentDao.getTournamentsStartingBetween(fromMs, toMs);
        Set<String> played;
        try {
            played = _tournamentDao.getTournamentsPlayedBetween(viewer.getName(), fromMs, toMs);
        } catch (Exception exp) {
            _log.debug("Could not determine the tournaments of " + viewer.getName(), exp);
            played = Set.of();
        }
        for (TournamentInfo tournament : tournaments) {
            boolean finished = tournament.getTournamentStage() == Tournament.Stage.FINISHED;
            if (finished && tournament.getTournamentRound() == 0)
                continue;
            var event = new LinkedHashMap<String, Object>();
            event.put("kind", "tournament");
            event.put("id", tournament.getTournamentId());
            event.put("name", tournament.getTournamentName());
            event.put("tournamentType", tournament.getDraftType() == null ? "CONSTRUCTED" : tournament.getDraftType());
            event.put("format", formatName(tournament.getTournamentFormat()));
            event.put("start", EventDates.isoDateOfMillis(tournament.getStartTime()));
            event.put("startTime", EventDates.isoDateTimeOfMillis(tournament.getStartTime()));
            event.put("end", EventDates.isoDateOfMillis(tournament.getStartTime()));
            event.put("started", true);
            event.put("finished", finished);
            event.put("stage", tournament.getTournamentStage() == null ? null : tournament.getTournamentStage().getHumanReadable());
            event.put("rounds", tournament.getTournamentRound());
            event.put("cost", null);
            event.put("joined", played.contains(tournament.getTournamentId()));
            event.put("queueOpen", false);
            events.add(event);
        }
    }

    /**
     * The leagues the enabled schedules would create whose span touches the range (LOTR CalendarRequestHandler's
     * addProjectedLeagues).  Nothing is created; a projection whose definition is broken carries {@code error}.
     */
    private void addProjectedLeagues(List<Map<String, Object>> events, LocalDate from, LocalDate to) {
        if (_leagueScheduleService == null)
            return;
        List<LeagueSchedule> schedules;
        try {
            schedules = _leagueScheduleService.getSchedules();
        } catch (Exception exp) {
            _log.warn("Could not load the league schedules for the calendar: " + exp.getMessage());
            return;
        }
        for (LeagueSchedule schedule : schedules) {
            if (!schedule.isEnabled())
                continue;
            // A projected league that started before the range but is still running should show too
            LocalDate lookBack = from.minusDays(PROJECTION_LOOK_BACK_DAYS);
            for (LeagueScheduleService.ProjectedEvent projected : _leagueScheduleService.project(schedule, lookBack, to, PROJECTION_LIMIT)) {
                if (projected.end().isBefore(from))
                    continue;
                var event = new LinkedHashMap<String, Object>();
                event.put("kind", "projected");
                event.put("id", schedule.getId() + ":" + projected.eventIndex() + ":" + projected.start());
                event.put("scheduleId", schedule.getId());
                event.put("scheduleName", schedule.getName());
                event.put("eventIndex", projected.eventIndex());
                event.put("eventName", projected.eventName());
                event.put("name", projected.leagueName());
                event.put("leagueType", schedule.getKind() == null ? null : kindLabel(schedule.getKind().name()));
                event.put("start", projected.start().toString());
                event.put("end", projected.end().toString());
                event.put("createdOn", projected.createdOn().toString());
                event.put("error", projected.error());
                events.add(event);
            }
        }
    }

    /** CONSTRUCTED / SEALED / SOLODRAFT as the league events spell their kind ("Constructed", "Sealed", "Solo draft"). */
    static String kindLabel(String kind) {
        switch (kind) {
            case "SEALED":
                return "Sealed";
            case "SOLODRAFT":
                return "Solo draft";
            default:
                return "Constructed";
        }
    }

    /** The format's name, or its code when the library no longer knows it; null for no code. */
    private String formatName(String code) {
        if (code == null || code.isBlank())
            return null;
        try {
            SwccgFormat format = _formatLibrary.getFormat(code);
            if (format != null && format.getName() != null && !format.getName().isBlank())
                return format.getName();
        } catch (Exception exp) {
            _log.debug("Unable to look up format " + code, exp);
        }
        return code;
    }
}
