package com.gempukku.swccgo.game.formats;

import com.gempukku.swccgo.game.DeckInvalidException;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * White-border anthology previews (Bill 2026-11-12): the preview cards carry the text printed on the white-border
 * card (read from the Decipher.com anthology images on wiki.swccg.com, e.g. File:FA-P-D-bobafett.gif), the wiki ids
 * 57_/58_/59_ load them, and they are the same card as their black-border printing for deck counts and bans.
 */
public class AnthologyPreviewCardsTests {
    // {PC id, wiki id, black-border id, printed game text}
    private static final String[][] PREVIEWS = {
            {"105_1", "57_12", "7_12", "Adds 3 to power of any starship he pilots. On Rogue 3, also adds 2 to maneuver and draws one battle destiny if not able to otherwise. May use 2 Force to search your Reserve Deck and take one One More Pass into hand. Shuffle, cut and replace."},
            {"105_2", "57_92", "7_92", "Just after the weapons phase of a battle, you may move any or all of your starships there away, at normal use of the Force. ('Hit' cards must still be lost.)"},
            {"105_3", "57_150", "7_150", "*Replaces 3 X-wings at one location (X-wings go to Used Pile). Permanent pilots aboard provide total ability of 3."},
            {"105_4", "57_167", "7_167", "Adds 2 to power and 1 to maneuver of any starship he pilots. May deploy -1 as a 'react' to same site as a gangster or smuggler. When firing weapons, any 'hit' characters are forfeit = 0. May 'fly' (landspeed = 3). Immune to attrition < 3."},
            {"105_5", "57_303", "7_303", "Permanent pilots aboard are •Darth Vader, •DS-61-2 and •DS-61-3, who provide total ability of 10 and add 9 to total power of •Vader's Custom TIE, •Black 2 and •Black 3."},
            {"105_6", "57_231", "7_231", "Deploy on one of your gangsters or bounty hunters. Once during each of your control phases, if present with an opponent's smuggler or character of ability = 1, may use 3 Force. Opponent must use 5 Force or that character returns to opponent's hand."},
            {"107_1", "59_79", "9_79", "Deploys only at Mon Calamari or any Rebel Base. May add 5 pilots, 6 passengers, 1 vehicle and 3 starfighters. Has ship-docking capability. Permanent pilot aboard provides ability of 2."},
            {"107_2", "58_22", "8_22", "While at your war room, subtracts 1 from each opponent's battle destiny at all system locations and adds 2 to forfeit of all non-unique Rebels. During your deploy phase, may deploy one non-unique Rebel from Reserve Deck and reshuffle."},
            {"107_3", "58_58", "8_58", "If you occupy an Endor or Rebel Base site at the beginning of your deploy phase, deploy up to 5 troopers there from Reserve Deck (those troopers deploy -1) and reshuffle. You may not deploy any other characters or starships this turn."},
            {"107_4", "59_125", "9_125", "Deploy on a Star Destroyer if at least five Executor sites on table. Your TIEs are deploy -1, forfeit +2 and destiny +2. At systems where you have a TIE, your Imperial capital starships are deploy -3 and your battle destiny draws are +1 each. (If on Executor, immune to Alter.)"},
            {"107_5", "57_214", "7_214", "*Ferocity = 4 + destiny. Habitat: Great Pit Of Carkoon. If Sarlacc eats a captive, may retrieve 1 Force for each of your aliens here. Anything eaten by Sarlacc is placed out of play."},
            {"107_6", "59_174", "9_174", "May add 6 pilots, 8 passengers, 2 vehicles and 4 TIEs. Has ship-docking capability. Permanent pilot aboard provides ability of 1. Turbolaser Batteries deploy and fire free here."},
    };

    private static SwccgCardBlueprintLibrary library;
    private static SwccgoFormatLibrary formats;

    @BeforeClass
    public static void setUpClass() {
        library = new SwccgCardBlueprintLibrary();
        formats = new SwccgoFormatLibrary(library);
    }

    @Test
    public void AnthologyPreviewsCarryTheTextPrintedOnTheWhiteBorderCard() {
        for (String[] p : PREVIEWS) {
            SwccgCardBlueprint preview = library.getSwccgoCardBlueprint(p[0]);
            assertNotNull(p[0], preview);
            assertEquals(p[0], p[3], preview.getGameText());
            SwccgCardBlueprint blackBorder = library.getSwccgoCardBlueprint(p[2]);
            assertEquals(p[0], blackBorder.getTitle(), preview.getTitle());
            assertEquals(p[0], blackBorder.getUniqueness(), preview.getUniqueness());
            assertEquals(p[0], blackBorder.getSide(), preview.getSide());
            assertEquals(p[0], blackBorder.getPersonas(), preview.getPersonas());
        }
    }

    @Test
    public void AnthologyPreviewWikiIdsLoadThePreviewCard() {
        for (String[] p : PREVIEWS) {
            assertEquals(p[1], p[0], library.getBaseBlueprintId(p[1]));
            assertEquals(p[1], library.getSwccgoCardBlueprint(p[0]).getClass(), library.getSwccgoCardBlueprint(p[1]).getClass());
        }
    }

    @Test
    public void AnthologyPreviewsAreTheSameCardAsTheirBlackBorderPrinting() {
        DefaultSwccgFormat format = (DefaultSwccgFormat) formats.getFormat("open");
        for (String[] p : PREVIEWS) {
            assertEquals(p[0], p[2], format.sameCardBlueprintId(p[0]));
            assertEquals(p[1], p[2], format.sameCardBlueprintId(p[1]));
            assertEquals(p[2], p[2], format.sameCardBlueprintId(p[2]));
        }
    }

    @Test
    public void AnthologyPreviewWikiIdsAreLegalWhereThePreviewSetIs() throws DeckInvalidException {
        for (String[] p : PREVIEWS) {
            formats.getFormat("open").validateCard(p[1], false);
            formats.getFormat("anything_goes").validateCard(p[1], false);
        }
    }
}
