package com.gempukku.swccgo.cards.set2271.light;

import com.gempukku.swccgo.cards.set1271.light.Card1271_034;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.1
 * Title: Princess Leia, Lost Scion
 *
 * PC-E2 version of 1271_34: the PC card image (princessleialostscion.gif), dated by its server upload (2013-02-25, a bulk upload; the image may be older). It prints the PC website card-list wording (PC-E1, 2271_1034) again, word for word.
 * The Master List of 2013-04-27 (the current card) follows it (Bill 2026-11-11: every publication is a version; a PC card image is its own version at its upload
 * date; batch R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2271_2034 extends Card1271_034 {
    public Card2271_2034() {
        super();
        setGameText("While present at a battleground, Their Fire Has Gone Out Of The Universe flips and may not flip back. While armed with Anakin's Lightsaber, her forfeit may not be reduced. Immune to attrition < 4.");
        setLegacyVersion(new LegacyVersion("1271_34", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
