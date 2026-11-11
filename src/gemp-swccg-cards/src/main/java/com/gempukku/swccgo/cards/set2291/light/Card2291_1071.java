package com.gempukku.swccgo.cards.set2291.light;

import com.gempukku.swccgo.cards.set1291.light.Card1291_071;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 9.1
 * Type: Vehicle
 * Subtype: Combat
 * Title: AT-RT
 * PC-E1 version of 1291_71: the Virtual Block 9.1 release sheet as it survives (official text as of 2014-01-10). The PC
 * changed the text "shortly after the release" so that it works with Dual Laser Cannon as intended (PC forum p862180,
 * 2014-01-22: "The current version of the 9.1 PDF has the correct text"): "T-47" on [Hoth] weapons may be treated as
 * "AT-RT". Power 3; when deployed, may deploy Dual Laser Cannon aboard from Reserve Deck. Superseded by the Competitive
 * Balance Team errata of 2014-07-23 (PC-E2). Anything Goes only (docs/legacy-versions.md).
 */
public class Card2291_1071 extends Card1291_071 {
    public Card2291_1071() {
        super(3, "May add 1 pilot (suspends permanent pilot). May move as a 'react.' Permanent pilot provides ability of 2. \"T-47\" on [Hoth] weapons may be treated as \"AT-RT\". When deployed, may ▼ Dual Laser Cannon aboard.",
                false, true, true);
        setLegacyVersion(new LegacyVersion("1291_71", "PC-E1",
                "Virtual Block 9.1 release sheet as corrected by 2014-01-22 (PC forum p862180)", LocalDate.of(2014, 1, 22), LocalDate.of(2014, 7, 23)));
    }
}
