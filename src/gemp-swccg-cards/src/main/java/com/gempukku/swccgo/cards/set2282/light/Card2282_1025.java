package com.gempukku.swccgo.cards.set2282.light;

import com.gempukku.swccgo.cards.set1282.light.Card1282_025;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.2
 * Type: Interrupt
 * Title: •You Will Take Me To Jabba Now (V)
 * PC-E1 version of 1282_25 (Batch R8-U): the PC website card list text (by 2012-12-12; the list was generated from the
 * Holotable card data), between the Virtual Block 8.2 release sheet original (2282_2025) and the Virtual Cards Master List
 * (2013-04-27, PC-E2, the current card), which prints the original words again. Against the original: two changes: "May" is added before the ▼, and the first "aliens" is written "Aliens", with a capital A.
 * Written-out icons in the list are notation, so the text here uses the icons. Same play as the current card.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2282_1025 extends Card1282_025 {
    public Card2282_1025() {
        super();
        setGameText("When you play this Interrupt, may place a Rep stacked on your Objective in Used Pile. May [download] Seeking An Audience or an alien to Audience Chamber (for -1 Force). OR Reveal up to 3 Aliens stacked on I Know That Laugh or from hand (X = number of aliens revealed); activate X Force.");
        setLegacyVersion(new LegacyVersion("1282_25", "PC-E1",
                "PC website card list (by 2012-12-12)", LocalDate.of(2012, 12, 12), LocalDate.of(2013, 4, 27)));
    }
}
