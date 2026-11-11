package com.gempukku.swccgo.cards.set2261.light;

import com.gempukku.swccgo.cards.set1261.light.Card1261_098;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Type: Interrupt
 * Title: •Out Of Somewhere (V)
 * PC-E1 version of 1261_98 (Batch R8-U5, Bill 2026-11-11: every publication is a version; PC card
 * images are versions at their proven date): PC card image (in the V-kit by 2009-12-09). Order so far: original sheet (2009-09-04) -> PC-E1 pc_vkit (2009-12-09); the current card is the
 * Virtual Cards Master List (2013-04-27). Same play as the current card; only the wording differs.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_3098 extends Card1261_098 {
    public Card2261_3098() {
        super();
        setGameText("Use 1 Force to ▼ (or retrieve) Lando. OR If a battle just initiated, and your Lando defending alone at a site, his forfeit cannot be reduced, he adds one destiny to power (+1 for each character and vehicle present) and draws one battle destiny if not able to otherwise. (Immune to Sense.)");
        setLegacyVersion(new LegacyVersion("1261_98", "PC-E1",
                "PC card image (in the V-kit by 2009-12-09)", LocalDate.of(2009, 12, 9), LocalDate.of(2012, 7, 21)));
    }
}
