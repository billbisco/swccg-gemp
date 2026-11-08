package com.gempukku.swccgo.cards.set2261.light;

import com.gempukku.swccgo.cards.set1261.light.Card1261_102;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Type: Interrupt
 * Title: Warrior's Courage (V)
 * Original version of 1261_102: the Virtual Block 6.1 release sheet text, "▼ (or retrieve) Leia" and "non-[Reflections III] Leia defending alone" (the PC card list, PC-E1, spells out the download and adds "is").
 * Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_102 extends Card1261_102 {
    public Card2261_102() {
        setGameText("Use 1 Force to ▼ (or retrieve) Leia. OR If a battle just initiated, and non-[Reflections III] Leia defending alone at a site, her forfeit cannot be reduced, she adds one destiny to power (+1 for each character and vehicle present), and draws one battle destiny if not able to otherwise. (Immune to Sense.)");
        setLegacyVersion(new LegacyVersion("1261_102", "",
                "Virtual Block 6.1 release sheet (2009-09-04), card #103", LocalDate.of(2009, 9, 4), LocalDate.of(2012, 7, 21)));
    }
}
