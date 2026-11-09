package com.gempukku.swccgo.cards.set2230.light;

import com.gempukku.swccgo.cards.set601.light.Card601_158;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 3
 * Type: Interrupt
 * Title: Rebel Leadership (V)
 * Original version of 1230_109: the Virtual Block 3 release sheet text (Dec 2009), "(that destiny may not be canceled)",
 * reworded on the Virtual Cards Master List (Apr 2013) to "(their battle destiny draws may not be canceled)". Plays the same as the current card; only the wording differs. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2230_109 extends Card601_158 {
    public Card2230_109() {
        super();
        setGameText("▲ an admiral or a non-Jedi general. OR Once per game, ▼ a war room. OR If your admiral or non-[Episode I] general is in battle, may add one battle destiny or prevent opponent from drawing more than one battle destiny (that destiny may not be canceled).");
        setLegacyVersion(new LegacyVersion("1230_109", "",
                "Virtual Block 3 release sheet (2009-12-07), card #110", LocalDate.of(2009, 12, 7), LocalDate.of(2013, 4, 13)));
    }
}
