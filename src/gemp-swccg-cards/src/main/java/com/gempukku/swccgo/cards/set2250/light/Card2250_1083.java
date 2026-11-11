package com.gempukku.swccgo.cards.set2250.light;

import com.gempukku.swccgo.cards.set601.light.Card601_249;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Title: Obi-Wan In Radiant VII
 * PC-E1 version of 1250_83: the PC website card-list text, shown there by 2012-07-21 (the card list was generated from the
 * Holotable card data). The Virtual Cards Master List (2013-04-27, PC-E2, the current card) repeats the
 * release-sheet wording, after the PC card image (uploaded 2013-02-25, the same wording); every publication is a version (Bill, 2026-11-11). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2250_1083 extends Card601_249 {
    public Card2250_1083() {
        super();
        setGameText("May add 1 pilot. Permanent pilot is •Obi-Wan, who provides ability of 6. Opponent's starships may not cloak. Your total ability here may not be reduced.");
        setLegacyVersion(new LegacyVersion("1250_83", "PC-E1",
                "PC website card list (by 2012-07-21)",
                LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
        setPrintedTitle("Obi-Wan in Radiant VII"); // the card list prints a small i
    }
}
