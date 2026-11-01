package com.gempukku.swccgo.cards.set1116.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Persona;
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
 * Title: Nute Gunray's Bounty
 */
public class Card1116_047 extends AbstractNormalEffect {
    public Card1116_047() {
        super(Side.DARK, 4, PlayCardZoneOption.ATTACHED, "Nute Gunray's Bounty", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_16, Rarity.V);
        setGameText("Deploy on Amidala or opponent's senator. During your move phase, unless a Jedi here, your alien bounty hunter present may capture and seize that character. During your control phase, if that character is escorted by an alien, opponent loses 1 Force.");
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.or(Filters.persona(Persona.AMIDALA), Filters.and(Filters.opponents(self), Filters.senator));
    }
}
