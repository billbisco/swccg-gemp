package com.gempukku.swccgo.game.formats;

import com.gempukku.swccgo.collection.CollectionsManager;
import com.gempukku.swccgo.collection.LegacyCardPool;
import com.gempukku.swccgo.game.DeckInvalidException;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.logic.vo.SwccgDeck;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

/**
 * 2261_1045 (One Bright Spot, Master List text) was live in r16 and was retired when the current card 1261_45 took
 * that text (legacy/p2-vb5-vb6): blueprintMapping.txt aliases it to 1261_45, so decks that use it still load and
 * validate in Anything Goes, and the card pool no longer offers it as a version.
 */
public class RemovedLegacyVersionIdsVB6Tests {
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
    public void RemovedVersionIdLoadsItsCurrentCard() {
        SwccgCardBlueprint alias = library.getSwccgoCardBlueprint("2261_1045");
        assertNotNull(alias);
        assertEquals("1261_45", library.getBaseBlueprintId("2261_1045"));
        assertSame(library.getSwccgoCardBlueprint("1261_45").getClass(), alias.getClass());
        assertEquals(library.getSwccgoCardBlueprint("1261_45").getGameText(), alias.getGameText());
        assertNull(alias.getLegacyVersion());
    }

    @Test
    public void RemovedVersionIdIsNotOfferedAsAVersion() {
        assertFalse(LegacyCardPool.implementedVersionIds(library).contains("2261_1045"));
        assertEquals(0, collections.getDefaultCollection(false).getItemCount("2261_1045"));
    }

    @Test
    public void AnythingGoesDeckWithTheRemovedVersionIdStillValidates() throws DeckInvalidException {
        SwccgDeck deck = new SwccgDeck("removed version");
        deck.addCard("2261_1045");
        while (deck.getCards().size() < 60)
            deck.addCard("1_194"); // Stormtrooper
        formats.getFormat("anything_goes").validateCard("2261_1045", false);
        formats.getFormat("anything_goes").validateDeck(deck);
    }
}
