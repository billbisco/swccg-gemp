package com.gempukku.swccgo.cards.set2250.light;

import com.gempukku.swccgo.cards.set1250.light.Card1250_041;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Type: Character
 * Title: Jedi Survivor
 * Original version of 1250_41: the Virtual Block 5 release sheet text (maintenance icons; the PC card list,
 * PC-E1, spells them out). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2250_041 extends Card1250_041 {
    public Card2250_041() {
        setGameText("When deployed, you may not initiate battle here this turn. While at opponent's site, adds one [Light Side Force] and one [Dark Side Force]. May 'swing' lightsabers for free. May never be won in sabacc. Immune to attrition < 5. End of your turn: [Maintenance] Use 1 or [Recycle] Lose 1 or [Skull].");
        setLegacyVersion(new LegacyVersion("1250_41", "",
                "Virtual Block 5 release sheet (2009-12-07), card #41", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
