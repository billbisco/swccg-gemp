package com.gempukku.swccgo.game.formats;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.DeckInvalidException;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.game.SwccgFormat;
import com.gempukku.swccgo.game.setup.CardPool;
import com.gempukku.swccgo.game.setup.GameOption;
import com.gempukku.swccgo.game.setup.GameSetup;
import com.gempukku.swccgo.guest.GuestDecks;
import com.gempukku.swccgo.guest.PlayNowSide;
import com.gempukku.swccgo.logic.vo.SwccgDeck;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import static com.gempukku.swccgo.game.formats.CustomCardPoolTestSupport.*;
import static org.junit.Assert.*;

/** swccg-guild/work#222: a tournament's custom card pool (sets, allows, bans) on top of a named format. */
public class CustomCardPoolTests {
    private static final SwccgoFormatLibrary FORMATS = VirtualTableScenario._formatLibrary;
    private static final SwccgCardBlueprintLibrary CARDS = VirtualTableScenario._cardLibrary;

    static SwccgFormat poolFormat(String formatCode, CardPool pool) {
        GameSetup setup = GameSetup.of(FORMATS.getFormat(formatCode), Map.of(), GameOption.Scope.TOURNAMENT).withPool(pool);
        return FORMATS.effectiveFormat(setup);
    }

    static String message(SwccgFormat format, SwccgDeck deck) {
        try {
            format.validateDeck(deck);
            return null;
        } catch (DeckInvalidException exp) {
            return exp.getMessage();
        }
    }

    @Test
    public void NoCustomizationIsTheNamedFormatItself() {
        SwccgFormat open = FORMATS.getFormat("open");
        assertSame(open, poolFormat("open", CardPool.NONE));
        assertSame(CardPool.NONE, CardPool.of(null, Collections.emptyList(), Collections.singletonList(" ")));
        assertTrue(GameSetup.defaults("open").withPool(CardPool.NONE).isDefault());
    }

    @Test
    public void SetsAllowsAndBansDecideTheDeck() {
        SwccgDeck light = GuestDecks.deck(PlayNowSide.LIGHT);
        SwccgFormat open = FORMATS.getFormat("open");
        assertNull("the guest deck is legal in Open", message(open, light));
        List<Integer> sets = setsOf(light);
        assertTrue("the test needs a deck from two or more sets: " + sets, sets.size() >= 2);
        int excluded = sets.get(sets.size() - 1);
        int included = sets.get(0);
        List<Integer> playing = new ArrayList<>(sets);
        playing.remove(Integer.valueOf(excluded));

        // the deck's own sets: legal
        assertNull(message(poolFormat("open", CardPool.of(sets, null, null)), light));

        // leave a set out: its cards are named as outside the pool
        String outside = message(poolFormat("open", CardPool.of(playing, null, null)), light);
        assertNotNull(outside);
        assertTrue(outside, outside.contains("not in this tournament's custom card pool"));

        // allow the deck's cards from that set: legal again
        List<String> fromExcluded = cardsFromSet(light, excluded);
        assertNull(message(poolFormat("open", CardPool.of(playing, fromExcluded, null)), light));

        // ban a card from a set the event plays: a deck with it is refused, naming the card
        String banned = unusedLightCard(light, included, open);
        SwccgFormat pool = poolFormat("open", CardPool.of(playing, fromExcluded, List.of(banned)));
        assertNull(message(pool, light));
        String refused = message(pool, withCard(light, banned));
        assertNotNull(refused);
        assertTrue(refused, refused.contains("banned in this tournament's custom card pool") && refused.contains(banned));
        // its foil (a cosmetic printing) follows the same ruling
        String foil = message(pool, withCard(light, banned + "*"));
        assertNotNull(foil);
        assertTrue(foil, foil.contains("banned in this tournament's custom card pool"));
        // the summary agrees with the pool
        assertEquals(light.getCards().size(), pool.getRequiredDeckSize());
        assertTrue(pool.getBannedCards().contains(banned));
        assertTrue(pool.getValidCards().containsAll(fromExcluded));
        assertEquals(playing, pool.getValidSets());
    }

