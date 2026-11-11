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
 * PC-E2 version of 1220_95: the PC card image (lobot.gif), dated by its server upload (2013-02-25, a bulk upload;
 * the image may be older). It prints the release-sheet wording. The Master List of 2013-04-27 (the current card)
 * follows it (Bill 2026-11-11: a PC card image is its own version, placed at its upload date; batch R8). Same
 * play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2220_2095 extends Card601_228 {
    public Card2220_2095() {
        super();
        setGameText("Deploys free to Cloud City or a site with a scomp link. If a battle was just initiated at same site, may [download] an alien here (free if Lando) as a 'react'. May [download] Cyborg Construct on Lobot.");
        setLegacyVersion(new LegacyVersion("1220_95", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
