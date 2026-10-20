package com.gempukku.swccgo.league;

import com.gempukku.swccgo.db.LeagueDAO;
import com.gempukku.swccgo.db.LeagueScheduleDAO;
import com.gempukku.swccgo.db.vo.League;
import com.gempukku.swccgo.db.vo.LeagueScheduleRow;
import com.gempukku.swccgo.draft2.SoloDraftDefinitions;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.game.SwccgFormat;
import com.gempukku.swccgo.game.formats.SwccgoFormatLibrary;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * The scheduler: what gets created when, that nothing is created twice or in the past, and what the calendar and
 * the admin page are told.  Ported from LOTR GEMP's LeagueScheduleServiceTest onto this server's league form.
 */
public class LeagueScheduleServiceTests {
    private static final LocalDate OCT_1 = LocalDate.of(2026, 10, 1);
    private static final LocalDate SEP_20 = LocalDate.of(2026, 9, 20);
    private static final LocalDate SEP_24 = LocalDate.of(2026, 9, 24);
    private static final LocalDate SEP_25 = LocalDate.of(2026, 9, 25);

    /** An in-memory league_schedule table: the service mutates rows and hands them back through updateProgress. */
    static class FakeScheduleDAO implements LeagueScheduleDAO {
        final Map<Integer, LeagueScheduleRow> rows = new LinkedHashMap<>();
        int nextId = 100;
        int progressUpdates;

        void add(LeagueScheduleRow row) {
            rows.put(row.id, row.copy());
        }

        @Override
        public List<LeagueScheduleRow> getAllSchedules() {
            List<LeagueScheduleRow> result = new ArrayList<>();
            for (LeagueScheduleRow row : rows.values())
                result.add(row.copy());
            return result;
        }

        @Override
        public LeagueScheduleRow getSchedule(int id) {
            return rows.containsKey(id) ? rows.get(id).copy() : null;
        }

        @Override
        public int saveSchedule(LeagueScheduleRow schedule) {
            LeagueScheduleRow copy = schedule.copy();
            if (copy.id == 0)
                copy.id = nextId++;
            copy.lastError = null;
            LeagueScheduleRow existing = rows.get(copy.id);
            if (existing != null) {
                copy.lastRun = existing.lastRun;
                copy.lastCreatedLeagueType = existing.lastCreatedLeagueType;
            }
            rows.put(copy.id, copy);
            return copy.id;
        }

        @Override
        public void setEnabled(int id, boolean enabled) {
            rows.get(id).enabled = enabled;
            rows.get(id).lastError = null;
        }

        @Override
        public void updateProgress(LeagueScheduleRow schedule) {
            progressUpdates++;
            LeagueScheduleRow stored = rows.get(schedule.id);
            stored.nextEventDate = schedule.nextEventDate;
            stored.nextEventIndex = schedule.nextEventIndex;
            stored.lastCreatedLeagueType = schedule.lastCreatedLeagueType;
            stored.lastRun = schedule.lastRun;
            stored.lastError = schedule.lastError;
        }

        @Override
        public void deleteSchedule(int id) {
            rows.remove(id);
        }
    }

    private LeagueDAO _leagueDao;
    private LeagueService _leagueService;
    private SwccgoFormatLibrary _formatLibrary;
    private SwccgCardBlueprintLibrary _library;
    private FakeScheduleDAO _scheduleDao;
    private LeagueFactory _factory;
    private LeagueScheduleService _service;

