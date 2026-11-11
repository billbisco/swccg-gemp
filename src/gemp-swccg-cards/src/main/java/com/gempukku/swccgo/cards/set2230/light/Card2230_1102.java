package com.gempukku.swccgo.cards.set2230.light;

import com.gempukku.swccgo.cards.set1230.light.Card1230_102;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 3
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Diversionary Tactics
 *
 * PC-E1 version of 1230_102: the PC website card list text (by 2012-07-21 to 2013-01-11; the list was generated
 * from the PC's Holotable card data). Then the PC card image (server upload 2013-02-25) and the Master List of 2013-04-27 (PC-E2, the current card) prints the
 * release-sheet wording again (Bill 2026-11-11: every publication is a version; batch R8). Same play. Anything
 * Goes only (docs/legacy-versions.md).
 */
public class Card2230_1102 extends Card1230_102 {
    public Card2230_1102() {
        super();
        setGameText("USED: Target one piloted starship at a system. That starship is hyperspeed -1 for remainder of turn. LOST: If you have 2 unique (•) piloted Rebel (or [Republic]) starfighters in battle at a system, draw destiny. If destiny < 4, exclude one starship from that battle.");
        setLegacyVersion(new LegacyVersion("1230_102", "PC-E1",
                "PC website card list (by 2012-07-21); superseded by the PC card image (server upload 2013-02-25)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
