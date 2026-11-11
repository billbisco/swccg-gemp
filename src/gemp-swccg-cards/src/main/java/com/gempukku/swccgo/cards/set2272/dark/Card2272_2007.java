package com.gempukku.swccgo.cards.set2272.dark;

import com.gempukku.swccgo.cards.set1272.dark.Card1272_007;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.2
 * Title: Where Are You Taking Them?
 *
 * PC-E2 version of 1272_7: the PC card image (whereareyoutakingthem.gif), dated by its server upload (2013-02-25, a bulk upload; the image may be older). It prints the release-sheet wording again, word for word.
 * The Master List of 2013-04-27 (the current card) follows it (Bill 2026-11-11: every publication is a version; a PC card image is its own version at its upload
 * date; batch R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2272_2007 extends Card1272_007 {
    public Card2272_2007() {
        super();
        setGameText("Deploy on table. At non-[Episode I] sites, immunity to attrition of [Coruscant] Jedi is limited to < 5. Your gametext on [Coruscant] systems is canceled. During your control phase, may say 'Coruscant?' and lose 1 Force to place a just-resolved Interrupt in owner's Lost Pile. [Immune to Alter.]");
        setLegacyVersion(new LegacyVersion("1272_7", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
