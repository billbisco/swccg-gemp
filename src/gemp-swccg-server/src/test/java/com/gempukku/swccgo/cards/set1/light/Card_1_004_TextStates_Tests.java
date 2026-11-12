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

/**
 * BoShek text states (docs/legacy-versions.md, "Text states"): 1_4o as printed (Premiere, 1995) and the bare card
 * 1_4 unchanged. Texts from wiki.swccg.com [[BoShek]] and [[BoShek (Errata)]].
 */
public class Card_1_004_TextStates_Tests {
    private static final String PRINTED_TEXT = "Adds 3 to power of any starship he pilots. May make a Kessel Run in place of a smuggler. Immune to attrition < 3.";
    private static final String CURRENT_TEXT = "Adds 3 to power of anything he pilots. May make a Kessel Run in place of a smuggler. Immune to attrition < 3.";

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
    public void BoShekPrintedHasThePrintedTextAndTextState() {
        var scn = GetScenario("1_4o");
        var printed = scn.GetLSCard("pilot").getBlueprint();
        var bare = GetScenario("1_4").GetLSCard("pilot").getBlueprint();
        assertEquals(PRINTED_TEXT, printed.getGameText());
        TextState state = printed.getTextState();
        assertEquals("1_4", state.currentId());
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
        assertEquals("BoShek [Printed]", GameUtils.getFullName(printed));
    }

    @Test
    public void BoShekBareCardIsUnchanged() {
        var scn = GetScenario("1_4");
        var bare = scn.GetLSCard("pilot").getBlueprint();
        assertEquals(CURRENT_TEXT, bare.getGameText());
        assertNull(bare.getTextState());
        assertEquals("BoShek", GameUtils.getFullName(bare));
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
    public void BoShekPrintedAddsPowerOnlyToAStarshipHePilots() {
        assertEquals(addedPower("1_4", false), addedPower("1_4o", false)); // starship: as the current card
        int bareVehicle = addedPower("1_4", true);
        assertEquals(bareVehicle - 3, addedPower("1_4o", true)); // vehicle: no bonus as printed
    }
}
