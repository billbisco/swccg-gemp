package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Legacy batch B12 (Virtual Block 7-8, 10 families; ai-team-skills shared/gemp-legacy-cards/versions/batches.json):
 * the current card carries the latest version's text and every older version is its own 22xx card with its text,
 * version label and current printing. Master Kenobi gains a middle version (PC-E2, the 2012 card list text that
 * printed "once per turn," twice); the Master List text is now PC-E3 (the current card). Images are pinned in
 * cardImageUrlTests.js.
 */
public class LegacyBatchB12VersionsTests {
    private static final Map<String, String> CURRENT_TEXT = Map.ofEntries(
            Map.entry("601_54", "While 'communing': you may not deploy [Maintenance] cards or [Episode I] Jedi; Rebels of ability < 5 draw one battle destiny if unable to otherwise and, once per turn, may lose 1 Force (or use 2 Force) to place one just forfeited in Used Pile; once per turn, may deploy from Reserve Deck a Tatooine battleground (or Obi-Wan's Hut); reshuffle."),
            Map.entry("1273_30", "Adds 2 to the power of anything he pilots. Once per turn may deploy from Reserve Deck (or deploy from hand) Disrupter Pistol for free on Wes; reshuffle. May fire Disruptor Pistol for 1 Force. Power and Forfeit +1 when with Hobbie or Wedge. If about to leave table place cards aboard Wes in owner's Used Pile."),
            Map.entry("1273_32", "If Center of Tyranny on Table, deploy on table. Insurrection is lost. Rogue Squadron pilots of ability < 3 are forfeit +1 and if lost from table, may be placed here. During your control phase may use 2 force to take a character here into hand. (Immune to Alter.)"),
            Map.entry("601_41", "If your starting location has exactly 2 [Light Side Force], play instead of a starting interrupt. deploy from Reserve Deck two always [Immune to Alter] Effects; reshuffle. Deploy on table; take into hand and stack a Jedi here from Reserve Deck (that Jedi is 'communing'); reshuffle. \nLuminous Beings: Whenever a Jedi is lost (or placed out of play) from table, may stack it here. Jedi stacked here are considered out of play. You generate +1 Force for each card stacked here. I Can't Believe He's Gone Is canceled. Once per turn, if two cards here, may use 1 Force to look at top card of Reserve Deck, Force Pile, and/or Used Pile; return one card to each deck or pile."),
            Map.entry("1273_34", "Choose up to X of the following in any order, where X = the number of cards stacked on Communing: peek at the top card of any deck or pile: may then shuffle it. OR draw 2 cards from reserve deck. OR retrieve 1 force. OR activate X force. OR your next destiny draw is + X."),
            Map.entry("1274_5", "Unless Imperial Propaganda on table, if you are about to lose Force during opponent's control phase, deploy on table to reduce loss by 4 (ignore other modifiers); opponent may immediately take any one card into hand from Reserve Deck; reshuffle. [Immune to Control]. OR Use 2 Force to deploy on table and stack one just-played interrupt here."),
            Map.entry("1274_7", "Deploy on a battleground. If you occupy, during battle here or at a related location, may place this Effect in Used Pile and choose: Draw up to two cards from Reserve Deck OR Prevent any destinies from being drawn during the power segment of that battle (except battle destiny)."),
            Map.entry("1274_8", "Deploy on Agents Of Black Sun. Mistryl are lost. Once per game may take Secret Plans from Reserve Deck; reshuffle. May use 3 Force to deploy Imperial Square from Reserve Deck; reshuffle, and while Emperor Palpatine there, once per turn, may draw a card from Force Pile (and if you occupy three battlegrounds, opponent loses 1 Force). (Immune to Alter.)"),
            Map.entry("1281_37", "Deploy on Infiltration with three [Scoundrel] cards from Reserve Deck face down under here. Once during your turn, at any time, may deploy a [Scoundrel] Effect from here. If a [Scoundrel] Effect just left table, stack it under here. At the start of your turn, stack all [Scoundrel] Effects under here. Once per turn, may place any card in Lost Pile out of play to deploy a character weapon (except a grenade) from Lost Pile. While two non-droid scoundrels present at battlegrounds, Their Fire Has Gone Out Of The Universe flips and may not flip back. (Immune to Alter.)"),
            Map.entry("1283_21", "Deploy on [Virtual Block 4] Hidden Base; you must choose alien/[Independent] starship. You may not deploy Ellorrs Madak, Revolution or operatives. Your Destiny is canceled. Opponent generates no Force at Hidden Landing Site. [Virtual Block 1] Yerka Mig may deploy on a Rebel Cell Site, may be placed in Used Pile if it was just lost, and you may retrieve Force with it only once per turn. While Systems Will Slip Through Your Fingers on table, attrition against opponent is +1 for each Rebel Cell site you occupy with an alien. (Immune to Alter.)"));

