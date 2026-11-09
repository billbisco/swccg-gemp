package com.gempukku.swccgo.cards.set2263.dark;

import com.gempukku.swccgo.cards.set1263.dark.Card1263_006;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.3
 * Type: Character
 * Title: Mercenary Shadow Pirate
 * Original version of 1263_6: the Virtual Block 6.3 release sheet text, "Ice Storm and Sandwhirl" (the Virtual Cards
 * Master List, PC-E1 and now the current card, names Sandwhirl first). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2263_006 extends Card1263_006 {
    public Card2263_006() {
        setGameText("[Pilot] 2. Opponent may not add destiny draws to power or attrition here. Ice Storm and Sandwhirl are canceled at same and related locations.");
        setLegacyVersion(new LegacyVersion("1263_6", "",
                "Virtual Block 6.3 release sheet (released 2010-03-12), card #3-06", LocalDate.of(2010, 3, 12), LocalDate.of(2013, 4, 27)));
    }
}
