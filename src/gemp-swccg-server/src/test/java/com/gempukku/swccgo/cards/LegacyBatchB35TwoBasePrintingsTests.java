package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.collection.CollectionsManager;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.game.DeckInvalidException;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.game.formats.SwccgoFormatLibrary;
import com.gempukku.swccgo.logic.GameUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Batch B35 (Bill 2026-11-09): a card printed on two base cards is two cards. Each printing has its own blueprint id,
 * image and display title; the second printing extends the first, so it plays the same and is the same card for
 * uniqueness and deck limits. The first printing keeps the id it already had, so existing decks don't change.
 */
public class LegacyBatchB35TwoBasePrintingsTests {
    /** first id, second id, rules title, first printed full name, second printed full name, second image */
    private static final String[][] MODERN = {
            {"200_95", "200_147", "Fanfare", "Fanfare (Reflections III) (V)", "Fanfare (Tatooine) (V)", "ResetDS-Dark/hires/fanfaretatooine.png"},
            {"200_100", "200_148", "You Cannot Hide Forever", "You Cannot Hide Forever (Reflections III) (V)", "You Cannot Hide Forever (Death Star II) (V)", "ResetDS-Dark/hires/youcannothideforeverv.png"},
            {"200_26", "200_149", "Don't Do That Again", "Don't Do That Again (Reflections III) (V)", "Don't Do That Again (Tatooine) (V)", "ResetDS-Light/hires/dontdothatagaintatooine.png"},
            {"200_32", "200_150", "Your Insight Serves You Well", "Your Insight Serves You Well (Reflections III) (V)", "Your Insight Serves You Well (Death Star II) (V)", "ResetDS-Light/hires/yourinsightservesyouwellv.png"},
            {"216_5", "216_50", "Crossfire", "Crossfire (Reflections III) (V)", "Crossfire (Endor) (V)", "Virtual16-Dark/hires/crossfirev.png"},
            {"225_3", "225_60", "Come Here You Big Coward", "Come Here You Big Coward (Reflections III) (V)", "Come Here You Big Coward (Special Edition) (V)", "Virtual25-Dark/hires/comehereyoubigcowardv.png"},
            {"225_51", "225_61", "Let's Keep A Little Optimism Here", "Let's Keep A Little Optimism Here (Reflections III) (V)", "Let's Keep A Little Optimism Here (Death Star II) (V)", "Virtual25-Light/hires/letskeepalittleoptimismherev.png"},
    };
    /** Modern first printings now show the PC's own (ResetDS) image, as in swccg-card-json */
    private static final String[][] MODERN_FIRST_IMAGES = {
            {"200_95", "ResetDS-Dark/hires/fanfare.png"},
            {"200_100", "ResetDS-Dark/hires/youcannothideforever.png"},
            {"200_26", "ResetDS-Light/hires/dontdothatagain.png"},
            {"200_32", "ResetDS-Light/hires/yourinsightservesyouwell.png"},
    };
    /** first id (class that plays it), second id, first printed full name, second printed full name */
    private static final String[][] LEGACY = {
            {"601_35", "1272_9", "Fanfare (V)", "Fanfare (V)"},
            {"1200_8", "1272_21", "Don't Do That Again (V)", "Don't Do That Again (V)"},
            {"1261_27", "1261_28", "You Cannot Hide Forever (Ref III) (V)", "You Cannot Hide Forever (Death Star II) (V)"},
            {"1261_73", "1261_75", "Your Insight Serves You Well (Ref III) (V)", "Your Insight Serves You Well (Death Star II) (V)"},
            {"1261_77", "1261_76", "Let's Keep A Little Optimism Here (Ref III) (V)", "Let's Keep A Little Optimism Here (Death Star II) (V)"},
    };

    private static SwccgCardBlueprintLibrary library;
    private static CollectionsManager collections;
    private static SwccgoFormatLibrary formats;
    private static String images;

    @BeforeClass
    public static void load() throws Exception {
        library = new SwccgCardBlueprintLibrary();
        collections = new CollectionsManager(null, null, null, library);
        formats = new SwccgoFormatLibrary(library);
        images = Files.readString(Path.of("../gemp-swccg-async/src/main/web/js/gemp-016/cards/CardImages.js"));
    }

    private static void assertSameCardOwnPrinting(String first, String second) {
        SwccgCardBlueprint a = library.getSwccgoCardBlueprint(first);
        SwccgCardBlueprint b = library.getSwccgoCardBlueprint(second);
        assertNotNull(first, a);
        assertNotNull(second, b);
        assertEquals(second, library.getBaseBlueprintId(second));
        assertNotEquals(a.getClass(), b.getClass());
        assertTrue(second + " extends " + first, a.getClass().isAssignableFrom(b.getClass()));
        assertEquals(a.getTitle(), b.getTitle());
        assertEquals(a.getGameText(), b.getGameText());
        assertEquals(a.getLore(), b.getLore());
        assertEquals(a.getSide(), b.getSide());
        assertEquals(a.getUniqueness(), b.getUniqueness());
        assertEquals(a.getCardCategory(), b.getCardCategory());
        assertEquals(a.getDestiny(), b.getDestiny());
    }

