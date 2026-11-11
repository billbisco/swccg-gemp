package com.gempukku.swccgo.cards.set2250.dark;

import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Type: Character
 * Title: Aurra Sing (V)
 * PC-E1 version of the AI printing 1250_2ai: the PC website card list text (by 2012-07-21), "a site where opponent has a
 * Jedi". Same text and play as 2250_1002; its own card for the AI printing (CardMaker image of this text).
 * An AI printing is the same card as its base printing. Anything Goes only (docs/legacy-versions.md, "AI printings").
 */
public class Card2250_1002ai extends Card2250_1002 {
    public Card2250_1002ai() {
        super();
        setLegacyVersion(new LegacyVersion("1250_2ai", "PC-E1",
                "PC website card list (by 2012-07-21), AI printing", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
