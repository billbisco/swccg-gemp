package com.gempukku.swccgo.cards.set1291.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
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
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MovesFreeFromLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MovesFreeToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 9.1
 * Type: Location
 * Subtype: Site
 * Title: Muunilinst: City Of Harnaidan
 */
public class Card1291_061 extends AbstractSite {
    public Card1291_061() {
        super(Side.LIGHT, "Muunilinst: City Of Harnaidan", Title.Muunilinst, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_9_1, Rarity.V);
        setLocationLightSideGameText("Your combat vehicles here are power +1 and add 1 to each of their weapon destiny draws.");
        setLocationDarkSideGameText("Unless opponent controls this site, [Presence Droid] and [Separatist] cards may move to and from here for free.");
        setGameText("Light: Your combat vehicles here are power +1 and add 1 to each of their weapon destiny draws. Dark: Unless opponent controls this site, [Presence Droid] and [Separatist] cards may move to and from here for free.");
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.EPISODE_I, Icon.PLANET, Icon.EXTERIOR_SITE);
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        Filter yourCombatVehiclesHere = Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.combat_vehicle, Filters.here(self));
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, yourCombatVehiclesHere, 1));
        modifiers.add(new EachWeaponDestinyModifier(self, Filters.any, yourCombatVehiclesHere, 1));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        // GEMP has no Presence Droid icon (E9); every card that prints it also has the Separatist icon.
        Condition unlessOpponentControls = new UnlessCondition(new ControlsCondition(game.getOpponent(playerOnDarkSideOfLocation), self));
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MovesFreeToLocationModifier(self, Icon.SEPARATIST, unlessOpponentControls, self));
        modifiers.add(new MovesFreeFromLocationModifier(self, Icon.SEPARATIST, unlessOpponentControls, self));
        return modifiers;
    }
}
