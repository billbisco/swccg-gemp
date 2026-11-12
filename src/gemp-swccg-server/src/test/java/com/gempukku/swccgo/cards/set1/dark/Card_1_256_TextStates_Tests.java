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
import static org.junit.Assert.assertSame;

/**
 * Local Trouble text states (docs/legacy-versions.md, "Text states"): 1_256o as printed (Premiere, 1995) and the bare card
 * 1_256 unchanged. Texts from wiki.swccg.com [[Local Trouble]] and [[Local Trouble (PC Errata)]]. The printed text plays as the current card
 * ("Any two Stormtroopers in the Cantina" battle for you, so they are your Stormtroopers, as the errata says), so the printed state reuses the current card's game text code.
 */
public class Card_1_256_TextStates_Tests {
    private static final String PRINTED_TEXT = "Use 1 Force at the beginning of your battle phase to allow any two Stormtroopers in the Cantina to battle any one opponent's character (your choice). You may add one battle destiny. No other battles may occur in Cantina this turn.";
    private static final String CURRENT_TEXT = "Use 1 Force at the beginning of your battle phase to allow any two of your Stormtroopers in the Cantina to battle any one opponent's character (your choice). You may add one battle destiny. No other battles may occur in Cantina this turn.";

    private static VirtualTableScenario GetScenario(String id) {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("filler", "1_28");
                }},
                new HashMap<>() {{
                    put("card", id);
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
    public void LocalTroublePrintedHasThePrintedTextAndTextState() {
        var scn = GetScenario("1_256o");
        var printed = scn.GetDSCard("card").getBlueprint();
        var bare = GetScenario("1_256").GetDSCard("card").getBlueprint();
        assertEquals(PRINTED_TEXT, printed.getGameText());
        TextState state = printed.getTextState();
        assertEquals("1_256", state.currentId());
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
        assertEquals("Local Trouble [Printed]", GameUtils.getFullName(printed));
    }

    @Test
    public void LocalTroublePrintedPlaysAsTheCurrentCard() {
        var printed = GetScenario("1_256o").GetDSCard("card").getBlueprint();
        var bare = GetScenario("1_256").GetDSCard("card").getBlueprint();
        // Same game text code: the printed class only changes the text and the text-state information
        assertSame(Card1_256.class, printed.getClass().getSuperclass());
        assertSame(Card1_256.class, bare.getClass());
    }

    @Test
    public void LocalTroubleBareCardIsUnchanged() {
        var bare = GetScenario("1_256").GetDSCard("card").getBlueprint();
        assertEquals(CURRENT_TEXT, bare.getGameText());
        assertNull(bare.getTextState());
        assertEquals("Local Trouble", GameUtils.getFullName(bare));
    }
}
