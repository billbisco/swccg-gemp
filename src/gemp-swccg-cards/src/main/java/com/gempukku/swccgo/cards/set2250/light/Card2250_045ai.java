package com.gempukku.swccgo.cards.set2250.light;

import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Type: Character
 * Title: Mace Windu (V)
 * Original version of the AI printing 1250_45ai (Virtual Block 5, card #44EAI): "During battle, Mace may fire one weapon
 * twice". Same text and play as 2250_45 (the original of 1250_45); its own card for the AI image, which shows this text.
 * An AI printing is the same card as its base printing. Anything Goes only (docs/legacy-versions.md, "AI printings").
 */
public class Card2250_045ai extends Card2250_045 {
    public Card2250_045ai() {
        super();
        setLegacyVersion(new LegacyVersion("1250_45ai", "",
                "Virtual Block 5 release (2009-12-07), AI card #44EAI", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
