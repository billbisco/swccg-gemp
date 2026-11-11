package com.gempukku.swccgo.cards.set2261.light;

import com.gempukku.swccgo.cards.set601.light.Card601_250;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Type: Starship
 * Title: Overseer
 * PC-E1 version of 1261_106: the PC website card list text (by 2012-07-21): "If opponent just deployed a Dark Jedi or Sith".
 * The Virtual Cards Master List (2013-04-27, PC-E3) repeats the 2009 sheet words and is the current card.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_1106 extends Card601_250 {
    public Card2261_1106() {
        super();
        setGameText("May add Harc as a pilot. During battle, adds one destiny to total power. If opponent just deployed a Dark Jedi or Sith to a related site, may deploy a related location from Reserve Deck; reshuffle. Immune to attrition < 4 (< 6 while at Bespin).");
        setLegacyVersion(new LegacyVersion("1261_106", "PC-E1",
                "PC website card list (by 2012-07-21)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }

    @Override
    protected Filter justDeployedFilter(PhysicalCard self) {
        return Filters.and(Filters.opponents(self), Filters.or(Filters.Dark_Jedi, Filters.Sith));
    }
}
