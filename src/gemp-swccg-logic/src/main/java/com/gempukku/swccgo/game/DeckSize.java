package com.gempukku.swccgo.game;

/**
 * Deck size as a setup setting, separate from the format (swccg-guild/work#90).
 * <p>
 * A table, league or tournament that plays a format at a size other than the format's own carries the size in its
 * format code as {@code <format>@<size>}, for example {@code open@100}. The format library resolves that code to the
 * base format with the chosen size, so every place that already stores or looks up a format by code (tables, league
 * series, tournaments, saved games, replays) keeps the size without a schema change.
 * <p>
 * The supported range lives here and nowhere else: setup screens, the format library and validation all read it.
 * #80 and #88 widen it by changing these two constants.
 */
public final class DeckSize {
    /** Smallest deck size a table, league or tournament may choose. */
    public static final int MIN_SUPPORTED = 40;
    /** Largest deck size a table, league or tournament may choose. */
    public static final int MAX_SUPPORTED = 100;
    /** Separates the base format code from the chosen size in a format code. */
    public static final char SEPARATOR = '@';

    private DeckSize() {
    }

    public static boolean isSupported(int size) {
        return size >= MIN_SUPPORTED && size <= MAX_SUPPORTED;
    }

    /** The format code without a chosen size ({@code open@100} → {@code open}). */
    public static String baseCode(String formatCode) {
        if (formatCode == null)
            return null;
        int at = formatCode.indexOf(SEPARATOR);
        return at < 0 ? formatCode : formatCode.substring(0, at);
    }

    /**
     * The chosen size in a format code, or {@code null} when the code names no size.
     *
     * @throws IllegalArgumentException when the size part is not a whole number
     */
    public static Integer chosenSize(String formatCode) {
        if (formatCode == null)
            return null;
        int at = formatCode.indexOf(SEPARATOR);
        if (at < 0)
            return null;
        String size = formatCode.substring(at + 1);
        if (!size.matches("[0-9]{1,4}"))
            throw new IllegalArgumentException("Deck size must be a whole number from " + MIN_SUPPORTED + " to " + MAX_SUPPORTED);
        return Integer.parseInt(size);
    }

    /** The format code for a base format played at a chosen size. */
    public static String code(String baseCode, int size) {
        return baseCode(baseCode) + SEPARATOR + size;
    }

    /**
     * Parses a deck size typed at setup. Empty means "the format's default" and returns {@code null}.
     *
     * @throws IllegalArgumentException when the value is not a whole number in the supported range
     */
    public static Integer parse(String value) {
        if (value == null || value.trim().isEmpty())
            return null;
        String trimmed = value.trim();
        if (!trimmed.matches("[0-9]{1,4}") || !isSupported(Integer.parseInt(trimmed)))
            throw new IllegalArgumentException("Deck size must be a whole number from " + MIN_SUPPORTED + " to " + MAX_SUPPORTED);
        return Integer.parseInt(trimmed);
    }
}
