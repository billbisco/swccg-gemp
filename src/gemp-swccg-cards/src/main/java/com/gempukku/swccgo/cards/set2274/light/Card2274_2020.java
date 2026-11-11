package com.gempukku.swccgo.cards.set2274.light;

import com.gempukku.swccgo.cards.set1274.light.Card1274_020;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.4
 * Title: Temporary Foothold
 *
 * PC-E2 version of 1274_20: the PC card image (temporaryfoothold.gif), dated by its server upload (2013-02-25, a bulk upload; the image may be older). It prints the release-sheet wording again, word for word.
 * The Master List of 2013-04-27 (the current card) follows it (Bill 2026-11-11: every publication is a version; a PC card image is its own version at its upload
 * date; batch R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2274_2020 extends Card1274_020 {
    public Card2274_2020() {
        super();
        setGameText("Deploy on a battleground. While you occupy at least two battlegrounds, your Force drains here may not be canceled. During battle here or at a related location, may place this Effect in Used Pile to draw up to two cards from Reserve Deck.");
        setLegacyVersion(new LegacyVersion("1274_20", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
