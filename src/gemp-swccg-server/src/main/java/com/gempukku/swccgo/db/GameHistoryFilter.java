package com.gempukku.swccgo.db;

import java.time.LocalDate;

/**
 * Optional filters on one player's game history (the hall's History tab).  Every field may be null, meaning "any".
 *
 * @param format        exact format name as stored in game_history.format_name
 * @param opponent      opponent name, matched ignoring case: as a prefix, or exactly when opponentExact is set
 * @param opponentExact match the opponent name exactly (still ignoring case) instead of as a prefix
 * @param event         {@link #CASUAL_EVENT} for casual games (no event, or "Casual" / "Casual (Private)"),
 *                      otherwise the start of a league or tournament name, matched ignoring case
 * @param side          "Dark" or "Light": only games in which the player played that side
 * @param result        "W" or "L": only games the player won, or lost
 * @param from          first day (server time, UTC) on which the game ended, inclusive
 * @param to            last day (server time, UTC) on which the game ended, inclusive
 */
public record GameHistoryFilter(String format, String opponent, boolean opponentExact, String event, String side,
                                String result, LocalDate from, LocalDate to) {
    /** The event filter value that selects casual games. */
    public static final String CASUAL_EVENT = "casual";
    public static final String DARK = "Dark";
    public static final String LIGHT = "Light";
    public static final String WON = "W";
    public static final String LOST = "L";
    /** Longest text accepted for the format, opponent and event filters. */
    public static final int MAX_TEXT_LENGTH = 255;

    public static final GameHistoryFilter NONE = new GameHistoryFilter(null, null, false, null, null, null, null, null);

    public GameHistoryFilter {
        format = clean(format);
        opponent = clean(opponent);
        event = clean(event);
        side = normalizeSide(clean(side));
        result = normalizeResult(clean(result));
    }

    private static String clean(String value) {
        if (value == null)
            return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static String normalizeSide(String value) {
        if (value == null)
            return null;
        if (value.equalsIgnoreCase(DARK) || value.equalsIgnoreCase("ds"))
            return DARK;
        if (value.equalsIgnoreCase(LIGHT) || value.equalsIgnoreCase("ls"))
            return LIGHT;
        return value;
    }

    private static String normalizeResult(String value) {
        if (value == null)
            return null;
        if (value.equalsIgnoreCase(WON) || value.equalsIgnoreCase("won") || value.equalsIgnoreCase("win"))
            return WON;
        if (value.equalsIgnoreCase(LOST) || value.equalsIgnoreCase("lost") || value.equalsIgnoreCase("loss"))
            return LOST;
        return value;
    }

    public boolean isEmpty() {
        return format == null && opponent == null && event == null && side == null && result == null
                && from == null && to == null;
    }

    public boolean casualOnly() {
        return event != null && event.equalsIgnoreCase(CASUAL_EVENT);
    }

    /** @return null when usable, otherwise why not (for a 400 response) */
    public String validate() {
        if (tooLong(format) || tooLong(opponent) || tooLong(event))
            return "Filter text is too long.";
        if (side != null && !side.equals(DARK) && !side.equals(LIGHT))
            return "Side must be Dark or Light.";
        if (result != null && !result.equals(WON) && !result.equals(LOST))
            return "Result must be W or L.";
        if (from != null && to != null && from.isAfter(to))
            return "The 'from' date is after the 'to' date.";
        return null;
    }

    private static boolean tooLong(String value) {
        return value != null && value.length() > MAX_TEXT_LENGTH;
    }
}