    @Test
    public void AnAllowDoesNotLiftDeckRules() {
        SwccgDeck light = GuestDecks.deck(PlayNowSide.LIGHT);
        List<String> everything = new ArrayList<>(light.getCards());
        SwccgFormat pool = poolFormat("open", CardPool.of(setsOf(light), everything, null));
        // deck size is not part of the pool
        SwccgDeck shortDeck = new SwccgDeck("short");
        for (String id : light.getCards().subList(0, light.getCards().size() - 1))
            shortDeck.addCard(id);
        for (String id : light.getCardsOutsideDeck())
            shortDeck.addCardOutsideDeck(id);
        String size = message(pool, shortDeck);
        assertNotNull(size);
        assertTrue(size, size.contains("cards are required"));
        // nor is mixing sides: an allowed Dark Side card still can't join a Light deck
        SwccgDeck dark = GuestDecks.deck(PlayNowSide.DARK);
        String darkCard = dark.getCards().get(0);
        SwccgFormat allowDark = poolFormat("open", CardPool.of(setsOf(light), List.of(darkCard), null));
        String mixed = message(allowDark, withCard(light, darkCard));
        assertNotNull(mixed);
        assertTrue(mixed, mixed.contains("both light side and dark side"));
        // nor a table's "Shields: none" game option
        GameSetup noShields = GameSetup.of(FORMATS.getFormat("open"), Map.of("noShields", "true"), GameOption.Scope.TOURNAMENT);
        String shield = null;
        for (String id : light.getCardsOutsideDeck()) {
            SwccgCardBlueprint card = CARDS.getSwccgoCardBlueprint(id);
            if (card != null && card.getCardCategory() == CardCategory.DEFENSIVE_SHIELD) {
                shield = id;
                break;
            }
        }
        if (shield != null) {
            SwccgFormat format = FORMATS.effectiveFormat(noShields.withPool(CardPool.of(null, List.of(shield), null)));
            try {
                format.validateCard(shield, false);
                fail("an allow must not lift Shields: none");
            } catch (DeckInvalidException expected) {
                assertTrue(expected.getMessage().contains("Shields: none"));
            }
        }
    }

    @Test
    public void PoolsAreCheckedWholeBeforeAnEventUsesThem() {
        SwccgFormat open = FORMATS.getFormat("open");
        FORMATS.checkPool(open, CardPool.of(List.of(1, 2), List.of("3_1"), List.of("1_1")));
        assertProblem(open, CardPool.of(null, List.of("1_80"), List.of("1_80")), "both allowed and banned");
        assertProblem(open, CardPool.of(null, List.of("1_80"), List.of("1_80*")), "both allowed and banned");
        assertProblem(open, CardPool.of(List.of(1, 9999), null, null), "Set 9999 is not a set GEMP supports");
        assertProblem(open, CardPool.of(null, List.of("1_99999"), null), "Card 1_99999 is not a card GEMP supports");
        assertProblem(open, CardPool.of(Collections.emptyList(), null, null), "at least one set");
        // every problem is reported at once
        try {
            FORMATS.checkPool(open, CardPool.of(List.of(9999), List.of("1_99999"), null));
            fail();
        } catch (IllegalArgumentException exp) {
            assertTrue(exp.getMessage().contains("9999 is not a set") && exp.getMessage().contains("1_99999"));
        }
    }

    private static void assertProblem(SwccgFormat format, CardPool pool, String problem) {
        try {
            FORMATS.checkPool(format, pool);
            fail("expected: " + problem);
        } catch (IllegalArgumentException exp) {
            assertTrue(exp.getMessage(), exp.getMessage().contains(problem));
        }
    }

