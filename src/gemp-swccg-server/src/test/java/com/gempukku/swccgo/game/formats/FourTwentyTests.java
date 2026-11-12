package com.gempukku.swccgo.game.formats;

import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.DeckInvalidException;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgFormat;
import com.gempukku.swccgo.game.setup.GameOption;
import com.gempukku.swccgo.game.setup.GameOptions;
import com.gempukku.swccgo.game.setup.GameSetup;
import com.gempukku.swccgo.logic.vo.SwccgDeck;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

/**
 * swccg-guild/work#224: the "Deck: 420" game option (Clayton Moore). No Objectives; locations count as destiny 0;
 * every other card needs printed destiny 0, 2 or 4, inside or outside the deck. It adds to the base format's rules.
 */
public class FourTwentyTests {
    private static final SwccgoFormatLibrary LIBRARY = VirtualTableScenario._formatLibrary;
    private static final Map<String, String> ON = Map.of(GameOptions.FOUR_TWENTY, "true");

    private static SwccgFormat table(String format, Map<String, String> options) {
        return LIBRARY.effectiveFormat(GameSetup.of(LIBRARY.getFormat(format), options, GameOption.Scope.CASUAL));
    }

    private static SwccgCardBlueprint card(String id) {
        SwccgCardBlueprint blueprint = cardLibrary().getSwccgoCardBlueprint(id);
        assertNotNull(id, blueprint);
        return blueprint;
    }

    private static com.gempukku.swccgo.game.SwccgCardBlueprintLibrary cardLibrary() {
        return VirtualTableScenario._cardLibrary;
    }

    /** A 60-card Dark deck legal in Premiere–Death Star II and under 420, plus the given cards replacing filler. */
    private static SwccgDeck deck(String... extra) {
        SwccgDeck deck = new SwccgDeck("420");
        deck.addCard("1_281"); // Alderaan: location, no printed destiny
        deck.addCard("1_188"); // MSE-6 'Mouse' Droid: destiny 0
        deck.addCard("1_163"); // 5D6-RA-7: destiny 2
        for (String card : extra)
            deck.addCard(card);
        while (deck.getCards().size() < 60)
            deck.addCard("1_193"); // R4-M9: destiny 4
        return deck;
    }

    private static String error(SwccgFormat format, SwccgDeck deck) {
        try {
            format.validateDeck(deck);
            return null;
        } catch (DeckInvalidException e) {
            return e.getMessage();
        }
    }

    // --- AC2: the per-card rule ---

    @Test
    public void LocationsCountAsZeroAndPrintedZeroTwoFourQualify() {
        assertNull("location", FourTwenty.violation(card("1_281")));
        assertNull("destiny 0", FourTwenty.violation(card("1_188")));
        assertNull("destiny 0 Effect", FourTwenty.violation(card("9_134")));
        assertNull("destiny 2", FourTwenty.violation(card("1_163")));
        assertNull("destiny 4", FourTwenty.violation(card("1_193")));
    }

    @Test
    public void ObjectivesUnprintedNonnumericAndOtherNumbersAreRefused() {
        assertEquals("Objectives are not permitted", FourTwenty.violation(card("9_151")));      // Bring Him Before Me (destiny 0)
        assertTrue(FourTwenty.violation(card("1_194")).contains("printed destiny 1 is not 0, 2 or 4")); // Stormtrooper
        assertTrue(FourTwenty.violation(card("1_170")).contains("printed destiny 3"));        // Death Star Trooper
        assertTrue(FourTwenty.violation(card("9_109")).contains("printed destiny 6"));        // Emperor Palpatine
        assertTrue(FourTwenty.violation(card("1_175")).contains("no printed destiny"));       // EG-6: no destiny value
        assertTrue(FourTwenty.violation(card("2_2")).contains("π"));                          // Brainiac: pi
        assertTrue(FourTwenty.violation(card("213_10")).contains("0.5"));                     // Maul: destiny 0.5
        assertTrue(FourTwenty.violation(card("217_2")).contains("4.5"));                      // BB-9E: destiny 4.5
        // Defensive Shields print no destiny number; the engine's 0 does not count
        assertTrue(FourTwenty.violation(card("13_51")).contains("no printed destiny"));
    }

    @Test
    public void OnlyThePrintedDestinyCountsNotASecondOrConditionalValue() {
        SwccgCardBlueprint artoo = card("6_003"); // Artoo: printed 1, a second value of 6 under conditions
        assertEquals(Float.valueOf(1f), artoo.getDestiny());
        assertNotEquals(artoo.getDestiny(), artoo.getAlternateDestiny());
        assertNotNull(FourTwenty.violation(artoo));
    }

    // --- AC2/AC3: composed deck validation ---

    @Test
    public void QualifyingDeckIsAcceptedInPremiereDeathStarIIAndOpen() {
        assertNull(error(table("premiere_ds2", ON), deck()));
        assertNull(error(table("open", ON), deck()));
    }

