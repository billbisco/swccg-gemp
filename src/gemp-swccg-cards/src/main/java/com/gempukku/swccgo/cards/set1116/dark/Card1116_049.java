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
import com.gempukku.swccgo.logic.modifiers.DestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Set 16: Revenge Of The Sith
 * Type: Effect
 * Title: Strategic Reserves (V)
 */
public class Card1116_049 extends AbstractNormalEffect {
    public Card1116_049() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Strategic Reserves", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_16, Rarity.V);
        setVirtualSuffix(true);
        setGameText("Deploy on table. Your clones and Coruscant guards are destiny +1 when drawn for weapon or battle destiny. Once per turn, may cancel a Force drain by placing here from hand a clone or Coruscant guard. Characters may deploy aboard your starships for free.");
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return Collections.<Modifier>singletonList(new DestinyModifier(self, Filters.or(Filters.clone, Filters.titleContains("Coruscant Guard")), 1));
    }
}
