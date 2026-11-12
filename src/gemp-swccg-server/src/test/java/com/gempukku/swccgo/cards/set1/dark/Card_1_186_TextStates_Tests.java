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
 * LIN-V8M (Elleyein-Veeateemm) text states (docs/legacy-versions.md, "Text states"): 1_186o as printed (Premiere, 1995) and the bare card
 * 1_186 unchanged. Texts from wiki.swccg.com [[LIN-V8M (Elleyein-Veeateemm)]] and [[LIN-V8M (Elleyein-Veeateemm) (PC Errata)]]. The printed text plays as the current card
 * ("Lays mines" names no game action (Premiere has no mines), so only the defuse ability plays), so the printed state reuses the current card's game text code.
 */
public class Card_1_186_TextStates_Tests {
    private static final String PRINTED_TEXT = "Lays mines. OR At any time during your turn, may use 1 Force to 'defuse' (lose) any one mine at same site.";
    private static final String CURRENT_TEXT = "At any time during your turn, may use 1 Force to 'defuse' (lose) any one mine at same site.";

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
    public void LINV8MElleyeinVeeateemmPrintedHasThePrintedTextAndTextState() {
        var scn = GetScenario("1_186o");
        var printed = scn.GetDSCard("card").getBlueprint();
        var bare = GetScenario("1_186").GetDSCard("card").getBlueprint();
        assertEquals(PRINTED_TEXT, printed.getGameText());
        TextState state = printed.getTextState();
        assertEquals("1_186", state.currentId());
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
        assertEquals("LIN-V8M (Elleyein-Veeateemm) [Printed]", GameUtils.getFullName(printed));
    }

    @Test
    public void LINV8MElleyeinVeeateemmPrintedPlaysAsTheCurrentCard() {
        var printed = GetScenario("1_186o").GetDSCard("card").getBlueprint();
        var bare = GetScenario("1_186").GetDSCard("card").getBlueprint();
        // Same game text code: the printed class only changes the text and the text-state information
        assertSame(Card1_186.class, printed.getClass().getSuperclass());
        assertSame(Card1_186.class, bare.getClass());
    }

    @Test
    public void LINV8MElleyeinVeeateemmBareCardIsUnchanged() {
        var bare = GetScenario("1_186").GetDSCard("card").getBlueprint();
        assertEquals(CURRENT_TEXT, bare.getGameText());
        assertNull(bare.getTextState());
        assertEquals("LIN-V8M (Elleyein-Veeateemm)", GameUtils.getFullName(bare));
    }
}
