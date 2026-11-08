package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Current Legacy cards whose only "later version" was the 2012-13 PC website card list, which spelled out or dropped
 * icons (audit 2026-11-08; Bill: "If there are no changes, there is no errata"). Each current card shows the latest
 * real published text (release sheet / Master List wording, icons in GEMP notation), not the card-list wording.
 * The 14 current cards switched by r16/r17 have their own text tests; Dark Waters is in Card_1261_033_Tests.
 */
public class LegacyIconLossCurrentTextTests {
    private static final Map<String, String> LATEST_TEXT = Map.ofEntries(
            Map.entry("1210_108", "Opponent activates no Force from Beru Stew. While present with Lars' Vaporator: during your control phase, may use 3 Force to exchange a card from hand with a 'crop' card; and cards with 'lightsaber' in title may not deploy here."),
            Map.entry("1210_154", "USED: [upload] (or retrieve) a device. (Immune to Sense.) STARTING: If your starting location was a Tatooine location, [download] a farm, two devices, and two Effects that are always [Immune to Alter]. Place this Interrupt in Reserve Deck."),
            Map.entry("1210_158", "[upload] a device or non-lightsaber weapon. OR If opponent just initiated a battle or Force drain at a system or sector, [download] (as a 'react') an unpiloted Rebel starfighter and a pilot of ability < 3 (simultaneously). (Immune to Sense.)"),
            Map.entry("1210_57", "[upload] a device or non-lightsaber weapon. OR If opponent just initiated a battle or Force drain at a system or sector, [download] (as a 'react') an unpiloted TIE and a pilot of ability < 3 (simultaneously). (Immune to Sense.)"),
            Map.entry("1210_64", "USED: [upload] a non-[Droid with Presence], non-[Permanent Weapon] droid. OR Place a card from hand on Used Pile. STARTING: [download] two always [Immune to Alter] Effects. If you have only one location (with < 3 [Dark Side Force]) on table, may [download] Central Office or Mos Eisley. Place this Interrupt in hand."),
            Map.entry("1220_129", "Deploy on table. Dack Ralter and Wes Janson have [Pilot] 2. During your control phase, may [upload] one Attack Pattern Delta, Desperate Tactics, Lucky Shot, One More Pass, Rapid Fire, or a vehicle weapon. May lose 2 Force to cancel Trample or Walker Barrage. (Immune to Alter.)"),
            Map.entry("1220_133", "Deploy on table. Rebel scouts (except pilots) are troopers. Rebel troopers have armor = 4 and are immune to Physical Choke. Whenever you deploy a Rebel trooper, may immediately [download] Blaster Rifle on that character. (Immune to Alter.)"),
            Map.entry("1220_153", "Unless you have a senate majority, if opponent just [upload] or [download] a character (except with an Effect with 'matching' in game text) or just drew a card from Reserve Deck using a character's game text, they must place 2 other cards from hand on Used Pile (if possible)."),
            Map.entry("1220_20", "Habitat: Hoth sites. Deploys only to a marker site. Wampas are [Selective Creature]. Other wampas are ferocity and defense value +2. Once per game, may [download] Wampa Cave."),
            Map.entry("1220_44", "If you just deployed a TIE/sa, deploy on a unique (•) TIE/sa. Bombing Run is [Immune to Control]. May place this card in Used Pile to [upload] (or, once per game, retrieve) Bombing Run or ComScan Detection; opponent loses 1 Force. (Immune to Control.)"),
            Map.entry("1220_57", "Once per turn, [upload] Crash Landing or Weapon Malfunction. OR If you have a piloted AT-AT on Hoth, target artillery weapon at a related site is lost. (Immune to Sense.)"),
            Map.entry("1220_66", "Unless you have a senate majority, if opponent just [upload] or [download] a character (except with an Effect with 'matching' in game text) or just drew a card from Reserve Deck using a character's game text, they must place 2 other cards from hand on Used Pile (if possible)."),
            Map.entry("1220_97", "May deploy or move as a 'react' to same site as an Imperial. Adds one battle destiny if with Han or Vader. During your deploy phase, may [download] one Leia Of Alderaan, Reflection, or matching blaster on Leia. Immune to attrition < 4."),
            Map.entry("1230_2", "Opponent's non-Jedi characters are deploy +1 to [Jabba's Palace] sites. Once during your control phase, may place three cards from hand in Used Pile to reveal top card of Reserve Deck; if it is an alien, take it into hand."),
            Map.entry("1230_21", "[Pilot] 3. Wooof may deploy with any cruiser using Combat Response. When deployed, may draw top card from Reserve Deck. During your deploy phase, if piloting Jabba's Space Cruiser, may [download] Jabba aboard (for free)."),
            Map.entry("1230_3", "[Pilot] 2, 3: any starfighter. If your starship participating in battle is about to be excluded (even if that starship may not participate in battle) or made lost by opponent, you may choose for it to be 'hit' instead."),
            Map.entry("1230_32", "Deploy on a system. Your TIEs may relocate (as a regular move) to a system from 0 to 3 parsecs away. For each battleground system you control with two TIEs piloted by Imperials, may add 3 to your total power in battles. Opponent's Force drain is +3 here. (Immune to Alter.)"),
            Map.entry("1230_40", "USED: Creature attacks at pits are immune to At Peace and Yarna d'al' Gargan until end of your next turn. (Immune to Sense.) OR Cancel an attempt to target a rancor with a character weapon. OR Use 1 Force (2 if a rancor on table) to [download] Nal Hutta. STARTING: If Great Pit Of Carkoon on table, [download] an Effect that is always [Immune to Alter] and choose: [download] Rancor Pit and a rancor there, OR [download] Power Of The Hutt and Sarlacc. Place this Interrupt in hand."),
            Map.entry("1230_45", "USED: [download] Empire's New Order. OR If Vader just won a battle at same site as an alien, place that alien out of play. LOST: If a battle was just initiated where you have a character weapon card, place the top card of your Lost Pile on top of Reserve Deck."),
            Map.entry("1230_46", "If opponent just initiated battle at a site where their total power is greater than yours, may use 1 Force (or cause opponent to use 1 Force) to relocate one of your characters or vehicles to that site from a related location."),
            Map.entry("1230_5", "[Pilot] 2. While Undercover, opponent's Force drains at related locations are -1. Once during battle, if with an ISB agent, may cause opponent to draw destiny; may retrieve 2 Force if destiny > 2."),
            Map.entry("1230_61", "Use 1 Force to deploy on your bounty hunter (free on Feltipern Trevagg). May target a character for free. Draw destiny. Target captured (and may take one Interrupt from Lost Pile into hand) if destiny +2 > defense value."),
            Map.entry("1230_65", "Deploys only on Tatooine. Adds one battle destiny with Kalit. Your Jawas and sandcrawlers are destiny +2. May [download] one Jawa Camp, Sergeant Doallyn, or Thedit."),
            Map.entry("1240_66ai", "Matching weapon is Elegant Lightsaber, and, if Sai'torr Kal Fas on table, may lose 2 Force to [download] it on her. While with Galen or a [Sidious] Effect, adds one battle destiny. Immune to [Permanent Weapon] weapons and attrition < 5."),
            Map.entry("1240_94", "If your starting location was a system, [download] a related site (must be a battleground if the system is a non-battleground or Endor) with < 3 [Light Side Force] and up to three Effects that are always immune to Alter. Place this Interrupt in Lost Pile."),
            Map.entry("1250_25", "Deploy on a site. Unless a player controls a battleground site, that player's [Permanent Weapon] weapon destiny draws are -2. During opponent's draw phase, if opponent's non-battleground site (with no related locations) on table, may activate or retrieve 1 Force. (Immune to Alter.)"),
            Map.entry("1261_100", "USED: ▲ a non-[Episode I] droid (except IL-19). OR Place a card from hand on Used Pile. STARTING: ▼ two always [Immune to Alter] Effects. If you have only one location (with < 3 [Light Side Force]) (except Massassi Throne Room) on table, may ▼ a [Cloud City] site. Place this Interrupt in hand."),
            Map.entry("1261_102", "Use 1 Force to ▼ (or retrieve) Leia. OR If a battle just initiated, and non-[Reflections III] Leia defending alone at a site, her forfeit cannot be reduced, she adds one destiny to power (+1 for each character and vehicle present), and draws one battle destiny if not able to otherwise. (Immune to Sense.)"),
            Map.entry("1261_44", "If your character's forfeit was just reduced to 0, use 1 Force (free if forfeit reduced by a [Permanent Weapon] weapon) to restore it to normal. (Immune to Sense.) OR Lose 2 Force to cancel an attempt by Yoda or Obi-Wan to cancel your Interrupt or your just drawn weapon destiny. OR During your move phase, target any or all of your characters at one site to 'transport' (relocate) to an exterior or battleground site. Draw destiny. Use that much Force to 'transport,' or place this Interrupt in Lost Pile."),
            Map.entry("1262_6", "Deploy on table. If your Landing Craft just landed, may use 1 Force to ▼ a [Presence] droid or Neimoidian aboard. Once per turn, may use 1 Force: reveal a [Theed Palace] vehicle (or Landing Craft) from hand to ▲ a [Presence] droid pilot (or vice versa), and deploy both simultaneously. (Immune to Alter.)"),
            Map.entry("1263_10", "Deploy on Vader. [Coruscant] holograms are lost. Opponent may not add destiny draws to total power or attrition here. While with a Skywalker or Jedi, Vader's defense value is +2 (maximum of 8) and his immunity to attrition is +2. When Vader leaves table, place Effect in Used Pile. (Immune to Alter.)"),
            Map.entry("1263_3", "[Pilot] 2, 3: IG-2000. Deploys -3 aboard IG-2000. Adds one battle destiny if alone or with a smuggler. During battle, if piloting a starship, may draw a destiny. Starship is immune to attrition < X, or its immunity is +X (limit +2), where X = destiny."),
            Map.entry("1271_11", "Deploy on table. Once per game, may [upload] a [Setup] card. May place this Effect in Used Pile to [upload] an Immediate Effect. May place this Effect in Lost Pile to cancel a Force drain. (Immune to Alter.)"),
            Map.entry("1271_12", "Deploy on table. Sunsdown is canceled. ISB agents who are leaders are deploy -2 to sites with 'ISB' in title. Once per game, may [upload] a [Setup] Mission. Look Sir, Droids and your Interrupts with 'advisor' in title may be played as follows: 'USED: Use 1 Force to [upload] an ISB agent.' (Immune to Alter.)"),
            Map.entry("1271_41", "Deploy on table. Once per game, if Vader on table, may [download] Obi-Wan. May place this Effect in Used Pile to [upload] a [Grabber]. If you just attempted to [download] or [upload] a card, and the card was not found, may place this Effect in Lost Pile to search your Force Pile for that card; reshuffle."),
            Map.entry("1271_6", "[Pilot] 2. Matching pilot for Saber Squadron TIE and while piloting it: it may relocate using [Virtual Block 7] Combat Response, adds one destiny to total power only, and may add 1 to Force drains here (opponent loses no more than 2 Force to that Force drain)."),
            Map.entry("1271_8", "Deploy on table. Once per turn, if your character of ability > 4 just failed to 'hit' an [Episode I] Jedi (or [Virtual Block 6] Luke) with a non-[Permanent Weapon] lightsaber, may lose 1 Force to 'swing' at target again. May place this Effect in Used Pile to cancel Obi-Wan's Journal."),
            Map.entry("1272_20", "Deploy on table. Bail, Leia, and Mon Mothma are Insurgents. While an Insurgent present at a battleground, your total battle destiny is +1, and, if with your non-unique character, each may forfeit for 4 when hit. Once per game may [upload] K'lor'slug. (Immune to Alter.)"),
            Map.entry("1273_22", "[Pilot] 2. Heroic. Rogue Squadron pilot. During battle, when Dack (or a starship or vehicle he pilots) fires a weapon, may add 1 to total weapon destiny and hit targets are power -2. If Dack about to leave table, may place him in Used Pile instead."),
            Map.entry("1281_12", "Deploy on Contract Killers. Assassins are defense value +1. Once per game may retrieve [Virtual Block 8] Death Mark. At end of opponent's turn, if their character targeted by [Virtual Block 8] Death Mark is at a non-battleground or aboard their starship, opponent loses 3 Force. (Immune to Alter.)"),
            Map.entry("1282_25", "When you play this Interrupt, may place a Rep stacked on your Objective in Used Pile. [download] Seeking An Audience or an alien to Audience Chamber (for -1 Force). OR Reveal up to 3 aliens stacked on I Know That Laugh or from hand (X = number of aliens revealed); activate X Force."));

    private static SwccgCardBlueprintLibrary library;

    @BeforeClass
    public static void setUpClass() {
        library = new SwccgCardBlueprintLibrary();
    }

    @Test
    public void CurrentCardsShowTheLatestRealPublishedText() {
        assertEquals(41, LATEST_TEXT.size());
        LATEST_TEXT.forEach((id, text) -> {
            SwccgCardBlueprint card = library.getSwccgoCardBlueprint(id);
            assertNotNull(id, card);
            assertNull(id, card.getLegacyVersion());
            assertEquals(id, text, card.getGameText());
        });
    }
}
