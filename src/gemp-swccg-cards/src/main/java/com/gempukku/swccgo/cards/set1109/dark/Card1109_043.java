package com.gempukku.swccgo.cards.set1109.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
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
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 9
 * Type: Character
 * Subtype: Imperial
 * Title: Major Hewex
 */
public class Card1109_043 extends AbstractImperial {
    public Card1109_043() {
        super(Side.DARK, 2, 2, 2, 2, 4, Title.Hewex, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_9, Rarity.V);
        setVirtualSuffix(true);
        setLore("Leader of Navy detachment assigned to guard the Endor control bunker. Liaison between Imperial technicians and command staff.");
        setGameText("Your unique (•) Stormtroopers are deploy -1 to same site and are forfeit +1 at same and related locations.");
        addIcons(Icon.WARRIOR, Icon.ENDOR);
        addKeywords(Keyword.LEADER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter yourUniqueStormtroopers = Filters.and(Filters.your(self), Filters.unique, Filters.stormtrooper);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, yourUniqueStormtroopers, -1, Filters.sameSite(self)));
        modifiers.add(new ForfeitModifier(self, Filters.and(yourUniqueStormtroopers, Filters.atSameOrRelatedLocation(self)), 1));
        return modifiers;
    }
}