    @Before
    public void setUp() throws Exception {
        _leagueDao = Mockito.mock(LeagueDAO.class);
        _leagueService = Mockito.mock(LeagueService.class);
        _formatLibrary = Mockito.mock(SwccgoFormatLibrary.class);
        _library = Mockito.mock(SwccgCardBlueprintLibrary.class);
        Mockito.when(_leagueService.getActiveLeagues()).thenReturn(List.of());
        Mockito.when(_leagueDao.loadLeaguesOfScheduleStarting(Mockito.any(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(List.of());
        for (String code : new String[]{"open", "premiere_ds2", "reflections_sealed"}) {
            SwccgFormat format = Mockito.mock(SwccgFormat.class);
            Mockito.when(format.getName()).thenReturn(code);
            Mockito.when(_formatLibrary.getFormat(code)).thenReturn(format);
        }
        _factory = new LeagueFactory(_library, _formatLibrary, Mockito.mock(SoloDraftDefinitions.class), _leagueDao, _leagueService);
        _scheduleDao = new FakeScheduleDAO();
        _service = new LeagueScheduleService(_scheduleDao, _factory, _leagueService, _leagueDao, _library);
    }

    // --- fixtures ----------------------------------------------------------------------------------------------

    /** A two-event rotating constructed schedule: Open (7 days) then Death Star II (14 days), monthly on the 1st, 7 lead days. */
    private static LeagueScheduleRow rotatingSchedule() {
        LeagueScheduleRow row = LeagueScheduleTests.row(LeagueScheduleTests.TEMPLATE, LeagueScheduleTests.EVENTS);
        row.nextEventDate = 20261001;
        return row;
    }

    private static LeagueScheduleRow sealedSchedule() {
        LeagueScheduleRow row = LeagueScheduleTests.row(
                "{\"cost\": 500, \"series\": [{\"format\": \"reflections_sealed\", \"duration\": 7, \"matches\": 10}]}",
                "[{\"name\": \"Reflections\"}]");
        row.id = 8;
        row.name = "Reflections Sealed";
        row.leagueType = "SEALED";
        row.namePattern = "{series} - {month} {yyyy}";
        return row;
    }

    private League activeLeague(String name, String type) {
        return new League(_library, 0, name, type, NewConstructedLeagueData.class.getName(), "20261001,default,1,open,7,10", 0,
                true, false, false, false, "", 300, 50, null, 20261001, 20261009, 7);
    }

    private List<String> createdNames() throws Exception {
        ArgumentCaptor<String> names = ArgumentCaptor.forClass(String.class);
        Mockito.verify(_leagueDao, Mockito.atLeast(0)).addLeague(Mockito.anyInt(), names.capture(), Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyBoolean(), Mockito.anyBoolean(), Mockito.anyBoolean(),
                Mockito.anyBoolean(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt(), Mockito.any(), Mockito.any());
        return names.getAllValues();
    }

    // --- materialisation ------------------------------------------------------------------------------------------

    @Test
    public void NothingHappensBeforeTheLeadWindow() throws Exception {
        _scheduleDao.add(rotatingSchedule());

        List<LeagueScheduleService.Created> created = _service.processDueSchedules(SEP_20);

        assertTrue(created.isEmpty());
        assertTrue(createdNames().isEmpty());
        assertEquals(20261001, _scheduleDao.getSchedule(7).nextEventDate);
        assertEquals(0, _scheduleDao.progressUpdates);
    }

    @Test
    public void DueScheduleCreatesTheLeagueAndAdvances() throws Exception {
        //test1: the day the lead window opens the league is created through the factory, tagged with the schedule id
        //test2: the row moves to the next event and date, remembers the code, clears any error
        //test3: the next run finds nothing due
        _scheduleDao.add(rotatingSchedule());

        List<LeagueScheduleService.Created> created = _service.processDueSchedules(SEP_24);

        assertEquals(1, created.size());
        assertEquals("Constructed - Open", created.get(0).league().name());
        assertEquals(7, created.get(0).scheduleId());
        ArgumentCaptor<Integer> scheduleId = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<String> parameters = ArgumentCaptor.forClass(String.class);
        Mockito.verify(_leagueDao).addLeague(Mockito.eq(500), Mockito.eq("Constructed - Open"), Mockito.anyString(),
                Mockito.eq(NewConstructedLeagueData.class.getName()), parameters.capture(), Mockito.eq(20261001), Mockito.eq(20261009),
                Mockito.eq(true), Mockito.eq(false), Mockito.eq(true), Mockito.eq(false), Mockito.eq(""), Mockito.eq(300), Mockito.eq(50),
                Mockito.isNull(), scheduleId.capture());
        assertEquals("20261001,default,1,open,7,10", parameters.getValue()); //test1
        assertEquals(Integer.valueOf(7), scheduleId.getValue()); //test1
        Mockito.verify(_leagueService).clearCache();

        LeagueScheduleRow row = _scheduleDao.getSchedule(7);
        assertEquals(20261101, row.nextEventDate); //test2
        assertEquals(1, row.nextEventIndex); //test2
        assertEquals(created.get(0).code(), row.lastCreatedLeagueType); //test2
        assertNotNull(row.lastRun);
        assertNull(row.lastError); //test2

        assertTrue(_service.processDueSchedules(SEP_25).isEmpty()); //test3
        assertEquals(1, createdNames().size()); //test3
    }

    @Test
    public void RotationWrapsAroundToTheFirstEvent() throws Exception {
        LeagueScheduleRow row = rotatingSchedule();
        row.nextEventIndex = 1;
        _scheduleDao.add(row);

        _service.processDueSchedules(SEP_25);

        assertEquals(List.of("Constructed - Death Star II"), createdNames());
        assertEquals(0, _scheduleDao.getSchedule(7).nextEventIndex);
        assertEquals(20261101, _scheduleDao.getSchedule(7).nextEventDate);
    }

    @Test
    public void AScheduleThatFellBehindCatchesUpWithACap() throws Exception {
        //test1: several due occurrences are created in one run, up to the cap; the stale ones before them are skipped
        //test2: the rest come on the next run
        LeagueScheduleRow row = rotatingSchedule();
        row.intervalMonths = 0.25; // weekly
        row.leadDays = 21;
        row.nextEventDate = 20261001;
        _scheduleDao.add(row);

        // Oct 30: Oct 1, 8, 15, 22 are more than 2 days gone (skipped, the rotation turns 4 times); Oct 29, Nov 5, 12, 19 are due
        List<LeagueScheduleService.Created> created = _service.processDueSchedules(LocalDate.of(2026, 10, 30));

        assertEquals(LeagueScheduleService.MAX_CATCH_UP_PER_RUN, created.size()); //test1
        assertEquals(List.of("Constructed - Open", "Constructed - Death Star II", "Constructed - Open"), createdNames()); //test1
        assertEquals(20261119, _scheduleDao.getSchedule(7).nextEventDate);
        assertEquals(1, _scheduleDao.getSchedule(7).nextEventIndex);

        // Nov 30: Nov 19 and 26 are stale; Dec 3, 10, 17 are due (Dec 24 is not yet)
        assertEquals(3, _service.processDueSchedules(LocalDate.of(2026, 11, 30)).size()); //test2
        assertEquals(6, createdNames().size());
        assertEquals(20261224, _scheduleDao.getSchedule(7).nextEventDate);
    }

    @Test
    public void OccurrencesInThePastAreSkippedNotCreated() throws Exception {
        //test1: a schedule turned on months late skips every occurrence older than the grace
        //test2: the skip is noted on the row
        //test3: an occurrence exactly at the grace is still created
        LeagueScheduleRow row = rotatingSchedule();
        row.nextEventDate = 20260601;
        _scheduleDao.add(row);

        List<LeagueScheduleService.Created> created = _service.processDueSchedules(LocalDate.of(2026, 10, 3));

        assertEquals(1, created.size());
        assertEquals("Constructed - Open", created.get(0).league().name()); // June, July, August, September skipped: Oct 1 is index 0 again
        assertEquals(List.of("Constructed - Open"), createdNames()); //test1
        LeagueScheduleRow after = _scheduleDao.getSchedule(7);
        assertEquals(20261101, after.nextEventDate);
        assertEquals(1, after.nextEventIndex);
        assertTrue(after.lastError, after.lastError.contains("Skipped 4 occurrences")); //test2

        FakeScheduleDAO dao = new FakeScheduleDAO();
        LeagueScheduleRow edge = rotatingSchedule();
        dao.add(edge);
        LeagueScheduleService service = new LeagueScheduleService(dao, _factory, _leagueService, _leagueDao, _library);
        assertEquals(1, service.processDueSchedules(OCT_1.plusDays(LeagueScheduleService.PAST_GRACE_DAYS)).size()); //test3
        assertNull(dao.getSchedule(7).lastError);
    }

    @Test
    public void DisabledSchedulesAreSkipped() throws Exception {
        LeagueScheduleRow row = rotatingSchedule();
        row.enabled = false;
        _scheduleDao.add(row);

        assertTrue(_service.processDueSchedules(OCT_1).isEmpty());
        assertTrue(createdNames().isEmpty());
        assertEquals(20261001, _scheduleDao.getSchedule(7).nextEventDate);
    }

    @Test
    public void ALeagueAlreadyCreatedForTheStartDayIsNotCreatedAgain() throws Exception {
        //test1: the league table already holds this schedule's league for Oct 1 (a crash after the insert, or a second server):
        //       the row advances without a second insert
        //test2: the following occurrence is created normally
        _scheduleDao.add(rotatingSchedule());
        Mockito.when(_leagueDao.loadLeaguesOfScheduleStarting(_library, 7, 20261001)).thenReturn(List.of(activeLeague("Constructed - Open", "1700000000000")));

        List<LeagueScheduleService.Created> created = _service.processDueSchedules(OCT_1);

        assertTrue(created.isEmpty()); //test1
        assertTrue(createdNames().isEmpty()); //test1
        LeagueScheduleRow row = _scheduleDao.getSchedule(7);
        assertEquals(20261101, row.nextEventDate); //test1
        assertEquals(1, row.nextEventIndex);
        assertEquals("1700000000000", row.lastCreatedLeagueType);
        assertNull(row.lastError);

        created = _service.processDueSchedules(LocalDate.of(2026, 10, 26));
        assertEquals(List.of("Constructed - Death Star II"), createdNames()); //test2
        assertEquals(1, created.size());
    }

    @Test
    public void RunningTwiceOnTheSameDayCreatesOnce() throws Exception {
        _scheduleDao.add(rotatingSchedule());

        _service.processDueSchedules(SEP_25);
        _service.processDueSchedules(SEP_25);
        _service.processDueSchedules(SEP_25);

        assertEquals(1, createdNames().size());
    }

    @Test
    public void AnActiveLeagueWithTheSameNameBlocksCreation() throws Exception {
        //test1: the failure is recorded, the row does not move, nothing is inserted
        //test2: the failure clears once the league is gone and the next run succeeds
        _scheduleDao.add(rotatingSchedule());
        Mockito.when(_leagueService.getActiveLeagues()).thenReturn(List.of(activeLeague("constructed - OPEN", "x")));

        assertTrue(_service.processDueSchedules(OCT_1).isEmpty());

        LeagueScheduleRow row = _scheduleDao.getSchedule(7);
        assertEquals(20261001, row.nextEventDate); //test1
        assertTrue(row.lastError, row.lastError.contains("already active")); //test1
        assertTrue(createdNames().isEmpty()); //test1

        Mockito.when(_leagueService.getActiveLeagues()).thenReturn(List.of());
        assertEquals(1, _service.processDueSchedules(OCT_1).size()); //test2
        assertNull(_scheduleDao.getSchedule(7).lastError); //test2
    }

    @Test
    public void AnInvalidEventDefinitionIsRecordedNotThrown() throws Exception {
        LeagueScheduleRow row = rotatingSchedule();
        row.events = "[{\"name\": \"Broken\", \"params\": {\"series\": [{\"format\": \"no_such_format\", \"duration\": 7, \"matches\": 10}]}}]";
        _scheduleDao.add(row);

        assertTrue(_service.processDueSchedules(OCT_1).isEmpty());

        LeagueScheduleRow after = _scheduleDao.getSchedule(7);
        assertEquals(20261001, after.nextEventDate);
        assertTrue(after.lastError, after.lastError.contains("Unknown format 'no_such_format'"));
        assertTrue(createdNames().isEmpty());
    }

    @Test
    public void MalformedTemplateJsonIsRecordedNotThrown() throws Exception {
        LeagueScheduleRow row = rotatingSchedule();
        row.template = "{not json";
        _scheduleDao.add(row);

        assertTrue(_service.processDueSchedules(OCT_1).isEmpty());
        assertTrue(_scheduleDao.getSchedule(7).lastError.contains("template"));
        assertTrue(createdNames().isEmpty());
    }

    @Test
    public void AFailingScheduleDoesNotStopTheOthers() throws Exception {
        LeagueScheduleRow broken = rotatingSchedule();
        broken.template = "{not json";
        _scheduleDao.add(broken);
        _scheduleDao.add(sealedSchedule());

        List<LeagueScheduleService.Created> created = _service.processDueSchedules(OCT_1);

        assertEquals(1, created.size());
        assertEquals("Reflections Sealed - October 2026", created.get(0).league().name());
        assertEquals(8, created.get(0).scheduleId());
        assertTrue(created.get(0).league().parameters().startsWith("reflections_sealed,20261001,7,10,"));
        assertTrue(created.get(0).league().parameters().endsWith(",Reflections Sealed - October 2026"));
    }

    @Test
    public void RunNowIgnoresTheLeadTimeAndTheEnabledFlag() throws Exception {
        //test1: a disabled schedule far from its date still creates its next league on request
        //test2: an unknown id is null
        LeagueScheduleRow row = rotatingSchedule();
        row.enabled = false;
        row.nextEventDate = 20270601;
        _scheduleDao.add(row);

        LeagueScheduleService.Created created = _service.runNow(7);

        assertNotNull(created); //test1
        assertEquals("Constructed - Open", created.league().name());
        assertEquals(20270701, _scheduleDao.getSchedule(7).nextEventDate);
        assertNull(_service.runNow(999)); //test2
    }

    @Test
    public void TheHallTickEntryPointRunsAtMostHourly() throws Exception {
        // today is real time here: push the schedule's date to be due whatever day it is
        LeagueScheduleRow row = rotatingSchedule();
        LocalDate soon = LocalDate.now(java.time.ZoneOffset.UTC).plusDays(3);
        row.nextEventDate = soon.getYear() * 10000 + soon.getMonthValue() * 100 + soon.getDayOfMonth();
        _scheduleDao.add(row);

        _service.processDueSchedulesIfTime();
        _service.processDueSchedulesIfTime();

        assertEquals(1, createdNames().size());
        assertEquals(1, _scheduleDao.getSchedule(7).nextEventIndex);
    }

    // --- definitions ---------------------------------------------------------------------------------------------

    @Test
    public void AValidDefinitionIsAccepted() throws LeagueDefinitionException {
        _service.validateDefinition(rotatingSchedule());
        _service.validateDefinition(sealedSchedule());
    }

    private String rejection(LeagueScheduleRow row) {
        try {
            _service.validateDefinition(row);
            return null;
        } catch (LeagueDefinitionException exp) {
            return exp.getParameter() + ": " + exp.getMessage();
        }
    }

    @Test
    public void DefinitionRules() {
        LeagueScheduleRow row = rotatingSchedule();
        row.name = " ";
        assertTrue(rejection(row).startsWith("name:"));
        row = rotatingSchedule();
        row.leagueType = "RTMD";
        assertTrue(rejection(row).startsWith("leagueType:"));
        row = rotatingSchedule();
        row.nextEventDate = 20261301;
        assertTrue(rejection(row).startsWith("nextEventDate:"));
        row = rotatingSchedule();
        row.intervalMonths = 0;
        assertTrue(rejection(row).startsWith("intervalMonths:"));
        row = rotatingSchedule();
        row.leadDays = -1;
        assertTrue(rejection(row).startsWith("leadDays:"));
        row = rotatingSchedule();
        row.events = "[]";
        assertTrue(rejection(row).startsWith("events:"));
        row = rotatingSchedule();
        row.nextEventIndex = 2;
        assertTrue(rejection(row).startsWith("nextEventIndex:"));
        row = rotatingSchedule();
        row.template = "nope";
        assertTrue(rejection(row).startsWith("template:"));
    }

    @Test
    public void EveryEventMustProduceAValidLeague() {
        LeagueScheduleRow row = rotatingSchedule();
        row.events = "[{\"name\": \"Open\"}, {\"name\": \"Bad\", \"params\": {\"series\": [{\"format\": \"nope\", \"duration\": 7, \"matches\": 10}]}}]";
        String rejection = rejection(row);
        assertTrue(rejection, rejection.startsWith("events[1].series[0].format: Event 2: Unknown format 'nope'"));
    }

    @Test
    public void SaveStoresAndReloadsAndAnEnabledScheduleRunsAtOnce() throws Exception {
        //test1: a new row gets an id and comes back parsed
        //test2: an enabled schedule whose next event is within the lead time is created at save time
        //test3: a disabled one is not
        LeagueScheduleRow row = sealedSchedule();
        row.id = 0;
        row.enabled = true;
        LocalDate soon = LocalDate.now(java.time.ZoneOffset.UTC).plusDays(3);
        row.nextEventDate = soon.getYear() * 10000 + soon.getMonthValue() * 100 + soon.getDayOfMonth();

        LeagueSchedule saved = _service.saveSchedule(row);

        assertEquals(100, saved.getId()); //test1
        assertEquals("Reflections Sealed", saved.getName()); //test1
        assertEquals(LeagueFactory.Kind.SEALED, saved.getKind());
        assertEquals(1, createdNames().size()); //test2
        assertEquals(createdNames().get(0), saved.getLastCreatedLeagueType() == null ? null : createdNames().get(0));
        assertNotNull(saved.getLastCreatedLeagueType()); //test2

        LeagueScheduleRow off = rotatingSchedule();
        off.id = 0;
        off.enabled = false;
        off.nextEventDate = row.nextEventDate;
        LeagueSchedule savedOff = _service.saveSchedule(off);
        assertFalse(savedOff.isEnabled());
        assertEquals(1, createdNames().size()); //test3

        assertEquals(1, _service.setEnabled(savedOff.getId(), true).getNextEventIndex()); // enabling runs it
        assertEquals(2, createdNames().size());
        assertEquals(2, _service.getSchedules().size());
        _service.deleteSchedule(savedOff.getId());
        assertEquals(1, _service.getSchedules().size());
        assertNull(_service.getSchedule(savedOff.getId()));
    }

    @Test
    public void SaveRefusesABadDefinitionWithoutStoring() {
        LeagueScheduleRow row = rotatingSchedule();
        row.id = 0;
        row.events = "[]";
        try {
            _service.saveSchedule(row);
            fail("saved an empty rotation");
        } catch (LeagueDefinitionException exp) {
            assertEquals("events", exp.getParameter());
        }
        assertTrue(_scheduleDao.rows.isEmpty());
    }

    @Test
    public void ARowWithBrokenJsonIsStillListed() {
        LeagueScheduleRow row = rotatingSchedule();
        row.template = "{broken";
        _scheduleDao.add(row);

        List<LeagueSchedule> schedules = _service.getSchedules();

        assertEquals(1, schedules.size());
        assertEquals("Constructed", schedules.get(0).getName());
        assertTrue(schedules.get(0).getEvents().isEmpty());
        assertTrue(schedules.get(0).getLastError().contains("template"));
    }

    // --- projection ----------------------------------------------------------------------------------------------

    @Test
    public void ProjectionWalksTheRotationWithoutCreatingAnything() throws Exception {
        //test1: the next N occurrences alternate the events and step a month, with real series spans
        //test2: createdOn is start minus lead days
        //test3: nothing was created, the row did not move
        _scheduleDao.add(rotatingSchedule());
        LeagueSchedule schedule = _service.getSchedule(7);

        List<LeagueScheduleService.ProjectedEvent> next = _service.projectNext(schedule, 4);

        assertEquals(4, next.size());
        assertEquals("Constructed - Open", next.get(0).leagueName()); //test1
        assertEquals(0, next.get(0).eventIndex());
        assertEquals(LocalDate.of(2026, 10, 1), next.get(0).start());
        assertEquals(LocalDate.of(2026, 10, 7), next.get(0).end()); //test1
        assertEquals(LocalDate.of(2026, 9, 24), next.get(0).createdOn()); //test2
        assertNull(next.get(0).error());
        assertEquals("Constructed - Death Star II", next.get(1).leagueName()); //test1
        assertEquals(LocalDate.of(2026, 11, 1), next.get(1).start());
        assertEquals(LocalDate.of(2026, 11, 14), next.get(1).end()); //test1
        assertEquals(LocalDate.of(2026, 12, 1), next.get(2).start());
        assertEquals("Constructed - Open", next.get(2).leagueName());
        assertEquals(LocalDate.of(2027, 1, 1), next.get(3).start());
        assertTrue(createdNames().isEmpty()); //test3
        assertEquals(20261001, _scheduleDao.getSchedule(7).nextEventDate); //test3
    }

    @Test
    public void ProjectionOverARangeKeepsOnlyStartsInside() {
        _scheduleDao.add(rotatingSchedule());
        LeagueSchedule schedule = _service.getSchedule(7);

        List<LeagueScheduleService.ProjectedEvent> inRange = _service.project(schedule, LocalDate.of(2026, 11, 15), LocalDate.of(2027, 2, 15), 50);
        assertEquals(List.of(LocalDate.of(2026, 12, 1), LocalDate.of(2027, 1, 1), LocalDate.of(2027, 2, 1)),
                inRange.stream().map(LeagueScheduleService.ProjectedEvent::start).toList());

        List<LeagueScheduleService.ProjectedEvent> limited = _service.project(schedule, LocalDate.of(2026, 1, 1), LocalDate.of(2030, 1, 1), 5);
        assertEquals(5, limited.size());

        assertTrue(_service.project(schedule, LocalDate.of(2020, 1, 1), LocalDate.of(2020, 12, 31), 50).isEmpty());
    }

    @Test
    public void ProjectionReportsBrokenEventsInsteadOfFailing() {
        LeagueScheduleRow row = rotatingSchedule();
        row.events = "[{\"name\": \"Open\"}, {\"name\": \"Bad\", \"params\": {\"series\": [{\"format\": \"nope\", \"duration\": 7, \"matches\": 10}]}}]";
        _scheduleDao.add(row);
        LeagueSchedule schedule = _service.getSchedule(7);

        List<LeagueScheduleService.ProjectedEvent> next = _service.projectNext(schedule, 2);

        assertNull(next.get(0).error());
        assertNotNull(next.get(1).error());
        assertTrue(next.get(1).error().contains("nope"));
        assertEquals("Constructed - Bad", next.get(1).leagueName());
        assertEquals(LocalDate.of(2026, 11, 1), next.get(1).start());
        assertEquals(LocalDate.of(2026, 11, 1), next.get(1).end());
    }

    @Test
    public void ProjectionOfAnEmptyOrDatelessScheduleIsEmpty() {
        LeagueScheduleRow row = rotatingSchedule();
        row.events = "[]";
        _scheduleDao.add(row);
        assertTrue(_service.projectNext(_service.getSchedule(7), 3).isEmpty());
        LeagueScheduleRow none = rotatingSchedule();
        none.id = 9;
        none.nextEventDate = 0;
        _scheduleDao.add(none);
        assertTrue(_service.projectNext(_service.getSchedule(9), 3).isEmpty());
        assertTrue(_service.project(_service.getSchedule(9), OCT_1, OCT_1.plusYears(1), 3).isEmpty());
    }
}
