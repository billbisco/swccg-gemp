package com.gempukku.swccgo.game.formats;

import com.gempukku.swccgo.db.DeckSerialization;
import com.gempukku.swccgo.game.DeckInvalidException;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.logic.vo.SwccgDeck;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Virtual Modern occupancy rewrite: a dated format maps former PC ids to 701/702
 * before set checks. Current Open / Anything Goes do not rewrite.
 */
public class OccupancyRewriteTests {
    private static SwccgCardBlueprintLibrary library;
    private static SwccgoFormatLibrary formats;

    @BeforeClass
    public static void setUpClass() {
        library = new SwccgCardBlueprintLibrary();
        formats = new SwccgoFormatLibrary(library);
    }

    @Test
    public void JsonRowsMatchWikiOccupancyTable() {
        assertEquals(5, OccupancyRewriter.replacedCount());
        assertEquals(10, OccupancyRewriter.blankedCount());
        OccupancyRewriter.Row justice = OccupancyRewriter.virtualModern().get("200_109");
        assertEquals("701_109", justice.newId);
        assertEquals(LocalDate.parse("2021-03-18"), justice.until);
        assertFalse(justice.blanked);
        OccupancyRewriter.Row clumsy = OccupancyRewriter.virtualModern().get("213_45");
        assertEquals("702_213045", clumsy.newId);
        assertEquals(LocalDate.parse("2024-07-22"), clumsy.until);
        assertTrue(clumsy.blanked);
    }

    @Test
    public void Date20210301RewritesCoarseIdToImperialJustice() {
        assertEquals("701_109", OccupancyRewriter.apply("200_109", LocalDate.parse("2021-03-01")));
        assertEquals("Imperial Justice", library.getSwccgoCardBlueprint("701_109").getTitle());
    }

    @Test
    public void Date20210301Dark60OfRewrittenImperialJusticeIsValidWhen701IsInFormat() throws DeckInvalidException {
        DefaultSwccgFormat format = occupancyFormat("2021-03-01", true);
        format.validateDeck(deck("200_109"));
        format.validateDeck(deck("701_109"));
        SwccgDeck rewritten = format.applyOccupancy(deck("200_109"));
        assertTrue(rewritten.getCards().contains("701_109"));
        assertFalse(rewritten.getCards().contains("200_109"));
    }

    @Test
    public void Date20210318KeepsCoarseAndOpenLikeWithout701AcceptsIt() throws DeckInvalidException {
        assertEquals("200_109", OccupancyRewriter.apply("200_109", LocalDate.parse("2021-03-18")));
        DefaultSwccgFormat format = occupancyFormat("2021-03-18", false);
        format.validateDeck(deck("200_109"));
        try {
            format.validateDeck(deck("701_109"));
            fail("Open-like accepted Imperial Justice 701_109");
        } catch (DeckInvalidException expected) {
            assertTrue(expected.getMessage(), expected.getMessage().contains("Imperial Justice"));
        }
    }

    @Test
    public void Date20240801BlankedClumsyIsNotALegalOpenCard() {
        assertEquals("213_45", OccupancyRewriter.apply("213_45", LocalDate.parse("2024-08-01")));
        DefaultSwccgFormat format = occupancyFormat("2024-08-01", false);
        try {
            format.validateDeck(lightDeck("213_45"));
            fail("blanked 213_45 was accepted as an Open card");
        } catch (DeckInvalidException expected) {
            // Left as 213_45 (no class). A rewrite to 702_213045 would name Clumsy in the set-ban error.
            assertFalse("Clumsy must not become a legal Open card after blanking: " + expected.getMessage(),
                    expected.getMessage() != null && expected.getMessage().contains("Clumsy"));
        }
        try {
            format.validateDeck(lightDeck("702_213045"));
            fail("Open-like accepted Clumsy 702_213045");
        } catch (DeckInvalidException expected) {
            assertTrue(expected.getMessage(), expected.getMessage().contains("Clumsy"));
        }
    }

