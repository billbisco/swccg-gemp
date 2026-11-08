package com.gempukku.swccgo.game.formats;

import com.gempukku.swccgo.collection.CollectionsManager;
import com.gempukku.swccgo.collection.LegacyCardPool;
import com.gempukku.swccgo.game.DeckInvalidException;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.logic.vo.SwccgDeck;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

/**
 * Version ids of Virtual Block 1/2 that were live (release r16) and were retired when their current card took over
 * their text (legacy/p2-vb1-vb2): 2220_161 (Hoth: Echo Med Lab original) is aliased to 1220_161 in
 * blueprintMapping.txt, so decks that use it still load and validate in Anything Goes.
 */
public class RemovedLegacyVersionIdsVB2Tests {
    private static final Map<String, String> REMOVED = Map.of("2220_161", "1220_161");

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
    public void RemovedVersionIdsLoadTheirCurrentCard() {
        REMOVED.forEach((removed, current) -> {
            SwccgCardBlueprint alias = library.getSwccgoCardBlueprint(removed);
            assertNotNull(removed, alias);
            assertEquals(removed, current, library.getBaseBlueprintId(removed));
            assertSame(removed, library.getSwccgoCardBlueprint(current).getClass(), alias.getClass());
            assertEquals(removed, library.getSwccgoCardBlueprint(current).getGameText(), alias.getGameText());
            assertNull(removed, alias.getLegacyVersion());
        });
    }

    @Test
    public void RemovedVersionIdsAreNotOfferedAsVersions() {
        var versions = LegacyCardPool.implementedVersionIds(library);
        for (String removed : REMOVED.keySet()) {
            assertFalse(removed, versions.contains(removed));
            assertEquals(removed, 0, collections.getDefaultCollection(false).getItemCount(removed));
        }
    }

    @Test
    public void AnythingGoesDeckWithARemovedVersionIdStillValidates() throws DeckInvalidException {
        var anythingGoes = formats.getFormat("anything_goes");
        anythingGoes.validateCard("2220_161", false);
        SwccgDeck deck = new SwccgDeck("removed versions VB2");
        deck.addCard("2220_161");
        while (deck.getCards().size() < 60)
            deck.addCard("1_28"); // Rebel Trooper
        anythingGoes.validateDeck(deck);
    }
}
