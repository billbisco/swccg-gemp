package com.gempukku.swccgo.cards.set1291.dark;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.conditions.ControlsWithCondition;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 9.1
 * Type: Location
 * Subtype: System
 * Title: Geonosis
 */
public class Card1291_015 extends AbstractSystem {
    public Card1291_015() {
        super(Side.DARK, Title.Geonosis, 6, ExpansionSet.VIRTUAL_BLOCK_9_1, Rarity.V);
        setLocationDarkSideGameText("If you control with two [Separatist] starships or two [Trade Federation] starships (or one of each), Force drain +1 here.");
        setLocationLightSideGameText("Unless your [Clone Army] starship here, Force drain -1 here. May not be 'liberated.'");
        setGameText("Dark: If you control with two [Separatist] starships or two [Trade Federation] starships (or one of each), Force drain +1 here. Light: Unless your [Clone Army] starship here, Force drain -1 here. May not be 'liberated.'");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.PLANET, Icon.EPISODE_I);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self,
                new ControlsWithCondition(playerOnDarkSideOfLocation, self, 2,
                        Filters.and(Filters.starship, Filters.or(Icon.SEPARATIST, Icon.TRADE_FEDERATION))),
                1, playerOnDarkSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self,
                new UnlessCondition(new HereCondition(self, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.starship, Icon.CLONE_ARMY))),
                -1, playerOnLightSideOfLocation));
        return modifiers;
    }
}
