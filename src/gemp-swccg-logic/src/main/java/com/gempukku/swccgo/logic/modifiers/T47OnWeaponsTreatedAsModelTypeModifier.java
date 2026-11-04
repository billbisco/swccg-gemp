package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.game.PhysicalCard;

/**
 * A modifier that lets the affected weapons treat "T-47" in their game text as another model type (AT-RT 1291_71:
 * "'T-47' on [Hoth] weapons may be treated as 'AT-RT'"). Read by LegacyFilters.t47ForWeapon.
 */
public class T47OnWeaponsTreatedAsModelTypeModifier extends AbstractModifier {
    private final ModelType _modelType;

    /**
     * Creates a modifier that lets the weapons accepted by the filter treat "T-47" as the specified model type.
     * @param source the source of the modifier
     * @param weaponFilter the weapons affected
     * @param modelType the model type "T-47" may be treated as
     */
    public T47OnWeaponsTreatedAsModelTypeModifier(PhysicalCard source, Filterable weaponFilter, ModelType modelType) {
        super(source, "'T-47' may be treated as '" + modelType.getHumanReadable() + "'", weaponFilter, null, ModifierType.T47_ON_WEAPONS_TREATED_AS_MODEL_TYPE, true);
        _modelType = modelType;
    }

    public ModelType getModelType() {
        return _modelType;
    }
}
