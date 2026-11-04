package com.gempukku.swccgo.cards.set1291.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 9.1
 * Type: Location
 * Subtype: Site
 * Title: Muunilinst: Separatist Command Center
 */
public class Card1291_024 extends AbstractSite {
    public Card1291_024() {
        super(Side.DARK, "Muunilinst: Separatist Command Center", Title.Muunilinst, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_9_1, Rarity.V);
        setLocationDarkSideGameText("Battle droids and [Separatist] cards are power +1 here.");
        setGameText("Dark: Battle droids and [Separatist] cards are power +1 here. Light: -");
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcon(Icon.DARK_FORCE, 2);
        addIcons(Icon.EPISODE_I, Icon.PLANET, Icon.EXTERIOR_SITE, Icon.SCOMP_LINK);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, Filters.and(Filters.or(Filters.battle_droid, Icon.SEPARATIST), Filters.here(self)), 1));
        return modifiers;
    }
}
