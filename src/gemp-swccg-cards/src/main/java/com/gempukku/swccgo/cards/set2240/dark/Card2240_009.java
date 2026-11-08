package com.gempukku.swccgo.cards.set2240.dark;

import com.gempukku.swccgo.cards.set1240.dark.Card1240_009;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 4
 * Type: Character
 * Title: Imperial Stormtrooper
 * Original version of 1240_9: the Virtual Block 4 release sheet text (Dec 2009, slip 07); the PC website card list
 * (by 2012-07-21) has the current wording ("may take the other into hand" became "may take other into hand"). Plays the same as the current card; only the
 * wording differs. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2240_009 extends Card1240_009 {
    public Card2240_009() {
        super();
        setGameText("Clone. Blaster Rifle deploys (and fires) for free on this trooper. When this trooper fires Blaster Rifle, may draw two weapon destiny and choose one (may take the other into hand). Immune to Rebel Barrier.");
        setLegacyVersion(new LegacyVersion("1240_9", "",
                "Virtual Block 4 release sheet (2009-12-07), card #07; superseded by the PC card list text (by 2012-07-21)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
