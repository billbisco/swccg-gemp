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

/**
 * Legacy batch B32 (hidden card-list versions on single-version cards, from the strict text audit of 8 November 2026;
 * ai-team-skills shared/gemp-legacy-cards/versions/batches.json, record legacy-versions-v4.json): 22 families whose 2012
 * PC card-list text (PC-E1) differs from the release-sheet text. The current card carries the card-list text; the
 * release-sheet text is its own 22xx card. Images are pinned in cardImageUrlTests.js. Boba Fett, Prepared Hunter is held
 * (its PC card image prints a third wording).
 */
public class LegacyBatchB32VersionsTests {
    /** printing id -> current game text (latest version) */
    private static final Map<String, String> CURRENT_TEXT = Map.ofEntries(
            Map.entry("1210_109", "During battle with C-3PO, may not be targeted by weapons. While aboard a starfighter, it is power, maneuver, and hyperspeed +3. Unless you have 13 or more cards in hand, opponent may not place those cards in Used Pile."),
            Map.entry("1210_98", "Leia is defense value and forfeit +3. Your senators at battleground sites and characters at Yavin 4 locations are forfeit +2. Once per game, may take non-[Permanent Weapon] Leia into hand from Reserve Deck; reshuffle."),
            Map.entry("1220_150", "Cancel Broken Concentration, The Circle Is Now Complete, or a duel initiated during battle. (Immune to Young Fool.) OR During any draw phase, place a card from hand under your Used Pile to take any one card into hand from Force Pile; reshuffle."),
            Map.entry("1220_62", "Deploy (or retrieve) a device (for free) from Reserve Deck; reshuffle. (Immune to Sense.) OR Cancel Sabotage targeting a device. OR During battle, place your weapon or device present in Used Pile to add 2 to your total power (3 if Vader present)."),
            Map.entry("1220_95", "Deploys free to Cloud City or a site with a scomp link. If a battle was just initiated at same site, may [download] an alien here (free if Lando) as a 'react'. May [download] Cyborg Construct on Lobot."),
            Map.entry("1230_75", "Adds 2 to power of anything he pilots. Deploys -2 to a forest. Luke's weapon destiny draws are +1. At same site, opponent must first use 1 Force to fire a weapon. During opponent's draw phase, if alone on Endor, may retrieve 1 Force. Immune to attrition < 5."),
            Map.entry("1250_59", "Deploy on a site. Nightfall is canceled. While you have 8 or fewer cards in hand, non-unique characters (except clones), vehicles, starships, weapons, and devices are immune to Monnok. Once per turn, may use 2 Force to deploy Jedi Archives or a battleground planet location (except a sector) not on table from Reserve Deck; reshuffle. (Immune to Alter.)"),
            Map.entry("1250_79", "May add 3 pilots and 4 passengers. Permanent pilot provides ability of 2. Opponent must use +2 Force to initiate a battle here. Once per game, may deploy a senator aboard from Reserve Deck; reshuffle."),
            Map.entry("1261_109", "May add 1 pilot. Permanent pilot is •Dash, who provides ability of 3. During battle, if with another T-47 and about to draw battle destiny, may draw two and choose one. Immune to attrition < 4."),
            Map.entry("1261_62", "This character is lost if a Starting Effect on table. Your non-alien characters at sites are power and forfeit = 0. If present at a battleground site, your Force drains at battleground sites where you have two aliens are +1, and whenever you deploy an alien, retrieve 1 Force."),
            Map.entry("1261_83", "Deploy on table. Star Cruisers (except Home One) are deploy -2 (to a maximum of -3), ignore deployment restrictions in their game text, draw one battle destiny if not able to otherwise, and are immune to attrition < 4. Capital Support is canceled. (Immune to Alter.)"),
            Map.entry("1271_19", "Deploy on table (deploys only at start of game) with three different [Vader] Effects from your Reserve Deck face down under here. Once per turn, may deploy a [Vader] Effect from here (never from elsewhere). Limit one [Vader] Effect on table and, when lost, stack it under here. You may cancel your [Vader] Effect at any time. Twisted And Evil: Dark Jedi Presence, Jedi Presence, Revenge Of The Sith and Sith Tests are canceled. When using The Empire's Back to deploy Vader from Reserve Deck, it is a Used Interrupt. If you just drew a [Vader] card for destiny, may take it into hand."),
            Map.entry("1271_9", "Deploy on table. Tallon Roll is canceled. Once per turn, may reveal an unpiloted starfighter from hand to take its matching pilot character into hand from Reserve Deck (or vice versa) and deploy both simultaneously; reshuffle. Your unique (•) TIEs (except Scythe Squadron TIEs) at systems may use 1 Force to relocate (as a regular move) to a system within 3 parsecs. (Immune to Alter.)"),
            Map.entry("1210_103", "Leia's game text may not be canceled. While at opponent's Galactic Senate, may lose 1 Force to place Leia and any character present out of play. Once per battle at her location, may cancel game text of opponent's leader of ability < 5 present for remainder of turn."),
            Map.entry("1220_23", "Deploy on table. Droids are destiny +1. Once per turn, may place the bottom two cards in Lost Pile out of play to deploy from Lost Pile: Forced Servitude, a character weapon, a location, or an Effect that deploys on a location (without 'lose' in game text). (Immune to Alter.)"),
            Map.entry("1220_78", "May add 2 pilots and 4 passengers. Vader deploys -3 aboard. While Vader piloting at a battleground, maneuver +2 and you lose no Force to Uncontrollable Fury or You Must Confront Vader."),
            Map.entry("1220_81", "May add 1 pilot and 8 passengers. Permanent pilot provides ability of 2. During your control phase, may [upload] one Trample or Walker Barrage. Walker Barrage total is +2. Immune to attrition < 4."),
            Map.entry("1240_91", "Deploy on [Virtual Block 4] Hidden Base. Organized Attack, S-Foils, Revolution, Your Destiny (unless you chose Rebel/Rebel Starship), and [Virtual Block 4] Ellorrs Madak are canceled. You may not deploy operatives or [Maintenance] cards. Force generation at Endor system may not be canceled. Unless Leave Them To Me on table, opponent's combat vehicles, Dark Jedi, [Maintenance] cards, and [Permanent Weapon] cards are deploy +2 to Rebel Cell sites. Opponent generates no Force at Hidden Landing Site. (Immune to Alter.)"),
            Map.entry("1250_39", "Never deploys, except to Coruscant. While Yoda here, Force retrieval from your [Virtual Block 5] objective cannot be canceled (except by Secret Plans). Unless Inner Strength on table, your duel destiny and lightsaber combat totals are cumulatively +1."),
            Map.entry("1250_60", "Deploy on Jedi Council Chamber or Yoda's Hut. Once per turn, may use 1 Force to take a card with 'A Jedi's' in title (except A Jedi's Resilience) into hand from Reserve Deck; reshuffle. [Virtual Block 5] Wipe Them Out, All Of Them and opponent's [Sidious] Effects here are cancelled. (Immune to Alter.)"),
            Map.entry("1250_19", "Deploy on Amidala or opponent's senator. During your move phase, unless a Jedi here, your alien bounty hunter present may capture and seize that character. During your control phase, if that character is escorted by an alien, opponent loses 1 Force."),
            Map.entry("1274_1", "During battle, if [Block 4] No Bargain on table, may lose 2 force to cancel a non-[Immune To Sense] interrupt. Once per game during your move phase, may relocate any of your characters here to any site you occupy as a regular move (or vice versa). Immune to attrition < 3."));

