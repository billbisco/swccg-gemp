package com.gempukku.swccgo.cards.set2281.dark;

import com.gempukku.swccgo.cards.set1281.dark.Card1281_016;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.1
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Abyssin Ornament (V)
 * Original version of 1281_16 (Virtual Block 8.1 release sheet): the sheet wording, before the PC website card list rephrase (2012-07-21, PC-E1). Same play as the current card. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2281_016 extends Card1281_016 {
    public Card2281_016() {
        super();
        setGameText("USED: Cancel an attempt to target your non-droid assassin (or a starship piloted by a non-droid assassin) with a weapon. LOST: Use 1 Force to take a just-lost non-droid assassin into hand.");
        setLegacyVersion(new LegacyVersion("1281_16", "", "Virtual Block 8.1 release sheet (2012-05-11)", LocalDate.of(2012, 5, 11), LocalDate.of(2012, 7, 21)));
    }
}
