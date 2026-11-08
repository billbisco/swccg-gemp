package com.gempukku.swccgo.cards.set2263.dark;

import com.gempukku.swccgo.cards.set601.dark.Card601_008;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.3
 * Type: Character
 * Title: IG-88, Renegade Droid
 * Original version of 1263_3: the Virtual Block 6.3 release sheet text, "may draw a destiny" (the PC card list, PC-E1, says "may draw destiny" and spells out the pilot icon).
 * Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2263_003 extends Card601_008 {
    public Card2263_003() {
        setGameText("[Pilot] 2, 3: IG-2000. Deploys -3 aboard IG-2000. Adds one battle destiny if alone or with a smuggler. During battle, if piloting a starship, may draw a destiny. Starship is immune to attrition < X, or its immunity is +X (limit +2), where X = destiny.");
        setLegacyVersion(new LegacyVersion("1263_3", "",
                "Virtual Block 6.3 release sheet (2010-03-15), card #3-04", LocalDate.of(2010, 3, 15), LocalDate.of(2012, 7, 21)));
    }
}
