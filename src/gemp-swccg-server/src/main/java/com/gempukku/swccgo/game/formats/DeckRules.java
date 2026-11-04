package com.gempukku.swccgo.game.formats;

/**
 * Deck rules a table's game options add to its format (swccg-guild/work#142/#144): checked when a deck is validated
 * for that table, on top of the format's own rules. Built by {@link SwccgoFormatLibrary#effectiveFormat}.
 */
public final class DeckRules {
    public static final DeckRules NONE = new DeckRules(false, false, false, 1);

    private final boolean _noShields;
    private final boolean _multipleObjectives;
    private final boolean _uncappedShields;
    private final int _startingEffects;

    public DeckRules(boolean noShields, boolean multipleObjectives, boolean uncappedShields, int startingEffects) {
        _noShields = noShields;
        _multipleObjectives = multipleObjectives;
        _uncappedShields = uncappedShields;
        _startingEffects = Math.max(1, startingEffects);
    }

    /** "Starting: Effects" (#142 R2c): how many Starting Effects a deck may hold. */
    public int getStartingEffects() { return _startingEffects; }

    /** "Shields: no play limit" (#145): no Starting Effect cap (10 / 15) on Defensive Shields outside the deck. */
    public boolean isUncappedShields() { return _uncappedShields; }

    /** "Shields: none" (#144): no card with the Defensive Shield icon, as in Open (no shields). */
    public boolean isNoShields() { return _noShields; }

    /** "Starting: Objective optional" (#142): a deck may hold several Objectives. */
    public boolean isMultipleObjectives() { return _multipleObjectives; }

    public boolean isNone() { return !_noShields && !_multipleObjectives && !_uncappedShields && _startingEffects == 1; }
}
