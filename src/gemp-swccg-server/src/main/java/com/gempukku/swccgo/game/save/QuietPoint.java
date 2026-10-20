package com.gempukku.swccgo.game.save;

import com.gempukku.swccgo.game.state.actions.DefaultActionsEnvironment;
import com.gempukku.swccgo.logic.timing.DefaultSwccgGame;
import com.gempukku.swccgo.logic.timing.DefaultUserFeedback;

/**
 * A quiet point: one player is asked a top-level decision and nothing is being carried out. About a third of all
 * decisions are quiet points; the longest stretch between two (a long battle) is about 60 answers.
 */
public final class QuietPoint {
    private QuietPoint() {
    }

    public static boolean isQuiet(DefaultSwccgGame game, DefaultUserFeedback feedback) {
        return !game.isFinished()
                && feedback.getUsersPendingDecision().size() == 1
                && game.getActionsEnvironment() instanceof DefaultActionsEnvironment env
                && env.isActionStackEmpty()
                && game.getGameState().getPlayCardStates().isEmpty()
                && !game.getGameState().isDuringWeaponFiring();
    }
}
