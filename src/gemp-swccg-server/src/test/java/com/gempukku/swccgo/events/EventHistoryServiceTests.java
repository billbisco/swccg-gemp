package com.gempukku.swccgo.events;

import com.gempukku.swccgo.DateUtils;
import com.gempukku.swccgo.cache.CacheManager;
import com.gempukku.swccgo.db.LeagueDAO;
import com.gempukku.swccgo.db.vo.CollectionType;
import com.gempukku.swccgo.db.vo.League;
import com.gempukku.swccgo.draft2.SoloDraftDefinitions;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.game.SwccgFormat;
import com.gempukku.swccgo.game.formats.SwccgoFormatLibrary;
import com.gempukku.swccgo.tournament.Tournament;
import com.gempukku.swccgo.tournament.TournamentDAO;
import com.gempukku.swccgo.tournament.TournamentInfo;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

/**
 * The completed-event browser's service layer (ported from the LOTR EventHistoryServiceTest and rewritten for this
 * database's dates).  The DAOs are mocked, so the SQL itself is covered by EventQueryTests; here it is that the
 * right half-open ranges are asked for and that the rows are assembled correctly.
 */
public class EventHistoryServiceTests {
    private static final long OCT_1_2026_UTC = 1790812800000L;
    private static final long DAY = 24L * 60 * 60 * 1000;
    private static final long HOUR = 60L * 60 * 1000;
    private static final String T = EventHistoryService.KIND_TOURNAMENT;
    private static final String L = EventHistoryService.KIND_LEAGUE;
    private static final YearMonth OCTOBER = YearMonth.of(2026, 10);
    /** Leagues are listed once their end day has passed, so the league tests live in a month that is over. */
    private static final YearMonth MARCH = YearMonth.of(2026, 3);

    private LeagueDAO _leagueDao;
    private TournamentDAO _tournamentDao;
    private SwccgoFormatLibrary _formatLibrary;
    private SwccgCardBlueprintLibrary _library;
    private EventHistoryService _service;

