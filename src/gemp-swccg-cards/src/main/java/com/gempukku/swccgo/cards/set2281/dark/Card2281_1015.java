package com.gempukku.swccgo.cards.set2281.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.CardsInHandFewerThanCondition;
import com.gempukku.swccgo.cards.set1281.dark.Card1281_015;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ImmuneToDeployCostModifiersToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotHaveForfeitValueReducedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.1
 * Type: Effect
 * Title: Well Guarded (V)
 * PC-E1 version of 1281_15: the PC website card list text (by 2012-07-21): "Non-unique aliens are immune" (both players'). The Virtual Cards Master List
 * (2013-04-27, PC-E2) repeats the release-sheet words and is the current card. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2281_1015 extends Card1281_015 {
    public Card2281_1015() {
        super();
        setGameText("Deploy on table. Non-unique aliens are immune to Grimtaash (While you have < 13 cards in hand) , The Camp, and deployment conditions on Watch Your Step. During battle with two non-unique aliens of your Rep's species, your aliens there may not have their forfeit reduced. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1281_15", "PC-E1",
                "PC website card list (by 2012-07-21)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }

    @Override
    protected Filter immuneAliens(String playerId) {
        return Filters.and(Filters.non_unique, Filters.alien);
    }
}
