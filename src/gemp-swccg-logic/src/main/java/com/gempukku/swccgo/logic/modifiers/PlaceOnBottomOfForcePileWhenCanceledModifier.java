package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier that has the source card placed on the bottom of its owner's Force Pile (instead of Lost Pile) when canceled
 * (All My Urchins & Cloud City Celebration 1283_20: "Place Effect on bottom of Force Pile if canceled.").
 * Read by the cancel effects through {@link #isPlacedOnBottomOfForcePileWhenCanceled}.
 */
public class PlaceOnBottomOfForcePileWhenCanceledModifier extends AbstractModifier {

    /**
     * Creates a modifier that has the source card placed on the bottom of its owner's Force Pile when canceled.
     * @param source the source of the modifier (and the card affected)
     */
    public PlaceOnBottomOfForcePileWhenCanceledModifier(PhysicalCard source) {
        super(source, null, source, null, ModifierType.LEGACY__PLACE_ON_BOTTOM_OF_FORCE_PILE_WHEN_CANCELED, true);
    }

    /**
     * Determines if the card is placed on the bottom of its owner's Force Pile when canceled.
     * @param gameState the game state
     * @param modifiersQuerying the modifiers querying
     * @param card the card being canceled
     * @return true or false
     */
    public static boolean isPlacedOnBottomOfForcePileWhenCanceled(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card) {
        return !modifiersQuerying.getModifiersAffectingCard(gameState, ModifierType.LEGACY__PLACE_ON_BOTTOM_OF_FORCE_PILE_WHEN_CANCELED, card).isEmpty();
    }
}
