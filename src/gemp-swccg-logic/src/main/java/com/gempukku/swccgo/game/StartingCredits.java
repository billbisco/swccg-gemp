package com.gempukku.swccgo.game;

/** Validates the optional table setup budget; zero keeps ordinary random starting hands. */
public final class StartingCredits {
    public static final int MAX_BUDGET = 60;
    private StartingCredits() { }

    public static int resolve(SwccgFormat format, String value) {
        if (value == null || value.isEmpty()) return format.getDefaultStartingCredits();
        if (!format.supportsStartingCredits())
            throw new IllegalArgumentException("This format does not use starting credits");
        if (!value.matches("[0-9]{1,2}"))
            throw new IllegalArgumentException("Starting credits must be a whole number from 0 to " + MAX_BUDGET);
        int credits = Integer.parseInt(value);
        if (credits > MAX_BUDGET)
            throw new IllegalArgumentException("Starting credits must be from 0 to " + MAX_BUDGET);
        return credits;
    }
}
