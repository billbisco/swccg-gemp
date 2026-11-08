package com.gempukku.swccgo.cards.set2210.light;

import com.gempukku.swccgo.cards.set1210.light.Card1210_092;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 1
 * Type: Character
 * Title: C-3PO (See-Threepio)
 * Original version of 1210_92: the Virtual Block 1 release sheet text (Dec 2009, slip 92); the PC website card list
 * (by 2012-07-21) has the current wording ("During battle at a site" became "During a battle at a site"). Plays the same as the current card; only the
 * wording differs. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2210_092 extends Card1210_092 {
    public Card2210_092() {
        super();
        setGameText("During battle at a site with R2-D2, may not be targeted by weapons, and may draw one destiny; subtract that amount from opponent's total power. Once per game, may place all your non-[Immune to Alter] Effects on table in Used Pile.");
        setLegacyVersion(new LegacyVersion("1210_92", "",
                "Virtual Block 1 release sheet (2009-12-07), card #92; superseded by the PC card list text (by 2012-07-21)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
