package com.gempukku.swccgo.league;

import com.gempukku.swccgo.DateUtils;
import com.gempukku.swccgo.db.LeagueDAO;
import com.gempukku.swccgo.draft2.SoloDraftDefinitions;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.game.formats.SwccgoFormatLibrary;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The one place a league definition (what the League Admin form posts to /admin/league/&lt;type&gt;/create, or
 * what a league schedule stores) becomes a league row: it renders this server's positional {@code parameters}
 * string for the league's {@link LeagueData} class, builds the series through that class (so dates, formats and
 * collections come out exactly as the hall will read them), validates, and inserts.  AdminRequestHandler and
 * {@link LeagueScheduleService} both go through here, so a scheduled league is built the same way as a hand-made
 * one.  Ported from LOTR GEMP's LeagueFactory onto this server's league model.
 */
public class LeagueFactory {
    /** league.name is VARCHAR(45). */
    public static final int MAX_NAME_LENGTH = 45;
    /** league.parameters is VARCHAR(255). */
    public static final int MAX_PARAMETERS_LENGTH = 255;
    /** A league stays listed in the hall this many days after its last serie ends. */
    public static final int DISPLAY_DAYS_AFTER_END = 2;

    /** The three kinds of league the admin form creates; each has its own parameter string and LeagueData class. */
    public enum Kind {
        SEALED, SOLODRAFT, CONSTRUCTED;

        /** The kind named (case-insensitively), or null. */
        public static Kind parse(String value) {
            if (value == null)
                return null;
            for (Kind kind : values())
                if (kind.name().equalsIgnoreCase(value.trim()))
                    return kind;
            return null;
        }
    }

    /** One serie as the form describes it: a format (or sealed / draft product) code, a length in days and a ranked-match cap. */
    public record Serie(String format, int duration, int maxMatches) {
    }

    /**
     * Everything the New-league form posts.  {@code start} is {@code yyyyMMdd}; {@code collectionType} ("default" /
     * "permanent") and {@code lockedDeckType} apply to constructed leagues; a sealed or draft league uses only the
     * first serie (its product fixes the number of series).
     */
    public static class Definition {
        public Kind kind;
        public String name;
        public int cost;
        public int start;
        public String collectionType = "default";
        public String lockedDeckType = "";
        public boolean allowTimeExtensions;
        public boolean allowSpectators = true;
        public boolean showPlayerNames;
        public boolean invitationOnly;
        public String registrationInfo = "";
        public int decisionTimeoutSeconds = 300;
        public int timePerPlayerMinutes = 50;
        public List<Serie> series = new ArrayList<>();
    }

    /**
     * A definition that has been rendered and checked.  {@code code} is the league's type (its id in every other
     * table), {@code series} are the dates and formats the hall will show, {@code displayEnd} the last day it is
     * listed; {@code registrationInfo} is the stored form (a starwarsccg.org address wrapped in a link, as the admin
     * handler always did).
     */
    public record PreparedLeague(Definition definition, String code, String leagueClass, String parameters,
                                 List<LeagueSeriesData> series, int start, int displayEnd, String registrationInfo,
                                 LockedDeckType lockedDeckType) {
        public String name() {
            return definition.name;
        }

        public int cost() {
            return definition.cost;
        }
    }

    private final SwccgCardBlueprintLibrary _cardLibrary;
    private final SwccgoFormatLibrary _formatLibrary;
    private final SoloDraftDefinitions _soloDraftDefinitions;
    private final LeagueDAO _leagueDao;
    private final LeagueService _leagueService;

    public LeagueFactory(SwccgCardBlueprintLibrary cardLibrary, SwccgoFormatLibrary formatLibrary, SoloDraftDefinitions soloDraftDefinitions,
                         LeagueDAO leagueDao, LeagueService leagueService) {
        _cardLibrary = cardLibrary;
        _formatLibrary = formatLibrary;
        _soloDraftDefinitions = soloDraftDefinitions;
        _leagueDao = leagueDao;
        _leagueService = leagueService;
    }

    /** Prepares a league with a fresh code (the current time in milliseconds, as the admin handler always used). */
    public PreparedLeague prepare(Definition definition) throws LeagueDefinitionException {
        return prepare(definition, String.valueOf(System.currentTimeMillis()));
    }

