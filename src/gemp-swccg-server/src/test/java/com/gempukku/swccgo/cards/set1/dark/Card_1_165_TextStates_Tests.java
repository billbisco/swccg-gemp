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
 * Chief Bast text states (docs/legacy-versions.md, "Text states"): 1_165o as printed (Premiere, 1995) and the bare card
 * 1_165 unchanged. Texts from wiki.swccg.com [[Chief Bast]] and [[Chief Bast (PC Errata)]].
 */
public class Card_1_165_TextStates_Tests {
    private static final String PRINTED_TEXT = "If aboard a starship at the start of a battle, can immediately evacuate with one other person to a related site. Power +1 if at same site as Tarkin. Adds 2 to power of any starship he pilots.";
    private static final String CURRENT_TEXT = "Adds 2 to power of anything he pilots. Power +1 if at same site as Tarkin. If a battle was just initiated at a system where Bast is present with your non-droid character aboard a starship, may 'evacuate' (relocate) both to a related site.";

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
    public void ChiefBastPrintedHasThePrintedTextAndTextState() {
        var scn = GetScenario("1_165o");
        var printed = scn.GetDSCard("pilot").getBlueprint();
        var bare = GetScenario("1_165").GetDSCard("pilot").getBlueprint();
        assertEquals(PRINTED_TEXT, printed.getGameText());
        TextState state = printed.getTextState();
        assertEquals("1_165", state.currentId());
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
        assertEquals("Chief Bast [Printed]", GameUtils.getFullName(printed));
    }

    @Test
    public void ChiefBastBareCardIsUnchanged() {
        var scn = GetScenario("1_165");
        var bare = scn.GetDSCard("pilot").getBlueprint();
        assertEquals(CURRENT_TEXT, bare.getGameText());
        assertNull(bare.getTextState());
        assertEquals("Chief Bast", GameUtils.getFullName(bare));
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
    public void ChiefBastPrintedAddsPowerOnlyToAStarshipHePilots() {
        assertEquals(addedPower("1_165", false), addedPower("1_165o", false)); // starship: as the current card
        int bareVehicle = addedPower("1_165", true);
        assertEquals(bareVehicle - 2, addedPower("1_165o", true)); // vehicle: no bonus as printed
    }
}
