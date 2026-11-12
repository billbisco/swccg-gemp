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
 * Rebel Guard text states (docs/legacy-versions.md, "Text states"): 1_26o as printed (Premiere, 1995) and the bare card
 * 1_26 unchanged. Texts from wiki.swccg.com [[Rebel Guard]] and [[Rebel Guard (PC Errata)]]. The printed text plays as the current card
 * (The printed power "*" (0 in an offensive battle, 4 when defending) is the current power 0 with power +4 when defending), so the printed state reuses the current card's game text code.
 */
public class Card_1_026_TextStates_Tests {
    private static final String PRINTED_TEXT = "Cannot move. * Power = 0 during an offensive battle, but Power = 4 when defending a battle.";
    private static final String CURRENT_TEXT = "Power +4 when defending a battle. Cannot move.";

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
    public void RebelGuardPrintedHasThePrintedTextAndTextState() {
        var scn = GetScenario("1_26o");
        var printed = scn.GetLSCard("card").getBlueprint();
        var bare = GetScenario("1_26").GetLSCard("card").getBlueprint();
        assertEquals(PRINTED_TEXT, printed.getGameText());
        TextState state = printed.getTextState();
        assertEquals("1_26", state.currentId());
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
        assertEquals("Rebel Guard [Printed]", GameUtils.getFullName(printed));
    }

    @Test
    public void RebelGuardPrintedPlaysAsTheCurrentCard() {
        var printed = GetScenario("1_26o").GetLSCard("card").getBlueprint();
        var bare = GetScenario("1_26").GetLSCard("card").getBlueprint();
        // Same game text code: the printed class only changes the text and the text-state information
        assertSame(Card1_026.class, printed.getClass().getSuperclass());
        assertSame(Card1_026.class, bare.getClass());
    }

    @Test
    public void RebelGuardBareCardIsUnchanged() {
        var bare = GetScenario("1_26").GetLSCard("card").getBlueprint();
        assertEquals(CURRENT_TEXT, bare.getGameText());
        assertNull(bare.getTextState());
        assertEquals("Rebel Guard", GameUtils.getFullName(bare));
    }
}
