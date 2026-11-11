package com.gempukku.swccgo.cards.set2220.dark;

import com.gempukku.swccgo.cards.set601.dark.Card601_213;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 2
 * Type: Vehicle
 * Subtype: Combat
 * Title: Blizzard 2 (V)
 *
 * PC-E1 version of 1220_81: the PC website card list text (by 2012-07-21 to 2013-01-11; the list was generated
 * from the PC's Holotable card data). Then the PC card image (server upload 2013-02-25) and the Master List of 2013-04-27 (PC-E2, the current card) prints the
 * release-sheet wording again (Bill 2026-11-11: every publication is a version; batch R8). Same play. Anything
 * Goes only (docs/legacy-versions.md).
 */
public class Card2220_1081 extends Card601_213 {
    public Card2220_1081() {
        super();
        setGameText("May add 1 pilot and 8 passengers. Permanent pilot provides ability of 2. During your control phase, may take one Trample or Walker Barrage into hand from Reserve Deck; reshuffle. Walker Barage total is +2. Immune to attrition < 4.");
        setLegacyVersion(new LegacyVersion("1220_81", "PC-E1",
                "PC website card list (by 2012-07-21); superseded by the PC card image (server upload 2013-02-25)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
