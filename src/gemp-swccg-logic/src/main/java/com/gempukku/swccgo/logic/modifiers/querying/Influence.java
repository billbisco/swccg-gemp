package com.gempukku.swccgo.logic.modifiers.querying;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.InfluenceModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierCollector;
import com.gempukku.swccgo.logic.modifiers.ModifierCollectorImpl;
import com.gempukku.swccgo.logic.modifiers.ModifierType;
import com.gempukku.swccgo.logic.modifiers.ResetInfluenceModifier;
import com.gempukku.swccgo.logic.modifiers.TotalInfluenceAtLocationModifier;

import java.util.Collection;

public interface Influence extends BaseQuery {

    /**
     * Gets a card's current influence.
     */
    default float getInfluence(GameState gameState, PhysicalCard physicalCard) {
        return getInfluence(gameState, physicalCard, new ModifierCollectorImpl());
    }

    /**
     * Gets a card's current influence. Printed value (doubled if the character is doubled) plus modifiers;
     * lowest unmodifiable reset wins; floor 0.
     */
    default float getInfluence(GameState gameState, PhysicalCard physicalCard, ModifierCollector modifierCollector) {
        float result = physicalCard.getBlueprint().getInfluence();

        if (physicalCard.getBlueprint().getCardCategory() == CardCategory.CHARACTER
                && isDoubled(gameState, physicalCard, modifierCollector)) {
            result *= 2;
        }

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.INFLUENCE, physicalCard)) {
            if (modifier instanceof InfluenceModifier) {
                result += ((InfluenceModifier) modifier).getAmount(gameState, query(), physicalCard);
            }
            modifierCollector.addModifier(modifier);
        }

        Float lowestResetValue = null;
        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.UNMODIFIABLE_INFLUENCE, physicalCard)) {
            if (modifier instanceof ResetInfluenceModifier) {
                float modifierAmount = ((ResetInfluenceModifier) modifier).getAmount(gameState, query(), physicalCard);
                lowestResetValue = (lowestResetValue != null) ? Math.min(lowestResetValue, modifierAmount) : modifierAmount;
            }
            modifierCollector.addModifier(modifier);
        }
        if (lowestResetValue != null) {
            result = lowestResetValue;
        }

        return Math.max(0, result);
    }

    /**
     * True when current influence is greater than 0.
     */
    default boolean hasInfluence(GameState gameState, PhysicalCard card) {
        return getInfluence(gameState, card) > 0;
    }

    /**
     * Sum of that player's characters' current influence at the location, plus modifiers to that total. Floor 0.
     */
    default float getTotalInfluenceAtLocation(GameState gameState, String playerId, PhysicalCard location) {
        float result = 0;
        Collection<PhysicalCard> characters = Filters.filterActive(gameState.getGame(), null,
                Filters.and(Filters.owner(playerId), Filters.character, Filters.at(location)));
        for (PhysicalCard character : characters) {
            result += getInfluence(gameState, character);
        }

        for (Modifier modifier : getModifiersAffectingCard(gameState, ModifierType.TOTAL_INFLUENCE_AT_LOCATION, location)) {
            if (modifier instanceof TotalInfluenceAtLocationModifier) {
                result += ((TotalInfluenceAtLocationModifier) modifier).getAmount(playerId, gameState, query(), location);
            }
        }

        return Math.max(0, result);
    }

    /**
     * Highest printed influence among characters at the location. Ignores modifiers.
     */
    default float getHighestPrintedInfluenceAtLocation(GameState gameState, PhysicalCard location) {
        float highest = 0;
        Collection<PhysicalCard> characters = Filters.filterActive(gameState.getGame(), null,
                Filters.and(Filters.character, Filters.at(location)));
        for (PhysicalCard character : characters) {
            highest = Math.max(highest, character.getBlueprint().getInfluence());
        }
        return highest;
    }
}