    /** version id -> {current printing id, label, game text} */
    private static final Map<String, String[]> VERSIONS = Map.ofEntries(
            Map.entry("2273_1027", new String[]{"1273_27", "", "While 'communing': you may not deploy [Episode I] Jedi; Rebels of ability < 5 draw one battle destiny if unable to otherwise and, once per turn, may place one just forfeited in Used Pile; Once per turn, may deploy from Reserve Deck a Tatooine battleground (or Obi-Wan's Hut); reshuffle."}),
            Map.entry("2273_27", new String[]{"1273_27", "PC-E1", "While 'communing': you may not deploy [Maintenance] cards or [Episode I] Jedi; Rebels of ability < 5 draw one battle destiny if unable to otherwise and, once per turn, may place one just forfeited in Used Pile; Once per turn, may deploy from Reserve Deck a Tatooine battleground (or Obi-Wan's Hut); reshuffle."}),
            Map.entry("2273_2027", new String[]{"1273_27", "PC-E3", "While 'communing': you may not deploy [Maintenance] cards or [Episode I] Jedi; Rebels of ability < 5 draw one battle destiny if unable to otherwise and, once per turn, once per turn, may lose 1 Force (or use 2 Force) to place one just forfeited in Used Pile; once per turn, may deploy from Reserve Deck a Tatooine battleground (or Obi-Wan's Hut); reshuffle."}),
            Map.entry("2273_30", new String[]{"1273_30", "", "[Pilot] 2. Once per turn, may [download] (or deploy from hand) Disruptor Pistol for free on Wes. May fire Disruptor Pistol for 1 Force. Power and forfeit +1 while with Hobbie or Wedge. If about to leave table, place cards on Wes in owner's Used Pile."}),
            Map.entry("2273_32", new String[]{"1273_32", "", "If Center Of Tyranny on table, deploy on table. Insurrection is lost. Rogue Squadron pilots of ability < 3 are forfeit +1 and, if just lost from table, may be placed here. During your control phase, may use 2 Force to take any character from here into hand. [Immune to Alter.]"}),
            Map.entry("2273_33", new String[]{"1273_33", "", "If your starting location has exactly two [Light Side], play instead of a Starting Interrupt. [download] two always [Immune to Alter] Effects. Deploy on table; [upload] and stack a Jedi here. Luminous Beings: Whenever a Jedi is lost (or placed out of play) from table, may stack it here. Jedi stacked here are 'communing' and considered out of play. You generate +1 Force for each card stacked here. Once per turn, if two cards here, may use 1 Force to look at top card of Reserve Deck, Force Pile, and/or Used Pile; return one card to each deck or pile."}),
            Map.entry("2273_1033", new String[]{"1273_33", "PC-E1", "If your starting location has exactly two [Light Side], play instead of a Starting Interrupt. [download] two always [Immune to Alter] Effects. Deploy on table; [upload] and stack a Jedi here. Luminous Beings: Whenever a Jedi is lost (or placed out of play) from table, may stack it here. Jedi stacked here are 'communing' and considered out of play. You generate +1 Force for each card stacked here. I Can't Believe He's Gone is canceled. Once per turn, if two cards here, may use 1 Force to look at top card of Reserve Deck, Force Pile, and/or Used Pile; return one card to each deck or pile."}),
            Map.entry("2273_34", new String[]{"1273_34", "", "Choose up to X of the following in any order, where X = number of cards stacked on Communing: Peek at the top card of any deck or pile; may then shuffle it. OR Draw 2 cards from Reserve Deck. OR Retrieve 1 Force. OR Activate X Force. OR Your next destiny draw this turn is +X."}),
            Map.entry("2274_5", new String[]{"1274_5", "", "Unless Imperial Propaganda on table, if you are about to lose Force during opponent's control phase, deploy on table to reduce loss by 4 (ignore other modifiers); opponent may [upload] any one card. [Immune to Control.] OR Use 2 Force to deploy on table and stack a just-played Interrupt here."}),
            Map.entry("2274_7", new String[]{"1274_7", "", "Deploy on a battleground. If you occupy, during battle here or at a related location, may place this Effect in Used Pile and choose: Draw up to two cards from Reserve Deck OR Prevent opponent from drawing any destinies during the power segment of that battle (except battle destiny)."}),
            Map.entry("2274_8", new String[]{"1274_8", "", "Deploy on Agents Of Black Sun. Mistryl are lost. Once per game, may [upload] Secret Plans. May use 3 Force to [download] Imperial Square, and once per turn, if Emperor Palpatine there, may draw a card from Force Pile (and if you occupy three battlegrounds, opponent loses 1 Force). [Immune to Alter.]"}),
            Map.entry("2281_37", new String[]{"1281_37", "PC-E4", "Deploy on Infiltration with three [Scoundrel] cards from Reserve Deck face down under here. Once during your turn, at any time, may deploy a [Scoundrel] Effect from here. If a [Scoundrel] Effect just left table, stack it under here. At the start of your turn, stack all [Scoundrel] Effects under here (even if inactive). Once per turn, may place any card in Lost Pile out of play to deploy a character weapon (except a grenade) from Lost Pile. While two non-droid scoundrels present at battlegrounds, Their Fire Has Gone Out Of The Universe flips and may not flip back. (Immune to Alter.)"}),
            Map.entry("2283_21", new String[]{"1283_21", "", "Deploy on [Virtual Block 4] Hidden Base; you must choose alien/[Independent] starship. You may not deploy Ellorrs Madak, Revolution or operatives. Your Destiny is canceled. Opponent generates no Force at Hidden Landing Site. [Virtual Block 1] Yerka Mig may deploy on a Rebel Cell Site, may be placed in Used Pile if it was just canceled, and you may retrieve Force with it only once per turn. While Systems Will Slip Through Your Fingers on table, attrition against opponent is +1 for each Rebel Cell site you occupy with an alien. (Immune to Alter.)"}));

