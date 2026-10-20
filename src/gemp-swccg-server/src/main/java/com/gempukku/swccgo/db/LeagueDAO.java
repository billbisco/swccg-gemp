package com.gempukku.swccgo.db;

import com.gempukku.swccgo.db.vo.League;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.league.LockedDeckType;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface LeagueDAO {
    void addLeague(int cost, String name, String type, String clazz, String parameters, int start, int endTime, boolean allowSpectators, boolean allowTimeExtensions, boolean showPlayerNames, boolean invitationOnly, String registrationInfo, int decisionTimeoutSeconds, int timePerPlayerMinutes, LockedDeckType lockedDeckType) throws SQLException, IOException;

    /**
     * As {@link #addLeague(int, String, String, String, String, int, int, boolean, boolean, boolean, boolean, String, int, int, LockedDeckType)},
     * recording which league schedule created the league.
     * @param scheduleId the league_schedule row on whose behalf the league is created, or null for a hand-made league
     */
    void addLeague(int cost, String name, String type, String clazz, String parameters, int start, int endTime, boolean allowSpectators, boolean allowTimeExtensions, boolean showPlayerNames, boolean invitationOnly, String registrationInfo, int decisionTimeoutSeconds, int timePerPlayerMinutes, LockedDeckType lockedDeckType, Integer scheduleId) throws SQLException, IOException;

    /** The leagues the given schedule created with that start day ({@code yyyyMMdd}); normally none or one. */
    List<League> loadLeaguesOfScheduleStarting(SwccgCardBlueprintLibrary library, int scheduleId, int start) throws SQLException, IOException;

    List<League> loadActiveLeagues(SwccgCardBlueprintLibrary library, int currentTime) throws SQLException, IOException;

    void setStatus(League league, int newStatus);

    // ---- the Events tab (calendar and completed-event browser) ----
    // Days are yyyyMMdd integers, as the league table stores them; ranges on end are half-open [from, to).

    /** The league with that type (code), whether or not it has ended; null when there is none. */
    League loadLeagueByType(SwccgCardBlueprintLibrary library, String type) throws SQLException, IOException;

    /** Leagues whose stored span touches [from, to] (both inclusive), oldest start first. */
    List<League> loadLeaguesOverlapping(SwccgCardBlueprintLibrary library, int from, int to) throws SQLException, IOException;

    /** Leagues whose stored end day is in [from, to), latest end first. */
    List<League> loadLeaguesEndingBetween(SwccgCardBlueprintLibrary library, int from, int to) throws SQLException, IOException;

    /** League type to the number of players who joined it, for the leagues whose end day is in [from, to). */
    Map<String, Integer> getParticipantCountsForLeaguesEndingBetween(int from, int to) throws SQLException;

    /** Every "yyyy-MM" holding the end day of at least one league that ended before {@code before}, in no order. */
    List<String> getLeagueEndMonths(int before) throws SQLException;
}
