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
 * Obi-Wan Kenobi text states (docs/legacy-versions.md, "Text states"): 1_21o as printed (Premiere, 1995) and the bare card
 * 1_21 unchanged. Texts from wiki.swccg.com [[Obi-Wan Kenobi]] and [[Obi-Wan Kenobi (Errata)]]. The printed text plays as the current card
 * ("When present at the start of a battle" is the current "when a battle was just initiated where present"), so the printed state reuses the current card's game text code.
 */
public class Card_1_021_TextStates_Tests {
    private static final String PRINTED_TEXT = "When present at the start of a battle, may use 1 Force to cause one opponent's character of ability = 1 (your choice) to move away (for free), or that character is immediately lost. Immune to attrition < 5.";
    private static final String CURRENT_TEXT = "When a battle was just initiated where present, may use 1 Force to choose one opponent's character of ability = 1 present to move away (for free), or that character is lost. Immune to attrition < 5.";

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
    public void ObiWanKenobiPrintedHasThePrintedTextAndTextState() {
        var scn = GetScenario("1_21o");
        var printed = scn.GetLSCard("card").getBlueprint();
        var bare = GetScenario("1_21").GetLSCard("card").getBlueprint();
        assertEquals(PRINTED_TEXT, printed.getGameText());
        TextState state = printed.getTextState();
        assertEquals("1_21", state.currentId());
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
        assertEquals("Obi-Wan Kenobi [Printed]", GameUtils.getFullName(printed));
    }

    @Test
    public void ObiWanKenobiPrintedPlaysAsTheCurrentCard() {
        var printed = GetScenario("1_21o").GetLSCard("card").getBlueprint();
        var bare = GetScenario("1_21").GetLSCard("card").getBlueprint();
        // Same game text code: the printed class only changes the text and the text-state information
        assertSame(Card1_021.class, printed.getClass().getSuperclass());
        assertSame(Card1_021.class, bare.getClass());
    }

    @Test
    public void ObiWanKenobiBareCardIsUnchanged() {
        var bare = GetScenario("1_21").GetLSCard("card").getBlueprint();
        assertEquals(CURRENT_TEXT, bare.getGameText());
        assertNull(bare.getTextState());
        assertEquals("Obi-Wan Kenobi", GameUtils.getFullName(bare));
    }
}
