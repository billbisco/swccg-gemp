package com.gempukku.swccgo.cards.set1.dark;

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
 * Dr. Evazan text states (docs/legacy-versions.md, "Text states"): 1_172o as printed (Premiere, 1995) and the bare card
 * 1_172 unchanged. Texts from wiki.swccg.com [[Dr. Evazan]] and [[Dr. Evazan (Errata)]].
 */
public class Card_1_172_TextStates_Tests {
    private static final String PRINTED_TEXT = "The not-so-good doctor may immediately 'operate' on any hit or Disarmed character at same site. 'Patient' is immediately lost. Adds 2 to power of any starship he pilots.";
    private static final String CURRENT_TEXT = "Adds 2 to power of anything he pilots. The not-so-good doctor may 'operate' on any other character present that was just 'hit' or just Disarmed; 'patient' is lost.";

    private static VirtualTableScenario GetScenario(String pilotId) {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("filler", "1_28");
                }},
                new HashMap<>() {{
                    put("pilot", pilotId);
                    put("ship", "1_304");
                    put("vehicle", "3_157");
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
    public void DrEvazanPrintedHasThePrintedTextAndTextState() {
        var scn = GetScenario("1_172o");
        var printed = scn.GetDSCard("pilot").getBlueprint();
        var bare = GetScenario("1_172").GetDSCard("pilot").getBlueprint();
        assertEquals(PRINTED_TEXT, printed.getGameText());
        TextState state = printed.getTextState();
        assertEquals("1_172", state.currentId());
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
        assertEquals("Dr. Evazan [Printed]", GameUtils.getFullName(printed));
    }

    @Test
    public void DrEvazanBareCardIsUnchanged() {
        var scn = GetScenario("1_172");
        var bare = scn.GetDSCard("pilot").getBlueprint();
        assertEquals(CURRENT_TEXT, bare.getGameText());
        assertNull(bare.getTextState());
        assertEquals("Dr. Evazan", GameUtils.getFullName(bare));
    }

    /** Power the pilot adds to the starship (at a system) or the combat vehicle (at a site) he pilots. */
    private static int addedPower(String pilotId, boolean vehicle) {
        var scn = GetScenario(pilotId);
        var craft = scn.GetDSCard(vehicle ? "vehicle" : "ship");
        scn.StartGame();
        scn.MoveCardsToLocation(vehicle ? scn.GetDSStartingLocation() : scn.GetLSStartingLocation(), craft);
        scn.BoardAsPilot(craft, scn.GetDSCard("pilot"));
        return scn.GetPower(craft) - Math.round(craft.getBlueprint().getPower());
    }

    @Test
    public void DrEvazanPrintedAddsPowerOnlyToAStarshipHePilots() {
        assertEquals(addedPower("1_172", false), addedPower("1_172o", false)); // starship: as the current card
        int bareVehicle = addedPower("1_172", true);
        assertEquals(bareVehicle - 2, addedPower("1_172o", true)); // vehicle: no bonus as printed
    }
}
