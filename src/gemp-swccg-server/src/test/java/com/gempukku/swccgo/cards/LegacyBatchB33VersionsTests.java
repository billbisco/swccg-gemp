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
 * Legacy batch B33 (Virtual Blocks 6-9 eye check; ai-team-skills shared/gemp-legacy-cards/versions/batches.json): the
 * release-sheet slips were read by eye. Five card-list texts with typos or a dropped phrase ("Sqaudron", "capacit", "drwn",
 * "from here", "an/or ... weapon_") are PC-E1 (the current cards) and the sheet texts are 22xx originals; Chasm (V) and
 * Abyss (V) keep the Master List errata text and their earlier card-list text is the 22xx original. Four current cards had a
 * wording that no printing has (Leia "deploy are", Antilles Maneuver, Tarkin's Orders, Blockade Support Ship). Images are
 * pinned in cardImageUrlTests.js. 601_63 / 601_204 are the current classes of 1273_35 / 1262_15.
 */
public class LegacyBatchB33VersionsTests {
    private static final Map<String, String> CURRENT_TEXT = Map.ofEntries(
            Map.entry("1261_69", "Adds 1 to power of anything she pilots. Dark Jedi deploy +1 here. May move as a 'react' (one other Rebel present may move with her). While armed with Leia's Sporting Blaster, its weapon destiny draws are +1 and Leia is power +1. Immune to attrition < 3."),
            Map.entry("1261_92", "USED: Cancel Lateral Damage. (Immune to Sense.) OR During opponent's deploy phase, deploy a Rebel of ability < 3 (except an admiral) aboard your starship from Reserve Deck; reshuffle. LOST: Once per game, if opponent has more cards with ability on table than you, draw destiny and retrieve up to that number of Rebels and/or Rebel starships (may take one retrieved card into hand)."),
            Map.entry("1261_49", "USED: If a battle just initiated at a site, target your Imperial of ability = 3 defending. Target's forfeit cannot be reduced, and draws one battle destiny if not able to otherwise. LOST: Use 1 Force to deploy (or retrieve) Eriadu or an Imperial of ability = 3 (except Veers) from Reserve Deck; reshuffle. (Immune to Sense.)"),
            Map.entry("601_204", "Unless Fondor on table, may deploy -3 as a 'react.' May add 3 pilots and 4 passengers. Permanent pilot provides ability of 2."),
            Map.entry("601_63", "USED: ▼ a Rogue Squadron pilot to a location you occupy with a Rogue Squadron pilot. LOST: Cancel an attempt by opponent to target a Rogue Squadron pilot to be captured or excluded from battle. OR If two Rogue Squadron pilots are in battle together, add one battle destiny."),
            Map.entry("1271_45", "Deploy on table. Your Sandwhirl is canceled. At battleground systems where you have a Rebel of ability = 2 piloting a starfighter, and you have no Force drain bonus (except from the system card itself), your Force drains are +1. Your starfighters with a pilot character aboard are immune to attrition < 4. Once per turn, may ▼ Close Air Support. Your Rebel starfighters without astromech capacity (except Falcon or Artoo-Detoo In Red 5) gain \"May add 1 astromech.\""),
            Map.entry("1273_14", "If opponent went first this game, retrieve 1 Force when you play this Interrupt. Add or subtract 1 from opponent's just drawn destiny. OR Activate up to 2 Force. OR ▲ Control. OR Until end of turn, battle destiny draws may not be modified or canceled (unless being redrawn)."),
            Map.entry("1281_18", "If Bring Him Before Me on table, play instead of a Starting Interrupt. ▼ an Endor site and two always-[Immune to Alter] Effects. Deploy on table; may ▲ and stack Son Of Vader here. A Powerful Ally: While Son Of Vader on table, Jedi, Rebels, and Rebel starships lose all immunity to attrition and opponent loses 1 Force at the end of each turn. When Son Of Vader leaves table, stack him here. If Luke crossed over, Your Destiny is lost and may deploy (or persona replace) Son Of Vader from here as if from hand."),
            Map.entry("1282_20", "Deploy on The Hyperdrive Generator's Gone; treat \"4\" on it as \"2.\" Your Force generation at City Outskirts is +1. May deploy systems regardless of your Objective deployment restrictions. Once per game, may ▼ Qui-Gon or Obi-Wan (and/or their matching weapon). (Immune to Alter.)"));

    /** version id -> {current printing id, label, game text} */
    private static final Map<String, String[]> VERSIONS = Map.ofEntries(
            Map.entry("2273_35", new String[]{"1273_35", "", "USED: ▼ a Rogue Squadron pilot to a location you occupy with a Rogue Squadron pilot. LOST: Cancel an attempt by opponent to target a Rogue Squadron pilot to be captured or excluded from battle. OR If two Rogue Squadron pilots are in battle together, add one battle destiny."}),
            Map.entry("2271_45", new String[]{"1271_45", "", "Deploy on table. Your Sandwhirl is canceled. At battleground systems where you have a Rebel of ability = 2 piloting a starfighter, and you have no Force drain bonus (except from the system card itself), your Force drains are +1. Your starfighters with a pilot character aboard are immune to attrition < 4. Once per turn, may ▼ Close Air Support. Your Rebel starfighters without astromech capacity (except Falcon or Artoo-Detoo In Red 5) gain \"May add 1 astromech.\""}),
            Map.entry("2273_14", new String[]{"1273_14", "", "If opponent went first this game, retrieve 1 Force when you play this Interrupt. Add or subtract 1 from opponent's just drawn destiny. OR Activate up to 2 Force. OR ▲ Control. OR Until end of turn, battle destiny draws may not be modified or canceled (unless being redrawn)."}),
            Map.entry("2281_18", new String[]{"1281_18", "", "If Bring Him Before Me on table, play instead of a Starting Interrupt. ▼ an Endor site and two always-[Immune to Alter] Effects. Deploy on table; may ▲ and stack Son Of Vader here. A Powerful Ally: While Son Of Vader on table, Jedi, Rebels, and Rebel starships lose all immunity to attrition and opponent loses 1 Force at the end of each turn. When Son Of Vader leaves table, stack him here. If Luke crossed over, Your Destiny is lost and may deploy (or persona replace) Son Of Vader from here as if from hand."}),
            Map.entry("2282_20", new String[]{"1282_20", "", "Deploy on The Hyperdrive Generator's Gone; treat \"4\" on it as \"2.\" Your Force generation at City Outskirts is +1. May deploy systems regardless of your Objective deployment restrictions. Once per game, may ▼ Qui-Gon or Obi-Wan (and/or their matching weapon). (Immune to Alter.)"}),
            Map.entry("2200_7", new String[]{"1200_7", "", "Plays on table. Substituted battle destinies do not count against battle destiny limits. If all your battle destinies were canceled (and not redrawn) your total battle destiny = 1. Once per game, may play a Defensive Shield from under your Starting Effect."}),
            Map.entry("2200_15", new String[]{"1200_15", "", "Plays on table. Substituted battle destinies do not count against battle destiny limits. If all your battle destinies were canceled (and not redrawn) your total battle destiny = 1. Once per game, may play a Defensive Shield from under your Starting Effect."}));

    private static SwccgCardBlueprintLibrary library;

    @BeforeClass
    public static void setUpClass() {
        library = new SwccgCardBlueprintLibrary();
    }

    @Test
    public void CurrentCardsCarryTheLatestText() {
        assertEquals(9, CURRENT_TEXT.size());
        CURRENT_TEXT.forEach((id, text) -> {
            SwccgCardBlueprint card = library.getSwccgoCardBlueprint(id);
            assertNotNull(id, card);
            assertNull(id, card.getLegacyVersion());
            assertEquals(id, text, card.getGameText());
        });
    }

    @Test
    public void CurrentIdsResolveToTheCurrentClasses() {
        assertEquals(library.getSwccgoCardBlueprint("601_63").getGameText(), library.getSwccgoCardBlueprint("1273_35").getGameText());
        assertEquals(library.getSwccgoCardBlueprint("601_204").getGameText(), library.getSwccgoCardBlueprint("1262_15").getGameText());
    }

    @Test
    public void OlderVersionsAreTheirOwnCards() {
        assertEquals(7, VERSIONS.size());
        VERSIONS.forEach((id, v) -> {
            SwccgCardBlueprint card = library.getSwccgoCardBlueprint(id);
            assertNotNull(id, card);
            LegacyVersion version = card.getLegacyVersion();
            assertNotNull(id, version);
            assertEquals(id, v[0], version.currentPrintingId());
            assertEquals(id, v[1], version.label());
            assertEquals(id, v[2], card.getGameText());
            assertEquals(id, library.getSwccgoCardBlueprint(v[0]).getTitle(), card.getTitle());
        });
    }

    @Test
    public void ReleaseSheetSourcesNameTheSlipNumber() {
        assertEquals("Virtual Block 7.1 release sheet (2010-06-18), card #45", library.getSwccgoCardBlueprint("2271_45").getLegacyVersion().source());
        assertEquals("Virtual Block 8.1 release sheet (2012-05-11), card #00", library.getSwccgoCardBlueprint("2281_18").getLegacyVersion().source());
        assertEquals("PC website card list (2012-07-21)", library.getSwccgoCardBlueprint("2200_7").getLegacyVersion().source());
    }
}
