package com.gempukku.swccgo.db;

import com.gempukku.swccgo.db.vo.LeagueScheduleRow;

import java.util.List;

/**
 * The {@code league_schedule} table (db-scripts/update_league_schedule.sql).  Ported from LOTR GEMP's
 * LeagueScheduleDAO; every statement is parameterised.
 */
public interface LeagueScheduleDAO {
    /** Every schedule, enabled or not, by name. */
    List<LeagueScheduleRow> getAllSchedules();

    /** The schedule with that id, or null. */
    LeagueScheduleRow getSchedule(int id);

    /**
     * Inserts the schedule (when {@code id} is 0) or updates every definition column of an existing one; the
     * progress columns are left alone except {@code last_error}, which is cleared.
     * @return the id of the row
     */
    int saveSchedule(LeagueScheduleRow schedule);

    /** Turns a schedule on or off without touching the rest of its definition. */
    void setEnabled(int id, boolean enabled);

    /**
     * Records the outcome of a scheduler run: the advanced next event, what was created and any error.
     */
    void updateProgress(LeagueScheduleRow schedule);

    /** Deletes the schedule; leagues it created stay, with their {@code schedule_id} cleared. */
    void deleteSchedule(int id);
}
