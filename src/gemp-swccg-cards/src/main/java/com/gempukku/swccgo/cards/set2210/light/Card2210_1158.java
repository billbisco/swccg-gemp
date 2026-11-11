package com.gempukku.swccgo.cards.set2210.light;

import com.gempukku.swccgo.cards.set1210.light.Card1210_158;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 1
 * Type: Interrupt
 * Title: Panic (V)
 * PC-E1 version of 1210_158 (Batch R8-U): the PC website card list text (by 2012-07-21; the list was generated from the
 * Holotable card data), after the original (2210_2158) and before the PC card image (on the PC server by 2013-02-25, PC-E2, 2210_3158), which prints the original words again.
 * Against the original: two changes: "a" is added before "non-lightsaber weapon", and "on table" is added after "(simultaneously)".
 * Written-out icons in the list are notation, so the text here uses the icons. Same play as the current card.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2210_1158 extends Card1210_158 {
    public Card2210_1158() {
        super();
        setGameText("[upload] a device or a non-lightsaber weapon. OR If opponent just initiated a battle or Force drain at a system or sector, [download] (as a 'react') an unpiloted Rebel starfighter and a pilot of ability < 3 (simultaneously) on table. (Immune to Sense.)");
        setLegacyVersion(new LegacyVersion("1210_158", "PC-E1",
                "PC website card list (by 2012-07-21)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
