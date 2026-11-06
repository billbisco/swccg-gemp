package com.gempukku.swccgo.game.setup;

import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.DeckSize;
import com.gempukku.swccgo.game.SwccgFormat;
import com.gempukku.swccgo.game.formats.SwccgoFormatLibrary;
import com.gempukku.swccgo.game.setup.GameOption.Kind;
import com.gempukku.swccgo.game.setup.GameOption.Scope;
import com.gempukku.swccgo.game.setup.GameOption.Type;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/** swccg-guild/work#116: the game setup (format + options) and the option registry. */
public class GameSetupTests {
    private static final SwccgoFormatLibrary LIBRARY = VirtualTableScenario._formatLibrary;

    private static Map<String, String> options(String... pairs) {
        Map<String, String> map = new LinkedHashMap<>();
        for (int i = 0; i < pairs.length; i += 2)
            map.put(pairs[i], pairs[i + 1]);
        return map;
    }

    @Test
    public void GameSetupValuesAtTheDefaultAreDropped() {
        SwccgFormat open = LIBRARY.getFormat("open");
        GameSetup setup = GameSetup.of(open, options("deckSize", "60", "timer", "default"), Scope.CASUAL);
        assertTrue(setup.isDefault());
        assertEquals("", setup.chipsText());
        assertEquals("60", setup.get("deckSize", open));
        assertEquals("default", setup.get("timer", open));
        assertSame("a default setup plays the format itself", open, LIBRARY.effectiveFormat(setup));
        assertTrue(GameSetup.of(open, null, Scope.CASUAL).isDefault());
        assertTrue(GameSetup.of(open, options("deckSize", " "), Scope.CASUAL).isDefault());
    }

    @Test
    public void GameSetupChipsShowOnlyNonDefaultOptionsInKindOrder() {
        SwccgFormat rg = LIBRARY.getFormat("premiere_reflections_gold");
        // given in the "wrong" order; chips come out as deck rules, play rules, table settings
        GameSetup setup = GameSetup.of(rg, options("timer", "blitz", "startingCredits", "4", "deckSize", "100"), Scope.CASUAL);
        assertEquals(List.of("Deck: 100 cards", "Starting: 4 credits", "Timer: Blitz"), setup.chips());
        assertEquals("Deck: 100 cards · Starting: 4 credits · Timer: Blitz", setup.chipsText());
        assertEquals("premiere_reflections_gold", setup.getFormatCode());
        SwccgFormat effective = LIBRARY.effectiveFormat(setup);
        assertEquals(100, effective.getRequiredDeckSize());
        assertEquals("the name never changes", rg.getName(), effective.getName());
        Map<String, String> full = setup.describe(rg);
        assertEquals("100", full.get("Deck: size"));
        assertEquals("4", full.get("Starting: credits"));
        assertEquals("Blitz! (25m/3m)", full.get("Timer: game"));
    }

    @Test
    public void GameSetupFullViewShowsDefaultsAndHidesUnavailableOptions() {
        SwccgFormat open = LIBRARY.getFormat("open");
        Map<String, String> full = GameSetup.defaults("open").describe(open);
        assertEquals("60 (default)", full.get("Deck: size"));
        assertEquals("Default (45m/6m) (default)", full.get("Timer: game"));
        assertFalse("Open has no starting credits", full.containsKey("Starting: credits"));
        assertEquals("40–60 (default)", GameSetup.defaults("anything_goes").describe(LIBRARY.getFormat("anything_goes")).get("Deck: size"));
    }

    @Test
    public void GameSetupRejectsUnknownUnavailableOutOfRangeAndOutOfScopeOptions() {
        SwccgFormat open = LIBRARY.getFormat("open");
        for (Map<String, String> bad : List.of(
                options("noSuchOption", "1"),
                options("startingCredits", "4"),                       // not used in Open
                options("deckSize", String.valueOf(DeckSize.MIN_SUPPORTED - 1)),
                options("deckSize", String.valueOf(DeckSize.MAX_SUPPORTED + 1)),
                options("deckSize", "lots"),
                options("timer", "forever")))
            assertThrows(bad.toString(), IllegalArgumentException.class, () -> GameSetup.of(open, bad, Scope.CASUAL));
        // the timer is a casual-table setting only
        assertThrows(IllegalArgumentException.class, () -> GameSetup.of(open, options("timer", "blitz"), Scope.BOT));
        assertEquals("Timer: Blitz", GameSetup.of(open, options("timer", "BLITZ"), Scope.CASUAL).chipsText());
    }