    /**
     * Renders and validates a definition without storing anything.
     * @throws LeagueDefinitionException when the definition cannot produce a league; the message says why
     */
    public PreparedLeague prepare(Definition definition, String code) throws LeagueDefinitionException {
        if (definition.kind == null)
            throw new LeagueDefinitionException("type", "Unknown league type.");
        if (StringUtils.isBlank(definition.name))
            throw new LeagueDefinitionException("name", "The league name cannot be blank.");
        if (definition.name.length() > MAX_NAME_LENGTH)
            throw new LeagueDefinitionException("name", "The league name must be " + MAX_NAME_LENGTH + " characters or less.");
        // a sealed / draft league's name ends its comma-separated parameters string (it names the league's private collection)
        if (definition.kind != Kind.CONSTRUCTED && definition.name.contains(","))
            throw new LeagueDefinitionException("name", "A sealed or draft league name cannot contain a comma.");
        if (!isDay(definition.start))
            throw new LeagueDefinitionException("start", "The start date must be a day in yyyyMMdd form.");
        if (definition.cost < 0)
            throw new LeagueDefinitionException("cost", "The cost cannot be negative.");
        if (definition.decisionTimeoutSeconds < 1)
            throw new LeagueDefinitionException("decisionTimeoutSeconds", "The decision timeout must be at least 1 second.");
        if (definition.timePerPlayerMinutes < 1)
            throw new LeagueDefinitionException("timePerPlayerMinutes", "The time per player must be at least 1 minute.");
        if (definition.series == null || definition.series.isEmpty())
            throw new LeagueDefinitionException("series", "At least one serie must be defined.");
        List<Serie> series = definition.kind == Kind.CONSTRUCTED ? definition.series : definition.series.subList(0, 1);
        for (int i = 0; i < series.size(); i++) {
            Serie serie = series.get(i);
            String where = "series[" + i + "]";
            if (serie == null || StringUtils.isBlank(serie.format()))
                throw new LeagueDefinitionException(where + ".format", "Serie " + (i + 1) + " has no format.");
            if (serie.format().contains(","))
                throw new LeagueDefinitionException(where + ".format", "Serie " + (i + 1) + ": a format code cannot contain a comma.");
            if (serie.duration() < 1)
                throw new LeagueDefinitionException(where + ".duration", "Serie " + (i + 1) + " must last at least one day.");
            if (serie.maxMatches() < 1)
                throw new LeagueDefinitionException(where + ".matches", "Serie " + (i + 1) + " must allow at least one match.");
            switch (definition.kind) {
                case SEALED -> {
                    if (SealedLeagueType.getLeagueType(serie.format()) == null)
                        throw new LeagueDefinitionException(where + ".format", "Unknown sealed league type '" + serie.format() + "'.");
                }
                case SOLODRAFT -> {
                    if (_soloDraftDefinitions.getSoloDraft(serie.format()) == null)
                        throw new LeagueDefinitionException(where + ".format", "Unknown draft '" + serie.format() + "'.");
                }
                case CONSTRUCTED -> {
                    if (_formatLibrary.getFormat(serie.format()) == null)
                        throw new LeagueDefinitionException(where + ".format", "Unknown format '" + serie.format() + "'.");
                }
            }
        }

        LockedDeckType lockedDeckType = null;
        if (definition.kind == Kind.CONSTRUCTED) {
            if (!"default".equals(definition.collectionType) && !"permanent".equals(definition.collectionType))
                throw new LeagueDefinitionException("collectionType", "Unknown collection type '" + definition.collectionType + "'.");
            lockedDeckType = LockedDeckType.getLockedDeckType(definition.lockedDeckType);
            if (!StringUtils.isEmpty(definition.lockedDeckType) && lockedDeckType == null)
                throw new LeagueDefinitionException("lockedDeckType", "Unknown deck lock type '" + definition.lockedDeckType + "'.");
        }

        String parameters = renderParameters(definition, series, code);
        if (parameters.length() > MAX_PARAMETERS_LENGTH)
            throw new LeagueDefinitionException("series", "The league definition is too long to store (" + parameters.length()
                    + " characters of " + MAX_PARAMETERS_LENGTH + "); use fewer series or a shorter name.");

        LeagueData leagueData;
        try {
            leagueData = switch (definition.kind) {
                case SEALED -> new NewSealedLeagueData(_cardLibrary, _soloDraftDefinitions, parameters);
                case SOLODRAFT -> new NewSoloDraftLeagueData(_cardLibrary, _soloDraftDefinitions, parameters);
                case CONSTRUCTED -> new NewConstructedLeagueData(_cardLibrary, _soloDraftDefinitions, parameters);
            };
        } catch (RuntimeException exp) {
            throw new LeagueDefinitionException("series", "The league could not be built from its definition: " + exp.getMessage());
        }
        List<LeagueSeriesData> built = leagueData.getSeries();
        if (built == null || built.isEmpty())
            throw new LeagueDefinitionException("series", "The league definition produces no series.");
        for (LeagueSeriesData serie : built) {
            if (_formatLibrary.getFormat(serie.getFormat()) == null)
                throw new LeagueDefinitionException("series", "Serie '" + serie.getName() + "' plays an unknown format '" + serie.getFormat() + "'.");
        }

        int start = built.get(0).getStart();
        int displayEnd = DateUtils.offsetDate(built.get(built.size() - 1).getEnd(), DISPLAY_DAYS_AFTER_END);
        return new PreparedLeague(definition, code, leagueData.getClass().getName(), parameters,
                Collections.unmodifiableList(built), start, displayEnd, storedRegistrationInfo(definition.registrationInfo), lockedDeckType);
    }

