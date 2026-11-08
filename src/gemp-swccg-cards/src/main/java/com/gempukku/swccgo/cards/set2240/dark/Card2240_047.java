package com.gempukku.swccgo.cards.set2240.dark;

import com.gempukku.swccgo.cards.set1240.dark.Card1240_047;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 4
 * Type: Interrupt
 * Title: Unexpected Interruption (V)
 * Original version of 1240_47: the Virtual Block 4 release sheet text (Dec 2009), "If destiny +2 > target's ability, target excluded ..." (the card list moved the condition to the end).
 * The current text is the PC website card list (by Jul 2012, PC-E1). Plays the same as the current card; only the wording differs.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2240_047 extends Card1240_047 {
    public Card2240_047() {
        super();
        setGameText("If a battle was just initiated, target an opponent's female non-droid character present with Xizor. Draw destiny. If destiny +2 > target's ability, target excluded from battle and adds her power to Xizor's for remainder of turn.");
        setLegacyVersion(new LegacyVersion("1240_47", "",
                "Virtual Block 4 release sheet (2009-12-07), card #47", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
