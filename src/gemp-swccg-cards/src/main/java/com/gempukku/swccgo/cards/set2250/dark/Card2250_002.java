package com.gempukku.swccgo.cards.set2250.dark;

import com.gempukku.swccgo.cards.set1250.dark.Card1250_002;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Type: Character
 * Title: Aurra Sing (V)
 * Original version of 1250_2: the Virtual Block 5 release sheet text, "same site as a Jedi" (the PC card list,
 * PC-E1 and now the current card, says "a site where opponent has a Jedi"). Same play: Jedi are Light Side characters.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2250_002 extends Card1250_002 {
    public Card2250_002() {
        setGameText("Once during opponent's activate phase, may relocate Aurra to same site as a Jedi. Once per game, during your move phase, may fire her weapon at a Jedi; hit target is lost. Immune to attrition < 4.");
        setLegacyVersion(new LegacyVersion("1250_2", "",
                "Virtual Block 5 release sheet (2009-12-07), card #02", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
