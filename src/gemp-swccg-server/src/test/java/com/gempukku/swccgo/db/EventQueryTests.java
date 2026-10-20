package com.gempukku.swccgo.db;

import org.junit.Test;
import org.mockito.Mockito;

import java.sql.PreparedStatement;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EventQueryTests {
    private static final long OCT_1_2026_UTC = 1790812800000L;
    private static final long NOV_1_2026_UTC = OCT_1_2026_UTC + 31L * 24 * 60 * 60 * 1000;

    @Test
    public void EventQueryLeagueByTypeBindsTheType() {
        EventQuery query = EventQuery.leagueByType("hoth_sealed");

        assertEquals("select " + EventQuery.LEAGUE_COLUMNS + " from league where type = ?", query.sql());
        assertEquals(List.of("hoth_sealed"), query.parameters());
    }

    @Test
    public void EventQueryLeaguesOverlappingBindsTheRangeEndsSwapped() {
        //test1: a league overlaps the range when it starts on or before the range's end and ends on or after its start
        //test2: so the range's end is bound to start and its start to end
        EventQuery query = EventQuery.leaguesOverlapping(20261001, 20261031);

        assertTrue(query.sql().contains("where start <= ? and end >= ?")); //test1
        assertEquals(List.of(20261031, 20261001), query.parameters()); //test2
    }

    @Test
    public void EventQueryLeaguesEndingBetweenIsHalfOpenLatestFirst() {
        EventQuery query = EventQuery.leaguesEndingBetween(20261001, 20261101);

        assertTrue(query.sql().contains("where end >= ? and end < ? order by end desc"));
        assertEquals(List.of(20261001, 20261101), query.parameters());
    }

    @Test
    public void EventQueryLeagueParticipantCountsJoinOnTheLeagueType() {
        EventQuery query = EventQuery.leagueParticipantCounts(20261001, 20261101);

        assertTrue(query.sql().startsWith("select lp.league_type, count(*) from league_participation lp join league l on l.type = lp.league_type"));
        assertTrue(query.sql().contains("where l.end >= ? and l.end < ? group by lp.league_type"));
        assertEquals(List.of(20261001, 20261101), query.parameters());
    }

    @Test
    public void EventQueryLeagueEndMonthsTakeTheMonthOfTheEndDay() {
        EventQuery query = EventQuery.leagueEndMonths(20261020);

        assertEquals("select distinct (end div 100) from league where end < ?", query.sql());
        assertEquals(List.of(20261020), query.parameters());
    }

    @Test
    public void EventQueryTournamentsStartingBetweenTakesEveryStage() {
        EventQuery query = EventQuery.tournamentsStartingBetween(OCT_1_2026_UTC, NOV_1_2026_UTC);

        assertEquals("select " + EventQuery.TOURNAMENT_COLUMNS + " from tournament where start >= ? and start < ? order by start", query.sql());
        assertFalse(query.sql().substring(query.sql().indexOf("where")).contains("stage"));
        assertEquals(List.of(OCT_1_2026_UTC, NOV_1_2026_UTC), query.parameters());
    }

    @Test
    public void EventQueryFinishedTournamentsBindTheStageNotInline() {
        //test1: the stage is a bound value, not spliced into the text
        //test2: bound before the half-open millisecond range
        EventQuery query = EventQuery.finishedTournamentsBetween(OCT_1_2026_UTC, NOV_1_2026_UTC);

        assertTrue(query.sql().contains("where stage = ? and start >= ? and start < ? order by start desc")); //test1
        assertFalse(query.sql().contains("FINISHED")); //test1
        assertEquals(List.of("FINISHED", OCT_1_2026_UTC, NOV_1_2026_UTC), query.parameters()); //test2
    }

    @Test
    public void EventQueryFinishedTournamentPlayerCountsGroupByTournament() {
        EventQuery query = EventQuery.finishedTournamentPlayerCounts(OCT_1_2026_UTC, NOV_1_2026_UTC);

        assertTrue(query.sql().contains("join tournament t on t.tournament_id = tp.tournament_id where t.stage = ? and t.start >= ? and t.start < ? group by tp.tournament_id"));
        assertEquals(List.of("FINISHED", OCT_1_2026_UTC, NOV_1_2026_UTC), query.parameters());
    }

    @Test
    public void EventQueryFinishedTournamentDaysCountFromTheEpochAndNeedARound() {
        EventQuery query = EventQuery.finishedTournamentDays();

        assertEquals("select distinct floor(start / 86400000) from tournament where stage = ? and round > 0", query.sql());
        assertEquals(List.of("FINISHED"), query.parameters());
    }

    @Test
    public void EventQueryTournamentsPlayedBetweenBindsThePlayerFirst() {
        EventQuery query = EventQuery.tournamentsPlayedBetween("Bill", OCT_1_2026_UTC, NOV_1_2026_UTC);

        assertTrue(query.sql().contains("where tp.player = ? and t.start >= ? and t.start < ?"));
        assertEquals(List.of("Bill", OCT_1_2026_UTC, NOV_1_2026_UTC), query.parameters());
    }

    @Test
    public void EventQueryBindsEachParameterByItsType() throws Exception {
        PreparedStatement statement = Mockito.mock(PreparedStatement.class);

        EventQuery.tournamentsPlayedBetween("Bill", OCT_1_2026_UTC, NOV_1_2026_UTC).bind(statement);
        EventQuery.leaguesEndingBetween(20261001, 20261101).bind(statement);

        Mockito.verify(statement).setString(1, "Bill");
        Mockito.verify(statement).setLong(2, OCT_1_2026_UTC);
        Mockito.verify(statement).setLong(3, NOV_1_2026_UTC);
        Mockito.verify(statement).setInt(1, 20261001);
        Mockito.verify(statement).setInt(2, 20261101);
        Mockito.verifyNoMoreInteractions(statement);
    }

    @Test
    public void LeaguesOfScheduleStartingIsTheSchedulersIdempotencyCheck() {
        EventQuery query = EventQuery.leaguesOfScheduleStarting(7, 20261101);

        assertEquals("select " + EventQuery.LEAGUE_COLUMNS + " from league where schedule_id = ? and start = ?", query.sql());
        assertEquals(List.of(7, 20261101), query.parameters());
        assertTrue(EventQuery.LEAGUE_COLUMNS.endsWith("start, end, schedule_id"));
    }
}
