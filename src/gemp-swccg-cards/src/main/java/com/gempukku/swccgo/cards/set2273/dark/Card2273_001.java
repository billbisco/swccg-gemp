package com.gempukku.swccgo.cards.set2273.dark;

import com.gempukku.swccgo.cards.set601.dark.Card601_178;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.3
 * Type: Character
 * Subtype: Droid
 * Title: 5D6-RA-7 (Fivedesix) (V)
 * Original version of 1273_1 (class 601_178): the Virtual Block 7.3 release sheet text (Jun 2011; surviving copy re-made
 * Jul 2011). Superseded by the PC website card list text (PC-E1, by 2012-07-21: "draws a destiny", "activate one force",
 * no comma after "0 or 7"), the current card. Plays the same as the current card; only the wording differs.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2273_001 extends Card601_178 {
    public Card2273_001() {
        super();
        setGameText("[Pilot] 2. Power +1 while with your leader. If at a Scomp link when opponent draws destiny of: 1-3, you may activate 1 Force; 4-6, you may draw top card of your Reserve Deck; 0 or 7, you may retrieve 1 Force.");
        setLegacyVersion(new LegacyVersion("1273_1", "",
                "Virtual Block 7.3 release sheet (2011-06-03; surviving copy re-made 2011-07-25)", LocalDate.of(2011, 6, 3), LocalDate.of(2012, 7, 21)));
    }
}
