package com.gempukku.swccgo.cards.set2220.light;

import com.gempukku.swccgo.cards.set1220.light.Card1220_123;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 2
 * Type: Effect
 * Title: Frostbite
 *
 * PC-E2 version of 1220_123: the PC card image (frostbite.gif), dated by its server upload (2013-02-25, a bulk
 * upload; the image may be older). It prints the release-sheet wording. The Master List of 2013-04-27 (the current
 * card) follows it (Bill 2026-11-11: a PC card image is its own version, placed at its upload date; batch R8).
 * Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2220_2123 extends Card1220_123 {
    public Card2220_2123() {
        super();
        setGameText("Deploy on Hoth system if you control two exterior Hoth sites. During your control phase, may retrieve 1 Force for each Hoth site you control with a unique (•) combat vehicle. If opponent controls this system, this Effect lost. While you occupy 1st marker, [Immune to Alter]");
        setLegacyVersion(new LegacyVersion("1220_123", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
