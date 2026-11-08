package com.gempukku.swccgo.cards.set2230.dark;

import com.gempukku.swccgo.cards.set1230.dark.Card1230_002;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 3
 * Type: Character
 * Title: Bib Fortuna (V)
 * Original version of 1230_2: the Virtual Block 3 release sheet text (Dec 2009), "may place three cards" (the card list added "you").
 * The current text is the PC website card list (by Jul 2012, PC-E1). Plays the same as the current card; only the wording differs.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2230_002 extends Card1230_002 {
    public Card2230_002() {
        super();
        setGameText("Opponent's non-Jedi characters are deploy +1 to [Jabba's Palace] sites. Once during your control phase, may place three cards from hand in Used Pile to reveal top card of Reserve Deck; if it is an alien, take it into hand.");
        setLegacyVersion(new LegacyVersion("1230_2", "",
                "Virtual Block 3 release sheet (2009-12-07), card #02", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
