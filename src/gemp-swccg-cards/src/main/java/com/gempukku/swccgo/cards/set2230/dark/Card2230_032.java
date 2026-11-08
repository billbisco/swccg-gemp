package com.gempukku.swccgo.cards.set2230.dark;

import com.gempukku.swccgo.cards.set1230.dark.Card1230_032;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 3
 * Type: Effect
 * Title: Flagship Operations (V)
 * Original version of 1230_32: the Virtual Block 3 release sheet text (Dec 2009), "may add 3 to your total power" (the card list added "you").
 * The current text is the PC website card list (by Jul 2012, PC-E1). Plays the same as the current card; only the wording differs.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2230_032 extends Card1230_032 {
    public Card2230_032() {
        super();
        setGameText("Deploy on a system. Your TIEs may relocate (as a regular move) to a system from 0 to 3 parsecs away. For each battleground system you control with two TIEs piloted by Imperials, may add 3 to your total power in battles. Opponent's Force drain is +3 here. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1230_32", "",
                "Virtual Block 3 release sheet (2009-12-07), card #30", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
