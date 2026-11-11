package com.gempukku.swccgo.cards.set2291.light;

import com.gempukku.swccgo.cards.set1291.light.Card1291_071;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 9.1
 * Type: Vehicle
 * Subtype: Combat
 * Title: AT-RT
 * Original version of 1291_71: the first Virtual Block 9.1 release (2014-01-10) as the PC card image prints it (typo
 * "Cannnon" kept): "Dual Laser Cannnon may deploy aboard. When deployed, may ▼ Dual Laser Cannon aboard.", no "T-47"
 * clause. The PC changed the text "shortly after the release" (PC forum p862180, 2014-01-22) to the surviving sheet text
 * (PC-E1, 2291_1071). GEMP lets Dual Laser Cannon deploy aboard through the same "T-47" treatment the later texts give.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2291_071 extends Card1291_071 {
    public Card2291_071() {
        super(3, "May add 1 pilot (suspends permanent pilot). May move as a 'react.' Permanent pilot provides ability of 2. Dual Laser Cannnon may deploy aboard. When deployed, may ▼ Dual Laser Cannon aboard.",
                false, true, true);
        setLegacyVersion(new LegacyVersion("1291_71", "PC-E3",
                "PC card image (image server folder Virtual9-Light, file dated 2014-02-18); may print the lost first Virtual Block 9.1 text (2014-01-10)", LocalDate.of(2014, 2, 18), LocalDate.of(2014, 7, 23)));
    }
}
