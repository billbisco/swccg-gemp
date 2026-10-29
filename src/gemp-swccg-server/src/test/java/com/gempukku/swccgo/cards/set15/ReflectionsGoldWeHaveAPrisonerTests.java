package com.gempukku.swccgo.cards.set15;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

public class ReflectionsGoldWeHaveAPrisonerTests {
    @Test
    public void WeHaveAPrisonerCapturesForfeitedCharacterInsteadOfPuttingItInLostPile() {
        var light = new HashMap<String, String>();
        light.put("luke", "1_19");
        var dark = new HashMap<String, String>();
        dark.put("vader", "1_168");
        dark.put("prisoner", "15_303");
        var scn = new VirtualTableScenario(light, dark);
        var luke = scn.GetLSCard("luke");
        var vader = scn.GetDSCard("vader");
        var prisoner = scn.GetDSCard("prisoner");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), vader, luke);
        scn.MoveCardsToDSHand(prisoner);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.EnsureDSForcePile(3);
        scn.PrepareDSDestiny(6);
        scn.PrepareLSDestiny(0);
        scn.DSInitiateBattle(scn.GetDSStartingLocation());
        scn.SkipToDamageSegment(false);

        boolean played = false;
        int forceBefore = -1;
        for (int i = 0; i < 24 && !played; i++) {
            if (scn.DSGetDecision() != null && scn.DSGetADParam("cardId") != null
                    && scn.DSCardPlayAvailable(prisoner)) {
                forceBefore = scn.GetDSForcePileCount();
                scn.DSPlayCard(prisoner);
                played = true;
            } else if (scn.LSGetDecision() != null
                    && (scn.AwaitingLSBattleDamagePayment() || scn.AwaitingLSAttritionPayment())
                    && scn.LSHasCardChoiceAvailable(luke)) {
                scn.LSChooseCard(luke);
            } else if (scn.LSGetDecision() != null && scn.LSDecisionAvailable("Optional")) {
                scn.LSPass();
            } else if (scn.DSGetDecision() != null && scn.DSDecisionAvailable("Optional")) {
                scn.DSPass();
            } else {
                fail("Expected forfeiture or its capture-response window: " + scn.GetCurrentDecision());
            }
        }
        assertTrue("Capture must be played from the real forfeiture response", played);
        for (int i = 0; i < 24 && !luke.isCaptive(); i++) {
            if (scn.DSCaptureDecisionAvailable()) {
                scn.DSChooseSeizeCaptive();
            } else if (scn.DSHasCardChoiceAvailable(vader)) {
                scn.DSChooseCard(vader);
            } else if (scn.DSHasCardChoiceAvailable(luke)) {
                scn.DSChooseCard(luke);
            } else if (scn.LSGetDecision() != null && scn.LSDecisionAvailable("Optional")) {
                scn.LSPass();
            } else if (scn.DSGetDecision() != null && scn.DSDecisionAvailable("Optional")) {
                scn.DSPass();
            } else {
                fail("Expected capture choice or response: " + scn.GetCurrentDecision());
            }
        }
        scn.PassAllResponses();
        assertTrue(luke.isCaptive());
        assertEquals(vader, luke.getAttachedTo());
        assertFalse(scn.GetLSLostPile().contains(luke));
        assertEquals(forceBefore - 1, scn.GetDSForcePileCount());
        assertTrue(scn.GetDSLostPile().contains(prisoner));
    }
}
