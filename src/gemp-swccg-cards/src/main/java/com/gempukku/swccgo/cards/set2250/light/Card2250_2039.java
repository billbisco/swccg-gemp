package com.gempukku.swccgo.cards.set2250.light;

import com.gempukku.swccgo.cards.set1250.light.Card1250_039;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Title: Jedi Advisor
 *
 * PC-E2 version of 1250_39: the PC card image (jediadvisor.gif), dated by its server upload (2013-02-25, a bulk
 * upload; the image may be older). It prints the release-sheet wording. The Master List of 2013-04-27 (the current
 * card) follows it (Bill 2026-11-11: a PC card image is its own version, placed at its upload date; batch R8).
 * Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2250_2039 extends Card1250_039 {
    public Card2250_2039() {
        super();
        setGameText("Never deploys, except to Coruscant. While Yoda here, Force retrieval from your [Virtual Block 5] objective cannot be canceled (except by Secret Plans). Unless Inner Strength on table, your duel destiny and lightsaber combat totals are cumulatively +1.");
        setLegacyVersion(new LegacyVersion("1250_39", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
