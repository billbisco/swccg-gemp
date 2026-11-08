package com.gempukku.swccgo.cards.set2271.light;

import com.gempukku.swccgo.common.LegacyVersion;
import java.time.LocalDate;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.set1271.LegacySetupSearchEffect;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;

/** Luke, Trust Me with the "no Epic Events" errata (PC-E1, by 2012-07-21), a version of 1271_39; replaced by the PC card-list rephrase (PC-E2, by 2012-07-21; both dates are upper bounds from the same capture). Same play as the current card. Anything Goes only (docs/legacy-versions.md). */
public class Card2271_1039 extends LegacySetupSearchEffect {
    public Card2271_1039() {
        super(Side.LIGHT, "Luke, Trust Me", GameTextActionId.LEGACY__LUKE_TRUST_ME__TAKE_SETUP,
            GameTextActionId.LEGACY__LUKE_TRUST_ME__TAKE_IMMEDIATE_EFFECT);
        setLore("Luke's piloting skills and Force abilities made his X-wing a difficult target for Darth Vader as they raced down the Death Star trench.");
        setGameText("If you have no Epic Events on table (and did not play an Epic Event at the start of the game), deploy on table. Once per game, may [upload] a [Setup] card. May place this Effect in Used Pile to [upload] an Immediate Effect. May place this Effect in Lost Pile to cancel a Force drain. [Immune to Alter.]");
        setLegacyVersion(new LegacyVersion("1271_39", "PC-E1",
                "PC website card list (by 2012-07-21); also Virtual Cards Master List (2013-04-27, stale reprint)", LocalDate.of(2012, 7, 21), LocalDate.of(2012, 7, 21)));
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self,
            PlayCardOptionId playCardOptionId, boolean asReact) {
        return !game.getGameState().hasPlayedStartingEpicEvent(playerId)
            && !GameConditions.canSpot(game, self, Filters.and(Filters.your(self), Filters.Epic_Event));
    }
}
