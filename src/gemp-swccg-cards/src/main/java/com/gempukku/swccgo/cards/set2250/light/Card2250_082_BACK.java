package com.gempukku.swccgo.cards.set2250.light;

import com.gempukku.swccgo.cards.set1250.light.Card1250_082_BACK;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Type: Objective
 * Title: Duel Of The Fates (V)
 * Back side of 2250_82, the pre-errata We'll Handle This (V). Its text is unchanged from the current back
 * (1250_82_BACK). Anything Goes only (docs/legacy-versions.md).
 */
public class Card2250_082_BACK extends Card1250_082_BACK {
    public Card2250_082_BACK() {
        setLegacyVersion(new LegacyVersion("1250_82", "",
                "Virtual Block 5 release sheet (2009-12-07), card #79; superseded by the PC forum errata (2012-08-04)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 8, 4)));
    }
}
