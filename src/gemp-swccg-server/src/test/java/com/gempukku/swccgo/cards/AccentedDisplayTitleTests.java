package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.collection.CollectionsManager;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.CardItem;
import com.gempukku.swccgo.game.SortAndFilterCards;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.game.formats.SwccgoFormatLibrary;
import com.gempukku.swccgo.logic.GameUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Accented printed titles (Bill, 2026-11-12): the face's accented spelling is the card's Display Title (display only),
 * every rule keeps the plain title, and the name search folds accents for a query typed without them ("Padme" finds
 * "Padmé"), while a query typed with an accent matches only that accent ("Padmé" does not find "Padme").
 */
public class AccentedDisplayTitleTests {
    /** Id -> printed title (wiki article face, checked 2026-11-12). */
    private static final Map<String, String> PRINTED = new LinkedHashMap<>() {{
        put("11_8", "Padmé Naberrie");
        put("11_9", "Padmé Naberrie");
        put("12_24", "Ric Olié");
        put("12_25", "Sabé");
        put("12_26", "Saché");
        put("12_33", "Yané");
        put("14_29", "Ric Olié, Bravo Leader");
        put("200_11", "Dormé");
        put("1291_40", "Dormé");
        put("601_223", "Dormé");
        put("201_7", "Yané");
        put("203_10", "Padmé Naberrie");
        put("203_36", "Padmé Naberrie");
        put("207_2", "Chirrut Îmwe");
        put("209_40", "Vaneé");
        put("1111_13", "Padmé Naberrie");
        put("1116_4", "Senator Padmé Amidala");
        put("1250_48", "Padmé Naberrie");
        put("1250_48ai", "Padmé Naberrie");
        put("601_55", "Padmé Naberrie");
        put("1250_52", "Senator Padmé Amidala");
    }};

    private static SwccgCardBlueprintLibrary library;

    @BeforeClass
    public static void setUpClass() {
        library = new SwccgCardBlueprintLibrary();
    }

    @Test
    public void AccentedCardsShowTheirPrintedSpellingAndKeepThePlainRulesTitle() {
        for (var entry : PRINTED.entrySet()) {
            String id = entry.getKey();
            SwccgCardBlueprint card = library.getSwccgoCardBlueprint(id);
            String printed = entry.getValue();
            assertEquals(id, printed, card.getDisplayTitle());
            assertEquals(id, printed, GameUtils.getDisplayTitle(card));
            assertFalse(id + " is not a Legacy version", card.isDisplayTitleComplete());
            // The rules title is the plain spelling
            String plain = java.text.Normalizer.normalize(printed, java.text.Normalizer.Form.NFD).replaceAll("\\p{M}+", "");
            assertEquals(id, plain, card.getTitle());
            // Full name: printed spelling, " (V)" still added for a Virtual card
            // (and " (AI)" for an alternate image)
            String rulesName = GameUtils.getRulesFullName(card);
            assertTrue(id + ": " + rulesName, rulesName.startsWith(plain));
            assertEquals(id, rulesName.replace(plain, printed), GameUtils.getFullName(card));
        }
        assertEquals("Padmé Naberrie (V)", GameUtils.getFullName(library.getSwccgoCardBlueprint("203_10")));
        assertEquals("Padmé Naberrie", GameUtils.getFullName(library.getSwccgoCardBlueprint("11_8")));
        assertEquals(Title.Padme, library.getSwccgoCardBlueprint("11_8").getTitle());
        // A card without accents has no Display Title
        assertNull(library.getSwccgoCardBlueprint("1_21").getDisplayTitle());
    }

