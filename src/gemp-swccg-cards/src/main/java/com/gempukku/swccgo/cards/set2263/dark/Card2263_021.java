package com.gempukku.swccgo.cards.set2263.dark;

import com.gempukku.swccgo.cards.set1263.dark.Card1263_021;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.3
 * Type: Vehicle
 * Title: Droid Walker
 * Original version of 1263_21: the Virtual Block 6.3 release sheet text, "targets it hits may not be used to satisfy
 * attrition" (the PC card list, PC-E1 and now the current card, says "cannot be used"). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2263_021 extends Card1263_021 {
    public Card2263_021() {
        setGameText("Deploys -1 to same site as your MTT. AT-AT Cannon may deploy aboard, and targets it hits may not be used to satisfy attrition. Unless alone, draws one battle destiny if not able to otherwise.");
        setLegacyVersion(new LegacyVersion("1263_21", "",
                "Virtual Block 6.3 release sheet (2010-03-15), card #3-21", LocalDate.of(2010, 3, 15), LocalDate.of(2012, 7, 21)));
    }
}
