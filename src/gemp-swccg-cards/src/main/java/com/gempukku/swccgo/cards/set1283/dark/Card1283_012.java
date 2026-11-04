package com.gempukku.swccgo.cards.set1283.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.DockingBayTransitFromCostModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToCardMatchingFilterModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.3
 * Type: Location
 * Subtype: Site
 * Title: Spaceport Docking Bay (V)
 */
public class Card1283_012 extends AbstractSite {
    public Card1283_012() {
        super(Side.DARK, "Spaceport Docking Bay", Uniqueness.DIAMOND_1, ExpansionSet.VIRTUAL_BLOCK_8_3, Rarity.V);
        setVirtualSuffix(true);
        setLocationDarkSideGameText("Your docking bay transit from here requires 2 Force. Your starfighters deploy -1 (or -2 if freighter) here. Immune to [Reflections III] Ounee Ta.");
        setLocationLightSideGameText("May not be deployed to Bespin, Dagobah, Endor, Hoth or Yavin 4. Your docking bay transit from here requires 3 Force.");
        setGameText("Dark: Your docking bay transit from here requires 2 Force. Your starfighters deploy -1 (or -2 if freighter) here. Immune to [Reflections III] Ounee Ta. Light: May not be deployed to Bespin, Dagobah, Endor, Hoth or Yavin 4. Your docking bay transit from here requires 3 Force.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.EXTERIOR_SITE, Icon.PLANET, Icon.SCOMP_LINK);
        addKeywords(Keyword.SPACEPORT_SITE, Keyword.DOCKING_BAY);
        addMayNotBePartOfSystem(Title.Bespin, Title.Dagobah, Title.Endor, Title.Hoth, Title.Yavin_4);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        Filter yourStarfighters = Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.or(Filters.starfighter, Filters.deploysLikeStarfighter));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DockingBayTransitFromCostModifier(self, 2, playerOnDarkSideOfLocation));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(yourStarfighters, Filters.not(Filters.freighter)), -1, self, true));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(yourStarfighters, Filters.freighter), -2, self, true));
        modifiers.add(new ImmuneToCardMatchingFilterModifier(self, self, Filters.and(Filters.Ounee_Ta, Icon.REFLECTIONS_III)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DockingBayTransitFromCostModifier(self, 3, playerOnLightSideOfLocation));
        return modifiers;
    }
}
