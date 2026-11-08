package com.gempukku.swccgo.game.formats;

import com.gempukku.swccgo.collection.CollectionsManager;
import com.gempukku.swccgo.collection.LegacyCardPool;
import com.gempukku.swccgo.game.DeckInvalidException;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.logic.vo.SwccgDeck;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

/**
 * 2240_2058 / 2240_2058_BACK (PC-E2 Hunt Down And Destroy The Jedi (V), live since release r16) were retired when the
 * current card 1240_58 took over their Virtual Cards Master List text (legacy/p2-vb3-vb4). blueprintMapping.txt
 * aliases them to the current card, so decks that use them still load and validate in Anything Goes, and the card pool
 * no longer offers them as versions.
 */
public class RemovedLegacyVersionIdsVB4Tests {
    private static SwccgCardBlueprintLibrary library;
    private static SwccgoFormatLibrary formats;
    private static CollectionsManager collections;

    @BeforeClass
    public static void setUpClass() {
        library = new SwccgCardBlueprintLibrary();
        formats = new SwccgoFormatLibrary(library);
        collections = new CollectionsManager(null, null, null, library);
    }

    @Test
    public void RemovedHuntDownVersionLoadsTheCurrentCard() {
        assertEquals("1240_58", library.getBaseBlueprintId("2240_2058"));
        assertEquals("1240_58_BACK", library.getBaseBlueprintId("2240_2058_BACK"));
        SwccgCardBlueprint front = library.getSwccgoCardBlueprint("2240_2058");
        SwccgCardBlueprint back = library.getSwccgoCardBlueprintBack("2240_2058");
        assertNotNull(front);
        assertNotNull(back);
        assertSame(library.getSwccgoCardBlueprint("1240_58").getClass(), front.getClass());
        assertSame(library.getSwccgoCardBlueprintBack("1240_58").getClass(), back.getClass());
        assertEquals(library.getSwccgoCardBlueprint("1240_58").getGameText(), front.getGameText());
        assertEquals(library.getSwccgoCardBlueprintBack("1240_58").getGameText(), back.getGameText());
        assertNull(front.getLegacyVersion());
    }

    @Test
    public void RemovedHuntDownVersionIsNotOfferedAsAVersion() {
        List<String> versions = LegacyCardPool.implementedVersionIds(library);
        assertFalse(versions.contains("2240_2058"));
        assertEquals(0, collections.getDefaultCollection(false).getItemCount("2240_2058"));
        // the other Hunt Down versions stay
        assertEquals(true, versions.contains("2240_58") && versions.contains("2240_1058"));
    }

    @Test
    public void AnythingGoesDeckWithTheRemovedHuntDownVersionStillValidates() throws DeckInvalidException {
        SwccgDeck deck = new SwccgDeck("removed version");
        deck.addCard("2240_2058");
        while (deck.getCards().size() < 60)
            deck.addCard("1_194"); // Stormtrooper
        formats.getFormat("anything_goes").validateDeck(deck);
    }
}
