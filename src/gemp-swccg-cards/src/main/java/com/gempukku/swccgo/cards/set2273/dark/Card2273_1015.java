package com.gempukku.swccgo.cards.set2273.dark;

import com.gempukku.swccgo.cards.set1273.dark.Card1273_015;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.3
 * Title: Evacuate? (V)
 * PC-E1 version of 1273_15: the PC website card-list text, shown there by 2012-07-21 (the card list was generated from the
 * Holotable card data). The Virtual Cards Master List (2013-04-27, PC-E2, the current card) repeats the
 * release-sheet wording, after the PC card image (uploaded 2013-02-25, the same wording); every publication is a version (Bill, 2026-11-11). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2273_1015 extends Card1273_015 {
    public Card2273_1015() {
        super();
        setGameText("Use 1 Force to traget your character of ability > 3 alone at a site. USED: During any move phase, take target into hand. LOST: If opponent has exactly one non-spy character at target's site, place target in Used Pile.");
        setLegacyVersion(new LegacyVersion("1273_15", "PC-E1",
                "PC website card list (by 2012-07-21)",
                LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
