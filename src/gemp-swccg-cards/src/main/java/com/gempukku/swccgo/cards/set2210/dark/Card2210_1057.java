package com.gempukku.swccgo.cards.set2210.dark;

import com.gempukku.swccgo.cards.set1210.dark.Card1210_057;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 1
 * Type: Interrupt
 * Title: Emergency Deployment (V)
 * PC-E1 version of 1210_57 (Batch R8-U): the PC website card list text (by 2012-07-21; the list was generated from the
 * Holotable card data), between the Virtual Block 1 release sheet original (2210_2057) and the Virtual Cards Master List
 * (2013-04-27, PC-E2, the current card), which prints the original words again. Against the original: two changes: "a" is added before "non-lightsaber weapon", and "on table" is added after "(simultaneously)".
 * Written-out icons in the list are notation, so the text here uses the icons. Same play as the current card.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2210_1057 extends Card1210_057 {
    public Card2210_1057() {
        super();
        setGameText("[upload] a device or a non-lightsaber weapon. OR If opponent just initiated a battle or Force drain at a system or sector, [download] (as a 'react') an unpiloted TIE and a pilot of ability < 3 (simultaneously) on table. (Immune to Sense.)");
        setLegacyVersion(new LegacyVersion("1210_57", "PC-E1",
                "PC website card list (by 2012-07-21)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
