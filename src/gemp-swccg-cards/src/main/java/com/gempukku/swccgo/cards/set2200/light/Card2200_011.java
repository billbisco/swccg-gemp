package com.gempukku.swccgo.cards.set2200.light;

import com.gempukku.swccgo.cards.set1200.light.Card1200_011;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;

import java.time.LocalDate;

/**
 * Set: Virtual Shields
 * Type: Defensive Shield
 * Title: There is Another
 * Original version of 1200_11 (PC website card list, by 2012-07-21): "Luke" and "Son of Vader" on opponent's (Death Star II) cards only (the Virtual Shields sheet, May 2013, PC-E1, says "opponent's cards"). Anything Goes only (docs/legacy-versions.md).
 */
public class Card2200_011 extends Card1200_011 {
    public Card2200_011() {
        super();
        setGameText("Unless you have deployed a Skywalker this game, plays on Table. 'Luke' and 'Son of Vader' on opponent's (Death Star II) cards are treated as 'Leia'and 'Lady Vader,' respectively. Once during your turn, opponent may activate 1 Force.");
        setLegacyVersion(new LegacyVersion("1200_11", "", "PC website card list (2012-07-21)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 5, 21)));
    }

    /** The PC website card list text (2012-07-21) says "opponent's (Death Star II) cards". */
    @Override
    protected Filter affectedCards(PhysicalCard self) {
        return Filters.and(Filters.opponents(self), Icon.DEATH_STAR_II);
    }
}
