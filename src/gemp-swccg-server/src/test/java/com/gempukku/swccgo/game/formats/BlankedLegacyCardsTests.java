package com.gempukku.swccgo.game.formats;

import com.gempukku.swccgo.game.DeckInvalidException;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.logic.vo.SwccgDeck;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Blanked leftover cards that never had a PC 601/501 id. Appended on 1210/1230/1261
 * (and the Set D JPSD Ounee Ta twin on 702). Refs swccg-guild/work 606.
 */
public class BlankedLegacyCardsTests {
    private static SwccgCardBlueprintLibrary library;
    private static SwccgoFormatLibrary formats;

    @BeforeClass
    public static void setUpClass() {
        library = new SwccgCardBlueprintLibrary();
        formats = new SwccgoFormatLibrary(library);
    }

    @Test
    public void MercSunletLoadsAs1210_175() {
        assertNotNull(library.getSwccgoCardBlueprint("1210_175"));
        assertEquals("Merc Sunlet", library.getSwccgoCardBlueprint("1210_175").getTitle());
        assertTrue(library.getSwccgoCardBlueprint("1210_175").hasVirtualSuffix());
    }

    @Test
    public void MediumTransportLoadsAs1230_119() {
        assertNotNull(library.getSwccgoCardBlueprint("1230_119"));
        assertEquals("Medium Transport", library.getSwccgoCardBlueprint("1230_119").getTitle());
        assertTrue(library.getSwccgoCardBlueprint("1230_119").hasVirtualSuffix());
    }

    @Test
    public void CaptainVerrackBlock3LoadsAs1230_120DistinctFrom1282_15() {
        assertNotNull(library.getSwccgoCardBlueprint("1230_120"));
        assertEquals("Captain Verrack", library.getSwccgoCardBlueprint("1230_120").getTitle());
        assertTrue(library.getSwccgoCardBlueprint("1230_120").hasVirtualSuffix());
        assertNotEquals(library.getSwccgoCardBlueprint("1230_120").getGameText(),
                library.getSwccgoCardBlueprint("1282_15").getGameText());
        assertNotEquals("1230_120", library.getBaseBlueprintId("1282_15"));
    }

    @Test
    public void WipeThemOutLoadsRefIIIAndCoruscantAlias() {
        assertNotNull(library.getSwccgoCardBlueprint("1261_115"));
        assertEquals("Wipe Them Out, All Of Them", library.getSwccgoCardBlueprint("1261_115").getTitle());
        assertTrue(library.getSwccgoCardBlueprint("1261_115").hasVirtualSuffix());
        assertNotNull(library.getSwccgoCardBlueprint("1261_116"));
        assertEquals("Wipe Them Out, All Of Them", library.getSwccgoCardBlueprint("1261_116").getTitle());
        assertEquals("1261_115", library.getBaseBlueprintId("1261_116"));
    }

    @Test
    public void AnotherPatheticLifeformLoadsRefIIIAndCoruscantAlias() {
        assertNotNull(library.getSwccgoCardBlueprint("1261_117"));
        assertEquals("Another Pathetic Lifeform", library.getSwccgoCardBlueprint("1261_117").getTitle());
        assertTrue(library.getSwccgoCardBlueprint("1261_117").hasVirtualSuffix());
        assertNotNull(library.getSwccgoCardBlueprint("1261_118"));
        assertEquals("Another Pathetic Lifeform", library.getSwccgoCardBlueprint("1261_118").getTitle());
        assertEquals("1261_117", library.getBaseBlueprintId("1261_118"));
    }

    @Test
    public void OuneeTaJpsdTwinLoadsAs702_217044() {
        assertNotNull(library.getSwccgoCardBlueprint("702_217044"));
        assertEquals("Ounee Ta", library.getSwccgoCardBlueprint("702_217044").getTitle());
        assertTrue(library.getSwccgoCardBlueprint("702_217044").hasVirtualSuffix());
        assertEquals("Ounee Ta (Jabba's Palace Sealed Deck)", library.getSwccgoCardBlueprint("702_217044").getDisplayTitle());
        assertEquals("Ounee Ta (Reflections III)", library.getSwccgoCardBlueprint("702_217043").getDisplayTitle());
    }

    @Test
    public void HidingInTheGarbageLegacyRejects1220_124() throws Exception {
        try {
            formats.getFormat("legacy").validateDeck(lightDeck("1220_124"));
            fail("Legacy accepted Hiding In The Garbage 1220_124");
        } catch (DeckInvalidException expected) {
            assertTrue(expected.getMessage(), expected.getMessage().contains("Hiding In The Garbage"));
        }
    }

    @Test
    public void MediumTransportLegacyRejects1230_119() throws Exception {
        try {
            formats.getFormat("legacy").validateDeck(lightDeck("1230_119"));
            fail("Legacy accepted Medium Transport 1230_119");
        } catch (DeckInvalidException expected) {
            assertTrue(expected.getMessage(), expected.getMessage().contains("Medium Transport"));
        }
    }

    @Test
    public void MercSunletAnythingGoesAcceptsLight60With1210_175() throws DeckInvalidException {
        formats.getFormat("anything_goes").validateDeck(lightDeck("1210_175"));
    }

    @Test
    public void WipeThemOutAnythingGoesAcceptsDark60With1261_115AfterPipe() throws DeckInvalidException {
        SwccgDeck deck = filledDeck("1_175");
        deck.addCardOutsideDeck("1261_115");
        formats.getFormat("anything_goes").validateDeck(deck);
    }

    @Test
    public void OpenDoesNotIncludeAppendedBlankedIds() throws Exception {
        try {
            formats.getFormat("open").validateDeck(lightDeck("1210_175"));
            fail("Open accepted Merc Sunlet 1210_175");
        } catch (DeckInvalidException expected) {
            assertTrue(expected.getMessage(), expected.getMessage().contains("Merc Sunlet"));
        }
        SwccgDeck jpsd = filledDeck("1_28");
        jpsd.addCardOutsideDeck("702_217044");
        try {
            formats.getFormat("open").validateDeck(jpsd);
            fail("Open accepted Ounee Ta 702_217044");
        } catch (DeckInvalidException expected) {
            assertTrue(expected.getMessage(), expected.getMessage().contains("Ounee Ta"));
        }
    }

    @Test
    public void YouWillGoToTheDagobahSystemStaysBannedOnLegacy() throws Exception {
        try {
            formats.getFormat("legacy").validateDeck(lightDeck("1220_164"));
            fail("Legacy accepted You Will Go To The Dagobah System 1220_164");
        } catch (DeckInvalidException expected) {
            assertTrue(expected.getMessage(), expected.getMessage().contains("You Will Go To The Dagobah System"));
        }
    }

    private static SwccgDeck lightDeck(String... cards) {
        return filledDeck("1_28", cards);
    }

    private static SwccgDeck filledDeck(String filler, String... cards) {
        SwccgDeck deck = new SwccgDeck("blanked leftover");
        for (String card : cards)
            deck.addCard(card);
        while (deck.getCards().size() < 60)
            deck.addCard(filler);
        return deck;
    }
}
