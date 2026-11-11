package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.logic.GameUtils;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/** Identity checks shared by the pre-errata Legacy version tests (docs/legacy-versions.md). */
public final class LegacyVersionAssertions {
    private LegacyVersionAssertions() {
    }

    /**
     * The version is the same card as its current printing (title, uniqueness, side, set, persona) with its own text
     * and version information. An original printing has no label and shows no suffix in its full name.
     */
    public static void assertLegacyVersion(SwccgCardBlueprint version, SwccgCardBlueprint current, String currentId,
                                           String label, LocalDate printedOn, LocalDate supersededOn, String gameText) {
        assertEquals(gameText, version.getGameText());
        if (!LegacyBatchR8B1B6VersionsTests.CURRENT_IDS.contains(version.getLegacyVersion().currentPrintingId())) // batch R8: the current Master List text repeats the release-sheet text word for word
            assertNotEquals(current.getGameText(), version.getGameText());
        assertNull(current.getLegacyVersion());
        var info = version.getLegacyVersion();
        assertEquals(currentId, info.currentPrintingId());
        assertEquals(label, info.label());
        assertEquals(printedOn, info.printedOn());
        assertEquals(supersededOn, info.supersededOn());
        assertEquals(current.getTitle(), version.getTitle());
        assertEquals(current.getUniqueness(), version.getUniqueness());
        assertEquals(current.getSide(), version.getSide());
        assertEquals(current.getCardTypes(), version.getCardTypes());
        assertEquals(current.getPersonas(), version.getPersonas());
        assertEquals(current.getExpansionSet(), version.getExpansionSet());
        assertEquals(current.hasVirtualSuffix(), version.hasVirtualSuffix());
        assertEquals(current.getDestiny(), version.getDestiny(), 0.001f);
        for (var icon : com.gempukku.swccgo.common.Icon.values())
            assertEquals(icon.name(), current.getIconCount(icon), version.getIconCount(icon));
        assertTrue(version.isLegacy());
        String fullName = GameUtils.getFullName(version);
        if (label.isEmpty())
            assertFalse(fullName, fullName.contains("["));
        else
            assertTrue(fullName, fullName.endsWith(" [" + label + "]"));
    }
}
