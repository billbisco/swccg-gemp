package com.gempukku.swccgo.cards.set1291.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.ControlsWithCondition;
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
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ShuttlesFreeFromLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ShuttlesFreeToLocationModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 9.1
 * Type: Location
 * Subtype: Site
 * Title: Muunilinst: Republic Landing Site
 */
public class Card1291_064 extends AbstractSite {
    public Card1291_064() {
        super(Side.LIGHT, "Muunilinst: Republic Landing Site", Title.Muunilinst, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_9_1, Rarity.V);
        setLocationLightSideGameText("Your [Clone Army] characters and [Clone Army] combat vehicles may shuttle to and from this site for free.");
        setLocationDarkSideGameText("Unless you control with a [Separatist] card, Force drain -1 here.");
        setGameText("Light: Your [Clone Army] characters and [Clone Army] combat vehicles may shuttle to and from this site for free. Dark: Unless you control with a [Separatist] card, Force drain -1 here.");
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.EPISODE_I, Icon.PLANET, Icon.EXTERIOR_SITE);
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        Filter yourCloneArmy = Filters.and(Filters.your(playerOnLightSideOfLocation), Icon.CLONE_ARMY,
                Filters.or(Filters.character, Filters.combat_vehicle));
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ShuttlesFreeToLocationModifier(self, yourCloneArmy, self));
        modifiers.add(new ShuttlesFreeFromLocationModifier(self, yourCloneArmy, self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self,
                new UnlessCondition(new ControlsWithCondition(playerOnDarkSideOfLocation, self, Icon.SEPARATIST)),
                -1, playerOnDarkSideOfLocation));
        return modifiers;
    }
}