    @Test
    public void GameSetupJsonRoundTripsAndOldRowsMeanDefaults() {
        SwccgFormat open = LIBRARY.getFormat("open");
        GameSetup setup = GameSetup.of(open, options("deckSize", "75", "timer", "slow"), Scope.CASUAL);
        String json = setup.toJson();
        assertTrue(json.contains("\"v\":1"));
        assertEquals(setup, GameSetup.fromJson(json, "ignored"));
        assertEquals(GameSetup.defaults("open"), GameSetup.fromJson(null, "open"));
        assertEquals(GameSetup.defaults("open"), GameSetup.fromJson("", "open"));
        // an option this build doesn't know survives a round trip
        GameSetup newer = GameSetup.fromJson("{\"v\":1,\"format\":\"open\",\"mode\":\"1v1\",\"options\":{\"fromTheFuture\":\"x\"}}", "open");
        assertEquals("x", GameSetup.fromJson(newer.toJson(), "open").getChosen("fromTheFuture"));
    }

    @Test
    public void GameSetupStoredRowsWithRetiredCodesKeepTheirDeckSize() {
        for (Map.Entry<String, Integer> retired : Map.of("open40card", 40, "open_45_card", 45, "open_100_card", 100).entrySet()) {
            GameSetup setup = LIBRARY.storedSetup(retired.getKey(), null);
            assertEquals("open", setup.getFormatCode());
            assertEquals(String.valueOf(retired.getValue()), setup.getChosen("deckSize"));
            assertEquals("Deck: " + retired.getValue() + " cards", setup.chipsText());
            assertEquals((int) retired.getValue(), LIBRARY.effectiveFormat(setup).getRequiredDeckSize());
        }
        // the deck-list key form, and a plain old row
        assertEquals("45", LIBRARY.storedSetup("open@45", null).getChosen("deckSize"));
        assertTrue(LIBRARY.storedSetup("open", null).isDefault());
        // a stored setup wins over what the code implies
        GameSetup stored = GameSetup.of(LIBRARY.getFormat("open"), options("deckSize", "50"), Scope.CASUAL);
        assertEquals("50", LIBRARY.storedSetup("open", stored.toJson()).getChosen("deckSize"));
    }

    @Test
    public void GameSetupDeckSizeRangeComesFromDeckSize() {
        GameOption deckSize = GameOptions.get(GameOptions.DECK_SIZE);
        SwccgFormat open = LIBRARY.getFormat("open");
        assertEquals(DeckSize.MIN_SUPPORTED, deckSize.getMin(open));
        assertEquals(DeckSize.MAX_SUPPORTED, deckSize.getMax(open));
        assertEquals(String.valueOf(DeckSize.MAX_SUPPORTED),
                GameSetup.of(open, options("deckSize", String.valueOf(DeckSize.MAX_SUPPORTED)), Scope.CASUAL).getChosen("deckSize"));
    }

    @Test
    public void GameSetupANewOptionNeedsOnlyARegistryEntry() {
        GameOption probe = GameOption.builder("testProbe", Kind.PLAY_RULES, Type.BOOLEAN, "Test probe rule")
                .defaultValue(f -> "true")
                .shortLabel(v -> "true".equals(v) ? "Probe on" : "No probe")
                .scopes(Scope.CASUAL, Scope.LEAGUE)
                .build();
        GameOptions.register(probe);
        try {
            SwccgFormat open = LIBRARY.getFormat("open");
            assertTrue(GameOptions.all().contains(probe));
            GameSetup setup = GameSetup.of(open, options("testProbe", "off", "deckSize", "100"), Scope.CASUAL);
            assertEquals("false", setup.getChosen("testProbe"));
            assertEquals("Deck: 100 cards · No probe", setup.chipsText());
            assertEquals("Off", setup.describe(open).get("Test probe rule"));
            assertEquals(setup, GameSetup.fromJson(setup.toJson(), "open"));
            assertTrue(GameSetup.of(open, options("testProbe", "on"), Scope.CASUAL).isDefault());
            assertThrows(IllegalArgumentException.class, () -> GameSetup.of(open, options("testProbe", "off"), Scope.BOT));
        } finally {
            GameOptions.unregister("testProbe");
        }
        assertNull(GameOptions.get("testProbe"));
    }
}
