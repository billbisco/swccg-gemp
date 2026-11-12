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
 * Don't Get Cocky text states (docs/legacy-versions.md, "Text states"): 1_76o as printed (Premiere, 1995) and the bare card
 * 1_76 unchanged. Texts from wiki.swccg.com [[Don't Get Cocky]] and [[Don't Get Cocky (Errata)]]. As printed the card knows only systems
 * (sectors came later); the errata added "or sector".
 */
public class Card_1_076_TextStates_Tests {
    private static final String PRINTED_TEXT = "If Luke and Han are in a battle together, you may add two battle destiny. OR If the opponent just initiated a battle at any system, destroy (lose) one TIE Fighter of your choice at that system.";
    private static final String CURRENT_TEXT = "If Luke and Han are in a battle together, you may add two battle destiny. OR If opponent just initiated a battle at a system or sector, choose one TIE/ln present to be lost.";

    private static VirtualTableScenario GetScenario(String id) {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("ship", "1_146");
                    put("card", id);
                    put("bespin", "5_076");   // Bespin (system)
                    put("clouds", "5_085");   // Clouds (sector related to Bespin)
                }},
                new HashMap<>() {{
                    put("ship", "1_304");
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
    public void DontGetCockyPrintedHasThePrintedTextAndTextState() {
        var printed = GetScenario("1_76o").GetLSCard("card").getBlueprint();
        var bare = GetScenario("1_76").GetLSCard("card").getBlueprint();
        assertEquals(PRINTED_TEXT, printed.getGameText());
        assertEquals("1_76", printed.getTextState().currentId());
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
        assertEquals("Don't Get Cocky [Printed]", GameUtils.getFullName(printed));
    }

    @Test
    public void DontGetCockyBareCardIsUnchanged() {
        var bare = GetScenario("1_76").GetLSCard("card").getBlueprint();
        assertEquals(CURRENT_TEXT, bare.getGameText());
        assertNull(bare.getTextState());
    }

    /** Dark Side initiates a battle at the given location, one starfighter on each side; is the LS Interrupt playable? */
    private static boolean playableInBattleAt(String id, String where) {
        var scn = GetScenario(id);
        scn.StartGame();
        scn.MoveLocationToTable(scn.GetLSCard("bespin"));
        scn.MoveLocationToTable(scn.GetLSCard("clouds"));
        var loc = scn.GetLSCard(where);
        scn.MoveCardsToLocation(loc, scn.GetLSCard("ship"), scn.GetDSCard("ship"));
        scn.MoveCardsToLSHand(scn.GetLSCard("card"));
        scn.EnsureLSForcePile(5);
        scn.EnsureDSForcePile(5);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(loc);
        for (int i = 0; i < 4; i++) {
            if (scn.LSGetDecision() != null && scn.LSCardPlayAvailable(scn.GetLSCard("card")))
                return true;
            if (scn.DSGetDecision() != null)
                scn.DSPass();
            else if (scn.LSGetDecision() != null)
                scn.LSPass();
        }
        return false;
    }

    @Test
    public void DontGetCockyPrintedPlaysOnlyInABattleAtASystem() {
        assertTrue(playableInBattleAt("1_76", "bespin"));
        assertTrue(playableInBattleAt("1_76", "clouds"));
        assertTrue(playableInBattleAt("1_76o", "bespin"));
        assertFalse(playableInBattleAt("1_76o", "clouds"));
    }
}
