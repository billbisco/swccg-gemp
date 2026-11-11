package com.gempukku.swccgo.cards.set2230.light;

import com.gempukku.swccgo.cards.set1230.light.Card1230_116;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 3
 * Type: Starship
 * Subtype: Capital
 * Title: Mon Calamari Star Cruiser
 *
 * PC-E2 version of 1230_116: the PC card image (moncalamaristarcruiser.gif), dated by its server upload
 * (2013-02-25, a bulk upload; the image may be older). It prints the release-sheet wording. The Master List of
 * 2013-04-27 (the current card) follows it (Bill 2026-11-11: a PC card image is its own version, placed at its
 * upload date; batch R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2230_2116 extends Card1230_116 {
    public Card2230_2116() {
        super();
        setGameText("Deploys -1 (to a minimum of 3) for each Imperial starship on table. May add 5 pilots, 6 passengers, 1 vehicle, and 3 starfighters. Permanent pilot provides ability of 2.");
        setLegacyVersion(new LegacyVersion("1230_116", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
