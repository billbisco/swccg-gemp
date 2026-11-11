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
 * Legacy batch R8-P (Bill 2026-11-11: every publication is a version, even a word-for-word repeat; play changes
 * approved 08:12; a PC card image is placed at its server upload date, 08:45 / 09:14). In each family the PC Virtual
 * Cards Master List (2013-04-27) repeats an earlier text after the PC website card list (2012) had changed it, and the
 * PC card images are dated by their folder upload dates (2013-02-25 for Virtual Blocks 1-7, 2013-05-07 for Virtual
 * Block 8). The latest publication is the current card: the Master List text, or (Sonic Bombardment, Assassin's
 * Blaster Rifle, Anakin Solo, Well Guarded, Rennek) the Virtual Block 8 card image. Behaviour tests for each play change
 * are in the cards' own test classes; the old current-card tests run on the card-list versions (Card_22xx_1nnn_Tests).
 */
public class LegacyBatchR8PVersionsTests {
    /** Current game text of each card after R8-P. */
    private static final Map<String, String> CURRENT_TEXT = Map.ofEntries(
            Map.entry("1210_103", "While at opponent's Galactic Senate, Leia's game text may not be canceled and may lose 1 Force to place Leia and any character present out of play. During battle, may cancel game text of a leader of ability < 5 with Leia for remainder of turn."),
            Map.entry("1210_127", "If Obi-Wan is out of play (and not on table), use 1 Force to deploy on table. During battle, may use 1 Force to add 5 to your total power there for remainder of turn. Once per turn, if Luke a captive, may activate 1 Force. (Immune to Control.)"),
            Map.entry("1210_159", "USED: If you just hit a target with a non-lightsaber non-[Permanent Weapon] weapon, place target (and all cards on it) in owner's Used Pile. LOST: Cancel Heroic (and any immunity to attrition) on a character or starship this turn."),
            Map.entry("1220_10", "Adds 2 to power of anything he pilots. Deploys free aboard Punishing One, and while piloting it, draws one battle destiny if unable to otherwise. At same site, Han's game text is canceled. May not be attacked. Immune to Fallen Portal."),
            Map.entry("1220_6", "Adds 3 to power of anything he pilots. Twice per game during opponent's turn, may place three cards from hand on your Force Pile to draw top two cards from Reserve Deck."),
            Map.entry("1220_93", "Deploys free to Cloud City. You generate +1 Force for every Cloud City site you occupy. Your aliens are deploy -1 here. Opponent's aliens are deploy +1 here."),
            Map.entry("1230_47", "Find one of your missing characters. OR During battle, activate 1 Force for each of your scouts participating. OR During battle, if your Imperial with opponent's alien, your total battle destiny is +3."),
            Map.entry("1240_37", "Unless your [Reflections III] objective on table, deploy on table. Vader's Obsession may be played to initiate an epic duel as follows: If Mara is present with Luke, each player draws two destiny (if armed with a lightsaber, each destiny is +1). Add character's ability. If Karrde on table, your total is -1. Higher total wins. If Luke loses, place him out of play and you may retrieve 5 Force. If Mara loses, cross her to the Light Side (she is now Mara Skywalker). After the duel (unless it was canceled or tied) Mara is ability = 6, adds one battle destiny, and once per battle involving her, may cancel one just drawn destiny."),
            Map.entry("1250_1", "At each system a player controls with a capital starship, non-[Presence] characters (and vehicles) deploy -1 to related sites and sectors. If you occupy a system with a capital starship (or three unique starships) and have no Objective on table, may add 1 to each of your Force drains at related battleground sites (opponent loses no more than 2 Force from those drains)."),
            Map.entry("1250_18", "Deploy on Sidious. While at Senate, if battle was just initiated anywhere, opponent must relocate this card to their participating character; during battle here, your destiny draws may not be canceled. If character about to be lost, relocate to Sidious; may ▲ a card."),
            Map.entry("1250_2", "Once during opponent's activate phase, may relocate Aurra to same site as a Jedi. Once per game, during your move phase, may fire her weapon at a Jedi; hit target is lost. Immune to attrition < 4."),
            Map.entry("1261_101", "Use 1 Force to ▼ (or retrieve) non-[Reflections III] Han. OR If a battle just initiated, and Han defending alone at a site, his forfeit cannot be reduced, he adds one destiny to power (+1 for each character and vehicle present), and draws one battle destiny if not able to otherwise. (Immune to Sense.)"),
            Map.entry("1261_106", "May add Harc as a pilot. During battle, adds one destiny to total power. If a Dark Jedi or Sith was just deployed to a related site, may ▼ a related location. Immune to attrition < 4 (< 6 at Bespin)."),
            Map.entry("1261_34", "Deploy on table. During battle involving your starship (or vehicle), it is power +2 for each of your characters at its related sites (they may be forfeited to reduce battle damage and attrition). Once per turn, may ▼ a non-unique starship (or vehicle) site (if such sites leave table, place them and all your cards there in Used Pile). (Immune to Alter.)"),
            Map.entry("1261_35", "Deploy on table with 5 cards from outside your deck face down under here. Twice per game, may play a Defensive Shield from here (as if from hand). If This Deal Is Getting Worse All The Time on table, for remainder of game lightsabers may not add to Force drains. Cloud City Occupation is canceled."),
            Map.entry("1261_80", "Deploy on table with 5 cards from outside your deck face down under here. Twice per game, may play a Defensive Shield from here (as if from hand). If Quiet Mining Colony or Independent Operation on table, lightsabers may not add to Force drains. Cloud City Celebration is canceled."),
            Map.entry("1261_81", "Deploy on a Rebel Base war room. Echo Base Operations is canceled. Your ability-1 permanent pilots aboard T-47s, X-wings, Y-wings, and Z-95s (except squadrons) are replaced by ability-2 permanent pilots who add 1 to power. During your control phase, may retrieve one Y-wing or Z-95. Suspended if opponent controls this site. (Immune to Alter.)"),
            Map.entry("1261_82", "Deploy on table. Your vehicles with a pilot character or gunner aboard are not hit when 'thrown.' During your deploy phase, may reveal an unpiloted combat vehicle from hand to ▲ its matching pilot character (or vice versa) and deploy both simultaneously. Whenever you play Desperate Tactics, Lucky Shot, On Target, Rapid Fire, or Steady Aim, may draw top card from Reserve Deck. May not be suspended. (Immune to Alter.)"),
            Map.entry("1261_97", "USED: Reveal the top 3 cards of Reserve Deck; place one in hand, and the others on bottom of Used Pile (in any order). STARTING: If your starting location was a non-[Special Edition] location, ▼ an [Immune to Alter] Effect and any number of <> sites with < 3 total [Light Side Force]. Place this Interrupt in hand."),
            Map.entry("1273_34", "Choose up to X of the following in any order, where X = number of cards stacked on Communing: Peek at the top card of any deck or pile; may then shuffle it. OR Draw 2 cards from Reserve Deck. OR Retrieve 1 Force. OR Activate X Force. OR Your next destiny draw this turn is +X."),
            Map.entry("1274_7", "Deploy on a battleground. If you occupy, during battle here or at a related location, may place this Effect in Used Pile and choose: Draw up to two cards from Reserve Deck OR Prevent opponent from drawing any destinies during the power segment of that battle (except battle destiny)."),
            Map.entry("1281_15", "Deploy on table. Your non-unique aliens are immune to Grimtaash (while you have < 13 cards in hand), The Camp, and deployment costs on Watch Your Step. During battle with two non-unique aliens of your Rep's species, your aliens there may not have their forfeit reduced. (Immune to Alter.)"),
            Map.entry("1281_17", "Once per game, ▲ a prison. OR Use 1 Force to ▲ a non-droid assassin (or slaver) of ability < 5. OR During battle involving an assassin, opponent's battle destiny draws are -1. OR Cancel opponent's attempt to modify the power of a starfighter piloted by your alien."),
            Map.entry("1282_12", "Power +2 while armed with a lightsaber. May use any 'stolen' lightsaber. Once during battle, where present, may steal an opponent's just-lost lightsaber. Immune to attrition < 3 (< 5 while armed with a 'stolen' lightsaber)."),
            Map.entry("1282_19", "May be stacked on I Know That Laugh from hand, using 2 Force. While stacked on I Know That Laugh, stack face-up and during any battle may draw destiny. If destiny < number of your aliens in battle, reset a participating character's forfeit to 0."));
    /** new version id -> {current printing id, label, printed on, superseded on} */
    private static final Map<String, String[]> VERSIONS = Map.ofEntries(
            Map.entry("2210_1103", new String[]{"1210_103", "PC-E1", "2012-07-21", "2013-02-25"}),
            Map.entry("2210_1127", new String[]{"1210_127", "PC-E1", "2012-07-21", "2013-02-25"}),
            Map.entry("2210_1159", new String[]{"1210_159", "PC-E1", "2012-07-21", "2013-02-25"}),
            Map.entry("2210_2103", new String[]{"1210_103", "PC-E2", "2013-02-25", "2013-04-27"}),
            Map.entry("2210_2127", new String[]{"1210_127", "PC-E2", "2013-02-25", "2013-04-27"}),
            Map.entry("2210_2159", new String[]{"1210_159", "PC-E2", "2013-02-25", "2013-04-27"}),
            Map.entry("2220_1006", new String[]{"1220_6", "PC-E1", "2012-07-21", "2013-02-25"}),
            Map.entry("2220_1010", new String[]{"1220_10", "PC-E1", "2012-07-21", "2013-02-25"}),
            Map.entry("2220_2006", new String[]{"1220_6", "PC-E2", "2013-02-25", "2013-04-27"}),
            Map.entry("2220_2010", new String[]{"1220_10", "PC-E2", "2013-02-25", "2013-04-27"}),
            Map.entry("2220_1093", new String[]{"1220_93", "PC-E1", "2012-07-21", "2013-02-25"}),
            Map.entry("2220_2093", new String[]{"1220_93", "PC-E2", "2013-02-25", "2013-04-27"}),
            Map.entry("2230_1047", new String[]{"1230_47", "PC-E1", "2012-07-21", "2013-02-25"}),
            Map.entry("2230_2047", new String[]{"1230_47", "PC-E2", "2013-02-25", "2013-04-27"}),
            Map.entry("2240_1037", new String[]{"1240_37", "PC-E1", "2012-07-21", "2013-02-25"}),
            Map.entry("2240_2037", new String[]{"1240_37", "PC-E2", "2013-02-25", "2013-04-27"}),
            Map.entry("2250_1001", new String[]{"1250_1", "PC-E1", "2012-07-21", "2013-02-25"}),
            Map.entry("2250_2001", new String[]{"1250_1", "PC-E2", "2013-02-25", "2013-04-27"}),
            Map.entry("2250_1002", new String[]{"1250_2", "PC-E1", "2012-07-21", "2013-02-25"}),
            Map.entry("2250_1002ai", new String[]{"1250_2ai", "PC-E1", "2012-07-21", "2013-02-25"}),
            Map.entry("2250_1018", new String[]{"1250_18", "PC-E1", "2012-07-21", "2013-02-25"}),
            Map.entry("2250_2002", new String[]{"1250_2", "PC-E2", "2013-02-25", "2013-04-27"}),
            Map.entry("2250_2002ai", new String[]{"1250_2ai", "PC-E2", "2013-02-25", "2013-04-27"}),
            Map.entry("2250_2018", new String[]{"1250_18", "PC-E2", "2013-02-25", "2013-04-27"}),
            Map.entry("2261_1035", new String[]{"1261_35", "PC-E1", "2010-05-29", "2013-02-25"}),
            Map.entry("2261_2034", new String[]{"1261_34", "PC-E2", "2012-07-21", "2013-02-25"}),
            Map.entry("2261_2035", new String[]{"1261_35", "PC-E2", "2013-02-25", "2013-04-27"}),
            Map.entry("2261_3034", new String[]{"1261_34", "PC-E3", "2013-02-25", "2013-04-27"}),
            Map.entry("2261_1080", new String[]{"1261_80", "PC-E1", "2010-05-29", "2013-02-25"}),
            Map.entry("2261_1081", new String[]{"1261_81", "PC-E1", "2012-07-21", "2013-02-25"}),
            Map.entry("2261_1082", new String[]{"1261_82", "PC-E1", "2012-07-21", "2013-02-25"}),
            Map.entry("2261_1097", new String[]{"1261_97", "PC-E1", "2012-07-21", "2013-02-25"}),
            Map.entry("2261_1101", new String[]{"1261_101", "PC-E1", "2012-07-21", "2013-02-25"}),
            Map.entry("2261_1106", new String[]{"1261_106", "PC-E1", "2012-07-21", "2013-02-25"}),
            Map.entry("2261_2080", new String[]{"1261_80", "PC-E2", "2013-02-25", "2013-04-27"}),
            Map.entry("2261_2081", new String[]{"1261_81", "PC-E2", "2013-02-25", "2013-04-27"}),
            Map.entry("2261_2082", new String[]{"1261_82", "PC-E2", "2013-02-25", "2013-04-27"}),
            Map.entry("2261_2097", new String[]{"1261_97", "PC-E2", "2013-02-25", "2013-04-27"}),
            Map.entry("2261_2101", new String[]{"1261_101", "PC-E2", "2013-02-25", "2013-04-27"}),
            Map.entry("2261_2106", new String[]{"1261_106", "PC-E2", "2013-02-25", "2013-04-27"}),
            Map.entry("2273_1034", new String[]{"1273_34", "PC-E1", "2012-07-21", "2013-02-25"}),
            Map.entry("2273_2034", new String[]{"1273_34", "PC-E2", "2013-02-25", "2013-04-27"}),
            Map.entry("2274_1007", new String[]{"1274_7", "PC-E1", "2012-07-21", "2013-02-25"}),
            Map.entry("2274_2007", new String[]{"1274_7", "PC-E2", "2013-02-25", "2013-04-27"}),
            Map.entry("2281_1015", new String[]{"1281_15", "PC-E1", "2012-07-21", "2013-04-27"}),
            Map.entry("2281_1017", new String[]{"1281_17", "PC-E1", "2012-07-21", "2013-04-27"}),
            Map.entry("2281_2015", new String[]{"1281_15", "PC-E2", "2013-04-27", "2013-05-07"}),
            Map.entry("2281_2017", new String[]{"1281_17", "PC-E2", "2013-04-27", "2013-05-07"}),
            Map.entry("2282_1011", new String[]{"1282_11", "PC-E1", "2012-12-12", "2013-04-27"}),
            Map.entry("2282_2011", new String[]{"1282_11", "PC-E2", "2013-04-27", "2013-05-07"}),
            Map.entry("2282_1012", new String[]{"1282_12", "PC-E1", "2012-12-12", "2013-04-27"}),
            Map.entry("2282_1019", new String[]{"1282_19", "PC-E1", "2012-12-12", "2013-04-27"}),
            Map.entry("2282_2012", new String[]{"1282_12", "PC-E2", "2013-04-27", "2013-05-07"}),
            Map.entry("2282_2019", new String[]{"1282_19", "PC-E2", "2013-04-27", "2013-05-07"}));

