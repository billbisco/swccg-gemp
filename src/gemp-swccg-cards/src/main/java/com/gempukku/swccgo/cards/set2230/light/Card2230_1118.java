package com.gempukku.swccgo.cards.set2230.light;

import com.gempukku.swccgo.cards.set1230.light.Card1230_118;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 3
 * Type: Weapon
 * Subtype: Starship
 * Title: Enhanced Proton Torpedoes
 *
 * PC-E1 version of 1230_118: the PC website card list text (by 2012-07-21 to 2013-01-11; the list was generated
 * from the PC's Holotable card data). Then the PC card image (server upload 2013-02-25) and the Master List of 2013-04-27 (PC-E2, the current card) prints the
 * release-sheet wording again (Bill 2026-11-11: every publication is a version; batch R8). Same play. Anything
 * Goes only (docs/legacy-versions.md).
 */
public class Card2230_1118 extends Card1230_118 {
    public Card2230_1118() {
        super();
        setGameText("Use 1 Force to deploy on your starfighter. May target a starship for free. Draw destiny. If on a Heroic starfighter, may use 1 Force to add 3 to total. Target hit if destiny > defense value.");
        setLegacyVersion(new LegacyVersion("1230_118", "PC-E1",
                "PC website card list (by 2012-07-21); superseded by the PC card image (server upload 2013-02-25)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
