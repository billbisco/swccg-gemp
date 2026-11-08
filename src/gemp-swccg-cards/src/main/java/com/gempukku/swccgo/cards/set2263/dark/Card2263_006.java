package com.gempukku.swccgo.cards.set2263.dark;

import com.gempukku.swccgo.cards.set1263.dark.Card1263_006;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.3
 * Type: Character
 * Title: Mercenary Shadow Pirate
 * PC-E1 version of 1263_6: the Virtual Cards Master List wording (2013-04-27), Sandwhirl named first; same play as the Mar 2010 sheet text GEMP's current card shows.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2263_006 extends Card1263_006 {
    public Card2263_006() {
        setGameText("[Pilot] 2. Opponent may not add destiny draws to power or attrition here. Sandwhirl and Ice Storm are canceled at same and related locations.");
        setLegacyVersion(new LegacyVersion("1263_6", "PC-E1",
                "Virtual Cards Master List (2013-04-27)", LocalDate.of(2013, 4, 27), null));
    }
}