    @Test
    public void NameMatchFoldsAccentsOnlyForAQueryWithoutAccents() {
        assertTrue(SortAndFilterCards.nameContains("Padmé Naberrie", "padme"));
        assertTrue(SortAndFilterCards.nameContains("Padmé Naberrie", "PADME"));
        assertTrue(SortAndFilterCards.nameContains("•Padme Naberrie", "padme"));
        assertTrue(SortAndFilterCards.nameContains("Padmé Naberrie", "Padmé"));
        assertTrue(SortAndFilterCards.nameContains("Padmé Naberrie", "PADMÉ"));
        assertFalse(SortAndFilterCards.nameContains("•Padme Naberrie", "Padmé"));
        assertTrue(SortAndFilterCards.nameContains("•Chirrut Îmwe", "imwe"));
        assertFalse(SortAndFilterCards.nameContains("•Chirrut Imwe", "Îmwe"));
        // A decomposed accent (e + combining acute) typed by the client is the same as the composed é
        assertTrue(SortAndFilterCards.nameContains("Padmé Naberrie", "Padmé"));
        assertFalse(SortAndFilterCards.nameContains("•Padme Naberrie", "Padmé"));
    }

    @Test
    public void DeckBuilderNameSearchFindsAccentedCards() {
        var formats = new SwccgoFormatLibrary(library);
        var collections = new CollectionsManager(null, null, null, library);
        var all = collections.getDefaultCollection(false).getAll().values();
        List<String> plain = ids(new SortAndFilterCards().process("name:padme name:naberrie", all, library, formats, Collections.emptyMap()));
        List<String> accented = ids(new SortAndFilterCards().process("name:Padmé name:Naberrie", all, library, formats, Collections.emptyMap()));
        for (String id : List.of("11_8", "203_10", "1111_13")) {
            assertTrue(id, plain.contains(id));
            assertTrue(id, accented.contains(id));
        }
        assertTrue(ids(new SortAndFilterCards().process("name:Imwe", all, library, formats, Collections.emptyMap())).contains("207_2"));
        assertTrue(ids(new SortAndFilterCards().process("name:Îmwe", all, library, formats, Collections.emptyMap())).contains("207_2"));
        assertTrue(ids(new SortAndFilterCards().process("name:ric name:olie", all, library, formats, Collections.emptyMap())).contains("14_29"));
        // An accented query matches only accented text: no card prints "Naberrié"
        assertTrue(new SortAndFilterCards().process("name:Naberrié", all, library, formats, Collections.emptyMap()).isEmpty());
    }

    private static List<String> ids(Collection<? extends CardItem> items) {
        return items.stream().map(CardItem::getBlueprintId).toList();
    }

    private static VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("padme", "11_8");
            put("padmeV", "1111_13");
            put("ric", "12_24");
            put("ricBravo", "14_29");
        }}, new HashMap<>() {{
            put("vader", "1_168");
        }}, 40, 40, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts, StartingSetup.NoLSShields,
                StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void UniquenessPersonaAndTitleRulesUseThePlainTitle() {
        var scn = scenario();
        var padme = scn.GetLSCard("padme");
        var padmeV = scn.GetLSCard("padmeV");
        var ric = scn.GetLSCard("ric");
        var ricBravo = scn.GetLSCard("ricBravo");
        scn.StartGame();
        assertTrue(Filters.title(Title.Padme).accepts(scn.game(), padme));
        assertTrue(Filters.title("Ric Olie").accepts(scn.game(), ric));
        assertFalse(Filters.title("Ric Olié").accepts(scn.game(), ric));
        assertTrue(Filters.Padme.accepts(scn.game(), padmeV));
        assertTrue(Filters.sameTitle(padme).accepts(scn.game(), padmeV));
        assertEquals("Padmé Naberrie", GameUtils.getFullName(padme));

        // Padmé on table: the other Padmé Naberrie (V) may not deploy (same card, same persona); likewise Ric Olié
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), padme, ric);
        scn.MoveCardsToLSHand(padmeV, ricBravo);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(20);
        assertFalse(scn.LSDeployAvailable(padmeV));
        assertFalse(scn.LSDeployAvailable(ricBravo));
    }
}
