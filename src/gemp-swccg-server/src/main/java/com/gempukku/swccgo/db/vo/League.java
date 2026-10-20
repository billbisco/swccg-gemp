package com.gempukku.swccgo.db.vo;

import com.gempukku.swccgo.draft2.SoloDraftDefinitions;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.league.LeagueData;
import com.gempukku.swccgo.league.LockedDeckType;

import java.lang.reflect.Constructor;

public class League {
    private SwccgCardBlueprintLibrary _library;
    private int _cost;
    private String _name;
    private String _type;
    private String _clazz;
    private String _parameters;
    private int _status;
    private boolean _allowTimeExtensions;
    private boolean _allowSpectators;
    private boolean _showPlayerNames;
    private int _decisionTimeoutSeconds;
    private int _timePerPlayerMinutes;
    private LeagueData _leagueData;
    private boolean _invitationOnly;
    private String _registrationInfo;
    private LockedDeckType _lockedDeckType;
    private int _start;
    private int _end;
    private Integer _scheduleId;


    public League(SwccgCardBlueprintLibrary library, int cost, String name, String type, String clazz, String parameters, int status, boolean allowSpectators, boolean allowTimeExtensions, boolean showPlayerNames, boolean invitationOnly, String registrationInfo, int decisionTimeoutSeconds, int timePerPlayerMinutes, LockedDeckType lockedDeckType) {
        this(library, cost, name, type, clazz, parameters, status, allowSpectators, allowTimeExtensions, showPlayerNames, invitationOnly, registrationInfo, decisionTimeoutSeconds, timePerPlayerMinutes, lockedDeckType, 0, 0);
    }

    /**
     * @param start the league's stored start day ({@code yyyyMMdd}), 0 when not read
     * @param end   the league's stored end day ({@code yyyyMMdd}), 0 when not read
     */
    public League(SwccgCardBlueprintLibrary library, int cost, String name, String type, String clazz, String parameters, int status, boolean allowSpectators, boolean allowTimeExtensions, boolean showPlayerNames, boolean invitationOnly, String registrationInfo, int decisionTimeoutSeconds, int timePerPlayerMinutes, LockedDeckType lockedDeckType, int start, int end) {
        this(library, cost, name, type, clazz, parameters, status, allowSpectators, allowTimeExtensions, showPlayerNames, invitationOnly, registrationInfo, decisionTimeoutSeconds, timePerPlayerMinutes, lockedDeckType, start, end, null);
    }

    /**
     * @param scheduleId the league_schedule row that created this league, or null for a league an admin made by hand
     */
    public League(SwccgCardBlueprintLibrary library, int cost, String name, String type, String clazz, String parameters, int status, boolean allowSpectators, boolean allowTimeExtensions, boolean showPlayerNames, boolean invitationOnly, String registrationInfo, int decisionTimeoutSeconds, int timePerPlayerMinutes, LockedDeckType lockedDeckType, int start, int end, Integer scheduleId) {
        _library = library;
        _cost = cost;
        _name = name;
        _type = type;
        _clazz = clazz;
        _parameters = parameters;
        _status = status;
        _allowTimeExtensions = allowTimeExtensions;
        _allowSpectators = allowSpectators;
        _showPlayerNames = showPlayerNames;
        _decisionTimeoutSeconds = decisionTimeoutSeconds;
        _timePerPlayerMinutes = timePerPlayerMinutes;
        _invitationOnly = invitationOnly;
        _registrationInfo = registrationInfo;
        _lockedDeckType = lockedDeckType;
        _start = start;
        _end = end;
        _scheduleId = scheduleId;
    }

    /** The league_schedule row that created this league, or null for a league an admin made by hand (or one read without the column). */
    public Integer getScheduleId() {
        return _scheduleId;
    }

    /** The stored start day ({@code yyyyMMdd}) of the league row, or 0 when it was not read.  The series carry the dates the hall shows. */
    public int getStart() {
        return _start;
    }

    /** The stored end day ({@code yyyyMMdd}) of the league row, or 0 when it was not read. */
    public int getEnd() {
        return _end;
    }

    public String getParameters() {
        return _parameters;
    }

    /** The LeagueData class that interprets the parameters (constructed, sealed or solo draft). */
    public String getLeagueClass() {
        return _clazz;
    }

    public int getCost() {
        return _cost;
    }

    public String getName() {
        return _name;
    }

    public String getType() {
        return _type;
    }

    public boolean getAllowSpectators() { return _allowSpectators; }

    public boolean getAllowTimeExtensions() { return _allowTimeExtensions; }

    public boolean getShowPlayerNames() { return _showPlayerNames; }

    public boolean getInvitationOnly() { return _invitationOnly; }

    public String getRegistrationInfo() {
        if(_registrationInfo != null && _registrationInfo.toLowerCase().contains("starwarsccg.org") && !_registrationInfo.contains(" "))
            return "<a href='"+_registrationInfo+"' target='_new'>"+_registrationInfo+"</a>";
        return _registrationInfo;
    }

    public int getDecisionTimeoutSeconds() { return _decisionTimeoutSeconds; }

    public int getTimePerPlayerMinutes() { return _timePerPlayerMinutes; }

    public LockedDeckType getLockedDeckType() { return _lockedDeckType; }

    public synchronized LeagueData getLeagueData(SoloDraftDefinitions soloDraftDefinitions) {
        if (_leagueData == null) {
            try {
                Class<?> aClass = Class.forName(_clazz);
                Constructor<?> constructor = aClass.getConstructor(SwccgCardBlueprintLibrary.class, SoloDraftDefinitions.class, String.class);
                _leagueData = (LeagueData) constructor.newInstance(_library, soloDraftDefinitions, _parameters);
            } catch (Exception exp) {
                throw new RuntimeException("Unable to create LeagueData", exp);
            }
        }
        return _leagueData;
    }

    public int getStatus() {
        return _status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        League league = (League) o;

        if (_type != null ? !_type.equals(league._type) : league._type != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return _type != null ? _type.hashCode() : 0;
    }
}
