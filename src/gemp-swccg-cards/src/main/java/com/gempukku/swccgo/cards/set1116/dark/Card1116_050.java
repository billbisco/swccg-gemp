package com.gempukku.swccgo.cards.set1116.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;

/**
 * Set: Virtual Set 16: Revenge Of The Sith
 * Type: Effect
 * Title: With Thunderous Applause
 */
public class Card1116_050 extends AbstractNormalEffect {
    public Card1116_050() {
        super(Side.DARK, 6, PlayCardZoneOption.ATTACHED, "With Thunderous Applause", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_16, Rarity.V);
        setGameText("Deploy on Sidious. If at Senate, at start of your turn may draw 2 cards from Reserve Deck. During your move phase, relocate this Effect to a Rebel or Jedi at a battleground; that character loses all immunity to attrition and is power and forfeit -2.");
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Sidious;
    }
}
