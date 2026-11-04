package com.gempukku.swccgo.cards.set1282.light;

import com.gempukku.swccgo.framework.VirtualTableScenario;

/**
 * Test helpers shared by the Virtual Block 8.2 Light tests.
 */
final class LegacyVb82TestSupport {
    private LegacyVb82TestSupport() {
    }

    /** Passes responses (either player) until the Light Side player is offered the card action, or nothing is pending. */
    static boolean passUntilLSCardAction(VirtualTableScenario scn, com.gempukku.swccgo.game.PhysicalCardImpl card, String text) {
        for (int i = 0; i < 20; i++) {
            if (scn.LSAnyDecisionsAvailable() && scn.LSGetDecision().getText().contains("action") && scn.LSCardActionAvailable(card, text)) {
                return true;
            }
            if (scn.LSAnyDecisionsAvailable() && scn.LSGetDecision().getText().contains("Optional responses") && scn.LSCardActionAvailable(card, text)) {
                return true;
            }
            if (scn.DSAnyDecisionsAvailable()) {
                scn.DSPass();
            }
            else if (scn.LSAnyDecisionsAvailable() && scn.LSGetDecision().getText().contains("Optional responses")) {
                scn.LSPass();
            }
            else {
                return false;
            }
        }
        return false;
    }

    /** Pays the Light Side player's pending Force loss from Reserve Deck, one Force at a time. */
    static void lsPayForceLoss(VirtualTableScenario scn) {
        for (int i = 0; i < 10; i++) {
            if (scn.AwaitingLSForceLossPayment()) {
                scn.LSPayForceLossFromReserveDeck();
            }
            else if (scn.DSAnyDecisionsAvailable() && scn.DSGetDecision().getText().contains("Optional responses")) {
                scn.DSPass();
            }
            else if (scn.LSAnyDecisionsAvailable() && scn.LSGetDecision().getText().contains("Optional responses")) {
                scn.LSPass();
            }
            else {
                return;
            }
        }
    }
}
