package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;

/**
 * Titles and filters shared by the Virtual Block 7.3 Light cards (Center Of Tyranny, Rebel Strike Team (V) and the
 * cards that refer to them).
 */
public final class LegacyVb73Light {
    public static final String CENTER_OF_TYRANNY = "Center Of Tyranny";
    public static final String A_LIBERATED_WORLD = "A Liberated World";
    public static final String PLANETARY_SHIELD = "Planetary Shield";
    public static final String MAIN_POWER_PLANT = "Coruscant: Main Power Plant";
    public static final String LOWER_LEVELS = "Coruscant: Lower Levels";
    public static final String ROGUE_INSERTION = "Rogue Insertion";
    public static final String DECLARATION_OF_REBELLION = "Declaration Of Rebellion";
    public static final String THE_SHIELD_IS_DOWN = "The Shield Is Down!";

    private LegacyVb73Light() {
    }

    /**
     * Center Of Tyranny (front side of Center Of Tyranny / A Liberated World).
     */
    public static Filter centerOfTyranny() {
        return Filters.title(CENTER_OF_TYRANNY);
    }

    /**
     * (Virtual Block 7) Planetary Shield.
     */
    public static Filter planetaryShield() {
        return Filters.and(Filters.title(PLANETARY_SHIELD), Icon.LEGACY_BLOCK_7);
    }

    /**
     * (Virtual Block 7) Rebel Strike Team (front side of Rebel Strike Team / Garrison Destroyed (V)).
     */
    public static Filter rebelStrikeTeamV() {
        return Filters.and(Filters.title(Title.Rebel_Strike_Team), Icon.LEGACY_BLOCK_7);
    }
}