    @Test
    public void CurrentOpenLeavesCoarseWithNoRewrite() throws DeckInvalidException {
        assertNull(formats.getFormat("open").getOccupancyDate());
        formats.getFormat("open").validateDeck(deck("200_109"));
        assertEquals("Coarse And Rough And Irritating", library.getSwccgoCardBlueprint("200_109").getTitle());
        SwccgDeck original = deck("200_109");
        assertSame(original, formats.getFormat("open").applyOccupancy(original));
        try {
            formats.getFormat("open").validateDeck(deck("701_109"));
            fail("Open accepted Imperial Justice 701_109");
        } catch (DeckInvalidException expected) {
            assertTrue(expected.getMessage(), expected.getMessage().contains("Imperial Justice"));
        }
    }

    @Test
    public void AnythingGoesDoesNotRewriteBothIdsRemain() throws DeckInvalidException {
        assertNull(formats.getFormat("anything_goes").getOccupancyDate());
        formats.getFormat("anything_goes").validateDeck(deck("200_109", "701_109"));
        SwccgDeck original = deck("200_109", "701_109");
        assertSame(original, formats.getFormat("anything_goes").applyOccupancy(original));
        assertEquals("200_109", OccupancyRewriter.apply("200_109", null));
        assertEquals("701_109", OccupancyRewriter.apply("701_109", null));
    }

    @Test
    public void CurrentOpenClassicJawaLegacyHaveNoOccupancyDate() {
        for (String code : new String[] {"open", "classic", "jawa", "legacy"})
            assertNull(code, formats.getFormat(code).getOccupancyDate());
    }

    @Test
    public void UntilDateIsInclusiveForNewOccupant() {
        assertEquals("701_109", OccupancyRewriter.apply("200_109", LocalDate.parse("2021-03-17")));
        assertEquals("200_109", OccupancyRewriter.apply("200_109", LocalDate.parse("2021-03-18")));
        assertEquals("702_213045", OccupancyRewriter.apply("213_45", LocalDate.parse("2024-07-21")));
        assertEquals("213_45", OccupancyRewriter.apply("213_45", LocalDate.parse("2024-07-22")));
    }

    @Test
    public void DeckLoadKeepsBlankedFormerIdForLaterRewrite() {
        SwccgDeck loaded = DeckSerialization.buildDeckFromContents("occupancy", "213_45,1_28|", library);
        assertTrue(loaded.getCards().contains("213_45"));
        SwccgDeck rewritten = OccupancyRewriter.applyDeck(loaded, LocalDate.parse("2024-07-01"));
        assertTrue(rewritten.getCards().contains("702_213045"));
        assertFalse(rewritten.getCards().contains("213_45"));
    }

    private static DefaultSwccgFormat occupancyFormat(String isoDate, boolean includeReplacedBlanked) {
        DefaultSwccgFormat format = new DefaultSwccgFormat(library, "Occupancy test " + isoDate,
                false, false, false, false);
        format.setCode("occupancy_test_" + isoDate.replace("-", ""));
        format.setOccupancyDate(LocalDate.parse(isoDate));
        for (Integer set : formats.getFormat("open").getValidSets())
            format.addValidSet(set);
        if (includeReplacedBlanked) {
            format.addValidSet(701);
            format.addValidSet(702);
        }
        return format;
    }

    private static SwccgDeck deck(String... cards) {
        return filledDeck("1_175", cards);
    }

    private static SwccgDeck lightDeck(String... cards) {
        return filledDeck("1_28", cards);
    }

    private static SwccgDeck filledDeck(String filler, String... cards) {
        SwccgDeck deck = new SwccgDeck("occupancy rewrite");
        for (String card : cards)
            deck.addCard(card);
        while (deck.getCards().size() < 60)
            deck.addCard(filler);
        return deck;
    }
}
