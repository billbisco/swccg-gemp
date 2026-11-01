package com.gempukku.swccgo.cards.set1116.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
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
 * Title: An Enemy Of The Republic
 */
public class Card1116_040 extends AbstractNormalEffect {
    public Card1116_040() {
        super(Side.DARK, 6, PlayCardZoneOption.ATTACHED, "An Enemy Of The Republic", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_16, Rarity.V);
        setGameText("Deploy on Sidious. While at Senate, if a battle was just initiated anywhere, opponent must relocate this card to their participating non-droid character; during battle here, your destiny draws may not be canceled. If that character is about to be captured, lost, or forfeit, relocate this card to Sidious.");
        addIcons(Icon.EPISODE_I);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Sidious;
    }
}
