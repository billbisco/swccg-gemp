package com.gempukku.swccgo.cards.set2250.light;

import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Type: Character
 * Title: Master Qui-Gon (V)
 * Pre-errata version of the AI printing 1250_44ai (Virtual Block 5, card #45AI): "Immune to Disarmed" (the card Disarmed
 * only). Same text and play as 2250_44 (the original of 1250_44); its own card for the AI image.
 * An AI printing is the same card as its base printing. Anything Goes only (docs/legacy-versions.md, "AI printings").
 */
public class Card2250_044ai extends Card2250_044 {
    public Card2250_044ai() {
        super();
        setLegacyVersion(new LegacyVersion("1250_44ai", "",
                "Virtual Block 5 release (2009-12-07), AI card #45AI", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
