package com.gempukku.swccgo.game.formats;

import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.DeckInvalidException;
import com.gempukku.swccgo.game.DeckSize;
import com.gempukku.swccgo.game.SwccgFormat;
import com.gempukku.swccgo.guest.GuestDecks;
import com.gempukku.swccgo.guest.PlayNowSide;
import com.gempukku.swccgo.logic.vo.SwccgDeck;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/** swccg-guild/work#90: deck size is a setup setting, separate from the format. */
public class DeckSizeFormatTests {
    private static final SwccgoFormatLibrary LIBRARY = VirtualTableScenario._formatLibrary;

    /** The 60-card guest Light deck cut down, or padded with extra copies of a card it already runs twice. */
    static SwccgDeck deckOf(int size) {
        SwccgDeck full = GuestDecks.deck(PlayNowSide.LIGHT);
        List<String> cards = new ArrayList<>(full.getCards());
        Map<String, Integer> counts = new HashMap<>();
        for (String card : cards)
            counts.merge(card, 1, Integer::sum);
        String repeat = counts.entrySet().stream().filter(e -> e.getValue() >= 2).findFirst().orElseThrow().getKey();
        while (cards.size() > size)
            cards.remove(cards.size() - 1);
        while (cards.size() < size)
            cards.add(repeat);
        SwccgDeck deck = new SwccgDeck("deck" + size);
        for (String card : cards)
            deck.addCard(card);
        for (String card : full.getCardsOutsideDeck())
            deck.addCardOutsideDeck(card);
        return deck;
    }

    private static boolean valid(SwccgFormat format, SwccgDeck deck) {
        try {
            format.validateDeck(deck);
            return true;
        } catch (DeckInvalidException exp) {
            return false;
        }
    }

    @Test
    public void DeckSizeChosenSizeAcceptsOnlyExactlyThatSize() {
        SwccgFormat open = LIBRARY.getFormat("open");
        assertTrue("precondition: the 60-card guest deck is legal in Open", valid(open, deckOf(60)));
        for (int size : new int[] {DeckSize.MIN_SUPPORTED, 45, 75, DeckSize.MAX_SUPPORTED}) {
            SwccgFormat sized = LIBRARY.getFormat("open@" + size);
            assertNotNull(sized);
            assertEquals(size, sized.getRequiredDeckSize());
            assertEquals(size, sized.getMinimumDeckSize());
            assertEquals(size, sized.getMaximumDeckSize());
            assertFalse("one under " + size, valid(sized, deckOf(size - 1)));
            assertTrue("exactly " + size, valid(sized, deckOf(size)));
            assertFalse("one over " + size, valid(sized, deckOf(size + 1)));
        }
    }

    @Test
    public void DeckSizeChosenSizeKeepsTheFormatsCardPoolAndRules() {
        SwccgFormat base = LIBRARY.getFormat("open_no_virtual");
        SwccgFormat sized = LIBRARY.getFormat("open_no_virtual@45");
        assertEquals("the deck-list key", "open_no_virtual@45", sized.getCode());
        assertEquals("open_no_virtual", sized.getBaseCode());
        assertTrue(sized.isDeckSizeChosen());
        assertFalse(base.isDeckSizeChosen());
        assertEquals("the format name never changes (#114)", base.getName(), sized.getName());
        assertEquals(base.getValidSets(), sized.getValidSets());
        assertEquals(base.getBannedCards(), sized.getBannedCards());
        assertEquals(base.getRestrictedCards(), sized.getRestrictedCards());
        assertEquals(base.hasLoreHeroicRule(), sized.hasLoreHeroicRule());
        assertEquals(base.supportsStartingCredits(), sized.supportsStartingCredits());
        assertEquals(base.getDefaultGameTimerMinutes(), sized.getDefaultGameTimerMinutes());
        // A card outside the pool is still rejected at the chosen size
        assertEquals(valid(base, deckOf(60)), valid(LIBRARY.getFormat("open_no_virtual@60"), deckOf(60)));
    }

    @Test
    public void DeckSizeWorksForEveryFormat() {
        for (SwccgFormat format : LIBRARY.getAllFormats().values()) {
            SwccgFormat sized = LIBRARY.getFormat(format.getCode() + "@" + DeckSize.MAX_SUPPORTED);
            assertNotNull("deck size is generic: " + format.getCode(), sized);
            assertEquals(DeckSize.MAX_SUPPORTED, sized.getRequiredDeckSize());
            assertEquals(format.getCode(), sized.getBaseCode());
            assertSame("resolving a chosen size is cached", sized, LIBRARY.getFormat(sized.getCode()));
            assertEquals(format.getName(), sized.getName());
            assertTrue("fits tournament.format: " + sized.getCode(), sized.getCode().length() <= 45);
        }
    }

