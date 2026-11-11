package com.gempukku.swccgo.cards.set2281.dark;

import com.gempukku.swccgo.cards.set601.dark.Card601_017;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.1
 * Title: Ket Maliss, Shadow Killer
 *
 * PC-E2 version of 1281_5: the Virtual Cards Master List (2013-04-27). It prints the release-sheet wording again, word for word.
 * The PC card image, uploaded 2013-05-07 (a bulk upload; the image may be older), is the latest publication and the current card (Bill 2026-11-11: every publication is a version; a PC card image is its own version at its upload
 * date; batch R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2281_2005 extends Card601_017 {
    public Card2281_2005() {
        super();
        setGameText("Adds 2 to power of anything he pilots. While with opponent's character of ability > 2, Ket Maliss is power +1, defense value +2, and draws one battle destiny if unable to otherwise. Whenever an opponent's character is lost from same site, opponent loses 1 Force.");
        setLegacyVersion(new LegacyVersion("1281_5", "PC-E2",
                "Virtual Cards Master List (2013-04-27); superseded by the PC card image (server upload 2013-05-07)", LocalDate.of(2013, 4, 27), LocalDate.of(2013, 5, 7)));
    }
}
