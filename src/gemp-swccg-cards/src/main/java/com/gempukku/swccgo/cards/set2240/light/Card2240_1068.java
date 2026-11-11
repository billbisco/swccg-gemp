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
 * PC-E1 version of 1240_68: the PC website card list text (by 2012-07-21 to 2013-01-11; the list was generated
 * from the PC's Holotable card data). Then the PC card image (server upload 2013-02-25) and the Master List of 2013-04-27 (PC-E2, the current card) prints the
 * release-sheet wording again (Bill 2026-11-11: every publication is a version; batch R8). Same play. Anything
 * Goes only (docs/legacy-versions.md).
 */
public class Card2240_1068 extends Card1240_068 {
    public Card2240_1068() {
        super();
        setGameText("May be battled. If with Dash Rendar, adds one destiny to attrition only. Once per turn, if at a battleground, you may peek at the top card of opponent's Reserve Deck; may place that card on top of opponent's Used Pile.");
        setLegacyVersion(new LegacyVersion("1240_68", "PC-E1",
                "PC website card list (by 2012-07-21); superseded by the PC card image (server upload 2013-02-25)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
