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
            assertTrue(differsOrIsRepeatedByTheCurrentCard(current, version));
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

    /**
     * Every publication is a version (Bill, 2026-11-11): a version may repeat the current text word for word when the
     * current card is a later publication (Master List, PC card image) that reprinted it after a different text; that
     * different text must then be its own version (k = 1 .. 3) of the same current printing.
     */
    public static boolean differsOrIsRepeatedByTheCurrentCard(SwccgCardBlueprint current, SwccgCardBlueprint version) {
        if (!java.util.Objects.equals(current.getGameText(), version.getGameText()))
            return true;
        var info = version.getLegacyVersion();
        if (info == null)
            return false;
        var library = com.gempukku.swccgo.framework.VirtualTableScenario._cardLibrary;
        for (int k = 1; k <= 3; k++) {
            String id = com.gempukku.swccgo.common.LegacyVersion.versionIdOf(info.currentPrintingId(), k);
            SwccgCardBlueprint other = library.getBaseBlueprintId(id).equals(id) ? library.getSwccgoCardBlueprint(id) : null;
            if (other != null && other != version && !java.util.Objects.equals(other.getGameText(), current.getGameText()))
                return true;
        }
        return false;
    }
}
