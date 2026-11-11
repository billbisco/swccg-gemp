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
 * Legacy batch B09 (Virtual Block 5-6, 11 families; ai-team-skills shared/gemp-legacy-cards/versions/batches.json):
 * the current card carries the latest version's text and every older version is its own 22xx card with its text,
 * version label and current printing. Darth Vader, Betrayer Of The Jedi gains a middle version (PC-E1, the 2012 card
 * list text without "here"). Images (CardMaker recreations of each text) are pinned in cardImageUrlTests.js.
 */
public class LegacyBatchB09VersionsTests {
    private static final Map<String, String> CURRENT_TEXT = Map.ofEntries(
            Map.entry("601_164", "Deploy on a Jedi. May add 1 to Force drain at opponent's site where present. May target a character or creature for free. Draw two destiny. If total destiny > defense value, target hit and may not be used to satisfy attrition."),
            Map.entry("1261_10", "[Pilot] 3. The Circle Is Now Complete is canceled. While present with [Reflections III] Obi-Wan or a Jedi Council Member, may add one battle destiny, lightsabers here may not be 'swung' twice, and their immunity to attrition is canceled. Immune to attrition < 5."),
            Map.entry("1261_16", "Opponent's characters are deploy +1 to same mobile site. At battlegrounds where you have a unique (•) and non-unique Imperial present (and no other bonus to your Force drain), your Force drains are +1. Tactical Support is a Used Interrupt."),
            Map.entry("1261_34", "Deploy on table. Once per turn, may deploy a non-unique starship (or vehicle) site from Reserve Deck; reshuffle. During battle where you have a starship or vehicle, characters at related starship (or vehicle) sites cumulatively add 2 to your total power and may be forfeited to reduce battle damage or attrition. When a starship (or vehicle) site leaves table, place it and all cards there in owner's Used Pile. (Immune to Alter.)"),
            Map.entry("1261_35", "Deploy on table with 5 cards from outside your deck face down under here. Twice per game, may play a Defensive Shield from here (as if from hand). If This Deal Is Getting Worse All The Time on table, for remainder of game lightsabers may not add to Force Drains or 'swing' more than once per battle. Cloud City Occupation is canceled."),
            Map.entry("1261_39", "Deploy on table. A Jedi's Resilience may not be played during move phases. The Circle Is Now Complete or Vader's Obsession may be played during any move phase to initiate an epic duel as follows: If Vader present with specified target, each player draws two destiny (if armed with a lightsaber, each destiny is +1); add ability. Loser (lowest total) is lost, losing player loses 2 Force, and winning player may retrieve 2 Force."),
            Map.entry("1261_50", "USED: If you just drew a unique (•) Imperial starship for destiny, take that starship into hand to cancel and redraw the destiny. LOST: Use 3 Force to retrieve a unique (•) Imperial starship into hand."),
            Map.entry("1261_65", "Adds 3 to power of anything he pilots. While piloting a freighter at a battleground system, adds 1 to attrition against opponent here and their starships here may not 'cloak.' Opponent loses no more than 3 Force to your Force drains at same system."),
            Map.entry("1261_70", "Old Ben is a Used Interrupt and may target this site even if not a Tatooine site. Dark Jedi may not 'swing' lightsabers more than once per battle here. When leaving table, place all your cards on Obi-Wan in Used Pile. Immune to attrition < 5."),
            Map.entry("601_52", "Adds 3 to power of anything he pilots.  While piloting Red 5 or Rogue 1 (or armed with Luke's Lightsaber), may add one destiny to total power or attrition, and I Have You Now is canceled.  When Luke leaves table, place all your cards on him in Used Pile.  Immune to attrition < 4."),
            Map.entry("1261_80", "Deploy on table with 5 cards from outside your deck face down under here. Twice per game, may play a Defensive Shield from here (as if from hand). If Quiet Mining Colony or Independent Operation on table, lightsabers may not add to Force drains or 'swing' more than once per battle. Cloud City Celebration is canceled."));

