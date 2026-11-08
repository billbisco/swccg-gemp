package com.gempukku.swccgo.cards.set2240.dark;

import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 4
 * Type: Character
 * Title: Galen Marek, Starkiller
 * Original version of the AI printing 1240_8ai (Virtual Block 4, card #04AI): "while with Fallen Jedi ... (or defending with
 * Black Leader)". Same text and play as 2240_8 (the original of 1240_8); its own card for the AI image.
 * An AI printing is the same card as its base printing. Anything Goes only (docs/legacy-versions.md, "AI printings").
 */
public class Card2240_008ai extends Card2240_008 {
    public Card2240_008ai() {
        super();
        setLegacyVersion(new LegacyVersion("1240_8ai", "",
                "Virtual Block 4 release (2009-12-07), card #04AI (printed as Galen, Secret Apprentice (V) AI)", LocalDate.of(2009, 12, 7), LocalDate.of(2013, 4, 27)));
    }
}
