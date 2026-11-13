package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.logic.modifiers.ModifierType;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Virtual Modern cards whose current PC card image words differ from the PC card data (Bill, 12 Nov 2026: "the wording
 * on the card takes precedence and that is what the card data should show"). Each row is the game text read from the
 * card's current PC image in the Holotable image repository (swccgpc/holotable, Images-HT/starwars/Virtual*), in GEMP's
 * icon notation ([download] = the down-arrow icon, [upload] = the up-arrow icon). Locations: Light Side text, Dark Side
 * text ("" = blank side). Imperial Enforcement: set201/dark/Card_201_030_Tests.
 */
public class VirtualImageWordingTests {
    private static final String[][] CARDS = {
            {"223_40", "When drawn for destiny, if three Ewoks and/or Wookiees on table, destiny +2. Deploy a bowcaster or Ewok weapon from Lost Pile. OR [upload] an Ewok or Wookiee of power < 6. OR During battle at same site as Part Of The Tribe, add one battle destiny.", null, null},
            {"201_20", "May add 1 pilot. Permanent pilot is •Dash, who provides ability of 3. Unless this card 'hit,' other T-47s here may not be targeted by Crash Landing, High-speed Tactics, or weapons. Immune to attrition < 4.", null, null},
            {"203_19", "Deploy Tatooine system (with Tantive IV, non-[Reflections II] R2-D2, and Stolen Data Tapes there) and Dune Sea. For remainder of game, you may not deploy Sandwhirl, Strike Planning, Admiral's Orders, or [Episode I] Jedi. While this side up, your Force drains at Tatooine system are -2. Once per turn, may [download] Alderaan or a Tatooine battleground site. Until the start of your first turn, Tantive IV may be forfeited to cancel all remaining battle damage. Flip this card if Stolen Data Tapes 'delivered' and Rebels control two battlegrounds (a site and a system).", null, null},
            {"203_19_BACK", "While this side up, your total battle destiny is +X, where X = number of battlegrounds occupied by Rebels of ability < 4. Once per turn, may [download] Alderaan or a Tatooine battleground site. Once per turn, if you just won a battle, may retrieve 1 Force. Flip this card if you do not occupy a battleground site and a battleground system (or you do not control any locations).", null, null},
            {"223_20", "USED: [Upload] (or place in owner's Used Pile) Deactivated Hyperdrive or an Admiral's Order. LOST: During battle, if Vader present with two opponent's characters of ability < 4, add one battle destiny. If any are Rebel troopers, choose one to be 'choked' (lost).", null, null},
            {"225_36", "Deploy on Galactic Senate. [Set 17] Passel Argente's game text and your Political Effects are canceled. Twice per turn, may target your agenda here: Blockade: Cancel a 'react.' Taxation: Place a character with politics from hand in Used Pile to choose: Activate 1 Force or [download] Senate Hovercam. Trade: During your draw phase, place a card from hand in Lost Pile, shuffle that pile, and take top card into hand. Wealth: Subtract 1 from attrition against you.", null, null},
            {"225_47", "Deploy on Galactic Senate. Your Political Effects are canceled. Twice per turn, may target your agenda here: Justice: During battle, subtract 1 from a just drawn weapon destiny. Order: During any move phase, peek at the top 2 cards of any Reserve Deck and replace in any order. Peace: Subtract 1 from attrition against you. Taxation: Place a character with politics from hand in Used Pile to choose: Activate 1 Force or [download] Senate Hovercam.", null, null},
            {"221_5", "If Obi-Wan 'communing,' deploy on table. Once per battle involving a Rebel, may activate 1 Force or add 1 to a just drawn battle destiny. Once per game, may [upload] (or retrieve) Old Ben. [Immune to Alter.]", null, null},
            {"223_29", "Plays on table. You lose no Force to Boonta Eve Podrace. Unless opponent occupies three battlegrounds, I Did It! is suspended. Double Agent may not target opponent's Undercover spies. If an Undercover spy is about to be lost (for any reason), place it out of play instead.", null, null},
            {"223_30", "Plays on table. You lose no Force to Boonta Eve Podrace. While you occupy three battlegrounds, Watto's Box is suspended. Nevar Yalnal may not target opponent's Undercover spies. If an Undercover spy is about to be lost (for any reason), place it out of play instead.", null, null},
            {"223_11", "Deploy on an AT-AT; may immediately search your Used Pile and take an AT-AT Cannon or [Hoth] Epic Event into hand; reshuffle. Adds 2 to each of this AT-AT's weapon destiny draws (unless targeting a character).", null, null},
            {"210_39", null, "If Vader on table, unless Amidala at a Mustafar location, Force drain -1 here.", "While Anakin or Vader here, gains one [Dark Side] icon and one [Light Side] icon."},
            {"205_7", "Permanent pilots are •Han and •Chewie, who provide total ability of 5. While in battle, adds one destiny to total power. Immune to Come With Me, Life Debt, and attrition < 6.", null, null},
            {"206_11", "[Pilot] 3. Leader. Once per turn, may reveal from hand to [download] 6th Marker or Blizzard 1 to Hoth. Deploys free aboard Blizzard 1. While piloting Blizzard 1, adds one destiny to total power.", null, null},
            {"207_25", "Deploy on Bunker if you control that site. Your Force generation at Endor system may not be canceled. Once per turn, may [download] an Endor site. Place Effect in Used Pile if opponent controls this site. (Immune to Alter.)", null, null},
            {"226_12_BACK", "While this side up, Force drain bonuses at same site as your Lando or your Lobot may not be canceled. While Vader at a Bespin location, game text of Admiral's Orders is canceled and Alter may not be played. While your alien/Imperial pair in battle, your total battle destiny is +2. Once per battle involving your Lando (twice if any Lobot also there), may subtract 1 from opponent's just drawn destiny. Flip this card if opponent controls more Bespin locations than you.", null, null},
            {"226_19", "Once per turn, if Wookiee Homestead on table, may place a card from hand on Force Pile to draw bottom card of your Force Pile. Once per game, may [download] a unique (•) forest. Immune to Wookiee Strangle and You Are Beaten.", null, null},
            {"227_12", "Once per game, may [download] a sandcrawler here. While driving a sandcrawler, it moves for free, may not be targeted by weapons, and Run'heb adds one battle destiny. Once during your turn, may choose a player to activate 1 Force.", null, null},
            {"227_19", "Deploy on table. Sense and Alter are Lost Interrupts. When any player makes a destiny draw for Sense or Alter, and that destiny draw is successful, that player loses 2 Force (may not be reduced). May [download] Bog Clearing. [Dagobah] Luke is a Padawan. Once per game, if [Dagobah] Luke in battle, may lose 1 Force to cancel a non-[Sense] Interrupt. Yoda and Padawans are immune to Imperial Barrier and Stunning Leader and may move for free using landspeed. While alone, Jedi Ahsoka's game text is canceled. [Immune to Alter.]", null, null},
            {"225_35", "[Pilot] 2. Power +1 for each opponent's card out of play. Opponent may not cancel your battle destiny draws where you have Kylo or a Knight of Ren. If you just initiated a Force drain here, may place topmost Interrupt of opponent's Lost Pile out of play.", null, null},
            {"223_47", "Once per game, 'BEEP-BOOP' (your [Grabber] card stacks a just played opponent's Interrupt; this does not count toward any [Grabber] card's once per game limit). OR Once per game, 'WHAAAAOW!' (lose your droid at a Scomp link to cancel a battle just initiated at same or related interior site).", null, null},
            {"223_25", "If two Imperials (or two Black Sun agents) in battle, re-circulate and shuffle your Reserve Deck. OR Once per game, if Emperor or Tarkin on table, Allegations Of Corruption stacks opponent's just played Interrupt; this does not count toward that card's once per game limit.", null, null},
            {"221_59", "If Podrace Arena on table, [upload] Skywalker Hut, Amidala, or Jar Jar. OR During your control phase, if you have won a Podrace and [Tatooine] Anakin is present at a battleground site, opponent loses 1 Force for each [Dark Side] at that site.", null, null},
            {"215_25", "If you did not deploy an Epic Event or Objective, deploy on table. We're Not Going To Attack?: Once per turn, may [download] a battleground system. You may not deploy non-Imperial starships. Unless you occupy a battleground site, Dreaded Imperial Starfleet is suspended. The Alliance Will Die...: Flagship Operations does not require any Executor sites on table to deploy, and does not target squadrons. ...As Will Your Friends: At battleground systems where you have a piloted capital starship/TIE pair, Force drains = 2. During battle at a site related to a system you occupy, may add one destiny to total power and/or re-circulate.", null, null},
            {"215_11", "[Pilot] 3. When deployed, may reveal the top two cards of your Reserve Deck; take one into hand. Cancels Kylo's game text here. Draws one battle destiny if unable to otherwise. During battle with Chewie or [Endor] Leia, may add one destiny to total power.", null, null},
            {"210_20", "Never deploys to a site opponent occupies. Deploys -2 to Ahch-To. Once per turn, may [upload] Force Projection. During battle, may cancel an opponent's just drawn destiny to cause a redraw. Immune to attrition.", null, null},
            {"209_43", "If Agents Of Black Sun on table, deploy on table. May use 1 Force to [download] Imperial Square. Once per turn, if Emperor on Coruscant, may draw top card of Force Pile (if during your turn and you occupy three battlegrounds, opponent also loses 1 Force). [Immune to Alter.]", null, null},
            {"219_10", null, "Piloted Rebel starships here are power and forfeit +1 and immune to Gravity Shadow.", "While you occupy, gains one [Dark Side] icon."},
            {"219_38", null, "Once per game, may simultaneously deploy Ghost and Hera here from hand and/or Reserve Deck; reshuffle.", "Thrawn and Pryce deploy -1 (and shuttle for free) to here. While Lothal converted, gains one [Dark Side] icon."},
            {"219_1", "Deploy Lothal system, Advanced Projects Laboratory, Imperial Complex, and Thrawn's Art Collection. For remainder of game, you may not deploy Chiraneau or [Episode I] (or [Episode VII]) cards with ability or [Presence]. Once per turn, may [download] a battleground system (or a site to Lothal). While this side up, Imperial Star Destroyers deploy -1 (-3 if Chimaera deploying to Lothal). Flip this card during any deploy phase if Thrawn at a battleground and two artwork cards on table.", null, null},
            {"216_4", null, "", "May [download] non-[Theed Palace] Sidious here. If your [Special Edition] objective on table, gains one [Light Side] icon and opponent's Force drains are +2 here."},
            {"216_25", null, "While Qui-Gon 'communing,' no Force drains here and, once per turn, may [download] Theed Palace Generator.", ""},
            {"216_26", null, "If Yoda here or [Set 16] Yoda 'communing,' Broken Concentration is canceled and, once per turn, you may subtract 2 from attrition against you.", ""},
            {"216_43", null, "While Obi-Wan 'communing,' no Force drains here and, once per turn, may subtract 2 from a just drawn [Permanent Weapon] weapon destiny.", ""},
            {"218_17", null, "Your stormtroopers here may move to an adjacent site as a 'react.'", "If you occupy, opponent's Level 6 Core Shaft Corridor game text is canceled."},
            {"219_19", "Never deploys or moves (even if carried) to a site opponent occupies. Players do not retrieve Force for initiating a battle. While Agents Of Black Sun on table, your total battle destiny is +1 where you have a Black Sun agent. Immune to attrition.", null, null},
            // Second pass (GEMP-IMG2, 12 Nov 2026): every current Virtual Modern version in vm-versions.json read from its current Holotable image.
            {"200_4", "Artillery weapons here are powered. Permanent weapon is Blaster Cannon (may target a character, creature, or vehicle at same or adjacent site for free; draw destiny; target hit, and its forfeit = 0, if destiny +1 > defense value).", null, null},
            {"200_13", "[Pilot] 3. During battle, may use 1 Force to [upload] an Interrupt, play it, then place that card out of play. Once per turn, may [upload] a non-Interrupt card with 'door' in game text.", null, null},
            {"200_26", "Plays on table. Cancels Always Thinking With Your Stomach, He Hasn't Come Back Yet, Ice Storm, and Sandwhirl. Once per game, may [upload] an Immediate Effect. Unless opponent occupies a battleground system, Mobilization Points is suspended.", null, null},
            {"200_32", "Plays on table. Cancels Scanning Crew and 'insert' cards just revealed. Sidious may not place Skywalkers out of play. Let Them Make The First Move may target only Undercover spies. Unless Inner Strength on table, opponent may use only one combat card per turn.", null, null},
            {"200_35", "Deploy on table with any number of Defensive Shields from outside your deck face-down under here. Four times per game, may play a card from here (as if from hand). Unless canceling your Interrupt, opponent may not play Uncertain Is The Future until the end of your first turn.", null, null},
            {"200_38", "Deploy on table. All immunity to attrition is -3. You may not fire [Permanent Weapon] weapons. If opponent just lost a Dark Jedi or a battle, they lose 1 Force. During your draw phase, use 3 Force or place this Effect in Used Pile. [Immune to Alter]", null, null},
            {"200_44", "Deploy on table. Star Cruisers (except Profundity) may deploy -2 (to a maximum of -3), ignore deployment restrictions in their game text, draw one battle destiny if unable to otherwise, and are immune to attrition < 4. [Immune to Alter.]", null, null},
            {"200_46", "Deploy on table. Once per game, if opponent just played an Interrupt, may stack it here. To play any new Interrupt of the same name, players must first stack it here. May place this Effect in Lost Pile (place cards here in owner's Used Pile) to retrieve a starship. Immune to Alter.", null, null},
            {"200_50", "If your starting location was a system, [download] a related site (must be a battleground if the system is a non-battleground or Endor) with < 3 [Light Side Force] and up to three Effects that are always immune to Alter. Place this Interrupt in Lost Pile.", null, null},
            {"200_62", "May add 1 pilot or passenger. Permanent pilot is •Dutch, who provides ability of 2. Opponent may not 'react' to here and must first use 1 Force to draw a card for battle destiny here.", null, null},
            {"200_64", "May add Harc as a pilot. During battle, adds one destiny to total power. If a Dark Jedi or Sith was just deployed to a related site, may [download] a related location. Immune to attrition < 4 (< 6 at Bespin).", null, null},
            {"200_72", "[Pilot] 2. Power +1 while with your leader. If at a Scomp link when opponent draws destiny of: 1-3, you may activate 1 Force; 4-6, you may draw top card of Reserve Deck; 0 or 7, you may retrieve 1 Force.", null, null},
            {"200_73", "[Pilot] 2. Chiraneau may not modify your Force drains. Kuat Drive Yards is [Immune to Alter]. Unless your Senate on table, unique (•) Star Destroyers are power +2 and immunity to attrition +1. Once per game, may [download] non-[Set 0] Kuat Drive Yards.", null, null},
            {"200_79", "While opponent's [Reflection II icon] objective on table, adds one battle destiny. Permanent weapon is •Dengar's Blaster Carbine (twice per battle, may target a character or vehicle for free; draw destiny; target hit, and its forfeit = 0, if destiny +1 > defense value).", null, null},
            {"200_80", "Permanent weapon is Twin Blaster Cannons (may target a character or vehicle; draw destiny; add 2 if targeting a vehicle; if total destiny > defense value, target hit and you may activate 1 Force). While with another [Presence] droid at a site, adds one battle destiny.", null, null},
            {"200_86", "While Luke or Emperor on table, power +1 and she moves for free. Permanent weapon is •Mara Jade's Lightsaber (may target a character or creature for free; draw two destiny; target hit, and its forfeit = 0, if total destiny > defense value).", null, null},
            {"200_91", "Deploys -1 to Coruscant. While present with Xizor, forfeit +2 and opponent's spies may not deploy to same non-battleground site. Once per game, may use 1 Force to [download] a non-war room battleground planet site (or system) not already on table.", null, null},
            {"200_95", "Plays on table. Ice Storm, Lost In The Wilderness, Order To Engage, Sandwhirl, and Scramble are canceled. Once per game, may [upload] an Immediate Effect. Unless opponent occupies a battleground system, Staging Areas is suspended.", null, null},
            {"200_100", "Plays on table. Cancels 'insert' cards just revealed. We'll Handle This may target only 5D6-RA-7 and Undercover spies. Unless Deep Hatred on table, opponent may use only one combat card per turn.", null, null},
            {"200_101", "Deploy on table. All immunity to attrition is -3. You may not fire [Permanent Weapon] weapons. If opponent just lost a Jedi or a battle, they lose 1 Force. During your draw phase, lose 1 Force or place this Effect in Used Pile. [Immune to Alter]", null, null},
            {"200_110", "Deploy on table with any number of Defensive Shields from outside your deck face-down under here. Four times per game, may play a card from here (as if from hand). Unless canceling your Interrupt, opponent may not play Recoil In Fear until the end of your first turn.", null, null},
            {"200_111", "Deploy on table. Unique (•) Imperial-class Star Destroyers draw one battle destiny if not able to otherwise and are immune to attrition < 4. During your deploy phase, may use 2 Force to [download] a battleground system. We're In Attack Position Now is canceled. Immune to Alter.", null, null},
            {"200_112", "Deploy on table. Once per game, if opponent just played an Interrupt, may stack it here. To play any new Interrupt of the same name, players must first stack it here. May place this Effect in Lost Pile (place cards here in owner's Used Pile) to retrieve a starship. Immune to Alter.", null, null},
            {"200_113", "Deploy on Death Star system. During your control phase, for each battleground system controlled by your Star Destroyer within 2 parsecs of Death Star, opponent loses 1 Force. Canceled if opponent controls this system.", null, null},
            {"200_117", "If your starting location was a system, [download] a related site (must be a battleground if the system is a non-battleground or Endor) with < 3 [Dark Side Force] and up to three Effects that are always immune to Alter. Place this Interrupt in Lost Pile.", null, null},
            {"200_120", "Once per game, choose: During battle, use 2 Force to target your Dark Jedi present with an opponent's character of equal or lesser ability. Both targets are excluded from battle. OR Exchange two cards from hand with any one card from Force Pile; reshuffle.", null, null},
            {"200_126", null, "If you control, may use 3 Force to release a captive here. Captive Pursuit is canceled.", "If Any Methods Necessary just played, may [upload] Despair. Aiiii! Aaa! Agggggggggg! is canceled."},
            {"200_130", "Permanent pilot is •Boba Fett, who provides ability of 3. Draws one battle destiny if not able to otherwise. Slave I's power may not be reduced by opponent. Immune to Eject! Eject! and attrition < 5.", null, null},
            {"200_139", "May add 1 pilot and 8 passengers. Permanent pilot provides ability of 2. During your control phase, may [upload] one Trample or Walker Barrage. Walker Barrage total is +2. Immune to attrition < 4.", null, null},
            {"200_147", "Plays on table. Ice Storm, Lost In The Wilderness, Order To Engage, Sandwhirl, and Scramble are canceled. Once per game, may [upload] an Immediate Effect. Unless opponent occupies a battleground system, Staging Areas is suspended.", null, null},
            {"200_148", "Plays on table. Cancels 'insert' cards just revealed. We'll Handle This may target only 5D6-RA-7 and Undercover spies. Unless Deep Hatred on table, opponent may use only one combat card per turn.", null, null},
            {"200_149", "Plays on table. Cancels Always Thinking With Your Stomach, He Hasn't Come Back Yet, Ice Storm, and Sandwhirl. Once per game, may [upload] an Immediate Effect. Unless opponent occupies a battleground system, Mobilization Points is suspended.", null, null},
            {"200_150", "Plays on table. Cancels Scanning Crew and 'insert' cards just revealed. Sidious may not place Skywalkers out of play. Let Them Make The First Move may target only Undercover spies. Unless Inner Strength on table, opponent may use only one combat card per turn.", null, null},
            {"201_4", "Obi-Wan's deploy cost may not be increased. Power +1 for each Dark Jedi or Skywalker here. Your 'hit' characters here may not be targeted to be lost. Once per game, may exchange two cards in hand with any one card in Lost Pile. Immune to attrition < 5.", null, null},
            {"201_13", "USED: Play a Defensive Shield from under your Starting Effect. [Immune to Sense] OR Cancel Counter Assault, [Set 0] Defensive Fire, or Hidden Weapons. OR Make a just drawn Overload destiny = ½. LOST: Cancel Cease Fire! or I Have You Now.", null, null},
            {"201_14", "USED: Play a Defensive Shield from under your Starting Effect. OR Target opponent's just-deployed [Maintenance] card. Target may not battle (or allow opponent to [download] cards) for remainder of turn. [Immune to Sense] LOST: Cancel Trample (unless targeting an Undercover spy).", null, null},
            {"201_18", "Permanent pilot is •Green Leader, who provides ability of 2. Adds one battle destiny with a Rebel snub fighter. During battle, may cancel immunity to attrition of one starship here; this A-wing is 'hit.'", null, null},
            {"201_23", "[Pilot] 1. When deployed, may take an ISB agent here into hand. Once per turn, if you just drew an ISB agent for battle destiny anywhere, may take that card into hand to cancel and redraw that destiny.", null, null},
            {"201_33", "If opponent's starting location was Massassi Throne Room, opponent loses 1 Force when you play this Interrupt. Add or subtract 1 from opponent's just drawn destiny. OR Activate 1 Force. OR Until end of turn, battle destiny draws may not be canceled (unless being redrawn) or modified.", null, null},
            {"201_41", "May add 6 pilots, 8 passengers, 2 vehicles and 4 TIEs. Permanent pilot provides ability of 1. Once per game, may follow an opponent's starship that just moved from here (if within range).", null, null},
            {"202_5", "If an Endor site on table, deploy on table. [Death Star II] Luke is deploy = 6 and may not be Disarmed. If Luke is at a battleground (even as a captive), your battle destiny draws are +1 there and, once during your turn, may [download] Luke's Lightsaber or place a card from hand on Force Pile. Immune to Alter.", null, null},
            {"202_7", "May add 1 pilot and 1 astromech. During battle, if Anakin or R2-D2 aboard, may lose 1 Force to cancel a non-[Immune to Sense] Interrupt. While Anakin or R2-D2 aboard, immune to attrition < 5 (< 6 if both).", null, null},
            {"202_9", "[Pilot] 2. Information broker. When deployed, may [download] a droid here or a Restraining Bolt. During battle, may retarget an opponent's weapon targeting your droid to another one of your characters present (even Daroe).", null, null},
            {"202_15", "May add 1 pilot. May move as a 'react.' Permanent pilot provides ability of 2. Unless this vehicle 'hit,' your other vehicles here may not be targeted by artillery weapons, vehicle weapons, or opponent's Interrupts.", null, null},
            {"203_4", "[Pilot] 3. Adds 2 to hyperspeed of any freighter he pilots. During your move phase, may use 1 Force to 'break cover' of opponent's Undercover spy at same site. Cancels Lateral Damage targeting your starship at same or related system.", null, null},
            {"203_18", "If your Padawan or Skywalker is in battle with a Dark Jedi, either add one battle destiny or cancel I Have You Now. OR If Luke is in battle with an opponent's character of ability > 3, add one battle destiny.", null, null},
            {"203_20", "May add 2 pilots and 6 passengers. Permanent pilot provides ability of 2. If in battle with another Rebel capital starship, may draw destiny. Subtract that amount from opponent's attrition and total power.", null, null},
            {"203_21", "May add 3 pilots and 6 passengers. Permanent pilot provides ability of 2. During battle, your total battle destiny is +1 for each smuggler aboard. Deploys and moves like a starfighter. Immune to attrition < 5.", null, null},
            {"204_29", null, "Once per game, may [download] Rey here. Rey deploys -2 here. If Rey present, she may not be targeted by weapons.", ""},
            {"204_47", "Deploy on table. Once per game, may [upload] Finalizer. Once per character, when you deploy Hux, Kylo, Phasma, or Snoke to an [Episode VII] battleground, may take any one card into hand from Used Pile; reshuffle. Immune to Alter.", null, null},
            {"222_30_BACK", "While this side up, attrition against opponent is +1 for each Imperial leader in battle. Rebel Leadership and We're Doomed are Lost Interrupts. Once per turn, may [download] a snowtrooper. Once during your control phase, may choose: retrieve bottom card of Lost Pile into hand or peek at the top X cards from opponent's Reserve Deck, where X = number of Hoth locations you control; replace in any order. Place out of play if you do not occupy a Hoth site with an AT-AT, Imperial leader, or snowtrooper.", null, null},
            {"223_15", "[Pilot] 2. Once per game, may [upload] Gideon. While piloting a speeder bike, Grogu is forfeit -2 here and opponent's weapon destinies targeting your speeder bikes are -1 here.", null, null},
            {"224_5", null, "Your smugglers and [Ind] starships deploy -1 here.", "Your [Ind] starships may move here as a 'react.' Once per game, if you control, may [upload] Death Mark."},
            {"225_28", null, "Unless your piloted [Resistance] starship at a system, Force drain -1 here.", "While you control, Supremacy is power +3."},
            {"220_2", "Plays on table. At end of a turn, if opponent has 15 or more cards in hand, may use 3 Force to shuffle all but 9 (random selection) into Used Pile. If Grimtaash or Thrown Back just targeted your hand, may reveal 3 cards from hand; they cannot be removed or checked for duplicates.", null, null},
            {"220_10", "Plays on table. At end of a turn, if opponent has 15 or more cards in hand, may use 3 Force to shuffle all but 9 (random selection) into Used Pile. If Monnok or Drop! just targeted your hand, may reveal 3 cards from hand; they cannot be removed or checked for duplicates.", null, null},
            {"219_24", "If an Imperial leader just won a battle, opponent loses 1 Force (and stacks it on Thrawn's Art Collection if possible). OR Suspend Ancient Watering Hole for remainder of turn. OR If you have 4 artwork cards, you retrieve 1 Force and opponent loses 1 Force.", null, null},
            {"219_11", null, "Opponent may not reduce or cancel your Force drains here.", "Once per turn, may [download] TIE Defender Project, Pryce, or Thrawn here."},
            // Third pass (GEMP-IMG3, 13 Nov 2026): current versions in vm-versions.json by vm-b2, vm-c2, vm-a4 (ALT) and vm-d2,
            // read from the current Holotable image; Luke Skywalker, The Emperor's Prize (205_14) from its E1 images
            // (16 Aug 2023). "200_20^" is the ALT printing of Luke Skywalker (V), whose image words its text differently.
            {"211_43", null, "Once per turn, may use 1 Force to [download] a Kamino site.", "If opponent has two clones on table, Force drain -1 here."},
            {"211_37", null, "During your deploy phase, if you just deployed a Maz's Castle location, may draw top card of Reserve Deck.", "If you just deployed a starship here, may shuffle any deck or pile."},
            {"211_12", "If you just lost a Dark Jedi and have exactly one Dark Jedi on table, [upload] a Dark Jedi. OR Once per game, if you have exactly two Dark Jedi on table, choose: Opponent loses 2 Force. OR Make a just drawn destiny = 2.", null, null},
            {"211_25", "Deploy on Aurra, Grievous, or your warrior of ability > 4. May add 1 to Force drain where present. May target a character or creature for free. Draw two destiny. Target hit, and may not be used to satisfy attrition, if total destiny > defense value.", null, null},
            {"211_11", "If A Stunning Move on table, deploy on 500 Republica. Opponent's Force generation is -1 here. While at a site (even while on a captive), adds one [Dark Side] icon here. While on Coruscant, opponent loses no more than 1 Force from your Force drains here. Once per turn, if a player controls this site, they may have this card (unless on Palpatine) follow their first character to move from here using landspeed (or docking bay transit) to a battleground site. If about to leave table, relocate to 500 Republica (if possible).", null, null},
            {"211_5", "Non-[Maintenance] bounty hunters here are immune to Clash Of Sabers and Rebel Barrier. Whenever you draw a destiny of 3, may choose: activate 1 Force, draw bottom card of Used Pile, or place a card from hand on top of Used Pile.", null, null},
            {"211_26", "Deploy 500 Republica (with Insidious Prisoner there) and Coruscant: Private Platform.\nFor remainder of game, you may not deploy Sidious, First Order characters, or Imperials. Grievous is immunity to attrition +2. Once per turn, may [download] an Invisible Hand site or a non-unique [Separatist] droid. \nFlip this card if Insidious Prisoner is at an Invisible Hand site.", null, null},
            {"211_15", "If Audience Chamber on table, [download] Passenger Deck or a pit. OR [download] an alien leader or a Twi'lek to Audience Chamber.", null, null},
            {"211_17", null, "May not be separated from Coruscant: Private Platform.", "Grievous moves from here for free."},
            {"211_20", null, "Unless your [Clone Army] character here, Force drain -1 here.", "Once per game, if you control, may [upload] Invisible Hand."},
            {"211_27", null, "While your [Clone Army] character here, your characters are defense value +1 here.", "While Insidious Prisoner here, your characters are power +1 here."},
            {"211_45", null, "While Luke here, opponent must first use 1 Force to fire a weapon anywhere.", "While Luke here, destiny draws for Sense and Alter are +2."},
            {"211_42", null, "During your deploy phase, your clone here may move to a site you occupy for free.", "If you initiate battle here, add one battle destiny. Fetts may move to and from here for free."},
            {"211_41", null, "While you control, your non-unique clone troopers are power and forfeit +1.", "Fetts deploy -1 here."},
            {"211_54", "Deploy on an alien of ability < 3. While present with a Scomp link, Computer Interface is a Used Interrupt and, once per turn, may use 1 Force to [upload] Computer Interface. Once per turn, if on Lobot, may [download] a trooper here.", null, null},
            {"211_33", "Deploy on your character of ability > 4 or Padawan. May add 1 to Force drain where present. May target a character or creature for free. Draw two destiny. Target hit, and may not be used to satisfy attrition, if total destiny > defense value.", null, null},
            {"211_34", "May add 2 pilots and 2 passengers. During your deploy phase, may relocate your character of ability < 4 aboard from a related exterior site. While BB-8, DJ, or Rose aboard, immune to attrition < 5.", null, null},
            {"211_30", "LOST: [download] a Tatooine battleground. STARTING: If you have deployed a Jabba's Palace site, deploy Seeking An Audience and up to two Effects that deploy on table and are always immune to Alter. Place Interrupt in hand.", null, null},
            {"211_35", "Permanent pilot is •Tallie, who provides ability of 2. May move as a 'react' to same location as your [Episode VII] character. Characters here may not have their forfeit increased above their printed value.", null, null},
            {"211_55", "[Pilot] 2: any capital starship. During battle, if piloting a capital starship, may place it and Holdo out of play to add X to your total battle destiny, where X = that starship's armor + hyperspeed.", null, null},
            {"211_1", "[Pilot] 3. Once per turn, may target a related location; opponent loses 1 Force the next time they move to that location this turn. May lose 1 Force to cancel a just drawn weapon destiny targeting a starship he is piloting. Thrawn's game text may not be canceled.", null, null},
            {"204_57", "Deploy on your First Order warrior. May target a character or vehicle for free. Draw destiny. Add 1 if fired by a stormtrooper. Target hit, and may not be used to satisfy attrition, if total destiny > defense value.", null, null},
            {"204_40", "Deploys -1 to same site as a First Order leader. Opponent's characters here are cumulatively defense value -1 (limit -3).", null, null},
            {"204_3", "[Pilot] 3. If with an Imperial (or two Rebels), adds one battle destiny. During battle, if you just drew a destiny < number of Rebels here, may cancel and redraw that destiny.", null, null},
            {"204_34", "May add Poe (or a spy) as a pilot and 1 passenger. Deploys free to opponent's system. While Poe piloting, maneuver +2. If Finn aboard, apply his ability toward drawing battle destiny.", null, null},
            {"206_10", "While you have no other trooper here (except death troopers), adds one battle destiny at same site as Krennic, Tarkin, or Thrawn. May place this character in Used Pile to cancel a just drawn weapon destiny targeting your leader here.", null, null},
            {"205_6", null, "Anakin, Beru, and Owen may move to or from here for free when using landspeed.", "Unless Vader or your trooper here, Force drain -1 here (if both here, Force drain +1 instead)."},
            {"204_35_BACK", "May not be placed in Reserve Deck. If Falcon about to leave table, place it out of play. May add 2 pilots and 2 passengers. Immune to Trample and Unsalvageable, even if unpiloted. While Finn or Rey aboard, immune to attrition < 4 (< 6 if both). Once during your move phase, if piloted, may flip this card.", null, null},
            {"218_4", "During battle, Mace may 'swing' one lightsaber twice. Power +1 for each opponent's character of ability < 2 here. Your clones are forfeit +1 here. Immune to attrition < 6.", null, null},
            {"205_14", "Luke Skywalker, The Emperor's Prize is a Dark Side card and is a persona of Luke only while on table. Plays only during start of game by revealing from outside your deck if Your Destiny is on table and suspended; deploy to Throne Room as a frozen captive. If not revealed, place under your Starting Effect. While this side up, Bring Him Before Me may not flip. May not be escorted. Flip this card if Vader here and not escorting a captive. Place out of play if released or about to leave table.", null, null},
            {"205_14_BACK", "Immediately 'thaw' Luke (Luke is non-frozen, captured, and seized by Vader). While this side up, subtracts 3 from attempts to cross Luke over (even while a captive). Place out of play if released or about to leave table.", null, null},
            {"200_20^", "Adds 3 to power of anything he pilots (and to Attack Run total if piloting lead starfighter). If piloting at a battleground during your control phase, may retrieve 1 Force or take Darklighter Spin into hand from Reserve Deck; reshuffle.", null, null},
            // Fourth pass (GEMP-IMG4, 13 Nov 2026): current versions in vm-versions.json by vm-b3, vm-b4, vm-c3, vm-d3 and vm-fix
            // (Virtual Sets 7-10 and 12-20, Virtual Set 0 shields, ALT printings), read from the current Holotable image.
            {"203_13", "Plays on any site. This location may not be targeted by Proton Bombs. Cancels Program Trap here. Unless Death Star: Docking Bay 327 on table, Commence Primary Ignition is canceled.", null, null},
            {"207_3", "If at least two [LS] icons here, once during your control phase may draw top card of Reserve Deck. If at least two [DS] icons here, may forfeit in place of your hit non-Jedi character present with Depa (forfeit for 0), restoring that character to normal.", null, null},
            {"207_6", "[Pilot] 2. Matching pilot for any snub fighter. While piloting a snub fighter, draws one battle destiny if unable to otherwise and opponent may not substitute battle destiny draws here.", null, null},
            {"207_8", "If opponent just looked at cards in their deck or pile, after replacing, you may peek at the top card of that deck or pile. At the start of your control phase, if the top card of your Lost Pile is a Resistance character or [Resistance] starship, opponent loses 1 Force.", null, null},
            {"207_9", "Once per turn, when firing a blaster or rifle, may target for free and add 2 to total weapon destiny. While with an Imperial (or two Rebels), whenever you win a battle here, opponent loses 2 Force. Immune to Hidden Weapons.", null, null},
            {"207_10", "May deploy to Dagobah. Prevents attacks at same Dagobah site. Your training destiny draws are +1. If Great Warrior on table, once per turn may take your just drawn destiny into hand to cancel and redraw that destiny. Immune to attrition.", null, null},
            {"207_11", "Deploy on table. Your Force generation is +1 at docking bays you occupy. Once per game, may [upload] one Dantooine, Mon Calamari, or Yavin 4 system or Profundity. Staging Areas is canceled. [Immune to Alter.]", null, null},
            {"207_24", "[Pilot] 2, 3: Meson Martinet. Once per turn, may place opponent's character just lost from here out of play unless opponent loses 1 Force (2 if your Rep is a pirate or you did not deploy an Objective).", null, null},
            {"207_26", "Deploy on a location. Your Force generation is +1 here. Once per game, you may restore a just 'hit' starship to normal. Unless opponent occupies a battleground site, Corellian Engineering Corporation is suspended. [Immune to Alter.]", null, null},
            {"207_28", "May add 2 pilots and 3 passengers. Your total battle destiny here is +1 for each pirate aboard. While Quiggold or Sidon piloting, immune to attrition < 5.", null, null},
            {"207_30", "[Pilot] 3. If a Rebel just moved from here, your Imperials present may follow that character (using landspeed). While at a battleground site, functions as a general and game text of Admiral's Orders is canceled.", null, null},
            {"208_21", "Once per turn, if drawn for destiny, you may activate 1 Force. During battle at a site, choose: Your total power is +1 for each of your characters present. OR If you have a weapon present, opponent must use or lose 1 Force to draw their first battle destiny.", null, null},
            {"208_39", "Use 4 Force to deploy on table. While present with a Jedi, destroyer droids are defense value +2. If your destroyer droid was just lost, may stack it here. During your control phase, may use 2 Force to place any destroyer droid here in Used Pile. [Immune to Alter]", null, null},
            {"208_56", null, "For remainder of game, may not be separated from Watto's Junkyard.", null},
            {"209_19", "USED: Cancel Broken Concentration, Lateral Damage, or Limited Resources. OR Place a card just stacked on Droid Racks or Strategic Reserves in opponent's Lost Pile. [Immune to Sense.] OR If you just drew a starship for destiny, take that starship into hand to cancel and redraw that destiny. LOST: Use 3 Force to retrieve an Effect of any kind or a non-[Maintenance] starship into hand.", null, null},
            {"209_20", "USED: If present with a Dark Jedi or [Episode I] Jedi, your Padawan is power +2 for remainder of turn. OR [download] one [Episode I] lightsaber. LOST: Once per game, retrieve a Padawan or [Episode I] lightsaber.", null, null},
            {"209_30", "Permanent pilot provides ability of 2. When in battle, adds 1 to attrition against opponent for each opponent's starship present. Cancels opponent's immunity to attrition here.", null, null},
            {"209_38", "Deploys -1 to Executor. While aboard a capital starship, adds 1 to armor and its defense value may not be reduced. If Fear Will Keep Them In Line here and your Imperial just won a battle at a related site, opponent loses 2 Force.", null, null},
            {"209_41", "Deploy on table. Once per game, may [upload] a [Set 9] Epic Event. During your deploy phase, if Krennic on Death Star, may flip Set Your Course For Alderaan (flip back at end of turn). [Immune to Alter.]", null, null},
            {"209_42", "Deploy on table. AT-AT Cannons deploy -1, fire for free, and add 1 to armor. During your control phase, may [upload] a non-[Jabba's Palace] vehicle weapon (without 'lost' in game text), [Hoth] device, or [Hoth] Epic Event. [Immune to Alter.]", null, null},
            {"209_44", "Deploy on table. Once per turn, may [download] Maul's Lightsaber. At start of opponent's control phase, may relocate Maul to same site as a Jedi. May place this Effect out of play to deploy Maul's Lightsaber from Lost Pile. [Immune to Alter.]", null, null},
            {"209_51", null, null, "Once per game, may exchange an alien in hand with a non-spy Black Sun agent in Lost Pile."},
            {"210_24", "USED: Cancel Elis Helrot or Stunning Leader. [Immune to Sense.] OR Cancel a smuggler's game text for remainder of turn. OR [upload] Mercenary Armor or [Reflections II] Chewie. LOST: During your move phase, 'break cover' of an Undercover spy.", null, null},
            {"210_25", "Deploy City Outskirts, Watto's Junkyard, and Credits Will Do Fine. For remainder of game, you may not deploy cards with ability except unique (•) aliens, Republic characters, [Republic] starships, and [Episode I] Jedi. Your Destiny is suspended. You lose no Force from [Reflections II] objectives. While this side up, once per game, may [upload] an [Episode I] system. Unless present with Qui-Gon, Maul is immune to attrition. Flip this card if there are four or more cards beneath Credits Will Do Fine.", null, null},
            {"210_28", "May add 1 pilot. While with a Black Squadron TIE, Force drain +1 here. While DS-61-3 piloting, immune to attrition < 4 and, during battle, players draw destiny from the bottom of their Reserve Deck.", null, null},
            {"210_30", "[Pilot] 2, 3: Judicator. Your total battle destiny here is +1 for each TIE subtype here or aboard same starship. During your turn, may [upload] one launch bay or non-unique TIE.", null, null},
            {"210_41", "USED: Cancel Nabrun Leids. [Immune to Sense.] OR Use 1 Force to [upload] a Rodian (or a non-unique alien of your Rep's species). LOST: During your move phase, \"break cover\" of an Undercover spy.", null, null},
            {"210_43", "Deploy on table. X = number of your devices on table. Once during your turn, may peek at the top X cards of your Force Pile and take one into hand, or [download] (or deploy from Lost Pile) a device (except Restraining Bolt or Tractor Beam). [Immune to Alter.]", null, null},
            {"210_46", "[Pilot] 2. If a Jedi or Padawan just lost from same site as any Inquisitor, opponent loses 1 Force. If a Jedi or Padawan just moved from here, Inquisitors present may follow that character (using landspeed). Immune to attrition < 4.", null, null},
            {"212_5", "[Pilot] 2. Unless alone, may place Trench in Used Pile to make an [Episode I] starship here immune to attrition for remainder of turn. While at opponent's system, their starships deploy +1 here.", null, null},
            {"213_1", "[Pilot] 2. Matching pilot for any Death Squadron Star Destroyer and, while piloting one, instead of Force draining here, may draw top card of Reserve Deck or raise your same or related converted location to the top.", null, null},
            {"213_5", "[Pilot] 2. May deploy or move as a 'react' to same site as a 'Hatred' card. During your move phase, unless alone, may return one of your Inquisitors here (and all cards on them) to owner's hand.", null, null},
            {"213_11", "Defense value -2 while opponent has a weapon present. While at a site, unless opponent's character of ability > 3 here, reset opponent's total battle destiny here to 0.", null, null},
            {"213_14", "Deploy on a battleground planet system. Adds one [Light Side] icon at your related Big One. If a sector was just drawn for asteroid destiny, cancel and redraw that destiny. Once per turn, may [download] a sector to this system. [Immune to Alter.]", null, null},
            {"213_19", "USED: [upload] First Light or Vos. OR Place a character from hand in Used Pile to activate 1 Force (2 if Vos on table). LOST: Once per game, exchange two cards in hand with any one card in Lost Pile. [Immune to Sense.]", null, null},
            {"213_20", "USED: If opponent's character is about to be lost, place all 'Hatred' cards (if any) on that character in owner's Used Pile. LOST: Lose Vader or an Inquisitor from hand to [upload] up to two Inquisitors.", null, null},
            {"213_21", "[upload] Krennic or a non-spy captain. OR Peek at the bottom card of any deck or pile. OR During the power segment of a battle, if your captain in battle with your leader, cancel one destiny just drawn (except a battle destiny).", null, null},
            {"213_23", null, null, "May [download] [Set 13] Maul here."},
            {"213_24", null, null, "While your Imperial here, [Dagobah] Executor is deploy -7 (except to Fondor)."},
            {"213_25", null, null, "Once per game, may [download] Margo, Wuher, or a musician here."},
            {"213_26", null, null, "During your move phase, your Crimson Dawn leader may move between here and a site."},
            {"213_29", null, "Once per game, you may [download] a Padawan (except Anakin) here.", null},
            {"213_31_BACK", "While this side up, you lose no Force from Visage Of The Emperor. While Vader armed with a lightsaber, opponent's Force drain bonuses are canceled.\nFlip this card if Vader not on table or if Luke, a Jedi, or a Padawan at a battleground site; once per game, may take Vader and your cards on him into hand from a site you control.", null, null},
            {"213_32", "Deploy Maul's Chambers. If Massassi Throne Room on table, may [download] [Set 13] Maul to Maul's Chambers. For remainder of game, you may not deploy cards with ability (or [Episode I] droids) except characters with 'Black Sun,' 'Crimson Dawn,' or 'Hutt' in lore, assassins, gangsters, [Episode I] bounty hunters, and [Independent] starships. Once per turn, may [download] a non-unique blaster (or a card with 'First Light' in title). Flip this card if you just 'hit' a character (or during your battle phase if your gangsters control two battlegrounds).", null, null},
            {"213_32_BACK", "May immediately re-circulate and shuffle your Reserve Deck. While this side up, if your gangster leader in battle at same site as your non-unique blaster, may add one destiny to total power. During your draw phase, if Maul alone, may peek at the cards in your Force Pile. Flip this card at end of turn. If you are about to flip this card and you occupy three battlegrounds, opponent loses 1 Force.", null, null},
            {"213_36", "[Pilot] 2, 3: Falcon. May deploy -1 as a 'react' (-2 to same location as Han). If you have completed a Kessel Run, opponent's Force retrieval is canceled. During battle here (or at a holosite), your dejariks are destiny +2.", null, null},
            {"213_38", "While aboard (or stacked on) a freighter, adds 1 to power, maneuver, and hyperspeed, and when forfeiting L3-37, stack her on that freighter. May lose L3-37 to cancel a non-[Immune to Sense] Interrupt targeting a freighter she is aboard or stacked on.", null, null},
            {"213_40", "Once per game, may deploy Meditation on Qui-Gon from Lost Pile. If alone during battle: your total battle destiny is +1 for each weapon destiny draw that was completed while alone this battle, and opponent may not cancel your destiny draws. Immune to attrition.", null, null},
            {"213_41", "[Pilot] 2. Once per turn, if you just deployed a smuggler here, may place a card from hand in Used Pile to activate 2 Force. Once per game, if you just won a battle here, may steal a starfighter from opponent's Lost Pile into hand.", null, null},
            {"213_47", "Deploy on your character of ability > 3. Opponent's aliens here are power -1 (and forfeit -2 if a leader, [Maintenance], or [Permanent Weapon] card). During battle at same site, if opponent has more characters participating than you, may add one battle destiny (then lose Effect at end of battle).", null, null},
            {"213_49", "During any move phase, if I Feel The Conflict on table, [upload] [Endor] Leia or [Dagobah] Luke. OR During a battle at a site involving [Death Star II] Luke, choose: Add one destiny to attrition. OR Cancel the game text of a character of ability < 4.", null, null},
            {"213_51", "USED: [upload] Kessel Run, L3-37, or Lando. OR If you just moved a 'coaxium' card to Used Pile, randomly retrieve 1 Force. LOST: Once per game, place a card stacked on [Set D] A Useless Gesture in your Lost Pile.", null, null},
            {"213_56", null, "While Luke alone here, he is power +2.", null},
            {"214_1", "This Interrupt is immune to opponent's Objective and does not cause you to lose Force to Do, Or Do Not. USED: Cancel Sense. LOST: Target an Effect, Political Effect, or Utinni Effect, and one of your characters on table. Draw destiny. If destiny < character's ability, target Effect is canceled. OR If a battle was just initiated at a site where opponent has four or more characters, draw destiny. If destiny < number of opponent's characters at that site, opponent chooses one to be lost.", null, null},
            {"214_4", null, null, "Once per game, may [download] [Episode VII] Emperor here."},
            {"214_9", "May add 6 pilots, 8 passengers, and 2 starfighters. Permanent pilot provides ability of 2. While alone at an [Episode VII] system, adds Force icons to equalize them for both sides here. Immune to attrition < 4.", null, null},
            {"214_11", "If I Will Finish What You Started on table, deploy on table. While [Episode VII] Emperor on Exegol, attrition against opponent is +2 at same location as Kylo, Rey, or Snoke and, once per game, may [download] an [Episode VII] battleground system. [Immune to Alter.]", null, null},
            {"214_13", "This Interrupt is immune to opponent's Objective and does does not cause you to lose Force to There Is No Try. USED: Cancel Sense. LOST: Target an Effect, Political Effect, or Utinni Effect, and one of your characters on table. Draw destiny. If destiny < character's ability, target Effect is canceled. OR If a battle was just initiated at a site where opponent has four or more characters, draw destiny. If destiny < number of opponent's characters at that site, opponent chooses one to be lost.", null, null},
            {"214_15", "Deploy on a Jedi or Padawan. May target a character or creature for free. Draw two destiny. Target hit, and may not be used to satisfy attrition, if total destiny > defense value. If hit by Ahsoka or a Padawan, opponent loses 1 Force.", null, null},
            {"214_16", "At same and related locations, deploy cost of opponent's characters may not be modified. If you have ten or more cards in Lost Pile, Force drain +1 here. Once during battle, may add Beaumont's power to another character present; Beaumont is 'hit'.", null, null},
            {"214_18", "[Pilot] 2. While with an alien, adds one battle destiny. Once per turn, may target opponent's non-'hit' character here. If Din on table when target lost this turn, choose: Retrieve 1 Force or place target in Used Pile to retrieve 2 Force.", null, null},
            {"214_21", "May add 1 Jedi pilot. * Maneuver = pilot's ability. While Plo piloting, power +2, immune to attrition < 3, and once per turn, may cancel and redraw your weapon or battle destiny just drawn here.", null, null},
            {"214_22", "Once during your control phase, may search your Lost Pile and choose two cards; opponent places one out of play (retrieve the other). Once per game, may [download] a lightsaber on Rey. Immune to attrition < 5.", null, null},
            {"215_1", "May add 1 clone pilot. Odd Ball deploys -1 aboard. While Odd Ball piloting, power and hyperspeed +1, and adds one destiny to total power. Immune to attrition < 3 (< 5 while Odd Ball piloting).", null, null},
            {"215_2", "Deploy on Central Core. Once per game, opponent may stack up to two cards from hand face-down here. All This Sneaking Around: Path Of Least Resistance is canceled. While no cards stacked here, Leia moves using landspeed for free. Allow The Ship To Leave: If you just won a battle at a Death Star site (or initiated a Force drain here), place a card stacked here in owner's Lost Pile. That Old Man Did It!: If you occupy this site and another Death Star site while no cards stacked here, for remainder of game, power 'shut down.'", null, null},
            {"215_4", "Once per game, if you are about to draw a card for battle destiny here, may instead use Cal Kestis' ability number. During opponent's draw phase, may place one card from your hand under Reserve Deck; reshuffle and draw top card of Reserve Deck.", null, null},
            {"217_47", null, "Once per turn, may [download] Shmi or [Tatooine] C-3PO here.", null},
            {"301_5", "Plays on table. Cancels A Dangerous Time, Bad Feeling Have I, Colo Claw Fish, Field Promotion, and Imperial Supply. Each player may play only one card with 'sabacc' in title each turn. You may lose 1 Force to cancel a card with 'sabacc' in title.", null, null},
    };

    /** Lore read from the current PC card image (fourth pass). */
    private static final String[][] LORE = {
            {"204_35_BACK", "The Millennium Falcon's well-known reputation is favorable not only for its captain and first mate, but for the Alliance as well. Enclosed."},
            {"208_16", "Female dancer from Askajia. Very protective mother. Makes sure that all those she cares about are well protected."},
            {"208_32", "Fiercely devoted Royal Guard. Feels deeply indebted to those that risk their life for him. Unaware of the extent of Palpatine's atrocities and cruelty."},
            {"208_38", "For important Imperial dignitaries, an individual Star Destroyer is placed at their personal disposal."},
            {"210_40", "Commanding Viceroy of the Trade Federation forces assigned to the blockade of Naboo. Takes responsibility for his own actions. Neimoidian leader."},
            {"213_50", "A pair of dice dangling above the Millennium Falcon's cockpit, for luck. 'I've never seen anything to make me believe there's one, all-powerful Force controlling everything.'"},
    };

    @Test
    public void GameTextMatchesTheCurrentPcCardImage() {
        SwccgCardBlueprintLibrary library = new SwccgCardBlueprintLibrary();
        for (String[] row : CARDS) {
            SwccgCardBlueprint card = library.getSwccgoCardBlueprint(row[0]);
            if (row[1] != null)
                assertEquals(row[0], row[1], card.getGameText());
            if (row[2] != null)
                assertEquals(row[0] + " Light Side", row[2], card.getLocationLightSideGameText());
            if (row[3] != null)
                assertEquals(row[0] + " Dark Side", row[3], card.getLocationDarkSideGameText());
        }
    }

    private static VirtualTableScenario lothalScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("lothal", "219_38"); // Lothal (Light Side system)
                }},
                new HashMap<>() {{
                    put("thrawn", "10_40");  // Grand Admiral Thrawn
                    put("trooper", "1_194"); // Stormtrooper
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    /** Lothal (current image, 30 Dec 2024): "Thrawn and Pryce deploy -1 (and shuttle for free) to here." Shuttling, not every move, is free. */
    @Test
    public void LothalThrawnShuttlesForFreeToHere() {
        var scn = lothalScenario();
        var lothal = scn.GetLSCard("lothal");
        var thrawn = scn.GetDSCard("thrawn");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveLocationToTable(lothal);
        scn.MoveCardsToLocation(site, thrawn, trooper);
        scn.SkipToDSTurn(Phase.MOVE);
        var mq = scn.game().getModifiersQuerying();
        assertEquals(0, mq.getShuttleCost(scn.gameState(), thrawn, site, lothal, 0), scn.epsilon);
        assertTrue(scn.game().getModifiersQuerying().getModifiersAffectingCard(scn.gameState(), ModifierType.MOVES_FREE_TO_LOCATION, thrawn).isEmpty());
        assertFalse(scn.game().getModifiersQuerying().getModifiersAffectingCard(scn.gameState(), ModifierType.SHUTTLES_FOR_FREE_TO_LOCATION, thrawn).isEmpty());
        assertTrue(scn.game().getModifiersQuerying().getModifiersAffectingCard(scn.gameState(), ModifierType.SHUTTLES_FOR_FREE_TO_LOCATION, trooper).isEmpty());
    }

    /**
     * Stats printed on the current PC card images where the PC card data differs (VM-A3, 12 Nov 2026): Luke Skywalker,
     * Rebel Scout (V) forfeit 9 (data 7); Obi-Wan Kenobi (V) deploy 5 (data 6); Darth Maul, Lone Hunter deploy 6 (data 7);
     * Droideka armor 3 (the 2022 image printed ABILITY 3; the current image prints ARMOR 3).
     */
    @Test
    public void StatsMatchTheCurrentPcCardImage() {
        SwccgCardBlueprintLibrary library = new SwccgCardBlueprintLibrary();
        SwccgCardBlueprint luke = library.getSwccgoCardBlueprint("200_21");
        assertEquals(6, luke.getDeployCost(), 0);
        assertEquals(6, luke.getPower(), 0);
        assertEquals(6, luke.getAbility(), 0);
        assertEquals(9, luke.getForfeit(), 0);
        SwccgCardBlueprint obiWan = library.getSwccgoCardBlueprint("201_4");
        assertEquals(5, obiWan.getDeployCost(), 0);
        assertEquals(5, obiWan.getPower(), 0);
        assertEquals(6, obiWan.getAbility(), 0);
        assertEquals(9, obiWan.getForfeit(), 0);
        SwccgCardBlueprint maul = library.getSwccgoCardBlueprint("203_26");
        assertEquals(6, maul.getDeployCost(), 0);
        assertEquals(7, maul.getPower(), 0);
        assertEquals(6, maul.getAbility(), 0);
        assertEquals(8, maul.getForfeit(), 0);
        SwccgCardBlueprint droideka = library.getSwccgoCardBlueprint("200_80");
        assertTrue(droideka.hasArmorAttribute());
        assertEquals(3, droideka.getArmor(), 0);
        assertEquals(3, droideka.getPower(), 0);
        assertEquals(3, droideka.getDeployCost(), 0);
        assertEquals(3, droideka.getForfeit(), 0);
    }

    private static VirtualTableScenario likeMyFatherScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("effect", "202_5");   // Like My Father Before Me (current image)
                    put("luke", "1_19");      // Luke Skywalker
                    put("saber", "9_90");     // Luke's Lightsaber
                    put("site", "1_133");     // Tatooine: Mos Eisley (battleground)
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");  // Stormtrooper
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    /**
     * Like My Father Before Me (current image, 202_5): "...and, once during your turn, may [download] Luke's Lightsaber or
     * place a card from hand on Force Pile." GEMP used to offer the Lightsaber download once per turn in either turn.
     */
    @Test
    public void LikeMyFatherBeforeMeDownloadsTheLightsaberOnlyDuringYourTurn() {
        var scn = likeMyFatherScenario();
        var effect = scn.GetLSCard("effect");
        var luke = scn.GetLSCard("luke");
        var saber = scn.GetLSCard("saber");
        var site = scn.GetLSCard("site");
        scn.StartGame();
        scn.MoveLocationToTable(site);
        scn.MoveCardsToLSSideOfTable(effect);
        scn.MoveCardsToLocation(site, luke);
        scn.MoveCardsToTopOfLSReserveDeck(saber);
        assertTrue(Filters.battleground.accepts(scn.game(), site));

        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSPass();
        assertTrue(scn.LSDecisionAvailable(""));
        assertFalse(scn.LSCardActionAvailable(effect, "Deploy Luke's Lightsaber"));

        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSCardActionAvailable(effect, "Deploy Luke's Lightsaber"));
    }

    /**
     * Jakku: Rey's Encampment (current image, 204_29) prints all its text on the Light Side ("Once per game, may ▼ Rey
     * here. Rey deploys -2 here. If Rey present, she may not be targeted by weapons."); the Dark Side is blank. The
     * weapons immunity now comes from the Light Side text.
     */
    @Test
    public void ReysEncampmentProtectsReyFromItsLightSideText() {
        var scn = new VirtualTableScenario(
                new HashMap<>() {{
                    put("camp", "204_29");
                    put("rey", "204_009");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
        var camp = scn.GetLSCard("camp");
        var rey = scn.GetLSCard("rey");
        scn.StartGame();
        scn.MoveLocationToTable(camp);
        scn.MoveCardsToLocation(camp, rey);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertEquals("", camp.getBlueprint().getLocationDarkSideGameText());
        assertFalse(scn.game().getModifiersQuerying().getModifiersAffectingCard(scn.gameState(), ModifierType.MAY_NOT_BE_TARGETED_BY, rey).isEmpty());
    }

    /**
     * An ALT printing whose current image words its game text differently has a class of its own (deck id "200_20^"
     * loads Card200_020ai); it is the same card as the regular printing. Cad Bane (ALT) prints the regular card's
     * text, so "203_24^" stays the regular class.
     */
    @Test
    public void AlternateImagePrintingWithItsOwnWordingHasItsOwnClass() {
        SwccgCardBlueprintLibrary library = new SwccgCardBlueprintLibrary();
        SwccgCardBlueprint base = library.getSwccgoCardBlueprint("200_20");
        SwccgCardBlueprint alt = library.getSwccgoCardBlueprint("200_20^");
        assertTrue(alt != base);
        assertTrue(base.getClass().isInstance(alt));
        assertEquals(base.getTitle(), alt.getTitle());
        assertEquals(base.getUniqueness(), alt.getUniqueness());
        assertEquals(base.getDeployCost(), alt.getDeployCost(), 0);
        assertEquals("200_20", library.getBaseBlueprintId("200_20^"));
        assertTrue(library.getSwccgoCardBlueprint("203_24^") == library.getSwccgoCardBlueprint("203_24"));
        assertEquals("[Pilot] 2. Your other bounty hunters here are power +1. Draws one battle destiny if unable to otherwise. During battle, if with your other bounty hunter, may cancel the immunity to attrition of a Jedi here.",
                library.getSwccgoCardBlueprint("203_24^").getGameText());
    }

    @Test
    public void AlternateImageLukeInPlayShowsItsImageWordingAndIsLuke() {
        var scn = new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "200_20^");
                }},
                new HashMap<>(),
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
        var luke = scn.GetLSCard("luke");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), luke);
        assertTrue(luke.getBlueprint().getGameText().startsWith("Adds 3 to power of anything he pilots (and to Attack Run total"));
        assertTrue(Filters.Luke.accepts(scn.game(), luke));
        assertEquals(4, luke.getBlueprint().getAbility(), 0);
    }

    @Test
    public void LoreMatchesTheCurrentPcCardImage() {
        SwccgCardBlueprintLibrary library = new SwccgCardBlueprintLibrary();
        for (String[] row : LORE)
            assertEquals(row[0], row[1], library.getSwccgoCardBlueprint(row[0]).getLore());
    }

    /**
     * The Falcon, Junkyard Garbage (current images, regular and ALT): the combat vehicle side prints no Scomp link icon.
     */
    @Test
    public void FalconJunkyardGarbageBackPrintsNoScompLink() {
        SwccgCardBlueprintLibrary library = new SwccgCardBlueprintLibrary();
        assertFalse(library.getSwccgoCardBlueprint("204_35_BACK").hasIcon(Icon.SCOMP_LINK));
        assertTrue(library.getSwccgoCardBlueprint("204_35").hasIcon(Icon.SCOMP_LINK));
    }

    private static VirtualTableScenario ambushScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("ambush", "210_2");     // Ambush (V), current image
                    put("cantina", "1_128");    // Tatooine: Cantina (battleground)
                    put("eisley", "1_133");     // Tatooine: Mos Eisley (battleground)
                    put("farm", "1_132");       // Tatooine: Lars' Moisture Farm (battleground)
                    put("clone1", "210_9");
                    put("clone2", "210_9");
                    put("clone3", "210_9");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    /**
     * Ambush (V) (current image, 210_2): "Once per game, target three related battlegrounds occupied by your clones. Your
     * Force drains at same and related battlegrounds this turn are +1." The player now targets the three battlegrounds.
     */
    @Test
    public void AmbushTargetsThreeRelatedBattlegroundsOccupiedByClones() {
        var scn = ambushScenario();
        var ambush = scn.GetLSCard("ambush");
        var cantina = scn.GetLSCard("cantina");
        var eisley = scn.GetLSCard("eisley");
        var farm = scn.GetLSCard("farm");
        scn.StartGame();
        scn.MoveLocationToTable(cantina);
        scn.MoveLocationToTable(eisley);
        scn.MoveLocationToTable(farm);
        scn.MoveCardsToLocation(cantina, scn.GetLSCard("clone1"));
        scn.MoveCardsToLocation(eisley, scn.GetLSCard("clone2"));
        scn.MoveCardsToLocation(farm, scn.GetLSCard("clone3"));
        scn.MoveCardsToLSHand(ambush);
        assertTrue(Filters.battleground.accepts(scn.game(), cantina));
        assertTrue(Filters.battleground.accepts(scn.game(), eisley));
        assertTrue(Filters.battleground.accepts(scn.game(), farm));

        scn.SkipToLSTurn(Phase.CONTROL);
        float before = scn.game().getModifiersQuerying().getForceDrainAmount(scn.gameState(), cantina, scn.LS);
        assertTrue(scn.LSCardActionAvailable(ambush, "Add to Force drains"));
        scn.LSPlayCard(ambush, "Add to Force drains");
        assertTrue(scn.LSHasCardChoicesAvailable(cantina, eisley, farm));
        scn.LSChooseCards(cantina, eisley, farm);
        scn.PassAllResponses();
        assertEquals(before + 1, scn.game().getModifiersQuerying().getForceDrainAmount(scn.gameState(), cantina, scn.LS), scn.epsilon);
    }

    @Test
    public void AmbushNeedsThreeBattlegroundsOccupiedByClones() {
        var scn = ambushScenario();
        var ambush = scn.GetLSCard("ambush");
        var cantina = scn.GetLSCard("cantina");
        var eisley = scn.GetLSCard("eisley");
        var farm = scn.GetLSCard("farm");
        scn.StartGame();
        scn.MoveLocationToTable(cantina);
        scn.MoveLocationToTable(eisley);
        scn.MoveLocationToTable(farm);
        scn.MoveCardsToLocation(cantina, scn.GetLSCard("clone1"));
        scn.MoveCardsToLocation(eisley, scn.GetLSCard("clone2"));
        scn.MoveCardsToLSHand(ambush);

        scn.SkipToLSTurn(Phase.CONTROL);
        assertFalse(scn.LSCardActionAvailable(ambush, "Add to Force drains"));
    }

    /**
     * Steadfast (current image, 214_9): "While alone at an [Episode VII] system, adds Force icons to equalize them for both
     * sides here." Starkiller Base (an [Episode VII] system) gains Light Side icons; Tatooine (not [Episode VII]) does not.
     */
    @Test
    public void SteadfastEqualizesForceIconsOnlyAtAnEpisodeVIISystem() {
        var scn = new VirtualTableScenario(
                new HashMap<>(),
                new HashMap<>() {{
                    put("steadfast", "214_9");
                    put("starkiller", "208_51");
                    put("tatooine", "1_289");
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
        var steadfast = scn.GetDSCard("steadfast");
        var starkiller = scn.GetDSCard("starkiller");
        var tatooine = scn.GetDSCard("tatooine");
        scn.StartGame();
        scn.MoveLocationToTable(starkiller);
        scn.MoveLocationToTable(tatooine);
        assertEquals(0, scn.GetIconCount(starkiller, Icon.LIGHT_FORCE));
        assertEquals(1, scn.GetIconCount(tatooine, Icon.LIGHT_FORCE));

        scn.MoveCardsToLocation(tatooine, steadfast);
        assertEquals(1, scn.GetIconCount(tatooine, Icon.LIGHT_FORCE));

        scn.MoveCardsToLocation(starkiller, steadfast);
        assertEquals(2, scn.GetIconCount(starkiller, Icon.LIGHT_FORCE));
        assertEquals(2, scn.GetIconCount(starkiller, Icon.DARK_FORCE));
    }

    private static VirtualTableScenario meditationScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("meditation", "213_47"); // Meditation, current image
                    put("luke", "1_19");         // Luke Skywalker (ability 4)
                    put("site", "1_133");        // Tatooine: Mos Eisley
                }},
                new HashMap<>() {{
                    put("trooper1", "1_194");
                    put("trooper2", "1_194");
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    /**
     * Meditation (current image, 213_47): "During battle at same site, if opponent has more characters participating than
     * you, may add one battle destiny." Two opponent's characters battling one: the destiny may be added.
     */
    @Test
    public void MeditationAddsBattleDestinyWhenOpponentHasMoreCharactersParticipating() {
        var scn = meditationScenario();
        var meditation = scn.GetLSCard("meditation");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetLSCard("site");
        scn.StartGame();
        scn.MoveLocationToTable(site);
        scn.MoveCardsToLocation(site, luke);
        scn.AttachCardsTo(luke, meditation);
        scn.MoveCardsToLocation(site, scn.GetDSCard("trooper1"), scn.GetDSCard("trooper2"));

        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSStartBattleAndSkipToWeaponsSegment(site);
        assertTrue(scn.LSCardActionAvailable(meditation, "Add one battle destiny"));
    }

    /** One opponent's character participating against one of yours: no battle destiny is added. */
    @Test
    public void MeditationAddsNoBattleDestinyWhenParticipantsAreEven() {
        var scn = meditationScenario();
        var meditation = scn.GetLSCard("meditation");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetLSCard("site");
        scn.StartGame();
        scn.MoveLocationToTable(site);
        scn.MoveCardsToLocation(site, luke);
        scn.AttachCardsTo(luke, meditation);
        scn.MoveCardsToLocation(site, scn.GetDSCard("trooper1"));

        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSStartBattleAndSkipToWeaponsSegment(site);
        assertTrue(scn.IsParticipatingInBattle(luke, scn.GetDSCard("trooper1")));
        assertFalse(scn.LSCardActionAvailable(meditation, "Add one battle destiny"));
    }
}
