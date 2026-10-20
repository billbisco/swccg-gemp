package com.gempukku.swccgo.league;

import com.alibaba.fastjson.JSONObject;
import com.gempukku.swccgo.db.vo.LeagueScheduleRow;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * The pure parts of a league schedule: the recurrence arithmetic, the name pattern, and the JSON template + event
 * overrides that become a league definition.  Dates are plain days (yyyyMMdd), so there is no DST to get wrong.
 */
public class LeagueScheduleTests {

    static LeagueScheduleRow row(String template, String events) {
        LeagueScheduleRow row = new LeagueScheduleRow();
        row.id = 7;
        row.name = "Constructed";
        row.leagueType = "CONSTRUCTED";
        row.template = template;
        row.events = events;
        row.namePattern = "{series} - {event}";
        row.nextEventDate = 20261001;
        row.nextEventIndex = 0;
        row.intervalMonths = 1;
        row.leadDays = 7;
        row.enabled = true;
        return row;
    }

    static final String TEMPLATE = "{\"cost\": 500, \"collectionType\": \"default\", \"allowSpectators\": true, \"showPlayerNames\": \"true\", "
            + "\"decisionTimeoutSeconds\": \"300\", \"timePerPlayerMinutes\": 50, \"series\": [{\"format\": \"open\", \"duration\": 7, \"matches\": 10}]}";
    static final String EVENTS = "[{\"name\": \"Open\", \"params\": {}}, "
            + "{\"name\": \"Death Star II\", \"params\": {\"series\": [{\"format\": \"premiere_ds2\", \"duration\": 14, \"matches\": 8}], \"cost\": 0}}]";

    // --- recurrence ------------------------------------------------------------------------------------------

    @Test
    public void WholeMonthsAdvanceByCalendarMonth() {
        //test1: the 1st stays the 1st across a year boundary
        //test2: a day the next month lacks clamps to its last day (Java's plusMonths), then stays there
        //test3: every N months
        assertEquals(LocalDate.of(2026, 11, 1), LeagueScheduleService.advance(LocalDate.of(2026, 10, 1), 1));
        assertEquals(LocalDate.of(2027, 1, 1), LeagueScheduleService.advance(LocalDate.of(2026, 12, 1), 1)); //test1
        assertEquals(LocalDate.of(2027, 2, 28), LeagueScheduleService.advance(LocalDate.of(2027, 1, 31), 1)); //test2
        assertEquals(LocalDate.of(2027, 3, 28), LeagueScheduleService.advance(LocalDate.of(2027, 2, 28), 1)); //test2
        assertEquals(LocalDate.of(2028, 2, 29), LeagueScheduleService.advance(LocalDate.of(2028, 1, 31), 1)); //test2
        assertEquals(LocalDate.of(2027, 1, 15), LeagueScheduleService.advance(LocalDate.of(2026, 10, 15), 3)); //test3
    }

    @Test
    public void FractionalMonthsAdvanceByWeeks() {
        //test1: 0.25 = one week, 0.5 = two weeks, over a month end
        //test2: 1.5 = a month and two weeks
        assertEquals(LocalDate.of(2026, 10, 8), LeagueScheduleService.advance(LocalDate.of(2026, 10, 1), 0.25));
        assertEquals(LocalDate.of(2026, 11, 4), LeagueScheduleService.advance(LocalDate.of(2026, 10, 28), 0.25)); //test1
        assertEquals(LocalDate.of(2026, 11, 11), LeagueScheduleService.advance(LocalDate.of(2026, 10, 28), 0.5)); //test1
        assertEquals(LocalDate.of(2026, 11, 15), LeagueScheduleService.advance(LocalDate.of(2026, 10, 1), 1.5)); //test2
    }

    @Test
    public void ATinyIntervalStillMovesForward() {
        //test1: an interval that rounds to zero days still advances a day, so a loop over occurrences terminates
        //test2: a non-positive interval is an error
        assertEquals(LocalDate.of(2026, 10, 2), LeagueScheduleService.advance(LocalDate.of(2026, 10, 1), 0.001)); //test1
        try {
            LeagueScheduleService.advance(LocalDate.of(2026, 10, 1), 0);
            fail("zero interval accepted");
        } catch (IllegalArgumentException expected) {
            //test2
        }
    }

