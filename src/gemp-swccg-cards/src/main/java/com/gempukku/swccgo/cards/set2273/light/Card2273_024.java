package com.gempukku.swccgo.cards.set2273.light;

import com.gempukku.swccgo.cards.set601.light.Card601_222;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.3
 * Type: Character
 * Subtype: Rebel
 * Title: Derek 'Hobbie' Klivian (V)
 * Original version of 1273_24 (class 601_222): the Virtual Block 7.3 release sheet text (Jun 2011; surviving copy
 * re-made Jul 2011). Superseded by the PC website card list text (PC-E1, by 2012-07-21: "X-Wing", "target hit and it's
 * forfeit = 0", "destiny + 1"), the current card. Plays the same as the current card; only the wording differs.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2273_024 extends Card601_222 {
    public Card2273_024() {
        super();
        setGameText("[Pilot] 2. While piloting Rogue 4 (or any X-wing), draws one battle destiny if unable to otherwise. Permanent weapon is Rogue Blaster (may target a character or creature for free; draw destiny; target hit, and its forfeit = 0, if destiny +1 > defense value).");
        setLegacyVersion(new LegacyVersion("1273_24", "",
                "Virtual Block 7.3 release sheet (2011-06-03; surviving copy re-made 2011-07-25)", LocalDate.of(2011, 6, 3), LocalDate.of(2012, 7, 21)));
    }
}
