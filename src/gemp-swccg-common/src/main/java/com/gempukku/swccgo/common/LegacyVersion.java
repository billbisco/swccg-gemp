package com.gempukku.swccgo.common;

import java.time.LocalDate;

/**
 * A historical (pre-errata) version of a Players Committee Virtual Block ("Legacy") card.
 * <p>
 * Id scheme (see docs/legacy-versions.md): a version of the card printed as {@code <sheet>_<n>}
 * (sheet set 1200–1292) has the blueprint id {@code <sheet + 1000>_<n + 1000 * k>}, where k = 0 is the
 * first older version, k = 1 the next, and so on. For example, the first older version of
 * Dark Waters &amp; Krayt Dragon Bones (1261_33) is 2261_33. Version sets 2200–2292 are in Anything Goes
 * and not in Legacy, so Legacy keeps only the latest text of every card.
 * <p>
 * A version is the same card as its current printing: same title, uniqueness and persona, so uniqueness,
 * persona and "title" rules treat the two as one card, and deck counts (restricted/banned) count the version
 * as its current printing.
 *
 * @param currentPrintingId the published sheet id of the current card this is a version of (e.g. "1261_33")
 * @param label             short visible label for the version, e.g. "Original (Sep 2009 slip)"
 * @param source            where the text comes from, e.g. "Virtual Block 6.1 release sheet, card #32"
 * @param printedOn         date the version's text was released
 * @param supersededOn      date the text was replaced (the errata); may be an upper bound, null if unknown
 */
public record LegacyVersion(String currentPrintingId, String label, String source, LocalDate printedOn,
                            LocalDate supersededOn) {

    /** Version set number = published sheet set number + this. */
    public static final int VERSION_SET_OFFSET = 1000;
    /** Card number of the (k+1)-th older version = current card number + k * this. */
    public static final int VERSION_NUMBER_STEP = 1000;
    /** Number of older versions per card the card pool looks for (k = 0 .. MAX_VERSIONS_PER_CARD - 1). */
    public static final int MAX_VERSIONS_PER_CARD = 3;

    public static final int FIRST_VERSION_SET = 1200 + VERSION_SET_OFFSET;
    public static final int LAST_VERSION_SET = 1292 + VERSION_SET_OFFSET;

    public LegacyVersion {
        if (currentPrintingId == null || label == null || label.isEmpty() || printedOn == null)
            throw new IllegalArgumentException("A Legacy version needs its current printing id, a label and a date");
    }

    /** True when the blueprint id (modifiers such as * ^ and _BACK allowed) is a pre-errata Legacy version. */
    public static boolean isVersionBlueprint(String blueprintId) {
        return currentPrintingIdOf(blueprintId) != null;
    }

    /**
     * The published sheet id of the current card for a version id ("2261_1033" -> "1261_33"),
     * or null if the id is not a version id.
     */
    public static String currentPrintingIdOf(String blueprintId) {
        if (blueprintId == null)
            return null;
        String id = blueprintId.replace("*", "").replace("^", "");
        String[] parts = id.split("_");
        if (parts.length < 2)
            return null;
        try {
            int set = Integer.parseInt(parts[0]);
            int number = Integer.parseInt(parts[1]);
            if (set < FIRST_VERSION_SET || set > LAST_VERSION_SET || number <= 0)
                return null;
            int currentNumber = number % VERSION_NUMBER_STEP;
            if (currentNumber == 0)
                return null;
            String current = (set - VERSION_SET_OFFSET) + "_" + currentNumber;
            return parts.length > 2 ? current + "_" + parts[2] : current;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** The id of the (k+1)-th older version of a published sheet card ("1261_33", 0 -> "2261_33"). */
    public static String versionIdOf(String currentPrintingId, int k) {
        String[] parts = currentPrintingId.split("_");
        int set = Integer.parseInt(parts[0]);
        int number = Integer.parseInt(parts[1]);
        return (set + VERSION_SET_OFFSET) + "_" + (number + k * VERSION_NUMBER_STEP);
    }
}