    private static SwccgCardBlueprintLibrary library;

    @BeforeClass
    public static void setUpClass() {
        library = new SwccgCardBlueprintLibrary();
    }

    @Test
    public void CurrentCardsCarryTheLatestPublication() {
        CURRENT_TEXT.forEach((id, text) -> {
            SwccgCardBlueprint card = library.getSwccgoCardBlueprint(id);
            assertNotNull(id, card);
            assertNull(id, card.getLegacyVersion());
            assertEquals(id, text, card.getGameText());
        });
        assertEquals(CURRENT_TEXT.get("1250_2"), library.getSwccgoCardBlueprint("1250_2ai").getGameText());
    }

    @Test
    public void NewVersionsAreTheirOwnCardsInDateOrder() {
        assertEquals(54, VERSIONS.size());
        VERSIONS.forEach((id, v) -> {
            SwccgCardBlueprint card = library.getSwccgoCardBlueprint(id);
            assertNotNull(id, card);
            LegacyVersion version = card.getLegacyVersion();
            assertNotNull(id, version);
            assertEquals(id, v[0], version.currentPrintingId());
            assertEquals(id, v[1], version.label());
            assertEquals(id, LocalDate.parse(v[2]), version.printedOn());
            assertEquals(id, LocalDate.parse(v[3]), version.supersededOn());
            SwccgCardBlueprint current = library.getSwccgoCardBlueprint(v[0]);
            assertEquals(id, current.getTitle(), card.getTitle());
            assertEquals(id, current.getUniqueness(), card.getUniqueness());
        });
    }
}
