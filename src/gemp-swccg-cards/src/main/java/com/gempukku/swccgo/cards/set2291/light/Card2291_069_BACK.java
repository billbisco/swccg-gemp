package com.gempukku.swccgo.cards.set2291.light;

import com.gempukku.swccgo.cards.set1291.light.Card1291_069;
import com.gempukku.swccgo.cards.set1291.light.Card1291_069_BACK;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 9.1
 * Type: Objective
 * Title: Republic At War / Aggressive Negotiations (back)
 * Original version of 1291_69_BACK: the Virtual Block 9.1 release sheet (official text as of 2014-01-10): "Immediately,
 * may place X cards from hand on bottom of Force Pile to draw up to X+1 cards from Reserve Deck once per game" (no
 * limit on X). The Advocate Council errata of 2014-08-08 (PC forum p901169) limited X to 3 and moved the cards to the
 * Used Pile (PC-E1, the current card). Anything Goes only (docs/legacy-versions.md).
 */
public class Card2291_069_BACK extends Card1291_069_BACK {
    public Card2291_069_BACK() {
        super();
        setGameText(Card1291_069.ORIGINAL_GAMETEXT);
        setLegacyVersion(new LegacyVersion("1291_69_BACK", "",
                "Virtual Block 9.1 release sheet (2014-01-10)", LocalDate.of(2014, 1, 10), LocalDate.of(2014, 8, 8)));
    }

    @Override
    protected boolean placesOnUsedPile() {
        return false;
    }

    @Override
    protected int placeLimit() {
        return Integer.MAX_VALUE;
    }
}
