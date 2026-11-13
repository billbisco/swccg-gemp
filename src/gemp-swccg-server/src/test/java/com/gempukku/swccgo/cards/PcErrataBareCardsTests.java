package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.modifiers.ModifierType;
import org.junit.Test;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Bare GEMP cards play their Players Committee errata (swccg-guild/work, "Bare GEMP cards don't play their PC errata").
 * The bare id is the current text: for each card below it is the text of the wiki's "(PC Errata)" article (Players
 * Committee Advanced Rulebook 2023 Appendix A); the printed text stays playable as the card's o state
 * (docs/legacy-versions.md "Text states"). Gameplay tests cover every card whose errata changes how it plays.
 */
public class PcErrataBareCardsTests {

    /** bare id, game text (or null for a location), Light Side text, Dark Side text. */
    private static final String[][] BARE_TEXTS = {
        {"1_15", "When in a battle, if both players draw only one battle destiny and yours is higher, reduces opponent's total battle destiny to zero. Landspeed = 3. Adds 2 to power of anything she pilots. May not be aboard starfighters or enclosed vehicles.", null, null},
        {"1_59", "Deploy on a Death Star site where a stormtrooper was just lost. Target one of your characters not on Death Star. When target reaches this card, relocate to target. Target is now 'disguised:' gains spy skill, power and forfeit +2, and armor = 5.", null, null},
        {"1_165", "Adds 2 to power of anything he pilots. Power +1 at same site as Tarkin. If a battle was just initiated at a system where Bast is present with your non-droid character, may 'evacuate' (relocate) both to a related site.", null, null},
        {"1_212", "Deploy on a site under \"nighttime conditions.\" One at a time, target each non-droid character here and draw destiny. If destiny > ability, character 'sleeps' until end of your next turn (power, forfeit, and ability = 0, game text is canceled, and may not pilot, drive, or move). At the end of your next turn, lose Effect.", null, null},
        {"1_321", "Deploys for 1 Force to an unoccupied site. Deploys and moves like an undercover spy. When present with Luke of ability < 4 or pilot of ability < 3, choose one to be immediately lost (treat as an \"all cards\" situation). Seeker is also lost.", null, null},
        {"2_28", "Use 1 Force to deploy at any site. Moves like a character at normal use of the Force. Once during each of your control phases, may: Select one character present to be power or forfeit +1 for remainder of turn. OR Use 2 Force to cancel any seeker present.", null, null},
        {"2_42", "Deploy on Death Star: Trench. During your move phase, you may make an Attack Run as follows: Enter Trench: Move up to 3 of your starfighters into trench (for free). Dark Side may immediately follow with up to 3 TIEs (for free). Provide Cover: Identify your lead starfighter (Proton Torpedoes * required) and wingmen (if any). Turbolaser Batteries and TIEs with weapons may now target your starfighters (wingmen first, then lead if no wingmen remaining). Hit starfighters are immediately lost. It's Away!: Draw two destiny. Pull Up!: All starfighters now move to Death Star system (for free). If (total destiny + X + Y - Z) > 15, Death Star is \"blown away.\" X = ability of lead pilot (or 3 if Targeting Computer aboard) just before Pull Up! Y = total sites at largest Rebel Base (Yavin 4 or Hoth). Z = highest ability of opponent's TIE pilots in trench just before Pull Up! *Your Proton Torpedoes are immune to Overload.", null, null},
        {"1_43", "Use 1 Force to deploy on one of your Jedi. While present at a site, unless a Dark Jedi is also present, opponent's total ability at same location is reduced by 2.", null, null},
        {"1_54", "Deploy on your character with ability > 2 and a lightsaber. That character is power +3 in battles or may add 1 to Force drain where present. Effect is lost if character loses lightsaber.", null, null},
        {"1_63", "Deploy on your non-smuggler to give that character smuggler skill. OR Deploy on your smuggler. That character is power +1. (Immune to Alter.)", null, null},
        {"1_64", "Deploy on your non-warrior character (except droids) to give that character [Warrior] skill. OR Deploy on your warrior. That character is power +1. (Immune to Alter.)", null, null},
        {"1_96", "If a battle was just initiated at a site where your Jedi is present, use 1 Force to draw destiny; that number of opponent's characters with ability = 1 (your choice) must each move away (for free), or be lost.", null, null},
        {"1_163", "Power +1 if at same site as Motti or Yularen. Adds 1 to deploy cost of each opponent's droid (and your 'mouse' droids) when deploying to same location. Immune to attrition during \"nighttime conditions.\"", null, null},
        {"1_217", "Use 1 Force to deploy on one of your Dark Jedi. While present at a site, unless a Jedi is also present, opponent's total ability at same location is reduced by 2.", null, null},
        {"1_221", "Deploy on your non-warrior character (except droids) to give that character [Warrior] skill. OR Deploy on your warrior. That character is power +1. (Immune to Alter.)", null, null},
        {"2_31", "Deploy on a superlaser. May not fire at a planet unless 8 cards stacked here. If fewer than 8 cards stacked here, opponent may stack top card of Force Pile here. If just fired at a system, Effect is canceled. If Effect canceled, cards stacked here are placed in Used Pile.", null, null},
        {"2_32", "Use 1 Force to target a starfighter having one or more permanent pilots. Draw destiny. If destiny > 2, deploy on starfighter to remove all permanent pilots (otherwise, Effect is lost). May add one pilot for each permanent pilot removed. (Immune to Alter.)", null, null},
        {"2_39", "Once per game, use 2 Force to deploy on Dantooine system. Target Death Star. Opponent may not play Commence Primary Ignition until target reaches Dantooine, canceling Utinni Effect.", null, null},
        {"2_45", "If a battle was just initiated at an interior site, use 1 Force; for remainder of battle all characters of ability > 2 and all leaders (on both sides) are simultaneously excluded from that battle.", null, null},
        {"2_70", "May add 1 pilot and 1 astromech. Immune to attrition < 3 if Wedge piloting.", null, null},
        {"2_71", "May add 1 pilot and 1 astromech. Immune to attrition < 4 if Luke piloting. When firing in an Attack Run, adds 1 to total.", null, null},
        {"2_100", "While aboard your capital starship, adds 1 to power and 2 to hyperspeed, and that starship is immune to attrition < 4. While at Death Star: Central Core, hyperspeed of Death Star system = 2.", null, null},
        {"2_109", "* Forfeit value begins at 7. When 'forfeited,' droid remains in play (unless 'hit'), but forfeit value is reduced by the amount of attrition or battle damage absorbed. Droid lost when forfeit value reaches zero.", null, null},
        {"2_115", "Use 2 Force to deploy on your Star Destroyer. At the end of a battle at same system or sector, may target an opponent's starship present (except a Mon Calamari Star Cruiser) using 2 Force. Draw destiny. Target captured if destiny > defense value.", null, null},
        {"2_118", "Use 1 Force to target a starfighter having one or more permanent pilots. Draw destiny. If destiny > 2, deploy on starfighter to remove all permanent pilots (otherwise, Effect is lost). May add one pilot for each permanent pilot removed. (Immune to Alter.)", null, null},
        {"2_124", "Use 2 Force to deploy on an opponent's droid (except C-3PO or R2-D2), 1 on your droid. When either player draws a destiny matching the number of characters at same site, droid 'explodes' (all characters present with Effect are lost).", null, null},
        {"2_125", "Deploy on Kessel system (may not be moved). Target one captive and one trooper to escort captive. When targets reach Utinni Effect, retrieve Force equal to captive's forfeit (captive and Utinni Effect lost). If trooper lost en route, captive released.", null, null},
        {"2_140", "If a battle was just initiated at an interior site, use 1 Force; for remainder of battle all characters of ability > 2 and all leaders (on both sides) are simultaneously excluded from that battle.", null, null},
        {"2_151", "May add 1 pilot. May deploy with a pilot as a 'react' to a battle where you have a TIE (for free if TIE is Black 2 or 3).", null, null},
        {"3_1", "Once per turn, one of your non-droid characters just lost from same site may be placed in your Used Pile. Subtracts 2 from X on your Bacta Tank.", null, null},
        {"3_9", "Once per turn, one of your 'hit' non-droid characters at same or adjacent site may be placed in your Used Pile instead of Lost Pile when forfeited. Cumulatively subtracts 2 from X on your Bacta Tank.", null, null},
        {"3_32", "Use 4 Force to deploy on your side of table. A non-droid character you just lost may be placed here. Holds 1 'patient' at a time. During your deploy phase, may use X Force to bring 'patient' to hand, where X = deploy cost of 'patient.'", null, null},
        {"3_60", null, "Once per turn, when you deploy a medical droid, it is deploy -2.", "If you control, with an Imperial present, Force drain +1 here."},
        {"3_77", "Deploy on a site. May be moved by two warriors for 1 additional Force. Your warrior present may target up to two characters or creatures at same or adjacent site using 2 Force. Draw two destiny. Target(s) hit if total destiny > total defense value.", null, null},
        {"3_81", "Deploy at same exterior site as your mining droid. 'Explodes' if starfighter (use 5 as defense value) or non-creature vehicle deploys or moves to or across same site. Draw destiny. Target lost if destiny +2 > defense value. Vehicle Mine is also lost.", null, null},
        {"3_86", "Once per turn, one of your 'hit' non-droid characters at same or adjacent site may be placed in your Used Pile instead of Lost Pile when forfeited.", null, null},
        {"3_108", "Deploy on Chewie if Han was just lost or just became missing. Opponent cannot play Let The Wookiee Win or Wookiee Roar. Opponent must also lose 1 Force at end of every player's turn. If Han on table, Immediate Effect canceled.", null, null},
        {"3_119", "If opponent just moved a character, vehicle, or starship as a 'react' to a location, you may immediately move one vehicle or starship, if within range, to that location (as a regular move).", null, null},
        {"4_19", "Deploy on any apprentice at the beginning of your turn. Apprentice 'rests' (may not attempt Jedi Tests) until end of your next turn, then relocate Immediate Effect to Jedi Test. When attempting this test, that apprentice adds 3 to training destiny. (Immune to Control.)", null, null},
        {"4_33", "Deploy on Leia. Once during each of your control phases, you may use 3 Force to retrieve 1 Force. Also, when an apprentice, adds 1 to her training destiny.", null, null},
        {"4_35", "Deploy on an asteroid sector and specify starting direction. Every move phase, moves to next adjacent asteroid sector (reversing direction as necessary). Where present, adds 2 to asteroid destiny. May be targeted by starship weapons (defense value = 3).", null, null},
        {"4_57", "Target one starfighter participating in a battle at a system or sector. For remainder of turn, starfighter may not move, is immune to attrition and, if piloted by a Corellian, is power +2.", null, null},
        {"4_58", "Use 3 Force. Each player places hand onto Reserve Deck (if possible), then places Used Pile onto Reserve Deck; reshuffle. Each player then draws cards from Reserve Deck equal to the number of cards just placed from hand onto Reserve Deck (if any).", null, null},
        {"4_74", "At the start of the damage segment during a battle, before any cards have been forfeited, cause all attrition for both sides to be reduced to zero.", null, null},
        {"4_77", "Deploy on a Dagobah site. Target a mentor here. Also, target or deploy (regardless of location deployment restrictions) an apprentice here. Attempt when targets are present at the beginning of your control phase. Draw training destiny. If destiny + apprentice's ability > 12, test completed: Place on apprentice. All opponent's Force drain bonuses are canceled. Mentor: Your character of ability > 2. Apprentice: Your non-droid, non-Jedi character of lesser ability than mentor. Each time you complete any Jedi Test, you may exchange one card in hand for one Jedi Test in your Lost Pile.", null, null},
        {"4_92", "Adds 2 to power of anything he pilots. When piloting Hound's Tooth, draws one battle destiny if not able to otherwise. Adds 1 to attrition against opponent in battles at same site. While present, may reduce Chewie's forfeit to zero here.", null, null},
        {"4_130", "Deploy on an asteroid sector and specify starting direction. Every move phase, moves to next adjacent asteroid sector (reversing direction as necessary). Where present, adds 2 to asteroid destiny. May be targeted by starship weapons (defense value = 3).", null, null},
        {"4_151", "Use 3 Force. Each player places hand onto Reserve Deck (if possible), then places Used Pile onto Reserve Deck; reshuffle. Each player then draws cards from Reserve Deck equal to the number of cards just placed from hand onto Reserve Deck (if any).", null, null},
        {"4_161", null, "Force drain +1 here.", "If Vader or Emperor on table, your Force generation is +1 here (+2 if both)."},
        {"5_4", "When at a site and opponent is losing Force from Force drains at related cloud sectors, lost Force must come from Reserve Deck if possible.", null, null},
        {"5_18", "Deploy on Bespin: Cloud City. Your Tibanna Gas Miners deploy free to Cloud City sites and double the Force they activate.", null, null},
        {"5_24", "Deploy on one of the following systems: Rendezvous Point, Hoth, Yavin 4, Alderaan, or Sullust. Your starships deploy -2 and your pilots deploy -1 here. When battling here, you may add one battle destiny. Effect canceled if opponent controls this system. (Immune to Alter.)", null, null},
        {"5_35", "USED: If you just targeted with a blaster, add 3 to your total weapon destiny. LOST: Lose 1 Force to cause one opponent's just 'hit' character to be immediately placed in Lost Pile. OR Cancel Levitation Attack.", null, null},
        {"5_54", "Relocate one of your characters from a Cloud City site to Weather Vane (if 'hit,' character is first restored to normal). May be played even after a battle has just been initiated.", null, null},
        {"5_79", null, "If Weather Vane on table, characters about to be 'hit' here are relocated there.", "If Weather Vane on table, characters about to be 'hit' here are relocated there."},
        {"5_98", "Deploys only aboard Executor or to any Death Star site. Cannot move. May cancel opponent's Force drains at adjacent Death Star or Executor sites.", null, null},
        {"5_112", "If an opponent's character at the Carbonite Chamber is about to be 'hit' by a lightsaber, deploy on that character. Character is instead immediately captured and 'frozen.' If captive released, lose Immediate Effect. (Immune to Control.)", null, null},
        {"5_123", "Deploy on a prison. When one of your troopers 'delivers' (imprisons) a captive here, you may search your Lost Pile, take any one card into hand and then lose this Effect (each captive may be 'delivered' only once until they are released or leave table).", null, null},
        {"5_124", "If Luke was just 'frozen,' deploy on Emperor or Detention Block Corridor. Target Luke and Vader. When reached by targets, place Utinni Effect on Luke and opponent must lose Force (from Life Force) equal to half their Life Force (round down). If Luke released, lose Utinni Effect. (Immune to Alter.)", null, null},
        {"5_135", "If you just targeted with a lightsaber, add 3 to your total weapon destiny. OR Cancel Swing-And-A-Miss. OR Lose 1 Force to cause one opponent's just 'hit' character to be immediately placed in Lost Pile.", null, null},
        {"5_145", "USED: If an opponent's weapon is about to 'hit' an Imperial present with a captive being escorted, the captive is 'hit' instead. LOST: During the damage segment of a battle at a site, you may forfeit any captives present (once per battle per captive).", null, null},
        {"5_152", "If a battle you won just ended at an interior site and you have a character of ability > 3 present there, relocate one opponent's character present to an adjacent site. (If on Cloud City, may relocate that character to Weather Vane instead.)", null, null},
        {"5_167", null, "If Weather Vane on table, characters about to be 'hit' here are relocated there.", "If Weather Vane on table, characters about to be 'hit' here are relocated there."},
        {"6_96", "Deploys only to a Jabba's Palace site. Power = 0 at any location other than a Tatooine site. Power +2 at any Jabba's Palace site, while Jabba on table. Your transport vehicles just lost from same site may be placed in your Used Pile.", null, null},
        {"6_141", "Deploy on your side of table. We Have A Prisoner and Oo-ta Goo-ta, Solo? play for free and are immune to Sense. If opponent's character is about to be forfeited, your bounty hunter present may capture that character (character is first restored to normal). (Immune to Alter.)", null, null},
        {"7_4", "Deploys only on Tatooine. When in a duel, adds 2 to your total. Once per turn, if a battle just ended here, may 'revive' (place here from Lost Pile) your character forfeited from same site this turn. Immune to attrition < 5.", null, null},
        {"7_37", "While aboard your capital starship, adds 1 to power and 2 to hyperspeed, and that starship is immune to attrition < 4. While at Death Star: Central Core, Death Star requires +2 Force to move.", null, null},
        {"7_216", "Deploy on a cloud sector (limit one per sector). Force you activate may be drawn into hand (one per turn for each of your Floating Refineries on table). Each cloud sector or gas miner drawn in this way by this device may be revealed to retrieve 1 Force.", null, null},
        {"7_221", "Deploy on an exterior Tatooine site. Specify starting direction. During your control phase, moves to next adjacent exterior site (reversing direction as necessary). During battle here, if your Tusken Raider or bantha here, may add one battle destiny.", null, null},
        {"7_254", "If you have a Star Destroyer in a battle, during the weapons segment use its tractor beam for free. Add 2 to tractor beam destiny if targeting a unique (•) starship. If not captured, target is power -3 and maneuver -3 (if possible) for remainder of battle.", null, null},
        {"8_8", "Deploys -3 on Tydirium, Endor or Dagobah. May be targeted instead of Luke by Mind What You Have Learned (that card then targets Leia instead of Luke for remainder of game). While at any exterior site, adds one [Light side] icon. Immune to attrition < 4.", null, null},
        {"8_43", "Deploy on Bunker. Once during your control phase, if you control Bunker with a Rebel, you may attempt to 'blow away' Bunker as follows: Charges! Come On, Come On!: Draw two destiny. Add 3 to total for each Explosive Charge on Bunker. Move! Move! Move!: If total destiny > 12, your characters here may relocate to Back Door for free, Bunker and Landing Platform (if on table) are 'blown away,' opponent loses 8 Force and this card is lost.", null, null},
        {"8_50", "During a battle, if you have weapons at at least 2 sites adjacent to that battle, add 3 to your total power and add one battle destiny. OR For remainder of turn, your scout at an exterior site is power +1 and adds 1 to each of that character's weapon destiny draws.", null, null},
        {"9_12", "Deploys -2 to Home One. Adds 3 to power of any capital starship she pilots. When piloting a Star Cruiser with another Mon Calamari pilot aboard, unless opponent has total ability > 6 piloting here, reset opponent's total battle destiny here to 0.", null, null},
        {"9_46", "Deploy on table. Once per turn, may take one The Force Is Strong With This One or [Death Star II] Anakin Skywalker into hand from Reserve Deck, reshuffle. May use 2 Force to cancel You Are Beaten (unless canceling Uncontrollable Fury) or Dark Jedi Presence. (Immune to Alter.)", null, null},
        {"9_77", "Deploys and moves like a starfighter. May add 1 pilot and 6 passengers. Permanent pilot provides ability of 2. May use Covert Landing like a shuttle or starfighter (even while landed).", null, null},
        {"9_101", "Deploys -2 aboard Scimitar 2. Adds 2 to power of anything he pilots. When forfeited from a TIE/sa he is piloting, also satisfies all remaining attrition and battle damage against you.", null, null},
        {"9_143", null, "When your starship moves from here, draw movement destiny. Add maneuver (if any). If total destiny < 2, starship is lost.", "Deploys only if Coolant Shaft on table. While That Thing's Operational on table, you may add one battle destiny during battles at Death Star II system and system it orbits."},
        {"9_144", null, "When your starship moves from here, draw movement destiny. Add maneuver (if any). If total destiny < 1, starship is lost.", "Deploys only if Death Star II system on table. Your TIEs at Death Star II locations may not Tallon Roll. Your non-Objective cards with 'Occupation' in title are canceled."},
        {"9_165", "May deploy with a pilot as a 'react' to a battle where you have a TIE (for free if TIE is in Saber Squadron). May add 1 pilot. Immune to attrition < 4 when DS-181-4 piloting.", null, null},
        {"10_8", "USED: If opponent just initiated a battle at a system or sector where you have a starship with maneuver > 3, use 1 Force to add one battle destiny. LOST: During the damage segment of a battle you lost, if you have no cards left that can be forfeited, cancel all remaining battle damage. (Immune to Sense.)", null, null},
        {"10_21", "USED: Target your highest-ability character and one just-played Interrupt. Draw destiny. If destiny < ability of target character, cancel target Interrupt. LOST: Use 3 Force. Each player places hand onto Reserve Deck (if possible), then places Used Pile onto Reserve Deck; reshuffle. Each player then draws cards from Reserve Deck equal to the number of cards just placed from hand onto Reserve Deck (if any).", null, null},
        {"10_27", "USED: During opponent's turn, take up to four cards from your hand and place them on top of your Force Pile. LOST: At the start of the damage segment during a battle, before any cards have been forfeited, cause all attrition for both sides to be reduced to zero.", null, null},
        {"10_39", "USED: Cancel Nabrun Leids. LOST: During the damage segment of a battle you lost, if you have no cards left that can be forfeited, cancel all remaining battle damage. (Immune to Sense.) OR Cancel Hyper Escape, Closer?!, or one 'react.' OR During your move phase, cancel Landing Claw.", null, null},
        {"10_46", "USED: Target your highest-ability character and one just-played Interrupt. Draw destiny. If destiny < ability of target character, cancel target Interrupt. LOST: Use 3 Force. Each player places hand onto Reserve Deck (if possible), then places Used Pile onto Reserve Deck; reshuffle. Each player then draws cards from Reserve Deck equal to the number of cards just placed from hand onto Reserve Deck (if any).", null, null},
        {"13_11", "Deploy on table. Cancels Opee Sea Killer. While no card here, you may place a card from hand face-up here. If you just drew weapon or battle destiny (limit once per destiny draw), you may exchange it for card here, which then counts as that destiny draw. (Immune to Alter.)", null, null},
        {"13_32", null, "Any characters of ability < 5 about to be 'hit' here (and all cards on them) are placed in owner's Used Pile.", "Any characters of ability < 5 about to be 'hit' here (and all cards on them) are placed in owner's Used Pile."},
        {"13_57", null, "Your Jedi deploy +2 here.", "If you occupy, your Force generation is +1 here, and opponent's characters may not move from here."},
        {"13_60", "Deploy on table. Cancels Opee Sea Killer. While no card here, you may place a card from hand face-up here. If you just drew weapon or battle destiny (limit once per destiny draw), you may exchange it for card here, which then counts as that destiny draw. (Immune to Alter.)", null, null},
        {"13_74", "Deploys -2 to Naboo. While with Qui-Gon, your battle destinies here are each +1. When Maul wins a lightsaber combat, adds 2 to opponent's Force loss. Immune to Clash Of Sabers and attrition < 5 (or < 6 if armed with a lightsaber). May not be Disarmed.", null, null},
        {"13_75", "Deploy on Maul. Adds 1 to Maul's lightsaber combat total. May lose 1 Force to add 2 to Force drain where present. Twice per battle, may target a character. Draw two destiny. Target hit, and its forfeit = 0, if total > defense value. While on Maul, may not be stolen.", null, null},
        {"13_77", null, "Any characters of ability < 5 about to be 'hit' here (and all cards on them) are placed in owner's Used Pile.", "Any characters of ability < 5 about to be 'hit' here (and all cards on them) are placed in owner's Used Pile."},
        {"14_63", "Deploy on your Fambaa. Twice per battle may draw destiny. If destiny < total number of opponent's characters and vehicles present, one of them is lost (opponent's choice).", null, null},
        {"14_115", "Deploys -1 to Naboo or same system as your battleship. While at same system as your battleship, during your deploy phase, may deploy one Droid Starfighter Laser Cannons aboard from Reserve Deck; reshuffle.", null, null},
        {"110_12", "May add 1 pilot and 3 passengers. Permanent pilot is •Zuckuss, who provides ability of 4 and adds 2 to power. Unless opponent has ability > 6 piloting here, reset opponent's total battle destiny here to 0.", null, null},
        {"112_1", "Deploy Hutt Trade Route and a Jabba's Palace site. May deploy Yarna d'al' Gargan. Reveal one unique (•) alien with a defined species from your deck. This card is your Rep. For remainder of game, your Rep is a leader. Yarna d'al' Gargan is immune to Alter. You may not deploy 'insert' cards or operatives. While a rancor is at Rancor Pit, Trap Door is immune to Bo Shuda. Flip this card if you occupy two battleground sites (must occupy a third with a non-unique alien of your Rep's species if a non-Tatooine location is on table).", null, null},
        {"112_15", "Deploy Desert Heart and a Jabba's Palace site. May deploy Well Guarded. Reveal one unique (•) alien with a defined species from your deck. This card is your Rep. For remainder of game, your Rep is a leader. Well Guarded is immune to Alter. You may not deploy 'insert' cards or operatives. While frozen Han on table, Rebels are immune to No Bargain and Bad Feeling Have I. Flip this card if you occupy two battleground sites (must occupy a third with a non-unique alien of your Rep's species if a non-Tatooine location is on table).", null, null},
    };

