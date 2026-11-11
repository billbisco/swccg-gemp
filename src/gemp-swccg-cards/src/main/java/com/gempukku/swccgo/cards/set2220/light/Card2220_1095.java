package com.gempukku.swccgo.cards.set2220.light;

import com.gempukku.swccgo.cards.set601.light.Card601_228;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 2
 * Type: Character
 * Subtype: Alien
 * Title: Lobot (V)
 *
 * PC-E1 version of 1220_95: the PC website card list text (by 2012-07-21 to 2013-01-11; the list was generated
 * from the PC's Holotable card data). Then the PC card image (server upload 2013-02-25) and the Master List of 2013-04-27 (PC-E2, the current card) prints the
 * release-sheet wording again (Bill 2026-11-11: every publication is a version; batch R8). Same play. Anything
 * Goes only (docs/legacy-versions.md).
 */
public class Card2220_1095 extends Card601_228 {
    public Card2220_1095() {
        super();
        setGameText("Deploys free to Cloud City or a site with a scomp link. If a battle was just initiated at same site, may deploy an alien here (free if Lando) as 'react' from Reserve Deck; reshuffle. May deploy Cyborg Construct on Lobot from Reserve Deck; reshuffle.");
        setLegacyVersion(new LegacyVersion("1220_95", "PC-E1",
                "PC website card list (by 2012-07-21); superseded by the PC card image (server upload 2013-02-25)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
