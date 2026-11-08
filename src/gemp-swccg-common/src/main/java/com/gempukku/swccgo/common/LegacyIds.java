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

    /**
     * The printing an alternate-image (AI) Legacy printing is the same card as: "1281_30ai" -> "1281_30".
     * Any other id is returned unchanged. An AI printing with a class of its own (docs/legacy-versions.md) is still
     * the same card as its base printing for deck counts and bans.
     */
    public static String withoutAlternateImageSuffix(String blueprintId) {
        if (blueprintId == null || !isLegacyBlueprint(blueprintId))
            return blueprintId;
        String[] parts = blueprintId.split("_", 3);
        if (parts.length < 2 || !parts[1].endsWith("ai") || parts[1].length() <= 2
                || !parts[1].substring(0, parts[1].length() - 2).chars().allMatch(Character::isDigit))
            return blueprintId;
        String base = parts[0] + "_" + parts[1].substring(0, parts[1].length() - 2);
        return parts.length > 2 ? base + "_" + parts[2] : base;
    }
}
