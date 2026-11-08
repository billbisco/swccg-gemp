package com.gempukku.swccgo.cards.set2230.dark;

import com.gempukku.swccgo.cards.set1230.dark.Card1230_028;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 3
 * Type: Effect
 * Title: Empire's New Order (V)
 * Original version of 1230_28: the Virtual Block 3 release sheet text (Dec 2009), "Emperor, your admirals, generals, and moffs" (the card list moved Emperor to the end).
 * The current text is the PC website card list (by Jul 2012, PC-E1). Plays the same as the current card; only the wording differs.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2230_028 extends Card1230_028 {
    public Card2230_028() {
        super();
        setGameText("Deploy on table. Whenever an opponent's alien is just lost, they lose 1 Force. While all of your ability on table is provided by Imperials and Imperial starship pilots, Emperor, your admirals, generals, and moffs are deploy -1 and are forfeit +1. Aliens are deploy +1 and forfeit -1.");
        setLegacyVersion(new LegacyVersion("1230_28", "",
                "Virtual Block 3 release sheet (2009-12-07), card #29", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
