package com.gempukku.swccgo.cards.set7.light;

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
 * Sandspeeder text states (docs/legacy-versions.md, "Text states"): 7_156o as printed (Special Edition, 1999, wiki.swccg.com
 * [[Sandspeeder]], scan-verified) and the bare card 7_156 (Players Committee text, [[Sandspeeder (PC Errata)]]) unchanged.
 */
public class Card_7_156_TextStates_Tests {
    private static final String PRINTED_TEXT = "May add 1 pilot or passenger. Permanent pilot provides ability of 1. May move as a 'react' only to Tatooine or desert sites.";
    private static final String CURRENT_TEXT = "May add 1 pilot or passenger. Permanent pilot provides ability of 1. May move as a 'react' to Tatooine or desert sites.";

    protected VirtualTableScenario GetScenario(String id) {
        return new VirtualTableScenario(
                new HashMap<>() {{ put("card", id); }},
                new HashMap<>() {{ put("trooper", "1_194"); }},
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
    public void SandspeederPrintedHasThePrintedText() {
        var scn = GetScenario("7_156o");
        var card = scn.GetLSCard("card").getBlueprint();
        var bare = GetScenario("7_156").GetLSCard("card").getBlueprint();
        assertEquals(PRINTED_TEXT, card.getGameText());
        assertEquals(bare.getTitle(), card.getTitle());
        assertEquals(bare.getUniqueness(), card.getUniqueness());
        assertEquals(bare.getSide(), card.getSide());
        assertEquals(bare.getExpansionSet(), card.getExpansionSet());
        assertEquals(bare.getRarity(), card.getRarity());
        assertEquals(bare.getLore(), card.getLore());
        assertEquals(bare.getDestiny(), card.getDestiny(), scn.epsilon);
        TextState state = card.getTextState();
        assertEquals("7_156", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(1999, 11, 1), state.printedOn());
        assertEquals("Sandspeeder [Printed]", GameUtils.getFullName(card));
    }

    @Test
    public void SandspeederBareCardIsUnchanged() {
        var card = GetScenario("7_156").GetLSCard("card").getBlueprint();
        assertEquals(CURRENT_TEXT, card.getGameText());
        assertNull(card.getTextState());
        assertEquals("Sandspeeder", GameUtils.getFullName(card));
    }
}
