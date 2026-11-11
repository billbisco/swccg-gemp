package com.gempukku.swccgo.cards.set2240.dark;

import com.gempukku.swccgo.cards.set1240.dark.Card1240_022;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 4
 * Type: Effect
 * Subtype: Political
 * Title: Crush The Rebellion
 *
 * PC-E2 version of 1240_22: the PC card image (crushtherebellion.gif), dated by its server upload (2013-02-25, a
 * bulk upload; the image may be older). It prints the release-sheet wording. The Master List of 2013-04-27 (the
 * current card) follows it (Bill 2026-11-11: a PC card image is its own version, placed at its upload date; batch
 * R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2240_2022 extends Card1240_022 {
    public Card2240_2022() {
        super();
        setGameText("Deploy on table. May place an Imperial Council Member here from hand. Once per turn, if a logistics agenda or general here, may subtract X from forfeit and immunity to attrition of a character at a site for remainder of turn, where X = total influence at Conference Room.");
        setLegacyVersion(new LegacyVersion("1240_22", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
