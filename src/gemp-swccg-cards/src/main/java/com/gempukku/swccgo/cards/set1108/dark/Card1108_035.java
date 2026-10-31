package com.gempukku.swccgo.cards.set1108.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
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
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeDisarmedModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeStolenModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 8
 * Type: Character
 * Subtype: Alien
 * Title: Hermi Odle (V)
 */
public class Card1108_035 extends AbstractAlien {
    public Card1108_035() {
        super(Side.DARK, 3, 2, 3, 2, 3, Title.Hermi_Odle, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_8, Rarity.V);
        setVirtualSuffix(true);
        setLore("Baragwin assigned by Jabba to be the palace's master weaponsmith. Enemy of Pote Snitkin.");
        setGameText("Characters at same and related sites may not have their weapons stolen or be targeted to be Disarmed. Your non-lightsaber, non-[Permanent Weapon] character weapon destiny draws are +2.");
        addIcons(Icon.WARRIOR, Icon.JABBAS_PALACE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter charactersHere = Filters.and(Filters.character, Filters.atSameOrRelatedLocation(self));
        Filter theirWeapons = Filters.and(Filters.weapon, Filters.attachedTo(charactersHere));
        Filter yourCharacterWeapons = Filters.and(Filters.your(self), Filters.character_weapon, Filters.not(Filters.lightsaber), Filters.not(Icon.PERMANENT_WEAPON));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotBeStolenModifier(self, theirWeapons));
        modifiers.add(new MayNotBeDisarmedModifier(self, charactersHere));
        modifiers.add(new EachWeaponDestinyModifier(self, yourCharacterWeapons, 2));
        return modifiers;
    }
}
