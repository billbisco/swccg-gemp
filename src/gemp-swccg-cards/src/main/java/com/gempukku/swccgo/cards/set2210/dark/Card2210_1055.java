package com.gempukku.swccgo.cards.set2210.dark;

import com.gempukku.swccgo.cards.set1210.dark.Card1210_055;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 1
 * Type: Interrupt
 * Subtype: Used
 * Title: Collateral Damage
 *
 * PC-E1 version of 1210_55: the PC website card list text (by 2012-07-21 to 2013-01-11; the list was generated
 * from the PC's Holotable card data). Then the PC card image (server upload 2013-02-25) and the Master List of 2013-04-27 (PC-E2, the current card) prints the
 * release-sheet wording again (Bill 2026-11-11: every publication is a version; batch R8). Same play. Anything
 * Goes only (docs/legacy-versions.md).
 */
public class Card2210_1055 extends Card1210_055 {
    public Card2210_1055() {
        super();
        setGameText("If you just initiated battle where you have at least two characters (one a trooper and the other a leader), add two battle destiny. Opponent takes no battle damagethis battle. OR Deploy one Dewback from Reserve Deck; reshuffle.");
        setLegacyVersion(new LegacyVersion("1210_55", "PC-E1",
                "PC website card list (by 2012-07-21); superseded by the PC card image (server upload 2013-02-25)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
