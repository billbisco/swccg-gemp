package com.gempukku.swccgo.league;

/**
 * A league (or league schedule) definition that cannot be turned into a league: the admin's input is wrong, not the
 * server.  {@link #getParameter()} names the field at fault, so the admin page can say which one.  Ported from LOTR
 * GEMP.
 */
public class LeagueDefinitionException extends Exception {
    private final String _parameter;

    public LeagueDefinitionException(String parameter, String message) {
        super(message);
        _parameter = parameter;
    }

    /** The form field (or JSON key) the problem is with, e.g. "name", "start", "series[1].format". */
    public String getParameter() {
        return _parameter;
    }
}
