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
 * Out Of Nowhere text states (docs/legacy-versions.md, "Text states"): 1_102o as printed (Premiere, 1995) and the bare card
 * 1_102 unchanged. Texts from wiki.swccg.com [[Out Of Nowhere]] and [[Out Of Nowhere (Errata)]]. As printed the card knows only systems
 * (sectors came later); the errata added "or sector".
 */
public class Card_1_102_TextStates_Tests {
    private static final String PRINTED_TEXT = "If opponent has just initiated a battle at a system where you have a starship with maneuver > 3, use 1 Force to add one battle destiny.";
    private static final String CURRENT_TEXT = "If opponent just initiated a battle at a system or sector where you have a starship with maneuver > 3, use 1 Force to add one battle destiny.";

    private static VirtualTableScenario GetScenario(String id) {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("ship", "1_146");
                    put("card", id);
                    put("bespin", "5_076");   // Bespin (system)
                    put("clouds", "5_077");   // Bespin: Cloud City (sector related to Bespin; no maneuver penalty, unlike Clouds)
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
    public void OutOfNowherePrintedHasThePrintedTextAndTextState() {
        var printed = GetScenario("1_102o").GetLSCard("card").getBlueprint();
        var bare = GetScenario("1_102").GetLSCard("card").getBlueprint();
        assertEquals(PRINTED_TEXT, printed.getGameText());
        assertEquals("1_102", printed.getTextState().currentId());
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
        assertEquals("Out Of Nowhere [Printed]", GameUtils.getFullName(printed));
    }

    @Test
    public void OutOfNowhereBareCardIsUnchanged() {
        var bare = GetScenario("1_102").GetLSCard("card").getBlueprint();
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
    public void OutOfNowherePrintedPlaysOnlyInABattleAtASystem() {
        assertTrue(playableInBattleAt("1_102", "bespin"));
        assertTrue(playableInBattleAt("1_102", "clouds"));
        assertTrue(playableInBattleAt("1_102o", "bespin"));
        assertFalse(playableInBattleAt("1_102o", "clouds"));
    }
}