    /** version id -> {current printing id, label, game text, printed on} */
    private static final Map<String, String[]> VERSIONS = Map.ofEntries(
            Map.entry("2210_109", new String[]{"1210_109", "", "During battle with C-3PO, may not be targeted by weapons. While aboard a starfighter, it is power, maneuver, and hyperspeed +3. Unless you have 13 or more cards in hand, opponent may not place those cards in Used Pile.", "2009-12-07"}),
            Map.entry("2210_98", new String[]{"1210_98", "", "Leia is defense value and forfeit +3. Your senators at battleground sites and characters at Yavin 4 locations are forfeit +2. Once per game, may take non-[Permanent Weapon] Leia into hand from Reserve Deck; reshuffle.", "2009-12-07"}),
            Map.entry("2220_150", new String[]{"1220_150", "", "Cancel Broken Concentration, The Circle Is Now Complete, or a duel initiated during battle. (Immune to Young Fool.) OR During any draw phase, place a card from hand under your Used Pile to take any one card into hand from Force Pile; reshuffle.", "2009-12-07"}),
            Map.entry("2220_62", new String[]{"1220_62", "", "Deploy (or retrieve) a device (for free) from Reserve Deck; reshuffle. (Immune to Sense.) OR Cancel Sabotage targeting a device. OR During battle, place your weapon or device present in Used Pile to add 2 to your total power (3 if Vader present).", "2009-12-07"}),
            Map.entry("2220_95", new String[]{"1220_95", "", "Deploys free to Cloud City or a site with a scomp link. If a battle was just initiated at same site, may [download] an alien here (free if Lando) as a 'react'. May [download] Cyborg Construct on Lobot.", "2009-12-07"}),
            Map.entry("2230_75", new String[]{"1230_75", "", "Adds 2 to power of anything he pilots. Deploys -2 to a forest. Luke's weapon destiny draws are +1. At same site, opponent must first use 1 Force to fire a weapon. During opponent's draw phase, if alone on Endor, may retrieve 1 Force. Immune to attrition < 5.", "2009-12-07"}),
            Map.entry("2250_59", new String[]{"1250_59", "", "Deploy on a site. Nightfall is canceled. While you have 8 or fewer cards in hand, non-unique characters (except clones), vehicles, starships, weapons, and devices are immune to Monnok. Once per turn, may use 2 Force to deploy Jedi Archives or a battleground planet location (except a sector) not on table from Reserve Deck; reshuffle. (Immune to Alter.)", "2009-12-07"}),
            Map.entry("2250_79", new String[]{"1250_79", "", "May add 3 pilots and 4 passengers. Permanent pilot provides ability of 2. Opponent must use +2 Force to initiate battle here. Once per game, may deploy a senator aboard from Reserve Deck; reshuffle.", "2009-12-07"}),
            Map.entry("2261_109", new String[]{"1261_109", "", "May add 1 pilot. Permanent pilot is •Dash, who provides ability of 3. During battle, if with another T-47 and about to draw a battle destiny, may draw two and choose one. Immune to attrition < 4.", "2009-09-04"}),
            Map.entry("2261_62", new String[]{"1261_62", "", "Lost if a Starting Effect on table. Your non-alien characters are power and forfeit = 0. If present at a battleground site, your Force drains at battleground sites where you have two aliens are +1, and when you deploy an alien, retrieve 1 Force.", "2009-09-04"}),
            Map.entry("2261_83", new String[]{"1261_83", "", "Deploy on table. Star Cruisers (except Home One) may deploy -2 (to a maximum of -3), ignore deployment restrictions in their game text, draw one battle destiny if not able to otherwise, and are immune to attrition < 4. Capital Support is canceled. (Immune to Alter.)", "2009-09-04"}),
            Map.entry("2271_19", new String[]{"1271_19", "", "Deploy on table (only at start of game) with three different [Vader] Effects from Reserve Deck face down under here. Once per turn, may deploy a [Vader] Effect from here (never from elsewhere). Limit one [Vader] Effect on table and, when lost, stack it under here. You may cancel your [Vader] Effect at any time. Twisted And Evil: Dark Jedi Presence, Jedi Presence, Revenge Of The Sith and Sith Tests are canceled. When using The Empire's Back to deploy Vader, it is a Used Interrupt. If you just drew a [Vader] card for destiny, may take it into hand.", "2010-06-18"}),
            Map.entry("2271_9", new String[]{"1271_9", "", "Deploy on table. Tallon Roll is canceled. Once per turn, may reveal an unpiloted starfighter from hand to [upload] its matching pilot character (or vice versa) and deploy both simultaneously. Your unique (•) TIEs (except Scythe Squadron TIEs) at systems may use 1 Force to relocate (as a regular move) to system within 3 parsecs. [Immune to Alter]", "2010-06-18"}),
            Map.entry("2210_103", new String[]{"1210_103", "", "While at opponent's Galactic Senate, Leia's game text may not be canceled and may lose 1 Force to place Leia and any character present out of play. During battle, may cancel game text of a leader of ability < 5 with Leia for remainder of turn.", "2009-12-07"}),
            Map.entry("2220_23", new String[]{"1220_23", "", "Deploy on table. Droids are destiny +1. Once per turn, may place the bottom two cards in Lost Pile out of play to deploy from Lost Pile: Forced Servitude, a character weapon, a location, or an Effect that deploys on a location (without 'lose' in game text). (Immune to Alter.)", "2009-12-07"}),
            Map.entry("2220_78", new String[]{"1220_78", "", "May add 2 pilots and 4 passengers. Vader deploys -3 aboard. While Vader piloting at a battleground, maneuver +2 and you lose no Force to Uncontrollable Fury or You Must Confront Vader.", "2009-12-07"}),
            Map.entry("2220_81", new String[]{"1220_81", "", "May add 1 pilot and 8 passengers. Permanent pilot provides ability of 2. During your control phase, may [upload] one Trample or Walker Barrage. Walker Barrage total is +2. Immune to attrition < 4.", "2009-12-07"}),
            Map.entry("2240_91", new String[]{"1240_91", "", "Deploy on [Virtual Block 4] Hidden Base. Organized Attack, S-Foils, Revolution, Your Destiny (unless you chose Rebel/Rebel Starship), and [Virtual Block 4] Ellorrs Madak are canceled. You may not deploy operatives or [Maintenance] cards. Force generation at Endor system may not be canceled. Unless Leave Them To Me on table, opponent's combat vehicles, Dark Jedi, [Maintenance] cards, and [Permanent Weapon] cards are deploy +2 to Rebel Cell sites. Opponent generates no Force at Hidden Landing Site. (Immune to Alter.)", "2009-12-07"}),
            Map.entry("2250_39", new String[]{"1250_39", "", "Never deploys, except to Coruscant. While Yoda here, Force retrieval from your [Virtual Block 5] objective cannot be canceled (except by Secret Plans). Unless Inner Strength on table, your duel destiny and lightsaber combat totals are cumulatively +1.", "2009-12-07"}),
            Map.entry("2250_60", new String[]{"1250_60", "", "Deploy on Jedi Council Chamber or Yoda's Hut. Once per turn, may use 1 Force to take a card with 'A Jedi's' in title (except A Jedi's Resilience) into hand from Reserve Deck; reshuffle. [Virtual Block 5] Wipe Them Out, All Of Them and opponent's [Sidious] Effects here are canceled. (Immune to Alter.)", "2009-12-07"}),
            Map.entry("2250_19", new String[]{"1250_19", "", "Deploy on Amidala or opponent's senator. During your move phase, unless a Jedi here, your alien bounty hunter present may capture and seize that character. During your control phase, if that character is escorted by an alien, opponent loses 1 Force.", "2009-12-07"}),
            Map.entry("2274_1", new String[]{"1274_1", "", "During battle, if [Block 4] No Bargain on table, may lose 2 Force to cancel a non-[Immune To Sense] Interrupt. Once per game, during your move phase, may relocate any of your characters here to a site you occupy as a regular move (or vice versa). Immune to attrition < 3.", "2011-12-16"}));

