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
 * Imperial Trooper Guard text states (docs/legacy-versions.md, "Text states"): 1_181o as printed (Premiere, 1995) and the bare card
 * 1_181 unchanged. Texts from wiki.swccg.com [[Imperial Trooper Guard]] and [[Imperial Trooper Guard (PC Errata)]]. The printed text plays as the current card
 * (The printed power "*" (0 in an offensive battle, 4 when defending) is the current power 0 with power +4 when defending), so the printed state reuses the current card's game text code.
 */
public class Card_1_181_TextStates_Tests {
    private static final String PRINTED_TEXT = "POWER * / Cannot move. *Power = 0 during an offensive battle, but Power = 4 when defending a battle.";
    private static final String CURRENT_TEXT = "Power +4 when defending a battle. Cannot move.";

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
    public void ImperialTrooperGuardPrintedHasThePrintedTextAndTextState() {
        var scn = GetScenario("1_181o");
        var printed = scn.GetDSCard("card").getBlueprint();
        var bare = GetScenario("1_181").GetDSCard("card").getBlueprint();
        assertEquals(PRINTED_TEXT, printed.getGameText());
        TextState state = printed.getTextState();
        assertEquals("1_181", state.currentId());
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
        assertEquals("Imperial Trooper Guard [Printed]", GameUtils.getFullName(printed));
    }

    @Test
    public void ImperialTrooperGuardPrintedPlaysAsTheCurrentCard() {
        var printed = GetScenario("1_181o").GetDSCard("card").getBlueprint();
        var bare = GetScenario("1_181").GetDSCard("card").getBlueprint();
        // Same game text code: the printed class only changes the text and the text-state information
        assertSame(Card1_181.class, printed.getClass().getSuperclass());
        assertSame(Card1_181.class, bare.getClass());
    }

    @Test
    public void ImperialTrooperGuardBareCardIsUnchanged() {
        var bare = GetScenario("1_181").GetDSCard("card").getBlueprint();
        assertEquals(CURRENT_TEXT, bare.getGameText());
        assertNull(bare.getTextState());
        assertEquals("Imperial Trooper Guard", GameUtils.getFullName(bare));
    }
}
