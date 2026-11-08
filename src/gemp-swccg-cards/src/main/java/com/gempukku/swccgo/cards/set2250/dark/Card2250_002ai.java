package com.gempukku.swccgo.cards.set2250.dark;

import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Type: Character
 * Title: Aurra Sing (V)
 * Original version of the AI printing 1250_2ai (Virtual Block 5, card #02AI): "relocate Aurra to same site as a Jedi". Same
 * text and play as 2250_2 (the original of 1250_2); its own card for the AI image, which shows this text.
 * An AI printing is the same card as its base printing. Anything Goes only (docs/legacy-versions.md, "AI printings").
 */
public class Card2250_002ai extends Card2250_002 {
    public Card2250_002ai() {
        super();
        setLegacyVersion(new LegacyVersion("1250_2ai", "",
                "Virtual Block 5 release (2009-12-07), AI card #02AI", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
