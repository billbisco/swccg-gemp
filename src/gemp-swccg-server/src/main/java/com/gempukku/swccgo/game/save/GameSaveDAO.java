package com.gempukku.swccgo.game.save;

import java.util.List;

/**
 * Server-side storage of saved games. Saves never come from the client.
 */
public interface GameSaveDAO {
    /** Inserts or replaces the save of a game. */
    void save(String gameId, String owner, GameSave save);

    /** @return the save, or null */
    GameSave load(String gameId);

    /** @return the owner of a saved game, or null */
    String getOwner(String gameId);

    List<String> getGameIds(String owner);

    void delete(String gameId);

    /** Moves every save of one owner to another (a guest who signs up keeps their games). */
    void changeOwner(String fromOwner, String toOwner);

    /**
     * Deletes every save whose last activity is at or before the cutoff.
     * @return how many were deleted
     */
    int deleteLastPlayedAtOrBefore(long cutoffMillis);
}
