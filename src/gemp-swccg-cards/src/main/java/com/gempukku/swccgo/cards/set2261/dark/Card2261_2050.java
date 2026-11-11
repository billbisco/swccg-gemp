package com.gempukku.swccgo.cards.set2261.dark;

import com.gempukku.swccgo.cards.set1261.dark.Card1261_050;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Title: TIE Sentry Ships (V)
 *
 * PC-E2 version of 1261_50: the PC card image (tiesentryships.gif), dated by its server upload (2013-02-25, a bulk upload; the image may be older). It prints the release-sheet wording again, word for word.
 * The Master List of 2013-04-27 (the current card) follows it (Bill 2026-11-11: every publication is a version; a PC card image is its own version at its upload
 * date; batch R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_2050 extends Card1261_050 {
    public Card2261_2050() {
        super();
        setGameText("USED: If you just drew a unique (•) Imperial starship for destiny, take that starship into hand to cancel and redraw that destiny. LOST: Use 3 Force to retrieve a unique (•) Imperial starship into hand.");
        setLegacyVersion(new LegacyVersion("1261_50", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
