package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Legacy batch B06 (Virtual Block 2-3, 11 families; ai-team-skills shared/gemp-legacy-cards/versions/batches.json):
 * the current card carries the latest version's text and every older version is its own 22xx card with its text,
 * version label and current printing. Images (CardMaker recreations of each text) are pinned in cardImageUrlTests.js.
 */
public class LegacyBatchB06VersionsTests {
    private static final Map<String, String> CURRENT_TEXT = Map.ofEntries(
            Map.entry("1220_169", "Deploy on Luke. During battle, opponent may not move from here. May target a character (target loses any immunity to attrition for remainder of turn) or vehicle (with maneuver) for free. Draw destiny. Target hit and forfeit -3 if destiny +2 > defense value."),
            Map.entry("1230_10", "Adds 2 to power of anything he pilots. Adds one battle destiny if with Colonel Dyer or if driving a swoop. May place Lieutenant Grond in Used Pile to cancel a Force drain at a related location."),
            Map.entry("1230_24", "Deploy on a Tatooine site. Once per turn, may place up to 3 cards from hand face down here. You may 'react' to battles initiated against your lone Tusken Raider by deploying (for free) any Tusken Raider(s) and/or Bantha(s) here. (Immune to Alter.)"),
            Map.entry("1230_28", "Deploy on table. Whenever an opponent's alien is just lost, they lose 1 Force. While all of your ability on table is provided by Imperials and Imperial starship pilots, your admirals, generals, moffs, and Emperor are deploy -1 and are forfeit +1. Aliens are deploy +1 and forfeit -1."),
            Map.entry("1230_41", "Cancel Ellors Madak. (Immune to Sense.) OR Use 2 Force to cancel the game text of an opponent's alien for remainder of turn. OR Place an Ewok Catapult in opponent's Used Pile. OR Make an Ewok power and forfeit = 0 for remainder of turn."),
            Map.entry("1230_47", "Find one of your missing characters. OR if your scout is in a battle, activate X Force, where X = the number of scouts you have in that battle. OR add 3 to your total battle destiny during a battle at a site where you have an Imperial and opponent has an alien."),
            Map.entry("1230_53", "Deploy -3 if Death Star II on table. May add 6 pilots, 8 passengers, 2 vehicles, and 4 TIEs. Permanent pilot aboard provides ability of 2. Power and forfeit +1 for each Rebel starship here."),
            Map.entry("1230_76", "Adds 2 to power of anything he pilots. During battle, may use 2 Force to place an opponent's alien (except Jabba or a Rep) present with Luke in owner's Force Pile (opponent may use or lose 3 Force instead). Immune to attrition < 4 (< 7 if with an alien leader)."),
            Map.entry("1230_104", "Cancel an Effect with 'occupation' in title. (Immune to Sense.) OR During your draw phase, if you have a Rebel scout alone at Back Door, draw destiny. Activate or retrieve 5 Force and place this Interrupt on bottom of Lost Pile if destiny < the number of your scouts on table."),
            Map.entry("601_158", "Take an admiral or a non-Jedi general into hand from Reserve Deck; reshuffle. OR Once per game, deploy a war room from Reserve Deck; reshuffle. OR If your admiral or non-[Episode I] general is in battle, may add one battle destiny or prevent opponent from drawing more than one battle destiny (their battle destiny draws may not be canceled)."),
            Map.entry("1230_113", "Heroic. May add 1 pilot. Once per game may place a non-[Immune to Alter] Effect at same system in owner's Used Pile. Corran Horn and Keir Santage deploy -1 aboard; while either piloting, immune to attrition < 4."));

