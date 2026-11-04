package com.gempukku.swccgo.game.formats;

/**
 * Deck rules a table's game options add to its format (swccg-guild/work#142/#144): checked when a deck is validated
 * for that table, on top of the format's own rules. Built by {@link SwccgoFormatLibrary#effectiveFormat}.
 */
public final class DeckRules {
    public static final DeckRules NONE = new DeckRules(false, false);

    private final boolean _noShields;
    private final boolean _multipleObjectives;

    public DeckRules(boolean noShields, boolean multipleObjectives) {
        _noShields = noShields;
        _multipleObjectives = multipleObjectives;
    }

    /** "Shields: none" (#144): no card with the Defensive Shield icon, as in Open (no shields). */
    public boolean isNoShields() { return _noShields; }

    /** "Starting: Objective optional" (#142): a deck may hold several Objectives. */
    public boolean isMultipleObjectives() { return _multipleObjectives; }

    public boolean isNone() { return !_noShields && !_multipleObjectives; }
}
