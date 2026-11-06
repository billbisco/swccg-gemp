package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;

import java.util.Collection;

/**
 * This interface identifies the methods that an effect result triggered by losing, forfeiting, or canceling a card from table
 * must implement.
 */
public interface LostFromTableResult {

    /**
     * Gets the card lost, forfeited, or canceled from table.
     * @return the card
     */
    PhysicalCard getCard();

    /**
     * Gets the card the card was attached to when it was lost from table.
     * @return the card, or null if card was not attached to another card
     */
    PhysicalCard getFromAttachedTo();

    /**
     * Whether this card occupied its former host's pilot capacity slot when it left table.
     * This is captured for this result and does not change if the card later moves again.
     * @return true for a former pilot, false for passengers and unattached cards
     */
    boolean wasInPilotCapacitySlot();

    /**
     * Gets the location the card was lost from.
     * @return the location, or null if card was not lost from a location
     */
    PhysicalCard getFromLocation();

    /**
     * Gets the cards that the card was present with when lost.
     * @return the cards that the cards was present with
     */
    Collection<PhysicalCard> getWasPresentWith();
}
