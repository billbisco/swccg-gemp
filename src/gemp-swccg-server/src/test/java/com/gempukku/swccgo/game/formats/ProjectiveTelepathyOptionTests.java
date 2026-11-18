package com.gempukku.swccgo.game.formats;

import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.DeckInvalidException;
import com.gempukku.swccgo.game.SwccgFormat;
import com.gempukku.swccgo.game.setup.CardPool;
import com.gempukku.swccgo.game.setup.GameOption;
import com.gempukku.swccgo.game.setup.GameOptions;
import com.gempukku.swccgo.game.setup.GameSetup;
import com.gempukku.swccgo.logic.vo.SwccgDeck;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class ProjectiveTelepathyOptionTests {
    private static final SwccgoFormatLibrary FORMATS = VirtualTableScenario._formatLibrary;
    private static final String OPTION = GameOptions.PROJECTIVE_TELEPATHY;

    private GameSetup setup(String format, String choice) {
        return GameSetup.of(FORMATS.getFormat(format), Map.of(OPTION, choice), GameOption.Scope.CASUAL);
    }

    private SwccgFormat chosen(String format, String choice) {
        return FORMATS.effectiveFormat(setup(format, choice));
    }

    private SwccgDeck deck(String id, int count) {
        var deck = new SwccgDeck("Telepathy option");
        deck.addCard(id);
        while (deck.getCards().size() < count)
            deck.addCard("1_175");
        return deck;
    }

    @Test
    public void ProjectiveTelepathyOptionDefaultsKeepExistingFormatAndExplicitStateBehavior() {
        for (String format : List.of("open", "premiere_ds2", "anything_goes")) {
            assertTrue(setup(format, "format").isDefault());
            var normal = FORMATS.getFormat(format);
            var effective = chosen(format, "format");
            for (String id : List.of("5_149", "5_149o", "5_149o*"))
                assertEquals(normal.resolveCardId(id), effective.resolveCardId(id));
        }
    }

    @Test
    public void ProjectiveTelepathyOptionOverridesBareAndImportedStatesInBothFormats() throws Exception {
        for (String format : List.of("open", "premiere_ds2", "anything_goes")) {
            for (String choice : List.of("printed", "current")) {
                var effective = chosen(format, choice);
                String expected = choice.equals("printed") ? "5_149o" : "5_149";
                for (String imported : List.of("5_149", "5_149o", "005_0149", "5_0149o")) {
                    assertEquals(expected, effective.resolveCardId(imported));
                    var saved = deck(imported, 60);
                    effective.validateDeck(saved);
                    assertEquals(expected, effective.resolveDeck(saved).getCards().getFirst());
                    assertEquals(imported, saved.getCards().getFirst());
                }
                assertEquals(FORMATS.getFormat(format).getName(), effective.getName());
            }
        }
    }

    @Test
    public void ProjectiveTelepathyOptionPreservesModifiersOutsideCardsAndSavedMetadata() {
        var saved = deck("5_149o^*", 60);
        saved.addCardOutsideDeck("5_149o*");
        saved.addCardOutsideDeck("1_175");
        saved.setFormatIndex("saved-index");
        saved.setSourceCollection("default");
        var effective = chosen("premiere_ds2", "current");
        var resolved = effective.resolveDeck(saved);
        assertEquals("5_149^*", resolved.getCards().getFirst());
        assertEquals(List.of("5_149*", "1_175"), resolved.getCardsOutsideDeck());
        assertEquals(List.of("5_149o*", "1_175"), saved.getCardsOutsideDeck());
        assertEquals("5_149o^*", saved.getCards().getFirst());
        assertEquals(saved.getDeckName(), resolved.getDeckName());
        assertEquals(saved.getFormatIndex(), resolved.getFormatIndex());
        assertEquals(saved.getSourceCollection(), resolved.getSourceCollection());
        assertSame(resolved, effective.resolveDeck(resolved));
    }

    @Test
    public void ProjectiveTelepathyOptionNeverChangesUnrelatedCardsOrAdmitsOtherStates() {
        var effective = chosen("open", "printed");
        for (String id : List.of("216_12", "2_45", "2_45o", "1_19o", "601_100"))
            assertEquals(FORMATS.getFormat("open").resolveCardId(id), effective.resolveCardId(id));
        assertThrows(DeckInvalidException.class, () -> effective.validateCard("1_19o", false));
        assertThrows(DeckInvalidException.class, () -> chosen("premiere", "printed").validateCard("5_149", false));
    }

    @Test
    public void ProjectiveTelepathyOptionPreservesDeckSizeAndDeckRuleWrappers() throws Exception {
        var setup = GameSetup.of(FORMATS.getFormat("premiere_ds2"),
                Map.of(OPTION, "current", "deckSize", "80", "noShields", "true"), GameOption.Scope.CASUAL);
        var effective = FORMATS.effectiveFormat(setup);
        assertEquals(80, effective.getRequiredDeckSize());
        assertEquals("5_149", effective.resolveCardId("5_149o"));
        effective.validateDeck(deck("5_149o", 80));
        assertThrows(DeckInvalidException.class, () -> effective.validateDeck(deck("5_149o", 60)));
        assertTrue(effective.getBannedIcons().contains("DEFENSIVE_SHIELD"));
    }

    @Test
    public void ProjectiveTelepathyOptionCannotBypassCustomPoolsOrFamilyBans() throws Exception {
        for (String choice : List.of("printed", "current")) {
            var original = setup("open", choice);
            var excluded = FORMATS.effectiveFormat(original.withPool(CardPool.of(List.of(1), null, null)));
            assertThrows(DeckInvalidException.class, () -> excluded.validateDeck(deck("5_149o", 60)));
            var allowed = FORMATS.effectiveFormat(original.withPool(CardPool.of(List.of(1), List.of("5_149"), null)));
            allowed.validateDeck(deck("5_149o", 60));
            for (String ban : List.of("5_149", "5_149o")) {
                var banned = FORMATS.effectiveFormat(original.withPool(CardPool.of(null, null, List.of(ban))));
                assertThrows(DeckInvalidException.class, () -> banned.validateDeck(deck("5_149", 60)));
                assertThrows(DeckInvalidException.class, () -> banned.validateDeck(deck("5_149o", 60)));
            }
        }
    }

    @Test
    public void ProjectiveTelepathyOptionDoesNotBypassBaseBansOrFamilyCopyLimits() {
        var library = VirtualTableScenario._cardLibrary;
        var base = new DefaultSwccgFormat(library, "restricted", false, false, false, false);
        base.setCode("restricted");
        base.addValidSet(1); base.addValidSet(5);
        base.addRestrictedCard("5_149");
        var mixed = deck("5_149", 59); mixed.addCard("5_149o");
        for (String selected : List.of("5_149", "5_149o")) {
            var restricted = base.withCardTextChoice("5_149", selected);
            assertThrows(DeckInvalidException.class, () -> restricted.validateDeck(mixed));
        }
        base.addBannedCard("5_149");
        var banned = base.withCardTextChoice("5_149", "5_149o");
        assertThrows(DeckInvalidException.class, () -> banned.validateDeck(deck("5_149", 60)));
    }

    @Test
    public void ProjectiveTelepathyOptionPoolAndLibraryCachesStayIsolated() {
        var library = VirtualTableScenario._cardLibrary;
        for (String code : List.of("open", "premiere_ds2", "anything_goes")) {
            var original = FORMATS.getFormat(code);
            String before = original.resolveCardId("5_149");
            var beforeCards = List.copyOf(original.getAllCardBlueprintsValidInFormat());
            for (String choice : List.of("printed", "current")) {
                var effective = chosen(code, choice);
                var pool = effective.getAllCardBlueprintsValidInFormat();
                String selected = choice.equals("printed") ? "5_149o" : "5_149";
                String other = choice.equals("printed") ? "5_149" : "5_149o";
                assertEquals(1, pool.stream().filter(c -> c == library.getSwccgoCardBlueprint(selected)).count());
                assertFalse(pool.contains(library.getSwccgoCardBlueprint(other)));
            }
            assertEquals(before, original.resolveCardId("5_149"));
            assertEquals(beforeCards, original.getAllCardBlueprintsValidInFormat());
        }
    }

    @Test
    public void ProjectiveTelepathyOptionValuesNormalizeAndRoundTripInEveryScope() {
        for (var scope : GameOption.Scope.values()) {
            var setup = GameSetup.of(FORMATS.getFormat("open"), Map.of(OPTION, "PRINTED"), scope);
            assertEquals("printed", GameSetup.fromJson(setup.toJson(), "open").getChosen(OPTION));
            assertEquals("Rules: printed Telepathy", setup.chipsText());
            assertEquals("Printed text", setup.describe(FORMATS.getFormat("open")).get("Rules: Projective Telepathy"));
        }
        for (String invalid : List.of("before-2000", "both", "5_149o", "true"))
            assertThrows(IllegalArgumentException.class, () -> setup("open", invalid));
    }

    @Test
    public void ProjectiveTelepathyOptionStillEnforcesSideAndPrintedDestinyDeckRules() {
        for (String choice : List.of("printed", "current")) {
            var selected = chosen("open", choice);
            var mixed = deck("5_149o", 59); mixed.addCard("1_28");
            assertThrows(DeckInvalidException.class, () -> selected.validateDeck(mixed));
            var fourTwenty = FORMATS.effectiveFormat(GameSetup.of(FORMATS.getFormat("open"),
                    Map.of(OPTION, choice, "fourTwenty", "true"), GameOption.Scope.CASUAL));
            var deck = new SwccgDeck("Destiny boundary"); deck.addCard("5_149o");
            while (deck.getCards().size() < 60) deck.addCard("1_193");
            var error = assertThrows(DeckInvalidException.class, () -> fourTwenty.validateDeck(deck));
            assertTrue(error.getMessage().contains("Projective Telepathy"));
            assertTrue(error.getMessage().contains("printed destiny 3"));
        }
    }

}