    /** version id -> {current printing id, label, game text} */
    private static final Map<String, String[]> VERSIONS = Map.ofEntries(
            Map.entry("2250_85", new String[]{"1250_85", "", "Deploy on a Jedi. May add 1 to Force drain at opponent's site where present. May target a character or creature for free. Draw two destiny. Target hit, and may not be used to satisfy attrition If total destiny > defense value."}),
            Map.entry("2261_10", new String[]{"1261_10", "", "[Pilot] 3. The Circle Is Now Complete is canceled. While present with [Reflections III] Obi-Wan or a Jedi Council Member, lightsabers here may not be 'swung' twice, their immunity to attrition is canceled and you may add one battle destiny. Immune to attrition < 5."}),
            Map.entry("2261_1010", new String[]{"1261_10", "PC-E1", "[Pilot] 3. The Circle Is Now Complete is canceled. While present with [Reflections III] Obi-Wan or a Jedi Council Member, lightsabers may not be 'swung' twice, their immunity to attrition is canceled and you may add one battle destiny. Immune to attrition < 5."}),
            Map.entry("2261_16", new String[]{"1261_16", "", "Opponent's characters are deploy +1 to same mobile site. At battlegrounds where you have a unique (•) and non-unique Imperial present (and no other Force drain bonus), your Force drains are +1. Tactical Support is a Used Interrupt."}),
            Map.entry("2261_34", new String[]{"1261_34", "", "Deploy on table. During battle involving your starship (or vehicle), it is power +2 for each character at its related sites (those characters may be forfeited to reduce battle damage and attrition). Once per turn, may ▼ a non-unique starship (or vehicle) site (if such sites leave table, place them and all your cards there in Used Pile). (Immune to Alter.)"}),
            Map.entry("2261_1034", new String[]{"1261_34", "PC-E1", "Deploy on table. During battle involving your starship (or vehicle), it is power +2 for each of your characters at its related sites (they may be forfeited to reduce battle damage and attrition). Once per turn, may ▼ a non-unique starship (or vehicle) site (if such sites leave table, place them and all your cards there in Used Pile). (Immune to Alter.)"}),
            Map.entry("2261_35", new String[]{"1261_35", "", "Deploy on table with 5 cards from outside your deck face down under here. Twice per game, may play a Defensive Shield from here (as if from hand). If This Deal Is Getting Worse All The Time on table, for remainder of game lightsabers may not add to Force drains. Cloud City Occupation is canceled."}),
            Map.entry("2261_39", new String[]{"1261_39", "", "Deploy on table. A Jedi's Resilience may not be played during move phases. The Circle Is Now Complete or Vader's Obsession may be played during any move phase to initiate an epic duel as follows: If Vader present with specified target, each player draws two destiny (if armed with a lightsaber, each destiny is +1); add ability. Loser (lower total) is lost, losing player loses 2 Force, and winning player may retrieve 2 Force."}),
            Map.entry("2261_50", new String[]{"1261_50", "", "USED: If you just drew a unique (•) Imperial starship for destiny, take that starship into hand to cancel and redraw that destiny. LOST: Use 3 Force to retrieve a unique (•) Imperial starship into hand."}),
            Map.entry("2261_65", new String[]{"1261_65", "", "[Pilot] 3. While piloting a freighter at a battleground system, adds 1 to attrition against opponent and their starships here may not 'cloak.' Opponent loses no more than 3 Force to your Force drains at same system."}),
            Map.entry("2261_1065", new String[]{"1261_65", "PC-E2", "Adds 3 to power of anything he pilots. When piloting a freighter at a battleground system, adds 1 to attrition against opponent here and their starships here may not 'cloak.' Opponent loses no more than 3 Force to your Force drains at same system."}),
            Map.entry("2261_70", new String[]{"1261_70", "", "Old Ben is a Used Interrupt and may target this site like a Tatooine site. Dark Jedi may not 'swing' lightsabers more than once per battle here. When leaving table, place all your cards on Obi-Wan in Used Pile. Immune to attrition < 5."}),
            Map.entry("2261_71", new String[]{"1261_71", "", "[Pilot] 3. While piloting Red 5 or Rogue 1 (or armed with Luke's Lightsaber), may add one destiny to total power or attrition, and I Have You Now is canceled. When leaving table, place all your cards on Luke in Used Pile. Immune to attrition < 4."}),
            Map.entry("2261_80", new String[]{"1261_80", "", "Deploy on table with 5 cards from outside your deck face down under here. Twice per game, may play a Defensive Shield from here (as if from hand). If Quiet Mining Colony or Independent Operation on table, lightsabers may not add to Force drains. Cloud City Celebration is canceled."}));

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
        assertEquals(14, VERSIONS.size());
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
    public void DarthVaderBetrayerOfTheJediHasThreeVersions() {
        assertEquals("2261_1010", LegacyVersion.versionIdOf("1261_10", 1));
        assertEquals("1261_10", LegacyVersion.currentPrintingIdOf("2261_1010"));
        // the original is superseded by the 2012 card list; the card-list text by the 2013 Master List (current card)
        assertEquals(LocalDate.of(2012, 7, 21), library.getSwccgoCardBlueprint("2261_10").getLegacyVersion().supersededOn());
        assertEquals(LocalDate.of(2012, 7, 21), library.getSwccgoCardBlueprint("2261_1010").getLegacyVersion().printedOn());
        assertEquals(LocalDate.of(2012, 10, 19), library.getSwccgoCardBlueprint("2261_1010").getLegacyVersion().supersededOn()); // PC forum errata 2012-10-19 (Batch B36)
    }

    @Test
    public void JabbaThisIsYourLastChanceHasNoLore() {
        // The Virtual Block 6 slip and the PC card image print an empty lore box.
        String lore = library.getSwccgoCardBlueprint("1261_80").getLore();
        assertTrue(lore == null || lore.isEmpty());
        String vlore = library.getSwccgoCardBlueprint("2261_80").getLore();
        assertTrue(vlore == null || vlore.isEmpty());
    }
}
