package com.gempukku.swccgo.cards.set2250.light;

import com.gempukku.swccgo.cards.set601.light.Card601_249;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Title: Obi-Wan In Radiant VII
 *
 * PC-E2 version of 1250_83: the PC card image (obiwaninradiantvii.gif), dated by its server upload (2013-02-25, a bulk upload; the image may be older). It prints the release-sheet wording again, word for word.
 * The Master List of 2013-04-27 (the current card) follows it (Bill 2026-11-11: every publication is a version; a PC card image is its own version at its upload
 * date; batch R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2250_2083 extends Card601_249 {
    public Card2250_2083() {
        super();
        setGameText("May add 1 pilot. Permanent pilot is •Obi-Wan, who provides ability of 6. Opponent's starships may not 'cloak'. Your total ability here may not be reduced.");
        setLegacyVersion(new LegacyVersion("1250_83", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
