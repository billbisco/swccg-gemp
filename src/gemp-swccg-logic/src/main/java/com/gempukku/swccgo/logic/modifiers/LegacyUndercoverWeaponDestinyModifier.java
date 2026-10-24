package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgBuiltInCardBlueprint;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.util.Collection;

/**
 * Undercover (V): opponent's [Permanent Weapon] destinies at same and related sites are -1 (-2 if targeting this character).
 */
public class LegacyUndercoverWeaponDestinyModifier extends AbstractModifier {
    private final int _permSourceCardId;

    public LegacyUndercoverWeaponDestinyModifier(PhysicalCard source) {
        super(source, null, null, null, ModifierType.EACH_WEAPON_DESTINY, false);
        _permSourceCardId = source.getPermanentCardId();
    }

    @Override
    public float getWeaponDestinyModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard weaponUser,
                                          PhysicalCard weaponCard, SwccgBuiltInCardBlueprint permanentWeapon, Collection<PhysicalCard> weaponTargets) {
        PhysicalCard source = gameState.findCardByPermanentId(_permSourceCardId);
        if (source == null) {
            return 0;
        }
        PhysicalCard host = source.getAttachedTo();
        PhysicalCard site = null;
        if (host != null) {
            site = host.getAtLocation();
            if (site == null) {
                site = modifiersQuerying.getLocationHere(gameState, host);
            }
        }
        if (site == null) {
            site = modifiersQuerying.getLocationHere(gameState, source);
        }
        if (site == null) {
            return 0;
        }
        Filter sameOrRelated = Filters.or(Filters.sameCardId(site), Filters.relatedSite(site));
        PhysicalCard firer = weaponUser;
        if (firer == null && permanentWeapon != null) {
            firer = permanentWeapon.getPhysicalCard(gameState.getGame());
        }
        if (firer == null) {
            firer = weaponCard;
        }
        if (firer == null || source.getOwner().equals(firer.getOwner())) {
            return 0;
        }
        PhysicalCard firerLoc = modifiersQuerying.getLocationHere(gameState, firer);
        if (firerLoc == null || !sameOrRelated.accepts(gameState, modifiersQuerying, firerLoc)) {
            return 0;
        }
        boolean isPermanentWeapon = permanentWeapon != null
                || (weaponUser != null && Filters.hasPermanentWeapon(Filters.any).accepts(gameState, modifiersQuerying, weaponUser))
                || (weaponCard != null && Filters.hasPermanentWeapon(Filters.any).accepts(gameState, modifiersQuerying, weaponCard));
        if (!isPermanentWeapon) {
            return 0;
        }
        Collection<PhysicalCard> targets = weaponTargets;
        if ((targets == null || targets.isEmpty()) && gameState.getWeaponFiringState() != null) {
            targets = gameState.getWeaponFiringState().getTargets();
        }
        if (host != null && targets != null) {
            for (PhysicalCard target : targets) {
                if (target != null
                        && (target.getCardId() == host.getCardId()
                        || Filters.sameCardId(host).accepts(gameState, modifiersQuerying, target))) {
                    return -2;
                }
            }
        }
        return -1;
    }
}
