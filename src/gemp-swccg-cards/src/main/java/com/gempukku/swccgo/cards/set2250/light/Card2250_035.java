package com.gempukku.swccgo.cards.set2250.light;

import com.gempukku.swccgo.cards.set1250.light.Card1250_035;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Type: Character
 * Title: Clone Pilot
 * Original version of 1250_35: the Virtual Block 5 release sheet text ("[Pilot] 2", printed "manuever"; the PC
 * card list, PC-E1, spells out the pilot icon and fixes the typo). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2250_035 extends Card1250_035 {
    public Card2250_035() {
        setGameText("[Pilot] 2. While piloting a [Republic] starship at a battleground, forfeit +1 and your Force drains are +1 here. Anything he pilots is manuever +1 and immune to attrition < 3 (< 5 if all your pilot characters here are clones and/or Jedi).");
        setLegacyVersion(new LegacyVersion("1250_35", "",
                "Virtual Block 5 release sheet (2009-12-07), card #37", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
