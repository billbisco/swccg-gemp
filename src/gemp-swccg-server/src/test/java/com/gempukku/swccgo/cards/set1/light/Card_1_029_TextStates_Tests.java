package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.GameUtils;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Red Leader text states (docs/legacy-versions.md, "Text states"): 1_29o as printed (Premiere, 1995) and the bare card
 * 1_29 unchanged. Texts from wiki.swccg.com [[Red Leader]] and [[Red Leader (Errata)]].
 */
public class Card_1_029_TextStates_Tests {
    private static final String PRINTED_TEXT = "Adds 2 to power of any starship he pilots. On Red 1, also adds 1 to maneuver and draws one battle destiny if not able to otherwise. Forfeit +1 to all other Red Squadron pilots at same location.";
    private static final String CURRENT_TEXT = "Adds 2 to power of anything he pilots. When piloting Red 1, also adds 1 to maneuver and draws one battle destiny if not able to otherwise. Adds 1 to forfeit of each other Red Squadron pilot at same location.";

    private static VirtualTableScenario GetScenario(String pilotId) {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("pilot", pilotId);
                    put("ship", "1_146");
                    put("vehicle", "3_69");
                }},
                new HashMap<>() {{
                    put("filler", "1_194");
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

    @Test
    public void RedLeaderPrintedHasThePrintedTextAndTextState() {
        var scn = GetScenario("1_29o");
        var printed = scn.GetLSCard("pilot").getBlueprint();
        var bare = GetScenario("1_29").GetLSCard("pilot").getBlueprint();
        assertEquals(PRINTED_TEXT, printed.getGameText());
        TextState state = printed.getTextState();
        assertEquals("1_29", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(1995, 12, 1), state.printedOn());
        assertEquals(bare.getTitle(), printed.getTitle());
        assertEquals(bare.getUniqueness(), printed.getUniqueness());
        assertEquals(bare.getPersonas(), printed.getPersonas());
        assertEquals(bare.getSide(), printed.getSide());
        assertEquals(bare.getRarity(), printed.getRarity());
        assertEquals(bare.getExpansionSet(), printed.getExpansionSet());
        assertEquals(bare.getLore(), printed.getLore());
        assertEquals(bare.getDestiny(), printed.getDestiny(), scn.epsilon);
        assertEquals(bare.getPower(), printed.getPower(), scn.epsilon);
        assertEquals(bare.getAbility(), printed.getAbility(), scn.epsilon);
        assertEquals(bare.getDeployCost(), printed.getDeployCost(), scn.epsilon);
        assertEquals(bare.getForfeit(), printed.getForfeit(), scn.epsilon);
        assertEquals("Red Leader [Printed]", GameUtils.getFullName(printed));
    }

    @Test
    public void RedLeaderDecipherErrataHasTheGlossaryTextAndIsALeader() {
        var scn = GetScenario("1_29d");
        var errata = scn.GetLSCard("pilot").getBlueprint();
        var bare = GetScenario("1_29").GetLSCard("pilot").getBlueprint();
        assertEquals("Adds 2 to power of anything he pilots. When piloting Red 1, also adds 1 to maneuver and draws one battle destiny if not able to otherwise. Adds 1 to forfeit of each other Red Squadron pilot at same location. This character is a leader. (See characteristics.)", errata.getGameText());
        TextState state = errata.getTextState();
        assertEquals("1_29", state.currentId());
        assertEquals("D-E1", state.label());
        assertEquals(LocalDate.of(1998, 11, 1), state.printedOn());
        assertEquals("Red Leader [D-E1]", GameUtils.getFullName(errata));
        assertEquals(bare.getPersonas(), errata.getPersonas());
        assertTrue(errata.hasKeyword(com.gempukku.swccgo.common.Keyword.LEADER));
        // Plays as the current card: adds 2 to the power of a vehicle as well as a starship
        assertEquals(addedPower("1_29", true), addedPower("1_29d", true));
        assertEquals(addedPower("1_29", false), addedPower("1_29d", false));
    }

    @Test
    public void RedLeaderBareCardIsUnchanged() {
        var scn = GetScenario("1_29");
        var bare = scn.GetLSCard("pilot").getBlueprint();
        assertEquals(CURRENT_TEXT, bare.getGameText());
        assertNull(bare.getTextState());
        assertEquals("Red Leader", GameUtils.getFullName(bare));
    }

    /** Power the pilot adds to the starship (at a system) or the combat vehicle (at a site) he pilots. */
    private static int addedPower(String pilotId, boolean vehicle) {
        var scn = GetScenario(pilotId);
        var craft = scn.GetLSCard(vehicle ? "vehicle" : "ship");
        scn.StartGame();
        scn.MoveCardsToLocation(vehicle ? scn.GetDSStartingLocation() : scn.GetLSStartingLocation(), craft);
        scn.BoardAsPilot(craft, scn.GetLSCard("pilot"));
        return scn.GetPower(craft) - Math.round(craft.getBlueprint().getPower());
    }

    @Test
    public void RedLeaderPrintedAddsPowerOnlyToAStarshipHePilots() {
        assertEquals(addedPower("1_29", false), addedPower("1_29o", false)); // starship: as the current card
        int bareVehicle = addedPower("1_29", true);
        assertEquals(bareVehicle - 2, addedPower("1_29o", true)); // vehicle: no bonus as printed
    }
}
