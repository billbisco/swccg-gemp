package com.gempukku.swccgo.cards.set2261.light;

import com.gempukku.swccgo.cards.set601.light.Card601_169;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Type: Interrupt
 * Title: I've Decided To Go Back (V)
 * Original version of 1261_96: the Virtual Block 6.1 release sheet text, "▼ (or retrieve) Panaka, Ric Olie, or non-[Virtual] Amidala" and "if unable to" (the PC card list, PC-E1, reorders and says "if not able to").
 * Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_096 extends Card601_169 {
    public Card2261_096() {
        setGameText("Use 1 Force to ▼ (or retrieve) Panaka, Ric Olie, or non-[Virtual] Amidala. OR If your ability = 3 character defending a battle alone at a site (except Senate), character is power +2, adds one destiny to power, and may draw one battle destiny if unable to otherwise. (Immune to Sense.)");
        setLegacyVersion(new LegacyVersion("1261_96", "",
                "Virtual Block 6.1 release sheet (2009-09-04), card #97", LocalDate.of(2009, 9, 4), LocalDate.of(2012, 7, 21)));
    }
}
