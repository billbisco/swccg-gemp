package com.gempukku.swccgo.game.formats;

import com.gempukku.swccgo.game.DefaultCardCollection;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.logic.vo.SwccgDeck;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Save-time format index: a Premiere list is also legal in later Premiere-block formats;
 * a 40-card Premiere-ANH list is legal in Premiere-ANH Sealed; Open-illegal cards stay out.
 */
public class DeckFormatIndexTests {
    private static SwccgCardBlueprintLibrary library;
    private static SwccgoFormatLibrary formats;

    @BeforeClass
    public static void setUpClass() {
        library = new SwccgCardBlueprintLibrary();
        formats = new SwccgoFormatLibrary(library);
    }

    @Test
    public void ContainsFormatDoesNotConfusePremiereWithPremiereAnh() {
        String encoded = "|premiere_anh_sealed|premiere_anh|open40card|";
        assertTrue(DeckFormatIndex.containsFormat(encoded, "premiere_anh"));
        assertTrue(DeckFormatIndex.containsFormat(encoded, "premiere_anh_sealed"));
        assertFalse(DeckFormatIndex.containsFormat(encoded, "premiere"));
        assertFalse(DeckFormatIndex.containsFormat(encoded, "open"));
    }

    @Test
    public void PremiereSixtyCardDeckIsLegalInLaterPremiereBlockFormats() {
        SwccgDeck deck = sixtyLightPremiere();
        String encoded = DeckFormatIndex.computeValidFormats(deck, formats);
        assertTrue(DeckFormatIndex.containsFormat(encoded, "premiere"));
        assertTrue(DeckFormatIndex.containsFormat(encoded, "premiere_anh"));
        assertTrue(DeckFormatIndex.containsFormat(encoded, "premiere_hoth"));
        assertFalse(DeckFormatIndex.containsFormat(encoded, "premiere_anh_sealed"));
    }

    @Test
    public void FortyCardPremiereAnhDeckIsLegalInPremiereAnhSealed() {
        SwccgDeck deck = fortyLightPremiere();
        String encoded = DeckFormatIndex.computeValidFormats(deck, formats);
        assertTrue(DeckFormatIndex.containsFormat(encoded, "premiere_anh_sealed"));
        assertTrue(DeckFormatIndex.containsFormat(encoded, "open40card"));
        assertFalse(DeckFormatIndex.containsFormat(encoded, "open"));
    }

    @Test
    public void HothCardIsNotLegalInPremiereAnhSealed() {
        SwccgDeck withHoth = new SwccgDeck("hoth-in-anh");
        for (int i = 0; i < 39; i++) {
            withHoth.addCard("1_28");
        }
        withHoth.addCard("3_28");
        String encoded = DeckFormatIndex.computeValidFormats(withHoth, formats);
        assertFalse(DeckFormatIndex.containsFormat(encoded, "premiere_anh_sealed"));
        assertFalse(DeckFormatIndex.containsFormat(encoded, "premiere_anh"));
    }

    @Test
    public void ConstructedCollectionCodesAreDefaultAndPermanent() {
        assertTrue(DeckFormatIndex.isConstructedCollection(null));
        assertTrue(DeckFormatIndex.isConstructedCollection("default"));
        assertTrue(DeckFormatIndex.isConstructedCollection("permanent"));
        assertFalse(DeckFormatIndex.isConstructedCollection("premiere_anh_sealed_202610"));
    }

    @Test
    public void DeckOwnedInCollectionRequiresEveryCopy() {
        SwccgDeck deck = fortyLightPremiere();
        DefaultCardCollection owned = new DefaultCardCollection();
        owned.addItem("1_28", 39);
        assertFalse(DeckFormatIndex.deckOwnedInCollection(deck, owned));
        owned.addItem("1_28", 1);
        assertTrue(DeckFormatIndex.deckOwnedInCollection(deck, owned));
    }

    private static SwccgDeck sixtyLightPremiere() {
        SwccgDeck deck = new SwccgDeck("premiere-60");
        for (int i = 0; i < 60; i++) {
            deck.addCard("1_28");
        }
        return deck;
    }

    private static SwccgDeck fortyLightPremiere() {
        SwccgDeck deck = new SwccgDeck("premiere-40");
        for (int i = 0; i < 40; i++) {
            deck.addCard("1_28");
        }
        return deck;
    }
}
