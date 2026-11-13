package com.gempukku.swccgo.cards;

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
}
