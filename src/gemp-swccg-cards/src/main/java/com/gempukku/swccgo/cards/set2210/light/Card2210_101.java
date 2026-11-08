package com.gempukku.swccgo.cards.set2210.light;

import com.gempukku.swccgo.cards.set1210.light.Card1210_101;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 1
 * Type: Character
 * Title: Han Solo
 * Original version of 1210_101: the Virtual Block 1 release sheet text (Dec 2009, slip 99E); the PC website card list
 * (by 2012-07-21) has the current wording ("cancel Boba Fett's or Jabba's" became "cancel Boba Fett or Jabba's"). Plays the same as the current card; only the
 * wording differs. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2210_101 extends Card1210_101 {
    public Card2210_101() {
        super();
        setGameText("[Pilot] 3. Cards with 'Han' in title are destiny +3. May use 1 Force to cancel Boba Fett's or Jabba's game text for remainder of turn. If opponent just initiated battle here, may use 1 Force to take the first weapons segment action.");
        setLegacyVersion(new LegacyVersion("1210_101", "",
                "Virtual Block 1 release sheet (2009-12-07), card #99E; superseded by the PC card list text (by 2012-07-21)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
