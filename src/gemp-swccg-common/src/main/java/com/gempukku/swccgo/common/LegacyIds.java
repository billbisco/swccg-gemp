package com.gempukku.swccgo.common;

/**
 * Printing-id tests for the Players Committee's 2009–2014 Virtual Block ("Legacy") cards.
 * Set 601 is the PC's implementation set; 1200–1292 are one GEMP set per published sheet.
 */
public final class LegacyIds {
    private LegacyIds() {
    }

    /**
     * True when the listed blueprint id is a Legacy printing (set 601 or 1200–1292).
     * Letter suffixes on the card number (ai, later o) are ignored.
     */
    public static boolean isLegacyBlueprint(String blueprintId) {
        if (blueprintId == null || blueprintId.isEmpty()) {
            return false;
        }
        int underscore = blueprintId.indexOf('_');
        String setPart = underscore < 0 ? blueprintId : blueprintId.substring(0, underscore);
        try {
            int set = Integer.parseInt(setPart);
            return set == 601 || (set >= 1200 && set <= 1292);
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
