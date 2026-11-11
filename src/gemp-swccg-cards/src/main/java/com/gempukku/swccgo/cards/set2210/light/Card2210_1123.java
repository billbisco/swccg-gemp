package com.gempukku.swccgo.cards.set2210.light;

import com.gempukku.swccgo.cards.set1210.light.Card1210_123;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 1
 * Type: Effect
 * Title: Hidden Fortress
 *
 * PC-E1 version of 1210_123: the PC website card list text (by 2012-07-21 to 2013-01-11; the list was generated
 * from the PC's Holotable card data). Then the PC card image (server upload 2013-02-25) and the Master List of 2013-04-27 (PC-E2, the current card) prints the
 * release-sheet wording again (Bill 2026-11-11: every publication is a version; batch R8). Same play. Anything
 * Goes only (docs/legacy-versions.md).
 */
public class Card2210_1123 extends Card1210_123 {
    public Card2210_1123() {
        super();
        setGameText("Deploy on table (only at start of game) with up to three systems from outside your deck face-down under here. If non-[Virtual Block 4] Hidden Base on table: Once per game, may deploy a system from here and/or exchange Hidden Base indicator with a system (from parsec 1 to 8) under here. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1210_123", "PC-E1",
                "PC website card list (by 2012-07-21); superseded by the PC card image (server upload 2013-02-25)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
