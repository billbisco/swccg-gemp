package com.gempukku.swccgo.game.formats;

import com.gempukku.swccgo.game.DefaultCardCollection;
import com.gempukku.swccgo.game.DeckInvalidException;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.game.SwccgFormat;
import com.gempukku.swccgo.logic.vo.SwccgDeck;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class DeckFormatIndexTests {
    private static SwccgCardBlueprintLibrary library;
    private static SwccgoFormatLibrary formats;

    @BeforeClass
    public static void setUpClass() {
        library = new SwccgCardBlueprintLibrary();
        formats = new SwccgoFormatLibrary(library);
    }

    @Test
    public void TripleEncodingDoesNotConfusePremiereWithPremiereAnh() {
        String stamp = "a1b2c3d4e5f60708";
        String encoded = DeckFormatIndex.upsert("|", "premiere_anh_sealed", stamp, true);
        encoded = DeckFormatIndex.upsert(encoded, "premiere_anh", stamp, true);
        encoded = DeckFormatIndex.upsert(encoded, "open40card", stamp, false);
        assertTrue(DeckFormatIndex.isFreshPass(encoded, "premiere_anh", stamp));
        assertTrue(DeckFormatIndex.isFreshPass(encoded, "premiere_anh_sealed", stamp));
        assertFalse(DeckFormatIndex.isFreshPass(encoded, "premiere", stamp));
        assertFalse(DeckFormatIndex.isFreshPass(encoded, "open", stamp));
        assertFalse(DeckFormatIndex.isFreshPass(encoded, "open40card", stamp));
    }

    @Test
    public void LegacyPipeListIsUnknownNotAPass() {
        String stamp = formats.stampFor("open");
        assertTrue(DeckFormatIndex.needsCheck("|open|premiere|", "open", stamp));
        assertFalse(DeckFormatIndex.isFreshPass("|open|premiere|", "open", stamp));
    }

    @Test
    public void StaleStampNeedsRecheck() {
        String encoded = DeckFormatIndex.upsert("|", "open", "aaaaaaaaaaaaaaaa", true);
        String current = formats.stampFor("open");
        assertNotEquals("aaaaaaaaaaaaaaaa", current);
        assertTrue(DeckFormatIndex.needsCheck(encoded, "open", current));
        assertFalse(DeckFormatIndex.isFreshPass(encoded, "open", current));
    }

    @Test
    public void FreshFailDoesNotNeedRecheck() {
        String stamp = formats.stampFor("open");
        String encoded = DeckFormatIndex.upsert("|", "open", stamp, false);
        assertFalse(DeckFormatIndex.needsCheck(encoded, "open", stamp));
        assertFalse(DeckFormatIndex.isFreshPass(encoded, "open", stamp));
    }

    @Test
    public void PremiereSixtyCardDeckPassesOpenAndPremiereAnh() {
        SwccgDeck deck = nLightPremiere(60);
        SwccgFormat open = formats.getFormat("open");
        SwccgFormat premiereAnh = formats.getFormat("premiere_anh");
        assertTrue(DeckFormatIndex.evaluateAndIndex(deck, open, formats.stampFor("open")));
        assertTrue(DeckFormatIndex.evaluateAndIndex(deck, premiereAnh, formats.stampFor("premiere_anh")));
        assertFalse(DeckFormatIndex.evaluateAndIndex(deck, formats.getFormat("premiere_anh_sealed"),
                formats.stampFor("premiere_anh_sealed")));
        assertTrue(DeckFormatIndex.isFreshPass(deck.getFormatIndex(), "open", formats.stampFor("open")));
        assertTrue(DeckFormatIndex.isFreshPass(deck.getFormatIndex(), "premiere_anh", formats.stampFor("premiere_anh")));
        assertFalse(DeckFormatIndex.isFreshPass(deck.getFormatIndex(), "premiere_anh_sealed",
                formats.stampFor("premiere_anh_sealed")));
    }

    @Test
    public void FortyCardPremiereAnhDeckPassesSealedAndAnythingGoes() {
        SwccgDeck deck = nLightPremiere(40);
        assertTrue(DeckFormatIndex.evaluateAndIndex(deck, formats.getFormat("premiere_anh_sealed"),
                formats.stampFor("premiere_anh_sealed")));
        assertTrue(DeckFormatIndex.evaluateAndIndex(deck, formats.getFormat("anything_goes"),
                formats.stampFor("anything_goes")));
        assertFalse(DeckFormatIndex.evaluateAndIndex(deck, formats.getFormat("open"), formats.stampFor("open")));
    }

    @Test
    public void HothCardIsNotLegalInPremiereAnhSealed() {
        SwccgDeck withHoth = nLightPremiere(39);
        withHoth.addCard("3_28");
        assertFalse(DeckFormatIndex.evaluateAndIndex(withHoth, formats.getFormat("premiere_anh_sealed"),
                formats.stampFor("premiere_anh_sealed")));
        assertFalse(DeckFormatIndex.evaluateAndIndex(withHoth, formats.getFormat("premiere_anh"),
                formats.stampFor("premiere_anh")));
    }

    @Test
    public void AnythingGoesAcceptsFortyThroughSixty() {
        SwccgFormat ag = formats.getFormat("anything_goes");
        try {
            ag.validateDeck(nLightPremiere(40));
            ag.validateDeck(nLightPremiere(60));
        } catch (DeckInvalidException e) {
            fail(e.getMessage());
        }
        try {
            ag.validateDeck(nLightPremiere(39));
            fail("39-card list must fail Anything Goes");
        } catch (DeckInvalidException ignored) {
        }
        try {
            ag.validateDeck(nLightPremiere(61));
            fail("61-card list must fail Anything Goes");
        } catch (DeckInvalidException ignored) {
        }
    }

    @Test
    public void AnythingGoesRejectsMixedSides() {
        SwccgDeck mixed = nLightPremiere(39);
        mixed.addCard("1_168");
        try {
            formats.getFormat("anything_goes").validateDeck(mixed);
            fail("mixed side must fail Anything Goes");
        } catch (DeckInvalidException e) {
            assertTrue(e.getMessage().contains("both light side and dark side"));
        }
    }

    @Test
    public void AnythingGoesAllowsHothInAFortyCardList() {
        SwccgDeck deck = nLightPremiere(39);
        deck.addCard("3_28");
        try {
            formats.getFormat("anything_goes").validateDeck(deck);
        } catch (DeckInvalidException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void OpenAndAnythingGoesHaveDifferentStamps() {
        assertNotEquals(formats.stampFor("open"), formats.stampFor("anything_goes"));
        assertEquals(16, formats.stampFor("open").length());
        assertEquals(16, formats.stampFor("anything_goes").length());
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
        SwccgDeck deck = nLightPremiere(40);
        DefaultCardCollection owned = new DefaultCardCollection();
        owned.addItem("1_28", 39);
        assertFalse(DeckFormatIndex.deckOwnedInCollection(deck, owned));
        owned.addItem("1_28", 1);
        assertTrue(DeckFormatIndex.deckOwnedInCollection(deck, owned));
    }

    private static SwccgDeck nLightPremiere(int n) {
        SwccgDeck deck = new SwccgDeck("premiere-" + n);
        for (int i = 0; i < n; i++) {
            deck.addCard("1_28");
        }
        return deck;
    }
}
