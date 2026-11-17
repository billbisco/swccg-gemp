package com.gempukku.swccgo.game.formats;

import com.gempukku.swccgo.game.DeckInvalidException;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.game.SwccgFormat;
import com.gempukku.swccgo.logic.vo.SwccgDeck;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Premiere - Virtual Block Worlds cutoffs. Refs swccg-guild/work 608.
 */
public class PeriodBlockFormatsTests {
    private static SwccgCardBlueprintLibrary library;
    private static SwccgoFormatLibrary formats;

    private static final List<String> WORLDS_CODES = Arrays.asList(
            "premiere_block_6_1",
            "premiere_block_7_1",
            "premiere_block_7_3",
            "premiere_block_8_2",
            "premiere_block_8_3",
            "premiere_block_9_1");
    private static final List<String> WORLDS_NAMES = Arrays.asList(
            "Premiere - Virtual Block 6.1",
            "Premiere - Virtual Block 7.1",
            "Premiere - Virtual Block 7.3",
            "Premiere - Virtual Block 8.2",
            "Premiere - Virtual Block 8.3",
            "Premiere - Virtual Block 9.1");

    @BeforeClass
    public static void setUpClass() {
        library = new SwccgCardBlueprintLibrary();
        formats = new SwccgoFormatLibrary(library);
    }

    @Test
    public void FormatNamesMatchWikiStrings() {
        for (int i = 0; i < WORLDS_CODES.size(); i++) {
            SwccgFormat format = formats.getFormat(WORLDS_CODES.get(i));
            assertNotNull(WORLDS_CODES.get(i), format);
            assertEquals(WORLDS_NAMES.get(i), format.getName());
            assertTrue(format.hasLoreHeroicRule());
        }
    }

    @Test
    public void HallListsWorldsFormatsAndKeepsOpenAndLegacy() {
        Map<String, SwccgFormat> hall = formats.getHallFormats();
        for (String code : WORLDS_CODES)
            assertTrue(code, hall.containsKey(code));
        assertTrue(hall.containsKey("open"));
        assertTrue(hall.containsKey("legacy"));
        assertEquals("Open", formats.getFormat("open").getName());
        assertEquals("Legacy", formats.getFormat("legacy").getName());
    }

    @Test
    public void PremiereBlock61Accepts1261AndRejects1271() throws Exception {
        formats.getFormat("premiere_block_6_1").validateDeck(deck("1261_1"));
        try {
            formats.getFormat("premiere_block_6_1").validateDeck(deck("1271_1"));
            fail("premiere_block_6_1 accepted 1271_1");
        } catch (DeckInvalidException expected) {
            assertTrue(expected.getMessage(),
                    expected.getMessage().contains("Grievous")
                            || expected.getMessage().toLowerCase().contains("set"));
        }
    }

    @Test
    public void PremiereBlock73Accepts1273AndRejects1282() throws Exception {
        formats.getFormat("premiere_block_7_3").validateDeck(deck("1273_2"));
        try {
            formats.getFormat("premiere_block_7_3").validateDeck(deck("1282_1"));
            fail("premiere_block_7_3 accepted 1282_1");
        } catch (DeckInvalidException expected) {
            assertTrue(expected.getMessage(),
                    expected.getMessage().contains("Chevin")
                            || expected.getMessage().toLowerCase().contains("set"));
        }
    }

    @Test
    public void PremiereBlock91RejectsHidingInTheGarbage() throws Exception {
        try {
            formats.getFormat("premiere_block_9_1").validateDeck(lightDeck("1220_124"));
            fail("premiere_block_9_1 accepted 1220_124");
        } catch (DeckInvalidException expected) {
            assertTrue(expected.getMessage(), expected.getMessage().contains("Hiding In The Garbage"));
        }
    }

    @Test
    public void LegacyRejectsHidingInTheGarbage() throws Exception {
        try {
            formats.getFormat("legacy").validateDeck(lightDeck("1220_124"));
            fail("legacy accepted 1220_124");
        } catch (DeckInvalidException expected) {
            assertTrue(expected.getMessage(), expected.getMessage().contains("Hiding In The Garbage"));
        }
    }

    private static SwccgDeck deck(String... cards) {
        return filledDeck("1_175", cards);
    }

    private static SwccgDeck lightDeck(String... cards) {
        return filledDeck("1_28", cards);
    }

    private static SwccgDeck filledDeck(String filler, String... cards) {
        SwccgDeck deck = new SwccgDeck("period block");
        for (String card : cards)
            deck.addCard(card);
        while (deck.getCards().size() < 60)
            deck.addCard(filler);
        return deck;
    }
}
