package com.gempukku.swccgo.cards.set2261.light;

import com.gempukku.swccgo.cards.set1261.light.Card1261_093;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Type: Interrupt
 * Title: Armed And Dangerous & Krayt Dragon Howl
 * Original version of 1261_93: the Virtual Block 6.1 release sheet text (printed as Krayt Dragon Howl & Armed And Dangerous; download icons; the PC card list, PC-E1
 * and now the current card, spells them out). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_093 extends Card1261_093 {
    public Card2261_093() {
        setGameText("USED: Cancel Disarmed or Stunning Leader. OR If a battle was just initiated, ▼ (or deploy from hand) a character weapon on your participating character. LOST: Use 1 Force to ▼ (or retrieve) non-[Reflections 3] Obi-Wan. OR If opponent just initiated a battle against non-[Reflections 3] Obi-Wan alone at a site, his forfeit cannot be reduced.");
        setLegacyVersion(new LegacyVersion("1261_93", "",
                "Virtual Block 6.1 release sheet (2009-09-04), card #91", LocalDate.of(2009, 9, 4), LocalDate.of(2012, 7, 21)));
        setPrintedTitle("Krayt Dragon Howl & Armed And Dangerous");
    }
}
