package com.gempukku.swccgo.cards.set2220.light;

import com.gempukku.swccgo.cards.set601.light.Card601_228;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 2
 * Type: Character
 * Title: Lobot (V)
 * Original version of 1220_95 (class 601_228): the Virtual Block 2 release sheet (2009-12-07) text.
 * Superseded by the PC website card list text (PC-E1, by 2012-07-21: "as 'react'" for "as a ‘react'" (no "a")), the current card.
 * Plays the same as the current card; only the wording differs. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2220_095 extends Card601_228 {
    public Card2220_095() {
        super();
        setGameText("Deploys free to Cloud City or a site with a scomp link. If a battle was just initiated at same site, may [download] an alien here (free if Lando) as a 'react'. May [download] Cyborg Construct on Lobot.");
        setLegacyVersion(new LegacyVersion("1220_95", "",
                "Virtual Block 2 release sheet (2009-12-07)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
