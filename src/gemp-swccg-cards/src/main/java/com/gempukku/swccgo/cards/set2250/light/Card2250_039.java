package com.gempukku.swccgo.cards.set2250.light;

import com.gempukku.swccgo.cards.set1250.light.Card1250_039;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Type: 
 * Title: Jedi Advisor (V)
 * Original version of 1250_39: the Virtual Block 5 release sheet (2009-12-07) text.
 * Superseded by the PC website card list text (PC-E1, by 2012-07-21: "cancelled" for "canceled" (the block list and the Virtual master list agree on "cancelled" in every capture)), the current card.
 * Plays the same as the current card; only the wording differs. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2250_039 extends Card1250_039 {
    public Card2250_039() {
        super();
        setGameText("Never deploys, except to Coruscant. While Yoda here, Force retrieval from your [Virtual Block 5] objective cannot be canceled (except by Secret Plans). Unless Inner Strength on table, your duel destiny and lightsaber combat totals are cumulatively +1.");
        setLegacyVersion(new LegacyVersion("1250_39", "",
                "Virtual Block 5 release sheet (2009-12-07)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
