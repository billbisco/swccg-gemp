package com.gempukku.swccgo.events;

import com.gempukku.swccgo.db.LeagueDAO;
import com.gempukku.swccgo.db.vo.CollectionType;
import com.gempukku.swccgo.db.vo.League;
import com.gempukku.swccgo.draft2.SoloDraftDefinitions;
import com.gempukku.swccgo.game.Player;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.game.SwccgFormat;
import com.gempukku.swccgo.game.formats.SwccgoFormatLibrary;
import com.gempukku.swccgo.league.LeagueService;
import com.gempukku.swccgo.tournament.Tournament;
import com.gempukku.swccgo.tournament.TournamentDAO;
import com.gempukku.swccgo.tournament.TournamentInfo;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class CalendarServiceTests {
    private static final long OCT_1_2026_UTC = 1790812800000L;
    private static final long DAY = 24L * 60 * 60 * 1000;
    private static final long HOUR = 60L * 60 * 1000;
    private static final LocalDate SEP_28 = LocalDate.of(2026, 9, 28);
    private static final LocalDate NOV_1 = LocalDate.of(2026, 11, 1);
    private static final Player BILL = new Player(1, "Bill", "", "u", null, null, null, null);
    private static final Player ADMIN = new Player(2, "Boss", "", "ua", null, null, null, null);

    private LeagueDAO _leagueDao;
    private LeagueService _leagueService;
    private TournamentDAO _tournamentDao;
    private SwccgoFormatLibrary _formatLibrary;
    private SwccgCardBlueprintLibrary _library;
    private CalendarService _service;

    @Before
    public void setUp() throws Exception {
        _leagueDao = Mockito.mock(LeagueDAO.class);
        _leagueService = Mockito.mock(LeagueService.class);
        _tournamentDao = Mockito.mock(TournamentDAO.class);
        _formatLibrary = Mockito.mock(SwccgoFormatLibrary.class);
        _library = Mockito.mock(SwccgCardBlueprintLibrary.class);
        Mockito.when(_leagueDao.loadLeaguesOverlapping(Mockito.any(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(List.of());
        Mockito.when(_tournamentDao.getTournamentsStartingBetween(Mockito.anyLong(), Mockito.anyLong())).thenReturn(List.of());
        Mockito.when(_tournamentDao.getTournamentsPlayedBetween(Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong())).thenReturn(Set.of());
        _service = new CalendarService(_leagueDao, _leagueService, _tournamentDao, _library, _formatLibrary, Mockito.mock(SoloDraftDefinitions.class));
    }

    private League league(String type, String name, String series) {
        return new League(_library, 500, name, type, TestLeagueData.class.getName(), series, 1, true, false, true, false, null, 300, 50, null, 0, 0);
    }

    private static TournamentInfo tournament(String id, String name, long start, Tournament.Stage stage, int round) {
        return new TournamentInfo(id, null, name, "open", new CollectionType("default", "All cards"), stage, "swiss", "none", round, start);
    }

    private void stubFormat(String code, String name) {
        SwccgFormat format = Mockito.mock(SwccgFormat.class);
        Mockito.when(format.getName()).thenReturn(name);
        Mockito.when(_formatLibrary.getFormat(code)).thenReturn(format);
    }

    @SuppressWarnings("unchecked")
    private static List<Map<String, Object>> events(Map<String, Object> calendar) {
        return (List<Map<String, Object>>) calendar.get("events");
    }

    private static Map<String, Object> only(List<Map<String, Object>> events, String id) {
        return events.stream().filter(x -> id.equals(x.get("id"))).findFirst()
                .orElseThrow(() -> new AssertionError("No event with id " + id + " in " + events));
    }

    // --- request parsing (the handler's 400s) ---------------------------------------------------------------

    @Test
    public void CalendarParsesDaysAndRejectsAnythingElse() {
        assertEquals(LocalDate.of(2026, 10, 20), CalendarService.parseDate("2026-10-20"));
        assertEquals(LocalDate.of(2026, 10, 20), CalendarService.parseDate(" 2026-10-20 "));
        assertNull(CalendarService.parseDate(null));
        assertNull(CalendarService.parseDate(""));
        assertNull(CalendarService.parseDate("2026-10"));
        assertNull(CalendarService.parseDate("20261020"));
        assertNull(CalendarService.parseDate("2026-13-01"));
    }

    @Test
    public void CalendarValidatesTheRange() {
        //test1: a month grid (up to 6 weeks) is fine
        //test2: a missing end, a backwards range and one over the limit are refused with a reason
        assertNull(CalendarService.validateRange(SEP_28, NOV_1)); //test1
        assertNull(CalendarService.validateRange(SEP_28, SEP_28)); //test1
        assertTrue(CalendarService.validateRange(null, NOV_1).contains("'from'")); //test2
        assertTrue(CalendarService.validateRange(SEP_28, null).contains("'to'")); //test2
        assertTrue(CalendarService.validateRange(NOV_1, SEP_28).contains("before")); //test2
        assertNull(CalendarService.validateRange(SEP_28, SEP_28.plusDays(CalendarService.MAX_RANGE_DAYS)));
        assertTrue(CalendarService.validateRange(SEP_28, SEP_28.plusDays(CalendarService.MAX_RANGE_DAYS + 1)).contains("at most")); //test2
    }

    // --- the response --------------------------------------------------------------------------------------

    @Test
    public void CalendarEchoesTheRangeAndWhetherTheViewerIsAnEventAdmin() {
        Map<String, Object> player = _service.getCalendar(SEP_28, NOV_1, BILL);
        Map<String, Object> admin = _service.getCalendar(SEP_28, NOV_1, ADMIN);

        assertEquals(List.of("from", "to", "eventAdmin", "events"), List.copyOf(player.keySet()));
        assertEquals("2026-09-28", player.get("from"));
        assertEquals("2026-11-01", player.get("to"));
        assertEquals(false, player.get("eventAdmin"));
        assertEquals(true, admin.get("eventAdmin"));
        assertTrue(events(player).isEmpty());
    }

    @Test
    public void CalendarAsksForLeaguesTouchingTheRangeAndTournamentsStartingInIt() throws Exception {
        _service.getCalendar(SEP_28, NOV_1, BILL);

        //test1: leagues by yyyyMMdd day, both ends inclusive
        //test2: tournaments by millisecond, from midnight starting 'from' to midnight after 'to'
        Mockito.verify(_leagueDao).loadLeaguesOverlapping(Mockito.same(_library), Mockito.eq(20260928), Mockito.eq(20261101)); //test1
        Mockito.verify(_tournamentDao).getTournamentsStartingBetween(OCT_1_2026_UTC - 3 * DAY, OCT_1_2026_UTC + 32 * DAY); //test2
        Mockito.verify(_tournamentDao).getTournamentsPlayedBetween("Bill", OCT_1_2026_UTC - 3 * DAY, OCT_1_2026_UTC + 32 * DAY); //test2
    }

    @Test
    public void CalendarLeagueCarriesItsSeriesAndMembership() throws Exception {
        stubFormat("open", "Open");
        League league = league("fall", "Fall League", "20261001:20261014:open;20261015:20261028:legacy");
        Mockito.when(_leagueDao.loadLeaguesOverlapping(Mockito.any(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(List.of(league));
        Mockito.when(_leagueService.isPlayerInLeague(league, BILL)).thenReturn(true);

        Map<String, Object> event = only(events(_service.getCalendar(SEP_28, NOV_1, BILL)), "fall");

        assertEquals("league", event.get("kind"));
        assertEquals("Fall League", event.get("name"));
        assertEquals("2026-10-01", event.get("start"));
        assertEquals("2026-10-28", event.get("end"));
        assertEquals(500, event.get("cost"));
        assertEquals(false, event.get("inviteOnly"));
        assertEquals(true, event.get("joined"));
        assertTrue(event.containsKey("description"));
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> series = (List<Map<String, Object>>) event.get("series");
        assertEquals(2, series.size());
        assertEquals("Serie 1", series.get(0).get("name"));
        assertEquals("2026-10-01", series.get(0).get("start"));
        assertEquals("2026-10-14", series.get(0).get("end"));
        assertEquals("Open", series.get(0).get("format"));
        assertEquals("legacy", series.get(1).get("format"));   // a code the library does not know stays a code
        assertEquals("2026-10-15", series.get(1).get("start"));
    }

    @Test
    public void CalendarLeagueWhoseSeriesMissTheRangeIsLeftOut() throws Exception {
        //test1: the row's stored span matched the query, but the series (what the hall shows) end before the range
        //test2: a league whose definition does not build is skipped, the others stay
        Mockito.when(_leagueDao.loadLeaguesOverlapping(Mockito.any(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(List.of(
                league("early", "Early", "20260901:20260920:open"),
                new League(_library, 0, "Broken", "broken", "com.gempukku.swccgo.league.NoSuchLeagueData", "x", 1, true, false, true, false, null, 300, 50, null, 20261001, 20261010),
                league("late", "Late", "20261102:20261120:open"),
                league("in", "In", "20261020:20261120:open")));

        List<Map<String, Object>> events = events(_service.getCalendar(SEP_28, NOV_1, BILL));

        assertEquals(List.of("in"), events.stream().map(x -> x.get("id")).toList()); //test1 //test2
        assertEquals(false, only(events, "in").get("joined"));
    }

    @Test
    public void CalendarTournamentIsAOneDayEventWithItsStartTime() {
        stubFormat("open", "Open");
        Mockito.when(_tournamentDao.getTournamentsStartingBetween(Mockito.anyLong(), Mockito.anyLong())).thenReturn(List.of(
                tournament("t1", "Tuesday Open", OCT_1_2026_UTC + 5 * DAY + 19 * HOUR + 30 * 60 * 1000, Tournament.Stage.FINISHED, 4),
                tournament("t2", "Running Now", OCT_1_2026_UTC + 19 * DAY + 18 * HOUR, Tournament.Stage.PLAYING_GAMES, 2)));
        Mockito.when(_tournamentDao.getTournamentsPlayedBetween(Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong())).thenReturn(Set.of("t2"));

        List<Map<String, Object>> events = events(_service.getCalendar(SEP_28, NOV_1, BILL));
        Map<String, Object> finished = only(events, "t1");
        Map<String, Object> running = only(events, "t2");

        assertEquals("tournament", finished.get("kind"));
        assertEquals("Tuesday Open", finished.get("name"));
        assertEquals("Open", finished.get("format"));
        assertEquals("2026-10-06", finished.get("start"));
        assertEquals("2026-10-06", finished.get("end"));
        assertEquals("2026-10-06T19:30:00", finished.get("startTime"));
        assertEquals(true, finished.get("started"));
        assertEquals(true, finished.get("finished"));
        assertEquals("Finished", finished.get("stage"));
        assertEquals(4, finished.get("rounds"));
        assertEquals(false, finished.get("joined"));
        assertEquals(false, finished.get("queueOpen"));
        assertEquals(false, running.get("finished"));
        assertEquals("Playing games", running.get("stage"));
        assertEquals(true, running.get("joined"));
    }

    @Test
    public void CalendarFinishedTournamentWithNoRoundPlayedIsNotAnEvent() {
        Mockito.when(_tournamentDao.getTournamentsStartingBetween(Mockito.anyLong(), Mockito.anyLong())).thenReturn(List.of(
                tournament("bots", "Bot Draft", OCT_1_2026_UTC + 5 * DAY, Tournament.Stage.FINISHED, 0),
                tournament("fresh", "Just Made", OCT_1_2026_UTC + 6 * DAY, Tournament.Stage.DECK_BUILDING, 0)));

        List<Map<String, Object>> events = events(_service.getCalendar(SEP_28, NOV_1, BILL));

        assertEquals(List.of("fresh"), events.stream().map(x -> x.get("id")).toList());
    }

    @Test
    public void CalendarMembershipLookupFailuresDoNotHideTheEvent() throws Exception {
        League league = league("fall", "Fall League", "20261001:20261014:open");
        Mockito.when(_leagueDao.loadLeaguesOverlapping(Mockito.any(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(List.of(league));
        Mockito.when(_leagueService.isPlayerInLeague(Mockito.any(), Mockito.any())).thenThrow(new RuntimeException("db"));
        Mockito.when(_tournamentDao.getTournamentsPlayedBetween(Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong())).thenThrow(new RuntimeException("db"));
        Mockito.when(_tournamentDao.getTournamentsStartingBetween(Mockito.anyLong(), Mockito.anyLong())).thenReturn(List.of(
                tournament("t1", "Open", OCT_1_2026_UTC + 5 * DAY, Tournament.Stage.FINISHED, 3)));

        List<Map<String, Object>> events = events(_service.getCalendar(SEP_28, NOV_1, BILL));

        assertEquals(2, events.size());
        assertFalse((Boolean) only(events, "fall").get("joined"));
        assertFalse((Boolean) only(events, "t1").get("joined"));
    }
}