    @Test
    public void IsDueCountsLeadDays() {
        LeagueScheduleRow row = row(TEMPLATE, EVENTS);
        row.nextEventDate = 20261101;
        row.leadDays = 7;
        assertFalse(LeagueScheduleService.isDue(row, LocalDate.of(2026, 10, 24)));
        assertTrue(LeagueScheduleService.isDue(row, LocalDate.of(2026, 10, 25)));
        assertTrue(LeagueScheduleService.isDue(row, LocalDate.of(2026, 11, 1)));
        assertTrue(LeagueScheduleService.isDue(row, LocalDate.of(2026, 12, 1)));
        row.leadDays = 0;
        assertFalse(LeagueScheduleService.isDue(row, LocalDate.of(2026, 10, 31)));
        assertTrue(LeagueScheduleService.isDue(row, LocalDate.of(2026, 11, 1)));
    }

    // --- names -----------------------------------------------------------------------------------------------

    @Test
    public void NamePatternTokensAreFilledIn() {
        //test1: every token
        //test2: an unknown token and surrounding whitespace
        //test3: a blank pattern falls back to the default
        LocalDate sep = LocalDate.of(2026, 9, 1);
        assertEquals("Open - September 2026", LeagueScheduleService.renderName("{series} - {month} {yyyy}", "Open", "x", sep)); //test1
        assertEquals("Open - Sep 26 09 2026", LeagueScheduleService.renderName("{series} - {mon} {yy} {mm} {year}", "Open", "x", sep)); //test1
        assertEquals("Constructed - Death Star II", LeagueScheduleService.renderName("{series} - {event}", "Constructed", "Death Star II", sep)); //test1
        assertEquals("Open {nope}", LeagueScheduleService.renderName("  {series} {nope}  ", "Open", null, sep)); //test2
        assertEquals("Open - DS2", LeagueScheduleService.renderName("", "Open", "DS2", sep)); //test3
        assertEquals("Open - DS2", LeagueScheduleService.renderName(null, "Open", "DS2", sep)); //test3
    }

    // --- template + events -------------------------------------------------------------------------------------

    @Test
    public void EventOverridesReplaceTemplateKeysOnly() {
        //test1: the first event uses the template as-is (strings for numbers and booleans are read as the form sends them)
        //test2: the second event replaces series and cost, keeps everything else
        LeagueSchedule schedule = new LeagueSchedule(row(TEMPLATE, EVENTS));
        assertEquals(2, schedule.getEvents().size());
        assertEquals("Death Star II", schedule.getEvents().get(1).name());

        LeagueFactory.Definition first = schedule.buildDefinition(0, "Open - Oct", LocalDate.of(2026, 10, 1));
        assertEquals(LeagueFactory.Kind.CONSTRUCTED, first.kind);
        assertEquals("Open - Oct", first.name);
        assertEquals(20261001, first.start);
        assertEquals(500, first.cost); //test1
        assertEquals("default", first.collectionType);
        assertTrue(first.allowSpectators);
        assertTrue(first.showPlayerNames); //test1
        assertFalse(first.allowTimeExtensions);
        assertEquals(300, first.decisionTimeoutSeconds); //test1
        assertEquals(50, first.timePerPlayerMinutes);
        assertEquals(List.of(new LeagueFactory.Serie("open", 7, 10)), first.series);

        LeagueFactory.Definition second = schedule.buildDefinition(1, "Constructed - DS2", LocalDate.of(2026, 11, 1));
        assertEquals(0, second.cost); //test2
        assertEquals(List.of(new LeagueFactory.Serie("premiere_ds2", 14, 8)), second.series); //test2
        assertTrue(second.showPlayerNames); //test2
        assertEquals(50, second.timePerPlayerMinutes); //test2
    }

    @Test
    public void TemplateNameStartAndCodeAreIgnored() {
        // a template copied from a league keeps its per-league values harmlessly: the schedule fills those in
        String template = "{\"name\": \"Old\", \"start\": 20200101, \"code\": \"1\", \"cost\": 5, \"series\": [{\"format\": \"open\", \"duration\": 7, \"matches\": 10}]}";
        LeagueSchedule schedule = new LeagueSchedule(row(template, "[{\"name\": \"Open\"}]"));
        LeagueFactory.Definition d = schedule.buildDefinition(0, "New", LocalDate.of(2026, 10, 1));
        assertEquals("New", d.name);
        assertEquals(20261001, d.start);
        assertEquals(5, d.cost);
        assertFalse(schedule.mergedParams(0).containsKey("name"));
    }

