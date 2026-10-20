package com.gempukku.swccgo.league;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gempukku.swccgo.db.vo.LeagueScheduleRow;
import com.gempukku.swccgo.events.EventDates;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * A recurring league definition: a shared template (the New-league form's common fields, as JSON), an ordered list
 * of events that each name the serie(s) to play and may override any template key, and the cadence at which the
 * server turns the next event into a real league.  This is the parsed, read-only view of a
 * {@link LeagueScheduleRow}; ported from LOTR GEMP's LeagueSchedule onto this server's league form.
 * <p>
 * Template keys are the names the form posts: {@code cost}, {@code collectionType}, {@code lockedDeckType},
 * {@code allowTimeExtensions}, {@code allowSpectators}, {@code showPlayerNames}, {@code invitationOnly},
 * {@code registrationInfo}, {@code decisionTimeoutSeconds}, {@code timePerPlayerMinutes}; an event's
 * {@code params.series} is a list of {@code {format, duration, matches}}.  Unknown keys are kept and ignored, so an
 * older or newer admin page can round-trip a schedule.
 */
public class LeagueSchedule {
    public static final String DEFAULT_NAME_PATTERN = "{series} - {event}";

    /**
     * One entry in the rotation.  {@code overrides} is a JSON object whose keys replace the template's keys; a
     * missing or empty object means "use the template as-is" (which then has to carry the series itself).
     */
    public record Event(String name, JSONObject overrides) {
        public Event {
            if (overrides == null)
                overrides = new JSONObject();
            if (name == null)
                name = "";
        }
    }

    private final int _id;
    private final String _name;
    private final LeagueFactory.Kind _kind;
    private final JSONObject _template;
    private final List<Event> _events;
    private final String _namePattern;
    private final LocalDate _nextEventDate;
    private final int _nextEventIndex;
    private final double _intervalMonths;
    private final int _leadDays;
    private final boolean _enabled;
    private final String _lastCreatedLeagueType;
    private final Date _lastRun;
    private final String _lastError;
    private final String _createdBy;

    /** @throws IllegalArgumentException when the template or events are not the JSON they should be */
    public LeagueSchedule(LeagueScheduleRow row) {
        _id = row.id;
        _name = row.name;
        _kind = LeagueFactory.Kind.parse(row.leagueType);
        _template = parseObject(row.template, "template");
        _events = parseEvents(row.events);
        _namePattern = StringUtils.isBlank(row.namePattern) ? DEFAULT_NAME_PATTERN : row.namePattern;
        _nextEventDate = row.nextEventDate == 0 ? null : EventDates.fromDateInt(row.nextEventDate);
        _nextEventIndex = row.nextEventIndex;
        _intervalMonths = row.intervalMonths;
        _leadDays = row.leadDays;
        _enabled = row.enabled;
        _lastCreatedLeagueType = row.lastCreatedLeagueType;
        _lastRun = row.lastRun;
        _lastError = row.lastError;
        _createdBy = row.createdBy;
    }

    public static JSONObject parseObject(String json, String what) {
        if (StringUtils.isBlank(json))
            return new JSONObject();
        try {
            JSONObject parsed = JSON.parseObject(json);
            return parsed == null ? new JSONObject() : parsed;
        } catch (Exception exp) {
            throw new IllegalArgumentException("League schedule " + what + " is not a JSON object: " + exp.getMessage(), exp);
        }
    }

    public static List<Event> parseEvents(String json) {
        List<Event> events = new ArrayList<>();
        if (StringUtils.isBlank(json))
            return events;
        JSONArray array;
        try {
            array = JSON.parseArray(json);
        } catch (Exception exp) {
            throw new IllegalArgumentException("League schedule events are not a JSON array: " + exp.getMessage(), exp);
        }
        if (array == null)
            return events;
        for (int i = 0; i < array.size(); i++) {
            JSONObject obj;
            try {
                obj = array.getJSONObject(i);
            } catch (Exception exp) {
                obj = null;
            }
            if (obj == null)
                throw new IllegalArgumentException("League schedule event " + (i + 1) + " is not a JSON object.");
            events.add(new Event(obj.getString("name"), obj.getJSONObject("params")));
        }
        return events;
    }

    /** The number of events in the JSON, or 0 when it does not parse (for advancing a rotation without failing on bad JSON). */
    public static int countEventsLenient(String json) {
        try {
            return parseEvents(json).size();
        } catch (IllegalArgumentException exp) {
            return 0;
        }
    }

    public static String serializeEvents(List<Event> events) {
        JSONArray array = new JSONArray();
        for (Event event : events) {
            JSONObject obj = new JSONObject();
            obj.put("name", event.name());
            obj.put("params", event.overrides());
            array.add(obj);
        }
        return JSON.toJSONString(array);
    }

    /**
     * The template with one event's overrides applied key by key: what one league of the schedule is built from,
     * before the per-league name and start day are filled in.
     */
    public JSONObject mergedParams(int eventIndex) {
        JSONObject merged = new JSONObject(true);
        merged.putAll(_template);
        if (eventIndex >= 0 && eventIndex < _events.size())
            merged.putAll(_events.get(eventIndex).overrides());
        merged.remove("name");
        merged.remove("start");
        merged.remove("code");
        return merged;
    }

    /**
     * The league definition for one occurrence: template + the event's overrides, the given name and start day.
     * @throws IllegalArgumentException when a value is not of the type the form field takes
     */
    public LeagueFactory.Definition buildDefinition(int eventIndex, String name, LocalDate start) {
        JSONObject params = mergedParams(eventIndex);
        LeagueFactory.Definition d = new LeagueFactory.Definition();
        d.kind = _kind;
        d.name = name;
        d.start = EventDates.toDateInt(start);
        d.cost = intValue(params, "cost", 0);
        d.collectionType = stringValue(params, "collectionType", "default");
        d.lockedDeckType = stringValue(params, "lockedDeckType", "");
        d.allowTimeExtensions = boolValue(params, "allowTimeExtensions", false);
        d.allowSpectators = boolValue(params, "allowSpectators", true);
        d.showPlayerNames = boolValue(params, "showPlayerNames", false);
        d.invitationOnly = boolValue(params, "invitationOnly", false);
        d.registrationInfo = stringValue(params, "registrationInfo", "");
        d.decisionTimeoutSeconds = intValue(params, "decisionTimeoutSeconds", 300);
        d.timePerPlayerMinutes = intValue(params, "timePerPlayerMinutes", 50);
        d.series = new ArrayList<>();
        JSONArray series;
        try {
            series = params.getJSONArray("series");
        } catch (Exception exp) {
            throw new IllegalArgumentException("'series' must be a list of {format, duration, matches}.");
        }
        if (series != null) {
            for (int i = 0; i < series.size(); i++) {
                JSONObject s;
                try {
                    s = series.getJSONObject(i);
                } catch (Exception exp) {
                    s = null;
                }
                if (s == null)
                    throw new IllegalArgumentException("Serie " + (i + 1) + " must be an object {format, duration, matches}.");
                d.series.add(new LeagueFactory.Serie(stringValue(s, "format", ""), intValue(s, "duration", 0), intValue(s, "matches", 0)));
            }
        }
        return d;
    }

    private static int intValue(JSONObject obj, String key, int fallback) {
        Object value = obj.get(key);
        if (value == null || (value instanceof String && ((String) value).isBlank()))
            return fallback;
        if (value instanceof Number)
            return ((Number) value).intValue();
        try {
            return Integer.parseInt(String.valueOf(value).trim());
        } catch (NumberFormatException exp) {
            throw new IllegalArgumentException("'" + key + "' must be a whole number.");
        }
    }

    private static boolean boolValue(JSONObject obj, String key, boolean fallback) {
        Object value = obj.get(key);
        if (value == null)
            return fallback;
        if (value instanceof Boolean)
            return (Boolean) value;
        String text = String.valueOf(value).trim();
        if (text.equalsIgnoreCase("true"))
            return true;
        if (text.equalsIgnoreCase("false") || text.isEmpty())
            return false;
        throw new IllegalArgumentException("'" + key + "' must be true or false.");
    }

    private static String stringValue(JSONObject obj, String key, String fallback) {
        Object value = obj.get(key);
        return value == null ? fallback : String.valueOf(value);
    }

    public int getId() { return _id; }
    public String getName() { return _name; }
    /** The league kind, or null when the row names one this server does not know. */
    public LeagueFactory.Kind getKind() { return _kind; }
    public JSONObject getTemplate() { return _template; }
    public List<Event> getEvents() { return Collections.unmodifiableList(_events); }
    public String getNamePattern() { return _namePattern; }
    /** The start day of the next league to create (null when the row has none). */
    public LocalDate getNextEventDate() { return _nextEventDate; }
    public int getNextEventIndex() { return _nextEventIndex; }
    public double getIntervalMonths() { return _intervalMonths; }
    public int getLeadDays() { return _leadDays; }
    public boolean isEnabled() { return _enabled; }
    public String getLastCreatedLeagueType() { return _lastCreatedLeagueType; }
    public Date getLastRun() { return _lastRun; }
    public String getLastError() { return _lastError; }
    public String getCreatedBy() { return _createdBy; }
}
