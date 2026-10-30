package com.gempukku.swccgo.cards.set1230.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeDisarmedModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeStolenModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 3
 * Type: Character
 * Subtype: Alien
 * Title: Hermi Odle
 */
public class Card1230_007 extends AbstractAlien {
    public Card1230_007() {
        super(Side.DARK, 3, 2, 3, 2, 3, Title.Hermi_Odle, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Baragwin assigned by Jabba to be the palace's master weaponsmith. Enemy of Pote Snitkin.");
        setGameText("Aliens at same and related sites may not have their weapons stolen or be Disarmed, and, while armed with a non-lightsaber, non-[Permanent Weapon] weapon: their weapon destiny draws are +1 and opponent's [Permanent Weapon] characters are power -1 there.");
        addIcons(Icon.WARRIOR, Icon.JABBAS_PALACE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter aliensHere = Filters.and(Filters.alien, Filters.atSameOrRelatedLocation(self));
        Filter nonLightsaberNonPermanentWeapon = Filters.and(Filters.weapon, Filters.not(Filters.lightsaber), Filters.not(Icon.PERMANENT_WEAPON));
        Filter theirWeapons = Filters.and(Filters.weapon, Filters.attachedTo(aliensHere));
        Condition armedWithSuchWeapon = new OnTableCondition(self, Filters.and(aliensHere, Filters.armedWith(nonLightsaberNonPermanentWeapon)));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotBeStolenModifier(self, theirWeapons));
        modifiers.add(new MayNotBeDisarmedModifier(self, aliensHere));
        modifiers.add(new EachWeaponDestinyModifier(self, nonLightsaberNonPermanentWeapon, aliensHere, 1));
        modifiers.add(new PowerModifier(self, Filters.and(Filters.opponents(self), Filters.character, Icon.PERMANENT_WEAPON, Filters.atSameOrRelatedLocation(self)), armedWithSuchWeapon, -1));
        return modifiers;
    }
}
