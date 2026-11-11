package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Legacy batch R8-U (Bill 2026-11-11: every publication is a version; batches R8-U1..U4 of
 * versions/R8-EVERY-PUBLICATION-INVENTORY.md). 46 families whose PC website card-list text (2012) was never built: the
 * release-sheet text is the original (new 22xx class), the card-list text is PC-E1 (new 22xx_1nnn class, icons written
 * as icons), and the Virtual Cards Master List (official text 2013-04-27) prints the original words again as PC-E2,
 * the current card, whose text and play do not change. Images are pinned in cardImageUrlTests.js.
 */
public class LegacyBatchR8UVersionsTests {
    /** current id, original version id, card-list version id, sheet date, card-list date, current (= original) text, card-list text */
    private static final List<String[]> FAMILIES = List.of(
            new String[]{"1210_57", "2210_57", "2210_1057", "2009-12-07", "2012-07-21",
                    "[upload] a device or non-lightsaber weapon. OR If opponent just initiated a battle or Force drain at a system or sector, [download] (as a 'react') an unpiloted TIE and a pilot of ability < 3 (simultaneously). (Immune to Sense.)",
                    "[upload] a device or a non-lightsaber weapon. OR If opponent just initiated a battle or Force drain at a system or sector, [download] (as a 'react') an unpiloted TIE and a pilot of ability < 3 (simultaneously) on table. (Immune to Sense.)"},
            new String[]{"1210_64", "2210_64", "2210_1064", "2009-12-07", "2012-07-21",
                    "USED: [upload] a non-[Droid with Presence], non-[Permanent Weapon] droid. OR Place a card from hand on Used Pile. STARTING: [download] two always [Immune to Alter] Effects. If you have only one location (with < 3 [Dark Side Force]) on table, may [download] Central Office or Mos Eisley. Place this Interrupt in hand.",
                    "USED: [upload] a non-[Droid with Presence], non-[Permanent Weapon] droid. OR Place a card from hand on Used Pile. STARTING: [download] two Effects which are always [Immune to Alter]. If you have only one location (with < 3 [Dark Side Force]) on table, may [download] Central Office or Mos Eisley. Place this interrupt in hand."},
            new String[]{"1210_66", "2210_66", "2210_1066", "2009-12-07", "2012-07-21",
                    "USED: Use 1 Force to [download] an Effect that deploys for free on table (or your side of table) and is always immune to Alter. STARTING: When drawing your starting hand, draw up to 12 cards instead of 8. Place this Interrupt in Reserve Deck.",
                    "USED: Use 1 Force to [download] an Effect that deploys for free on table (or your side of the table) and is always immune to Alter. STARTING: When drawing your starting hand, draw up to 12 cards instead of 8. Place this Interrupt in Reserve Deck."},
            new String[]{"1210_105", "2210_105", "2210_1105", "2009-12-07", "2012-07-21",
                    "[Pilot] 3. If piloting lead starfighter, Attack Run total is +3. During your control phase, if piloting at a battleground, may [upload] one Darklighter Spin or retrieve 1 Force.",
                    "[Pilot] 3. If piloting lead starfighter, Attack Run total is +3. During your control phase, if piloting at a battleground, may [upload] Darklighter Spin or retrieve 1 Force."},
            new String[]{"1210_108", "2210_108", "2210_1108", "2009-12-07", "2012-07-21",
                    "Opponent activates no Force from Beru Stew. While present with Lars' Vaporator: during your control phase, may use 3 Force to exchange a card from hand with a 'crop' card; and cards with 'lightsaber' in title may not deploy here.",
                    "Opponent activates no Force from Beru Stew. While present with Lars' Vaporator: during your control phase, may use 3 Force to exchange one card from hand with one 'crop' card; cards with 'lightsaber' in title may not deploy here."},
            new String[]{"1210_150", "2210_150", "2210_1150", "2009-12-07", "2012-07-21",
                    "USED: Use 1 Force to [download] an Effect that deploys for free on table (or your side of table) and is always immune to Alter. STARTING: When drawing your starting hand, draw up to 12 cards instead of 8. Place this Interrupt in Reserve Deck.",
                    "USED: Use 1 Force to [download] an Effect that deploys for free on table (or on your side of table) and is always immune to Alter. STARTING: When drawing your starting hand, draw up to 12 cards instead of 8. Place this Interrupt in Reserve Deck."},
            new String[]{"1210_154", "2210_154", "2210_1154", "2009-12-07", "2012-07-21",
                    "USED: [upload] (or retrieve) a device. (Immune to Sense.) STARTING: If your starting location was a Tatooine location, [download] a farm, two devices, and two Effects that are always [Immune to Alter]. Place this Interrupt in Reserve Deck.",
                    "USED: Retrieve a device, or [upload] a device. (Immune to Sense.) STARTING: If your starting location was a Tatooine location, [download] a farm, two devices, and two Effects that are always [Immune to Alter]. Place this Interrupt in Reserve Deck."},
            new String[]{"1210_158", "2210_158", "2210_1158", "2009-12-07", "2012-07-21",
                    "[upload] a device or non-lightsaber weapon. OR If opponent just initiated a battle or Force drain at a system or sector, [download] (as a 'react') an unpiloted Rebel starfighter and a pilot of ability < 3 (simultaneously). (Immune to Sense.)",
                    "[upload] a device or a non-lightsaber weapon. OR If opponent just initiated a battle or Force drain at a system or sector, [download] (as a 'react') an unpiloted Rebel starfighter and a pilot of ability < 3 (simultaneously) on table. (Immune to Sense.)"},
            new String[]{"1220_20", "2220_20", "2220_1020", "2009-12-07", "2012-07-21",
                    "Habitat: Hoth sites. Deploys only to a marker site. Wampas are [Selective Creature]. Other wampas are ferocity and defense value +2. Once per game, may [download] Wampa Cave.",
                    "Habitat: Hoth sites. Deploys only to a marker site. Wampas are [Selective Creature] creatures. Other wampas are ferocity and defense value +2. Once per game, may [download] Wampa Cave on table."},
            new String[]{"1220_44", "2220_44", "2220_1044", "2009-12-07", "2012-07-21",
                    "If you just deployed a TIE/sa, deploy on a unique (•) TIE/sa. Bombing Run is [Immune to Control]. May place this card in Used Pile to [upload] (or, once per game, retrieve) Bombing Run or ComScan Detection; opponent loses 1 Force. (Immune to Control.)",
                    "If you just deployed a TIE/SA, deploy on a unique TIE/SA. Bombing Run is [Immune to Control]. May place this card in Used Pile to [upload] Bombing Run or ComScan Detection (or, once per game, retrieve); opponent loses 1 Force. (Immune to Control.)"},
            new String[]{"1220_57", "2220_57", "2220_1057", "2009-12-07", "2012-07-21",
                    "Once per turn, [upload] Crash Landing or Weapon Malfunction. OR If you have a piloted AT-AT on Hoth, target artillery weapon at a related site is lost. (Immune to Sense.)",
                    "Once per turn, may [upload] one Crash Landing or Weapon Malfunction. OR if you have a piloted AT-AT on Hoth, target artillery weapon at a related site is lost. (Immune to Sense.)"},
            new String[]{"1220_66", "2220_66", "2220_1066", "2009-12-07", "2012-07-21",
                    "Unless you have a senate majority, if opponent just [upload] or [download] a character (except with an Effect with 'matching' in game text) or just drew a card from Reserve Deck using a character's game text, they must place 2 other cards from hand on Used Pile (if possible).",
                    "Unless you have a senate majority, if opponent just [upload] or [download] character (except with an Effect with 'matching' in game text) or just drew a card from Reserve Deck using a character's game text, they must place 2 other cards from hand on Used Pile (if possible)."},
            new String[]{"1220_97", "2220_97", "2220_1097", "2009-12-07", "2012-07-21",
                    "May deploy or move as a 'react' to same site as an Imperial. Adds one battle destiny if with Han or Vader. During your deploy phase, may [download] one Leia Of Alderaan, Reflection, or matching blaster on Leia. Immune to attrition < 4.",
                    "May deploy or move as a 'react' to same site as an Imperial. Adds one battle destiny if with Han or Vader. During your deploy phase, may [download] one Leia Of Alderaan, Reflection, or a matching blaster on Leia. Immune to attrition < 4."},
            new String[]{"1220_133", "2220_133", "2220_1133", "2009-12-07", "2012-07-21",
                    "Deploy on table. Rebel scouts (except pilots) are troopers. Rebel troopers have armor = 4 and are immune to Physical Choke. Whenever you deploy a Rebel trooper, may immediately [download] Blaster Rifle on that character. (Immune to Alter.)",
                    "Deploy on table. Rebel scouts (except pilots) are troopers. Rebel troopers have armor = 4 and are immune to Physical Choke. Whenever you deploy a Rebel trooper, may immediately [download] a Blaster Rifle on that character. (Immune to Alter.)"},
            new String[]{"1220_153", "2220_153", "2220_1153", "2009-12-07", "2012-07-21",
                    "Unless you have a senate majority, if opponent just [upload] or [download] a character (except with an Effect with 'matching' in game text) or just drew a card from Reserve Deck using a character's game text, they must place 2 other cards from hand on Used Pile (if possible).",
                    "Unless you have a senate majority, if opponent just [upload] a character, [download] a character (except with an Effect with 'matching' in game text), or just drew a card from Reserve Deck using a character's game text, they must place 2 other cards from hand on Used Pile (if possible)."},
            new String[]{"1230_2", "2230_2", "2230_1002", "2009-12-07", "2012-07-21",
                    "Opponent's non-Jedi characters are deploy +1 to [Jabba's Palace] sites. Once during your control phase, may place three cards from hand in Used Pile to reveal top card of Reserve Deck; if it is an alien, take it into hand.",
                    "Opponent's non-Jedi characters are deploy +1 to [Jabba's Palace] sites. Once during your control phase, you may place three cards from hand in Used Pile to reveal top card of Reserve Deck; if it is an alien, take it into hand."},
            new String[]{"1230_3", "2230_3", "2230_1003", "2009-12-07", "2012-07-21",
                    "[Pilot] 2, 3: any starfighter. If your starship participating in battle is about to be excluded (even if that starship may not participate in battle) or made lost by opponent, you may choose for it to be 'hit' instead.",
                    "[Pilot] 2, 3: any starfighter. If your starship participating in a battle is about to be excluded (even if that starship may not participate in battle) or made lost by opponent, you may choose for it to be 'hit' instead."},
            new String[]{"1230_5", "2230_5", "2230_1005", "2009-12-07", "2012-07-21",
                    "[Pilot] 2. While Undercover, opponent's Force drains at related locations are -1. Once during battle, if with an ISB agent, may cause opponent to draw destiny; may retrieve 2 Force if destiny > 2.",
                    "[Pilot] 2. While Undercover, opponent's Force drains at related locations are -1. Once during battle, if with an ISB agent, may cause opponent to draw destiny. You may retrieve 2 Force if destiny > 2."},
            new String[]{"1230_21", "2230_21", "2230_1021", "2009-12-07", "2012-07-21",
                    "[Pilot] 3. Wooof may deploy with any cruiser using Combat Response. When deployed, may draw top card from Reserve Deck. During your deploy phase, if piloting Jabba's Space Cruiser, may [download] Jabba aboard (for free).",
                    "[Pilot] 3. Wooof may deploy with any cruiser using Combat Response. When deployed, may draw top card from Reserve Deck. During your deploy phase, if piloting Jabba's Space Cruiser, may [download] one Jabba aboard (for free)."},
            new String[]{"1230_32", "2230_32", "2230_1032", "2009-12-07", "2012-07-21",
                    "Deploy on a system. Your TIEs may relocate (as a regular move) to a system from 0 to 3 parsecs away. For each battleground system you control with two TIEs piloted by Imperials, may add 3 to your total power in battles. Opponent's Force drain is +3 here. (Immune to Alter.)",
                    "Deploy on a system. Your TIEs may relocate (as a regular move) to a system from 0 to 3 parsecs away. For each battleground system you control with two TIEs piloted by Imperials, you may add 3 to your total power in battles. Opponent's Force drain is +3 here. (Immune to Alter.)"},
            new String[]{"1230_40", "2230_40", "2230_1040", "2009-12-07", "2012-07-21",
                    "USED: Creature attacks at pits are immune to At Peace and Yarna d'al' Gargan until end of your next turn. (Immune to Sense.) OR Cancel an attempt to target a rancor with a character weapon. OR Use 1 Force (2 if a rancor on table) to [download] Nal Hutta. STARTING: If Great Pit Of Carkoon on table, [download] an Effect that is always [Immune to Alter] and choose: [download] Rancor Pit and a rancor there, OR [download] Power Of The Hutt and Sarlacc. Place this Interrupt in hand.",
                    "USED: Creature attacks at pits are immune to At Peace and Yarna d'al' Gargan until end of your next turn. (Immune to Sense.) OR Cancel an attempt to target a rancor with a character weapon. OR Use 1 Force (2 if a rancor on table) to [download] Nal Hutta. STARTING: If Great Pit Of Carkoon on table, [download] an Effect that is always [Immune to Alter] and choose to [download]: Rancor Pit and a rancor there, OR Power Of The Hutt and Sarlacc. Place this Interrupt in hand."},
            new String[]{"1230_45", "2230_45", "2230_1045", "2009-12-07", "2012-07-21",
                    "USED: [download] Empire's New Order. OR If Vader just won a battle at same site as an alien, place that alien out of play. LOST: If a battle was just initiated where you have a character weapon card, place the top card of your Lost Pile on top of Reserve Deck.",
                    "USED: [download] Empire's New Order on table. OR If Vader just won a battle at the same site as an alien, place that alien out of play. LOST: If a battle was just initiated where you have a character weapon card, place the top card of your Lost Pile on top of your Reserve Deck."},
            new String[]{"1230_46", "2230_46", "2230_1046", "2009-12-07", "2012-07-21",
                    "If opponent just initiated battle at a site where their total power is greater than yours, may use 1 Force (or cause opponent to use 1 Force) to relocate one of your characters or vehicles to that site from a related location.",
                    "If opponent just initiated battle at a site where their total power is greater than yours, you may use 1 Force (or cause opponent to use 1 Force) to relocate one of your characters or vehicles to that site from a related location."},
            new String[]{"1230_61", "2230_61", "2230_1061", "2009-12-07", "2012-07-21",
                    "Use 1 Force to deploy on your bounty hunter (free on Feltipern Trevagg). May target a character for free. Draw destiny. Target captured (and may take one Interrupt from Lost Pile into hand) if destiny +2 > defense value.",
                    "Use 1 Force to deploy on your bounty hunter (free on Feltipern Trevagg). May target a character for free. Draw destiny. Target captured (and you may take one Interrupt from Lost Pile into hand) if destiny +2 > defense value."},
            new String[]{"1230_65", "2230_65", "2230_1065", "2009-12-07", "2012-07-21",
                    "Deploys only on Tatooine. Adds one battle destiny with Kalit. Your Jawas and sandcrawlers are destiny +2. May [download] one Jawa Camp, Sergeant Doallyn, or Thedit.",
                    "Deploys only on Tatooine. Adds one battle destiny with Kalit. Your Jawas and sandcrawlers are destiny +2. May [download] Jawa Camp, Sergeant Doallyn, or Thedit on table."},
            new String[]{"1230_105", "2230_105", "2230_1105", "2009-12-07", "2012-07-21",
                    "If you have an information broker on table, draw destiny. If destiny > the number of opponent's characters on table, may use 1 Force to [upload] any one card.",
                    "If you have an information broker on table, draw destiny. If destiny > the number of opponent's characters on table, you may use 1 Force to [upload] any one card."},
            new String[]{"1250_25", "2250_25", "2250_1025", "2009-12-07", "2012-07-21",
                    "Deploy on a site. Unless a player controls a battleground site, that player's [Permanent Weapon] weapon destiny draws are -2. During opponent's draw phase, if opponent's non-battleground site (with no related locations) on table, may activate or retrieve 1 Force. (Immune to Alter.)",
                    "Deploy on a site. Unless a player controls a battleground site, that player's [Permanent Weapon] destiny draws are -2. During opponent's draw phase, if opponent's non-battleground site (with no related locations) on table, may activate or retrieve 1 Force. (Immune to Alter.)"},
            new String[]{"1261_40", "2261_40", "2261_1040", "2009-09-04", "2012-07-21",
                    "Deploy on table. Each Amidala stacked on a Political Effect is a senator. While you have < 13 cards in hand, opponent may not peek at or remove them (except with Grimtaash). Once per turn, may ▲ [Virtual] Astromech Shortage or lose 2 Force to cancel a Political Effect. (Immune to Alter.)",
                    "Deploy on table. Each Amidala stacked on a Political Effect is a senator. While you have < 13 cards in hand, opponent may not peek at or remove them (except with Grimtaash). Once per turn, may ▲ one [Virtual] Astromech Shortage, or lose 2 Force to cancel a Political Effect. (Immune to Alter.)"},
            new String[]{"1261_44", "2261_44", "2261_1044", "2009-09-04", "2012-07-21",
                    "If your character's forfeit was just reduced to 0, use 1 Force (free if forfeit reduced by a [Permanent Weapon] weapon) to restore it to normal. (Immune to Sense.) OR Lose 2 Force to cancel an attempt by Yoda or Obi-Wan to cancel your Interrupt or your just drawn weapon destiny. OR During your move phase, target any or all of your characters at one site to 'transport' (relocate) to an exterior or battleground site. Draw destiny. Use that much Force to 'transport,' or place this Interrupt in Lost Pile.",
                    "If your character's forfeit was just reduced to 0, use 1 Force (free if forfeit reduced by a [Permanent Weapon] weapon) to restore it to normal. (Immune to Sense.) OR Lose 2 Force to cancel an attempt by Yoda or Obi-Wan to cancel your Interrupt or your just drawn weapon destiny. OR During your move phase, target any or all of your characters at one site to 'transport' (relocate) to an exterior or battleground site. Draw destiny. Use that much Force to 'transport,' or place Interrupt in Lost Pile."},
            new String[]{"1263_3", "2263_3", "2263_1003", "2010-03-12", "2012-07-21",
                    "[Pilot] 2, 3: IG-2000. Deploys -3 aboard IG-2000. Adds one battle destiny if alone or with a smuggler. During battle, if piloting a starship, may draw a destiny. Starship is immune to attrition < X, or its immunity is +X (limit +2), where X = destiny.",
                    "[Pilot] 2, 3: IG-2000. Deploys -3 aboard IG-2000. Adds one battle destiny if alone or with a smuggler. During battle, if piloting a starship, may draw destiny. Starship is immune to attrition < X, or its immunity is +X (limit +2), where X = destiny."},
            new String[]{"1263_10", "2263_10", "2263_1010", "2010-03-12", "2012-07-21",
                    "Deploy on Vader. [Coruscant] holograms are lost. Opponent may not add destiny draws to total power or attrition here. While with a Skywalker or Jedi, Vader's defense value is +2 (maximum of 8) and his immunity to attrition is +2. When Vader leaves table, place Effect in Used Pile. (Immune to Alter.)",
                    "Deploy on Vader. [Coruscant] holograms are lost. Opponent may not add destiny draws to total power or attrition here. While with a Skywalker or Jedi, Vader's defense value is +2 (to a maximum of 8) and his immunity to attrition is +2. When Vader leaves table, place Effect in Used Pile. (Immune to Alter.)"},
            new String[]{"1271_3", "2271_3", "2271_1003", "2010-06-18", "2012-07-21",
                    "[Pilot] 2, 3: Black 5. While piloting Black 5, it is maneuver +1 and he draws one battle destiny if not able to otherwise. If at opponent's system, instead of Force draining here, may take any one card into hand from Force Pile; reshuffle.",
                    "[Pilot] 2, 3: Black 5. While piloting Black 5, it is maneuver +1 and he draws one battle destiny if not able to otherwise. If at opponent's system, instead of Force draining here, may take any card into hand from Force Pile; reshuffle."},
            new String[]{"1271_6", "2271_6", "2271_1006", "2010-06-18", "2012-07-21",
                    "[Pilot] 2. Matching pilot for Saber Squadron TIE and while piloting it: it may relocate using [Virtual Block 7] Combat Response, adds one destiny to total power only, and may add 1 to Force drains here (opponent loses no more than 2 Force to that Force drain).",
                    "[Pilot] 2. Matching pilot for Saber Squadron TIE and while piloting it: it may relocate using [Virtual Block 7] Combat Response, adds one destiny to total power only, and may add 1 to Force drains here (opponent loses no more than 2 Force to that drain)."},
            new String[]{"1271_8", "2271_8", "2271_1008", "2010-06-18", "2012-07-21",
                    "Deploy on table. Once per turn, if your character of ability > 4 just failed to 'hit' an [Episode I] Jedi (or [Virtual Block 6] Luke) with a non-[Permanent Weapon] lightsaber, may lose 1 Force to 'swing' at target again. May place this Effect in Used Pile to cancel Obi-Wan's Journal.",
                    "Deploy on table. Once per turn, if your character of ability > 4 just failed to 'hit' an [Episode I] Jedi (or [Virtual Block 6] Luke) with a non-[Permanent Weapon] lightsaber, may lose 1 Force to 'swing' at target again. May place in Used Pile to cancel Obi-Wan's Journal."},
            new String[]{"1271_11", "2271_11", "2271_1011", "2010-06-18", "2012-07-21",
                    "Deploy on table. Once per game, may [upload] a [Setup] card. May place this Effect in Used Pile to [upload] an Immediate Effect. May place this Effect in Lost Pile to cancel a Force drain. (Immune to Alter.)",
                    "Deploy on table. Once per game, may [upload] a [Setup] card. May place this card in Used Pile to [upload] an Immediate Effect. May place this Effect in Lost Pile to cancel a Force drain. (Immune to Alter.)"},
            new String[]{"1271_12", "2271_12", "2271_1012", "2010-06-18", "2012-07-21",
                    "Deploy on table. Sunsdown is canceled. ISB agents who are leaders are deploy -2 to sites with 'ISB' in title. Once per game, may [upload] a [Setup] Mission. Look Sir, Droids and your Interrupts with 'advisor' in title may be played as follows: 'USED: Use 1 Force to [upload] an ISB agent.' (Immune to Alter.)",
                    "Deploy on table. Sunsdown is canceled. ISB agents who are leaders deploy -2 to sites with 'ISB' in title. Once per game, may [upload] a [Setup] Mission. Look Sir, Droids and your Interrupts with 'advisor' in title may be played as follows: 'USED: Use 1 Force to [upload] an ISB agent.' (Immune to Alter.)"},
            new String[]{"1271_31", "2271_31", "2271_1031", "2010-06-18", "2012-07-21",
                    "[Pilot] 2, 3: Red 3. While piloting Red 3, it is maneuver +1 and he draws one battle destiny if not able to otherwise. If at opponent's system, instead of Force draining here, may take any one card into hand from Force Pile; reshuffle.",
                    "[Pilot] 2, 3: Red 3. While piloting Red 3, it is maneuver +1 and he draws one battle destiny if not able to otherwise. If at opponent's system, instead of Force draining here, may take any card into hand from Force Pile; reshuffle."},
            new String[]{"1271_41", "2271_41", "2271_1041", "2010-06-18", "2012-07-21",
                    "Deploy on table. Once per game, if Vader on table, may [download] Obi-Wan. May place this Effect in Used Pile to [upload] a [Grabber]. If you just attempted to [download] or [upload] a card, and the card was not found, may place this Effect in Lost Pile to search your Force Pile for that card; reshuffle.",
                    "Deploy on table. Once per game, if Vader on table, may [download] Obi-Wan. May place this Effect in Used Pile to [upload] a [Grabber]. If you just attempted to [upload] or [download] a card, and the card was not found, may place this Effect in Lost Pile to search your Force Pile for that card; reshuffle."},
            new String[]{"1272_20", "2272_20", "2272_1020", "2010-12-15", "2012-07-21",
                    "Deploy on table. Bail, Leia, and Mon Mothma are Insurgents. While an Insurgent present at a battleground, your total battle destiny is +1, and, if with your non-unique character, each may forfeit for 4 when hit. Once per game may [upload] K'lor'slug. (Immune to Alter.)",
                    "Deploy on table. Bail, Leia, and Mon Mothma are Insurgents. While an Insurgent present at a battleground, your total battle destiny is +1, and, if with your non-unique character, each may forfeit for 4 when hit. Once per game, may [upload] one K'lor'slug. (Immune to Alter.)"},
            new String[]{"1273_4", "2273_4", "2273_1004", "2011-06-03", "2012-07-21",
                    "[Pilot] 1. May deploy -1 as a 'react'; shuffle any Reserve Deck. Ignores Objective deployment restrictions. Unless alone, may place Garindan in Used Pile to cancel a just drawn weapon destiny here. [Virtual Block 2] Imperial Domination ignores Garindan.",
                    "[Pilot] 1. May deploy -1 as a 'react'; shuffle any Reserve Deck. Ignores Objective deployment restrictions. Unless alone, may place Garindan in Used Pile to cancel a just drawn Weapons Destiny here. [Virtual Block 2] Imperial Domination ignores Garindan."},
            new String[]{"1273_22", "2273_22", "2273_1022", "2011-06-03", "2012-07-21",
                    "[Pilot] 2. Heroic. Rogue Squadron pilot. During battle, when Dack (or a starship or vehicle he pilots) fires a weapon, may add 1 to total weapon destiny and hit targets are power -2. If Dack about to leave table, may place him in Used Pile instead.",
                    "[Pilot] 2. Heroic. Rogue Squadron Pilot. During battle when Dack (or a starship or vehicle he pilots) fires a weapon, may add 1 to total weapon destiny and hit targets are power -2. If Dack is about to leave table may place him in Used Pile instead."},
            new String[]{"1274_2", "2274_2", "2274_1002", "2011-12-16", "2012-07-21",
                    "[Pilot] 2: any vehicle. While piloting a [Theed Palace] vehicle at a battleground site, adds one [Dark Side] and may add 1 to your Force drains here. Once per game, may [upload] a card with 'fire' in title.",
                    "[Pilot] 2: any vehicle. While piloting a [Theed Palace] vehicle at a battleground site: adds 1 [Dark Side] here and may add 1 to your Force drains here. Once per game, may [upload] any a card with 'fire' in title."},
            new String[]{"1274_3", "2274_3", "2274_1003", "2011-12-16", "2012-07-21",
                    "Deploy on table. Your [Theed Palace] vehicles are defense value and immunity to attrition +1. May [download] Deployment Orders. Once per game, if Deployment Orders on table, may [download] an exterior battleground site not on table. Once per game, may retrieve a [Theed Palace] vehicle into hand. (Immune to Alter.)",
                    "Deploy on table. Your [Theed Palace] vehicles are defense value and immunity to attrition +1. May [download] Deployment Orders. Once per game, if Deployment Orders on table, may [download] an exterior battleground site not already on table. Once per game, may retrieve a [Theed Palace] vehicle into hand. (Immune to Alter.)"},
            new String[]{"1274_17", "2274_17", "2274_1017", "2011-12-16", "2012-07-21",
                    "Deploy on your non-[Reflections 2] astromech droid. Droid is power +2, gains [Presence Droid], and if present at a site, draws one battle destiny if unable to otherwise. During any move phase, may place this device in Lost Pile to [upload] one What're You Tryin' To Push On Us? or R2-D2.",
                    "Deploy on your non-[Reflections 2] astromech droid. That droid is power +2, gains [Presence Droid] and if present at a site, draws one battle destiny if unable to otherwise. During any move phase, may place this device in Lost Pile to [upload] one What're You Tryin' To Push On Us? or one R2-D2."},
            new String[]{"1281_12", "2281_12", "2281_1012", "2012-05-11", "2012-07-21",
                    "Deploy on Contract Killers. Assassins are defense value +1. Once per game may retrieve [Virtual Block 8] Death Mark. At end of opponent's turn, if their character targeted by [Virtual Block 8] Death Mark is at a non-battleground or aboard their starship, opponent loses 3 Force. (Immune to Alter.)",
                    "Deploy on Contract Killers. Assassins are defense value +1. Once per game, may retrieve [Virtual Block 8] Death Mark. At the end of opponent's turn, if their character targeted by [Virtual Block 8] Death Mark is at a non-battleground or aboard their starship, opponent loses 3 Force. (Immune to Alter.)"},
            new String[]{"1282_25", "2282_25", "2282_1025", "2012-09-14", "2012-12-12",
                    "When you play this Interrupt, may place a Rep stacked on your Objective in Used Pile. [download] Seeking An Audience or an alien to Audience Chamber (for -1 Force). OR Reveal up to 3 aliens stacked on I Know That Laugh or from hand (X = number of aliens revealed); activate X Force.",
                    "When you play this Interrupt, may place a Rep stacked on your Objective in Used Pile. May [download] Seeking An Audience or an alien to Audience Chamber (for -1 Force). OR Reveal up to 3 Aliens stacked on I Know That Laugh or from hand (X = number of aliens revealed); activate X Force."}
    );