    @Test
    public void MissingKeysTakeTheFormDefaults() {
        LeagueSchedule schedule = new LeagueSchedule(row("{}", "[{\"name\": \"Bare\", \"params\": {\"series\": [{\"format\": \"open\", \"duration\": 7, \"matches\": 10}]}}]"));
        LeagueFactory.Definition d = schedule.buildDefinition(0, "Bare", LocalDate.of(2026, 10, 1));
        assertEquals(0, d.cost);
        assertTrue(d.allowSpectators);
        assertFalse(d.invitationOnly);
        assertEquals("", d.registrationInfo);
        assertEquals("", d.lockedDeckType);
        assertEquals(300, d.decisionTimeoutSeconds);
        assertEquals(50, d.timePerPlayerMinutes);
        LeagueSchedule none = new LeagueSchedule(row("{}", "[{\"name\": \"Empty\"}]"));
        assertTrue(none.buildDefinition(0, "Empty", LocalDate.of(2026, 10, 1)).series.isEmpty());
    }

    @Test
    public void BadJsonAndBadValuesAreReported() {
        //test1: template not an object, events not an array, an event that is not an object
        //test2: a value of the wrong type names its key
        try {
            new LeagueSchedule(row("[1]", EVENTS));
            fail("array accepted as template");
        } catch (IllegalArgumentException exp) {
            assertTrue(exp.getMessage(), exp.getMessage().contains("template")); //test1
        }
        try {
            new LeagueSchedule(row(TEMPLATE, "{\"a\": 1}"));
            fail("object accepted as events");
        } catch (IllegalArgumentException exp) {
            assertTrue(exp.getMessage(), exp.getMessage().contains("events")); //test1
        }
        try {
            new LeagueSchedule(row(TEMPLATE, "[1, 2]"));
            fail("numbers accepted as events");
        } catch (IllegalArgumentException exp) {
            assertTrue(exp.getMessage(), exp.getMessage().contains("event 1")); //test1
        }
        LeagueSchedule schedule = new LeagueSchedule(row("{\"cost\": \"lots\", \"series\": []}", "[{\"name\": \"x\"}]"));
        try {
            schedule.buildDefinition(0, "x", LocalDate.of(2026, 10, 1));
            fail("non-numeric cost accepted");
        } catch (IllegalArgumentException exp) {
            assertTrue(exp.getMessage(), exp.getMessage().contains("'cost'")); //test2
        }
        LeagueSchedule bool = new LeagueSchedule(row("{\"invitationOnly\": \"maybe\"}", "[{\"name\": \"x\"}]"));
        try {
            bool.buildDefinition(0, "x", LocalDate.of(2026, 10, 1));
            fail("non-boolean accepted");
        } catch (IllegalArgumentException exp) {
            assertTrue(exp.getMessage(), exp.getMessage().contains("'invitationOnly'")); //test2
        }
        assertEquals(0, LeagueSchedule.countEventsLenient("nope"));
        assertEquals(2, LeagueSchedule.countEventsLenient(EVENTS));
    }

    @Test
    public void EventsRoundTripThroughJson() {
        JSONObject overrides = new JSONObject();
        overrides.put("cost", 0);
        List<LeagueSchedule.Event> events = List.of(new LeagueSchedule.Event("A", null), new LeagueSchedule.Event(null, overrides));
        String json = LeagueSchedule.serializeEvents(events);
        List<LeagueSchedule.Event> back = LeagueSchedule.parseEvents(json);
        assertEquals(2, back.size());
        assertEquals("A", back.get(0).name());
        assertTrue(back.get(0).overrides().isEmpty());
        assertEquals("", back.get(1).name());
        assertEquals(0, back.get(1).overrides().getIntValue("cost"));
        assertTrue(LeagueSchedule.parseEvents("").isEmpty());
        assertTrue(LeagueSchedule.parseEvents(null).isEmpty());
    }

    @Test
    public void RowFieldsComeThrough() {
        LeagueScheduleRow row = row(TEMPLATE, EVENTS);
        row.enabled = false;
        row.namePattern = "  ";
        row.nextEventDate = 0;
        row.lastError = "boom";
        row.createdBy = "Bill";
        LeagueSchedule schedule = new LeagueSchedule(row);
        assertEquals(7, schedule.getId());
        assertEquals("Constructed", schedule.getName());
        assertEquals(LeagueFactory.Kind.CONSTRUCTED, schedule.getKind());
        assertEquals(LeagueSchedule.DEFAULT_NAME_PATTERN, schedule.getNamePattern());
        assertNull(schedule.getNextEventDate());
        assertFalse(schedule.isEnabled());
        assertEquals("boom", schedule.getLastError());
        assertEquals("Bill", schedule.getCreatedBy());
        assertEquals(1.0, schedule.getIntervalMonths(), 0.0001);
        assertEquals(7, schedule.getLeadDays());
        row.leagueType = "RTMD";
        assertNull(new LeagueSchedule(row).getKind());
    }
}
