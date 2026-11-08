package com.gempukku.swccgo.cards.set2230.light;

import com.gempukku.swccgo.cards.set1230.light.Card1230_105;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 3
 * Type: Interrupt
 * Title: Ke Chu Ke Kukuta? (V)
 * Original version of 1230_105: the Virtual Block 3 release sheet text (Dec 2009). The PC website card list (by Jul 2012,
 * PC-E1) is the current text: the card list added "you" ("you may use 1 Force").
 * Plays the same as the current card; only the wording differs. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2230_105 extends Card1230_105 {
    public Card2230_105() {
        super();
        setGameText("If you have an information broker on table, draw destiny. If destiny > the number of opponent's characters on table, may use 1 Force to take any one card into hand from Reserve Deck; reshuffle.");
        setLegacyVersion(new LegacyVersion("1230_105", "",
                "Virtual Block 3 release sheet (2009-12-07), card #105", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
