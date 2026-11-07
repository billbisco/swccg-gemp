package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgBuiltInCardBlueprint;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;
import java.util.Collection;

/**
 * This effect result is triggered when a weapon (or permanent weapon) has been fired.
 */
public class FiredWeaponResult extends EffectResult {
    private PhysicalCard _weaponCard;
    private SwccgBuiltInCardBlueprint _permanentWeapon;
    private PhysicalCard _cardFiringWeapon;
    private boolean _thrown;
    private boolean _repeatedFiring;
    private Collection<PhysicalCard> _targets;
    private Collection<PhysicalCard> _targetsNotHit;

    /**
     * Creates an effect result that is triggered when a weapon (or permanent weapon) has been fired.
     * @param game the game
     * @param weaponCard the weapon card (or null if weapon was a permanent weapon)
     * @param permanentWeapon the permanent weapon (or null if weapon is not a permanent weapon)
     * @param cardFiringWeapon the card that fired the weapon (or null if the card is self-firing)
     */
    public FiredWeaponResult(SwccgGame game, PhysicalCard weaponCard, SwccgBuiltInCardBlueprint permanentWeapon, PhysicalCard cardFiringWeapon, boolean thrown, boolean repeatedFiring, Collection<PhysicalCard> targets) {
        super(Type.FIRED_WEAPON, (weaponCard != null ? weaponCard.getOwner() :
                (permanentWeapon != null ? permanentWeapon.getPhysicalCard(game).getOwner()
                        : (cardFiringWeapon != null ? cardFiringWeapon.getOwner() : null))));
        _weaponCard = weaponCard;
        _permanentWeapon = permanentWeapon;
        _cardFiringWeapon = cardFiringWeapon;
        _thrown = thrown;
        _repeatedFiring = repeatedFiring;
        _targets = targets;
    }

    /**
     * Creates an effect result that is triggered when a weapon (or permanent weapon) has been fired, including which
     * of its targets were not 'hit' (or made lost) by that weapon firing.
     * @param game the game
     * @param weaponCard the weapon card (or null if weapon was a permanent weapon)
     * @param permanentWeapon the permanent weapon (or null if weapon is not a permanent weapon)
     * @param cardFiringWeapon the card that fired the weapon (or null if the card is self-firing)
     * @param thrown true if the weapon was 'thrown'
     * @param repeatedFiring true if this was a repeated firing
     * @param targets the targets
     * @param targetsNotHit the targets that were not 'hit' (or made lost) by this weapon firing
     */
    public FiredWeaponResult(SwccgGame game, PhysicalCard weaponCard, SwccgBuiltInCardBlueprint permanentWeapon, PhysicalCard cardFiringWeapon, boolean thrown, boolean repeatedFiring, Collection<PhysicalCard> targets, Collection<PhysicalCard> targetsNotHit) {
        this(game, weaponCard, permanentWeapon, cardFiringWeapon, thrown, repeatedFiring, targets);
        _targetsNotHit = targetsNotHit;
    }

    public FiredWeaponResult(SwccgGame game, PhysicalCard weaponCard, SwccgBuiltInCardBlueprint permanentWeapon, PhysicalCard cardFiringWeapon, boolean thrown, Collection<PhysicalCard> targets) {
        this(game, weaponCard, permanentWeapon, cardFiringWeapon, thrown, false, targets);
    }

    /**
     * Gets the weapon card fired.
     * @return the weapon card, or null
     */
    public PhysicalCard getWeaponCardFired() {
        return _weaponCard;
    }

    /**
     * Gets the permanent weapon fired.
     * @return the permanent weapon built-in, or null
     */
    public SwccgBuiltInCardBlueprint getPermanentWeaponFired() {
        return _permanentWeapon;
    }

    /**
     * Gets the card that fired the weapon.
     * @return the card that fired the weapon, or null
     */
    public PhysicalCard getCardFiringWeapon() {
        return _cardFiringWeapon;
    }

    /**
     * True if the weapon was 'thrown'
     *
     * @return true or false
     */
    public boolean wasThrown() {
        return _thrown;
    }

    /**
     * True if the weapon was fired repeatedly
     *
     * @return true or false
     */
    public boolean wasRepeatedFiring() {
        return _repeatedFiring;
    }

    /**
     * Returns the cards targeted by the weapon
     *
     * @return the cards that were targeted by the weapon that was fired
     */
    public Collection<PhysicalCard> getTargets() {
        return _targets;
    }
    /**
     * Returns the targets of this weapon firing that were not 'hit' (or made lost) by it, i.e. the weapon 'failed to hit'
     * those targets. This is only known for weapon firings performed through the standard fire weapon action; otherwise
     * it is empty.
     * @return the targets not 'hit'
     */
    public Collection<PhysicalCard> getTargetsNotHit() {
        return _targetsNotHit != null ? _targetsNotHit : java.util.Collections.<PhysicalCard>emptyList();
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        String text = "Fired ";

        if (_thrown) {
            text = "Threw ";
        }

        if (_permanentWeapon != null) {
            return text + GameUtils.getCardLink(_permanentWeapon.getPhysicalCard(game));
        } else {
            return text + GameUtils.getCardLink(_weaponCard);
        }
    }
}
