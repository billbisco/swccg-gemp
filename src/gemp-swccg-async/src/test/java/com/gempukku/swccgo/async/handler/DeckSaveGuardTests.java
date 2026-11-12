package com.gempukku.swccgo.async.handler;

import com.gempukku.swccgo.async.HttpProcessingException;
import com.gempukku.swccgo.logic.vo.SwccgDeck;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** work#221: save rules for the join screen's deck editor (save as new, checked overwrite). */
public class DeckSaveGuardTests {

    private static SwccgDeck deck(String name, String[] cards, String[] outside) {
        SwccgDeck deck = new SwccgDeck(name);
        for (String card : cards)
            deck.addCard(card);
        for (String card : outside)
            deck.addCardOutsideDeck(card);
        return deck;
    }

    private static final SwccgDeck ORIGINAL = deck("Hunt Down", new String[]{"1_1", "1_2", "1_2"}, new String[]{"200_16"});

    @Test
    public void RevisionFollowsContentsNotName() {
        //test1: same cards give the same revision, whatever the deck is called
        SwccgDeck sameCards = deck("Copy of Hunt Down", new String[]{"1_1", "1_2", "1_2"}, new String[]{"200_16"});
        assertEquals(DeckSaveGuard.revisionOf(ORIGINAL), DeckSaveGuard.revisionOf(sameCards));
        assertEquals(16, DeckSaveGuard.revisionOf(ORIGINAL).length());
        //test2: adding, removing or moving a card between zones changes it
        assertNotEquals(DeckSaveGuard.revisionOf(ORIGINAL),
                DeckSaveGuard.revisionOf(deck("x", new String[]{"1_1", "1_2"}, new String[]{"200_16"})));
        assertNotEquals(DeckSaveGuard.revisionOf(ORIGINAL),
                DeckSaveGuard.revisionOf(deck("x", new String[]{"1_1", "1_2", "1_2", "200_16"}, new String[]{})));
        assertNotEquals(DeckSaveGuard.revisionOf(ORIGINAL),
                DeckSaveGuard.revisionOf(deck("x", new String[]{"1_1", "1_2"}, new String[]{"200_16", "1_2"})));
        //test3: no deck has an empty revision
        assertEquals("", DeckSaveGuard.revisionOf(null));
    }

    @Test
    public void SaveAsNewNeverReplacesAnExistingDeck() {
        //test4: the name is taken: refused with a message, so the original stays as it was
        HttpProcessingException e = assertThrows(HttpProcessingException.class,
                () -> DeckSaveGuard.check(ORIGINAL, true, null));
        assertEquals(409, e.getStatus());
        assertTrue(e.getMessage().contains("different name"));
        //test5: a free name is allowed (and the revision is ignored for a new deck)
        assertDoesNotThrow(() -> DeckSaveGuard.check(null, true, "abc"));
    }

    @Test
    public void CheckedOverwriteIsRefusedWhenTheStoredDeckIsNewer() {
        String startedFrom = DeckSaveGuard.revisionOf(ORIGINAL);
        //test6: unchanged since the editor opened: allowed
        assertDoesNotThrow(() -> DeckSaveGuard.check(ORIGINAL, false, startedFrom));
        //test7: saved elsewhere since: refused, not silently overwritten
        SwccgDeck newer = deck("Hunt Down", new String[]{"1_1", "1_3"}, new String[]{"200_16"});
        HttpProcessingException e = assertThrows(HttpProcessingException.class,
                () -> DeckSaveGuard.check(newer, false, startedFrom));
        assertEquals(409, e.getStatus());
        assertTrue(e.getMessage().contains("changed and saved elsewhere"));
        //test8: deleted or renamed since: refused
        assertEquals(409, assertThrows(HttpProcessingException.class,
                () -> DeckSaveGuard.check(null, false, startedFrom)).getStatus());
        //test9: the standalone Deckbuilder sends neither parameter and saves as before
        assertDoesNotThrow(() -> DeckSaveGuard.check(newer, false, null));
        assertDoesNotThrow(() -> DeckSaveGuard.check(null, false, ""));
    }
}
