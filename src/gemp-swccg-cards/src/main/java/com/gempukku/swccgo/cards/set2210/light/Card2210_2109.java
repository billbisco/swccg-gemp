package com.gempukku.swccgo.cards.set2210.light;

import com.gempukku.swccgo.cards.set1210.light.Card1210_109;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 1
 * Type: Character
 * Subtype: Droid
 * Title: R2-D2 (Artoo-Detoo)
 *
 * PC-E2 version of 1210_109: the PC card image (r2d2.gif), dated by its server upload (2013-02-25, a bulk upload;
 * the image may be older). It prints the release-sheet wording. The Master List of 2013-04-27 (the current card)
 * follows it (Bill 2026-11-11: a PC card image is its own version, placed at its upload date; batch R8). Same
 * play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2210_2109 extends Card1210_109 {
    public Card2210_2109() {
        super();
        setGameText("During battle with C-3PO, may not be targeted by weapons. While aboard a starfighter, it is power, maneuver, and hyperspeed +3. Unless you have 13 or more cards in hand, opponent may not place those cards in Used Pile.");
        setLegacyVersion(new LegacyVersion("1210_109", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
