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
 * Jek Porkins text states (docs/legacy-versions.md, "Text states"): 1_13o as printed (Premiere, 1995) and the bare card
 * 1_13 unchanged. Texts from wiki.swccg.com [[Jek Porkins]] and [[Jek Porkins (Errata)]].
 */
public class Card_1_013_TextStates_Tests {
    private static final String PRINTED_TEXT = "Adds 2 to power of any starship he pilots. On Red 6 also adds 1 to maneuver and draws one battle destiny if not able to otherwise.";
    private static final String CURRENT_TEXT = "Adds 2 to power of anything he pilots. When piloting Red 6, also adds 1 to maneuver and draws one battle destiny if not able to otherwise.";

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
    public void JekPorkinsPrintedHasThePrintedTextAndTextState() {
        var scn = GetScenario("1_13o");
        var printed = scn.GetLSCard("pilot").getBlueprint();
        var bare = GetScenario("1_13").GetLSCard("pilot").getBlueprint();
        assertEquals(PRINTED_TEXT, printed.getGameText());
        TextState state = printed.getTextState();
        assertEquals("1_13", state.currentId());
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
        assertEquals("Jek Porkins [Printed]", GameUtils.getFullName(printed));
    }

    @Test
    public void JekPorkinsBareCardIsUnchanged() {
        var scn = GetScenario("1_13");
        var bare = scn.GetLSCard("pilot").getBlueprint();
        assertEquals(CURRENT_TEXT, bare.getGameText());
        assertNull(bare.getTextState());
        assertEquals("Jek Porkins", GameUtils.getFullName(bare));
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
    public void JekPorkinsPrintedAddsPowerOnlyToAStarshipHePilots() {
        assertEquals(addedPower("1_13", false), addedPower("1_13o", false)); // starship: as the current card
        int bareVehicle = addedPower("1_13", true);
        assertEquals(bareVehicle - 2, addedPower("1_13o", true)); // vehicle: no bonus as printed
    }
}
