package com.gempukku.swccgo.game.formats;

import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.game.DeckInvalidException;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.logic.vo.SwccgDeck;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Virtual Modern replaced (701) and blanked (702) cards: Open copies the PC set list;
 * Anything Goes includes 701 and 702. Refs swccg-guild/work 600.
 */
public class ReplacedBlankedSetsTests {
    private static SwccgCardBlueprintLibrary library;
    private static SwccgoFormatLibrary formats;

    @BeforeClass
    public static void setUpClass() {
        library = new SwccgCardBlueprintLibrary();
        formats = new SwccgoFormatLibrary(library);
    }

    @Test
    public void ExpansionSetsExist() {
        assertEquals(701, ExpansionSet.VIRTUAL_MODERN_REPLACED.getSetNumber());
        assertEquals(702, ExpansionSet.VIRTUAL_MODERN_BLANKED.getSetNumber());
        assertEquals("Virtual Modern replaced", ExpansionSet.VIRTUAL_MODERN_REPLACED.getHumanReadable());
        assertEquals("Virtual Modern blanked", ExpansionSet.VIRTUAL_MODERN_BLANKED.getHumanReadable());
    }

    @Test
    public void ImperialJusticeReplacedLoadsAs701_109() {
        assertNotNull(library.getSwccgoCardBlueprint("701_109"));
        assertEquals("Imperial Justice", library.getSwccgoCardBlueprint("701_109").getTitle());
        assertEquals("Coarse And Rough And Irritating", library.getSwccgoCardBlueprint("200_109").getTitle());
    }

    @Test
    public void OpenRejectsReplacedIdAndKeepsCoarse() throws Exception {
        formats.getFormat("open").validateDeck(deck("200_109"));
        try {
            formats.getFormat("open").validateDeck(deck("701_109"));
            fail("Open accepted Imperial Justice 701_109");
        } catch (DeckInvalidException expected) {
            assertTrue(expected.getMessage(), expected.getMessage().contains("Imperial Justice"));
        }
    }

    @Test
    public void AnythingGoesAllowsReplacedAndCurrentTogether() throws DeckInvalidException {
        formats.getFormat("anything_goes").validateDeck(deck("701_109", "200_109"));
    }

    @Test
    public void RemainingReplacedCardsLoadWithoutStealingCurrentOccupants() {
        assertTrue(library.getSwccgoCardBlueprint("701_16").getTitle().contains("Jabba's Prize"));
        assertNotNull(library.getSwccgoCardBlueprint("200_16"));
        assertFalse(library.getSwccgoCardBlueprint("701_16").getTitle().contains("Mythrol"));
        assertEquals("Obi-Wan In Radiant VII", library.getSwccgoCardBlueprint("701_63").getTitle());
        assertEquals("Madakor In Radiant VII", library.getSwccgoCardBlueprint("200_63").getTitle());
        assertTrue(library.getSwccgoCardBlueprint("701_90").getTitle().contains("Sith Probe Droid"));
        assertEquals("DRK-1 (Dark Eye Probe Droid)", library.getSwccgoCardBlueprint("200_90").getTitle());
    }

    @Test
    public void ClumsyBlankedLoadsAs702_213045() {
        assertNotNull(library.getSwccgoCardBlueprint("702_213045"));
        assertEquals("Clumsy And Stupid", library.getSwccgoCardBlueprint("702_213045").getTitle());
    }

    @Test
    public void OpenRejectsClumsy702AndAnythingGoesAllowsIt() throws Exception {
        formats.getFormat("anything_goes").validateDeck(lightDeck("702_213045"));
        try {
            formats.getFormat("open").validateDeck(lightDeck("702_213045"));
            fail("Open accepted Clumsy 702_213045");
        } catch (DeckInvalidException expected) {
            assertTrue(expected.getMessage(), expected.getMessage().contains("Clumsy"));
        }
    }

    @Test
    public void AnythingGoesSetListIncludes701And702AndOpenDoesNot() throws Exception {
        JSONParser parser = new JSONParser();
        try (InputStreamReader reader = new InputStreamReader(
                SwccgoFormatLibrary.class.getResourceAsStream("/swccgFormats.json"), StandardCharsets.UTF_8)) {
            JSONArray all = (JSONArray) parser.parse(reader);
            JSONArray ag = null;
            JSONArray open = null;
            for (Object o : all) {
                JSONObject f = (JSONObject) o;
                if ("anything_goes".equals(f.get("code")))
                    ag = (JSONArray) f.get("set");
                if ("open".equals(f.get("code")))
                    open = (JSONArray) f.get("set");
            }
            assertNotNull(ag);
            assertNotNull(open);
            assertTrue(ag.contains(701L) || ag.contains(701));
            assertTrue(ag.contains(702L) || ag.contains(702));
            assertFalse(open.contains(701L) || open.contains(701));
            assertFalse(open.contains(702L) || open.contains(702));
        }
    }

    private static SwccgDeck deck(String... cards) {
        return filledDeck("1_175", cards);
    }

    private static SwccgDeck lightDeck(String... cards) {
        return filledDeck("1_28", cards);
    }

    private static SwccgDeck filledDeck(String filler, String... cards) {
        SwccgDeck deck = new SwccgDeck("replaced blanked");
        for (String card : cards)
            deck.addCard(card);
        while (deck.getCards().size() < 60)
            deck.addCard(filler);
        return deck;
    }
}
