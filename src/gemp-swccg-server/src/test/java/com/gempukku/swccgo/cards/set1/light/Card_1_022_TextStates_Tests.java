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
 * Owen Lars text states (docs/legacy-versions.md, "Text states"): 1_22o as printed (Premiere, 1995) and the bare card
 * 1_22 unchanged. Texts from wiki.swccg.com [[Owen Lars]] and [[Owen Lars (Errata)]]. The printed text plays as the current card
 * ("If lost during the opponent's turn" is the current "if lost from table during opponent's turn"), so the printed state reuses the current card's game text code.
 */
public class Card_1_022_TextStates_Tests {
    private static final String PRINTED_TEXT = "Deploys free at Lars' Moisture Farm. Power +2 if at same site as Beru Lars or one Vaporator. If lost during the opponent's turn, Luke is power +3 until the end of your next turn.";
    private static final String CURRENT_TEXT = "Deploys free at Lars' Moisture Farm. Power +2 if at same site as Beru Lars or a Vaporator. If lost from table during opponent's turn, Luke is power +3 until the end of your next turn.";

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
    public void OwenLarsPrintedHasThePrintedTextAndTextState() {
        var scn = GetScenario("1_22o");
        var printed = scn.GetLSCard("card").getBlueprint();
        var bare = GetScenario("1_22").GetLSCard("card").getBlueprint();
        assertEquals(PRINTED_TEXT, printed.getGameText());
        TextState state = printed.getTextState();
        assertEquals("1_22", state.currentId());
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
        assertEquals("Owen Lars [Printed]", GameUtils.getFullName(printed));
    }

    @Test
    public void OwenLarsPrintedPlaysAsTheCurrentCard() {
        var printed = GetScenario("1_22o").GetLSCard("card").getBlueprint();
        var bare = GetScenario("1_22").GetLSCard("card").getBlueprint();
        // Same game text code: the printed class only changes the text and the text-state information
        assertSame(Card1_022.class, printed.getClass().getSuperclass());
        assertSame(Card1_022.class, bare.getClass());
    }

    @Test
    public void OwenLarsBareCardIsUnchanged() {
        var bare = GetScenario("1_22").GetLSCard("card").getBlueprint();
        assertEquals(CURRENT_TEXT, bare.getGameText());
        assertNull(bare.getTextState());
        assertEquals("Owen Lars", GameUtils.getFullName(bare));
    }
}
