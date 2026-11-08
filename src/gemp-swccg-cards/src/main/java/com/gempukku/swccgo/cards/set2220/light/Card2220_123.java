package com.gempukku.swccgo.cards.set2220.light;

import com.gempukku.swccgo.cards.set1220.light.Card1220_123;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 2
 * Type: Effect
 * Title: Frostbite
 * Original version of 1220_123: the Virtual Block 2 release sheet text (Dec 2009, slip 123); the PC website card list
 * (by 2012-07-21) has the current wording ("Deploy on Hoth system" became "Deploy on the Hoth system"). Plays the same as the current card; only the
 * wording differs. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2220_123 extends Card1220_123 {
    public Card2220_123() {
        super();
        setGameText("Deploy on Hoth system if you control two exterior Hoth sites. During your control phase, may retrieve 1 Force for each Hoth site you control with a unique (•) combat vehicle. If opponent controls this system, this Effect lost. While you occupy 1st marker, [Immune to Alter]");
        setLegacyVersion(new LegacyVersion("1220_123", "",
                "Virtual Block 2 release sheet (2009-12-07), card #123; superseded by the PC card list text (by 2012-07-21)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