    /**
     * Inserts a prepared league and refreshes the league cache so the hall lists it at once.
     * @param scheduleId the league_schedule row on whose behalf the league is created, or null for a hand-made league
     * @return the league's code (its {@code type})
     */
    public String create(PreparedLeague prepared, Integer scheduleId) {
        Definition d = prepared.definition();
        try {
            _leagueDao.addLeague(d.cost, d.name, prepared.code(), prepared.leagueClass(), prepared.parameters(), prepared.start(),
                    prepared.displayEnd(), d.allowSpectators, d.allowTimeExtensions, d.showPlayerNames, d.invitationOnly,
                    prepared.registrationInfo(), d.decisionTimeoutSeconds, d.timePerPlayerMinutes, prepared.lockedDeckType(), scheduleId);
        } catch (Exception exp) {
            throw new RuntimeException("Unable to store league '" + d.name + "'", exp);
        }
        _leagueService.clearCache();
        return prepared.code();
    }

    /**
     * The {@code league.parameters} string each LeagueData class parses.  Sealed and draft: {@code product,start,
     * duration,matches,code,name} (code and name become the league's private collection); constructed:
     * {@code start,collection,count,format,duration,matches,...}.  Exactly what AdminRequestHandler built.
     */
    static String renderParameters(Definition definition, List<Serie> series, String code) {
        Serie first = series.get(0);
        switch (definition.kind) {
            case SEALED, SOLODRAFT:
                return first.format() + "," + definition.start + "," + first.duration() + "," + first.maxMatches() + "," + code + "," + definition.name;
            default:
                StringBuilder sb = new StringBuilder();
                sb.append(definition.start).append(",").append(definition.collectionType).append(",").append(series.size());
                for (Serie serie : series)
                    sb.append(",").append(serie.format()).append(",").append(serie.duration()).append(",").append(serie.maxMatches());
                return sb.toString();
        }
    }

    /** A bare starwarsccg.org address is stored wrapped in a link, as the admin handler always did; anything else as given. */
    static String storedRegistrationInfo(String registrationInfo) {
        String info = registrationInfo == null ? "" : registrationInfo;
        if (info.toLowerCase().contains("starwarsccg.org") && !info.contains(" ") && !info.startsWith("<a "))
            return "<a href='" + info + "' target='_blank'>" + info + "</a>";
        return info;
    }

    /** Whether the number is a calendar day in {@code yyyyMMdd} form. */
    static boolean isDay(int yyyymmdd) {
        if (yyyymmdd < 10000101 || yyyymmdd > 99991231)
            return false;
        try {
            java.time.LocalDate.of(yyyymmdd / 10000, (yyyymmdd / 100) % 100, yyyymmdd % 100);
            return true;
        } catch (java.time.DateTimeException exp) {
            return false;
        }
    }
}
