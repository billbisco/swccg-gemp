package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.LegacyVersion;

import java.util.Set;

/**
 * Legacy versions whose words are repeated word for word by the current card (Bill 2026-11-11: every publication is a
 * version, even a word-for-word repeat). Example: the Virtual Cards Master List (2013-04-27) reprints a release-sheet
 * text after the PC website card list had changed it; the Master List text is the current card and the sheet text
 * stays a version of its own with the same words. Keyed by current printing id and version label.
 */
public final class LegacyRepeatedTexts {
    private LegacyRepeatedTexts() {
    }

    /** Batch R8-P (play changes): originals repeated by the Master List; Fleet Security Protocols PC-E1 (2010 sheet);
     * Assassin's Blaster Rifle PC-E1 (card list) repeated by the PC card image (PC-E3); PC card images (and the
     * Well Guarded / Rennek Master List texts) whose words the current card repeats. */
    private static final Set<String> REPEATED = Set.of(
            "1210_103|", "1210_127|", "1210_159|", "1220_6|", "1220_10|", "1220_93|", "1230_47|", "1240_37|",
            "1250_1|", "1250_2|", "1250_2ai|", "1250_18|", "1261_35|", "1261_80|", "1261_81|", "1261_82|",
            "1261_97|", "1261_101|", "1261_106|", "1273_34|", "1274_7|", "1281_15|", "1282_19|",
            "1261_34|PC-E1", "1282_11|PC-E1",
            // PC card images that repeat the current words (server upload dates; Bill 2026-11-11 09:14)
            "1210_103|PC-E2", "1210_127|PC-E2", "1210_159|PC-E2", "1220_6|PC-E2", "1220_10|PC-E2", "1220_93|PC-E2", "1230_47|PC-E2", "1250_2|PC-E2", "1250_18|PC-E2", "1261_35|PC-E2", "1261_80|PC-E2", "1261_81|PC-E2", "1261_82|PC-E2", "1261_97|PC-E2", "1261_106|PC-E2", "1273_34|PC-E2", "1274_7|PC-E2", "1250_2ai|PC-E2", "1281_15|PC-E2", "1282_19|PC-E2", "1261_34|PC-E3");

    /** True when the current card repeats this version's words exactly. */
    public static boolean isRepeatedByCurrent(LegacyVersion version) {
        return version != null && REPEATED.contains(version.currentPrintingId() + "|" + version.label());
    }
}
