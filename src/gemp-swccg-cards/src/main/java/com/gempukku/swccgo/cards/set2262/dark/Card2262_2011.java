package com.gempukku.swccgo.cards.set2262.dark;

import com.gempukku.swccgo.cards.set1262.dark.Card1262_011;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.2
 * Title: So Be It, Jedi
 *
 * PC-E2 version of 1262_11: the PC card image (sobeitjedi.gif), dated by its server upload (2013-02-25, a bulk upload; the image may be older). It prints the release-sheet wording again, word for word.
 * The Master List of 2013-04-27 (the current card) follows it (Bill 2026-11-11: every publication is a version; a PC card image is its own version at its upload
 * date; batch R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2262_2011 extends Card1262_011 {
    public Card2262_2011() {
        super();
        setGameText("Deploy on table. When Luke deploys using your Objective, he deploys to Vader's site (if possible). Duels initiated with your Objective may not be canceled. Once per game, may ▲ Rise, My Friend. If this Effect canceled by opponent, stack it on Insignificant Rebellion.");
        setLegacyVersion(new LegacyVersion("1262_11", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
