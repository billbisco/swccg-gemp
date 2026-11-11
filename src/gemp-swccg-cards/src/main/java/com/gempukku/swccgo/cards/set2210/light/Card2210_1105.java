package com.gempukku.swccgo.cards.set2210.light;

import com.gempukku.swccgo.cards.set601.light.Card601_229;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 1
 * Type: Character
 * Title: •Luke Skywalker (V)
 * PC-E1 version of 1210_105 (Batch R8-U): the PC website card list text (by 2012-07-21; the list was generated from the
 * Holotable card data), after the original (2210_2105) and before the PC card image (on the PC server by 2013-02-25, PC-E2, 2210_3105), which prints the original words again.
 * Against the original: one change: "one" before "Darklighter Spin" is dropped.
 * Written-out icons in the list are notation, so the text here uses the icons. Same play as the current card.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2210_1105 extends Card601_229 {
    public Card2210_1105() {
        super();
        setGameText("[Pilot] 3. If piloting lead starfighter, Attack Run total is +3. During your control phase, if piloting at a battleground, may [upload] Darklighter Spin or retrieve 1 Force.");
        setLegacyVersion(new LegacyVersion("1210_105", "PC-E1",
                "PC website card list (by 2012-07-21)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
