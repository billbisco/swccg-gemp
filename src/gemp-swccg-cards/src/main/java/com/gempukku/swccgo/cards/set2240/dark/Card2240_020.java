package com.gempukku.swccgo.cards.set2240.dark;

import com.gempukku.swccgo.cards.set1240.dark.Card1240_020;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 4
 * Type: Effect
 * Title: Dark Reconnaissance
 * Original version of 1240_20: the Virtual Block 4 release sheet text (Dec 2009), "Your Force drains here cannot be modified or canceled by opponent" last (the card list moved it to second).
 * The current text is the PC website card list (by Jul 2012, PC-E1). Plays the same as the current card; only the wording differs.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2240_020 extends Card1240_020 {
    public Card2240_020() {
        super();
        setGameText("Deploy on opponent's location. Wherever you have two Mistryl, your Force drains are +1. 'Stolen' on Swilla Corey is treated as 'placed in owner's Used Pile' and it may target a weapon instead of a device. Your Force drains here cannot be modified or canceled by opponent.");
        setLegacyVersion(new LegacyVersion("1240_20", "",
                "Virtual Block 4 release sheet (2009-12-07), card #21", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
