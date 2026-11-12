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
 * Grondorn Muse text states (docs/legacy-versions.md, "Text states"): 7_19o as printed (Special Edition, 1999, wiki.swccg.com
 * [[Grondorn Muse]], scan-verified) and the bare card 7_19 (Players Committee text, [[Grondorn Muse (PC Errata)]]) unchanged.
 */
public class Card_7_019_TextStates_Tests {
    private static final String PRINTED_TEXT = "While Grondorn is on Yavin 4, your Yavin 4 Sentry is not unique (•), is doubled, deploys free, applies all three of its modifiers and is immune to Alter. Power -1 when not on Yavin 4.";
    private static final String CURRENT_TEXT = "While Grondorn is on Yavin 4, your Yavin Sentry is not unique (•), is doubled, deploys free, applies all three of its modifiers and is immune to Alter. Power -1 when not on Yavin 4.";

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
    public void GrondornMusePrintedHasThePrintedText() {
        var scn = GetScenario("7_19o");
        var card = scn.GetLSCard("card").getBlueprint();
        var bare = GetScenario("7_19").GetLSCard("card").getBlueprint();
        assertEquals(PRINTED_TEXT, card.getGameText());
        assertEquals(bare.getTitle(), card.getTitle());
        assertEquals(bare.getUniqueness(), card.getUniqueness());
        assertEquals(bare.getSide(), card.getSide());
        assertEquals(bare.getExpansionSet(), card.getExpansionSet());
        assertEquals(bare.getRarity(), card.getRarity());
        assertEquals(bare.getLore(), card.getLore());
        assertEquals(bare.getDestiny(), card.getDestiny(), scn.epsilon);
        TextState state = card.getTextState();
        assertEquals("7_19", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(1999, 11, 1), state.printedOn());
        assertEquals("Grondorn Muse [Printed]", GameUtils.getFullName(card));
    }

    @Test
    public void GrondornMuseBareCardIsUnchanged() {
        var card = GetScenario("7_19").GetLSCard("card").getBlueprint();
        assertEquals(CURRENT_TEXT, card.getGameText());
        assertNull(card.getTextState());
        assertEquals("Grondorn Muse", GameUtils.getFullName(card));
    }
}