    /** printed state id, bare id, printedOn, supersededOn. */
    private static final Object[][] PRINTED_STATES = {
        {"1_43o", "1_43", LocalDate.of(1995, 12, 1), LocalDate.of(2023, 7, 30)},
        {"1_54o", "1_54", LocalDate.of(1995, 12, 1), LocalDate.of(2023, 7, 30)},
        {"1_63o", "1_63", LocalDate.of(1995, 12, 1), LocalDate.of(2023, 7, 30)},
        {"1_64o", "1_64", LocalDate.of(1995, 12, 1), LocalDate.of(2023, 7, 30)},
        {"1_96o", "1_96", LocalDate.of(1995, 12, 1), LocalDate.of(2023, 7, 30)},
        {"1_163o", "1_163", LocalDate.of(1995, 12, 1), LocalDate.of(2023, 7, 30)},
        {"1_217o", "1_217", LocalDate.of(1995, 12, 1), LocalDate.of(2023, 7, 30)},
        {"1_221o", "1_221", LocalDate.of(1995, 12, 1), LocalDate.of(2023, 7, 30)},
        {"2_124o", "2_124", LocalDate.of(1997, 7, 1), LocalDate.of(2023, 7, 30)},
        {"2_151o", "2_151", LocalDate.of(1997, 7, 1), LocalDate.of(2023, 7, 30)},
        {"3_1o", "3_1", LocalDate.of(1996, 11, 1), LocalDate.of(2023, 7, 30)},
        {"3_9o", "3_9", LocalDate.of(1996, 11, 1), LocalDate.of(2023, 7, 30)},
        {"3_32o", "3_32", LocalDate.of(1996, 11, 1), LocalDate.of(2023, 7, 30)},
        {"3_60o", "3_60", LocalDate.of(1996, 11, 1), LocalDate.of(2023, 7, 30)},
        {"3_86o", "3_86", LocalDate.of(1996, 11, 1), LocalDate.of(2023, 7, 30)},
        {"4_19o", "4_19", LocalDate.of(1997, 4, 1), LocalDate.of(2023, 7, 30)},
        {"4_33o", "4_33", LocalDate.of(1997, 4, 1), LocalDate.of(2023, 7, 30)},
        {"4_35o", "4_35", LocalDate.of(1997, 4, 1), LocalDate.of(2023, 7, 30)},
        {"4_58o", "4_58", LocalDate.of(1997, 4, 1), LocalDate.of(2023, 7, 30)},
        {"4_74o", "4_74", LocalDate.of(1997, 4, 1), LocalDate.of(2023, 7, 30)},
        {"4_130o", "4_130", LocalDate.of(1997, 4, 1), LocalDate.of(2023, 7, 30)},
        {"4_151o", "4_151", LocalDate.of(1997, 4, 1), LocalDate.of(2023, 7, 30)},
        {"4_161o", "4_161", LocalDate.of(1997, 4, 1), LocalDate.of(2023, 7, 30)},
        {"5_18o", "5_18", LocalDate.of(1997, 11, 1), LocalDate.of(2023, 7, 30)},
        {"5_24o", "5_24", LocalDate.of(1997, 11, 1), LocalDate.of(2023, 7, 30)},
        {"5_35o", "5_35", LocalDate.of(1997, 11, 1), LocalDate.of(2023, 7, 30)},
        {"5_54o", "5_54", LocalDate.of(1997, 11, 1), LocalDate.of(2023, 7, 30)},
        {"5_79o", "5_79", LocalDate.of(1997, 11, 1), LocalDate.of(2023, 7, 30)},
        {"5_112o", "5_112", LocalDate.of(1997, 11, 1), LocalDate.of(2023, 7, 30)},
        {"5_135o", "5_135", LocalDate.of(1997, 11, 1), LocalDate.of(2023, 7, 30)},
        {"5_167o", "5_167", LocalDate.of(1997, 11, 1), LocalDate.of(2023, 7, 30)},
        {"10_8o", "10_8", LocalDate.of(2001, 1, 10), LocalDate.of(2017, 6, 1)},
        {"10_21o", "10_21", LocalDate.of(2001, 1, 10), LocalDate.of(2017, 6, 1)},
        {"10_27o", "10_27", LocalDate.of(2001, 1, 10), LocalDate.of(2017, 6, 1)},
        {"10_39o", "10_39", LocalDate.of(2001, 1, 10), LocalDate.of(2017, 6, 1)},
        {"10_46o", "10_46", LocalDate.of(2001, 1, 10), LocalDate.of(2017, 6, 1)},
        {"13_11o", "13_11", LocalDate.of(2001, 10, 31), LocalDate.of(2023, 7, 30)},
        {"13_32o", "13_32", LocalDate.of(2001, 10, 31), LocalDate.of(2023, 7, 30)},
        {"13_57o", "13_57", LocalDate.of(2001, 10, 31), LocalDate.of(2017, 6, 1)},
        {"13_60o", "13_60", LocalDate.of(2001, 10, 31), LocalDate.of(2023, 7, 30)},
        {"13_74o", "13_74", LocalDate.of(2001, 10, 31), LocalDate.of(2017, 6, 1)},
        {"13_75o", "13_75", LocalDate.of(2001, 10, 31), LocalDate.of(2017, 6, 1)},
        {"13_77o", "13_77", LocalDate.of(2001, 10, 31), LocalDate.of(2017, 6, 1)},
        {"14_63o", "14_63", LocalDate.of(2001, 12, 1), LocalDate.of(2023, 7, 30)},
        {"14_115o", "14_115", LocalDate.of(2001, 12, 1), LocalDate.of(2023, 7, 30)},
        {"110_12o", "110_12", LocalDate.of(2000, 1, 1), LocalDate.of(2023, 7, 30)},
    };

