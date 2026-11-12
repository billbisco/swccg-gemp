package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.GameUtils;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Collision! text states (docs/legacy-versions.md, "Text states"): 1_74o as printed (Premiere, 1995) and the bare card
 * 1_74 unchanged. Texts from wiki.swccg.com [[Collision!]] and [[Collision! (Errata)]]. As printed the card knows only systems
 * (sectors came later); the errata added "or sector".
 */
public class Card_1_074_TextStates_Tests {
    private static final String PRINTED_TEXT = "Use 1 Force to play at a system where opponent has at least two starships. Draw destiny. If destiny < number of opponent's starships there, one is lost (opponent's choice).";
    private static final String CURRENT_TEXT = "Use 1 Force if opponent has at least two starships present at same system or sector. Draw destiny. If destiny < number of those starships, opponent must lose one of them.";

    private static VirtualTableScenario GetScenario(String id) {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("card", id);
                }},
                new HashMap<>() {{
                    put("tie1", "1_304");
                    put("tie2", "1_304");
                    put("bespin", "5_164");   // Bespin (system)
                    put("clouds", "5_174");   // Clouds (sector related to Bespin)
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
    public void CollisionPrintedHasThePrintedTextAndTextState() {
        var printed = GetScenario("1_74o").GetLSCard("card").getBlueprint();
        var bare = GetScenario("1_74").GetLSCard("card").getBlueprint();
        assertEquals(PRINTED_TEXT, printed.getGameText());
        assertEquals("1_74", printed.getTextState().currentId());
        assertEquals("Printed", printed.getTextState().label());
        assertEquals(LocalDate.of(1995, 12, 1), printed.getTextState().printedOn());
        assertEquals(bare.getTitle(), printed.getTitle());
        assertEquals(bare.getUniqueness(), printed.getUniqueness());
        assertEquals(bare.getSide(), printed.getSide());
        assertEquals(bare.getCardTypes(), printed.getCardTypes());
        assertEquals(bare.getCardSubtype(), printed.getCardSubtype());
        assertEquals(bare.getRarity(), printed.getRarity());
        assertEquals(bare.getLore(), printed.getLore());
        assertEquals(bare.getDestiny(), printed.getDestiny(), 0.01f);
        assertEquals("Collision! [Printed]", GameUtils.getFullName(printed));
    }

    @Test
    public void CollisionBareCardIsUnchanged() {
        var bare = GetScenario("1_74").GetLSCard("card").getBlueprint();
        assertEquals(CURRENT_TEXT, bare.getGameText());
        assertNull(bare.getTextState());
    }

    /** Whether Light Side may play Collision! with two Dark Side starships at the given location. */
    private static boolean playableWithTwoStarshipsAt(String id, String where) {
        var scn = GetScenario(id);
        scn.StartGame();
        scn.MoveLocationToTable(scn.GetDSCard("bespin"));
        scn.MoveLocationToTable(scn.GetDSCard("clouds"));
        var loc = scn.GetDSCard(where);
        scn.MoveCardsToLocation(loc, scn.GetDSCard("tie1"), scn.GetDSCard("tie2"));
        scn.MoveCardsToLSHand(scn.GetLSCard("card"));
        scn.EnsureLSForcePile(5);
        scn.SkipToLSTurn(Phase.DEPLOY);
        return scn.LSCardPlayAvailable(scn.GetLSCard("card"));
    }

    @Test
    public void CollisionPrintedPlaysOnlyAtASystem() {
        assertTrue(playableWithTwoStarshipsAt("1_74", "bespin"));
        assertTrue(playableWithTwoStarshipsAt("1_74", "clouds"));
        assertTrue(playableWithTwoStarshipsAt("1_74o", "bespin"));
        assertFalse(playableWithTwoStarshipsAt("1_74o", "clouds"));
    }
}
