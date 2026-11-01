package com.gempukku.swccgo.cards.set1116.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.LimitForceLossFromForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Set 16: Revenge Of The Sith
 * Type: Effect
 * Title: Resistance
 */
public class Card1116_063 extends AbstractNormalEffect {
    public Card1116_063() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Resistance", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_16, Rarity.V);
        setGameText("Deploy on table. While you occupy at least 3 battlegrounds or opponent occupies no battlegrounds, you lose no more than 2 Force from each Force drain or 'insert' card.");
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return Collections.<Modifier>singletonList(new LimitForceLossFromForceDrainModifier(self, Filters.location, 2, self.getOwner()));
    }
}
