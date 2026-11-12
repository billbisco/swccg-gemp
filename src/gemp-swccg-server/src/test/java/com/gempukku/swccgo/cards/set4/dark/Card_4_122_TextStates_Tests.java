package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Flagship text states (docs/legacy-versions.md, "Text states"): as printed (4_122o, Dagobah, April 1997) your other
 * starships may react only to the same system; Decipher's Glossary Version 2.0 (4_122d) and the current card (4_122)
 * add sectors. Texts are checked in FollowUpTextStatesTests.
 */
public class Card_4_122_TextStates_Tests {

    /** Whether Flagship on a Star Destroyer at Bespin or at Clouds lets your other starships react to there. */
    private static boolean reactsTo(String id, boolean sector) {
        var scn = new VirtualTableScenario(new HashMap<>(Map.of("filler", "1_28")),
                new HashMap<>(Map.of("flagship", id, "isd", "1_302", "bespin", "5_164", "clouds", "5_174")), 10, 10,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, "anything_goes");
        scn.StartGame();
        var where = scn.GetDSCard(sector ? "clouds" : "bespin");
        scn.MoveLocationToTable(scn.GetDSCard("bespin"));
        scn.MoveLocationToTable(scn.GetDSCard("clouds"));
        scn.MoveCardsToLocation(where, scn.GetDSCard("isd"));
        scn.AttachCardsTo(scn.GetDSCard("isd"), scn.GetDSCard("flagship"));
        var flagship = scn.GetDSCard("flagship");
        return ((Card4_122) flagship.getBlueprint()).getReactLocation(flagship).accepts(scn.game(), where);
    }

    @Test
    public void FlagshipPrintedReactsOnlyToTheSameSystem() {
        assertTrue(reactsTo("4_122", false));
        assertTrue(reactsTo("4_122", true));
        assertTrue(reactsTo("4_122d", true));
        assertTrue(reactsTo("4_122o", false));
        assertFalse(reactsTo("4_122o", true));
    }
}
