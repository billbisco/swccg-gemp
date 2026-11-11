package com.gempukku.swccgo.cards.set2281.light;

import com.gempukku.swccgo.cards.set1281.light.Card1281_046;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.1
 * Title: Han's Blaster, So Uncivilized
 *
 * PC-E3 version of 1281_46: the Virtual Cards Master List (2013-04-27). It prints the release-sheet wording again, word for word.
 * The PC card image, uploaded 2013-05-07 (a bulk upload; the image may be older), is the latest publication and the current card (Bill 2026-11-11: every publication is a version; a PC card image is its own version at its upload
 * date; batch R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2281_3046 extends Card1281_046 {
    public Card2281_3046() {
        super();
        setGameText("Deploy on Han. I've Got A Bad Feeling About This is canceled. When fired, Han is cumulatively power +1 until end of turn. May target a character or creature for free. Draw destiny. Target hit, its forfeit = 0 and opponent loses 1 Force if destiny +1 > defense value.");
        setLegacyVersion(new LegacyVersion("1281_46", "PC-E3",
                "Virtual Cards Master List (2013-04-27); superseded by the PC card image (server upload 2013-05-07)", LocalDate.of(2013, 4, 27), LocalDate.of(2013, 5, 7)));
    }
}
