package com.gempukku.swccgo.cards.set2281.light;

import com.gempukku.swccgo.cards.set1281.light.Card1281_036;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.1
 * Title: Scoundrel's Ingenuity
 *
 * PC-E2 version of 1281_36: the Virtual Cards Master List (2013-04-27). It prints the release-sheet wording again, word for word.
 * The PC card image, uploaded 2013-05-07 (a bulk upload; the image may be older), is the latest publication and the current card (Bill 2026-11-11: every publication is a version; a PC card image is its own version at its upload
 * date; batch R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2281_2036 extends Card1281_036 {
    public Card2281_2036() {
        super();
        setGameText("Deploy from under Scoundrel's Luck on a scoundrel. Scoundrel is power +2 and his total weapon destiny is +1. During your control phase, may draw destiny. If destiny > 3, your Force drains at same and related locations are +1 for remainder of turn.");
        setLegacyVersion(new LegacyVersion("1281_36", "PC-E2",
                "Virtual Cards Master List (2013-04-27); superseded by the PC card image (server upload 2013-05-07)", LocalDate.of(2013, 4, 27), LocalDate.of(2013, 5, 7)));
    }
}
