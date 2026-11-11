package com.gempukku.swccgo.cards.set2240.light;

import com.gempukku.swccgo.cards.set1240.light.Card1240_068;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 4
 * Type: Character
 * Subtype: Droid
 * Title: LE-BO2D9 (Leebo) (V)
 *
 * PC-E2 version of 1240_68: the PC card image (lebo2d9leebo.gif), dated by its server upload (2013-02-25, a bulk
 * upload; the image may be older). It prints the release-sheet wording. The Master List of 2013-04-27 (the current
 * card) follows it (Bill 2026-11-11: a PC card image is its own version, placed at its upload date; batch R8).
 * Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2240_2068 extends Card1240_068 {
    public Card2240_2068() {
        super();
        setGameText("May be battled. If with Dash Rendar, adds one destiny to attrition only. Once per turn, if at a battleground, you may peek at top card of opponent's Reserve Deck; may place that card on top of opponent's Used Pile.");
        setLegacyVersion(new LegacyVersion("1240_68", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
