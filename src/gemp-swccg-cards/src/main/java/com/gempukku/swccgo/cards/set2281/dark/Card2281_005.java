package com.gempukku.swccgo.cards.set2281.dark;

import com.gempukku.swccgo.cards.set601.dark.Card601_017;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Block  8
 * Type: Character
 * Subtype: Alien
 * Title: Ket Maliss, Shadow Killer
 * Original version of 1281_5 (Virtual Block 8.1 release sheet), before the PC website card list rephrase (2012-07-21, PC-E1, the current card). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2281_005 extends Card601_017 {
    public Card2281_005() {
        super();
        setGameText("Adds 2 to power of anything he pilots. While with opponent's character of ability > 2, Ket Maliss is power +1, defense value +2, and draws one battle destiny if unable to otherwise. Whenever an opponent's character is lost from same site, opponent loses 1 Force.");
        setLegacyVersion(new LegacyVersion("1281_5", "", "Virtual Block 8.1 release sheet (2012-05-11)", LocalDate.of(2012, 5, 11), LocalDate.of(2012, 7, 21)));
    }
}
