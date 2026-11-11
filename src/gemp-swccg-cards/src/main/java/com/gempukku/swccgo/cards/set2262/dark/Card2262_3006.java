package com.gempukku.swccgo.cards.set2262.dark;

import com.gempukku.swccgo.cards.set1262.dark.Card1262_006;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.2
 * Type: Effect
 * Title: •Deployment Orders
 * PC-E1 version of 1262_6 (Batch R8-U5, Bill 2026-11-11: every publication is a version; PC card
 * images are versions at their proven date): PC card image (in the V-kit by 2009-12-09). Order so far: original sheet (2009-12-07) -> PC-E1 pc_vkit (2009-12-09); the current card is the
 * Virtual Cards Master List (2013-04-27). Same play as the current card; only the wording differs.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2262_3006 extends Card1262_006 {
    public Card2262_3006() {
        super();
        setGameText("Deploy on table. If your Landing Craft just landed, you may use 1 Force to ▼ a [Presence] droid or Neimoidian aboard. Once per turn, may use 1 Force: reveal a [Theed Palace] vehicle (or Landing Craft) from hand to ▲ a [Presence] droid pilot (or vice versa), and deploy both simultaneously. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1262_6", "PC-E1",
                "PC card image (in the V-kit by 2009-12-09)", LocalDate.of(2009, 12, 9), LocalDate.of(2012, 7, 21)));
    }
}
