package com.gempukku.swccgo.game.save;

/**
 * The move log no longer replays on this build (a card in the game changed behavior). The game can't continue.
 */
public class GameSaveIncompatibleException extends Exception {
    private final int _step;

    public GameSaveIncompatibleException(int step, String message) {
        super("Replay went a different way at answer " + step + ": " + message);
        _step = step;
    }

    /** The answer (0-based) where the replay diverged. */
    public int getStep() {
        return _step;
    }
}
