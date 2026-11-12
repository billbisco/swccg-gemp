package com.gempukku.swccgo.cards.set8.light;

import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.logic.GameUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Rebel Strike Team / Garrison Destroyed text states (docs/legacy-versions.md, "Text states"): 8_78o as printed
 * ("into hand; reshuffle") with its 7 side 8_78o_BACK, and the bare card 8_78 with Decipher's Glossary Supplement
 * (29 January 2002) clarification "into hand from Reserve Deck; reshuffle" ([[Rebel Strike Team / Garrison Destroyed
 * (Errata)]]).
 */
public class Card_8_078_TextStates_Tests {
    private static SwccgCardBlueprintLibrary library;

    @BeforeClass
    public static void load() {
        library = new SwccgCardBlueprintLibrary();
    }

    @Test
    public void RebelStrikeTeamPrintedHasThePrintedText() {
        var card = library.getSwccgoCardBlueprint("8_78o");
        assertEquals("Card8_078o", card.getClass().getSimpleName());
        assertEquals("Deploy Endor system and Rebel Landing Site. While this side up, opponent loses no more than 1 Force from each of your Force drains at Endor locations. Once during each of your deploy phases, may use 2 Force to take one Bunker or Deactivate The Shield Generator into hand; reshuffle. Flip this card if Bunker 'blown away' or if, during your move phase, you control three exterior Endor sites (with two Rebel scouts at each). Place out of play if Endor 'blown away.'", card.getGameText());
        TextState state = card.getTextState();
        assertEquals("8_78", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(1998, 6, 1), state.printedOn());
        assertEquals(LocalDate.of(2002, 1, 29), state.supersededOn());
        assertEquals(GameUtils.getFullName(library.getSwccgoCardBlueprint("8_78")) + " [Printed]", GameUtils.getFullName(card));
    }

    @Test
    public void RebelStrikeTeamPrintedFlipsToItsOwnGarrisonDestroyed() {
        var back = library.getSwccgoCardBlueprintBack("8_78o");
        assertEquals("Card8_078o_BACK", back.getClass().getSimpleName());
        var bareBack = library.getSwccgoCardBlueprintBack("8_78");
        assertEquals(bareBack.getTitle(), back.getTitle());
        assertEquals(bareBack.getGameText(), back.getGameText());
        assertEquals("8_78_BACK", back.getTextState().currentId());
    }

    @Test
    public void RebelStrikeTeamBareCardIsUnchanged() {
        var bare = library.getSwccgoCardBlueprint("8_78");
        var printed = library.getSwccgoCardBlueprint("8_78o");
        assertNull(bare.getTextState());
        assertNull(library.getSwccgoCardBlueprintBack("8_78").getTextState());
        assertEquals(bare.getTitle(), printed.getTitle());
        assertEquals(bare.getUniqueness(), printed.getUniqueness());
        assertEquals("Deploy Endor system and Rebel Landing Site. While this side up, opponent loses no more than 1 Force from each of your Force drains at Endor locations. Once during each of your deploy phases, may use 2 Force to take one Bunker or Deactivate The Shield Generator into hand from Reserve Deck; reshuffle. Flip this card if Bunker 'blown away' or if, during your move phase, you control three exterior Endor sites (with two Rebel scouts at each). Place out of play if Endor 'blown away.'", bare.getGameText());
    }
}
