package com.gempukku.swccgo.cards.set1271.light;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.set1271.LegacySetupSearchEffect;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;

/** Luke, Trust Me, final Virtual Block 7.1 errata. */
public class Card1271_039 extends LegacySetupSearchEffect {
    public Card1271_039() {
        super(Side.LIGHT, "Luke, Trust Me", GameTextActionId.LEGACY__LUKE_TRUST_ME__TAKE_SETUP,
            GameTextActionId.LEGACY__LUKE_TRUST_ME__TAKE_IMMEDIATE_EFFECT);
        setLore("Luke's piloting skills and Force abilities made his X-wing a difficult target for Darth Vader as they raced down the Death Star trench.");
        setGameText("If you have no Epic Events on table (and did not play an Epic Even at the start of the game), deploy on table. Once per game, may take a [Setup] card into hand from Reserve Deck; reshuffle. May place this Effect in Used Pile to take an Immediate Effect into hand from Reserve Deck; reshuffle. May place this Effect in Lost Pile to cancel a Force drain. (Immune to Alter.)");
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self,
            PlayCardOptionId playCardOptionId, boolean asReact) {
        return !game.getGameState().hasPlayedStartingEpicEvent(playerId)
            && !GameConditions.canSpot(game, self, Filters.and(Filters.your(self), Filters.Epic_Event));
    }
}
