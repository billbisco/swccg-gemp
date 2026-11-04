package com.gempukku.swccgo.game.formats;

import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.DeckInvalidException;
import com.gempukku.swccgo.game.SwccgFormat;
import com.gempukku.swccgo.game.setup.GameOption;
import com.gempukku.swccgo.game.setup.GameOptions;
import com.gempukku.swccgo.game.setup.GameSetup;
import com.gempukku.swccgo.logic.vo.SwccgDeck;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

/**
 * swccg-guild/work#142/#144: deck rules a table's game options add. "Shields: none" refuses decks with Defensive
 * Shields (as Open (no shields)); "Starting: Objective optional" lets a deck hold several Objectives.
 */
public class DeckRuleOptionsTests {
    private static final SwccgoFormatLibrary LIBRARY = VirtualTableScenario._formatLibrary;

    private static SwccgFormat table(String format, Map<String, String> options) {
        return LIBRARY.effectiveFormat(GameSetup.of(LIBRARY.getFormat(format), options, GameOption.Scope.CASUAL));
    }

    private static SwccgDeck darkDeck(int stormtroopers, String... extra) {
        SwccgDeck deck = new SwccgDeck("deck");
        for (String card : extra)
            deck.addCard(card);
        for (int i = 0; i < stormtroopers; i++)
            deck.addCard("1_194"); // Stormtrooper
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

    @Test
    public void ShieldsNoneRefusesDecksWithShieldsAndAllowsTheStartingEffect() {
        SwccgDeck withShield = darkDeck(59, "13_69"); // Fear Is My Ally (Starting Effect)
        withShield.addCardOutsideDeck("13_51");      // a Dark Side Defensive Shield
        SwccgDeck noShield = darkDeck(59, "13_69");

        assertNull("Open allows shields", error(table("open", Map.of()), withShield));
        String refused = error(table("open", Map.of(GameOptions.NO_SHIELDS, "true")), withShield);
        assertNotNull(refused);
        assertTrue(refused, refused.contains("Shields: none"));
        assertNull("the Starting Effect is still allowed", error(table("open", Map.of(GameOptions.NO_SHIELDS, "true")), noShield));
        // the same rule as the Open (no shields) format
        assertNotNull(error(LIBRARY.getFormat("open_no_shields"), withShield));
        assertNull(error(LIBRARY.getFormat("open_no_shields"), noShield));
    }

    @Test
    public void ShieldsNoneKeepsTheFormatAndTheChosenDeckSize() {
        SwccgFormat format = table("open", Map.of(GameOptions.NO_SHIELDS, "true", GameOptions.DECK_SIZE, "45"));
        assertEquals("open@45", format.getCode());
        assertEquals("Open", format.getName());
        assertTrue(format.getBannedIcons().contains("DEFENSIVE_SHIELD"));
        assertNull(error(format, darkDeck(45)));
        assertNotNull("the chosen size still applies", error(format, darkDeck(60)));
        assertFalse("not offered where shields are already banned",
                GameOptions.get(GameOptions.NO_SHIELDS).isAvailable(LIBRARY.getFormat("open_no_shields")));
    }

    @Test
    public void ObjectiveOptionalLetsADeckHoldSeveralObjectives() {
        SwccgDeck twoObjectives = darkDeck(58, "9_151", "7_296"); // Bring Him Before Me, Carbon Chamber Testing
        assertNotNull("Open allows one Objective", error(table("open", Map.of()), twoObjectives));
        assertNull(error(table("open", Map.of(GameOptions.OBJECTIVE_OPTIONAL, "true")), twoObjectives));
        assertNull("Anything Goes allows them by itself", error(table("anything_goes", Map.of()), twoObjectives));
    }

    @Test
    public void OpenNoShieldsIsOpenWithShieldsNone() {
        SwccgFormat alias = LIBRARY.getFormat("open_no_shields");
        assertNotNull("the old code keeps working", alias);
        assertEquals("open", alias.getCode());
        assertTrue(alias.getBannedIcons().contains("DEFENSIVE_SHIELD"));
        // a stored table, league or game with the old code gets the option
        GameSetup stored = LIBRARY.storedSetup("open_no_shields", null);
        assertEquals("open", stored.getFormatCode());
        assertEquals("true", stored.getChosen(GameOptions.NO_SHIELDS));
        assertTrue("the chip says so", stored.chipsText().contains("No shields"));
        // the deck builder's card filter leaves shields out, as before
        assertThrows(DeckInvalidException.class, () -> alias.validateCard("13_51", false));
        try {
            alias.validateCard("13_69", false); // the Starting Effect is fine
        } catch (DeckInvalidException e) {
            fail(e.getMessage());
        }
        assertNull("no longer a separate format in the hall", LIBRARY.getHallFormats().get("open_no_shields"));
    }

    @Test
    public void ShieldsNoPlayLimitLiftsTheOutsideDeckCap() {
        // Fear Is My Ally (an original Starting Effect) allows 10 shields outside the deck
        SwccgDeck eleven = darkDeck(59, "13_69");
        for (String shield : new String[]{"13_51", "13_52", "13_54", "13_61", "13_63", "13_66", "13_68", "13_72", "13_78", "13_81", "13_84"})
            eleven.addCardOutsideDeck(shield);
        assertNotNull("11 shields are over the cap", error(table("open", Map.of()), eleven));
        assertNull(error(table("open", Map.of(GameOptions.SHIELDS_NO_PLAY_LIMIT, "true")), eleven));
    }

    @Test
    public void NoOptionsIsTheLibraryFormatItself() {
        assertSame(LIBRARY.getFormat("open"), table("open", Map.of()));
    }
}
