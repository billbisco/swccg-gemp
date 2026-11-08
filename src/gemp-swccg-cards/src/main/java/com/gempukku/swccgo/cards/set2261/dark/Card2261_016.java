package com.gempukku.swccgo.cards.set2261.dark;

import com.gempukku.swccgo.cards.set1261.dark.Card1261_016;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Type: Character
 * Title: Lieutenant Cecius (V)
 * Original version of 1261_16: the Virtual Block 6.1 release sheet text, "(and no other Force drain bonus)" (the
 * PC card list, PC-E1 and now the current card, says "no other bonus to your Force drain"). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_016 extends Card1261_016 {
    public Card2261_016() {
        setGameText("Opponent's characters are deploy +1 to same mobile site. At battlegrounds where you have a unique (•) and non-unique Imperial present (and no other Force drain bonus), your Force drains are +1. Tactical Support is a Used Interrupt.");
        setLegacyVersion(new LegacyVersion("1261_16", "",
                "Virtual Block 6.1 release sheet (2009-09-04), card #12", LocalDate.of(2009, 9, 4), LocalDate.of(2012, 7, 21)));
    }
}
