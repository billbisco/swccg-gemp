package com.gempukku.swccgo.cards.set3.dark;

import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.GameUtils;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

/**
 * Printed text states (docs/legacy-versions.md, "Text states") of Hoth and Dagobah pilots whose printed text says
 * "any starship he pilots"; Decipher Glossary Version 2.0 (November 1998) changed it to "anything he pilots", which
 * the bare cards play. Printed texts from the plain wiki.swccg.com articles (Decipher archive scans).
 * Each state: exact printed text, same card as the bare printing, and the pilot bonus goes to a starship he pilots
 * but not to a combat vehicle he pilots (the bare card adds it to both).
 */
public class Card_3_4_StarshipPilots_TextStates_Tests {
    /** id, title, printed text, light side, power added to a piloted starship. */
    private static final Object[][] CARDS = {
        {"3_11o", "Jeroen Webb", "Adds 2 to power of any starship he pilots. When in battle with a Rebel leader, subtracts 1 from opponent's total battle destiny.", true, 2},
        {"3_18o", "Romas 'Lock' Navander", "Adds 1 to power and maneuver of any starship he pilots. Opponent may not 'react' to or from same location.", true, 1},
        {"3_20o", "Tamizander Rey", "Adds 2 to power of any starship he pilots. Your starships may move from same exterior site for free. While at Echo Docking Bay, once every turn, may allow one character on Hoth to be immune to The Shield Doors Must Be Closed.", true, 2},
        {"3_84o", "Captain Lennox", "Adds 2 to power of any starship he pilots (3 if starship is Tyrant). When on a Star Destroyer, may use its Tractor Beam once each turn during your control phase.", false, 2},
        {"3_85o", "Captain Piett", "Power +1 when at same site as Vader. Adds 2 to power of any starship he pilots (3 if starship is Executor). May use 1 Force to search your Reserve Deck and take one Probe Droid into hand. Shuffle, cut and replace.", false, 2},
        {"3_89o", "Lieutenant Cabbel", "Adds 2 to power of any starship he pilots. On Tyrant, also adds 1 to armor. When in battle with an Imperial leader, subtracts 1 from opponent's total battle destiny.", false, 2},
        {"4_100o", "Dengar", "Adds 2 to power of any starship he pilots. Power +1 for every opponent's character present. May reduce Han's forfeit to zero while present.", false, 2},
        {"4_101o", "IG-88", "Adds 2 to power of any starship he pilots. May 'assassinate' any character at same site hit by IG-88 (victim is immediately lost). May use two different weapons. May initiate battle and be battled. Immune to Restraining Bolt and purchase.", false, 2},
        {"4_106o", "Warrant Officer M'Kae", "Adds 2 to power of any starship he pilots. May use 1 Force to search your Reserve Deck and take any one tractor beam, Our First Catch Of The Day or Besieged into hand. Shuffle, cut and replace.", false, 2},
        {"4_107o", "Zuckuss", "Adds 2 to power of any starship he pilots. May move for free as a 'react.' Once during each battle, may use 1 Force to cause one alien of ability < 3 at same site to be forfeit = 0 for remainder of turn. Immune to attrition < 3.", false, 2},
        {"3_5o", "Derek 'Hobbie' Klivian", "Power +2 when at same site as Biggs. Adds 2 to power of any starship he pilots (3 if a Star Destroyer is present). On Rogue 4, also adds 2 to maneuver.", true, 2},
        {"3_23o", "Toryn Farr", "Adds 2 to power of any starship she pilots. When at any war room, adds 1 to weapon destiny draws of your Planet Defender Ion Cannon on same planet.", true, 2},
        {"3_27o", "Zev Senesca", "Adds 2 to power of any starship he pilots. On Rogue 2, also adds 3 to maneuver and may draw one battle destiny if not able to otherwise.", true, 2},
        {"3_82o", "Admiral Ozzel", "Your capital starships are deploy -1 to same system. Adds 2 to power of any starship he pilots. Immediately lost if Vader is on table and opponent 'reacts' to same location as Ozzel.", false, 2},
        {"4_102o", "Imperial Helmsman", "Adds 2 to power of any starship he pilots. On a Star Destroyer, draws one battle destiny if not able to otherwise.", false, 2},
    };

    private static VirtualTableScenario scenario(String pilotId, boolean light) {
        var mine = new HashMap<String, String>() {{
            put("pilot", pilotId);
            put("vehicle", light ? "3_69" : "3_157");  // Snowspeeder / Blizzard Walker (combat vehicles)
            put("starship", light ? "1_146" : "1_305"); // X-wing / TIE Scout (no permanent pilot)
        }};
        var other = new HashMap<String, String>() {{
            put("other", light ? "1_194" : "1_28");
        }};
        return new VirtualTableScenario(light ? mine : other, light ? other : mine, 10, 10,
                light ? StartingSetup.DefaultLSGroundLocation : StartingSetup.DefaultLSSpaceSystem,
                light ? StartingSetup.DSStartingLocation("1_288") : StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, "anything_goes");
    }

    private static PhysicalCardImpl card(VirtualTableScenario scn, boolean light, String name) {
        return light ? scn.GetLSCard(name) : scn.GetDSCard(name);
    }

    /** Power of the given ship (vehicle or starship) piloted by the card, and of the ship unpiloted. */
    private static int pilotedPower(String pilotId, boolean light, String ship) {
        var scn = scenario(pilotId, light);
        scn.StartGame();
        boolean vehicle = ship.equals("vehicle");
        var where = vehicle == light ? scn.GetLSStartingLocation() : scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(where, card(scn, light, ship));
        scn.BoardAsPilot(card(scn, light, ship), card(scn, light, "pilot"));
        return scn.GetPower(card(scn, light, ship));
    }

    @Test
    public void PrintedTextStatesHaveThePrintedTextAndAreTheSameCard() {
        for (Object[] c : CARDS) {
            String id = (String) c[0];
            boolean light = (Boolean) c[3];
            String bareId = id.substring(0, id.length() - 1);
            var printed = card(scenario(id, light), light, "pilot").getBlueprint();
            var bare = card(scenario(bareId, light), light, "pilot").getBlueprint();
            assertEquals(id, c[2], printed.getGameText());
            assertNotEquals(id, printed.getGameText(), bare.getGameText());
            TextState state = printed.getTextState();
            assertEquals(bareId, state.currentId());
            assertEquals("Printed", state.label());
            assertEquals(bare.getTitle() + " [Printed]", GameUtils.getFullName(printed));
            assertEquals(bare.getTitle(), printed.getTitle());
            assertEquals(bare.getUniqueness(), printed.getUniqueness());
            assertEquals(bare.getPersonas(), printed.getPersonas());
            assertEquals(bare.getLore(), printed.getLore());
            assertEquals(bare.getSide(), printed.getSide());
            assertEquals(bare.getDestiny(), printed.getDestiny(), 0.01f);
            assertEquals(bare.getRarity(), printed.getRarity());
            assertNull(bare.getTextState());
        }
    }

    @Test
    public void PrintedPilotBonusGoesToAStarshipButNotToAVehicle() {
        for (Object[] c : CARDS) {
            String id = (String) c[0];
            boolean light = (Boolean) c[3];
            int bonus = (Integer) c[4];
            String bareId = id.substring(0, id.length() - 1);
            assertEquals(id + " starship", pilotedPower(bareId, light, "starship"), pilotedPower(id, light, "starship"));
            assertEquals(id + " vehicle", pilotedPower(bareId, light, "vehicle") - bonus, pilotedPower(id, light, "vehicle"));
        }
    }
}
