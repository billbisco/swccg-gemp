package com.gempukku.swccgo.collection;

import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SortAndFilterCards;
import com.gempukku.swccgo.game.formats.SwccgoFormatLibrary;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LegacyCardPoolTests {
    private static SwccgCardBlueprintLibrary library;
    private static CollectionsManager collections;
    private static SwccgoFormatLibrary formats;

    @BeforeClass
    public static void loadPools() {
        library = new SwccgCardBlueprintLibrary();
        collections = new CollectionsManager(null, null, null, library);
        formats = new SwccgoFormatLibrary(library);
    }

    @Test
    public void LegacySheetCardsAreSelectableInNormalAndPlaytesterCollections() {
        //test1: All eight implemented 7.2 Light cards are offered with the normal 60 copies.
        for (int number : new int[]{20, 22, 23, 24, 25, 26, 27, 28}) {
            String id = "1272_" + number;
            assertEquals(60, collections.getDefaultCollection(false).getItemCount(id)); //test1
            assertEquals(60, collections.getDefaultCollection(true).getItemCount(id)); //test1
        }
    }

    @Test
    public void LegacyPoolKeepsPcCardsWithoutDuplicatingAliases() {
        //test1: A PC implementation remains available under its canonical id.
        assertEquals(60, collections.getDefaultCollection(false).getItemCount("601_35")); //test1
        assertEquals(0, collections.getDefaultCollection(false).getItemCount("1272_9")); //test1
    }

    @Test
    public void LegacyPoolOmitsUnimplementedSlotsWithoutHidingImplementedCards() {
        //test1: Simulate one published slot without a blueprint, independent of future card waves.
        var partialLibrary = new SwccgCardBlueprintLibrary() {
            @Override
            public SwccgCardBlueprint getSwccgoCardBlueprint(String id) {
                return id.equals("1272_28") ? null : super.getSwccgoCardBlueprint(id);
            }
        };
        var partial = new CollectionsManager(null, null, null, partialLibrary);
        assertEquals(0, partial.getDefaultCollection(false).getItemCount("1272_28")); //test1
        assertEquals(60, partial.getDefaultCollection(false).getItemCount("1272_22")); //test1
    }

    @Test
    public void LegacyShieldBeyondTheOldInventoryIsSelectable() {
        //test1: Planetary Defenses, added after the original 17-card shield inventory, is included.
        assertEquals(60, collections.getDefaultCollection(false).getItemCount("1200_18")); //test1
        assertEquals(60, collections.getDefaultCollection(false).getItemCount("1292_11")); //test1
    }

    @Test
    public void LegacyFormatEnumeratesSheetCardsAndPcImplementations() {
        //test1: Format-wide searches see both new sheet ids and the existing PC implementation set.
        var pool = formats.getFormat("legacy").getAllCardBlueprintsValidInFormat();
        assertTrue(pool.contains(library.getSwccgoCardBlueprint("1272_22"))); //test1
        assertTrue(pool.contains(library.getSwccgoCardBlueprint("601_17"))); //test1
    }

    @Test
    public void OpenFormatDoesNotGainLegacyCards() {
        //test1: Enumerating more implemented ids does not alter format legality.
        var pool = formats.getFormat("open").getAllCardBlueprintsValidInFormat();
        assertFalse(pool.contains(library.getSwccgoCardBlueprint("1272_22"))); //test1
        assertFalse(pool.contains(library.getSwccgoCardBlueprint("601_17"))); //test1
    }

    @Test
    public void LegacySetDropdownFindsPublishedSheetIds() {
        //test1: The existing Legacy set choice uses 601, but must also include published 12xx ids.
        var result = new SortAndFilterCards().process("format:legacy set:601 name:Gunrunner",
                collections.getDefaultCollection(false).getAll().values(), library, formats, Collections.emptyMap());
        assertTrue(result.stream().anyMatch(item -> item.getBlueprintId().equals("1272_22"))); //test1
    }

    @Test
    public void LegacyVirtualRarityFilterUsesThePrintedBlueprintRarity() {
        //test1: Legacy has no Decipher booster rarity table, but its virtual rarity is known.
        var result = new SortAndFilterCards().process("format:legacy rarity:V name:Gunrunner",
                collections.getDefaultCollection(false).getAll().values(), library, formats, Collections.emptyMap());
        assertTrue(result.stream().anyMatch(item -> item.getBlueprintId().equals("1272_22"))); //test1
    }

    @Test
    public void PremiereSetDropdownDoesNotGainLegacySheetCards() {
        //test1: Grouping Legacy's old set choice does not broaden another set choice.
        var result = new SortAndFilterCards().process("format:legacy set:1 name:Gunrunner",
                collections.getDefaultCollection(false).getAll().values(), library, formats, Collections.emptyMap());
        assertTrue(result.isEmpty()); //test1
    }
}
