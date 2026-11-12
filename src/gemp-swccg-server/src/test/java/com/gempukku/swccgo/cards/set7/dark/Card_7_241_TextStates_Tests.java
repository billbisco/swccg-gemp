package com.gempukku.swccgo.cards.set7.dark;

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
 * Sienar Fleet Systems text states (docs/legacy-versions.md, "Text states"): 7_241o as printed (Special Edition, 1999, wiki.swccg.com
 * [[Sienar Fleet Systems]], scan-verified) and the bare card 7_241 (Players Committee text, [[Sienar Fleet Systems (PC Errata)]]) unchanged.
 */
public class Card_7_241_TextStates_Tests {
    private static final String PRINTED_TEXT = "Deploy on Wakeelmui system. Retrieve 1 Force each time you deploy a TIE. Also, once per turn you may relocate a TIE just lost from table to Used Pile. (Immune to Alter while you occupy Wakeelmui.)";
    private static final String CURRENT_TEXT = "Deploy on Wakeelmui system. Each time you deploy a TIE, retrieve 1 Force (or 3 if a TIE squadron). Also once per turn you may relocate a TIE just lost from table to Used Pile. (Immune to Alter while you occupy Wakeelmui.)";

    protected VirtualTableScenario GetScenario(String id) {
        return new VirtualTableScenario(
                new HashMap<>() {{ put("rebel", "1_28"); }},
                new HashMap<>() {{ put("card", id); }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                "anything_goes"
        );
    }

    @Test
    public void SienarFleetSystemsPrintedHasThePrintedText() {
        var scn = GetScenario("7_241o");
        var card = scn.GetDSCard("card").getBlueprint();
        var bare = GetScenario("7_241").GetDSCard("card").getBlueprint();
        assertEquals(PRINTED_TEXT, card.getGameText());
        assertEquals(bare.getTitle(), card.getTitle());
        assertEquals(bare.getUniqueness(), card.getUniqueness());
        assertEquals(bare.getSide(), card.getSide());
        assertEquals(bare.getExpansionSet(), card.getExpansionSet());
        assertEquals(bare.getRarity(), card.getRarity());
        assertEquals(bare.getLore(), card.getLore());
        assertEquals(bare.getDestiny(), card.getDestiny(), scn.epsilon);
        TextState state = card.getTextState();
        assertEquals("7_241", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(1999, 11, 1), state.printedOn());
        assertEquals("Sienar Fleet Systems [Printed]", GameUtils.getFullName(card));
    }

    @Test
    public void SienarFleetSystemsBareCardIsUnchanged() {
        var card = GetScenario("7_241").GetDSCard("card").getBlueprint();
        assertEquals(CURRENT_TEXT, card.getGameText());
        assertNull(card.getTextState());
        assertEquals("Sienar Fleet Systems", GameUtils.getFullName(card));
    }
}