    private static SwccgCardBlueprintLibrary library;

    @BeforeClass
    public static void setUpClass() {
        library = new SwccgCardBlueprintLibrary();
    }

    @Test
    public void CurrentCardsCarryTheLatestText() {
        assertEquals(22, CURRENT_TEXT.size());
        CURRENT_TEXT.forEach((id, text) -> {
            SwccgCardBlueprint card = library.getSwccgoCardBlueprint(id);
            assertNotNull(id, card);
            assertNull(id, card.getLegacyVersion());
            assertEquals(id, text, card.getGameText());
        });
    }

    @Test
    public void OlderVersionsAreTheirOwnCards() {
        assertEquals(22, VERSIONS.size());
        VERSIONS.forEach((id, v) -> {
            SwccgCardBlueprint card = library.getSwccgoCardBlueprint(id);
            assertNotNull(id, card);
            LegacyVersion version = card.getLegacyVersion();
            assertNotNull(id, version);
            assertEquals(id, v[0], version.currentPrintingId());
            assertEquals(id, v[1], version.label());
            assertEquals(id, v[2], card.getGameText());
            assertEquals(id, LocalDate.parse(v[3]), version.printedOn());
            assertEquals(id, LocalDate.of(2012, 7, 21), version.supersededOn());
            assertEquals(id, card.getTitle(), library.getSwccgoCardBlueprint(v[0]).getTitle());
            assertEquals(id, card.getSide(), library.getSwccgoCardBlueprint(v[0]).getSide());
        });
    }
}
