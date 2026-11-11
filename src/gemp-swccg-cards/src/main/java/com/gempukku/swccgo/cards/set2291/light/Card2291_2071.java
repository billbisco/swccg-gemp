package com.gempukku.swccgo.cards.set2291.light;

import com.gempukku.swccgo.cards.set1291.light.Card1291_071;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 9.1
 * Type: Vehicle
 * Subtype: Combat
 * Title: AT-RT
 * PC-E2 version of 1291_71: the Competitive Balance Team errata as first posted on 2014-07-23 (PC forum "Current
 * Rulings/Errata", p894811): power 1, the permanent pilot adds 2 to power, "Dual Laser Cannon may deploy aboard" and
 * the deploy-time Dual Laser Cannon download, without the "T-47" clause. Corrected two days later (2014-07-25, p895797:
 * "there was an error in carrying over some text"), PC-E3, the current card. GEMP lets Dual Laser Cannon deploy aboard
 * through the same "T-47" treatment the other versions use. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2291_2071 extends Card1291_071 {
    public Card2291_2071() {
        super(1, "May add 1 pilot (suspends permanent pilot). May move as a 'react.' Permanent pilot provides ability of 2 and adds 2 to power. Dual Laser Cannon may deploy aboard. When deployed, may ▼ Dual Laser Cannon aboard.",
                true, true, true);
        setLegacyVersion(new LegacyVersion("1291_71", "PC-E4",
                "Competitive Balance Team errata, PC forum post p894811 (2014-07-23); corrected 2014-07-25 (p895797)", LocalDate.of(2014, 7, 23), LocalDate.of(2014, 7, 25)));
    }
}
