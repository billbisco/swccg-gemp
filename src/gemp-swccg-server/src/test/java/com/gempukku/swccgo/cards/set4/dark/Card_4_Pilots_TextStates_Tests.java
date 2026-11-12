package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.GameUtils;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Printed text states (docs/legacy-versions.md, "Text states") of three Dagobah Imperial pilots whose printed text
 * says "any starship he pilots"; Decipher Glossary Version 2.0 (November 1998) changed it to "anything he pilots".
 * Printed texts from the plain wiki.swccg.com articles [[Comm Chief]], [[Commander Brandei]], [[Commander Nemet]]
 * (Decipher archive scans); errata from their (Errata) articles, which equal the bare cards' text, so no d state.
 */
public class Card_4_Pilots_TextStates_Tests {
    private static final String COMM_CHIEF_PRINTED = "Adds 2 to power of any starship he pilots. When piloting a starship or combat vehicle, that starship or vehicle moves for free.";
    private static final String COMM_CHIEF_BARE = "Adds 2 to power of anything he pilots, and that starship or vehicle moves for free.";
    private static final String BRANDEI_PRINTED = "Adds 2 to power of any starship he pilots. If aboard a Star Destroyer or at any docking bay, once during each of your control phases, may use 1 Force to search your Reserve Deck and take one TIE into hand. Shuffle, cut and replace.";
    private static final String BRANDEI_BARE = "Adds 2 to power of anything he pilots. Once during each of your control phases, when aboard a Star Destroyer or at any docking bay, may use 1 Force to take one TIE into hand from Reserve Deck; reshuffle.";
    private static final String NEMET_PRINTED = "Adds 1 to power and maneuver of any starship he pilots. Opponent may not 'react' to or from same location.";
    private static final String NEMET_BARE = "Adds 1 to power and maneuver of anything he pilots. Opponent may not 'react' to or from same location.";

    private static VirtualTableScenario scenario(String pilotId) {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("rebel", "1_28");
                }},
                new HashMap<>() {{
                    put("pilot", pilotId);
                    put("walker", "3_157"); // Blizzard Walker, combat vehicle, power 6
                    put("scout", "1_305");  // TIE Scout, starfighter, power 2, maneuver 5
                }},
                10, 10,
                StartingSetup.DefaultLSSpaceSystem,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                "anything_goes"
        );
    }

    private static void assertState(String id, String bareId, String title, String text) {
        var card = scenario(id).GetDSCard("pilot").getBlueprint();
        var bare = scenario(bareId).GetDSCard("pilot").getBlueprint();
        assertEquals(text, card.getGameText());
        TextState state = card.getTextState();
        assertEquals(bareId, state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(1997, 4, 1), state.printedOn());
        assertEquals(title + " [Printed]", GameUtils.getFullName(card));
        assertEquals(bare.getTitle(), card.getTitle());
        assertEquals(bare.getUniqueness(), card.getUniqueness());
        assertEquals(bare.getPersonas(), card.getPersonas());
        assertEquals(bare.getLore(), card.getLore());
        assertEquals(bare.getDestiny(), card.getDestiny(), 0.01f);
        assertEquals(bare.getExpansionSet(), card.getExpansionSet());
        assertEquals(bare.getRarity(), card.getRarity());
        assertNull(bare.getTextState());
    }

    /** Power (and maneuver) of the walker and the TIE Scout, each piloted by a copy of the card. */
    private static int[] pilotedStats(String pilotId) {
        var scn = scenario(pilotId);
        var walkerScn = scenario(pilotId);
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetDSCard("scout"));
        scn.BoardAsPilot(scn.GetDSCard("scout"), scn.GetDSCard("pilot"));
        walkerScn.StartGame();
        walkerScn.MoveCardsToLocation(walkerScn.GetDSStartingLocation(), walkerScn.GetDSCard("walker"));
        walkerScn.BoardAsPilot(walkerScn.GetDSCard("walker"), walkerScn.GetDSCard("pilot"));
        return new int[]{scn.GetPower(scn.GetDSCard("scout")), scn.GetManeuver(scn.GetDSCard("scout")),
                walkerScn.GetPower(walkerScn.GetDSCard("walker"))};
    }

    @Test
    public void CommChiefPrintedHasThePrintedText() {
        assertState("4_94o", "4_94", "Comm Chief", COMM_CHIEF_PRINTED);
        assertEquals(COMM_CHIEF_BARE, scenario("4_94").GetDSCard("pilot").getBlueprint().getGameText());
    }

    @Test
    public void CommChiefPrintedAddsPowerToAStarshipButNotToAVehicle() {
        int[] printed = pilotedStats("4_94o");
        int[] bare = pilotedStats("4_94");
        assertEquals(bare[0], printed[0]);      // starship: +2 in both
        assertEquals(bare[2] - 2, printed[2]);  // vehicle: bare adds 2, printed adds nothing
    }

    @Test
    public void CommanderBrandeiPrintedHasThePrintedText() {
        assertState("4_95o", "4_95", "Commander Brandei", BRANDEI_PRINTED);
        assertEquals(BRANDEI_BARE, scenario("4_95").GetDSCard("pilot").getBlueprint().getGameText());
    }

    @Test
    public void CommanderBrandeiPrintedAddsPowerToAStarshipButNotToAVehicle() {
        int[] printed = pilotedStats("4_95o");
        int[] bare = pilotedStats("4_95");
        assertEquals(bare[0], printed[0]);
        assertEquals(bare[2] - 2, printed[2]);
    }

    @Test
    public void CommanderNemetPrintedHasThePrintedText() {
        assertState("4_97o", "4_97", "Commander Nemet", NEMET_PRINTED);
        assertEquals(NEMET_BARE, scenario("4_97").GetDSCard("pilot").getBlueprint().getGameText());
    }

    @Test
    public void CommanderNemetPrintedAddsToAStarshipButNotToAVehicle() {
        int[] printed = pilotedStats("4_97o");
        int[] bare = pilotedStats("4_97");
        assertEquals(bare[0], printed[0]);      // starship power +1 in both
        assertEquals(bare[1], printed[1]);      // starship maneuver +1 in both
        assertEquals(bare[2] - 1, printed[2]);  // vehicle: bare adds 1, printed adds nothing
    }
}
