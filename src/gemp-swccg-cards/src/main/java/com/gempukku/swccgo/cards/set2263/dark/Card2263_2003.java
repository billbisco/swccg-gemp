package com.gempukku.swccgo.cards.set2263.dark;

import com.gempukku.swccgo.cards.set601.dark.Card601_008;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.3
 * Type: Character
 * Title: •IG-88, Renegade Droid
 * Original version of 1263_3 (Batch R8-U, Bill 2026-11-11: every publication is a version): the text of the
 * Virtual Block 6.3 release sheet (2010-03-12), card #304. Later versions: PC website card list (PC-E1, by 2012-07-21, 2263_1003), PC card image (PC-E2, on the PC server by 2013-02-25, 2263_3003) and Virtual Cards Master List (PC-E3, 2013-04-27, the current card).
 * The PC card image and the Master List print these words again, so this version has the same text and play as the
 * current card. Id 2263_3 was retired on 2026-11-08 (it aliases the current card in blueprintMapping), so this original takes 2263_2003. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2263_2003 extends Card601_008 {
    public Card2263_2003() {
        super();
        setLegacyVersion(new LegacyVersion("1263_3", "",
                "Virtual Block 6.3 release sheet (2010-03-12), card #304; superseded by the PC card list text (by 2012-07-21)", LocalDate.of(2010, 3, 12), LocalDate.of(2012, 7, 21)));
    }
}
