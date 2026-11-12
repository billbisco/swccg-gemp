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
 * Old Ben text states (docs/legacy-versions.md, "Text states"): 1_100o as printed (Premiere, 1995) and the bare card
 * 1_100 unchanged. Texts from wiki.swccg.com [[Old Ben]] and [[Old Ben (PC Errata)]]. The printed text plays as the current card
 * ("Revives (re-deploy for free) that character back to same site" is the current card's return from the Lost Pile to that site), so the printed state reuses the current card's game text code.
 */
public class Card_1_100_TextStates_Tests {
    private static final String PRINTED_TEXT = "Use 1 Force if any of your characters (except Obi-Wan) was just forfeited from a Tatooine site. Mysterious \"crazy wizard\" steps in and revives (re-deploy for free) that character back to same site.";
    private static final String CURRENT_TEXT = "Use 1 Force if any of your characters (except Obi-Wan) was just forfeited from a Tatooine site. Mysterious 'crazy wizard' steps in and revives (returns from Lost Pile) that character back to same site.";

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
    public void OldBenPrintedHasThePrintedTextAndTextState() {
        var scn = GetScenario("1_100o");
        var printed = scn.GetLSCard("card").getBlueprint();
        var bare = GetScenario("1_100").GetLSCard("card").getBlueprint();
        assertEquals(PRINTED_TEXT, printed.getGameText());
        TextState state = printed.getTextState();
        assertEquals("1_100", state.currentId());
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
        assertEquals("Old Ben [Printed]", GameUtils.getFullName(printed));
    }

    @Test
    public void OldBenPrintedPlaysAsTheCurrentCard() {
        var printed = GetScenario("1_100o").GetLSCard("card").getBlueprint();
        var bare = GetScenario("1_100").GetLSCard("card").getBlueprint();
        // Same game text code: the printed class only changes the text and the text-state information
        assertSame(Card1_100.class, printed.getClass().getSuperclass());
        assertSame(Card1_100.class, bare.getClass());
    }

    @Test
    public void OldBenBareCardIsUnchanged() {
        var bare = GetScenario("1_100").GetLSCard("card").getBlueprint();
        assertEquals(CURRENT_TEXT, bare.getGameText());
        assertNull(bare.getTextState());
        assertEquals("Old Ben", GameUtils.getFullName(bare));
    }
}