    @Test
    public void AlternatePrintingsShareTheRuling() throws Exception {
        // a mapped printing (blueprintMapping.txt) of a banned card is banned too
        String[] pair = null;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                SwccgCardBlueprintLibrary.class.getResourceAsStream("/blueprintMapping.txt"), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null && pair == null) {
                if (line.startsWith("#") || line.contains("BACK"))
                    continue;
                String[] split = line.split(",");
                if (split.length == 2 && CARDS.getSwccgoCardBlueprint(split[0]) != null && CARDS.getSwccgoCardBlueprint(split[1]) != null)
                    pair = split;
            }
        }
        assertNotNull("blueprintMapping.txt has a mapped printing", pair);
        CardPool pool = CardPool.of(null, null, List.of(pair[1]));
        SwccgFormat format = FORMATS.effectiveFormat(GameSetup.defaults("open").withPool(pool));
        for (String printing : pair) {
            try {
                format.validateCard(printing, false);
                fail(printing + " is a printing of a banned card");
            } catch (DeckInvalidException expected) {
                assertTrue(expected.getMessage().contains("banned in this tournament's custom card pool"));
            }
        }
        assertProblem(FORMATS.getFormat("open"), CardPool.of(null, List.of(pair[0]), List.of(pair[1])), "both allowed and banned");
    }

    @Test
    public void ThePoolIsSavedWithTheSetupAndSummarized() {
        CardPool pool = CardPool.of(List.of(2, 1), List.of("3_1"), List.of("1_121"));
        GameSetup setup = GameSetup.of(FORMATS.getFormat("open"), Map.of("deckSize", "45"), GameOption.Scope.TOURNAMENT).withPool(pool);
        GameSetup reloaded = GameSetup.fromJson(setup.toJson(), "open");
        assertEquals(setup, reloaded);
        assertEquals(List.of(1, 2), reloaded.getPool().getSets());
        assertEquals("Deck: 45 cards · Custom card pool", reloaded.chipsText());
        // a setup saved before #222 has no pool
        assertTrue(GameSetup.fromJson("{\"v\":1,\"format\":\"open\",\"mode\":\"1v1\",\"options\":{}}", "open").getPool().isNone());
        // the pool sits on top of the chosen deck size
        assertEquals(45, FORMATS.effectiveFormat(reloaded).getRequiredDeckSize());
        assertEquals("open", FORMATS.effectiveFormat(reloaded).getBaseCode());
        // the summary tells same-titled cards apart by side, set and id
        List<String> lines = FORMATS.describePool(reloaded);
        assertEquals("Sets: 1, 2 (instead of Open's sets)", lines.get(0));
        assertTrue(lines.toString(), lines.get(1).startsWith("Allowed: ") && lines.get(1).contains("3_1)"));
        assertTrue(lines.toString(), lines.get(2).startsWith("Banned: ") && lines.get(2).contains("(Light, set 1, 1_121)"));
        assertTrue(FORMATS.cardLabel("1_281").contains("(Dark, set 1, 1_281)"));
        assertTrue(FORMATS.describePool(GameSetup.defaults("open")).isEmpty());
    }

    @Test
    public void TwoEventsOnOneFormatKeepTheirOwnRules() {
        SwccgDeck light = GuestDecks.deck(PlayNowSide.LIGHT);
        SwccgFormat open = FORMATS.getFormat("open");
        int set = setsOf(light).get(0);
        String card = unusedLightCard(light, set, open);
        SwccgDeck withIt = withCard(light, card);
        SwccgFormat banning = poolFormat("open", CardPool.of(null, null, List.of(card)));
        SwccgFormat plain = poolFormat("open", CardPool.NONE);
        assertNotNull(message(banning, withIt));
        assertNull("the other event on Open is untouched", message(plain, withIt));
        assertNull("so is Open itself", message(FORMATS.getFormat("open"), withIt));
        assertFalse(open.getBannedCards().contains(card));
    }

    @Test
    public void TheFormSearchesCardsAndPreviewsWhatTheEventWillShow() {
        List<String> found = FORMATS.searchCards("alderaan", 40);
        assertTrue(found.toString(), found.contains("1_121") && found.contains("1_281"));
        assertEquals(List.of("1_121"), FORMATS.searchCards("1_121", 40));
        assertTrue(FORMATS.searchCards("a", 40).isEmpty());
        assertEquals(3, FORMATS.searchCards("er", 3).size());
        CardPool pool = CardPool.of(List.of(1, 2), List.of("3_1"), List.of("1_121"));
        assertEquals(FORMATS.describePool(GameSetup.defaults("open").withPool(pool)), FORMATS.previewPool("open", pool));
        assertEquals("a deck size on the code is the same format", FORMATS.previewPool("open", pool), FORMATS.previewPool("open@45", pool));
        try {
            FORMATS.previewPool("open", CardPool.of(null, List.of("1_121"), List.of("1_121")));
            fail();
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("both allowed and banned"));
        }
        try {
            FORMATS.previewPool("no-such-format", pool);
            fail();
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("not supported"));
        }
    }
}
