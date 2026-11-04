package com.gempukku.swccgo.game.formats;

import com.gempukku.swccgo.game.DeckInvalidException;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.logic.vo.SwccgDeck;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * swccg-guild/work#141: an Anything Goes deck may hold several Objectives (the player chooses one at game start);
 * other formats still allow one.
 */
public class MultipleObjectivesDeckTests {
    private static SwccgoFormatLibrary formats;

    @BeforeClass
    public static void setUpClass() {
        formats = new SwccgoFormatLibrary(new SwccgCardBlueprintLibrary());
    }

    private static SwccgDeck darkDeckWithTwoObjectives() {
        SwccgDeck deck = new SwccgDeck("two objectives");
        deck.addCard("9_151"); // Bring Him Before Me
        deck.addCard("7_296"); // Carbon Chamber Testing
        for (int i = 0; i < 58; i++)
            deck.addCard("1_194"); // Stormtrooper
        return deck;
    }

    @Test
    public void AnythingGoesAllowsSeveralObjectives() throws DeckInvalidException {
        formats.getFormat("anything_goes").validateDeck(darkDeckWithTwoObjectives());
    }

    @Test
    public void OpenStillAllowsOneObjective() {
        try {
            formats.getFormat("open").validateDeck(darkDeckWithTwoObjectives());
            fail("Open must refuse a deck with two Objectives");
        } catch (DeckInvalidException e) {
            assertEquals("Deck contains more than one Objective", e.getMessage());
        }
    }
}
