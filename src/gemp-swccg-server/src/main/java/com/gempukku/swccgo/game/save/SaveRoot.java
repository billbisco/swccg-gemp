package com.gempukku.swccgo.game.save;

import com.gempukku.swccgo.ai.SwccgAiController;
import com.gempukku.swccgo.logic.timing.DefaultSwccgGame;
import com.gempukku.swccgo.logic.timing.DefaultUserFeedback;

import java.util.Map;

/**
 * What a snapshot holds: the game, its pending decisions, and the bots (their memory is part of the game).
 * @param game the game
 * @param feedback the pending decisions
 * @param bots bot controllers by player id (empty for a game between people)
 */
public record SaveRoot(DefaultSwccgGame game, DefaultUserFeedback feedback, Map<String, SwccgAiController> bots) {
}
