package com.gempukku.swccgo.filters;

import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.ModifierType;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * Filters for Legacy-only attributes (influence, later agendas/missions). Never add these to Filters.java.
 */
public class LegacyFilters {

    private LegacyFilters() {
    }

    /**
     * Filter that accepts cards whose current influence is greater than 0.
     */
    public static Filter hasInfluence() {
        return influenceMoreThan(0);
    }

    /**
     * Filter that accepts cards whose current influence is greater than n.
     */
    public static Filter influenceMoreThan(final float n) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return modifiersQuerying.getInfluence(gameState, physicalCard) > n;
            }
        };
    }

    /**
     * Filter that accepts systems that may not be 'liberated' (Geonosis 1291_15).
     */
    public static Filter mayNotBeLiberated() {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return !modifiersQuerying.getModifiersAffectingCard(gameState, ModifierType.MAY_NOT_BE_LIBERATED, physicalCard).isEmpty();
            }
        };
    }

    /**
     * Filter that accepts systems that can be 'liberated': a battleground system that is not already 'liberated' and
     * is not protected from it.
     */
    public static Filter canBeLiberated() {
        return Filters.and(Filters.battleground_system, Filters.except(Filters.liberated_system), Filters.except(mayNotBeLiberated()));
    }

    /**
     * Filter for the "T-47" a weapon's game text names (deploy on / used by a T-47): accepts T-47s, and cards of a model
     * type that the weapon may treat "T-47" as (AT-RT 1291_71: "'T-47' on [Hoth] weapons may be treated as 'AT-RT'").
     * @param weapon the weapon whose game text names "T-47"
     */
    public static Filter t47ForWeapon(final PhysicalCard weapon) {
        final int weaponPermId = weapon.getPermanentCardId();
        return Filters.or(Filters.T_47, new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                PhysicalCard weaponCard = gameState.findCardByPermanentId(weaponPermId);
                if (weaponCard == null) {
                    return false;
                }
                for (com.gempukku.swccgo.logic.modifiers.Modifier modifier : modifiersQuerying.getModifiersAffectingCard(gameState, ModifierType.T47_ON_WEAPONS_TREATED_AS_MODEL_TYPE, weaponCard)) {
                    if (modifier instanceof com.gempukku.swccgo.logic.modifiers.T47OnWeaponsTreatedAsModelTypeModifier
                            && physicalCard.getBlueprint().getModelTypes().contains(((com.gempukku.swccgo.logic.modifiers.T47OnWeaponsTreatedAsModelTypeModifier) modifier).getModelType())) {
                        return true;
                    }
                }
                return false;
            }
        });
    }

    /**
     * Filter that accepts cards that may be targeted by weapons like a vehicle.
     */
    public static Filter canBeTargetedByWeaponAsVehicle() {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return modifiersQuerying.canBeTargetedByWeaponsAsVehicle(gameState, physicalCard);
            }
        };
    }

    /**
     * Filter that accepts frigates (Nebulon-B, modified Nebulon-B, and Munificent-class).
     */
    public static final Filter frigate = Filters.or(
            hasModelType(com.gempukku.swccgo.common.ModelType.NEBULON_B_FRIGATE),
            hasModelType(com.gempukku.swccgo.common.ModelType.MODIFIED_NEBULON_B_FRIGATE),
            hasModelType(com.gempukku.swccgo.common.ModelType.MUNIFICENT_CLASS_STAR_FRIGATE));

    private static Filter hasModelType(final com.gempukku.swccgo.common.ModelType modelType) {
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                for (com.gempukku.swccgo.common.ModelType curModelType : physicalCard.getBlueprint().getModelTypes()) {
                    if (curModelType == modelType) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    /**
     * Filter that accepts systems that are 'aligned' (Rally To Our Cause 1291_28 records each system it aligned by
     * card id in its while-in-play data; the stacked card under the system marks it).
     */
    public static final Filter alignedSystem = new Filter() {
        @Override
        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
            if (physicalCard.getBlueprint().getCardSubtype() != com.gempukku.swccgo.common.CardSubtype.SYSTEM) {
                return false;
            }
            String id = String.valueOf(physicalCard.getCardId());
            for (PhysicalCard card : gameState.getAllPermanentCards()) {
                if (card.getZone() != null && card.getZone().isInPlay()
                        && "Rally To Our Cause".equals(card.getBlueprint().getTitle())
                        && card.getWhileInPlayData() != null
                        && card.getWhileInPlayData().getTextValues() != null
                        && card.getWhileInPlayData().getTextValues().contains(id)) {
                    return true;
                }
            }
            return false;
        }
    };
}