    @Test
    public void OffendingCardIsNamedWithTheRestrictionItFailed() {
        String objective = error(table("premiere_ds2", ON), deck("7_296")); // Carbon Chamber Testing
        assertNotNull(objective);
        assertTrue(objective, objective.contains("Carbon Chamber Testing") && objective.contains("Deck: 420")
                && objective.contains("Objectives are not permitted"));
        String one = error(table("open", ON), deck("1_194"));
        assertTrue(one, one.contains("Stormtrooper") && one.contains("printed destiny 1"));
        // with 420 off, the same decks get the ordinary format decision
        assertNull(error(table("premiere_ds2", Map.of()), deck("7_296")));
        assertNull(error(table("open", Map.of()), deck("1_194")));
    }

    @Test
    public void OutsideDeckCardsAreCheckedToo() {
        SwccgDeck withShield = deck("13_69"); // Fear Is My Ally (Starting Effect, destiny 0)
        withShield.addCardOutsideDeck("13_51"); // A Useless Gesture (Defensive Shield)
        String refused = error(table("open", ON), withShield);
        assertNotNull(refused);
        assertTrue(refused, refused.contains("A Useless Gesture") && refused.contains("no printed destiny"));
        assertNull("the format alone allows it", error(table("open", Map.of()), withShield));

        SwccgDeck goodOutside = deck("13_69");
        goodOutside.addCardOutsideDeck("1_188"); // destiny 0
        assertNull(error(table("open", ON), goodOutside));
        SwccgDeck badOutside = deck("13_69");
        badOutside.addCardOutsideDeck("1_194"); // destiny 1
        assertNotNull(error(table("open", ON), badOutside));
    }

    @Test
    public void QualifyingCardTheBaseFormatForbidsIsStillRefused() {
        SwccgDeck virtual = deck("200_72"); // 5D6-RA-7 (V): destiny 2, not in Premiere–Death Star II
        assertNull(FourTwenty.violation(card("200_72")));
        assertNotNull("the base format still applies", error(table("premiere_ds2", ON), virtual));
        assertNotNull(error(table("premiere_ds2", Map.of()), virtual));
        assertNull(error(table("open", ON), virtual));
        // and other options still apply with it: Shields: none + 420
        SwccgDeck withShield = deck();
        withShield.addCardOutsideDeck("13_51");
        assertNotNull(error(table("open", Map.of(GameOptions.FOUR_TWENTY, "true", GameOptions.NO_SHIELDS, "true")), withShield));
        // and the chosen deck size
        assertNotNull(error(table("open", Map.of(GameOptions.FOUR_TWENTY, "true", GameOptions.DECK_SIZE, "45")), deck()));
    }

    @Test
    public void DeckBuilderCardFilterLeavesOutFailingCards() throws Exception {
        SwccgFormat format = table("open", ON);
        format.validateCard("1_193", false);
        assertThrows(DeckInvalidException.class, () -> format.validateCard("7_296", false));
        assertThrows(DeckInvalidException.class, () -> format.validateCard("1_194", false));
        table("open", Map.of()).validateCard("1_194", false);
    }

    // --- AC1/AC4: the option, its default and persistence ---

    @Test
    public void OptionIsOffByDefaultAndAvailableWithEveryFormatAndScope() {
        GameOption option = GameOptions.get(GameOptions.FOUR_TWENTY);
        assertNotNull(option);
        assertEquals(GameOption.Kind.DECK_RULES, option.getKind());
        assertTrue(option.getHelp().contains("Clayton Moore") && option.getHelp().contains("0, 2 or 4"));
        for (GameOption.Scope scope : GameOption.Scope.values())
            assertTrue(scope.name(), option.isAllowedIn(scope));
        for (SwccgFormat format : LIBRARY.getAllFormats().values()) {
            assertTrue(format.getCode(), option.isAvailable(format));
            assertEquals(format.getCode(), "false", option.getDefault(format));
        }
        GameSetup on = GameSetup.of(LIBRARY.getFormat("premiere_ds2"), ON, GameOption.Scope.LEAGUE);
        assertEquals("Deck: 420", on.chipsText());
        assertTrue(GameSetup.of(LIBRARY.getFormat("premiere_ds2"), Map.of(), GameOption.Scope.LEAGUE).isDefault());
    }

    @Test
    public void SavedSetupKeepsFourTwentyAndOldSetupsAreOff() {
        GameSetup on = GameSetup.of(LIBRARY.getFormat("premiere_ds2"), ON, GameOption.Scope.TOURNAMENT);
        GameSetup reloaded = LIBRARY.storedSetup("premiere_ds2", on.toJson());
        assertEquals("true", reloaded.getChosen(GameOptions.FOUR_TWENTY));
        assertNotNull("a later round validates with 420", error(LIBRARY.effectiveFormat(reloaded), deck("7_296")));
        assertNull(error(LIBRARY.effectiveFormat(reloaded), deck()));

        GameSetup old = LIBRARY.storedSetup("premiere_ds2", null);
        assertNull(old.getChosen(GameOptions.FOUR_TWENTY));
        assertNull("no 420 in an old setup means off", error(LIBRARY.effectiveFormat(old), deck("7_296")));
        GameSetup oldJson = LIBRARY.storedSetup("open", GameSetup.of(LIBRARY.getFormat("open"),
                Map.of(GameOptions.DECK_SIZE, "45"), GameOption.Scope.LEAGUE).toJson());
        assertFalse(LIBRARY.effectiveFormat(oldJson) instanceof DeckRulesSwccgFormat);
    }
}
