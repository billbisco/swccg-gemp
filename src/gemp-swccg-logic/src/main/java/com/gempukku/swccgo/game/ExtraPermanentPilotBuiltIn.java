package com.gempukku.swccgo.game;

import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * A synthetic permanent pilot granted to a starship or vehicle by another card.
 */
public class ExtraPermanentPilotBuiltIn implements SwccgBuiltInCardBlueprint {
    private Integer _permCardId;
    private int _builtInId;
    private final float _ability;

    public ExtraPermanentPilotBuiltIn(PhysicalCard card, float ability) {
        if (card != null) {
            _permCardId = card.getPermanentCardId();
        }
        _ability = ability;
    }

    @Override
    public void setPhysicalCard(PhysicalCard card) {
        if (card != null) {
            _permCardId = card.getPermanentCardId();
        }
    }

    @Override
    public PhysicalCard getPhysicalCard(SwccgGame game) {
        return game.getGameState().findCardByPermanentId(_permCardId);
    }

    @Override
    public void setBuiltInId(int builtInId) {
        _builtInId = builtInId;
    }

    @Override
    public int getBuiltInId() {
        return _builtInId;
    }

    @Override
    public Uniqueness getUniqueness() {
        return null;
    }

    @Override
    public String getTitle(SwccgGame game) {
        return "Permanent Pilot";
    }

    @Override
    public boolean hasKeyword(Keyword keyword) {
        return false;
    }

    @Override
    public boolean hasPersona(SwccgGame game, Persona persona) {
        return false;
    }

    @Override
    public Set<Persona> getPersonas(SwccgGame game) {
        return Collections.emptySet();
    }

    @Override
    public boolean isWeapon() {
        return false;
    }

    @Override
    public boolean isPilot() {
        return true;
    }

    @Override
    public boolean isAstromech() {
        return false;
    }

    @Override
    public float getAbility() {
        return _ability;
    }

    @Override
    public List<Modifier> getGameTextModifiers(PhysicalCard self) {
        return null;
    }

    @Override
    public List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
        return null;
    }
}
