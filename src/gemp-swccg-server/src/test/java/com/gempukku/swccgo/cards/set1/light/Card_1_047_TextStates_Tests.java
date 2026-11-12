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
 * Demotion text states (docs/legacy-versions.md, "Text states"): 1_47o as printed (Premiere, 1995) and the bare card
 * 1_47 unchanged. Texts from wiki.swccg.com [[Demotion]] and [[Demotion (Errata)]]. The printed text plays as the current card
 * (Only the wording of the excluded Imperials (Darth Vader, Emperor Palpatine, Stormtrooper) and of the canceled game text changed), so the printed state reuses the current card's game text code.
 */
public class Card_1_047_TextStates_Tests {
    private static final String PRINTED_TEXT = "Use 2 Force to deploy on any Imperial, except Darth Vader, Emperor Palpatine or Stormtrooper. That Imperial is power -2 and cannot use its \"game text.\"";
    private static final String CURRENT_TEXT = "Use 2 Force to deploy on any Imperial except Vader, Emperor or a stormtrooper. That Imperial is power -2 and its game text is canceled.";

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
    public void DemotionPrintedHasThePrintedTextAndTextState() {
        var scn = GetScenario("1_47o");
        var printed = scn.GetLSCard("card").getBlueprint();
        var bare = GetScenario("1_47").GetLSCard("card").getBlueprint();
        assertEquals(PRINTED_TEXT, printed.getGameText());
        TextState state = printed.getTextState();
        assertEquals("1_47", state.currentId());
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
        assertEquals("Demotion [Printed]", GameUtils.getFullName(printed));
    }

    @Test
    public void DemotionPrintedPlaysAsTheCurrentCard() {
        var printed = GetScenario("1_47o").GetLSCard("card").getBlueprint();
        var bare = GetScenario("1_47").GetLSCard("card").getBlueprint();
        // Same game text code: the printed class only changes the text and the text-state information
        assertSame(Card1_047.class, printed.getClass().getSuperclass());
        assertSame(Card1_047.class, bare.getClass());
    }

    @Test
    public void DemotionBareCardIsUnchanged() {
        var bare = GetScenario("1_47").GetLSCard("card").getBlueprint();
        assertEquals(CURRENT_TEXT, bare.getGameText());
        assertNull(bare.getTextState());
        assertEquals("Demotion", GameUtils.getFullName(bare));
    }
}
