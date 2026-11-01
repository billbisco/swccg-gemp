package com.gempukku.swccgo.cards.set1115.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.evaluators.HereEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalPowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 15: Galactic Frontier
 * Type: Location
 * Subtype: Site
 * Title: Rebel Cell - Situation Room
 */
public class Card1115_025 extends AbstractSite {
    public Card1115_025() {
        super(Side.LIGHT, "Rebel Cell - Situation Room", Uniqueness.DIAMOND_1, ExpansionSet.VIRTUAL_ORIGINAL_SET_15, Rarity.V);
        setLocationLightSideGameText("Deploys only to same planet system as Rebel Cell - Hidden Landing Site. For each of your characters here, your power is +1 at related system.");
        setLocationDarkSideGameText("For each of your characters here, your power is +1 at related system.");
        setGameText("Light: Deploys only to same planet system as Rebel Cell - Hidden Landing Site. For each of your characters here, your power is +1 at related system. Dark: For each of your characters here, your power is +1 at related system.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.INTERIOR_SITE, Icon.EXTERIOR_SITE, Icon.PLANET, Icon.A_NEW_HOPE);
    }

    @Override
    public boolean mayNotBePartOfSystem(SwccgGame game, String system) {
        return Filters.filterTopLocationsOnTable(game, Filters.and(Filters.titleContains("Rebel Cell - Hidden Landing Site"), Filters.partOfSystem(system))).isEmpty();
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalPowerModifier(self, Filters.relatedSystem(self),
                new HereEvaluator(self, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.character)),
                playerOnLightSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalPowerModifier(self, Filters.relatedSystem(self),
                new HereEvaluator(self, Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.character)),
                playerOnDarkSideOfLocation));
        return modifiers;
    }
}
