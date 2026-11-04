package com.gempukku.swccgo.cards.set1291.light;

import com.gempukku.swccgo.cards.AbstractSite;
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
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 9.1
 * Type: Location
 * Subtype: Site
 * Title: Muunilinst: Harnaidan Plains
 */
public class Card1291_063 extends AbstractSite {
    public Card1291_063() {
        super(Side.LIGHT, "Muunilinst: Harnaidan Plains", Title.Muunilinst, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_9_1, Rarity.V);
        setLocationDarkSideGameText("Battle droids and [Separatist] cards are forfeit -1 and power +1 here.");
        setGameText("Light: - Dark: Battle droids and [Separatist] cards are forfeit -1 and power +1 here.");
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.EPISODE_I, Icon.PLANET, Icon.EXTERIOR_SITE);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        Filter droidsAndSeparatistsHere = Filters.and(Filters.or(Filters.battle_droid, Icon.SEPARATIST), Filters.here(self));
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForfeitModifier(self, droidsAndSeparatistsHere, -1));
        modifiers.add(new PowerModifier(self, droidsAndSeparatistsHere, 1));
        return modifiers;
    }
}
