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
 * Moment Of Triumph text states (docs/legacy-versions.md, "Text states"): 1_260o as printed (Premiere, 1995) and the bare card
 * 1_260 unchanged. Texts from wiki.swccg.com [[Moment Of Triumph]] and [[Moment Of Triumph (PC Errata)]]. The printed text plays as the current card
 * (The alien defending a battle alone is the player's own alien; the errata only says "your"), so the printed state reuses the current card's game text code.
 */
public class Card_1_260_TextStates_Tests {
    private static final String PRINTED_TEXT = "If Tarkin is defending a battle alone, add two battle destiny. OR If any alien is defending a battle alone, add one battle destiny.";
    private static final String CURRENT_TEXT = "If Tarkin is defending a battle alone, add two battle destiny. OR If your alien is defending a battle alone, add one battle destiny.";

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
    public void MomentOfTriumphPrintedHasThePrintedTextAndTextState() {
        var scn = GetScenario("1_260o");
        var printed = scn.GetDSCard("card").getBlueprint();
        var bare = GetScenario("1_260").GetDSCard("card").getBlueprint();
        assertEquals(PRINTED_TEXT, printed.getGameText());
        TextState state = printed.getTextState();
        assertEquals("1_260", state.currentId());
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
        assertEquals("Moment Of Triumph [Printed]", GameUtils.getFullName(printed));
    }

    @Test
    public void MomentOfTriumphPrintedPlaysAsTheCurrentCard() {
        var printed = GetScenario("1_260o").GetDSCard("card").getBlueprint();
        var bare = GetScenario("1_260").GetDSCard("card").getBlueprint();
        // Same game text code: the printed class only changes the text and the text-state information
        assertSame(Card1_260.class, printed.getClass().getSuperclass());
        assertSame(Card1_260.class, bare.getClass());
    }

    @Test
    public void MomentOfTriumphBareCardIsUnchanged() {
        var bare = GetScenario("1_260").GetDSCard("card").getBlueprint();
        assertEquals(CURRENT_TEXT, bare.getGameText());
        assertNull(bare.getTextState());
        assertEquals("Moment Of Triumph", GameUtils.getFullName(bare));
    }
}
