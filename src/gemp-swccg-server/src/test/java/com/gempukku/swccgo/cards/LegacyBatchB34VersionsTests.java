package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.collection.LegacyCardPool;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Legacy batch B34 (leftovers; ai-team-skills shared/gemp-legacy-cards/versions/BATCH-METHOD.md): Jedi Survivor (V).
 * The Virtual Block 5 release-sheet slip prints destiny 1; the PC card image and the Master List (2013-04-27) print
 * destiny 3 with the same game text. Bill: stat-only changes count as versions, so the destiny-1 original is its own
 * 22xx card (2250_41, an icon-loss alias of 1250_41 before this batch). Ice-Heart (2271_5) keeps its printed title
 * (LegacyVersionPrintedTitleTests). Images are pinned in cardImageUrlTests.js.
 */
public class LegacyBatchB34VersionsTests {
    private static SwccgCardBlueprintLibrary library;

    @BeforeClass
    public static void setUpClass() {
        library = new SwccgCardBlueprintLibrary();
    }

    @Test
    public void CurrentJediSurvivorIsDestiny3() {
        SwccgCardBlueprint card = library.getSwccgoCardBlueprint("1250_41");
        assertNull(card.getLegacyVersion());
        assertEquals(3f, card.getDestiny(), 0.001f);
        assertEquals(3f, card.getAlternateDestiny(), 0.001f);
    }

    @Test
    public void OriginalJediSurvivorIsItsOwnCardWithDestiny1() {
        assertEquals("2250_41", library.getBaseBlueprintId("2250_41"));
        SwccgCardBlueprint original = library.getSwccgoCardBlueprint("2250_41");
        SwccgCardBlueprint current = library.getSwccgoCardBlueprint("1250_41");
        assertNotEquals(current.getClass(), original.getClass());
        assertEquals(1f, original.getDestiny(), 0.001f);
        assertEquals("a single destiny value, not a second one", 1f, original.getAlternateDestiny(), 0.001f);
        LegacyVersion version = original.getLegacyVersion();
        assertNotNull(version);
        assertEquals("1250_41", version.currentPrintingId());
        assertEquals("", version.label());
        assertEquals(LocalDate.of(2009, 12, 7), version.printedOn());
        assertEquals(LocalDate.of(2013, 4, 27), version.supersededOn());
        assertEquals(current.getTitle(), original.getTitle());
        assertEquals(current.getGameText(), original.getGameText());
        assertEquals(current.getLore(), original.getLore());
        assertEquals(current.getPower(), original.getPower());
        assertEquals(current.getAbility(), original.getAbility());
        assertEquals(current.getDeployCost(), original.getDeployCost());
        assertEquals(current.getForfeit(), original.getForfeit());
        assertTrue(LegacyCardPool.implementedVersionIds(library).contains("2250_41"));
    }
}
