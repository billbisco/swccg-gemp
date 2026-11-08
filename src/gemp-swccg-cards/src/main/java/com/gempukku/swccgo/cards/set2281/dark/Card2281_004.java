package com.gempukku.swccgo.cards.set2281.dark;

import com.gempukku.swccgo.cards.set1281.dark.Card1281_004;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.1
 * Type: Character
 * Subtype: Alien
 * Title: Bossk With Mortar Gun (V)
 * Original version of 1281_4 (Virtual Block 8.1 release sheet): the sheet wording, before the PC website card list rephrase (2012-07-21, PC-E1). Same play as the current card. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2281_004 extends Card1281_004 {
    public Card2281_004() {
        super();
        setGameText("Adds 2 to the power of anything he pilots. May not be targeted by weapons unless your other slavers present are each 'hit.' Permanent weapon is •Bossk's Mortar Gun (may target a character for free; draw destiny; target hit, and its forfeit = 0, if destiny = target's destiny number).");
        setLegacyVersion(new LegacyVersion("1281_4", "", "Virtual Block 8.1 release sheet (2012-05-11)", LocalDate.of(2012, 5, 11), LocalDate.of(2012, 7, 21)));
    }
}
