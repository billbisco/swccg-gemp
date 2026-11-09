package com.gempukku.swccgo.cards.set2210.light;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
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
 * Original version of 1210_103 (class 601_227): the Virtual Block 1 release sheet (2009-12-07) text.
 * Superseded by the PC website card list text (PC-E1, by 2012-07-21: a rewritten text: her game text may not be canceled anywhere (not only at opponent's Galactic Senate), and "Once per battle at her location, may cancel game text of opponent's leader of ability < 5 present" for "During battle, may cancel game text of a leader of ability < 5 with Leia"), the current card.
 * Plays differently from the current card (see below). Anything Goes only (docs/legacy-versions.md).
 */
public class Card2210_103 extends Card601_227 {
    public Card2210_103() {
        super();
        setGameText("While at opponent's Galactic Senate, Leia's game text may not be canceled and may lose 1 Force to place Leia and any character present out of play. During battle, may cancel game text of a leader of ability < 5 with Leia for remainder of turn.");
        setLegacyVersion(new LegacyVersion("1210_103", "",
                "Virtual Block 1 release sheet (2009-12-07)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }

    /** The release-sheet text: game text may not be canceled only while at opponent's Galactic Senate. */
    @Override
    protected Condition gameTextMayNotBeCanceledCondition(PhysicalCard self) {
        return new AtCondition(self, Filters.and(Filters.opponents(self), Filters.Galactic_Senate));
    }

    /** The release-sheet text: during battle, any leader of ability < 5 with Leia. */
    @Override
    protected Filter leaderToCancel(PhysicalCard self) {
        return Filters.and(Filters.leader, Filters.with(self), Filters.abilityLessThan(5));
    }

    @Override
    protected boolean battleConditionMet(SwccgGame game, PhysicalCard self) {
        return GameConditions.isInBattle(game, self);
    }
}
