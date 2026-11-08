package com.gempukku.swccgo.cards.set2210.dark;

import com.gempukku.swccgo.cards.set1210.dark.Card1210_063;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 1
 * Type: Interrupt
 * Title: Local Trouble
 * Original version of 1210_63: the Virtual Block 1 release sheet text (Dec 2009, slip 63); the PC website card list
 * (by 2012-07-21) has the current wording ("to initiate a battle" became "to initate a battle"). Plays the same as the current card; only the
 * wording differs. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2210_063 extends Card1210_063 {
    public Card2210_063() {
        super();
        setGameText("At the beginning of your battle phase, use 1 Force to initiate a battle between two Imperials and an opponent's character present at same interior site. For remainder of turn, no other battles may occur here and opponent takes no battle damage here.");
        setLegacyVersion(new LegacyVersion("1210_63", "",
                "Virtual Block 1 release sheet (2009-12-07), card #63; superseded by the PC card list text (by 2012-07-21)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