    private static SwccgCardBlueprint blueprint(String id) {
        SwccgCardBlueprint blueprint = VirtualTableScenario._cardLibrary.getSwccgoCardBlueprint(id);
        assertTrue("no card " + id, blueprint != null);
        return blueprint;
    }

    @Test
    public void BareCardsHaveTheirPcErrataText() {
        for (String[] row : BARE_TEXTS) {
            SwccgCardBlueprint bare = blueprint(row[0]);
            assertNull(row[0], bare.getTextState());
            if (row[1] != null) {
                assertEquals(row[0], row[1], bare.getGameText());
            }
            else {
                if (row[2] != null) assertEquals(row[0] + " Light Side", row[2], bare.getLocationLightSideGameText());
                if (row[3] != null) assertEquals(row[0] + " Dark Side", row[3], bare.getLocationDarkSideGameText());
            }
        }
    }

    @Test
    public void PrintedStatesKeepThePrintedTextAndAreTheSameCard() {
        for (Object[] row : PRINTED_STATES) {
            String stateId = (String) row[0];
            SwccgCardBlueprint printed = blueprint(stateId);
            SwccgCardBlueprint bare = blueprint((String) row[1]);
            assertEquals(stateId, bare.getTitle(), printed.getTitle());
            assertEquals(stateId, bare.getUniqueness(), printed.getUniqueness());
            assertEquals(stateId, bare.getSide(), printed.getSide());
            assertEquals(stateId, bare.getCardTypes(), printed.getCardTypes());
            assertEquals(stateId, bare.getLore(), printed.getLore());
            assertTrue(stateId, bare.getClass().isInstance(printed));
            boolean textDiffers = !String.valueOf(bare.getGameText()).equals(String.valueOf(printed.getGameText()))
                    || !String.valueOf(bare.getLocationLightSideGameText()).equals(String.valueOf(printed.getLocationLightSideGameText()))
                    || !String.valueOf(bare.getLocationDarkSideGameText()).equals(String.valueOf(printed.getLocationDarkSideGameText()));
            assertTrue(stateId + " text should differ from the errata", textDiffers);
            TextState state = printed.getTextState();
            assertEquals(stateId, row[1], state.currentId());
            assertEquals(stateId, "Printed", state.label());
            assertEquals(stateId, row[2], state.printedOn());
            assertEquals(stateId, row[3], state.supersededOn());
            assertEquals(stateId, bare.getTitle() + " [Printed]", GameUtils.getFullName(printed));
        }
    }

