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
 * Crash Site Memorial text states (docs/legacy-versions.md, "Text states"): 1_45o as printed (Premiere, 1995) and the bare card
 * 1_45 unchanged. Texts from wiki.swccg.com [[Crash Site Memorial]] and [[Crash Site Memorial (PC Errata)]]. The printed text plays as the current card
 * (Placing a just-lost vehicle, droid, weapon or device here and swapping one with a hand card once during your deploy phase is the current card's behaviour), so the printed state reuses the current card's game text code.
 */
public class Card_1_045_TextStates_Tests {
    private static final String PRINTED_TEXT = "Deploy on your side of table. You may place lost vehicles, droids, weapons and devices here instead of Lost Pile. Once each turn, during your deploy phase, you may exchange any one hand card with one card from your 'memorial'.";
    private static final String CURRENT_TEXT = "Deploy on your side of table. If you just lost a vehicle, droid, weapon, or device, may stack it here. Once during your deploy phase, you may exchange any one card in hand with one card stacked here.";

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
    public void CrashSiteMemorialPrintedHasThePrintedTextAndTextState() {
        var scn = GetScenario("1_45o");
        var printed = scn.GetLSCard("card").getBlueprint();
        var bare = GetScenario("1_45").GetLSCard("card").getBlueprint();
        assertEquals(PRINTED_TEXT, printed.getGameText());
        TextState state = printed.getTextState();
        assertEquals("1_45", state.currentId());
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
        assertEquals("Crash Site Memorial [Printed]", GameUtils.getFullName(printed));
    }

    @Test
    public void CrashSiteMemorialPrintedPlaysAsTheCurrentCard() {
        var printed = GetScenario("1_45o").GetLSCard("card").getBlueprint();
        var bare = GetScenario("1_45").GetLSCard("card").getBlueprint();
        // Same game text code: the printed class only changes the text and the text-state information
        assertSame(Card1_045.class, printed.getClass().getSuperclass());
        assertSame(Card1_045.class, bare.getClass());
    }

    @Test
    public void CrashSiteMemorialBareCardIsUnchanged() {
        var bare = GetScenario("1_45").GetLSCard("card").getBlueprint();
        assertEquals(CURRENT_TEXT, bare.getGameText());
        assertNull(bare.getTextState());
        assertEquals("Crash Site Memorial", GameUtils.getFullName(bare));
    }
}
