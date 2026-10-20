package com.gempukku.swccgo.league;

import com.gempukku.swccgo.db.LeagueDAO;
import com.gempukku.swccgo.db.LeagueScheduleDAO;
import com.gempukku.swccgo.db.vo.League;
import com.gempukku.swccgo.db.vo.LeagueScheduleRow;
import com.gempukku.swccgo.events.EventDates;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Turns {@link LeagueSchedule} definitions into leagues.  At startup and then about once an hour (the hall's
 * cleanup tick calls {@link #processDueSchedulesIfTime()}), every enabled schedule whose next event is within its
 * lead time is materialised through the {@link LeagueFactory} - the same code the League Admin form's Create button
 * runs - and the schedule advances to its next event and date.  A schedule that cannot be materialised (invalid
 * definition, an active league with the same name) is left where it is with the reason recorded on it, and is
 * retried on the next run.
 * <p>
 * Ported from LOTR GEMP's LeagueScheduleService with three changes: a schedule never creates a second league for the
 * same start day (the league table is checked by {@code schedule_id} + {@code start}, so a crash between the insert
 * and the schedule update, or two servers on one database, cannot double up); occurrences whose start day is more
 * than {@link #PAST_GRACE_DAYS} gone are skipped instead of created (a schedule that was off for months does not
 * flood the hall with finished leagues when it is turned back on); and a schedule that is saved or enabled is
 * processed at once rather than on the next tick.
 */
public class LeagueScheduleService {
    private static final Logger _log = LogManager.getLogger(LeagueScheduleService.class);

    /**
     * Upper bound on leagues created for one schedule in one run, so a schedule that fell a little behind (or has a
     * tiny interval by mistake) cannot flood the league table.
     */
    public static final int MAX_CATCH_UP_PER_RUN = 3;
    /** An occurrence whose start day is more than this many days in the past is skipped, never created. */
    public static final int PAST_GRACE_DAYS = 2;
    /** How often the hall tick does the real work. */
    public static final long RUN_INTERVAL_MS = 60L * 60 * 1000;
    /** How many occurrences a projection or a catch-up may walk before giving up (a loop guard, not a feature). */
    private static final int WALK_GUARD = 2000;

    private final LeagueScheduleDAO _scheduleDao;
    private final LeagueFactory _leagueFactory;
    private final LeagueService _leagueService;
    private final LeagueDAO _leagueDao;
    private final SwccgCardBlueprintLibrary _library;

    private long _nextRunAt;

    public LeagueScheduleService(LeagueScheduleDAO scheduleDao, LeagueFactory leagueFactory, LeagueService leagueService,
                                 LeagueDAO leagueDao, SwccgCardBlueprintLibrary library) {
        _scheduleDao = scheduleDao;
        _leagueFactory = leagueFactory;
        _leagueService = leagueService;
        _leagueDao = leagueDao;
        _library = library;
    }

    // ------------------------------------------------------------------------------------------
    // Definitions
    // ------------------------------------------------------------------------------------------

    public synchronized List<LeagueSchedule> getSchedules() {
        List<LeagueSchedule> result = new ArrayList<>();
        for (LeagueScheduleRow row : _scheduleDao.getAllSchedules()) {
            try {
                result.add(new LeagueSchedule(row));
            } catch (IllegalArgumentException exp) {
                // a row whose JSON was broken by hand: still list it, so the admin can fix or delete it
                _log.warn("League schedule " + row.id + " (" + row.name + ") does not parse: " + exp.getMessage());
                LeagueScheduleRow blank = row.copy();
                blank.template = "{}";
                blank.events = "[]";
                blank.lastError = exp.getMessage();
                result.add(new LeagueSchedule(blank));
            }
        }
        return result;
    }

    public synchronized LeagueSchedule getSchedule(int id) {
        LeagueScheduleRow row = _scheduleDao.getSchedule(id);
        return row == null ? null : new LeagueSchedule(row);
    }

    /**
     * Validates and stores a definition.  Every event is dry-run through the factory so that a template that can
     * never produce a league is rejected up front.  An enabled schedule is processed at once, so a next event that
     * is already within its lead time appears in the hall without waiting for the timer.
     * @return the stored schedule
     */
    public synchronized LeagueSchedule saveSchedule(LeagueScheduleRow row) throws LeagueDefinitionException {
        validateDefinition(row);
        int id = _scheduleDao.saveSchedule(row);
        LeagueScheduleRow saved = _scheduleDao.getSchedule(id);
        if (saved != null && saved.enabled)
            processSchedule(saved, today());
        return getSchedule(id);
    }

    /** Turns a schedule on or off; turning one on processes it at once (see {@link #saveSchedule}). */
    public synchronized LeagueSchedule setEnabled(int id, boolean enabled) {
        _scheduleDao.setEnabled(id, enabled);
        LeagueScheduleRow row = _scheduleDao.getSchedule(id);
        if (row == null)
            return null;
        if (enabled)
            processSchedule(row, today());
        return getSchedule(id);
    }

    public synchronized void deleteSchedule(int id) {
        _scheduleDao.deleteSchedule(id);
    }

    /**
     * Checks a definition without storing it: the row must parse, and every event must produce a valid league.
     */
    public void validateDefinition(LeagueScheduleRow row) throws LeagueDefinitionException {
        if (StringUtils.isBlank(row.name))
            throw new LeagueDefinitionException("name", "The schedule name cannot be blank.");
        if (row.name.length() > LeagueFactory.MAX_NAME_LENGTH)
            throw new LeagueDefinitionException("name", "The schedule name must be " + LeagueFactory.MAX_NAME_LENGTH + " characters or less.");
        if (LeagueFactory.Kind.parse(row.leagueType) == null)
            throw new LeagueDefinitionException("leagueType", "Unknown league type '" + row.leagueType + "'.");
        if (!LeagueFactory.isDay(row.nextEventDate))
            throw new LeagueDefinitionException("nextEventDate", "The next event date must be a day.");
        if (row.intervalMonths <= 0)
            throw new LeagueDefinitionException("intervalMonths", "The interval between events must be positive.");
        if (row.leadDays < 0)
            throw new LeagueDefinitionException("leadDays", "Lead days cannot be negative.");
        if (row.namePattern != null && row.namePattern.length() > 255)
            throw new LeagueDefinitionException("namePattern", "The name pattern must be 255 characters or less.");

        LeagueSchedule schedule;
        try {
            schedule = new LeagueSchedule(row);
        } catch (IllegalArgumentException exp) {
            throw new LeagueDefinitionException("template", exp.getMessage());
        }
        if (schedule.getEvents().isEmpty())
            throw new LeagueDefinitionException("events", "At least one event must be defined.");
        if (row.nextEventIndex < 0 || row.nextEventIndex >= schedule.getEvents().size())
            throw new LeagueDefinitionException("nextEventIndex", "The next event index must point at one of the " + schedule.getEvents().size() + " events.");

        // Every event must be a valid league, not just the next one, otherwise the rotation breaks mid-way
        for (int i = 0; i < schedule.getEvents().size(); i++) {
            try {
                _leagueFactory.prepare(buildDefinition(schedule, i, schedule.getNextEventDate()), "preview");
            } catch (IllegalArgumentException exp) {
                throw new LeagueDefinitionException("events[" + i + "]", "Event " + (i + 1) + ": " + exp.getMessage());
            } catch (LeagueDefinitionException exp) {
                throw new LeagueDefinitionException("events[" + i + "]." + exp.getParameter(), "Event " + (i + 1) + ": " + exp.getMessage());
            }
        }
    }

    // ------------------------------------------------------------------------------------------
    // Materialisation
    // ------------------------------------------------------------------------------------------

    /** What one run created: the league as prepared, its code, and the schedule it came from. */
    public record Created(LeagueFactory.PreparedLeague league, String code, int scheduleId, String scheduleName) {
    }

    /**
     * Cheap entry point for the hall's cleanup tick: does the real work at startup and then at most once per
     * {@link #RUN_INTERVAL_MS}.  Never throws.
     */
    public void processDueSchedulesIfTime() {
        long now = System.currentTimeMillis();
        synchronized (this) {
            if (now < _nextRunAt)
                return;
            _nextRunAt = now + RUN_INTERVAL_MS;
        }
        try {
            processDueSchedules(today());
        } catch (Exception exp) {
            _log.error("League schedule processing failed", exp);
        }
    }

    /**
     * Materialises every enabled schedule whose next event falls within its lead time of {@code today}.
     * @return the leagues created, in order
     */
    public synchronized List<Created> processDueSchedules(LocalDate today) {
        List<Created> created = new ArrayList<>();
        for (LeagueScheduleRow row : _scheduleDao.getAllSchedules()) {
            if (!row.enabled)
                continue;
            created.addAll(processSchedule(row, today));
        }
        return created;
    }

    /**
     * One schedule's turn: occurrences whose start day is already more than the grace behind are skipped, then up
     * to {@link #MAX_CATCH_UP_PER_RUN} due occurrences are created.
     */
    private List<Created> processSchedule(LeagueScheduleRow row, LocalDate today) {
        List<Created> created = new ArrayList<>();
        if (!row.enabled)
            return created;
        int skipped = skipStaleOccurrences(row, today);
        int creations = 0;
        int guard = 0;
        while (creations < MAX_CATCH_UP_PER_RUN && guard++ < WALK_GUARD && isDue(row, today)) {
            Outcome outcome = materialize(row, today, false);
            if (outcome.failed())
                break;
            if (outcome.created != null) {
                created.add(outcome.created);
                creations++;
            }
        }
        if (skipped > 0 && row.lastError == null) {
            row.lastError = "Skipped " + skipped + " occurrence" + (skipped == 1 ? "" : "s") + " whose start day was more than "
                    + PAST_GRACE_DAYS + " days ago (a league is never created in the past).";
            row.lastRun = new Date();
            _scheduleDao.updateProgress(row);
        }
        return created;
    }

    /**
     * Advances past every occurrence whose start day is more than {@link #PAST_GRACE_DAYS} before {@code today}.
     * @return how many were skipped
     */
    private int skipStaleOccurrences(LeagueScheduleRow row, LocalDate today) {
        LocalDate oldest = today.minusDays(PAST_GRACE_DAYS);
        int events = Math.max(1, LeagueSchedule.countEventsLenient(row.events));
        int skipped = 0;
        while (skipped < WALK_GUARD && row.intervalMonths > 0 && EventDates.fromDateInt(row.nextEventDate).isBefore(oldest)) {
            LocalDate next = advance(EventDates.fromDateInt(row.nextEventDate), row.intervalMonths);
            _log.info("League schedule " + row.id + " (" + row.name + ") skips the occurrence of " + EventDates.fromDateInt(row.nextEventDate)
                    + ": its start day is in the past; next " + next);
            row.nextEventDate = EventDates.toDateInt(next);
            row.nextEventIndex = (Math.floorMod(row.nextEventIndex, events) + 1) % events;
            skipped++;
        }
        if (skipped > 0) {
            row.lastRun = new Date();
            _scheduleDao.updateProgress(row);
        }
        return skipped;
    }

    /**
     * Creates the next event of one schedule right now, regardless of lead time or the enabled flag (the admin
     * "Create next now" button).
     * @return the created league, or null if it could not be created (the reason is recorded on the schedule)
     */
    public synchronized Created runNow(int scheduleId) {
        LeagueScheduleRow row = _scheduleDao.getSchedule(scheduleId);
        if (row == null)
            return null;
        Outcome outcome = materialize(row, today(), true);
        return outcome.created;
    }

    /** Whether the row's next event is within its lead time of {@code today}. */
    public static boolean isDue(LeagueScheduleRow row, LocalDate today) {
        return !EventDates.fromDateInt(row.nextEventDate).minusDays(row.leadDays).isAfter(today);
    }

    /** {@code created} is null when the occurrence was skipped (already existed) or failed; {@code failed} says which. */
    private record Outcome(Created created, boolean failed) {
    }

    /**
     * Creates the league for the row's next event and advances the row.  On failure the row keeps its position and
     * the error is stored; the caller decides whether to keep going.  When a league of this schedule with the same
     * start day already exists the row is advanced without creating anything.
     */
    private Outcome materialize(LeagueScheduleRow row, LocalDate today, boolean manual) {
        LeagueSchedule schedule;
        try {
            schedule = new LeagueSchedule(row);
        } catch (IllegalArgumentException exp) {
            return recordFailure(row, exp.getMessage());
        }
        if (schedule.getEvents().isEmpty())
            return recordFailure(row, "The schedule has no events.");
        if (schedule.getKind() == null)
            return recordFailure(row, "Unknown league type '" + row.leagueType + "'.");

        int index = Math.floorMod(row.nextEventIndex, schedule.getEvents().size());
        LocalDate start = schedule.getNextEventDate();
        if (!manual && start.isBefore(today.minusDays(PAST_GRACE_DAYS)))
            return recordFailure(row, "The next occurrence (" + start + ") is in the past; not creating it.");

        LeagueFactory.Definition definition;
        try {
            definition = buildDefinition(schedule, index, start);
        } catch (IllegalArgumentException exp) {
            return recordFailure(row, "Event " + (index + 1) + ": " + exp.getMessage());
        }

        List<League> existing;
        try {
            existing = _leagueDao.loadLeaguesOfScheduleStarting(_library, row.id, EventDates.toDateInt(start));
        } catch (Exception exp) {
            _log.error("Unable to check the leagues of schedule " + row.id, exp);
            return recordFailure(row, "Could not check whether the league already exists: " + exp.getMessage());
        }
        if (!existing.isEmpty()) {
            _log.info("League schedule " + row.id + " (" + row.name + ") already created '" + existing.get(0).getName()
                    + "' (" + existing.get(0).getType() + ") for " + start + "; advancing without creating another");
            advance(row, schedule, index, existing.get(0).getType());
            return new Outcome(null, false);
        }

        if (isLeagueNameActive(definition.name))
            return recordFailure(row, "A league named '" + definition.name + "' is already active; not creating another. "
                    + "Rename or end that league, or adjust this schedule's name pattern.");

        LeagueFactory.PreparedLeague prepared;
        String code;
        try {
            prepared = _leagueFactory.prepare(definition);
            code = _leagueFactory.create(prepared, row.id);
        } catch (LeagueDefinitionException exp) {
            return recordFailure(row, "Event " + (index + 1) + " (" + definition.name + "): " + exp.getMessage());
        } catch (RuntimeException exp) {
            _log.error("Unable to create league for schedule " + row.id, exp);
            return recordFailure(row, "Event " + (index + 1) + " (" + definition.name + "): " + exp.getMessage());
        }

        advance(row, schedule, index, code);
        _log.info("League schedule " + row.id + " (" + row.name + ") created league '" + definition.name + "' (" + code
                + ", " + EventDates.isoDate(prepared.start()) + " to " + EventDates.isoDate(prepared.displayEnd())
                + "); next event " + EventDates.fromDateInt(row.nextEventDate) + " index " + row.nextEventIndex);
        return new Outcome(new Created(prepared, code, row.id, row.name), false);
    }

    private void advance(LeagueScheduleRow row, LeagueSchedule schedule, int index, String createdCode) {
        row.nextEventIndex = (index + 1) % schedule.getEvents().size();
        row.nextEventDate = EventDates.toDateInt(advance(schedule.getNextEventDate(), row.intervalMonths));
        row.lastCreatedLeagueType = createdCode;
        row.lastRun = new Date();
        row.lastError = null;
        _scheduleDao.updateProgress(row);
    }

    private Outcome recordFailure(LeagueScheduleRow row, String message) {
        _log.warn("League schedule " + row.id + " (" + row.name + ") not materialised: " + message);
        row.lastRun = new Date();
        row.lastError = StringUtils.abbreviate(message, 1000);
        _scheduleDao.updateProgress(row);
        return new Outcome(null, true);
    }

    private boolean isLeagueNameActive(String name) {
        for (League league : _leagueService.getActiveLeagues()) {
            if (league.getName() != null && league.getName().equalsIgnoreCase(name))
                return true;
        }
        return false;
    }

    // ------------------------------------------------------------------------------------------
    // Projection (for calendars and previews - nothing is created)
    // ------------------------------------------------------------------------------------------

    /**
     * One future occurrence of a schedule.  {@code error} is set when the event's definition does not produce a
     * valid league; {@code start}/{@code end} are then just the scheduled day.
     */
    public record ProjectedEvent(int scheduleId, String scheduleName, int eventIndex, String eventName, String leagueName,
                                 LocalDate start, LocalDate end, LocalDate createdOn, String error) {
    }

    /**
     * Lists the events a schedule would produce with a start day in [from, to], up to {@code limit} of them.
     */
    public List<ProjectedEvent> project(LeagueSchedule schedule, LocalDate from, LocalDate to, int limit) {
        List<ProjectedEvent> result = new ArrayList<>();
        if (schedule.getEvents().isEmpty() || schedule.getIntervalMonths() <= 0 || schedule.getNextEventDate() == null)
            return result;

        LocalDate date = schedule.getNextEventDate();
        int index = Math.floorMod(schedule.getNextEventIndex(), schedule.getEvents().size());
        int guard = 0;
        while (!date.isAfter(to) && result.size() < limit && guard++ < WALK_GUARD) {
            if (!date.isBefore(from))
                result.add(projectOne(schedule, index, date));
            date = advance(date, schedule.getIntervalMonths());
            index = (index + 1) % schedule.getEvents().size();
        }
        return result;
    }

    /**
     * The next {@code count} occurrences of a schedule starting from its current position.
     */
    public List<ProjectedEvent> projectNext(LeagueSchedule schedule, int count) {
        List<ProjectedEvent> result = new ArrayList<>();
        if (schedule.getEvents().isEmpty() || schedule.getIntervalMonths() <= 0 || schedule.getNextEventDate() == null)
            return result;
        LocalDate date = schedule.getNextEventDate();
        int index = Math.floorMod(schedule.getNextEventIndex(), schedule.getEvents().size());
        for (int i = 0; i < count; i++) {
            result.add(projectOne(schedule, index, date));
            date = advance(date, schedule.getIntervalMonths());
            index = (index + 1) % schedule.getEvents().size();
        }
        return result;
    }

    private ProjectedEvent projectOne(LeagueSchedule schedule, int index, LocalDate date) {
        String eventName = schedule.getEvents().get(index).name();
        LocalDate createdOn = date.minusDays(schedule.getLeadDays());
        String leagueName = renderName(schedule.getNamePattern(), schedule.getName(), eventName, date);
        try {
            LeagueFactory.PreparedLeague prepared = _leagueFactory.prepare(buildDefinition(schedule, index, date), "preview");
            List<LeagueSeriesData> series = prepared.series();
            return new ProjectedEvent(schedule.getId(), schedule.getName(), index, eventName, leagueName,
                    EventDates.fromDateInt(series.get(0).getStart()), EventDates.fromDateInt(series.get(series.size() - 1).getEnd()),
                    createdOn, null);
        } catch (LeagueDefinitionException | IllegalArgumentException exp) {
            return new ProjectedEvent(schedule.getId(), schedule.getName(), index, eventName, leagueName, date, date,
                    createdOn, exp.getMessage());
        }
    }

    // ------------------------------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------------------------------

    /** Today in server time (UTC, as DateUtils.getCurrentDate). */
    static LocalDate today() {
        return LocalDate.now(ZoneOffset.UTC);
    }

    /**
     * Assembles the league definition for one occurrence: template + event overrides, then the generated name and
     * the start day.
     */
    public static LeagueFactory.Definition buildDefinition(LeagueSchedule schedule, int eventIndex, LocalDate start) {
        String name = renderName(schedule.getNamePattern(), schedule.getName(), schedule.getEvents().get(eventIndex).name(), start);
        return schedule.buildDefinition(eventIndex, name, start);
    }

    /**
     * Fills the name pattern's tokens: {series}, {event}, {month} (September), {mon} (Sep), {mm} (09), {year} / {yyyy}
     * (2026), {yy} (26).  Unknown tokens are left as they are.
     */
    public static String renderName(String pattern, String seriesName, String eventName, LocalDate date) {
        String result = StringUtils.isBlank(pattern) ? LeagueSchedule.DEFAULT_NAME_PATTERN : pattern;
        result = result.replace("{series}", seriesName == null ? "" : seriesName);
        result = result.replace("{event}", eventName == null ? "" : eventName);
        result = result.replace("{month}", date.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH));
        result = result.replace("{mon}", date.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH));
        result = result.replace("{mm}", String.format("%02d", date.getMonthValue()));
        result = result.replace("{yyyy}", String.valueOf(date.getYear()));
        result = result.replace("{year}", String.valueOf(date.getYear()));
        result = result.replace("{yy}", String.format("%02d", date.getYear() % 100));
        return result.trim();
    }

    /**
     * Adds an interval to a date.  The whole part is calendar months (so the 1st stays the 1st); the fractional
     * part is interpreted in weeks of a four-week month, so 0.25 = one week and 0.5 = two weeks.
     */
    public static LocalDate advance(LocalDate date, double intervalMonths) {
        if (intervalMonths <= 0)
            throw new IllegalArgumentException("Interval must be positive");
        int wholeMonths = (int) Math.floor(intervalMonths);
        double fraction = intervalMonths - wholeMonths;
        int days = (int) Math.round(fraction * 28);
        LocalDate result = date.plusMonths(wholeMonths).plusDays(days);
        if (result.equals(date))
            result = date.plusDays(1);
        return result;
    }
}
