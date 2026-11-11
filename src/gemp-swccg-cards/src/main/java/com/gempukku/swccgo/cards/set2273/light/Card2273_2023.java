package com.gempukku.swccgo.cards.set2273.light;

import com.gempukku.swccgo.cards.set1273.light.Card1273_023;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.3
 * Title: Captain Antilles
 *
 * PC-E2 version of 1273_23: the PC card image (captainantilles.gif), dated by its server upload (2013-02-25, a bulk upload; the image may be older). It prints the release-sheet wording again, word for word.
 * The Master List of 2013-04-27 (the current card) follows it (Bill 2026-11-11: every publication is a version; a PC card image is its own version at its upload
 * date; batch R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2273_2023 extends Card1273_023 {
    public Card2273_2023() {
        super();
        setGameText("[Pilot] 2, 3: any corvette. While piloting a corvette, it is immune to attrition < 5 and, once per turn, may peek at a card in opponent's hand. Once per game, during battle (unless Vader here) may use 2 Force to move starship he pilots away (for free).");
        setLegacyVersion(new LegacyVersion("1273_23", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
