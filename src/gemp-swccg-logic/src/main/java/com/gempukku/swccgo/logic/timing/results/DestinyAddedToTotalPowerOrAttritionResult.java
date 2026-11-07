package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The result triggered after a card just added a destiny to a player's total power and/or attrition (for example,
 * "adds one destiny to total power"). Cards that add a destiny with their own effects can emit it with
 * TriggeringResultEffect; the battle engine emits it for destiny draws to power only or attrition only.
 */
public class DestinyAddedToTotalPowerOrAttritionResult extends EffectResult {
    private final PhysicalCard _sourceCard;
    private final PhysicalCard _location;
    private final float _destiny;
    private final boolean _addedToTotalPower;
    private final boolean _addedToAttrition;

    /**
     * Creates a result to trigger after a card just added a destiny to total power and/or attrition.
     * @param sourceCard the card that added the destiny
     * @param playerId the player whose total power or attrition the destiny was added to
     * @param location the location (e.g. the battle location), or null
     * @param destiny the destiny value added
     * @param addedToTotalPower true if added to total power
     * @param addedToAttrition true if added to attrition
     */
    public DestinyAddedToTotalPowerOrAttritionResult(PhysicalCard sourceCard, String playerId, PhysicalCard location, float destiny, boolean addedToTotalPower, boolean addedToAttrition) {
        super(Type.DESTINY_ADDED_TO_TOTAL_POWER_OR_ATTRITION, playerId);
        _sourceCard = sourceCard;
        _location = location;
        _destiny = destiny;
        _addedToTotalPower = addedToTotalPower;
        _addedToAttrition = addedToAttrition;
    }

    public PhysicalCard getSourceCard() {
        return _sourceCard;
    }

    public PhysicalCard getLocation() {
        return _location;
    }

    public float getDestiny() {
        return _destiny;
    }

    public boolean isAddedToTotalPower() {
        return _addedToTotalPower;
    }

    public boolean isAddedToAttrition() {
        return _addedToAttrition;
    }

    @Override
    public String getText(SwccgGame game) {
        return "Destiny just added to " + (_addedToTotalPower ? "total power" : "") + (_addedToTotalPower && _addedToAttrition ? " and " : "")
                + (_addedToAttrition ? "attrition" : "") + (_sourceCard != null ? " by " + GameUtils.getFullName(_sourceCard) : "");
    }
}
