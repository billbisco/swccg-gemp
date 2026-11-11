package com.gempukku.swccgo.cards.set2210.light;

import com.gempukku.swccgo.cards.set1210.light.Card1210_092;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 1
 * Type: Character
 * Subtype: Droid
 * Title: C-3PO (See-Threepio)
 *
 * PC-E2 version of 1210_92: the PC card image (c3po.gif), dated by its server upload (2013-02-25, a bulk upload;
 * the image may be older). It prints the release-sheet wording. The Master List of 2013-04-27 (the current card)
 * follows it (Bill 2026-11-11: a PC card image is its own version, placed at its upload date; batch R8). Same
 * play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2210_2092 extends Card1210_092 {
    public Card2210_2092() {
        super();
        setGameText("During battle at a site with R2-D2, may not be targeted by weapons, and may draw one destiny; subtract that amount from opponent's total power. Once per game, may place all your non-[Immune to Alter] Effects on table in Used Pile.");
        setLegacyVersion(new LegacyVersion("1210_92", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
