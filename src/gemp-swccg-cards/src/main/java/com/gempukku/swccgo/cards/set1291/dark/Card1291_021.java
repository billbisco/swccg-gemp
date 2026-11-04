package com.gempukku.swccgo.cards.set1291.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.ControlsWithCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 9.1
 * Type: Location
 * Subtype: Site
 * Title: Muunilinst: Banking Clan Headquarters
 */
public class Card1291_021 extends AbstractSite {
    public Card1291_021() {
        super(Side.DARK, "Muunilinst: Banking Clan Headquarters", Title.Muunilinst, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_9_1, Rarity.V);
        setLocationDarkSideGameText("If you control with a [Separatist] card, unique (•) [Separatist] cards deploy -1 here.");
        setLocationLightSideGameText("Your combat vehicles and [Clone Army] cards are power +1 here.");
        setGameText("Dark: If you control with a [Separatist] card, unique (•) [Separatist] cards deploy -1 here. Light: Your combat vehicles and [Clone Army] cards are power +1 here.");
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcon(Icon.DARK_FORCE, 2);
        addIcons(Icon.EPISODE_I, Icon.PLANET, Icon.EXTERIOR_SITE);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.unique, Icon.SEPARATIST),
                new ControlsWithCondition(playerOnDarkSideOfLocation, self, Icon.SEPARATIST), -1, self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, Filters.and(Filters.your(playerOnLightSideOfLocation),
                Filters.or(Filters.combat_vehicle, Icon.CLONE_ARMY), Filters.here(self)), 1));
        return modifiers;
    }
}
