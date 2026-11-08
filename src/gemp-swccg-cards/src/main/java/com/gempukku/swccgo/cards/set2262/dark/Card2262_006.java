package com.gempukku.swccgo.cards.set2262.dark;

import com.gempukku.swccgo.cards.set1262.dark.Card1262_006;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.2
 * Type: Effect
 * Title: Deployment Orders
 * Original version of 1262_6: the Virtual Block 6.2 release sheet text, (download icons; the PC card list, PC-E1, spells them out).
 * Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2262_006 extends Card1262_006 {
    public Card2262_006() {
        setGameText("Deploy on table. If your Landing Craft just landed, may use 1 Force to ▼ a [Presence] droid or Neimoidian aboard. Once per turn, may use 1 Force: reveal a [Theed Palace] vehicle (or Landing Craft) from hand to ▲ a [Presence] droid pilot (or vice versa), and deploy both simultaneously. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1262_6", "",
                "Virtual Block 6.2 release sheet (2009-12-07), card #2-06", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
