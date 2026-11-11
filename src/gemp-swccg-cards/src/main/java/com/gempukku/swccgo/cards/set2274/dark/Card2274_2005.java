package com.gempukku.swccgo.cards.set2274.dark;

import com.gempukku.swccgo.cards.set1274.dark.Card1274_005;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.4
 * Title: Desperate Counter (v)
 *
 * PC-E2 version of 1274_5: the PC card image (desperatecounter.gif), dated by its server upload (2013-02-25, a bulk upload; the image may be older). It prints the release-sheet wording again, word for word.
 * The Master List of 2013-04-27 (the current card) follows it (Bill 2026-11-11: every publication is a version; a PC card image is its own version at its upload
 * date; batch R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2274_2005 extends Card1274_005 {
    public Card2274_2005() {
        super();
        setGameText("Unless Imperial Propaganda on table, if you are about to lose Force during opponent's control phase, deploy on table to reduce loss by 4 (ignore other modifiers); opponent may [upload] any one card. [Immune to Control.] OR Use 2 Force to deploy on table and stack a just-played Interrupt here.");
        setLegacyVersion(new LegacyVersion("1274_5", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
