package com.gempukku.swccgo.cards.set15;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.HashMap;
import static org.junit.Assert.*;

public class ReflectionsGoldBlastDoorComboTests {
    private VirtualTableScenario scenario() {
        var light = new HashMap<String, String>(); light.put("barrier", "1_105");
        var dark = new HashMap<String, String>(); dark.put("combo", "15_175"); dark.put("trooper", "1_194"); dark.put("control", "3_128");
        var scn = new VirtualTableScenario(light, dark);
        scn.StartGame();
        scn.MoveCardsToDSHand(scn.GetDSCard("combo"), scn.GetDSCard("trooper"), scn.GetDSCard("control"));
        scn.MoveCardsToLSHand(scn.GetLSCard("barrier"));
        scn.EnsureDSForcePile(4); scn.EnsureLSForcePile(2);
        return scn;
    }
    @Test public void BlastDoorControlsAndReactorTerminalDeploysForFreeAndRecyclesDuringControl() {
        var scn = scenario(); var combo = scn.GetDSCard("combo"); var card = scn.GetDSCard("trooper");
        scn.SkipToDSTurn(Phase.DEPLOY);
        int before = scn.GetDSForcePileCount();
        scn.DSPlayCardAndPassResponses(combo);
        assertEquals(before, scn.GetDSForcePileCount());
        assertTrue(combo.getZone().isInPlay());
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSFiller(1), scn.GetDSFiller(2));
        scn.LSPass();
        assertTrue(scn.DSCardPlayAvailable(scn.GetDSCard("control"), "Activate 2 Force"));
        assertFalse(scn.DSCardActionAvailable(combo, "Place cards"));
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.DSUseCardAction(combo, "Place cards");
        scn.PassAllResponses();
        scn.DSChooseCard(card);
        scn.PassAllResponses();
        assertEquals(card, scn.GetTopOfDSUsedPile());
    }
    @Test public void BlastDoorControlsAndReactorTerminalCancelsRebelBarrierBeingPlayed() {
        var scn = scenario(); var combo = scn.GetDSCard("combo"); var barrier = scn.GetLSCard("barrier");
        scn.MoveCardsToDSSideOfTable(combo);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSPlayCard(scn.GetDSCard("trooper"));
        scn.DSChooseCard(scn.GetDSStartingLocation());
        for (int i = 0; i < 8; i++) {
            if (scn.LSGetDecision() != null && scn.LSCardPlayAvailable(barrier)) break;
            if (scn.LSGetDecision() != null) scn.LSPass();
            else if (scn.DSGetDecision() != null) scn.DSPass();
        }
        assertTrue(scn.LSCardPlayAvailable(barrier));
        scn.LSPlayCard(barrier);
        scn.PassAllResponses();
        assertTrue(scn.GetLSLostPile().contains(barrier));
        assertFalse(scn.GetLSUsedPile().contains(barrier));
    }
}
