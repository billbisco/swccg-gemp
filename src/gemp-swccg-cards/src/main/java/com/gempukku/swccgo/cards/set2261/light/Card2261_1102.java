package com.gempukku.swccgo.cards.set2261.light;

import com.gempukku.swccgo.cards.set1261.light.Card1261_102;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Type: Interrupt
 * Title: •Warrior's Courage (V)
 * PC-E2 version of 1261_102 (Batch R8-U5, Bill 2026-11-11: every publication is a version; PC card
 * images are versions at their proven date): PC website card list (by 2012-07-21). Order so far: original sheet (2009-09-04) -> PC-E1 pc_vkit (2009-12-09) -> PC-E2 cl (2012-07-21); the current card is the
 * Virtual Cards Master List (2013-04-27). Same play as the current card; only the wording differs.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_1102 extends Card1261_102 {
    public Card2261_1102() {
        super();
        setGameText("Use 1 Force to ▼ (or retrieve) Leia. OR If a battle just initiated, and non-[Reflections III] Leia is defending alone at a site, her forfeit cannot be reduced, she adds one destiny to power (+1 for each character and vehicle present), and draws one battle destiny if not able to otherwise. (Immune to Sense.)");
        setLegacyVersion(new LegacyVersion("1261_102", "PC-E2",
                "PC website card list (by 2012-07-21)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
