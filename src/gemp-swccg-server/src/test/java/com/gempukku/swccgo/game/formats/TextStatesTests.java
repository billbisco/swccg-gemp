package com.gempukku.swccgo.game.formats;

import com.gempukku.swccgo.collection.CollectionsManager;
import com.gempukku.swccgo.collection.TextStateCardPool;
import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.game.DeckInvalidException;
import com.gempukku.swccgo.game.SortAndFilterCards;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.logic.vo.SwccgDeck;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Older text states of one printing (docs/legacy-versions.md, "Text states"): ids such as 2_45o (as printed) and
 * 2_45d (Decipher's last word) load their own class, are the same card as the bare printing, and are legal in
 * Anything Goes only.
 */
public class TextStatesTests {
    /**
     * Printed text states whose errata only made the card unique (•): the printed card is non-unique (Bill 2026-11-12,
     * ai-team-skills RULINGS.md "printed-text readings" 6; SWCCG 2023 Advanced Rulebook Appendix A). The only text
     * states whose uniqueness differs from the bare card. Tests: UniquenessErrataTextStatesTests.
     */
    public static final Set<String> PRINTED_NON_UNIQUE_BY_ERRATA = Set.of("1_72o", "4_21o", "4_123o", "5_53o", "7_224o");

    private static SwccgCardBlueprintLibrary library;
    private static SwccgoFormatLibrary formats;
    private static CollectionsManager collections;

    @BeforeClass
    public static void setUpClass() {
        library = new SwccgCardBlueprintLibrary();
        formats = new SwccgoFormatLibrary(library);
        collections = new CollectionsManager(null, null, null, library);
    }

    @Test
    public void TextStateIdsMapToTheirBarePrinting() {
        assertEquals("2_45", TextState.currentIdOf("2_45o"));
        assertEquals("2_45", TextState.currentIdOf("2_45d"));
        assertEquals("2_45", TextState.currentIdOf("2_45d*"));
        assertEquals("2_45", TextState.currentIdOf("2_45o^"));
        assertEquals("1_19", TextState.currentIdOf("1_19o1"));
        assertEquals("1250_45ai", TextState.currentIdOf("1250_45aio"));
        assertEquals("7_100_BACK", TextState.currentIdOf("7_100d_BACK"));
        assertEquals("o", TextState.stateOf("2_45o"));
        assertEquals("d", TextState.stateOf("2_45d"));
        assertEquals("o2", TextState.stateOf("15_2o2"));
        assertEquals("2_45d", TextState.textStateIdOf("2_45", TextState.DECIPHER));
        assertEquals("2_45o", TextState.textStateIdOf("2_45", TextState.ORIGINAL));
        assertEquals("7_100o_BACK", TextState.textStateIdOf("7_100_BACK", "o"));
        assertEquals("045d", TextState.paddedCardNumber("45d"));
        assertEquals("002o1", TextState.paddedCardNumber("2o1"));
        assertEquals("045aio", TextState.paddedCardNumber("45aio"));
        assertEquals("1045o", TextState.paddedCardNumber("1045o"));
        for (String notAState : new String[]{"2_45", "1250_45ai", "2261_1033", "2_o", "2_45x", "2_45o0", "2_45o10", "2_45do", "x_45o", "2_45od", "45o"})
            assertNull(notAState, TextState.currentIdOf(notAState));
    }

    @Test
    public void TextStateIdsLoadTheirOwnClass() {
        assertEquals("Card2_045o", library.getSwccgoCardBlueprint("2_45o").getClass().getSimpleName());
        assertEquals("Card2_045d", library.getSwccgoCardBlueprint("2_45d").getClass().getSimpleName());
        assertEquals("Card2_045", library.getSwccgoCardBlueprint("2_45").getClass().getSimpleName());
        assertEquals("Card2_045d", library.getSwccgoCardBlueprint("2_45d*").getClass().getSimpleName());
    }

    @Test
    public void AStateWithNoClassIsUnknownInsteadOfCrashingTheLoader() {
        // Blue Milk has no text states: these ids used to throw NumberFormatException in the loader
        assertNull(library.getSwccgoCardBlueprint("2_46o"));
        assertNull(library.getSwccgoCardBlueprint("2_46d"));
        assertNull(library.getSwccgoCardBlueprint("15_2o"));
    }

    @Test
    public void EveryListedTextStateIsTheSameCardAsItsBarePrinting() {
        List<String> ids = TextStateCardPool.implementedIds(library);
        assertTrue(ids.toString(), ids.containsAll(List.of("2_45o", "2_45d")));
        for (String id : ids) {
            SwccgCardBlueprint state = library.getSwccgoCardBlueprint(id);
            TextState info = state.getTextState();
            assertNotNull(id + " has no setTextState", info);
            assertEquals(id, TextState.currentIdOf(id), info.currentId());
            SwccgCardBlueprint current = library.getSwccgoCardBlueprint(info.currentId());
            assertNotNull(id + ": bare card missing", current);
            assertNull(current.getTextState());
            assertEquals(id, current.getTitle(), state.getTitle());
            assertEquals(id, current.getTitles(), state.getTitles());
            if (PRINTED_NON_UNIQUE_BY_ERRATA.contains(id)) {
                assertEquals(id, com.gempukku.swccgo.common.Uniqueness.UNIQUE, current.getUniqueness());
                assertEquals(id, com.gempukku.swccgo.common.Uniqueness.UNRESTRICTED, state.getUniqueness());
            }
            else {
                assertEquals(id, current.getUniqueness(), state.getUniqueness());
            }
            assertEquals(id, current.getSide(), state.getSide());
            assertEquals(id, current.getCardCategory(), state.getCardCategory());
            assertEquals(id, current.getCardTypes(), state.getCardTypes());
            assertEquals(id, current.getExpansionSet(), state.getExpansionSet());
            assertEquals(id, current.getRarity(), state.getRarity());
            assertEquals(id, current.getPersonas(), state.getPersonas());
            assertFalse(id, info.label().isEmpty());
            assertTrue(id, com.gempukku.swccgo.logic.GameUtils.getFullName(state).endsWith(" [" + info.label() + "]"));
        }
    }

    @Test
    public void EveryTextStateClassIsListedForTheCardPool() throws IOException {
        Path cards = Paths.get("..", "gemp-swccg-cards", "src", "main", "java", "com", "gempukku", "swccgo", "cards");
        Pattern className = Pattern.compile("^Card(\\d+)_(\\d+)((?:ai)?(?:o[1-9]?|d))\\.java$");
        Set<String> classIds = new TreeSet<>();
        try (Stream<Path> files = Files.walk(cards)) {
            files.forEach(p -> {
                Matcher m = className.matcher(p.getFileName().toString());
                if (m.matches())
                    classIds.add(m.group(1) + "_" + Integer.parseInt(m.group(2)) + m.group(3));
            });
        }
        assertTrue(classIds.toString(), classIds.containsAll(List.of("2_45o", "2_45d")));
        assertEquals(classIds, new TreeSet<>(TextStateCardPool.listedIds()));
    }

    @Test
    public void TextStatesAreLegalInAnythingGoesOnly() throws DeckInvalidException {
        for (String id : new String[]{"2_45o", "2_45d"}) {
            formats.getFormat("anything_goes").validateCard(id, false);
            for (String format : new String[]{"open", "premiere_original", "legacy"}) {
                if (formats.getFormat(format) == null)
                    continue;
                try {
                    formats.getFormat(format).validateCard(id, false);
                    fail(id + " must not be legal in " + format);
                } catch (DeckInvalidException expected) {
                    // Legacy rejects set 2 before the text-state check; every other format says "older text"
                }
            }
            try {
                formats.getFormat("open").validateCard(id, false);
            } catch (DeckInvalidException expected) {
                assertTrue(expected.getMessage(), expected.getMessage().contains("older text"));
            }
        }
        // The bare card stays legal everywhere it was
        formats.getFormat("anything_goes").validateCard("2_45", false);
        formats.getFormat("open").validateCard("2_45", false);
    }

    @Test
    public void TextStatesAreInTheDeckBuilderForAnythingGoesOnly() {
        for (String id : new String[]{"2_45o", "2_45d"}) {
            SwccgCardBlueprint blueprint = library.getSwccgoCardBlueprint(id);
            assertEquals(id, 60, collections.getDefaultCollection(false).getItemCount(id));
            assertEquals(id, 60, collections.getDefaultCollection(true).getItemCount(id));
            assertTrue(id, formats.getFormat("anything_goes").getAllCardBlueprintsValidInFormat().contains(blueprint));
            assertFalse(id, formats.getFormat("open").getAllCardBlueprintsValidInFormat().contains(blueprint));
        }
        var all = collections.getDefaultCollection(false).getAll().values();
        var anythingGoes = new SortAndFilterCards().process("format:anything_goes", all, library, formats, Collections.emptyMap());
        var open = new SortAndFilterCards().process("format:open", all, library, formats, Collections.emptyMap());
        var stateSet = new SortAndFilterCards().process("set:" + SortAndFilterCards.TEXT_STATES_SET_FILTER, all, library, formats, Collections.emptyMap());
        var commons = new SortAndFilterCards().process("set:" + SortAndFilterCards.TEXT_STATES_SET_FILTER + " rarity:C2", all, library, formats, Collections.emptyMap());
        for (String id : new String[]{"2_45o", "2_45d"}) {
            assertTrue(id, anythingGoes.stream().anyMatch(item -> item.getBlueprintId().equals(id)));
            assertFalse(id, open.stream().anyMatch(item -> item.getBlueprintId().equals(id)));
            assertTrue(id, stateSet.stream().anyMatch(item -> item.getBlueprintId().equals(id)));
            assertTrue(id, commons.stream().anyMatch(item -> item.getBlueprintId().equals(id)));
        }
        assertTrue(stateSet.stream().allMatch(item -> TextState.isTextStateBlueprint(item.getBlueprintId())));
        assertTrue(open.stream().anyMatch(item -> item.getBlueprintId().equals("2_45")));
    }

    private static SwccgDeck deck(String... cards) {
        SwccgDeck deck = new SwccgDeck("text states");
        for (String card : cards)
            deck.addCard(card);
        while (deck.getCards().size() < 60)
            deck.addCard("1_28"); // Rebel Trooper (Light Side, like Blast The Door, Kid!)
        return deck;
    }

    private static DefaultSwccgFormat textStateFormat(String restricted, String banned) {
        DefaultSwccgFormat format = new DefaultSwccgFormat(library, "text states test", false, false, false, false);
        format.setCode("text_states_test");
        format.setTextStates(true);
        format.addValidSet(1);
        format.addValidSet(2);
        if (restricted != null)
            format.addRestrictedCard(restricted);
        if (banned != null)
            format.addBannedCard(banned);
        return format;
    }

    @Test
    public void AnythingGoesDeckMayUseATextStateAndOpenDeckMayNot() throws DeckInvalidException {
        formats.getFormat("anything_goes").validateDeck(deck("2_45o", "2_45d", "2_45"));
        formats.getFormat("open").validateDeck(deck("2_45"));
        try {
            formats.getFormat("open").validateDeck(deck("2_45d"));
            fail("Open deck with a text state");
        } catch (DeckInvalidException expected) {
            assertTrue(expected.getMessage(), expected.getMessage().contains("Blast The Door, Kid!"));
        }
    }

    @Test
    public void MixingTextStatesDoesNotAddCopiesOfARestrictedCard() throws DeckInvalidException {
        assertEquals("2_45", textStateFormat(null, null).sameCardBlueprintId("2_45d"));
        assertEquals("2_45", textStateFormat(null, null).sameCardBlueprintId("2_45o*"));
        var restricted = textStateFormat("2_45", null);
        restricted.validateDeck(deck("2_45"));
        restricted.validateDeck(deck("2_45d"));
        restricted.validateDeck(deck("2_45o"));
        for (String[] pair : new String[][]{{"2_45", "2_45d"}, {"2_45o", "2_45d"}, {"2_45o", "2_45"}}) {
            try {
                restricted.validateDeck(deck(pair));
                fail("Two copies of a restricted card: " + String.join(",", pair));
            } catch (DeckInvalidException expected) {
                assertTrue(expected.getMessage(), expected.getMessage().contains("more than one copy of an restricted card"));
            }
        }
    }

    @Test
    public void BanningTheBareCardBansItsTextStates() {
        var banned = textStateFormat(null, "2_45");
        for (String id : new String[]{"2_45", "2_45o", "2_45d"}) {
            try {
                banned.validateCard(id, false);
                fail(id + " must be banned with 2_45");
            } catch (DeckInvalidException expected) {
                assertTrue(expected.getMessage(), expected.getMessage().contains("banned card"));
            }
        }
    }
}
