package com.gempukku.swccgo.cards.set2240.dark;

import com.gempukku.swccgo.cards.set1240.dark.Card1240_021;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 4
 * Type: Effect
 * Subtype: Political
 * Title: Blown Clear
 *
 * PC-E2 version of 1240_21: the PC card image (blownclear.gif), dated by its server upload (2013-02-25, a bulk
 * upload; the image may be older). It prints the release-sheet wording. The Master List of 2013-04-27 (the current
 * card) follows it (Bill 2026-11-11: a PC card image is its own version, placed at its upload date; batch R8).
 * Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2240_2021 extends Card1240_021 {
    public Card2240_2021() {
        super();
        setGameText("Deploy on table. May place an Imperial Council Member here from hand. Once per turn, if a logistics agenda or admiral here, may subtract X from a starship's forfeit and immunity to attrition for remainder of turn, where X = total influence at Conference Room.");
        setLegacyVersion(new LegacyVersion("1240_21", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
