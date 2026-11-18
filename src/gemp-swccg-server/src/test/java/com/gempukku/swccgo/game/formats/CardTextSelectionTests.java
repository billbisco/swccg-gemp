package com.gempukku.swccgo.game.formats;

import com.gempukku.swccgo.game.DeckInvalidException;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.game.SwccgFormat;
import com.gempukku.swccgo.game.setup.CardPool;
import com.gempukku.swccgo.logic.vo.SwccgDeck;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class CardTextSelectionTests {
    private static SwccgCardBlueprintLibrary library;
    private static SwccgoFormatLibrary formats;

    @BeforeClass
    public static void setUpClass() {
        library = new SwccgCardBlueprintLibrary();
        formats = new SwccgoFormatLibrary(library);
    }

    @Test
    public void ProjectiveTelepathyHistoricalFormatSelectsOriginalWithoutChangingSavedDeck() throws Exception {
        SwccgFormat format = formats.getFormat("premiere_ds2");
        SwccgDeck saved = deck("5_149", 60);
        saved.setFormatIndex("saved-index");
        saved.setSourceCollection("default");
        format.validateDeck(saved);
        SwccgDeck resolved = format.resolveDeck(saved);
        assertEquals("5_149", saved.getCards().get(0));
        assertEquals("5_149o", resolved.getCards().get(0));
        assertEquals(saved.getCards().subList(1, 60), resolved.getCards().subList(1, 60));
        assertEquals("saved-index", resolved.getFormatIndex());
        assertEquals("default", resolved.getSourceCollection());
        assertEquals(saved.getDeckName(), resolved.getDeckName());
        assertSame(resolved, format.resolveDeck(resolved));
        format.validateDeck(resolved);
    }

    @Test
    public void ProjectiveTelepathySelectionPreservesModifiersAndOutsideDeckOrder() {
        SwccgDeck saved = deck("5_149^*", 60);
        saved.addCardOutsideDeck("5_149*");
        saved.addCardOutsideDeck("1_175");
        SwccgDeck resolved = formats.getFormat("premiere_ds2").resolveDeck(saved);
        assertEquals("5_149o^*", resolved.getCards().get(0));
        assertEquals(List.of("5_149o*", "1_175"), resolved.getCardsOutsideDeck());
        assertEquals(List.of("5_149*", "1_175"), saved.getCardsOutsideDeck());
    }

    @Test
    public void CurrentFormatsAndAnythingGoesKeepTheirExplicitTexts() throws Exception {
        for (String code : new String[]{"open", "legacy", "anything_goes"}) {
            SwccgDeck saved = deck("5_149", 60);
            assertSame(saved, formats.getFormat(code).resolveDeck(saved));
            formats.getFormat(code).validateDeck(saved);
        }
        SwccgDeck original = deck("5_149o", 60);
        assertSame(original, formats.getFormat("anything_goes").resolveDeck(original));
        formats.getFormat("anything_goes").validateDeck(original);
        assertThrows(DeckInvalidException.class, () -> formats.getFormat("open").validateDeck(original));
        assertThrows(DeckInvalidException.class,
                () -> formats.getFormat("premiere_ds2").validateDeck(deck("1_19o", 60)));
    }

    @Test
    public void UnknownCrossFamilyAndNonStateSelectionsFailBeforePlay() {
        for (Map<String, String> choice : List.of(Map.of("5_149", "5_149o9"),
                Map.of("5_149", "2_45o"), Map.of("5_149", "5_149"),
                Map.of("5_149o", "5_149o"), Map.of("5_149*", "5_149o"))) {
            assertThrows(IllegalArgumentException.class, () -> new CardTextSelection(library, choice));
        }
    }

    @Test
    public void ExistingAliasesResolveAndAnEmptySelectionIsANoOp() {
        CardTextSelection empty = new CardTextSelection(library, Map.of());
        SwccgDeck saved = deck("5_149", 60);
        assertSame(saved, empty.apply(saved));
        CardTextSelection choice = new CardTextSelection(library, Map.of("2_45", "2_45o"));
        assertEquals("2_45o", choice.resolve("2_45"));
        assertEquals("2_45d", choice.resolve("2_45d"));
        assertThrows(UnsupportedOperationException.class, () -> choice.choices().put("5_149", "5_149o"));
    }

    @Test
    public void HistoricalPoolShowsSelectedStateOnceAndAnythingGoesKeepsBoth() {
        var historical = formats.getFormat("premiere_ds2").getAllCardBlueprintsValidInFormat();
        var original = library.getSwccgoCardBlueprint("5_149o");
        assertFalse(historical.contains(library.getSwccgoCardBlueprint("5_149")));
        assertEquals(1L, historical.stream().filter(card -> card == original).count());
        var anything = formats.getFormat("anything_goes").getAllCardBlueprintsValidInFormat();
        assertTrue(anything.contains(original));
        assertTrue(anything.contains(library.getSwccgoCardBlueprint("5_149")));
    }

    @Test
    public void ChosenDeckSizeAndCustomPoolKeepTheSelectionAndFamilyBan() throws Exception {
        DefaultSwccgFormat base = (DefaultSwccgFormat) formats.getFormat("premiere_ds2");
        SwccgFormat sized = new DeckSizeSwccgFormat(base, 80, null, null);
        sized.validateDeck(deck("5_149", 80));
        assertEquals("5_149o", sized.resolveDeck(deck("5_149", 80)).getCards().get(0));
        for (String ban : List.of("5_149", "5_149o")) {
            SwccgFormat custom = new CustomPoolSwccgFormat(sized,
                    CardPool.of(null, List.of(), List.of(ban)), library);
            assertEquals("5_149o", custom.resolveCardId("5_149"));
            assertThrows(DeckInvalidException.class, () -> custom.validateDeck(deck("5_149", 80)));
        }
    }

    @Test
    public void SelectionChangesTheDeckFormatStampAndFamilyRestrictionStillApplies() {
        DefaultSwccgFormat format = new DefaultSwccgFormat(library, "selected", false, false, false, false);
        format.setCode("selected");
        format.addValidSet(1);
        format.addValidSet(5);
        String before = format.rulesStampSource();
        format.setCardTextOverrides(Map.of("5_149", "5_149o"));
        assertNotEquals(before, format.rulesStampSource());
        format.addRestrictedCard("5_149");
        SwccgDeck saved = deck("5_149", 59);
        saved.addCard("5_149o");
        assertThrows(DeckInvalidException.class, () -> format.validateDeck(saved));
    }

    private static SwccgDeck deck(String card, int size) {
        SwccgDeck result = new SwccgDeck("historical selection");
        result.addCard(card);
        while (result.getCards().size() < size)
            result.addCard("1_175");
        return result;
    }
}
