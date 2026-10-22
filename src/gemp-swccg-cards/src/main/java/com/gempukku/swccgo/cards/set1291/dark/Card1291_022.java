package com.gempukku.swccgo.cards.set1291.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
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
public class Card1291_022 extends AbstractSite {
    public Card1291_022() {
        super(Side.DARK, "Muunilinst: City Of Harnaidan", Card1291_020.SYSTEM_NAME, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_9_1, Rarity.V);
        setLocationDarkSideGameText("Your combat vehicles here are power +1 and may move to and from here for free.");
        setLocationLightSideGameText("Unless your combat vehicles or [Clone Army] card here, Force drain -1 here.");
        setGameText("Dark: Your combat vehicles here are power +1 and may move to and from here for free  Light: Unless your combat vehicles or [Clone Army] card here, Force drain -1 here.");
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcon(Icon.DARK_FORCE, 2);
        addIcons(Icon.EPISODE_I, Icon.PLANET, Icon.EXTERIOR_SITE, Icon.INTERIOR_SITE);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.combat_vehicle, Filters.here(self)), 1));
        modifiers.add(new MovesFreeToLocationModifier(self, Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.combat_vehicle), self));
        modifiers.add(new MovesFreeFromLocationModifier(self, Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.combat_vehicle), self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self,
                new UnlessCondition(new HereCondition(self, Filters.and(Filters.your(playerOnLightSideOfLocation),
                        Filters.or(Filters.combat_vehicle, Icon.CLONE_ARMY)))),
                -1, playerOnLightSideOfLocation));
        return modifiers;
    }
}
