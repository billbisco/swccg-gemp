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
 * Nevar Yalnal text states (docs/legacy-versions.md, "Text states"): 1_261o as printed (Premiere, 1995) and the bare card
 * 1_261 unchanged. Texts from wiki.swccg.com [[Nevar Yalnal]] and [[Nevar Yalnal (PC Errata)]]. The printed text plays as the current card
 * ("Lost if destiny > 2; if Undercover, lost if destiny > 0" is the same test as the current "add 2 if Undercover; lost if total destiny > 2"), so the printed state reuses the current card's game text code.
 */
public class Card_1_261_TextStates_Tests {
    private static final String PRINTED_TEXT = "If both players have a spy at the same site, draw destiny. Opponent's spy is lost if destiny > 2. If spy is Undercover, it is lost if destiny > 0.";
    private static final String CURRENT_TEXT = "If both players have a spy at same site, draw destiny. Add 2 if opponent's spy is Undercover. Opponent's spy is lost if total destiny > 2.";

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
    public void NevarYalnalPrintedHasThePrintedTextAndTextState() {
        var scn = GetScenario("1_261o");
        var printed = scn.GetDSCard("card").getBlueprint();
        var bare = GetScenario("1_261").GetDSCard("card").getBlueprint();
        assertEquals(PRINTED_TEXT, printed.getGameText());
        TextState state = printed.getTextState();
        assertEquals("1_261", state.currentId());
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
        assertEquals("Nevar Yalnal [Printed]", GameUtils.getFullName(printed));
    }

    @Test
    public void NevarYalnalPrintedPlaysAsTheCurrentCard() {
        var printed = GetScenario("1_261o").GetDSCard("card").getBlueprint();
        var bare = GetScenario("1_261").GetDSCard("card").getBlueprint();
        // Same game text code: the printed class only changes the text and the text-state information
        assertSame(Card1_261.class, printed.getClass().getSuperclass());
        assertSame(Card1_261.class, bare.getClass());
    }

    @Test
    public void NevarYalnalBareCardIsUnchanged() {
        var bare = GetScenario("1_261").GetDSCard("card").getBlueprint();
        assertEquals(CURRENT_TEXT, bare.getGameText());
        assertNull(bare.getTextState());
        assertEquals("Nevar Yalnal", GameUtils.getFullName(bare));
    }
}
