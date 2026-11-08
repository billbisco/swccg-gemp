package com.gempukku.swccgo.cards.set2230.dark;

import com.gempukku.swccgo.cards.set1230.dark.Card1230_005;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 3
 * Type: Character
 * Title: Fozec (V)
 * Original version of 1230_5: the Virtual Block 3 release sheet text (Dec 2009), "draw destiny; may retrieve 2 Force if destiny > 2".
 * The current text is the PC website card list (by Jul 2012, PC-E1). Plays the same as the current card; only the wording differs.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2230_005 extends Card1230_005 {
    public Card2230_005() {
        super();
        setGameText("Adds 2 to power of anything he pilots. While Undercover, opponent's Force drains at related locations are -1. Once during battle, if with an ISB agent, may cause opponent to draw destiny; may retrieve 2 Force if destiny > 2.");
        setLegacyVersion(new LegacyVersion("1230_5", "",
                "Virtual Block 3 release sheet (2009-12-07), card #05", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
