package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;

/**
 * This effect result is triggered when a card is lost from table.
 */
public class LostCardFromTableResult extends EffectResult implements LostFromTableResult {
    private PhysicalCard _card;
    private boolean _wasInPilotCapacitySlot;
    private PhysicalCard _lostFromAttachedTo;
    private PhysicalCard _lostFromLocation;
    private Collection<PhysicalCard> _wasPresentWith;
    private PhysicalCard _sourceCard;

    /**
     * Creates an effect result that is triggered when a card is lost from table.
     * @param action the action performing this effect result
     * @param card the card
     * @param lostFromAttachedTo the card that the card was attached to when lost, or null
     * @param lostFromLocation the location the card was lost from, or null
     * @param wasPresentWith the cards that the card was present with when lost
     */
    public LostCardFromTableResult(Action action, PhysicalCard card, PhysicalCard lostFromAttachedTo, PhysicalCard lostFromLocation, Collection<PhysicalCard> wasPresentWith) {
        this(action, action.getPerformingPlayer(), card, lostFromAttachedTo, lostFromLocation, wasPresentWith);
    }

    /**
     * Creates an effect result that is triggered when a card is place is lost from table.
     * @param action the action performing this effect result
     * @param performingPlayerId the performing player
     * @param card the card
     * @param lostFromAttachedTo the card that the card was attached to when lost, or null
     * @param lostFromLocation the location the card was lost from, or null
     * @param wasPresentWith the cards that the card was present with when lost
     */
    public LostCardFromTableResult(Action action, String performingPlayerId, PhysicalCard card, PhysicalCard lostFromAttachedTo, PhysicalCard lostFromLocation, Collection<PhysicalCard> wasPresentWith) {
        super(EffectResult.Type.LOST_FROM_TABLE, performingPlayerId);
        _card = card;
        _wasInPilotCapacitySlot = lostFromAttachedTo != null && card.wasPreviouslyInPilotCapacitySlot();
        _lostFromAttachedTo = lostFromAttachedTo;
        _lostFromLocation = lostFromLocation;
        _wasPresentWith = wasPresentWith;
        _sourceCard = action != null ? action.getActionSource() : null;
    }

    /**
     * Gets the card that is the source of the action that lost the card, or null.
     * @return the source card
     */
    public PhysicalCard getSourceCard() {
        return _sourceCard;
    }

    /**
     * Gets the card lost from table.
     * @return the card
     */
    @Override
    public PhysicalCard getCard() {
        return _card;
    }

    /**
     * Gets the card the card was attached to when it was lost from table.
     * @return the card, or null if card was not attached to another card
     */
    @Override
    public PhysicalCard getFromAttachedTo() {
        return _lostFromAttachedTo;
    }

    @Override
    public boolean wasInPilotCapacitySlot() {
        return _wasInPilotCapacitySlot;
    }

    /**
     * Gets the location the card was lost from.
     * @return the location, or null if card was not lost from a location
     */
    @Override
    public PhysicalCard getFromLocation() {
        return _lostFromLocation;
    }

    /**
     * Gets the cards that the card was present with when lost.
     * @return the cards that the cards was present with
     */
    @Override
    public Collection<PhysicalCard> getWasPresentWith() {
        return _wasPresentWith;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Just lost " + GameUtils.getCardLink(_card);
    }
}
