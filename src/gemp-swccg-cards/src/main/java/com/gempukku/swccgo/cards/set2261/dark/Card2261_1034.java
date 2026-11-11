package com.gempukku.swccgo.cards.set2261.dark;

import com.gempukku.swccgo.cards.set1261.dark.Card1261_034;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.modifiers.SiteAndCardsThereToUsedPileWhenLeavesTableModifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.time.LocalDate;
import java.util.List;

/**
 * Set: Virtual Block 6.1
 * Type: Effect
 * Title: Fleet Security Protocols
 * PC-E1 version of 1261_34: the revised Virtual Block 6.1 sheet text (2010-04-20), reprinted by the Virtual Cards
 * Master List (2013-04-27, PC-E4, the current card) and the PC card image (PC-E3, 2261_3034), so it plays as the current card. The PC card-list rewrite
 * (PC-E2, by 2012-07-21) is 2261_2034.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_1034 extends Card1261_034 {
    public Card2261_1034() {
        setGameText("Deploy on table. During battle involving your starship (or vehicle), it is power +2 for each of your characters at its related sites (they may be forfeited to reduce battle damage and attrition). Once per turn, may ▼ a non-unique starship (or vehicle) site (if such sites leave table, place them and all your cards there in Used Pile). (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1261_34", "PC-E1",
                "Virtual Block 6.1 release sheet, revised (2010-04-20), card #34", LocalDate.of(2010, 4, 20), LocalDate.of(2012, 7, 21)));
    }
}
