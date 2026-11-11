package com.gempukku.swccgo.cards.set2250.dark;

import com.gempukku.swccgo.cards.set1250.dark.Card1250_002;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Type: Character
 * Title: Aurra Sing (V)
 * PC-E1 version of 1250_2: the PC website card list text (by 2012-07-21): "relocate Aurra to a site where opponent has a
 * Jedi". The Virtual Cards Master List (2013-04-27, PC-E3) repeats the 2009 sheet words and is the current card.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2250_1002 extends Card1250_002 {
    public Card2250_1002() {
        super();
        setGameText("Once during opponent's activate phase, may relocate Aurra to a site where opponent has a Jedi. Once per game, during your move phase, may fire her weapon at a Jedi; hit target is lost. Immune to attrition < 4.");
        setLegacyVersion(new LegacyVersion("1250_2", "PC-E1",
                "PC website card list (by 2012-07-21)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }

    @Override
    protected Filter relocationJedi(PhysicalCard self) {
        return Filters.and(Filters.opponents(self), Filters.Jedi);
    }
}
