package com.gempukku.swccgo.cards.set2281.light;

import com.gempukku.swccgo.cards.set1281.light.Card1281_046;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.1
 * Title: Han's Blaster, So Uncivilized
 * PC-E2 version of 1281_46: the PC website card-list text, shown there by 2012-07-21 (the card list was generated from the
 * Holotable card data). The Virtual Cards Master List (2013-04-27, PC-E3, the current card) repeats the
 * release-sheet wording; every publication is a version (Bill, 2026-11-11). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2281_2046 extends Card1281_046 {
    public Card2281_2046() {
        super();
        setGameText("Deploy on Han. I've Got a Bad Feeling About This is canceled. When fired, Han is cumulatively power +1 until end of turn. May target a character or creature for free. Draw destiny. Target hit, forfeit = 0 and opponent loses 1 Force if destiny +1 > defense value.");
        setLegacyVersion(new LegacyVersion("1281_46", "PC-E2",
                "PC website card list (by 2012-12-12)",
                LocalDate.of(2012, 12, 12), LocalDate.of(2013, 4, 27)));
    }
}
