package com.gempukku.swccgo.collection;

import com.gempukku.swccgo.game.SortAndFilterCards;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.game.formats.SwccgoFormatLibrary;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class OriginalVirtualCardPoolTests {
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
    public void AnythingGoesOffersTheExactTrainedPrintingInBothCollections() {
        for (boolean playtesting : new boolean[]{false, true}) {
            var result = new SortAndFilterCards().process("format:anything_goes set:1115 name:Trained",
                    collections.getDefaultCollection(playtesting).getAll().values(), library, formats,
                    Collections.emptyMap());
            assertTrue(result.stream().anyMatch(item -> item.getBlueprintId().equals("1115_67")));
            assertEquals(60, collections.getDefaultCollection(playtesting).getItemCount("1115_67"));
        }
    }

    @Test
    public void FormatCardChoicesRespectOriginalVirtualCutoffs() {
        var trained = library.getSwccgoCardBlueprint("1115_67");
        assertNotNull(trained);
        assertTrue(formats.getFormat("anything_goes").getAllCardBlueprintsValidInFormat().contains(trained));
        assertTrue(formats.getFormat("premiere_ovs15").getAllCardBlueprintsValidInFormat().contains(trained));
        assertFalse(formats.getFormat("premiere_ovs14").getAllCardBlueprintsValidInFormat().contains(trained));
        assertFalse(formats.getFormat("open").getAllCardBlueprintsValidInFormat().contains(trained));
        assertFalse(formats.getFormat("legacy").getAllCardBlueprintsValidInFormat().contains(trained));
    }
}
