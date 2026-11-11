package com.gempukku.swccgo.cards.set2230.dark;

import com.gempukku.swccgo.cards.set601.dark.Card601_012;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 3
 * Type: Starship
 * Subtype: Capital
 * Title: Jabba's Space Cruiser (V)
 *
 * PC-E2 version of 1230_55: the PC website card list text (by 2012-07-21 to 2013-01-11; generated from the PC's
 * Holotable card data). The Master List of 2013-04-27 (PC-E3, the current card) prints the release-sheet wording
 * again. Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2230_1055 extends Card601_012 {
    public Card2230_1055() {
        super();
        setGameText("Deploys and moves like a starfighter. Permanent pilot provides ability of 2. May add 1 alien pilot and 6 passengers. Your [Independent] starships may deploy here as a 'react'. If Jabba here, immune to attrition.");
        setLegacyVersion(new LegacyVersion("1230_55", "PC-E2",
                "PC website card list (by 2012-07-21); superseded by the Master List text (2013-04-27)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
