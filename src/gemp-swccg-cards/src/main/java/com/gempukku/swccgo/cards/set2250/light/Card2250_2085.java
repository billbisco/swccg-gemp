package com.gempukku.swccgo.cards.set2250.light;

import com.gempukku.swccgo.cards.set601.light.Card601_164;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Title: Elegant Lightsaber
 *
 * PC-E2 version of 1250_85: the PC card image (elegantlightsaber.gif), dated by its server upload (2013-02-25, a bulk upload; the image may be older). It prints the release-sheet wording again, word for word.
 * The Master List of 2013-04-27 (the current card) follows it (Bill 2026-11-11: every publication is a version; a PC card image is its own version at its upload
 * date; batch R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2250_2085 extends Card601_164 {
    public Card2250_2085() {
        super();
        setGameText("Deploy on a Jedi. May add 1 to Force drain at opponent's site where present. May target a character or creature for free. Draw two destiny. Target hit, and may not be used to satisfy attrition If total destiny > defense value.");
        setLegacyVersion(new LegacyVersion("1250_85", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
