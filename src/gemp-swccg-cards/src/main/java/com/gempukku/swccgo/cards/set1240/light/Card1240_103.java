package com.gempukku.swccgo.cards.set1240.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.modifiers.ForceDrainsMayNotBeModifiedByModifier;
import com.gempukku.swccgo.logic.modifiers.MayFireOnlyOncePerBattleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 4
 * Type: Location
 * Subtype: Site
 * Title: Rebel Cell - Monitoring Station
 */
public class Card1240_103 extends AbstractSite {
    public Card1240_103() {
        super(Side.LIGHT, "Rebel Cell - Monitoring Station", Uniqueness.DIAMOND_1, ExpansionSet.VIRTUAL_BLOCK_4, Rarity.V);
        setLocationLightSideGameText("Deploys only to same planet system as Rebel Cell - Hidden Landing Site. While you occupy, your Force drains may not be modified by opponent's objective.");
        setLocationDarkSideGameText("Unless you control, each of your weapons at Rebel Cell sites may fire only once per battle.");
        setGameText("Light: Deploys only to same planet system as Rebel Cell - Hidden Landing Site. While you occupy, your Force drains may not be modified by opponent's objective. Dark: Unless you control, each of your weapons at Rebel Cell sites may fire only once per battle.");
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
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainsMayNotBeModifiedByModifier(self, Filters.and(Filters.opponents(playerOnLightSideOfLocation), Filters.Objective),
                new OccupiesCondition(playerOnLightSideOfLocation, self), playerOnLightSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayFireOnlyOncePerBattleModifier(self,
                Filters.and(Filters.your(playerOnDarkSideOfLocation),
                        Filters.or(Filters.weapon, Filters.hasPermanentWeapon),
                        Filters.at(Filters.titleContains("Rebel Cell"))),
                new UnlessCondition(new ControlsCondition(playerOnDarkSideOfLocation, self))));
        return modifiers;
    }
}
