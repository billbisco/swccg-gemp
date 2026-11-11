package com.gempukku.swccgo.cards.set2273.light;

import com.gempukku.swccgo.cards.set601.light.Card601_063;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.3
 * Title: Yub Yub, Commander
 *
 * PC-E2 version of 1273_35: the PC card image (yubyubcommander.gif), dated by its server upload (2013-02-25, a bulk upload; the image may be older). It prints the release-sheet wording again, word for word.
 * The Master List of 2013-04-27 (the current card) follows it (Bill 2026-11-11: every publication is a version; a PC card image is its own version at its upload
 * date; batch R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2273_2035 extends Card601_063 {
    public Card2273_2035() {
        super();
        setGameText("USED: ▼ a Rogue Squadron pilot to a location you occupy with a Rogue Squadron pilot. LOST: Cancel an attempt by opponent to target a Rogue Squadron pilot to be captured or excluded from battle. OR If two Rogue Squadron pilots are in battle together, add one battle destiny.");
        setLegacyVersion(new LegacyVersion("1273_35", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
