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
 * Emergency Deployment text states (docs/legacy-versions.md, "Text states"): 1_244o as printed (Premiere, 1995) and the bare card
 * 1_244 unchanged. Texts from wiki.swccg.com [[Emergency Deployment]] and [[Emergency Deployment (PC Errata)]]. The printed text plays as the current card
 * ("Draw up to 3 cards from your Reserve Deck" (to deploy from) is the current card's "reveal up to 3 cards"), so the printed state reuses the current card's game text code.
 */
public class Card_1_244_TextStates_Tests {
    private static final String PRINTED_TEXT = "If opponent just initiated a battle where opponent has more than double your power, draw up to 3 cards from your Reserve Deck. Of those 3, deploy anywhere (for free) any characters, starships, vehicles, devices or weapons. Any others are lost.";
    private static final String CURRENT_TEXT = "If opponent just initiated a battle where opponent has more than double your power, reveal up to 3 cards from your Reserve Deck. Of those 3, deploy anywhere (for free) any characters, starships, vehicles, devices or weapons. Any others are lost.";

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
    public void EmergencyDeploymentPrintedHasThePrintedTextAndTextState() {
        var scn = GetScenario("1_244o");
        var printed = scn.GetDSCard("card").getBlueprint();
        var bare = GetScenario("1_244").GetDSCard("card").getBlueprint();
        assertEquals(PRINTED_TEXT, printed.getGameText());
        TextState state = printed.getTextState();
        assertEquals("1_244", state.currentId());
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
        assertEquals("Emergency Deployment [Printed]", GameUtils.getFullName(printed));
    }

    @Test
    public void EmergencyDeploymentPrintedPlaysAsTheCurrentCard() {
        var printed = GetScenario("1_244o").GetDSCard("card").getBlueprint();
        var bare = GetScenario("1_244").GetDSCard("card").getBlueprint();
        // Same game text code: the printed class only changes the text and the text-state information
        assertSame(Card1_244.class, printed.getClass().getSuperclass());
        assertSame(Card1_244.class, bare.getClass());
    }

    @Test
    public void EmergencyDeploymentBareCardIsUnchanged() {
        var bare = GetScenario("1_244").GetDSCard("card").getBlueprint();
        assertEquals(CURRENT_TEXT, bare.getGameText());
        assertNull(bare.getTextState());
        assertEquals("Emergency Deployment", GameUtils.getFullName(bare));
    }
}
