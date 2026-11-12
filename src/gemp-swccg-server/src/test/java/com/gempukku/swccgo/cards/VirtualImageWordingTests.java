package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.Phase;
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
}
