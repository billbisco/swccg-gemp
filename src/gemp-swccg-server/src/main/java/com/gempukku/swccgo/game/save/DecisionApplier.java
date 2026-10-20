package com.gempukku.swccgo.game.save;

import com.gempukku.swccgo.logic.decisions.AwaitingDecision;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.timing.DefaultSwccgGame;
import com.gempukku.swccgo.logic.timing.DefaultUserFeedback;

/**
 * Applies one answer the way a headless game does: an invalid answer re-asks the same decision. Recording and
 * replay both go through here so they behave the same.
 */
public final class DecisionApplier {
    private DecisionApplier() {
    }

    /**
     * Applies an answer and runs the game to its next decision.
     * @return true if the answer was accepted
     */
    public static boolean apply(DefaultSwccgGame game, DefaultUserFeedback feedback, String player, String answer) {
        AwaitingDecision decision = feedback.getAwaitingDecision(player);
        if (decision == null) {
            throw new IllegalStateException("No decision pending for " + player);
        }
        feedback.participantDecided(player);
        boolean accepted = true;
        try {
            decision.decisionMade(answer);
        } catch (DecisionResultInvalidException e) {
            feedback.sendAwaitingDecision(player, decision);
            accepted = false;
        }
        game.carryOutPendingActionsUntilDecisionNeeded();
        return accepted;
    }
}
