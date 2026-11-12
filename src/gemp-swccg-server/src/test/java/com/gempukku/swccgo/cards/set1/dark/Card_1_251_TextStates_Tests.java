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
 * Imperial Reinforcements text states (docs/legacy-versions.md, "Text states"): 1_251o as printed (Premiere, 1995) and the bare card
 * 1_251 unchanged. Texts from wiki.swccg.com [[Imperial Reinforcements]] and [[Imperial Reinforcements (Errata)]]. The printed text plays as the current card
 * ("Outnumbers you" and "TIE fighters ... retrieved from Lost Pile" are the current "more total characters and starships" and retrieving TIE/lns), so the printed state reuses the current card's game text code.
 */
public class Card_1_251_TextStates_Tests {
    private static final String PRINTED_TEXT = "If opponent outnumbers you in total characters and starships on table, use 1 Force to draw destiny. That number of Stormtroopers and/or TIE fighters may be retrieved from Lost Pile.";
    private static final String CURRENT_TEXT = "If opponent has more total characters and starships on table than you have, use 1 Force to draw destiny. Retrieve that number of Stormtroopers and/or TIE/lns.";

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
    public void ImperialReinforcementsPrintedHasThePrintedTextAndTextState() {
        var scn = GetScenario("1_251o");
        var printed = scn.GetDSCard("card").getBlueprint();
        var bare = GetScenario("1_251").GetDSCard("card").getBlueprint();
        assertEquals(PRINTED_TEXT, printed.getGameText());
        TextState state = printed.getTextState();
        assertEquals("1_251", state.currentId());
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
        assertEquals("Imperial Reinforcements [Printed]", GameUtils.getFullName(printed));
    }

    @Test
    public void ImperialReinforcementsPrintedPlaysAsTheCurrentCard() {
        var printed = GetScenario("1_251o").GetDSCard("card").getBlueprint();
        var bare = GetScenario("1_251").GetDSCard("card").getBlueprint();
        // Same game text code: the printed class only changes the text and the text-state information
        assertSame(Card1_251.class, printed.getClass().getSuperclass());
        assertSame(Card1_251.class, bare.getClass());
    }

    @Test
    public void ImperialReinforcementsBareCardIsUnchanged() {
        var bare = GetScenario("1_251").GetDSCard("card").getBlueprint();
        assertEquals(CURRENT_TEXT, bare.getGameText());
        assertNull(bare.getTextState());
        assertEquals("Imperial Reinforcements", GameUtils.getFullName(bare));
    }
}
