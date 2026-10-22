package com.gempukku.swccgo.cards.set1283.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.3
 * Type: Location
 * Subtype: Site
 * Title: Rebel Cell - Refugee Quarter
 */
public class Card1283_024 extends AbstractSite {
    public Card1283_024() {
        super(Side.LIGHT, "Rebel Cell - Refugee Quarter", Uniqueness.DIAMOND_1, ExpansionSet.VIRTUAL_BLOCK_8_3, Rarity.V);
        setLocationLightSideGameText("While [Virtual Block 4] Hidden Base on table, your unique (*) aliens are deploy -1 (to a maximum of -1).");
        setLocationDarkSideGameText("Deploys only to same planet as Rebel Cell - Hidden Landing Site.");
        setGameText("Light: While [Virtual Block 4] Hidden Base on table, your unique (*) aliens are deploy -1 (to a maximum of -1). Dark: Deploys only to same planet as Rebel Cell - Hidden Landing Site.");
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.PLANET, Icon.EXTERIOR_SITE, Icon.UNDERGROUND);
    }

    @Override
    public boolean mayNotBePartOfSystem(SwccgGame game, String system) {
        return Filters.filterTopLocationsOnTable(game, Filters.and(Filters.titleContains("Rebel Cell - Hidden Landing Site"), Filters.partOfSystem(system))).isEmpty();
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostModifier(self,
                Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.unique, Filters.alien),
                new OnTableCondition(self, Filters.and(Filters.Hidden_Base, Icon.LEGACY_BLOCK_4)),
                -1));
        return modifiers;
    }
}
