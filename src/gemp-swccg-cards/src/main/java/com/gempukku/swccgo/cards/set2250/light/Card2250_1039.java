package com.gempukku.swccgo.cards.set2250.light;

import com.gempukku.swccgo.cards.set1250.light.Card1250_039;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Title: Jedi Advisor
 *
 * PC-E1 version of 1250_39: the PC website card list text (by 2012-07-21 to 2013-01-11; the list was generated
 * from the PC's Holotable card data). Then the PC card image (server upload 2013-02-25) and the Master List of 2013-04-27 (PC-E2, the current card) prints the
 * release-sheet wording again (Bill 2026-11-11: every publication is a version; batch R8). Same play. Anything
 * Goes only (docs/legacy-versions.md).
 */
public class Card2250_1039 extends Card1250_039 {
    public Card2250_1039() {
        super();
        setGameText("Never deploys, except to Coruscant. While Yoda here, Force retrieval from your [Virtual Block 5] objective cannot be cancelled (except by Secret Plans). Unless Inner Strength on table, your duel destiny and lightsaber combat totals are cumulatively +1.");
        setLegacyVersion(new LegacyVersion("1250_39", "PC-E1",
                "PC website card list (by 2012-07-21); superseded by the PC card image (server upload 2013-02-25)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
