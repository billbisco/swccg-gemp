package com.gempukku.swccgo.cards.set2281.dark;

import com.gempukku.swccgo.cards.set1281.dark.Card1281_025;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.1
 * Title: Mara Jade's Lightsaber (V)
 * PC-E2 version of 1281_25: the Virtual Cards Master List (2013-04-27). It prints the release-sheet wording again, word
 * for word ("and its forfeit = 0"). The PC card image, uploaded 2013-05-07 (a bulk upload; the image may be older), is the
 * latest publication and the current card, with the card-list words. Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2281_2025 extends Card1281_025 {
    public Card2281_2025() {
        super();
        setGameText("Deploy on Mara Jade. May add 1 to Force drain where present. During battle, Mara may use a blaster and this weapon. May target a character or creature for free. Draw two destiny. Target hit, and its forfeit = 0, if total destiny > defense value.");
        setLegacyVersion(new LegacyVersion("1281_25", "PC-E2", "Virtual Cards Master List (2013-04-27); superseded by the PC card image (server upload 2013-05-07)", LocalDate.of(2013, 4, 27), LocalDate.of(2013, 5, 7)));
    }
}
