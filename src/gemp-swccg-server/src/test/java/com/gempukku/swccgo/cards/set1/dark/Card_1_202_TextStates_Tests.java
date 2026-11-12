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
 * Droid Detector text states (docs/legacy-versions.md, "Text states"): 1_202o as printed (Premiere, 1995) and the bare card
 * 1_202 unchanged. Texts from wiki.swccg.com [[Droid Detector]] and [[Droid Detector (Errata)]]. The printed text plays as the current card
 * (Droids at the site are lost at the end of any turn after the turn it deploys (the printed "move away by the end of the opponent's next turn, or they are lost"), as the current card does), so the printed state reuses the current card's game text code.
 */
public class Card_1_202_TextStates_Tests {
    private static final String PRINTED_TEXT = "Deploy at any interior site. Cannot be moved. All droids there must move away (at normal use of the Force) by the end of the opponent's next turn, or they are lost. Droids may not deploy to same site and are lost if present at end of any turn.";
    private static final String CURRENT_TEXT = "Deploy at any interior site. Cannot be moved. Droids may not deploy to same site. Following the turn this device is deployed, all droids present are lost at end of any turn.";

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
    public void DroidDetectorPrintedHasThePrintedTextAndTextState() {
        var scn = GetScenario("1_202o");
        var printed = scn.GetDSCard("card").getBlueprint();
        var bare = GetScenario("1_202").GetDSCard("card").getBlueprint();
        assertEquals(PRINTED_TEXT, printed.getGameText());
        TextState state = printed.getTextState();
        assertEquals("1_202", state.currentId());
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
        assertEquals("Droid Detector [Printed]", GameUtils.getFullName(printed));
    }

    @Test
    public void DroidDetectorPrintedPlaysAsTheCurrentCard() {
        var printed = GetScenario("1_202o").GetDSCard("card").getBlueprint();
        var bare = GetScenario("1_202").GetDSCard("card").getBlueprint();
        // Same game text code: the printed class only changes the text and the text-state information
        assertSame(Card1_202.class, printed.getClass().getSuperclass());
        assertSame(Card1_202.class, bare.getClass());
    }

    @Test
    public void DroidDetectorBareCardIsUnchanged() {
        var bare = GetScenario("1_202").GetDSCard("card").getBlueprint();
        assertEquals(CURRENT_TEXT, bare.getGameText());
        assertNull(bare.getTextState());
        assertEquals("Droid Detector", GameUtils.getFullName(bare));
    }
}