    /** version id -> {current printing id, label, game text} */
    private static final Map<String, String[]> VERSIONS = Map.ofEntries(
            Map.entry("2220_169", new String[]{"1220_169", "", "Deploy on Luke. During battle, opponent may not move from here. Twice per battle, may target a character (target loses immunity to attrition for remainder of turn) or vehicle (with maneuver) for free. Draw destiny. Target hit and forfeit -3 if destiny +2 > defense value."}),
            Map.entry("2220_1169", new String[]{"1220_169", "PC-E1", "Deploy on Luke. During battle, opponent may not move from here. Once per battle, may target a character (target loses immunity to attrition for remainder of turn) or vehicle (with maneuver) for free. Draw destiny. Target hit and forfeit -3 if destiny +2 > defense value."}),
            Map.entry("2230_10", new String[]{"1230_10", "", "Adds 2 to power of anything he pilots. Adds one battle destiny with Colonel Dyer or if driving a swoop. May place Lieutenant Grond in Used Pile to cancel a Force drain at a related location."}),
            Map.entry("2230_24", new String[]{"1230_24", "", "Deploy on a Tatooine site. Once per turn, may place up to 3 cards from hand face down here. If a battle was just initiated against your lone Tusken Raider, may 'react' by deploying (for free) any Tusken Raider(s) and/or Bantha(s) here. (Immune to Alter.)"}),
            Map.entry("2230_28", new String[]{"1230_28", "", "Deploy on table. Whenever an opponent's alien is just lost, they lose 1 Force. While all of your ability on table is provided by Imperials and Imperial starship pilots, Emperor, your admirals, generals, and moffs are deploy -1 and are forfeit +1. Aliens are deploy +1 and forfeit -1."}),
            Map.entry("2230_41", new String[]{"1230_41", "", "Cancel Ellorrs Madak. (Immune to Sense.) OR Use 2 Force to cancel the game text of an opponent's alien for remainder of turn. OR Place an Ewok Catapult in opponent's Used Pile. OR Make an Ewok power and forfeit = 0 for remainder of turn."}),
            Map.entry("2230_47", new String[]{"1230_47", "", "Find one of your missing characters. OR During battle, activate 1 Force for each of your scouts participating. OR During battle, if your Imperial with opponent's alien, your total battle destiny is +3."}),
            Map.entry("2230_53", new String[]{"1230_53", "", "Deploys -3 if Death Star II on table. May add 6 pilots, 8 passengers, 2 vehicles, and 4 TIEs. Permanent pilot aboard provides ability of 2. Power and forfeit +1 for each Rebel starship here."}),
            Map.entry("2230_76", new String[]{"1230_76", "", "Adds 2 to power of anything he pilots. During battle, may use 2 Force to place an opponent's alien (except Jabba or a Rep) present with Luke on owner's Force Pile (opponent may use or lose 3 Force instead). Immune to attrition < 4 (< 7 if with an alien leader)."}),
            Map.entry("2230_104", new String[]{"1230_104", "", "Cancel an Effect with 'occupation' in title. (Immune to Sense.) OR During your draw phase, if you have a Rebel scout alone at Back Door, draw destiny. If destiny < the number of your scouts on table, activate or retrieve 5 Force and place this Interrupt on bottom of Lost Pile."}),
            Map.entry("2230_109", new String[]{"1230_109", "", "▲ an admiral or a non-Jedi general. OR Once per game, ▼ a war room. OR If your admiral or non-[Episode I] general is in battle, may add one battle destiny or prevent opponent from drawing more than one battle destiny (that destiny may not be canceled)."}),
            Map.entry("2230_113", new String[]{"1230_113", "", "Heroic. May add 1 pilot. Once per game may place a non-[Immune to Alter] Effect at same system in owner's Used Pile. Corran Horn and Keir Santage deploy -1 aboard; if either piloting, immune to attrition < 4."}));

    private static SwccgCardBlueprintLibrary library;

    @BeforeClass
    public static void setUpClass() {
        library = new SwccgCardBlueprintLibrary();
    }

    @Test
    public void CurrentCardsCarryTheLatestText() {
        assertEquals(11, CURRENT_TEXT.size());
        CURRENT_TEXT.forEach((id, text) -> {
            SwccgCardBlueprint card = library.getSwccgoCardBlueprint(id);
            assertNotNull(id, card);
            assertNull(id, card.getLegacyVersion());
            assertEquals(id, text, card.getGameText());
        });
    }

    @Test
    public void OlderVersionsAreTheirOwnCards() {
        assertEquals(12, VERSIONS.size());
        VERSIONS.forEach((id, v) -> {
            SwccgCardBlueprint card = library.getSwccgoCardBlueprint(id);
            assertNotNull(id, card);
            LegacyVersion version = card.getLegacyVersion();
            assertNotNull(id, version);
            assertEquals(id, v[0], version.currentPrintingId());
            assertEquals(id, v[1], version.label());
            assertEquals(id, v[2], card.getGameText());
        });
    }

    @Test
    public void LukesBlasterPistolHasTwoOlderVersions() {
        assertEquals("2220_1169", LegacyVersion.versionIdOf("1220_169", 1));
        assertEquals("1220_169", LegacyVersion.currentPrintingIdOf("2220_1169"));
    }

    @Test
    public void AccuserLoreAsPrinted() {
        // The Virtual Block 3 card (Decipher Accuser with the slip pasted on) prints "Patrol ship." at the start of the lore.
        String lore = "Patrol ship. Modified for optimal crisis response time. Veteran crew experienced at monitoring shipping lanes and Imperial port traffic.";
        assertEquals(lore, library.getSwccgoCardBlueprint("1230_53").getLore());
        assertEquals(lore, library.getSwccgoCardBlueprint("2230_53").getLore());
    }
}
