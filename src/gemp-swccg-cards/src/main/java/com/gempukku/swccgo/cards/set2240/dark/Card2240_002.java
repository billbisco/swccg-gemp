package com.gempukku.swccgo.cards.set2240.dark;

import com.gempukku.swccgo.cards.set601.dark.Card601_089;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 4
 * Type: Character
 * Title: Juno Eclipse, Black Leader
 * Original version of 1240_2: the Virtual Block 4 release sheet text (Dec 2009, printed as Black Leader (V)), "Naboo Blaster is a matching weapon for Black Leader." (the Virtual Cards Master List, Apr 2013, reads "for her"). The AI printing 1240_3 has its own version, 2240_3.
 * The current text is the Virtual Cards Master List (Apr 2013, PC-E1). Plays the same as the current card; only the wording differs.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2240_002 extends Card601_089 {
    public Card2240_002() {
        super();
        setGameText("Adds 3 to power of anything she pilots.  Deploys -1 to starships.  Naboo Blaster is a matching weapon for Black Leader.  While with Galen at a site, adds one battle destiny.  During your control phase, may use 2 Force to retrieve a card with 'back' in title.  Immune to attrition < 3.");
        setLegacyVersion(new LegacyVersion("1240_2", "",
                "Virtual Block 4 release sheet (2009-12-07), card #02", LocalDate.of(2009, 12, 7), LocalDate.of(2013, 4, 27)));
        setPrintedTitle("Black Leader (V)");
    }
}
