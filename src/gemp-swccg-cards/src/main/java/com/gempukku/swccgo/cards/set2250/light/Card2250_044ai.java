package com.gempukku.swccgo.cards.set2250.light;

import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Type: Character
 * Title: Master Qui-Gon (V)
 * Pre-errata version of the AI printing 1250_44ai (Virtual Block 5, card #45AI): "Immune to Disarmed" (the card Disarmed
 * only). Same text and play as 2250_44 (the original of 1250_44); its own card for the AI image. The PC card list still
 * showed this text for the AI printing until 2013-01-11 (the base printing changed by 2012-07-21); the AI took the
 * errata with the Virtual Cards Master List (2013-04-27), so this version is superseded then (legacy batch B16).
 * An AI printing is the same card as its base printing. Anything Goes only (docs/legacy-versions.md, "AI printings").
 */
public class Card2250_044ai extends Card2250_044 {
    public Card2250_044ai() {
        super();
        setLegacyVersion(new LegacyVersion("1250_44ai", "",
                "Virtual Block 5 release (2009-12-07), AI card #45AI", LocalDate.of(2009, 12, 7), LocalDate.of(2013, 4, 27)));
    }
}