    // ---- gameplay ----

    private static VirtualTableScenario scenario(Map<String, String> ls, Map<String, String> ds) {
        HashMap<String, String> lsCards = new HashMap<>(ls);
        HashMap<String, String> dsCards = new HashMap<>(ds);
        if (lsCards.isEmpty()) lsCards.put("filler", "1_28");
        if (dsCards.isEmpty()) dsCards.put("filler", "1_194");
        return new VirtualTableScenario(lsCards, dsCards, 10, 10,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, "anything_goes");
    }

    private static Object callHook(PhysicalCard card, String name, Class<?>[] types, Object... args) throws Exception {
        Class<?> type = card.getBlueprint().getClass();
        while (type != null) {
            try {
                Method method = type.getDeclaredMethod(name, types);
                method.setAccessible(true);
                return method.invoke(card.getBlueprint(), args);
            } catch (NoSuchMethodException e) {
                type = type.getSuperclass();
            }
        }
        throw new NoSuchMethodException(name);
    }

    private static float opponentAbility(VirtualTableScenario scn, String opponent, PhysicalCard location) {
        return scn.game().getModifiersQuerying().getTotalAbilityAtLocation(scn.gameState(), opponent, location);
    }

    /** Affect Mind (1_43): "While present at a site" (PC errata) - no effect while the Jedi pilots a starship at a system. */
    private static float affectMindAtSystem(String affectMindId) {
        var scn = scenario(Map.of("luke", "9_24", "xwing", "1_146", "effect", affectMindId),
                Map.of("kessel", "1_288", "tie", "1_304", "pilot", "1_173"));
        scn.StartGame();
        var kessel = scn.GetDSCard("kessel");
        scn.MoveLocationToTable(kessel);
        scn.MoveCardsToLocation(kessel, scn.GetLSCard("xwing"), scn.GetDSCard("tie"));
        scn.BoardAsPilot(scn.GetLSCard("xwing"), scn.GetLSCard("luke"));
        scn.BoardAsPilot(scn.GetDSCard("tie"), scn.GetDSCard("pilot"));
        scn.AttachCardsTo(scn.GetLSCard("luke"), scn.GetLSCard("effect"));
        return opponentAbility(scn, scn.DS, kessel);
    }

