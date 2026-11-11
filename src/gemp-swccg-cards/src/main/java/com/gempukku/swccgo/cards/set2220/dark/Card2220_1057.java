package com.gempukku.swccgo.cards.set2220.dark;

import com.gempukku.swccgo.cards.set1220.dark.Card1220_057;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 2
 * Type: Interrupt
 * Title: Debris Zone (V)
 * PC-E1 version of 1220_57 (Batch R8-U): the PC website card list text (by 2012-07-21; the list was generated from the
 * Holotable card data), after the original (2220_2057) and before the PC card image (on the PC server by 2013-02-25, PC-E2, 2220_3057), which prints the original words again.
 * Against the original: two changes: "may" and "one" are added ("may ▲ one Crash Landing"), and "If" after "OR" is written "if", with a small i.
 * Written-out icons in the list are notation, so the text here uses the icons. Same play as the current card.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2220_1057 extends Card1220_057 {
    public Card2220_1057() {
        super();
        setGameText("Once per turn, may [upload] one Crash Landing or Weapon Malfunction. OR if you have a piloted AT-AT on Hoth, target artillery weapon at a related site is lost. (Immune to Sense.)");
        setLegacyVersion(new LegacyVersion("1220_57", "PC-E1",
                "PC website card list (by 2012-07-21)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
