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
 * Feltipern Trevagg text states (docs/legacy-versions.md, "Text states"): 1_176o as printed (Premiere, 1995) and the bare card
 * 1_176 unchanged. Texts from wiki.swccg.com [[Feltipern Trevagg]] and [[Feltipern Trevagg (PC Errata)]]. The printed text plays as the current card
 * ("If a droid is present, Trevagg's game text is unusable" is the current "while no droid present with Trevagg"), so the printed state reuses the current card's game text code.
 */
public class Card_1_176_TextStates_Tests {
    private static final String PRINTED_TEXT = "To initiate battles at same location as Trevagg, player must use X Force, where X = total number of [Light Side Force] icons and [Dark Side Force] icons present. If a droid is present, Trevagg's 'game text' is unusable.";
    private static final String CURRENT_TEXT = "While no droid present with Trevagg, to initiate battles at same location as Trevagg, player must use X Force, where X = total number of [Dark Side Force] and [Light Side Force] present.";

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
    public void FeltipernTrevaggPrintedHasThePrintedTextAndTextState() {
        var scn = GetScenario("1_176o");
        var printed = scn.GetDSCard("card").getBlueprint();
        var bare = GetScenario("1_176").GetDSCard("card").getBlueprint();
        assertEquals(PRINTED_TEXT, printed.getGameText());
        TextState state = printed.getTextState();
        assertEquals("1_176", state.currentId());
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
        assertEquals("Feltipern Trevagg [Printed]", GameUtils.getFullName(printed));
    }

    @Test
    public void FeltipernTrevaggPrintedPlaysAsTheCurrentCard() {
        var printed = GetScenario("1_176o").GetDSCard("card").getBlueprint();
        var bare = GetScenario("1_176").GetDSCard("card").getBlueprint();
        // Same game text code: the printed class only changes the text and the text-state information
        assertSame(Card1_176.class, printed.getClass().getSuperclass());
        assertSame(Card1_176.class, bare.getClass());
    }

    @Test
    public void FeltipernTrevaggBareCardIsUnchanged() {
        var bare = GetScenario("1_176").GetDSCard("card").getBlueprint();
        assertEquals(CURRENT_TEXT, bare.getGameText());
        assertNull(bare.getTextState());
        assertEquals("Feltipern Trevagg", GameUtils.getFullName(bare));
    }
}