    @Before
    public void setUp() throws Exception {
        _leagueDao = Mockito.mock(LeagueDAO.class);
        _tournamentDao = Mockito.mock(TournamentDAO.class);
        _formatLibrary = Mockito.mock(SwccgoFormatLibrary.class);
        _library = Mockito.mock(SwccgCardBlueprintLibrary.class);

        Mockito.when(_tournamentDao.getFinishedTournamentsBetween(Mockito.anyLong(), Mockito.anyLong())).thenReturn(List.of());
        Mockito.when(_tournamentDao.getFinishedTournamentPlayerCountsBetween(Mockito.anyLong(), Mockito.anyLong())).thenReturn(Map.of());
        Mockito.when(_leagueDao.loadLeaguesEndingBetween(Mockito.any(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(List.of());
        Mockito.when(_leagueDao.getParticipantCountsForLeaguesEndingBetween(Mockito.anyInt(), Mockito.anyInt())).thenReturn(Map.of());

        _service = new EventHistoryService(_leagueDao, _tournamentDao, _library, _formatLibrary, Mockito.mock(SoloDraftDefinitions.class));
    }

    private static TournamentInfo tournament(String id, String name, long start, String formatCode, int round) {
        return new TournamentInfo(id, null, name, formatCode, new CollectionType("default", "All cards"),
                Tournament.Stage.FINISHED, "swiss", "none", round, start);
    }

    /** A league whose definition builds (TestLeagueData), one serie, format "premiere". */
    private League league(String type, String name, int start, int end) {
        return league(type, name, start, end, TestLeagueData.class.getName(), start + ":" + end + ":premiere");
    }

    private League league(String type, String name, int start, int end, String clazz, String parameters) {
        return new League(_library, 0, name, type, clazz, parameters, 1, true, false, true, false, null, 300, 50, null, start, end);
    }

    private void stubFormat(String code, String name) {
        SwccgFormat format = Mockito.mock(SwccgFormat.class);
        Mockito.when(format.getName()).thenReturn(name);
        Mockito.when(_formatLibrary.getFormat(code)).thenReturn(format);
    }

    private static Map<String, Object> only(List<Map<String, Object>> events, String id) {
        return events.stream().filter(x -> id.equals(x.get("id"))).findFirst()
                .orElseThrow(() -> new AssertionError("No event with id " + id + " in " + events));
    }

    private void stubTournaments(TournamentInfo... rows) {
        Mockito.when(_tournamentDao.getFinishedTournamentsBetween(Mockito.anyLong(), Mockito.anyLong())).thenReturn(List.of(rows));
    }

    private void stubLeagues(League... rows) throws Exception {
        Mockito.when(_leagueDao.loadLeaguesEndingBetween(Mockito.any(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(List.of(rows));
    }

    @SuppressWarnings("unchecked")
    private static List<Map<String, Object>> adminLinksOf(Map<String, Object> event) {
        return (List<Map<String, Object>>) event.get("adminLinks");
    }

    private static List<Object> ids(List<Map<String, Object>> events) {
        return events.stream().map(x -> x.get("id")).toList();
    }

    // --- request parsing (the handler's 400s) ---------------------------------------------------------------

    @Test
    public void EventHistoryParsesAWellFormedMonth() {
        assertEquals(YearMonth.of(2026, 3), EventHistoryService.parseMonth("2026-03"));
        assertEquals(YearMonth.of(2026, 12), EventHistoryService.parseMonth(" 2026-12 "));
    }

    @Test
    public void EventHistoryRejectsAMissingOrMalformedMonth() {
        assertNull(EventHistoryService.parseMonth(null));
        assertNull(EventHistoryService.parseMonth(""));
        assertNull(EventHistoryService.parseMonth("   "));
        assertNull(EventHistoryService.parseMonth("2026"));
        assertNull(EventHistoryService.parseMonth("2026-13"));
        assertNull(EventHistoryService.parseMonth("2026-03-04"));
        assertNull(EventHistoryService.parseMonth("March"));
    }

    @Test
    public void EventHistoryParsesBothKinds() {
        assertEquals("league", EventHistoryService.parseKind("league"));
        assertEquals("tournament", EventHistoryService.parseKind("tournament"));
        assertEquals("tournament", EventHistoryService.parseKind(" Tournament "));
    }

    @Test
    public void EventHistoryRejectsAMissingOrUnknownKind() {
        assertNull(EventHistoryService.parseKind(null));
        assertNull(EventHistoryService.parseKind(""));
        assertNull(EventHistoryService.parseKind("leagues"));
        assertNull(EventHistoryService.parseKind("all"));
        assertNull(EventHistoryService.parseKind("league,tournament"));
    }

    @Test
    public void EventHistoryRefusesAnUnparsedKind() {
        assertThrows(IllegalArgumentException.class, () -> _service.getMonth(OCTOBER, "bogus"));
        assertThrows(IllegalArgumentException.class, () -> _service.getAvailableMonths(null));
    }

    // --- kind filtering and bucketing -----------------------------------------------------------------------

    @Test
    public void EventHistoryEmptyMonthIsAnEmptyList() {
        assertTrue(_service.getMonth(YearMonth.of(2026, 5), T).isEmpty());
        assertTrue(_service.getMonthEvents(YearMonth.of(2026, 5), T, true).isEmpty());
        assertTrue(_service.getMonthEvents(YearMonth.of(2026, 5), L, true).isEmpty());
    }

    @Test
    public void EventHistoryTournamentRequestNeverQueriesLeagues() throws Exception {
        stubTournaments(tournament("t1", "October Open", OCT_1_2026_UTC + 7 * DAY + 19 * HOUR, "open", 5));
        stubLeagues(league("mar_league", "March League", 20260301, 20260321));

        var events = _service.getMonthEvents(OCTOBER, T, false);

        assertEquals(List.of("t1"), ids(events));
        Mockito.verify(_leagueDao, Mockito.never()).loadLeaguesEndingBetween(Mockito.any(), Mockito.anyInt(), Mockito.anyInt());
        Mockito.verify(_leagueDao, Mockito.never()).getParticipantCountsForLeaguesEndingBetween(Mockito.anyInt(), Mockito.anyInt());
    }

    @Test
    public void EventHistoryLeagueRequestNeverQueriesTournaments() throws Exception {
        stubTournaments(tournament("t1", "October Open", OCT_1_2026_UTC + 7 * DAY + 19 * HOUR, "open", 5));
        stubLeagues(league("mar_league", "March League", 20260301, 20260321));

        var events = _service.getMonthEvents(MARCH, L, false);

        assertEquals(List.of("mar_league"), ids(events));
        Mockito.verify(_tournamentDao, Mockito.never()).getFinishedTournamentsBetween(Mockito.anyLong(), Mockito.anyLong());
        Mockito.verify(_tournamentDao, Mockito.never()).getFinishedTournamentPlayerCountsBetween(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    public void EventHistoryTournamentsAreBucketedByStartOverAHalfOpenMillisecondRange() {
        //test1: October is [Oct 1 00:00 UTC, Nov 1 00:00 UTC)
        //test2: December rolls into the next year
        _service.getMonth(OCTOBER, T);
        var from = ArgumentCaptor.forClass(Long.class);
        var to = ArgumentCaptor.forClass(Long.class);
        Mockito.verify(_tournamentDao).getFinishedTournamentsBetween(from.capture(), to.capture());
        assertEquals(OCT_1_2026_UTC, (long) from.getValue()); //test1
        assertEquals(OCT_1_2026_UTC + 31 * DAY, (long) to.getValue()); //test1

        _service.getMonth(YearMonth.of(2026, 12), T);
        Mockito.verify(_tournamentDao).getFinishedTournamentsBetween(
                EventDates.startOfDayMillis(LocalDate.of(2026, 12, 1)), EventDates.startOfDayMillis(LocalDate.of(2027, 1, 1))); //test2
    }

    @Test
    public void EventHistoryTournamentStartingLateOnTheLastDayIsIncluded() {
        // The upper bound is the next month's midnight, not the last day's: start is an instant.
        stubTournaments(tournament("t1", "Late Night", OCT_1_2026_UTC + 31 * DAY - 1000, "open", 3));

        var events = _service.getMonthEvents(OCTOBER, T, false);

        assertEquals(1, events.size());
        assertEquals("2026-10-31", only(events, "t1").get("startDate"));
    }

    @Test
    public void EventHistoryLeaguesAreBucketedByEndDayOverAHalfOpenRange() throws Exception {
        _service.getMonth(MARCH, L);

        Mockito.verify(_leagueDao).loadLeaguesEndingBetween(Mockito.same(_library), Mockito.eq(20260301), Mockito.eq(20260401));
        Mockito.verify(_leagueDao).getParticipantCountsForLeaguesEndingBetween(20260301, 20260401);
    }

    @Test
    public void EventHistoryLeagueIsListedUnderTheMonthItEndedInWithItsSeriesDates() throws Exception {
        // Spans February into March; the DAO buckets it by end, and the dates shown are the series'
        stubLeagues(league("spring", "Spring League", 20260220, 20260314));

        var events = _service.getMonthEvents(MARCH, L, false);

        assertEquals(1, events.size());
        assertEquals("2026-02-20", only(events, "spring").get("startDate"));
        assertEquals("2026-03-14", only(events, "spring").get("endDate"));
    }

    // --- only completed events ----------------------------------------------------------------------------

    @Test
    public void EventHistoryLeagueThatHasNotEndedYetIsNotListed() throws Exception {
        int today = DateUtils.getCurrentDate();
        LocalDate todayDate = EventDates.fromDateInt(today);
        int yesterday = EventDates.toDateInt(todayDate.minusDays(1));
        int tomorrow = EventDates.toDateInt(todayDate.plusDays(3));
        int earlier = EventDates.toDateInt(todayDate.minusDays(20));
        stubLeagues(
                league("running", "Still Running", earlier, tomorrow),
                league("today", "Ends Today", earlier, today),
                league("over", "Over", earlier, yesterday));

        var events = _service.getMonthEvents(YearMonth.from(todayDate), L, false);

        assertEquals(List.of("over"), ids(events));
    }

    @Test
    public void EventHistoryFinishedTournamentWithNoRoundPlayedIsNotListed() {
        stubTournaments(
                tournament("t0", "Bot Draft", OCT_1_2026_UTC + 3 * DAY, "open", 0),
                tournament("t1", "Real Event", OCT_1_2026_UTC + 3 * DAY + HOUR, "open", 4));

        assertEquals(List.of("t1"), ids(_service.getMonthEvents(OCTOBER, T, false)));
    }

    // --- shape, counts and ordering -----------------------------------------------------------------------

    @Test
    public void EventHistoryTournamentRowsCarryExactlyTheContractedFields() {
        stubFormat("open", "Open");
        stubTournaments(tournament("t1", "October Open", OCT_1_2026_UTC + 7 * DAY + 19 * HOUR, "open", 5));
        Mockito.when(_tournamentDao.getFinishedTournamentPlayerCountsBetween(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(Map.of("t1", 14));

        var event = only(_service.getMonthEvents(OCTOBER, T, false), "t1");

        assertEquals(List.of("id", "name", "startDate", "endDate", "format", "playerCount", "rounds"), List.copyOf(event.keySet()));
        assertEquals("October Open", event.get("name"));
        assertEquals("2026-10-08", event.get("startDate"));
        assertNull(event.get("endDate"));                       // tournament has no finish column
        assertEquals("Open", event.get("format"));
        assertEquals(14, event.get("playerCount"));
        assertEquals(5, event.get("rounds"));
    }

    @Test
    public void EventHistoryLeagueRowsCarryExactlyTheContractedFields() throws Exception {
        stubFormat("premiere", "Premiere");
        stubLeagues(league("mar_league", "March League", 20260301, 20260321));
        Mockito.when(_leagueDao.getParticipantCountsForLeaguesEndingBetween(Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(Map.of("mar_league", 42));

        var event = only(_service.getMonthEvents(MARCH, L, false), "mar_league");

        assertEquals(List.of("id", "name", "startDate", "endDate", "format", "playerCount", "rounds"), List.copyOf(event.keySet()));
        assertEquals("March League", event.get("name"));
        assertEquals("2026-03-01", event.get("startDate"));
        assertEquals("2026-03-21", event.get("endDate"));
        assertEquals("Premiere", event.get("format"));
        assertEquals(42, event.get("playerCount"));
        assertNull(event.get("rounds"));                        // leagues have series, not rounds
    }

    @Test
    public void EventHistoryPlayerCountThatIsNotAvailableIsNullRatherThanZero() {
        stubTournaments(tournament("t1", "October Open", OCT_1_2026_UTC + 7 * DAY, "open", 5));

        var event = only(_service.getMonthEvents(OCTOBER, T, false), "t1");

        assertNull(event.get("playerCount"));
        assertTrue(event.containsKey("playerCount"));
    }

    @Test
    public void EventHistoryEventsAreSortedByStartDateDescendingThenName() throws Exception {
        stubTournaments(
                tournament("a", "Zulu", OCT_1_2026_UTC + 3 * DAY + 19 * HOUR, "open", 2),
                tournament("b", "Alpha", OCT_1_2026_UTC + 3 * DAY + 20 * HOUR, "open", 2),
                tournament("c", "Middle", OCT_1_2026_UTC + 8 * DAY + 20 * HOUR, "open", 2));
        stubLeagues(
                league("7", "B League", 20260306, 20260320),
                league("8", "A League", 20260306, 20260322),
                league("9", "C League", 20260310, 20260325));

        assertEquals(List.of("c", "b", "a"), ids(_service.getMonthEvents(OCTOBER, T, false)));
        assertEquals(List.of("9", "8", "7"), ids(_service.getMonthEvents(MARCH, L, false)));
    }

    // --- admin fields --------------------------------------------------------------------------------------

    @Test
    public void EventHistoryAdminLinksAreAbsentForANonAdmin() throws Exception {
        stubTournaments(tournament("t1", "October Open", OCT_1_2026_UTC + 7 * DAY, "open", 5));
        stubLeagues(league("mar_league", "March League", 20260301, 20260321));

        for (String kind : List.of(T, L)) {
            var events = _service.getMonthEvents(OCTOBER, kind, false);
            assertEquals(1, events.size());
            assertFalse("A non-admin must not be sent adminLinks", events.get(0).containsKey("adminLinks"));
        }
    }

    @Test
    public void EventHistoryAdminLinksAreTheIdsAsSelectableText() throws Exception {
        //test1: a tournament's id, no report page on this server so no url
        //test2: a league's type code, as plain text
        stubTournaments(tournament("1712345678901", "October Open", OCT_1_2026_UTC + 7 * DAY, "open", 5));
        stubLeagues(league("mar_league", "March League", 20260301, 20260321));

        var tournamentLinks = adminLinksOf(only(_service.getMonthEvents(OCTOBER, T, true), "1712345678901"));
        assertEquals(1, tournamentLinks.size()); //test1
        assertEquals(List.of("label", "text", "url"), List.copyOf(tournamentLinks.get(0).keySet())); //test1
        assertEquals("Id", tournamentLinks.get(0).get("label")); //test1
        assertEquals("1712345678901", tournamentLinks.get(0).get("text")); //test1
        assertNull(tournamentLinks.get(0).get("url")); //test1

        var leagueLinks = adminLinksOf(only(_service.getMonthEvents(MARCH, L, true), "mar_league"));
        assertEquals("Code", leagueLinks.get(0).get("label")); //test2
        assertEquals("mar_league", leagueLinks.get(0).get("text")); //test2
        assertTrue(leagueLinks.get(0).containsKey("url")); //test2
        assertNull(leagueLinks.get(0).get("url")); //test2
    }

    @Test
    public void EventHistoryOneCachedMonthServesAdminsAndNonAdminsAlike() {
        stubTournaments(tournament("t1", "October Open", OCT_1_2026_UTC + 7 * DAY, "open", 5));

        _service.getMonthEvents(OCTOBER, T, true);
        var asPlayer = _service.getMonthEvents(OCTOBER, T, false);

        assertFalse(only(asPlayer, "t1").containsKey("adminLinks"));
        Mockito.verify(_tournamentDao, Mockito.times(1)).getFinishedTournamentsBetween(Mockito.anyLong(), Mockito.anyLong());
    }

    // --- robustness ----------------------------------------------------------------------------------------

    @Test
    public void EventHistoryRetiredTournamentFormatDegradesToItsRawCode() {
        Mockito.when(_formatLibrary.getFormat("ancient")).thenReturn(null);
        stubTournaments(tournament("t1", "Old Event", OCT_1_2026_UTC + 7 * DAY, "ancient", 3));

        assertEquals("ancient", only(_service.getMonthEvents(OCTOBER, T, false), "t1").get("format"));
    }

    @Test
    public void EventHistoryTournamentWithoutAFormatFallsBackToItsDraftTypeOrUnknown() {
        TournamentInfo draft = new TournamentInfo("t1", "cube", "Cube Night", null, new CollectionType("default", "All cards"),
                Tournament.Stage.FINISHED, "swiss", "none", 3, OCT_1_2026_UTC + 7 * DAY);
        TournamentInfo bare = new TournamentInfo("t2", null, "Bare", "", new CollectionType("default", "All cards"),
                Tournament.Stage.FINISHED, "swiss", "none", 3, OCT_1_2026_UTC + 8 * DAY);
        stubTournaments(draft, bare);

        var events = _service.getMonthEvents(OCTOBER, T, false);

        assertEquals("cube", only(events, "t1").get("format"));
        assertEquals(EventHistoryService.UNKNOWN_FORMAT, only(events, "t2").get("format"));
    }

    @Test
    public void EventHistoryLeagueWhoseDefinitionNoLongerBuildsStillListsFromItsRow() throws Exception {
        //test1: the row's own start / end stand in for the series
        //test2: the format comes from the raw constructed parameters (start, collection, series, format...)
        //test3: a league whose raw parameters are not the constructed layout gets its kind as the format
        stubFormat("legacy", "Legacy");
        stubLeagues(
                league("broken", "Unbuildable", 20260301, 20260314, "com.gempukku.swccgo.league.NoSuchLeagueData", "20260301,default,1,legacy,14,6"),
                league("sealed", "Sealed Unbuildable", 20260303, 20260317, "com.gempukku.swccgo.league.NewSealedLeagueDataGone", "20260303,hoth_sealed,1,14,6"),
                league("fine", "Fine", 20260301, 20260321));

        var events = _service.getMonthEvents(MARCH, L, false);

        assertEquals(3, events.size());
        assertEquals("2026-03-01", only(events, "broken").get("startDate")); //test1
        assertEquals("2026-03-14", only(events, "broken").get("endDate")); //test1
        assertEquals("Legacy", only(events, "broken").get("format")); //test2
        assertEquals("Sealed", only(events, "sealed").get("format")); //test3
        assertNotNull(only(events, "fine").get("format"));
    }

    @Test
    public void EventHistoryRawConstructedParametersGiveTheFirstFormatCode() {
        assertEquals("open", EventHistoryService.rawLeagueFormatCode("20261001,default,2,open,14,6,legacy,14,6"));
        assertEquals("premiere", EventHistoryService.rawLeagueFormatCode("20261001,permanent,1,premiere,21,10"));
        assertNull(EventHistoryService.rawLeagueFormatCode("20261001,hoth_sealed,1,14,6"));   // a sealed league's product code is not a format
        assertNull(EventHistoryService.rawLeagueFormatCode("20261001,default,x,open"));
        assertNull(EventHistoryService.rawLeagueFormatCode(""));
        assertNull(EventHistoryService.rawLeagueFormatCode(null));
    }

    @Test
    public void EventHistoryFailingMonthQueryPropagatesAndIsNotCached() {
        Mockito.when(_tournamentDao.getFinishedTournamentsBetween(Mockito.anyLong(), Mockito.anyLong()))
                .thenThrow(new RuntimeException("database is down"))
                .thenReturn(List.of(tournament("t1", "October Open", OCT_1_2026_UTC + 7 * DAY, "open", 5)));

        assertThrows(RuntimeException.class, () -> _service.getMonthEvents(OCTOBER, T, false));
        assertEquals(0, _service.getItemCount());

        assertEquals(1, _service.getMonthEvents(OCTOBER, T, false).size());
    }

    @Test
    public void EventHistoryFailingPlayerCountsDegradeToNullAndAreNotCached() {
        stubTournaments(tournament("t1", "October Open", OCT_1_2026_UTC + 7 * DAY, "open", 5));
        Mockito.when(_tournamentDao.getFinishedTournamentPlayerCountsBetween(Mockito.anyLong(), Mockito.anyLong()))
                .thenThrow(new RuntimeException("timeout"))
                .thenReturn(Map.of("t1", 8));

        assertNull(only(_service.getMonthEvents(OCTOBER, T, false), "t1").get("playerCount"));
        assertEquals(0, _service.getItemCount());
        assertEquals(8, only(_service.getMonthEvents(OCTOBER, T, false), "t1").get("playerCount"));
        assertEquals(1, _service.getItemCount());
    }

    // --- caching -------------------------------------------------------------------------------------------

    @Test
    public void EventHistoryMonthIsOnlyQueriedOncePerKind() throws Exception {
        stubTournaments(tournament("t1", "October Open", OCT_1_2026_UTC + 7 * DAY, "open", 5));

        var first = _service.getMonth(OCTOBER, T);
        var second = _service.getMonth(OCTOBER, T);
        _service.getMonth(MARCH, L);
        _service.getMonth(MARCH, L);

        assertSame(first, second);
        Mockito.verify(_tournamentDao, Mockito.times(1)).getFinishedTournamentsBetween(Mockito.anyLong(), Mockito.anyLong());
        Mockito.verify(_leagueDao, Mockito.times(1)).loadLeaguesEndingBetween(Mockito.any(), Mockito.anyInt(), Mockito.anyInt());
        assertEquals(2, _service.getItemCount());               // one entry per (month, kind)
    }

    @Test
    public void EventHistoryFutureMonthIsNotCached() {
        YearMonth future = YearMonth.from(EventDates.fromDateInt(DateUtils.getCurrentDate())).plusMonths(2);

        _service.getMonth(future, T);
        _service.getMonth(future, T);

        assertEquals(0, _service.getItemCount());
        Mockito.verify(_tournamentDao, Mockito.times(2)).getFinishedTournamentsBetween(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    public void EventHistoryCacheManagerEmptiesIt() throws Exception {
        //test1: the admin panel's "clear cache" only calls CacheManager.clearCaches(); registration must be enough
        Mockito.when(_tournamentDao.getFinishedTournamentMonths()).thenReturn(List.of("2026-09"));
        Mockito.when(_leagueDao.getLeagueEndMonths(Mockito.anyInt())).thenReturn(List.of("2026-09"));
        CacheManager cacheManager = new CacheManager();
        cacheManager.addCache(_service);

        _service.getMonth(OCTOBER, T);
        _service.getMonth(YearMonth.of(2026, 4), L);
        _service.getAvailableMonths(T);
        _service.getAvailableMonths(L);
        assertEquals(4, cacheManager.getTotalCount());

        cacheManager.clearCaches(); //test1

        assertEquals(0, cacheManager.getTotalCount());
        _service.getMonth(OCTOBER, T);
        Mockito.verify(_tournamentDao, Mockito.times(2)).getFinishedTournamentsBetween(Mockito.anyLong(), Mockito.anyLong());
    }

    // --- the months list -----------------------------------------------------------------------------------

    @Test
    public void EventHistoryMonthsListIsPerKindNewestFirst() throws Exception {
        // 2026-02 holds only leagues, 2026-01 only tournaments
        Mockito.when(_tournamentDao.getFinishedTournamentMonths()).thenReturn(List.of("2026-01", "2026-03", "2025-11"));
        Mockito.when(_leagueDao.getLeagueEndMonths(Mockito.anyInt())).thenReturn(List.of("2026-02", "2026-03"));

        assertEquals(List.of("2026-03", "2026-01", "2025-11"), _service.getAvailableMonths(T));
        assertEquals(List.of("2026-03", "2026-02"), _service.getAvailableMonths(L));
        Mockito.verify(_leagueDao).getLeagueEndMonths(DateUtils.getCurrentDate());   // only leagues that have ended
    }

    @Test
    public void EventHistoryMonthsListIsCachedPerKindAndSkipsNullRows() throws Exception {
        Mockito.when(_tournamentDao.getFinishedTournamentMonths()).thenReturn(Arrays.asList("2026-03", null, "2026-01", "2026-03"));

        assertEquals(List.of("2026-03", "2026-01"), _service.getAvailableMonths(T));
        _service.getAvailableMonths(T);
        Mockito.verify(_tournamentDao, Mockito.times(1)).getFinishedTournamentMonths();
        Mockito.verify(_leagueDao, Mockito.never()).getLeagueEndMonths(Mockito.anyInt());

        _service.clearCache();
        _service.getAvailableMonths(T);
        Mockito.verify(_tournamentDao, Mockito.times(2)).getFinishedTournamentMonths();
    }

    @Test
    public void EventHistoryFailingMonthsQueryPropagatesAndIsNotCached() {
        Mockito.when(_tournamentDao.getFinishedTournamentMonths())
                .thenThrow(new RuntimeException("database is down"))
                .thenReturn(List.of("2026-02"));

        assertThrows(RuntimeException.class, () -> _service.getAvailableMonths(T));
        assertEquals(List.of("2026-02"), _service.getAvailableMonths(T));
    }
}
