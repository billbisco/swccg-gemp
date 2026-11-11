package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.logic.GameUtils;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/** Identity checks shared by the pre-errata Legacy version tests (docs/legacy-versions.md). */
public final class LegacyVersionAsserts {
    private LegacyVersionAsserts() {
    }

    /**
     * The version is the same card as its current printing (title, uniqueness, side, types, persona, icons, stats, set),
     * has exactly the given old text, and carries the given version info. An empty label adds nothing to the full name.
     */
    public static void assertVersionOf(SwccgCardBlueprint current, SwccgCardBlueprint version, String oldText,
                                       String currentPrintingId, String label, LocalDate printedOn, LocalDate supersededOn) {
        assertNull(current.getLegacyVersion());
        assertEquals(current.getTitle(), version.getTitle());
        assertEquals(current.getTitles(), version.getTitles());
        assertEquals(current.getUniqueness(), version.getUniqueness());
        assertEquals(current.getSide(), version.getSide());
        assertEquals(current.getCardCategory(), version.getCardCategory());
        assertEquals(current.getCardTypes(), version.getCardTypes());
        assertEquals(current.getCardSubtype(), version.getCardSubtype());
        assertEquals(current.hasVirtualSuffix(), version.hasVirtualSuffix());
        assertEquals(current.getPersonas(), version.getPersonas());
        for (Icon icon : Icon.values())
            assertEquals(icon.name(), current.getIconCount(icon), version.getIconCount(icon));
        assertEquals(current.getExpansionSet(), version.getExpansionSet());
        assertEquals(current.getRarity(), version.getRarity());
        assertEquals(current.getDestiny(), version.getDestiny());
        assertTrue(version.isLegacy());

        assertEquals(oldText, version.getGameText());
        if (!LegacyBatchR8B1B6VersionsTests.CURRENT_IDS.contains(version.getLegacyVersion().currentPrintingId())) // batch R8: the current Master List text repeats the release-sheet text word for word
            assertNotEquals(current.getGameText(), version.getGameText());

        LegacyVersion info = version.getLegacyVersion();
        assertEquals(currentPrintingId, info.currentPrintingId());
        assertEquals(label, info.label());
        assertEquals(printedOn, info.printedOn());
        assertEquals(supersededOn, info.supersededOn());
        assertFalse(info.source().isEmpty());
        if (label.isEmpty())
            assertEquals(GameUtils.getFullName(current), GameUtils.getRulesFullName(version));
        else
            assertEquals(GameUtils.getFullName(current) + " [" + label + "]", GameUtils.getRulesFullName(version));
    }
}
