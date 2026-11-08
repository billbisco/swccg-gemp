package com.gempukku.swccgo.cards.set2230.light;

import com.gempukku.swccgo.cards.set1230.light.Card1230_116;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 3
 * Type: Starship
 * Title: Mon Calamari Star Cruiser (V)
 * Original version of 1230_116: the Virtual Block 3 release sheet text (Dec 2009). The PC website card list (by Jul 2012,
 * PC-E1) is the current text: the card list moved "for each Imperial starship on table" to the front.
 * Plays the same as the current card; only the wording differs. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2230_116 extends Card1230_116 {
    public Card2230_116() {
        super();
        setGameText("Deploys -1 (to a minimum of 3) for each Imperial starship on table. May add 5 pilots, 6 passengers, 1 vehicle, and 3 starfighters. Permanent pilot provides ability of 2.");
        setLegacyVersion(new LegacyVersion("1230_116", "",
                "Virtual Block 3 release sheet (2009-12-07), card #116", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
