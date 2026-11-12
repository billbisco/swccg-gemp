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
import static org.junit.Assert.assertSame;

/**
 * Droid Shutdown text states (docs/legacy-versions.md, "Text states"): 1_78o as printed (Premiere, 1995) and the bare card
 * 1_78 unchanged. Texts from wiki.swccg.com [[Droid Shutdown]] and [[Droid Shutdown (PC Errata)]]. The printed text plays as the current card
 * (Canceling "any attempt to steal, destroy or capture one of your droids" is the current card's cancel of an attempt to steal, 'hit', lose or capture your droid), so the printed state reuses the current card's game text code.
 */
public class Card_1_078_TextStates_Tests {
    private static final String PRINTED_TEXT = "Cancel any attempt to steal, destroy or capture one of your droids. Droid is protected for remainder of this turn.";
    private static final String CURRENT_TEXT = "Cancel an attempt by opponent to target your droid to be stolen, 'hit,' lost or captured. Droid is protected from all such attempts for remainder of turn.";

    private static VirtualTableScenario GetScenario(String id) {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("card", id);
                }},
                new HashMap<>() {{
                    put("filler", "1_194");
                }},
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
    public void DroidShutdownPrintedHasThePrintedTextAndTextState() {
        var scn = GetScenario("1_78o");
        var printed = scn.GetLSCard("card").getBlueprint();
        var bare = GetScenario("1_78").GetLSCard("card").getBlueprint();
        assertEquals(PRINTED_TEXT, printed.getGameText());
        TextState state = printed.getTextState();
        assertEquals("1_78", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(1995, 12, 1), state.printedOn());
        assertEquals(bare.getTitle(), printed.getTitle());
        assertEquals(bare.getUniqueness(), printed.getUniqueness());
        assertEquals(bare.getPersonas(), printed.getPersonas());
        assertEquals(bare.getSide(), printed.getSide());
        assertEquals(bare.getCardTypes(), printed.getCardTypes());
        assertEquals(bare.getCardSubtype(), printed.getCardSubtype());
        assertEquals(bare.getRarity(), printed.getRarity());
        assertEquals(bare.getExpansionSet(), printed.getExpansionSet());
        assertEquals(bare.getLore(), printed.getLore());
        assertEquals(bare.getDestiny(), printed.getDestiny(), scn.epsilon);
        assertEquals("Droid Shutdown [Printed]", GameUtils.getFullName(printed));
    }

    @Test
    public void DroidShutdownPrintedPlaysAsTheCurrentCard() {
        var printed = GetScenario("1_78o").GetLSCard("card").getBlueprint();
        var bare = GetScenario("1_78").GetLSCard("card").getBlueprint();
        // Same game text code: the printed class only changes the text and the text-state information
        assertSame(Card1_078.class, printed.getClass().getSuperclass());
        assertSame(Card1_078.class, bare.getClass());
    }

    @Test
    public void DroidShutdownBareCardIsUnchanged() {
        var bare = GetScenario("1_78").GetLSCard("card").getBlueprint();
        assertEquals(CURRENT_TEXT, bare.getGameText());
        assertNull(bare.getTextState());
        assertEquals("Droid Shutdown", GameUtils.getFullName(bare));
    }
}
