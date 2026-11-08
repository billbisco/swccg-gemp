package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.logic.GameUtils;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Shared checks for the Legacy version classes of Virtual Block 8, Virtual Block 9 and Virtual Shields
 * (docs/legacy-versions.md): exact text and version info of a version, exact text of the current card, and
 * "same card" (title, uniqueness, persona) between a version and its current printing.
 */
public final class Vb89VersionAsserts {
    private static SwccgCardBlueprintLibrary library;

    private Vb89VersionAsserts() {
    }

    public static synchronized SwccgCardBlueprint blueprint(String id) {
        if (library == null)
            library = new SwccgCardBlueprintLibrary();
        SwccgCardBlueprint blueprint = library.getSwccgoCardBlueprint(id);
        assertNotNull(id, blueprint);
        return blueprint;
    }

    /** The current (Legacy) card plays exactly this text and is no version. */
    public static void assertCurrentCard(String id, String title, String gameText) {
        SwccgCardBlueprint card = blueprint(id);
        assertEquals(title, card.getTitle());
        assertEquals(gameText, card.getGameText());
        assertNull(id + " is the current card", card.getLegacyVersion());
    }

    /** A version: exact text, the current printing it belongs to, label (shown in the full name) and dates. */
    public static void assertVersion(String id, String currentId, String title, String gameText, String label,
                                     LocalDate printedOn, LocalDate supersededOn) {
        SwccgCardBlueprint card = blueprint(id);
        assertEquals(title, card.getTitle());
        assertTrue(card.isLegacy());
        assertEquals(gameText, card.getGameText());
        LegacyVersion version = card.getLegacyVersion();
        assertNotNull(id + " has version info", version);
        assertEquals(currentId, version.currentPrintingId());
        assertEquals(label, version.label());
        assertEquals(printedOn, version.printedOn());
        assertEquals(supersededOn, version.supersededOn());
        assertEquals(label.isEmpty() ? title : title + " [" + label + "]", GameUtils.getFullName(card));
    }

    /** A version is the same card as its current printing (uniqueness and persona rules see one card). */
    public static void assertSameCard(String versionId, String currentId) {
        SwccgCardBlueprint version = blueprint(versionId);
        SwccgCardBlueprint current = blueprint(currentId);
        assertEquals(current.getTitle(), version.getTitle());
        assertEquals(current.getTitles(), version.getTitles());
        assertEquals(current.getUniqueness(), version.getUniqueness());
        assertEquals(current.getPersonas(), version.getPersonas());
        assertEquals(current.getSide(), version.getSide());
        assertEquals(current.getCardCategory(), version.getCardCategory());
        assertNotEquals(current.getGameText(), version.getGameText());
    }
}
