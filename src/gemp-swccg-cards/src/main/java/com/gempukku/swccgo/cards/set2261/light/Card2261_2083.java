package com.gempukku.swccgo.cards.set2261.light;

import com.gempukku.swccgo.cards.set1261.light.Card1261_083;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Title: Mon Calamari Dockyards
 *
 * PC-E2 version of 1261_83: the PC card image (moncalamaridockyards.gif), dated by its server upload (2013-02-25, a bulk upload; the image may be older). It prints the release-sheet wording again, word for word.
 * The Master List of 2013-04-27 (the current card) follows it (Bill 2026-11-11: every publication is a version; a PC card image is its own version at its upload
 * date; batch R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_2083 extends Card1261_083 {
    public Card2261_2083() {
        super();
        setGameText("Deploy on table. Star Cruisers (except Home One) may deploy -2 (to a maximum of -3), ignore deployment restrictions in their game text, draw one battle destiny if not able to otherwise, and are immune to attrition < 4. Capital Support is canceled. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1261_83", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
