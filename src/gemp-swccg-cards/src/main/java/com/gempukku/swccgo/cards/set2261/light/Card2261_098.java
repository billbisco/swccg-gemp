package com.gempukku.swccgo.cards.set2261.light;

import com.gempukku.swccgo.cards.set1261.light.Card1261_098;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Type: Interrupt
 * Title: Out Of Somewhere (V)
 * Original version of 1261_98: the Virtual Block 6.1 release sheet text ("▼ (or retrieve) Lando", "(+1 for each
 * character and vehicle present)"; the PC card list, PC-E1 and now the current card, spells out the download and
 * says "add 1"). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_098 extends Card1261_098 {
    public Card2261_098() {
        setGameText("Use 1 Force to ▼ (or retrieve) Lando. OR If a battle just initiated, and your Lando defending alone at a site, his forfeit cannot be reduced, he adds one destiny to power (+1 for each character and vehicle present), and draws one battle destiny if not able to otherwise. (Immune to Sense.)");
        setLegacyVersion(new LegacyVersion("1261_98", "",
                "Virtual Block 6.1 release sheet (2009-09-04), card #99", LocalDate.of(2009, 9, 4), LocalDate.of(2012, 7, 21)));
    }
}