    @Test
    public void ModernSecondPrintingsAreTheirOwnCardsWithTheSameRules() {
        for (String[] p : MODERN) {
            assertSameCardOwnPrinting(p[0], p[1]);
            assertEquals(p[0], library.getBaseBlueprintId(p[0]));
            assertEquals(p[2], library.getSwccgoCardBlueprint(p[0]).getTitle());
        }
    }

    @Test
    public void ModernPrintingsShowTheirDisplayTitlesWithTheirBase() {
        for (String[] p : MODERN) {
            assertEquals(p[3], GameUtils.getFullName(library.getSwccgoCardBlueprint(p[0])));
            assertEquals(p[4], GameUtils.getFullName(library.getSwccgoCardBlueprint(p[1])));
            // rules still match by the plain title
            assertEquals(p[2] + " (V)", GameUtils.getRulesFullName(library.getSwccgoCardBlueprint(p[1])));
        }
    }

    @Test
    public void ModernSecondPrintingsKeepTheReflectionsIIIIconAsPrinted() {
        for (String[] p : MODERN) {
            assertTrue(p[1], library.getSwccgoCardBlueprint(p[1]).hasIcon(Icon.REFLECTIONS_III));
        }
    }

    @Test
    public void ModernPrintingsHaveTheirOwnImages() {
        for (String[] p : MODERN) {
            assertTrue(p[1], images.contains("\"" + p[1] + "\":\"https://res.starwarsccg.org/cards/" + p[5] + "\""));
        }
        for (String[] p : MODERN_FIRST_IMAGES) {
            assertTrue(p[0], images.contains("\"" + p[0] + "\":\"https://res.starwarsccg.org/cards/" + p[1] + "\""));
        }
    }

    @Test
    public void ModernSecondPrintingsAreInTheDeckBuilderAndValidInOpenFormats() throws DeckInvalidException {
        for (String[] p : MODERN) {
            assertEquals(p[1], 60, collections.getDefaultCollection(false).getItemCount(p[1]));
            assertEquals(p[0], 60, collections.getDefaultCollection(false).getItemCount(p[0]));
            formats.getFormat("open").validateCard(p[1], false);
        }
    }

    @Test
    public void FormatListsNamingAFirstPrintingAlsoNameItsSecondPrinting() throws DeckInvalidException {
        // Premiere - Virtual Set 3 lists Crossfire (V) and Come Here You Big Coward (V) by id
        formats.getFormat("premiere_set3").validateCard("216_50", false);
        formats.getFormat("premiere_set3").validateCard("225_60", false);
        // Limited Resources bans the Set 0 shields by id
        for (String id : new String[]{"200_147", "200_148", "200_149", "200_150"}) {
            try {
                formats.getFormat("limited_resources").validateCard(id, false);
                org.junit.Assert.fail(id + " should be banned like its first printing");
            } catch (DeckInvalidException expected) {
                // banned, as its first printing
            }
        }
    }

    @Test
    public void LegacySecondPrintingsAreTheirOwnCardsNotAliases() {
        for (String[] p : LEGACY) {
            assertSameCardOwnPrinting(p[0], p[1]);
            assertEquals(p[2], GameUtils.getFullName(library.getSwccgoCardBlueprint(p[0])));
            assertEquals(p[3], GameUtils.getFullName(library.getSwccgoCardBlueprint(p[1])));
            assertEquals(60, collections.getDefaultCollection(false).getItemCount(p[1]));
            assertTrue(p[1], images.contains("\"" + p[1] + "\":\"https://res.starwarsccg.org/cards/legacy/"));
        }
        // the Virtual Shields Fanfare and Don't Do That Again ids still play their existing cards
        assertEquals("601_35", library.getBaseBlueprintId("1200_5"));
        assertEquals("1200_8", library.getBaseBlueprintId("601_133"));
    }

    @Test
    public void LegacySecondPrintingsCarryTheirBaseCardsIcons() {
        assertTrue(library.getSwccgoCardBlueprint("1272_9").hasIcon(Icon.TATOOINE));
        assertFalse(library.getSwccgoCardBlueprint("1272_9").hasIcon(Icon.REFLECTIONS_III));
        assertTrue(library.getSwccgoCardBlueprint("601_35").hasIcon(Icon.REFLECTIONS_III));
        assertTrue(library.getSwccgoCardBlueprint("1272_21").hasIcon(Icon.TATOOINE));
        assertFalse(library.getSwccgoCardBlueprint("1200_8").hasIcon(Icon.TATOOINE));
        assertTrue(library.getSwccgoCardBlueprint("1261_75").hasIcon(Icon.DEATH_STAR_II));
        assertFalse(library.getSwccgoCardBlueprint("1261_75").hasIcon(Icon.REFLECTIONS_III));
        // the Death Star II slips of You Cannot Hide Forever and Let's Keep A Little Optimism Here print the Reflections III icon
        assertTrue(library.getSwccgoCardBlueprint("1261_28").hasIcon(Icon.REFLECTIONS_III));
        assertTrue(library.getSwccgoCardBlueprint("1261_76").hasIcon(Icon.REFLECTIONS_III));
    }
}
