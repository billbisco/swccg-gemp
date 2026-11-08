package com.gempukku.swccgo.cards.set2271.light;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.set1271.LegacySetupSearchEffect;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;

import java.time.LocalDate;

/**
 * Luke, Trust Me (Virtual Block 7.1).
 * Pre-errata version of 1271_39 (Virtual Block 7.1 release sheet text, Jun 2010), replaced by the Virtual Cards Master List text (no Epic Events on table and none played at the start of the game). Anything Goes only (docs/legacy-versions.md).
 */
public class Card2271_039 extends LegacySetupSearchEffect {
    public Card2271_039() {
        super(Side.LIGHT, "Luke, Trust Me", GameTextActionId.LEGACY__LUKE_TRUST_ME__TAKE_SETUP,
            GameTextActionId.LEGACY__LUKE_TRUST_ME__TAKE_IMMEDIATE_EFFECT);
        setLore("Luke's piloting skills and Force abilities made his X-wing a difficult target for Darth Vader as they raced down the Death Star trench.");
        setGameText("Unless your Epic Event on table, deploy on table. Once per game, may take a [Setup] card into hand from Reserve Deck; reshuffle. May place this Effect in Used Pile to take an Immediate Effect into hand from Reserve Deck; reshuffle. May place this Effect in Lost Pile to cancel a Force drain. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1271_39", "",
                "Virtual Block 7.1 release sheet (2010-06-18)", LocalDate.of(2010, 6, 18), LocalDate.of(2013, 4, 27)));
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self,
            PlayCardOptionId playCardOptionId, boolean asReact) {
        // Original text: "Unless your Epic Event on table" (an Epic Event played at the start of the game and since
        // gone does not stop it; the Master List added that).
        return !GameConditions.canSpot(game, self, Filters.and(Filters.your(self), Filters.Epic_Event));
    }
}
