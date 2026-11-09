package com.gempukku.swccgo.cards.set2210.light;

import com.gempukku.swccgo.cards.set1210.light.Card1210_109;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 1
 * Type: Character
 * Title: R2-D2 (Artoo-Detoo)
 * Original version of 1210_109: the Virtual Block 1 release sheet (2009-12-07) text.
 * Superseded by the PC website card list text (PC-E1, by 2012-07-21: "During a battle" for "During battle"), the current card.
 * Plays the same as the current card; only the wording differs. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2210_109 extends Card1210_109 {
    public Card2210_109() {
        super();
        setGameText("During battle with C-3PO, may not be targeted by weapons. While aboard a starfighter, it is power, maneuver, and hyperspeed +3. Unless you have 13 or more cards in hand, opponent may not place those cards in Used Pile.");
        setLegacyVersion(new LegacyVersion("1210_109", "",
                "Virtual Block 1 release sheet (2009-12-07)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
