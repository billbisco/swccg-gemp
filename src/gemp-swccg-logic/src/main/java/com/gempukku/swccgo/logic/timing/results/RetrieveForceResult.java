package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The result triggered after a unit of Force has been retrieved.
 */
public class RetrieveForceResult extends EffectResult {
    private PhysicalCard _source;
    private PhysicalCard _mostRecentCardRetrieved;
    private int _amount;
    private float _initialAmountToRetrieve = -1;

    /**
     * Creates a result to trigger after a unit of Force has been retrieved by the player.
     * @param source the source card
     * @param playerId the player
     * @param amount the amount of Force retrieved so far as part of this Force retrieval
     * @param mostRecentCardRetrieved the most recent card retrieved
     */
    public RetrieveForceResult(PhysicalCard source, String playerId, int amount, PhysicalCard mostRecentCardRetrieved) {
        super(Type.RETRIEVED_FORCE, playerId);
        _source = source;
        _amount = amount;
        _mostRecentCardRetrieved = mostRecentCardRetrieved;
    }

    /**
     * Creates a result to trigger after a unit of Force has been retrieved by the player.
     * @param source the source card
     * @param playerId the player
     * @param amount the amount of Force retrieved so far as part of this Force retrieval
     * @param mostRecentCardRetrieved the most recent card retrieved
     * @param initialAmountToRetrieve the amount of Force the source said to retrieve (before modifiers)
     */
    public RetrieveForceResult(PhysicalCard source, String playerId, int amount, PhysicalCard mostRecentCardRetrieved, float initialAmountToRetrieve) {
        this(source, playerId, amount, mostRecentCardRetrieved);
        _initialAmountToRetrieve = initialAmountToRetrieve;
    }

    /**
     * Gets the amount of Force the source said to retrieve (before modifiers), or -1 if unknown.
     * @return the initial amount to retrieve
     */
    public float getInitialAmountToRetrieve() {
        return _initialAmountToRetrieve;
    }

    /**
     * Gets the source card of the action.
     * @return the source card
     */
    public PhysicalCard getSourceCard() {
        return _source;
    }

    /**
     * Gets the amount of Force retrieved so far.
     * @return the amount of Force
     */
    public int getAmountOfForceRetrieved() {
        return _amount;
    }

    /*
     * Gets the most recently retrieved card
     * @return the most recently retrieved card
     */
    public PhysicalCard getMostRecentCardRetrieved() {
        return _mostRecentCardRetrieved;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Just retrieved " + getAmountOfForceRetrieved() + " Force";
    }
}
