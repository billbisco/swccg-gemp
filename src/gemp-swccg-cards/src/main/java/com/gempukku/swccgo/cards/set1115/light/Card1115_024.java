package com.gempukku.swccgo.cards.set1115.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.FiresForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitedToUsedPileModifier;
import com.gempukku.swccgo.logic.modifiers.IsPoweredModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 15: Galactic Frontier
 * Type: Location
 * Subtype: Site
 * Title: Rebel Cell - Perimeter
 */
public class Card1115_024 extends AbstractSite {
    public Card1115_024() {
        super(Side.LIGHT, "Rebel Cell - Perimeter", Uniqueness.DIAMOND_1, ExpansionSet.VIRTUAL_ORIGINAL_SET_15, Rarity.V);
        setLocationLightSideGameText("Your [Hoth] artillery weapons at Rebel Cell sites are powered, forfeit +2, fire for free, and are placed in Used Pile when forfeited.");
        setLocationDarkSideGameText("Deploys only to same planet system as Rebel Cell - Hidden Landing Site.");
        setGameText("Light: Your [Hoth] artillery weapons at Rebel Cell sites are powered, forfeit +2, fire for free, and are placed in Used Pile when forfeited. Dark: Deploys only to same planet system as Rebel Cell - Hidden Landing Site.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.EXTERIOR_SITE, Icon.PLANET, Icon.SPECIAL_EDITION);
    }

    @Override
    public boolean mayNotBePartOfSystem(SwccgGame game, String system) {
        return Filters.filterTopLocationsOnTable(game, Filters.and(Filters.titleContains("Rebel Cell - Hidden Landing Site"), Filters.partOfSystem(system))).isEmpty();
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        Filter hothArtilleryAtRebelCell = Filters.and(Filters.your(playerOnLightSideOfLocation), Icon.HOTH, Filters.artillery_weapon,
                Filters.at(Filters.titleContains("Rebel Cell")));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new IsPoweredModifier(self, hothArtilleryAtRebelCell));
        modifiers.add(new ForfeitModifier(self, hothArtilleryAtRebelCell, 2));
        modifiers.add(new FiresForFreeModifier(self, hothArtilleryAtRebelCell));
        modifiers.add(new ForfeitedToUsedPileModifier(self, hothArtilleryAtRebelCell));
        return modifiers;
    }
}
