package com.gempukku.swccgo.db.vo;

import java.util.Date;

/**
 * One row of the {@code league_schedule} table as the DAO reads and writes it (see
 * db-scripts/update_league_schedule.sql).  The definition columns ({@link #name} to {@link #enabled}) come from the
 * admin; the progress columns ({@link #lastCreatedLeagueType} onwards) are written by the scheduler.  Dates are
 * {@code yyyyMMdd} integers as everywhere in the league tables.  {@link com.gempukku.swccgo.league.LeagueSchedule}
 * is the parsed, immutable view of a row.
 */
public class LeagueScheduleRow {
    public int id;
    public String name;
    /** CONSTRUCTED / SEALED / SOLODRAFT. */
    public String leagueType;
    /** JSON object: the New-league form fields every league of the series shares. */
    public String template;
    /** JSON array of {name, params:{series:[{format, duration, matches}, ...]}}. */
    public String events;
    public String namePattern;
    /** {@code yyyyMMdd} start day of the next league to create. */
    public int nextEventDate;
    public int nextEventIndex;
    public double intervalMonths = 1;
    public int leadDays = 7;
    public boolean enabled;
    public String lastCreatedLeagueType;
    public Date lastRun;
    public String lastError;
    public String createdBy;
    public Date createdAt;

    /** A field-by-field copy, so a caller can advance a schedule without touching the row it was handed. */
    public LeagueScheduleRow copy() {
        LeagueScheduleRow row = new LeagueScheduleRow();
        row.id = id;
        row.name = name;
        row.leagueType = leagueType;
        row.template = template;
        row.events = events;
        row.namePattern = namePattern;
        row.nextEventDate = nextEventDate;
        row.nextEventIndex = nextEventIndex;
        row.intervalMonths = intervalMonths;
        row.leadDays = leadDays;
        row.enabled = enabled;
        row.lastCreatedLeagueType = lastCreatedLeagueType;
        row.lastRun = lastRun;
        row.lastError = lastError;
        row.createdBy = createdBy;
        row.createdAt = createdAt;
        return row;
    }
}
