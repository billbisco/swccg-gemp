package com.gempukku.swccgo.cards.set2230.light;

import com.gempukku.swccgo.cards.set1230.light.Card1230_113;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 3
 * Type: Starship
 * Title: Red Squadron 7 (V)
 * Original version of 1230_113: the Virtual Block 3 release sheet text (Dec 2009), "if either piloting" (the card list reads "while").
 * The current text is the PC website card list (by Jul 2012, PC-E1). Plays the same as the current card; only the wording differs.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2230_113 extends Card1230_113 {
    public Card2230_113() {
        super();
        setGameText("Heroic. May add 1 pilot. Once per game may place a non-[Immune to Alter] Effect at same system in owner's Used Pile. Corran Horn and Keir Santage deploy -1 aboard; if either piloting, immune to attrition < 4.");
        setLegacyVersion(new LegacyVersion("1230_113", "",
                "Virtual Block 3 release sheet (2009-12-07), card #116", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
