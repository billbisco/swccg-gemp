package com.gempukku.swccgo.cards.set1114.dark;

import com.gempukku.swccgo.cards.AbstractAdmiralsOrder;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalBattleDestinyModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 14: Armed & Operational
 * Type: Admiral's Order
 * Title: Nothing Can Get Through Our Shield
 */
public class Card1114_029 extends AbstractAdmiralsOrder {
    public Card1114_029() {
        super(Side.DARK, "Nothing Can Get Through Our Shield", ExpansionSet.VIRTUAL_ORIGINAL_SET_14, Rarity.V);
        setVirtualSuffix(true);
        setGameText("Starships may not have their deploy costs reduced below their printed deploy costs. During battle, may place 3 Defensive Shields from under your Starting Effect out of play to make your starship immune to attrition this turn. At sites related to systems you occupy, your total battle destiny is +1.");
        addIcons(Icon.THEED_PALACE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalBattleDestinyModifier(self, Filters.site, 1, self.getOwner()));
        return modifiers;
    }
}