    private static SwccgCardBlueprintLibrary library;

    @BeforeClass
    public static void setUpClass() {
        library = new SwccgCardBlueprintLibrary();
    }

    @Test
    public void CurrentCardsCarryTheLatestText() {
        assertEquals(10, CURRENT_TEXT.size());
        CURRENT_TEXT.forEach((id, text) -> {
            SwccgCardBlueprint card = library.getSwccgoCardBlueprint(id);
            assertNotNull(id, card);
            assertNull(id, card.getLegacyVersion());
            assertEquals(id, text, card.getGameText());
        });
    }

    @Test
    public void OlderVersionsAreTheirOwnCards() {
        assertEquals(13, VERSIONS.size());
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
    public void MasterKenobiHasFourVersions() {
        assertEquals("2273_2027", LegacyVersion.versionIdOf("1273_27", 2));
        assertEquals("1273_27", LegacyVersion.currentPrintingIdOf("2273_2027"));
        // original (2011-06-03) -> PC-E1 CBT errata (2011-06-23) -> PC-E2 card list (2012-07-21) -> PC-E3 Master List (2013-04-27, current card)
        assertEquals(LocalDate.of(2011, 6, 22), library.getSwccgoCardBlueprint("2273_1027").getLegacyVersion().supersededOn());
        assertEquals(LocalDate.of(2012, 4, 20), library.getSwccgoCardBlueprint("2273_27").getLegacyVersion().supersededOn());
        assertEquals(LocalDate.of(2012, 7, 21), library.getSwccgoCardBlueprint("2273_2027").getLegacyVersion().printedOn());
        assertEquals(LocalDate.of(2013, 2, 25), library.getSwccgoCardBlueprint("2273_2027").getLegacyVersion().supersededOn());
        assertEquals("Master Kenobi", library.getSwccgoCardBlueprint("2273_2027").getTitle());
    }

    @Test
    public void ScoundrelsLuckHasNoLore() {
        // The Virtual Block 8.1 slip, the PC card image and the Master List print an empty lore box.
        for (String id : new String[]{"1281_37", "2281_37"}) {
            String lore = library.getSwccgoCardBlueprint(id).getLore();
            assertTrue(id, lore == null || lore.isEmpty());
        }
    }
}
