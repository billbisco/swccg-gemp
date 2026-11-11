package com.gempukku.swccgo.cards.set2250.dark;

import com.gempukku.swccgo.cards.set1250.dark.Card1250_009;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Title: Keder The Black (V)
 *
 * PC-E2 version of 1250_9: the PC card image (kedertheblack.gif), dated by its server upload (2013-02-25, a bulk
 * upload; the image may be older). It prints the release-sheet wording. The Master List of 2013-04-27 (the current
 * card) follows it (Bill 2026-11-11: a PC card image is its own version, placed at its upload date; batch R8).
 * Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2250_2009 extends Card1250_009 {
    public Card2250_2009() {
        super();
        setGameText("While at Senate: Sidious is politics +3; 'senators' on Squabbling Delegates is treated as 'Coruscant Guards;' and may lose 1 Force to place Keder and an opponent's [Coruscant] Political Effect (or [Coruscant] senator here) in owner's Lost Pile.");
        setLegacyVersion(new LegacyVersion("1250_9", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
