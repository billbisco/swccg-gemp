package com.gempukku.swccgo.game.formats;

import java.util.Map;

/**
 * The white-border anthology preview cards (First Anthology May 1997, Second Anthology July 1998) are the same cards as
 * their later black-border printings: legality, uniqueness and copy limits are shared (ai-team-skills
 * shared/swccg-card-identity, "White-bordered Decipher printings"; Bill 2026-11-12). Their classes (105_x, 107_x, also
 * loaded by the wiki ids 57_/58_/59_ through blueprintMapping.txt) carry the text printed on the preview card, so deck
 * counts and bans map them to the black-border card here.
 */
final class AnthologyPreviewCards {
    private static final Map<String, String> BLACK_BORDER_ID = Map.ofEntries(
            Map.entry("105_1", "7_12"),    // Commander Wedge Antilles
            Map.entry("105_2", "7_92"),    // Hit And Run
            Map.entry("105_3", "7_150"),   // X-wing Assault Squadron
            Map.entry("105_4", "7_167"),   // Boba Fett
            Map.entry("105_5", "7_303"),   // Death Star Assault Squadron
            Map.entry("105_6", "7_231"),   // Jabba's Influence
            Map.entry("107_1", "9_79"),    // Mon Calamari Star Cruiser
            Map.entry("107_2", "8_22"),    // Mon Mothma
            Map.entry("107_3", "8_58"),    // Rapid Deployment
            Map.entry("107_4", "9_125"),   // Flagship Operations
            Map.entry("107_5", "7_214"),   // Sarlacc
            Map.entry("107_6", "9_174"));  // Thunderflare

    private AnthologyPreviewCards() {
    }

    /** The black-border card a preview printing counts as, or the id itself when it is not a preview printing. */
    static String sameCardId(String blueprintId) {
        return BLACK_BORDER_ID.getOrDefault(blueprintId, blueprintId);
    }
}
