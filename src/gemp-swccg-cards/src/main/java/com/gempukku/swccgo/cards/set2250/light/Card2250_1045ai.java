package com.gempukku.swccgo.cards.set2250.light;

import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Type: Character
 * Subtype: Jedi Master
 * Title: Mace Windu (V)
 *
 * PC-E1 version of the AI printing 1250_45ai: same text and play as 2250_1045 (the card-list version of 1250_45);
 * its own card for the AI printing. Anything Goes only (docs/legacy-versions.md, "AI printings").
 */
public class Card2250_1045ai extends Card2250_1045 {
    public Card2250_1045ai() {
        super();
        setLegacyVersion(new LegacyVersion("1250_45ai", "PC-E1",
                "PC website card list (by 2012-07-21); superseded by the PC card image (server upload 2013-02-25)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
