package com.gempukku.swccgo.cards.set2220.light;

import com.gempukku.swccgo.cards.set1220.light.Card1220_123;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 2
 * Type: Effect
 * Title: Frostbite
 *
 * PC-E1 version of 1220_123: the PC website card list text (by 2012-07-21 to 2013-01-11; the list was generated
 * from the PC's Holotable card data). Then the PC card image (server upload 2013-02-25) and the Master List of 2013-04-27 (PC-E2, the current card) prints the
 * release-sheet wording again (Bill 2026-11-11: every publication is a version; batch R8). Same play. Anything
 * Goes only (docs/legacy-versions.md).
 */
public class Card2220_1123 extends Card1220_123 {
    public Card2220_1123() {
        super();
        setGameText("Deploy on the Hoth system if you control two exterior Hoth sites. During your control phase, may retrieve 1 Force for each Hoth site you control with a unique (•) combat vehicle. If opponent controls this system, this Effect lost. While you occupy 1st marker, [Immune to Alter]");
        setLegacyVersion(new LegacyVersion("1220_123", "PC-E1",
                "PC website card list (by 2012-07-21); superseded by the PC card image (server upload 2013-02-25)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
