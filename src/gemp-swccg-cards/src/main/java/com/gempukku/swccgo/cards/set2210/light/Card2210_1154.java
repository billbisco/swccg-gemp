package com.gempukku.swccgo.cards.set2210.light;

import com.gempukku.swccgo.cards.set1210.light.Card1210_154;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 1
 * Type: Interrupt
 * Title: •Harvest (V)
 * PC-E1 version of 1210_154 (Batch R8-U): the PC website card list text (by 2012-07-21; the list was generated from the
 * Holotable card data), between the Virtual Block 1 release sheet original (2210_154) and the Virtual Cards Master List
 * (2013-04-27, PC-E2, the current card), which prints the original words again. Against the original: one change: "▲ (or retrieve) a device." is reworded as "Retrieve a device, or ▲ a device.".
 * Written-out icons in the list are notation, so the text here uses the icons. Same play as the current card.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2210_1154 extends Card1210_154 {
    public Card2210_1154() {
        super();
        setGameText("USED: Retrieve a device, or [upload] a device. (Immune to Sense.) STARTING: If your starting location was a Tatooine location, [download] a farm, two devices, and two Effects that are always [Immune to Alter]. Place this Interrupt in Reserve Deck.");
        setLegacyVersion(new LegacyVersion("1210_154", "PC-E1",
                "PC website card list (by 2012-07-21)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