    private static float affectMindAtSite(String affectMindId) {
        var scn = scenario(Map.of("luke", "9_24", "effect", affectMindId), Map.of("trooper", "1_173"));
        scn.StartGame();
        var site = scn.GetLSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetLSCard("luke"), scn.GetDSCard("trooper"));
        scn.AttachCardsTo(scn.GetLSCard("luke"), scn.GetLSCard("effect"));
        return opponentAbility(scn, scn.DS, site);
    }

    @Test
    public void AffectMindWorksWhilePresentAtASiteButNotAtASystem() {
        // At a site both texts reduce DS-61-2's ability (2) to 0.
        assertEquals(0, affectMindAtSite("1_43"), 0.01f);
        assertEquals(0, affectMindAtSite("1_43o"), 0.01f);
        // Piloting a starship at a system the Jedi is not present at a site: no reduction (DS-61-2 in a TIE: 3).
        assertEquals(3, affectMindAtSystem("1_43"), 0.01f);
    }

    /** I Find Your Lack Of Faith Disturbing (1_217): "While present at a site" (PC errata). */
    private static float lackOfFaithAtSystem(String effectId) {
        var scn = scenario(Map.of("xwing", "1_146", "redleader", "1_29"),
                Map.of("kessel", "1_288", "tie", "1_303", "vader", "1_168", "effect", effectId));
        scn.StartGame();
        var kessel = scn.GetDSCard("kessel");
        scn.MoveLocationToTable(kessel);
        scn.MoveCardsToLocation(kessel, scn.GetLSCard("xwing"), scn.GetDSCard("tie"));
        scn.BoardAsPilot(scn.GetLSCard("xwing"), scn.GetLSCard("redleader"));
        scn.BoardAsPilot(scn.GetDSCard("tie"), scn.GetDSCard("vader"));
        scn.AttachCardsTo(scn.GetDSCard("vader"), scn.GetDSCard("effect"));
        return opponentAbility(scn, scn.LS, kessel);
    }

    @Test
    public void LackOfFaithDoesNotWorkAtASystem() {
        // Vader piloting a starship at a system is not present at a site: Red Leader keeps his ability (2) + the X-wing's.
        assertEquals(lackOfFaithAtSystem("1_217o"), lackOfFaithAtSystem("1_217"), 0.01f);
        assertTrue(lackOfFaithAtSystem("1_217") >= 2);
    }

    /** "Deploy on your ..." (PC errata) versus the printed "Deploy on any ...": can the Effect deploy on an opponent's character? */
    private static boolean mayDeployOnOpponentsCharacter(String effectId, boolean effectIsLight, String opponentsCharacterId) {
        Map<String, String> mine = Map.of("effect", effectId);
        Map<String, String> theirs = Map.of("character", opponentsCharacterId);
        var scn = scenario(effectIsLight ? mine : theirs, effectIsLight ? theirs : mine);
        scn.StartGame();
        var effect = effectIsLight ? scn.GetLSCard("effect") : scn.GetDSCard("effect");
        var character = effectIsLight ? scn.GetDSCard("character") : scn.GetLSCard("character");
        scn.MoveCardsToLocation(effectIsLight ? scn.GetDSStartingLocation() : scn.GetLSStartingLocation(), character);
        if (effectIsLight) {
            scn.MoveCardsToLSHand(effect);
            scn.EnsureLSForcePile(6);
            scn.SkipToLSTurn(Phase.DEPLOY);
            return scn.LSDeployAvailable(effect);
        }
        scn.MoveCardsToDSHand(effect);
        scn.EnsureDSForcePile(6);
        scn.SkipToDSTurn(Phase.DEPLOY);
        return scn.DSDeployAvailable(effect);
    }

    @Test
    public void KetMalissDeploysOnlyOnYourCharacters() {
        assertFalse(mayDeployOnOpponentsCharacter("1_221", false, "1_19"));
        assertTrue(mayDeployOnOpponentsCharacter("1_221o", false, "1_19"));
    }

    @Test
    public void SaitorrKalFasDeploysOnlyOnYourCharacters() {
        assertFalse(mayDeployOnOpponentsCharacter("1_64", true, "1_173"));
        assertTrue(mayDeployOnOpponentsCharacter("1_64o", true, "1_173"));
    }

    @Test
    public void RycarRyjerdDeploysOnlyOnYourCharacters() {
        assertFalse(mayDeployOnOpponentsCharacter("1_63", true, "1_173"));
        assertTrue(mayDeployOnOpponentsCharacter("1_63o", true, "1_173"));
    }

    @Test
    public void LightsaberProficiencyDeploysOnlyOnYourCharacter() throws Exception {
        for (String id : new String[] {"1_54", "1_54o"}) {
            var scn = scenario(Map.of("effect", id), Map.of("vader", "1_168", "saber", "1_324"));
            scn.StartGame();
            var vader = scn.GetDSCard("vader");
            scn.MoveCardsToLocation(scn.GetDSStartingLocation(), vader);
            scn.AttachCardsTo(vader, scn.GetDSCard("saber"));
            var effect = scn.GetLSCard("effect");
            Filter filter = (Filter) callHook(effect, "getGameTextValidDeployTargetFilter",
                    new Class<?>[] {com.gempukku.swccgo.game.SwccgGame.class, PhysicalCard.class, com.gempukku.swccgo.common.PlayCardOptionId.class, boolean.class},
                    scn.game(), effect, null, false);
            assertEquals(id, id.endsWith("o"), filter.accepts(scn.game(), vader));
        }
    }

    @Test
    public void LordMaulMayNotBeDisarmed() {
        for (String id : new String[] {"13_74", "13_74o"}) {
            var scn = scenario(Map.of(), Map.of("maul", id));
            scn.StartGame();
            var maul = scn.GetDSCard("maul");
            scn.MoveCardsToLocation(scn.GetDSStartingLocation(), maul);
            boolean mayNotBeDisarmed = !scn.game().getModifiersQuerying().getModifiersAffectingCard(scn.gameState(), ModifierType.MAY_NOT_BE_DISARMED, maul).isEmpty();
            assertEquals(id, !id.endsWith("o"), mayNotBeDisarmed);
            assertEquals(id, id.endsWith("o"), scn.game().getModifiersQuerying().isImmuneToCardTitle(scn.gameState(), maul, "Disarmed"));
        }
    }

    @Test
    public void MaulsDoubleBladedLightsaberMayNotBeStolenOnlyWhileOnMaul() {
        for (String id : new String[] {"13_75", "13_75o"}) {
            var scn = scenario(Map.of(), Map.of("maul", "13_74", "saber", id, "vader", "1_168"));
            scn.StartGame();
            var saber = scn.GetDSCard("saber");
            scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("maul"), scn.GetDSCard("vader"));
            scn.AttachCardsTo(scn.GetDSCard("maul"), saber);
            assertFalse(id, scn.game().getModifiersQuerying().getModifiersAffectingCard(scn.gameState(), ModifierType.MAY_NOT_BE_STOLEN, saber).isEmpty());
            if (!id.endsWith("o")) {
                // PC errata: "While on Maul, may not be stolen."
                scn.AttachCardsTo(scn.GetDSCard("vader"), saber);
                assertTrue(id, scn.game().getModifiersQuerying().getModifiersAffectingCard(scn.gameState(), ModifierType.MAY_NOT_BE_STOLEN, saber).isEmpty());
            }
        }
    }

    /** Saber 4 / Black 4: 'react' to "a battle where you have a TIE" (PC errata); printed: "a battle initiated against a TIE". */
    private static void assertReactsWhenYourTieIsAttacking(String reactingId) throws Exception {
        for (String id : new String[] {reactingId, reactingId + "o"}) {
            var scn = scenario(Map.of("xwing", "1_146", "redleader", "1_29"),
                    Map.of("kessel", "1_288", "tie", "1_304", "pilot", "1_173", "react", id));
            scn.StartGame();
            var kessel = scn.GetDSCard("kessel");
            scn.MoveLocationToTable(kessel);
            var tie = scn.GetDSCard("tie");
            scn.MoveCardsToLocation(kessel, scn.GetLSCard("xwing"), tie);
            scn.BoardAsPilot(scn.GetLSCard("xwing"), scn.GetLSCard("redleader"));
            scn.BoardAsPilot(tie, scn.GetDSCard("pilot"));
            scn.MoveCardsToDSHand(scn.GetDSCard("react"));
            scn.SkipToDSTurn(Phase.BATTLE);
            scn.DSInitiateBattle(kessel);
            assertTrue(scn.IsBattleStarted() || scn.gameState().getBattleState() != null);
            Filter filter = (Filter) callHook(scn.GetDSCard("react"), "reactToBattleTIEFilter", new Class<?>[0]);
            assertEquals(id, !id.endsWith("o"), filter.accepts(scn.game(), tie));
        }
    }

    @Test
    public void Saber4MayReactToABattleWhereYouHaveATie() throws Exception {
        assertReactsWhenYourTieIsAttacking("9_165");
    }

    @Test
    public void Black4MayReactToABattleWhereYouHaveATie() throws Exception {
        assertReactsWhenYourTieIsAttacking("2_151");
    }

    @Test
    public void TwilightIsUponMeTakesTheDeathStarIIAnakinSkywalker() throws Exception {
        for (String id : new String[] {"9_46", "9_46o"}) {
            var scn = scenario(Map.of("twilight", id, "ds2anakin", "9_48", "otheranakin", "213_49"), Map.of());
            scn.StartGame();
            Filter filter = (Filter) callHook(scn.GetLSCard("twilight"), "anakinSkywalkerFilter", new Class<?>[0]);
            // Both "Anakin Skywalker" Interrupts in GEMP carry the [Death Star II] icon (9_48, Virtual Set 13's 213_49).
            assertTrue(id, filter.accepts(scn.game(), scn.GetLSCard("ds2anakin")));
            assertTrue(id, filter.accepts(scn.game(), scn.GetLSCard("otheranakin")));
            assertFalse(id, filter.accepts(scn.game(), scn.GetLSCard("twilight")));
        }
    }

    /** R3-T2 / R3-T6: "While aboard your capital starship" (PC errata); printed: "a capital starship". */
    private static void assertOnlyYourCapitalStarship(String droidId, boolean light, boolean printedStateExists) {
        String[] ids = printedStateExists ? new String[] {droidId, droidId + "o"} : new String[] {droidId};
        for (String id : ids) {
            // the droid boards an opponent's capital starship (Imperial-Class Star Destroyer / Corellian Corvette) at Kessel
            var scn = light ? scenario(Map.of("droid", id), Map.of("kessel", "1_288", "ship", "1_302"))
                    : scenario(Map.of("kessel", "1_126", "ship", "1_140"), Map.of("droid", id));
            scn.StartGame();
            var kessel = light ? scn.GetDSCard("kessel") : scn.GetLSCard("kessel");
            scn.MoveLocationToTable(kessel);
            var destroyer = light ? scn.GetDSCard("ship") : scn.GetLSCard("ship");
            scn.MoveCardsToLocation(kessel, destroyer);
            int before = scn.GetPower(destroyer);
            scn.BoardAsPassenger(destroyer, light ? scn.GetLSCard("droid") : scn.GetDSCard("droid"));
            assertEquals(id, before + (id.endsWith("o") ? 1 : 0), scn.GetPower(destroyer));
        }
    }

    @Test
    public void R3T2AddsPowerOnlyAboardYourCapitalStarship() {
        assertOnlyYourCapitalStarship("7_37", true, true);
    }

    @Test
    public void R3T6AddsPowerOnlyAboardYourCapitalStarship() {
        assertOnlyYourCapitalStarship("2_100", false, false);
    }

    @Test
    public void SeptoidDroidIsLostWhenForfeitedWhileHit() {
        for (String id : new String[] {"2_109", "2_109o"}) {
            var scn = scenario(Map.of(), Map.of("droid", id));
            scn.StartGame();
            var droid = scn.GetDSCard("droid");
            scn.MoveCardsToLocation(scn.GetDSStartingLocation(), droid);
            assertTrue(id, scn.game().getModifiersQuerying().isRemainsInPlayAndReducesForfeitWhenForfeited(scn.gameState(), droid));
            droid.setHit(true);
            assertEquals(id, id.endsWith("o"), scn.game().getModifiersQuerying().isRemainsInPlayAndReducesForfeitWhenForfeited(scn.gameState(), droid));
        }
    }

    /** The errata hooks that switch the printed timing or limit off in the o state. */
    private static boolean hook(String id, boolean light, String name) throws Exception {
        var scn = light ? scenario(Map.of("card", id), Map.of()) : scenario(Map.of(), Map.of("card", id));
        scn.StartGame();
        return (Boolean) callHook(light ? scn.GetLSCard("card") : scn.GetDSCard("card"), name, new Class<?>[0]);
    }

    @Test
    public void ColoClawFishIsLimitedToOncePerDestinyDraw() throws Exception {
        assertTrue(hook("13_11", true, "limitedToOncePerDestinyDraw"));
        assertFalse(hook("13_11o", true, "limitedToOncePerDestinyDraw"));
        assertTrue(hook("13_60", false, "limitedToOncePerDestinyDraw"));
        assertFalse(hook("13_60o", false, "limitedToOncePerDestinyDraw"));
    }

    @Test
    public void IntoTheVentilationShaftLeftyRestoresAHitCharacter() throws Exception {
        assertTrue(hook("5_54", true, "restoresHitCharacterToNormal"));
        assertFalse(hook("5_54o", true, "restoresHitCharacterToNormal"));
    }

    @Test
    public void HumanShieldPlaysWhenTheImperialIsAboutToBeHit() throws Exception {
        assertTrue(hook("5_145", false, "playsWhenAboutToBeHit"));
        assertFalse(hook("5_145o", false, "playsWhenAboutToBeHit"));
    }
}
