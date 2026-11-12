package com.gempukku.swccgo.cards.set1.dark;

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
 * Lone Pilot text states (docs/legacy-versions.md, "Text states"): 1_257o as printed (Premiere, 1995) and the bare card
 * 1_257 unchanged. Texts from wiki.swccg.com [[Lone Pilot]] and [[Lone Pilot (Errata)]]. As printed the card knows only systems
 * (sectors came later); the errata added "or sector".
 */
public class Card_1_257_TextStates_Tests {
    private static final String PRINTED_TEXT = "If any pilot (or permanent pilot) is defending a battle alone at a system, add one battle destiny. OR If Motti is defending a battle alone at a system, add two battle destiny.";
    private static final String CURRENT_TEXT = "If your pilot (or permanent pilot) is defending a battle alone at a system or sector, add one battle destiny. OR If Motti is defending a battle alone at a system or sector, add two battle destiny.";

    private static VirtualTableScenario GetScenario(String id) {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("ship", "1_146");
                }},
                new HashMap<>() {{
                    put("ship", "1_304");
                    put("card", id);
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
    public void LonePilotPrintedHasThePrintedTextAndTextState() {
        var printed = GetScenario("1_257o").GetDSCard("card").getBlueprint();
        var bare = GetScenario("1_257").GetDSCard("card").getBlueprint();
        assertEquals(PRINTED_TEXT, printed.getGameText());
        assertEquals("1_257", printed.getTextState().currentId());
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
        assertEquals("Lone Pilot [Printed]", GameUtils.getFullName(printed));
    }

    @Test
    public void LonePilotBareCardIsUnchanged() {
        var bare = GetScenario("1_257").GetDSCard("card").getBlueprint();
        assertEquals(CURRENT_TEXT, bare.getGameText());
        assertNull(bare.getTextState());
    }

    /** Light Side initiates a battle at the given location, one starfighter on each side; is the DS Interrupt playable? */
    private static boolean playableInBattleAt(String id, String where) {
        var scn = GetScenario(id);
        scn.StartGame();
        scn.MoveLocationToTable(scn.GetDSCard("bespin"));
        scn.MoveLocationToTable(scn.GetDSCard("clouds"));
        var loc = scn.GetDSCard(where);
        scn.MoveCardsToLocation(loc, scn.GetLSCard("ship"), scn.GetDSCard("ship"));
        scn.MoveCardsToDSHand(scn.GetDSCard("card"));
        scn.EnsureLSForcePile(5);
        scn.EnsureDSForcePile(5);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(loc);
        for (int i = 0; i < 4; i++) {
            if (scn.DSGetDecision() != null && scn.DSCardPlayAvailable(scn.GetDSCard("card")))
                return true;
            if (scn.LSGetDecision() != null)
                scn.LSPass();
            else if (scn.DSGetDecision() != null)
                scn.DSPass();
        }
        return false;
    }

    @Test
    public void LonePilotPrintedPlaysOnlyInABattleAtASystem() {
        assertTrue(playableInBattleAt("1_257", "bespin"));
        assertTrue(playableInBattleAt("1_257", "clouds"));
        assertTrue(playableInBattleAt("1_257o", "bespin"));
        assertFalse(playableInBattleAt("1_257o", "clouds"));
    }
}
