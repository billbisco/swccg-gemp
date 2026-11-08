package com.gempukku.swccgo.cards.set2230.dark;

import com.gempukku.swccgo.cards.set1230.dark.Card1230_003;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 3
 * Type: Character
 * Title: Captain Yorr (V)
 * Original version of 1230_3: the Virtual Block 3 release sheet text (Dec 2009), "[Pilot] 2, 3: any starfighter" and "participating in battle".
 * The current text is the PC website card list (by Jul 2012, PC-E1). Plays the same as the current card; only the wording differs.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2230_003 extends Card1230_003 {
    public Card2230_003() {
        super();
        setGameText("[Pilot] 2, 3: any starfighter. If your starship participating in battle is about to be excluded (even if that starship may not participate in battle) or made lost by opponent, you may choose for it to be 'hit' instead.");
        setLegacyVersion(new LegacyVersion("1230_3", "",
                "Virtual Block 3 release sheet (2009-12-07), card #03", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
