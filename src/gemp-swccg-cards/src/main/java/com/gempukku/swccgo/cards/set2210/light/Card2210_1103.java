package com.gempukku.swccgo.cards.set2210.light;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.set601.light.Card601_227;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;

import java.time.LocalDate;

/**
 * Set: Virtual Block 1
 * Type: Character
 * Title: Leia (V)
 * PC-E1 version of 1210_103 (class 601_227): the PC website card list text (by 2012-07-21): her game text may not be
 * canceled anywhere, and once per battle at her location she may cancel the game text of opponent's leader of
 * ability < 5 present. The Master List (2013-04-27, PC-E3) repeats the 2009 sheet words and is the current card.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2210_1103 extends Card601_227 {
    public Card2210_1103() {
        super();
        setGameText("Leia's game text may not be canceled. While at opponent's Galactic Senate, may lose 1 Force to place Leia and any character present out of play. Once per battle at her location, may cancel game text of opponent's leader of ability < 5 present for remainder of turn.");
        setLegacyVersion(new LegacyVersion("1210_103", "PC-E1",
                "PC website card list (by 2012-07-21)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }

    /** Card list: "Leia's game text may not be canceled." (anywhere). */
    @Override
    protected Condition gameTextMayNotBeCanceledCondition(PhysicalCard self) {
        return null;
    }

    /** Card list: "opponent's leader of ability < 5 present". */
    @Override
    protected Filter leaderToCancel(PhysicalCard self) {
        return Filters.and(Filters.opponents(self), Filters.leader, Filters.present(self), Filters.abilityLessThan(5));
    }

    /** Card list: "Once per battle at her location". */
    @Override
    protected boolean battleConditionMet(SwccgGame game, PhysicalCard self) {
        return GameConditions.isDuringBattleAt(game, Filters.sameLocation(self));
    }
}