    @Test
    public void DeckSizeLeftAtTheDefaultIsTheFormatItself() {
        SwccgFormat open = LIBRARY.getFormat("open");
        assertSame(open, LIBRARY.getFormat("open@60"));
        assertEquals("open", LIBRARY.codeWithDeckSize("open", 60));
        assertEquals("open", LIBRARY.codeWithDeckSize("open", null));
        assertEquals("open@45", LIBRARY.codeWithDeckSize("open", 45));
        assertEquals("open@45", LIBRARY.codeWithDeckSize("open@100", 45));
        // A range format (Anything Goes, 40-60) at a chosen size becomes exactly that size
        SwccgFormat anythingGoes = LIBRARY.getFormat("anything_goes");
        assertEquals(40, anythingGoes.getMinimumDeckSize());
        assertEquals(60, anythingGoes.getMaximumDeckSize());
        assertTrue(LIBRARY.getFormat("anything_goes@60").isDeckSizeChosen());
        assertEquals(60, LIBRARY.getFormat("anything_goes@60").getMinimumDeckSize());
        // The stamp of the deck-format index changes with the size, so cached results don't carry over
        assertNotEquals(LIBRARY.stampFor("open"), LIBRARY.stampFor("open@45"));
        assertEquals(LIBRARY.stampFor("open"), LIBRARY.stampFor("open@60"));
        assertEquals(16, LIBRARY.stampFor("open@45").length());
    }

    @Test
    public void DeckSizeRangeComesFromOnePlace() throws Exception {
        assertNotNull(LIBRARY.getFormat("open@" + DeckSize.MIN_SUPPORTED));
        assertNotNull(LIBRARY.getFormat("open@" + DeckSize.MAX_SUPPORTED));
        assertNull(LIBRARY.getFormat("open@" + (DeckSize.MIN_SUPPORTED - 1)));
        assertNull(LIBRARY.getFormat("open@" + (DeckSize.MAX_SUPPORTED + 1)));
        assertNull(LIBRARY.getFormat("open@abc"));
        assertNull(LIBRARY.getFormat("open@"));
        assertNull(LIBRARY.getFormat("no_such_format@60"));
        assertThrows(IllegalArgumentException.class, () -> LIBRARY.codeWithDeckSize("open", DeckSize.MAX_SUPPORTED + 1));
        assertEquals(Integer.valueOf(DeckSize.MAX_SUPPORTED), DeckSize.parse(String.valueOf(DeckSize.MAX_SUPPORTED)));
        assertNull(DeckSize.parse(""));
        for (String bad : new String[] {String.valueOf(DeckSize.MIN_SUPPORTED - 1), String.valueOf(DeckSize.MAX_SUPPORTED + 1), "4.5", "-50", "lots"})
            assertThrows(IllegalArgumentException.class, () -> DeckSize.parse(bad));

        // The setup screens read the range the server publishes; they don't hard-code it
        String hallUi = readWeb("js/gemp-016/hallUi.js");
        assertTrue(hallUi.contains("minSupportedDeckSize"));
        assertTrue(hallUi.contains("maxSupportedDeckSize"));
        String leagueAdmin = readWeb("includes/admin/leagueAdmin.html");
        assertTrue(leagueAdmin.contains("maxSupportedDeckSize"));
    }

    @Test
    public void DeckSizeOldSizeOnlyFormatsLoadAsOpenAtThatSize() {
        Map<String, Integer> retired = Map.of("open40card", 40, "open_45_card", 45, "open_100_card", 100);
        for (Map.Entry<String, Integer> entry : retired.entrySet()) {
            SwccgFormat format = LIBRARY.getFormat(entry.getKey());
            assertNotNull(entry.getKey() + " still loads", format);
            assertEquals("open", format.getBaseCode());
            assertEquals("open@" + entry.getValue(), format.getCode());
            assertEquals("Open", format.getName());
            assertEquals((int) entry.getValue(), format.getRequiredDeckSize());
            assertSame(LIBRARY.getFormat("open@" + entry.getValue()), format);
            assertTrue(valid(format, deckOf(entry.getValue())));
            assertFalse(valid(format, deckOf(60)));
            assertNotNull(LIBRARY.stampFor(entry.getKey()));
            assertFalse("no longer a separate format", LIBRARY.getAllFormats().containsKey(entry.getKey()));
            assertFalse("no longer offered in the hall", LIBRARY.getHallFormats().containsKey(entry.getKey()));
        }
        // Open at 100 keeps the 100-card format's longer timer and tenets
        SwccgFormat hundred = LIBRARY.getFormat("open_100_card");
        assertEquals(90, hundred.getDefaultGameTimerMinutes());
        assertEquals("https://www.starwarsccg.org/100card/", hundred.getTenetsLink());
        assertEquals(60, LIBRARY.getFormat("open@45").getDefaultGameTimerMinutes());
    }

    @Test
    public void DeckSizeHallFormatOnlyAcceptsHallBases() {
        assertNotNull(LIBRARY.getHallFormat("open@100"));
        assertNotNull(LIBRARY.getHallFormat("open"));
        // Cube is not a hall format, so neither is Cube at a chosen size
        assertFalse(LIBRARY.getHallFormats().containsKey("cube"));
        assertNull(LIBRARY.getHallFormat("cube@60"));
        assertNotNull(LIBRARY.getFormat("cube@60"));
    }

    @Test
    public void DeckSizeCodesFitTheDeckFormatIndex() {
        String stamp = LIBRARY.stampFor("open@45");
        String encoded = DeckFormatIndex.upsert(null, "open@45", stamp, true);
        assertTrue(DeckFormatIndex.isFreshPass(encoded, "open@45", stamp));
        assertFalse(DeckFormatIndex.isFreshPass(encoded, "open", LIBRARY.stampFor("open")));
    }

    private static String readWeb(String path) throws Exception {
        Path web = Paths.get("..", "gemp-swccg-async", "src", "main", "web", path);
        return Files.readString(web);
    }
}
