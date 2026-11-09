package com.gempukku.swccgo.game.formats;

import com.gempukku.swccgo.collection.CollectionsManager;
import com.gempukku.swccgo.collection.LegacyCardPool;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.game.DeckInvalidException;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.logic.vo.SwccgDeck;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

/**
 * Version ids that were live (release r15) and were retired when their current card took over their text
 * (legacy/p2-vb8-vb9-vs): blueprintMapping.txt aliases each to its current card, so decks that use them still load
 * and validate in Anything Goes, and the card pool no longer offers them as versions.
 */
public class RemovedLegacyVersionIdsTests {
    private static final Map<String, String> REMOVED = Map.of(
            "2281_1044", "1281_44",
            "2281_1044_BACK", "1281_44_BACK",
            "2291_19", "1291_19",
            "2291_29", "1291_29",
            "2291_29_BACK", "1291_29_BACK",
            "2291_58", "1291_58");

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
        });
    }

    @Test
    public void RemovedVersionIdsAreNotOfferedAsVersions() {
        List<String> versions = LegacyCardPool.implementedVersionIds(library);
        for (String removed : REMOVED.keySet()) {
            assertFalse(removed, versions.contains(removed));
            assertEquals(removed, 0, collections.getDefaultCollection(false).getItemCount(removed));
        }
    }

    private static SwccgDeck deck(Side side, String... cards) {
        SwccgDeck deck = new SwccgDeck("removed versions");
        for (String card : cards)
            deck.addCard(card);
        while (deck.getCards().size() < 60)
            deck.addCard(side == Side.DARK ? "1_194" : "1_12"); // Stormtrooper / Jawa
        return deck;
    }

    @Test
    public void AnythingGoesDeckWithARemovedVersionIdStillValidates() throws DeckInvalidException {
        var anythingGoes = formats.getFormat("anything_goes");
        anythingGoes.validateDeck(deck(Side.LIGHT, "2281_1044", "2291_58"));
        anythingGoes.validateDeck(deck(Side.DARK, "2291_19", "2291_29"));
    }
}
