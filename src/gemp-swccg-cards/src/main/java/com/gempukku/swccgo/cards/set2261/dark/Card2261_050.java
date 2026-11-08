package com.gempukku.swccgo.cards.set2261.dark;

import com.gempukku.swccgo.cards.set1261.dark.Card1261_050;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Type: Interrupt
 * Title: TIE Sentry Ships (V)
 * Original version of 1261_50: the Virtual Block 6.1 release sheet text, "cancel and redraw that destiny" (the PC
 * card list, PC-E1 and now the current card, says "the destiny"). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_050 extends Card1261_050 {
    public Card2261_050() {
        setGameText("USED: If you just drew a unique (•) Imperial starship for destiny, take that starship into hand to cancel and redraw that destiny. LOST: Use 3 Force to retrieve a unique (•) Imperial starship into hand.");
        setLegacyVersion(new LegacyVersion("1261_50", "",
                "Virtual Block 6.1 release sheet (2009-09-04), card #49", LocalDate.of(2009, 9, 4), LocalDate.of(2012, 7, 21)));
    }
}