    private static SwccgCardBlueprintLibrary library;

    @BeforeClass
    public static void setUpClass() {
        library = new SwccgCardBlueprintLibrary();
    }

    @Test
    public void CurrentCardsKeepTheMasterListText() {
        assertEquals(46, FAMILIES.size());
        for (String[] f : FAMILIES) {
            SwccgCardBlueprint current = library.getSwccgoCardBlueprint(f[0]);
            assertNotNull(f[0], current);
            assertNull(f[0], current.getLegacyVersion());
            assertEquals(f[0], f[5], current.getGameText());
        }
    }

    @Test
    public void OriginalsAreTheReleaseSheetText() {
        for (String[] f : FAMILIES) {
            SwccgCardBlueprint current = library.getSwccgoCardBlueprint(f[0]);
            SwccgCardBlueprint original = library.getSwccgoCardBlueprint(f[1]);
            assertNotNull(f[1], original);
            LegacyVersion v = original.getLegacyVersion();
            assertNotNull(f[1], v);
            assertEquals(f[1], f[0], v.currentPrintingId());
            assertEquals(f[1], "", v.label());
            assertEquals(f[1], LocalDate.parse(f[3]), v.printedOn());
            assertEquals(f[1], LocalDate.parse(f[4]), v.supersededOn());
            assertEquals(f[1], current.getGameText(), original.getGameText());
            assertEquals(f[1], current.getTitle(), original.getTitle());
        }
    }

    @Test
    public void CardListVersionsAreTheirOwnCards() {
        for (String[] f : FAMILIES) {
            SwccgCardBlueprint current = library.getSwccgoCardBlueprint(f[0]);
            SwccgCardBlueprint cardList = library.getSwccgoCardBlueprint(f[2]);
            assertNotNull(f[2], cardList);
            LegacyVersion v = cardList.getLegacyVersion();
            assertNotNull(f[2], v);
            assertEquals(f[2], f[0], v.currentPrintingId());
            assertEquals(f[2], "PC-E1", v.label());
            assertEquals(f[2], LocalDate.parse(f[4]), v.printedOn());
            assertEquals(f[2], LocalDate.of(2013, 4, 27), v.supersededOn());
            assertEquals(f[2], f[6], cardList.getGameText());
            assertNotEquals(f[2], current.getGameText(), cardList.getGameText());
            assertEquals(f[2], current.getTitle(), cardList.getTitle());
            assertEquals(f[2], current.getSide(), cardList.getSide());
        }
    }
}
